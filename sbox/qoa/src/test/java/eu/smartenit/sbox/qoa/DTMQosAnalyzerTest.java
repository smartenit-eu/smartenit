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
package eu.smartenit.sbox.qoa;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
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
import eu.smartenit.sbox.db.dto.ZVector;
import eu.smartenit.sbox.eca.EconomicAnalyzer;
import eu.smartenit.sbox.ntm.dtm.DTMTrafficManager;

/**
 * Test class for {@link DTMQosAnalyzer} class. Tests whole DTMQosAnylazer
 * workflow launched after each reporting period including: new counter values
 * collection from routers, traffic vectors calculation and Economic Analyzer
 * and Network Traffic Manager updates.
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a
 *         href="http://psnc.pl">PSNC</a>)
 * 
 */
public class DTMQosAnalyzerTest {
	private DTMQosAnalyzer dtmQosAnalyzer;
	private DTMTrafficManager trafficManager;
	private EconomicAnalyzer economicAnalyzer;
	
	@Before
	public void init() {
		this.trafficManager = mock(DTMTrafficManager.class);
		this.economicAnalyzer = mock(EconomicAnalyzer.class);
		
		SNMPWrapper snmpWrapper = mock(SNMPWrapper.class);
		ASDAO asDAO = mock(ASDAO.class);
		DC2DCCommunicationDAO dcDAO = mock(DC2DCCommunicationDAO.class);
		TimeScheduleParametersDAO tspDAO = mock(TimeScheduleParametersDAO.class);
		
		DAOFactory.setASDAOInstance(asDAO);
		DAOFactory.setDC2DCCommunicationDAO(dcDAO);
		DAOFactory.setTimeScheduleParametersDAO(tspDAO);
		
		SNMPWrapperFactory.disableUniqueSnmpWrappperCreationMode();
		SNMPWrapperFactory.setSNMPWrapperInstance(snmpWrapper);
		
		when(snmpWrapper.snmpGet(any(OID.class), any(Target.class))).thenReturn("[1.2.3.4.5.6.7.8.9 = 987654321]");
		when(snmpWrapper.snmpWalk(any(OID.class), any(Target.class))).thenReturn(prepareSnmpwalkResponse());
		
		when(asDAO.findAllAsesCloudsBgRoutersLinks()).thenReturn(prepareLocalASs());
		when(dcDAO.findAllDC2DCCommunicationsCloudsTunnels()).thenReturn(prepareDCCommunications());
		when(tspDAO.findLast()).thenReturn(prepareTimeScheduleParameters());
		
		SBoxThreadHandler.threadService = 
				Executors.newScheduledThreadPool(SBoxProperties.CORE_POOL_SIZE, new ThreadFactory());

		this.dtmQosAnalyzer = new DTMQosAnalyzer(trafficManager, economicAnalyzer);
	}
	
//	@Ignore
	@Test
	public void shouldUpdateXAndZVectors() throws Exception {
		this.dtmQosAnalyzer.initialize();
		Thread.sleep(3500);
		verify(trafficManager, times(4)).updateXVector(any(XVector.class));
		verify(economicAnalyzer, times(4)).updateXZVectors(any(XVector.class), anyListOf(ZVector.class));
	}
	
//	@Ignore
	@Test
	public void shouldReturnFalseForInitialization() {
		SNMPWrapperFactory.setSNMPWrapperInstance(null);
		assertFalse(this.dtmQosAnalyzer.initialize());
	}
	
	@After
	public void cleanup() {
		SBoxThreadHandler.shutdownNowThreads();
	}

	private TimeScheduleParameters prepareTimeScheduleParameters() {
		final TimeScheduleParameters tsp = new TimeScheduleParameters();
		tsp.setAccountingPeriod(100);
		tsp.setReportingPeriod(2);
		tsp.setStartDate(new Date(DateTime.now().plusMillis(1500).getMillis()));
		return tsp;
	}

	private List<AS> prepareLocalASs() {
		return DBStructuresBuilder.systems;
	}

	private List<DC2DCCommunication> prepareDCCommunications() {
		return DBStructuresBuilder.communications;
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
