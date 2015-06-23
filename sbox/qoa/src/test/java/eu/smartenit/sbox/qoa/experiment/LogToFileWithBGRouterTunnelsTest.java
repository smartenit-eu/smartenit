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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import eu.smartenit.sbox.commons.SBoxProperties;
import eu.smartenit.sbox.db.dao.SystemControlParametersDAO;
import eu.smartenit.sbox.db.dao.TimeScheduleParametersDAO;
import eu.smartenit.sbox.db.dto.ChargingRule;
import eu.smartenit.sbox.db.dto.EndAddressPairTunnelID;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.SystemControlParameters;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;
import eu.smartenit.sbox.qoa.DAOFactory;
import eu.smartenit.sbox.qoa.DBStructuresBuilder;
import eu.smartenit.sbox.qoa.DTMQosAnalyzer;
import eu.smartenit.sbox.qoa.MonitoredLinksInventory;
import eu.smartenit.sbox.qoa.MonitoredTunnelsInventory;
import eu.smartenit.sbox.qoa.MonitoringDataProcessor;

/**
 * Unit test checking log generation for tunnels terminated at BG router.
 * 
 *  @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a href="http://psnc.pl">PSNC</a>)
 *
 */
public class LogToFileWithBGRouterTunnelsTest {
	private static final String FILE_NAME = "packets.txt";
	private static final String PATH = "." + File.separator;
	private DTMQosAnalyzer dtmQosAnalyzer = Mockito.mock(DTMQosAnalyzer.class);
	protected MonitoringDataProcessor monitoringDataProcessor = Mockito.mock(MonitoringDataProcessor.class);
	
	private static final int AS_NUMBER = 1;
	private MonitoredLinksInventory monitoredLinks = new MonitoredLinksInventory();
	private MonitoredTunnelsInventory monitoredTunnels = new MonitoredTunnelsInventory();
	private ExtendedSNMPTrafficCollector extendedSNMPTrafficCollector;
	private ExtendedSNMPTrafficCollector extendedSNMPTrafficCollector2;
	final StringBuilder log = new StringBuilder();
	
	@Before
	public void init() {
		prepareSystemControlParameters();
		prepareTimeScheduleParameters();
		monitoredLinks.populate(DBStructuresBuilder.systems);
		monitoredTunnels.populate(DBStructuresBuilder.communicationsWithTunnelsOnBGRouters);
		
		SBoxProperties.TRAFFIC_DETAILS_FILE_PATH = PATH;
		SBoxProperties.TRAFFIC_DETAILS_FILE_NAME = FILE_NAME;

		extendedSNMPTrafficCollector = new ExtendedSNMPTrafficCollector(dtmQosAnalyzer, monitoringDataProcessor, monitoredLinks, monitoredTunnels, DAOFactory.getTimeScheduleParametersDAOInstance().findLast());
		extendedSNMPTrafficCollector2 = new ExtendedSNMPTrafficCollector(dtmQosAnalyzer, null, monitoredLinks, monitoredTunnels, DAOFactory.getTimeScheduleParametersDAOInstance().findLast());
		
		log.append(extendedSNMPTrafficCollector2.logToFile(AS_NUMBER, prepareValues(100, 1000)));
		log.append(extendedSNMPTrafficCollector2.logToFile(AS_NUMBER, prepareValues(200, 2000)));
		log.append(extendedSNMPTrafficCollector2.logToFile(AS_NUMBER, prepareValues(300, 3000)));
		log.append(extendedSNMPTrafficCollector2.logToFile(AS_NUMBER, prepareValues(400, 4000)));
	}
	
	@Test @Ignore
	public void shouldLogToFile() throws IOException {
		extendedSNMPTrafficCollector.notifyNewCounterValues(AS_NUMBER, prepareValues(100, 1000));
		extendedSNMPTrafficCollector.notifyNewCounterValues(AS_NUMBER, prepareValues(200, 2000));
		extendedSNMPTrafficCollector.notifyNewCounterValues(AS_NUMBER, prepareValues(300, 3000));
		extendedSNMPTrafficCollector.notifyNewCounterValues(AS_NUMBER, prepareValues(400, 4000));
		
		assertEquals(removeTimestamp(log.toString()), removeTimestamp(getLogFromFile()));
	}
	
	private String removeTimestamp(String log) {
		final StringBuilder response = new StringBuilder();
		String[] lines = log.split("\n");
		for (int line = 0; line < lines.length; line++) {
			if(lines[line].startsWith("Timestamp")) {
				lines[line] = "Timestamp : ";
			}
			response.append(lines[line]).append("\n");
		}
		return response.toString();
	}
	
