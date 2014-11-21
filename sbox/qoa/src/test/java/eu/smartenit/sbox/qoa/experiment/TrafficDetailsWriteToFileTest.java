/**
 * Copyright (C) 2014 The SmartenIT consortium (http://www.smartenit.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.smartenit.sbox.qoa.experiment;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.snmp4j.CommunityTarget;
import org.snmp4j.Target;
import org.snmp4j.smi.OID;

import eu.smartenit.sbox.commons.SBoxProperties;
import eu.smartenit.sbox.commons.SBoxThreadHandler;
import eu.smartenit.sbox.commons.ThreadFactory;
import eu.smartenit.sbox.db.dao.ASDAO;
import eu.smartenit.sbox.db.dao.DC2DCCommunicationDAO;
import eu.smartenit.sbox.db.dao.TimeScheduleParametersDAO;
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.eca.EconomicAnalyzer;
import eu.smartenit.sbox.ntm.dtm.receiver.DTMTrafficManager;
import eu.smartenit.sbox.qoa.DAOFactory;
import eu.smartenit.sbox.qoa.DBStructuresBuilderForLogicTest;
import eu.smartenit.sbox.qoa.DTMQosAnalyzer;
import eu.smartenit.sbox.qoa.SNMPWrapper;
import eu.smartenit.sbox.qoa.SNMPWrapperFactory;

public class TrafficDetailsWriteToFileTest {
	private DTMQosAnalyzer dtmQosAnalyzer;
	private DTMTrafficManager trafficManager;
	private EconomicAnalyzer economicAnalyzer;
	private SNMPWrapper snmpWrapper;
	private static final String FILE_NAME = "packets.txt";
	private static final String PATH = "." + File.separator;
	
	@Before
	public void init() {
		SBoxProperties.LOG_TRAFFIC_DETAILS = true;
		SBoxProperties.TRAFFIC_DETAILS_FILE_PATH = PATH;
		SBoxProperties.TRAFFIC_DETAILS_FILE_NAME = FILE_NAME;
		
		this.trafficManager = mock(DTMTrafficManager.class);
		this.economicAnalyzer = mock(EconomicAnalyzer.class);
		this.snmpWrapper = mock(SNMPWrapper.class);

		SNMPWrapperFactory.disableUniqueSnmpWrappperCreationMode();
		SNMPWrapperFactory.setSNMPWrapperInstance(snmpWrapper);
		
		ASDAO asDAO = mock(ASDAO.class);
		DC2DCCommunicationDAO dcDAO = mock(DC2DCCommunicationDAO.class);
		TimeScheduleParametersDAO tspDAO = mock(TimeScheduleParametersDAO.class);
		
		DAOFactory.setASDAOInstance(asDAO);
		DAOFactory.setDC2DCCommunicationDAO(dcDAO);
		DAOFactory.setTimeScheduleParametersDAO(tspDAO);
		
		when(asDAO.findAllAsesCloudsBgRoutersLinks()).thenReturn(prepareLocalASs());
		when(dcDAO.findAllDC2DCCommunicationsCloudsTunnels()).thenReturn(prepareDCCommunications());
		when(tspDAO.findLast()).thenReturn(prepareTimeScheduleParameters());
		
		SBoxThreadHandler.threadService = 
				Executors.newScheduledThreadPool(SBoxProperties.CORE_POOL_SIZE, new ThreadFactory());

		this.dtmQosAnalyzer = new DTMQosAnalyzer(trafficManager, economicAnalyzer);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void shouldWriteTrafficDetailsToFile() throws InterruptedException, IOException {
		prepareSnmpWrapper();
		
		this.dtmQosAnalyzer.initialize();
		Thread.sleep(7500);
		
		ArgumentCaptor<XVector> xVector = ArgumentCaptor.forClass(XVector.class);
		verify(trafficManager, times(4)).updateXVector(xVector.capture());
		
		ArgumentCaptor<List> zVectors = ArgumentCaptor.forClass(List.class);
		verify(economicAnalyzer, times(4)).updateXZVectors(xVector.capture(), zVectors.capture());
		
		//1.3.6.1.2.1.31.1.1.1.6.0 = 100
		assertTrue(getRecordsFromFile().contains("IF_IN_OCTETS : 100 / 100"));
		//1.3.6.1.2.1.31.1.1.1.6.0 = 110
		assertTrue(getRecordsFromFile().contains("IF_IN_OCTETS : 110 / 10"));
		//1.3.6.1.2.1.31.1.1.1.6.0 = 130
		assertTrue(getRecordsFromFile().contains("IF_IN_OCTETS : 130 / 20"));
	}
	
	private String getRecordsFromFile() throws IOException {
		File file = new File(PATH, FILE_NAME);
		assertTrue(file.exists());
		
		FileInputStream input = new FileInputStream(file);
		final StringBuilder content = new StringBuilder();
		int byteCode;
		while ((byteCode = input.read()) != -1) {
			content.append((char) byteCode);
		}
		
		input.close();
		
		return content.toString();
	}
	
	private void prepareSnmpWrapper() {
		when(snmpWrapper.snmpWalk(any(OID.class), any(Target.class))).thenReturn(prepareSnmpwalkResponse());
		Mockito.doCallRealMethod().when(snmpWrapper).prepareOID(any(String.class));
		Mockito.doCallRealMethod().when(snmpWrapper).prepareTarget(any(String.class), any(String.class));
		
		/* localLinkID -> 111 */
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEquals("1.3.6.1.2.1.31.1.1.1.6.0")), Mockito.argThat(new IsAddressEquals("1.2.1.1"))))
			.thenReturn("1.3.6.1.2.1.31.1.1.1.6.0 = 100]", "1.3.6.1.2.1.31.1.1.1.6.0 = 110]", "1.3.6.1.2.1.31.1.1.1.6.0 = 130]");
		
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEquals("1.3.6.1.2.1.31.1.1.1.7.0")), Mockito.argThat(new IsAddressEquals("1.2.1.1"))))
		.thenReturn("1.3.6.1.2.1.31.1.1.1.7.0 = 100]", "1.3.6.1.2.1.31.1.1.1.7.0 = 110]", "1.3.6.1.2.1.31.1.1.1.7.0 = 130]");
		
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEquals("1.3.6.1.2.1.31.1.1.1.8.0")), Mockito.argThat(new IsAddressEquals("1.2.1.1"))))
		.thenReturn("1.3.6.1.2.1.31.1.1.1.8.0 = 100]", "1.3.6.1.2.1.31.1.1.1.8.0 = 110]", "1.3.6.1.2.1.31.1.1.1.8.0 = 130]");
		
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEquals("1.3.6.1.2.1.31.1.1.1.9.0")), Mockito.argThat(new IsAddressEquals("1.2.1.1"))))
		.thenReturn("1.3.6.1.2.1.31.1.1.1.9.0 = 100]", "1.3.6.1.2.1.31.1.1.1.9.0 = 110]", "1.3.6.1.2.1.31.1.1.1.9.0 = 130]");
		
		/* localLinkID -> 121 */
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEquals("1.3.6.1.2.1.31.1.1.1.6.1")), Mockito.argThat(new IsAddressEquals("1.2.1.2"))))
			.thenReturn("1.3.6.1.2.1.31.1.1.1.6.1 = 200]", "1.3.6.1.2.1.31.1.1.1.6.1 = 210]", "1.3.6.1.2.1.31.1.1.1.6.1 = 230]");
		
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEquals("1.3.6.1.2.1.31.1.1.1.7.1")), Mockito.argThat(new IsAddressEquals("1.2.1.2"))))
		.thenReturn("1.3.6.1.2.1.31.1.1.1.7.1 = 200]", "1.3.6.1.2.1.31.1.1.1.7.1 = 210]", "1.3.6.1.2.1.31.1.1.1.7.1 = 230]");
		
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEquals("1.3.6.1.2.1.31.1.1.1.8.1")), Mockito.argThat(new IsAddressEquals("1.2.1.2"))))
		.thenReturn("1.3.6.1.2.1.31.1.1.1.8.1 = 200]", "1.3.6.1.2.1.31.1.1.1.8.1 = 210]", "1.3.6.1.2.1.31.1.1.1.8.1 = 230]");
		
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEquals("1.3.6.1.2.1.31.1.1.1.9.1")), Mockito.argThat(new IsAddressEquals("1.2.1.2"))))
		.thenReturn("1.3.6.1.2.1.31.1.1.1.9.1 = 200]", "1.3.6.1.2.1.31.1.1.1.9.1 = 210]", "1.3.6.1.2.1.31.1.1.1.9.1 = 230]");
		
		/* tunnelNumber -> 1111 */
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEquals("1.3.6.1.2.1.31.1.1.1.6.0")), Mockito.argThat(new IsAddressEquals("1.1.1.1"))))
			.thenReturn("1.3.6.1.2.1.31.1.1.1.6.0 = 10]", "1.3.6.1.2.1.31.1.1.1.6.0 = 11]", "1.3.6.1.2.1.31.1.1.1.6.0 = 13]");
		
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEquals("1.3.6.1.2.1.31.1.1.1.7.0")), Mockito.argThat(new IsAddressEquals("1.1.1.1"))))
		.thenReturn("1.3.6.1.2.1.31.1.1.1.7.0 = 10]", "1.3.6.1.2.1.31.1.1.1.7.0 = 11]", "1.3.6.1.2.1.31.1.1.1.7.0 = 13]");
		
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEquals("1.3.6.1.2.1.31.1.1.1.8.0")), Mockito.argThat(new IsAddressEquals("1.1.1.1"))))
		.thenReturn("1.3.6.1.2.1.31.1.1.1.8.0 = 10]", "1.3.6.1.2.1.31.1.1.1.8.0 = 11]", "1.3.6.1.2.1.31.1.1.1.8.0 = 13]");
		
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEquals("1.3.6.1.2.1.31.1.1.1.9.0")), Mockito.argThat(new IsAddressEquals("1.1.1.1"))))
		.thenReturn("1.3.6.1.2.1.31.1.1.1.9.0 = 10]", "1.3.6.1.2.1.31.1.1.1.9.0 = 11]", "1.3.6.1.2.1.31.1.1.1.9.0 = 13]");
		
		/* tunnelNumber -> 1211 */
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEquals("1.3.6.1.2.1.31.1.1.1.6.1")), Mockito.argThat(new IsAddressEquals("1.1.1.1"))))
			.thenReturn("1.3.6.1.2.1.31.1.1.1.6.1 = 20]", "1.3.6.1.2.1.31.1.1.1.6.1 = 21]", "1.3.6.1.2.1.31.1.1.1.6.1 = 23]");
		
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEquals("1.3.6.1.2.1.31.1.1.1.7.1")), Mockito.argThat(new IsAddressEquals("1.1.1.1"))))
		.thenReturn("1.3.6.1.2.1.31.1.1.1.7.1 = 20]", "1.3.6.1.2.1.31.1.1.1.7.1 = 21]", "1.3.6.1.2.1.31.1.1.1.7.1 = 23]");
		
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEquals("1.3.6.1.2.1.31.1.1.1.8.1")), Mockito.argThat(new IsAddressEquals("1.1.1.1"))))
		.thenReturn("1.3.6.1.2.1.31.1.1.1.8.1 = 20]", "1.3.6.1.2.1.31.1.1.1.8.1 = 21]", "1.3.6.1.2.1.31.1.1.1.8.1 = 23]");
		
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEquals("1.3.6.1.2.1.31.1.1.1.9.1")), Mockito.argThat(new IsAddressEquals("1.1.1.1"))))
		.thenReturn("1.3.6.1.2.1.31.1.1.1.9.1 = 20]", "1.3.6.1.2.1.31.1.1.1.9.1 = 21]", "1.3.6.1.2.1.31.1.1.1.9.1 = 23]");
	}
	
	@After
	public void cleanup() {
		SBoxThreadHandler.shutdownNowThreads();
		final File file = new File(PATH, FILE_NAME);
		if(file.exists()) {
			file.delete();
		}
	}

	class IsOidEquals extends ArgumentMatcher<OID> {
		private String oid;

		public IsOidEquals(String oid) {
			this.oid = oid;
		}

		@Override
		public boolean matches(Object argument) {
			if(argument instanceof OID) {
				if(((OID) argument).toDottedString().equals(oid)) {
					return true;
				}
			}
			return false;
		}
	}
	
	class IsAddressEquals extends ArgumentMatcher<CommunityTarget> {
		private String address;
		
		public IsAddressEquals(String address) {
			this.address = address;
		}

		@Override
		public boolean matches(Object argument) {
			if(argument instanceof CommunityTarget) {
				if(((CommunityTarget) argument).getAddress().toString().contains(address)) {
					return true;
				}
			}
			return false;
		}
	}

	private TimeScheduleParameters prepareTimeScheduleParameters() {
		final TimeScheduleParameters tsp = new TimeScheduleParameters();
		tsp.setAccountingPeriod(4);
		tsp.setReportingPeriod(2);
		tsp.setStartDate(new Date(DateTime.now().plusMillis(1500).getMillis()));
		return tsp;
	}
	
	private List<AS> prepareLocalASs() {
		return DBStructuresBuilderForLogicTest.systems;
	}

	private List<DC2DCCommunication> prepareDCCommunications() {
		return DBStructuresBuilderForLogicTest.communications;
	}
	
	private List<String> prepareSnmpwalkResponse() {
		return Arrays.asList("1.3.6.1.2.1.31.1.1.1.1.0 = eth0",  
			"1.3.6.1.2.1.31.1.1.1.1.1 = eth1",
			"1.3.6.1.2.1.31.1.1.1.1.2 = eth2",
			"1.3.6.1.2.1.31.1.1.1.1.3 = eth3", 
			"1.3.6.1.2.1.31.1.1.1.1.4 = eth4",
			"1.3.6.1.2.1.31.1.1.1.1.5 = eth5",
			"1.3.6.1.2.1.31.1.1.1.1.6 = eth6",
			"1.3.6.1.2.1.31.1.1.1.1.7 = eth7");
	}
}
