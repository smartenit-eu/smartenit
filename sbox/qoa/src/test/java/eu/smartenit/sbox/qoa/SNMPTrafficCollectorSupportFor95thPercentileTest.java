/**
 * Copyright (C) 2015 The SmartenIT consortium (http://www.smartenit.eu)
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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import eu.smartenit.sbox.commons.SBoxThreadHandler;
import eu.smartenit.sbox.db.dao.SystemControlParametersDAO;
import eu.smartenit.sbox.db.dao.TimeScheduleParametersDAO;
import eu.smartenit.sbox.db.dto.ChargingRule;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.SimpleTunnelID;
import eu.smartenit.sbox.db.dto.SystemControlParameters;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.db.dto.ZVector;

/**
 * Tests handling of counter values when {@link ChargingRule} is set to 95th
 * percentile based.
 * 
 * @author Lukasz Lopatowski
 * 
 */
public class SNMPTrafficCollectorSupportFor95thPercentileTest {

	private static final int AS_NUMBER = 1;
	private DTMQosAnalyzer dtmQosAnalyzer;
	private MonitoringDataProcessor monitoringDataProcessor;
	private SNMPTrafficCollector snmpTrafficCollector;
	private ScheduledExecutorService threadService;
	private MonitoredLinksInventory monitoredLinks;
	private MonitoredTunnelsInventory monitoredTunnels;
	
	final SimpleLinkID link1 = new SimpleLinkID("1", "1");
	final SimpleLinkID link2 = new SimpleLinkID("2", "1");
	final SimpleTunnelID tunnel1 = new SimpleTunnelID("A", 1);
	final SimpleTunnelID tunnel2 = new SimpleTunnelID("B", 2);
	
	@Before
	public void init() {
		prepareTimeScheduleParameters();
		prepareSystemControlParameters();
		this.dtmQosAnalyzer = mock(DTMQosAnalyzer.class);
		this.monitoringDataProcessor = mock(MonitoringDataProcessor.class);
		this.threadService = mock(ScheduledExecutorService.class);
		this.monitoredLinks = mock(MonitoredLinksInventory.class);
		this.monitoredTunnels = mock(MonitoredTunnelsInventory.class);
		
		this.snmpTrafficCollector = new SNMPTrafficCollector(dtmQosAnalyzer,
				monitoringDataProcessor, monitoredLinks, monitoredTunnels);
		
    	SBoxThreadHandler.threadService = threadService;
	}
	
	@Test
	public void shoulNotifyNewCounterValuesFor95thPercentile() {
		this.snmpTrafficCollector = new SNMPTrafficCollector(dtmQosAnalyzer,
				monitoringDataProcessor, monitoredLinks, monitoredTunnels);
		
		CounterValues cvEmpty = new CounterValues();
		cvEmpty.storeCounterValue(link1, 0);
		cvEmpty.storeCounterValue(link2, 0);
		cvEmpty.storeCounterValue(tunnel1, 0);
		cvEmpty.storeCounterValue(tunnel2, 0);
		CounterValues cv1 = new CounterValues();
		cv1.storeCounterValue(link1, 1000);
		cv1.storeCounterValue(link2, 1200);
		cv1.storeCounterValue(tunnel1, 200);
		cv1.storeCounterValue(tunnel2, 160);
		CounterValues cv2 = new CounterValues();
		cv2.storeCounterValue(link1, 2000);
		cv2.storeCounterValue(link2, 2200);
		cv2.storeCounterValue(tunnel1, 1200);
		cv2.storeCounterValue(tunnel2, 1260);
		CounterValues cv3 = new CounterValues();
		cv3.storeCounterValue(link1, 4000);
		cv3.storeCounterValue(link2, 4200);
		cv3.storeCounterValue(tunnel1, 2200);
		cv3.storeCounterValue(tunnel2, 2160);
		CounterValues cv4 = new CounterValues();
		cv4.storeCounterValue(link1, 5000);
		cv4.storeCounterValue(link2, 5200);
		cv4.storeCounterValue(tunnel1, 3100);
		cv4.storeCounterValue(tunnel2, 2560);
		CounterValues cv5 = new CounterValues();
		cv5.storeCounterValue(link1, 6000);
		cv5.storeCounterValue(link2, 6200);
		cv5.storeCounterValue(tunnel1, 4200);
		cv5.storeCounterValue(tunnel2, 4160);
		CounterValues cv6 = new CounterValues();
		cv6.storeCounterValue(link1, 6500);
		cv6.storeCounterValue(link2, 8200);
		cv6.storeCounterValue(tunnel1, 5200);
		cv6.storeCounterValue(tunnel2, 5160);
		
		snmpTrafficCollector.notifyNewCounterValues(AS_NUMBER, cvEmpty);
		snmpTrafficCollector.notifyNewCounterValues(AS_NUMBER, cv1);
		snmpTrafficCollector.notifyNewCounterValues(AS_NUMBER, cv2);
		snmpTrafficCollector.notifyNewCounterValues(AS_NUMBER, cv3);
		snmpTrafficCollector.notifyNewCounterValues(AS_NUMBER, cv4);
		snmpTrafficCollector.notifyNewCounterValues(AS_NUMBER, cv5);
		snmpTrafficCollector.notifyNewCounterValues(AS_NUMBER, cv6);
		
		verify(dtmQosAnalyzer, times(6)).updateXVector(any(XVector.class));
		verify(dtmQosAnalyzer, times(2)).updateXZVectors(any(XVector.class), anyListOf(ZVector.class));
	}
	
	private void prepareTimeScheduleParameters() {
		TimeScheduleParameters tsp = new TimeScheduleParameters();
		tsp.setAccountingPeriod(300);
		tsp.setReportingPeriod(30);
		tsp.setReportPeriodEA(30);
		tsp.setReportPeriodDTM(10);
		tsp.setTimeUnit(TimeUnit.SECONDS);
		tsp.setStartDate(new Date(DateTime.now().plusMillis(1000).getMillis()));
		
		final TimeScheduleParametersDAO tspDAO = mock(TimeScheduleParametersDAO.class);
		when(tspDAO.findLast()).thenReturn(tsp);
		DAOFactory.setTimeScheduleParametersDAO(tspDAO);
	}
	
	private void prepareSystemControlParameters() {
		SystemControlParameters scp = new SystemControlParameters(ChargingRule.the95thPercentile, null, 0.15);
		SystemControlParametersDAO scpDAO = mock(SystemControlParametersDAO.class);
    	when(scpDAO.findLast()).thenReturn(scp);
    	DAOFactory.setSCPDAOInstance(scpDAO);
	}
	
}