	private String getLogFromFile() throws IOException {
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
	
	protected ExtendedCounterValues prepareValues(int linkPackets, int tunnelPackets) {
		ExtendedCounterValues values = new ExtendedCounterValues();
		values.storeCounterValue(new SimpleLinkID("111", "isp1"), linkPackets + 1);
		values.storeReceivedPackets(new SimpleLinkID("111", "isp1"), new ReceivedPackets(linkPackets + 1 + 50L, linkPackets + 1 + 100L, linkPackets + 1 + 150L));
		values.storeCounterValue(new SimpleLinkID("112", "isp1"), linkPackets + 2);
		values.storeReceivedPackets(new SimpleLinkID("112", "isp1"), new ReceivedPackets(linkPackets + 2 + 50L, linkPackets + 2 + 100L, linkPackets + 2 + 150L));		
		values.storeCounterValue(new SimpleLinkID("121", "isp1"), linkPackets + 3);
		values.storeReceivedPackets(new SimpleLinkID("121", "isp1"), new ReceivedPackets(linkPackets + 3 + 50L, linkPackets + 3 + 100L, linkPackets + 3 + 150L));
		values.storeCounterValue(new SimpleLinkID("122", "isp1"), linkPackets + 4);
		values.storeReceivedPackets(new SimpleLinkID("122", "isp1"), new ReceivedPackets(linkPackets + 4 + 50L, linkPackets + 4 + 100L, linkPackets + 4 + 150L));
		
		final EndAddressPairTunnelID tunnelID1 = new EndAddressPairTunnelID("tunnel1111", 
				new NetworkAddressIPv4("10.1.1.1", 32), 
				new NetworkAddressIPv4("10.1.1.2", 32));
		final EndAddressPairTunnelID tunnelID2 = new EndAddressPairTunnelID("tunnel1121", 
				new NetworkAddressIPv4("10.1.2.1", 32), 
				new NetworkAddressIPv4("10.1.2.2", 32));
		final EndAddressPairTunnelID tunnelID3 = new EndAddressPairTunnelID("tunnel1211", 
				new NetworkAddressIPv4("10.2.1.1", 32), 
				new NetworkAddressIPv4("10.2.1.2", 32));
		final EndAddressPairTunnelID tunnelID4 = new EndAddressPairTunnelID("tunnel1212", 
				new NetworkAddressIPv4("10.2.1.2", 32), 
				new NetworkAddressIPv4("10.2.1.3", 32));
		final EndAddressPairTunnelID tunnelID5 = new EndAddressPairTunnelID("tunnel1221", 
				new NetworkAddressIPv4("10.2.2.1", 32), 
				new NetworkAddressIPv4("10.2.2.2", 32));
		final EndAddressPairTunnelID tunnelID6 = new EndAddressPairTunnelID("tunnel1222", 
				new NetworkAddressIPv4("10.2.2.2", 32), 
				new NetworkAddressIPv4("10.2.2.3", 32));
		final EndAddressPairTunnelID tunnelID7 = new EndAddressPairTunnelID("tunnel1223", 
				new NetworkAddressIPv4("10.2.2.4", 32), 
				new NetworkAddressIPv4("10.2.2.5", 32));
		values.storeCounterValue(tunnelID1, tunnelPackets + 1);
		values.storeReceivedPackets(tunnelID1, new ReceivedPackets(tunnelPackets + 1 + 5L, tunnelPackets + 1 + 10L, tunnelPackets + 1 + 15L));
		values.storeCounterValue(tunnelID2, tunnelPackets + 2);
		values.storeReceivedPackets(tunnelID2, new ReceivedPackets(tunnelPackets + 2 + 5L, tunnelPackets + 2 + 10L, tunnelPackets + 2 + 15L));
		values.storeCounterValue(tunnelID3, tunnelPackets + 3);
		values.storeReceivedPackets(tunnelID3, new ReceivedPackets(tunnelPackets + 3 + 5L, tunnelPackets + 3 + 10L, tunnelPackets + 3 + 15L));
		values.storeCounterValue(tunnelID4, tunnelPackets + 4);
		values.storeReceivedPackets(tunnelID4, new ReceivedPackets(tunnelPackets + 4 + 5L, tunnelPackets + 4 + 10L, tunnelPackets + 4 + 15L));
		values.storeCounterValue(tunnelID5, tunnelPackets + 5);
		values.storeReceivedPackets(tunnelID5, new ReceivedPackets(tunnelPackets + 5 + 5L, tunnelPackets + 5 + 10L, tunnelPackets + 5 + 15L));
		values.storeCounterValue(tunnelID6, tunnelPackets + 6);
		values.storeReceivedPackets(tunnelID6, new ReceivedPackets(tunnelPackets + 6 + 5L, tunnelPackets + 6 + 10L, tunnelPackets + 6 + 15L));
		values.storeCounterValue(tunnelID7, tunnelPackets + 7);
		values.storeReceivedPackets(tunnelID7, new ReceivedPackets(tunnelPackets + 7 + 5L, tunnelPackets + 7 + 10L, tunnelPackets + 7 + 15L));
		
		return values;
	}
	
	@After
	public void clean() {
		final File file = new File(PATH, FILE_NAME);
		if(file.exists()) {
			file.delete();
		}
	}
	
	private void prepareSystemControlParameters() {
		SystemControlParametersDAO scpDAO = mock(SystemControlParametersDAO.class);
		SystemControlParameters scp = new SystemControlParameters(ChargingRule.volume, null, 0.15);
    	when(scpDAO.findLast()).thenReturn(scp);
    	DAOFactory.setSCPDAOInstance(scpDAO);
	}
	
	private void prepareTimeScheduleParameters() {
		final TimeScheduleParameters tsp = new TimeScheduleParameters();
		tsp.setAccountingPeriod(2);
		tsp.setReportingPeriod(1);
		tsp.setStartDate(new Date(DateTime.now().plusMillis(2000).getMillis()));
		final TimeScheduleParametersDAO tspDAO = mock(TimeScheduleParametersDAO.class);
		when(tspDAO.findLast()).thenReturn(tsp);
		DAOFactory.setTimeScheduleParametersDAO(tspDAO);
	}
}
