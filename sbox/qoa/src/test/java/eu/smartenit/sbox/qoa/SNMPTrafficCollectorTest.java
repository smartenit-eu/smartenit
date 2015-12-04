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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
 * Test class for {@link SNMPTrafficCollector} class.
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a href="http://psnc.pl">PSNC</a>)
 *
 */
public class SNMPTrafficCollectorTest {
	private static final int AS_NUMBER = 1;
	private DTMQosAnalyzer dtmQosAnalyzer;
	private MonitoringDataProcessor monitoringDataProcessor;
	private SNMPTrafficCollector snmpTrafficCollector;
	private ScheduledExecutorService threadService;
	private MonitoredLinksInventory monitoredLinks;
	private MonitoredTunnelsInventory monitoredTunnels;
	
	@Before
	public void init() {
		prepareTimeScheduleParameters(null);
		prepareSystemControlParameters(null);
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
	public void shouldCalculateXVextor() {
		snmpTrafficCollector.notifyNewCounterValues(AS_NUMBER, prepareCounterValues());
		verify(monitoringDataProcessor, times(1)).calculateXVector(AS_NUMBER, null, prepareCounterValues());
	}
	
	@Test
	public void shouldCalculateZVectors() {
		snmpTrafficCollector.notifyNewCounterValues(AS_NUMBER, prepareCounterValues());
		verify(monitoringDataProcessor, times(1)).calculateZVectors(AS_NUMBER, null, prepareCounterValues());
	}
	
	@Test
	public void shouldUpdateXVector() {
		snmpTrafficCollector.notifyNewCounterValues(AS_NUMBER, prepareCounterValues());
		snmpTrafficCollector.notifyNewCounterValues(AS_NUMBER, prepareCounterValues());
		verify(dtmQosAnalyzer, times(1)).updateXVector(any(XVector.class));
	}
	
	@Test
	public void shouldUpdateXZVectors() {
		snmpTrafficCollector.notifyNewCounterValues(AS_NUMBER, prepareCounterValues());
		snmpTrafficCollector.notifyNewCounterValues(AS_NUMBER, prepareCounterValues());
		verify(dtmQosAnalyzer, times(1)).updateXZVectors(any(XVector.class), anyListOf(ZVector.class));
	}
	
	@Test
	public void shouldTriggerFiveScheduledTasks() {
		final Set<Integer> asList = prepareAsList(5);
		when(monitoredLinks.getAllAsNumbers()).thenReturn(asList);
		when(monitoredTunnels.getAllAsNumbers()).thenReturn(asList);
		snmpTrafficCollector.scheduleMonitoringTasks();
		verify(threadService, times(5)).scheduleAtFixedRate(any(Runnable.class), any(Long.class), any(Long.class), any(TimeUnit.class));
	}

	@Test
	public void shouldPrepareLogForScheduleMonitoringTasks() {
		final StringBuilder expected = new StringBuilder();
		expected.append("Started counters monitoring task:\n");
		expected.append("\tAS number: 1\n");
		expected.append("\n\tinitila delay: 10\n");
		expected.append("\n\tRaporting period: 30\n");
		expected.append("\n\t time unit: MILISECONDS\n");
		snmpTrafficCollector.prepareLogForScheduleMonitoringTasks(1, 10, 30, TimeUnit.MICROSECONDS);
	}
	
	@Test
	public void shouldPrepareErrorForAsNumbersValidation() {
		final StringBuilder error = new StringBuilder();
		error.append("The lists of Autonomus Systems for links and tunnels are not equals:\n");
		error.append("\tAS numers for links: [1, 2, 3]");
		error.append("\n\t AS numbers for tunnels: [1, 2]");
		error.append("\n");
		final Set<Integer> asNumbersForLinks = prepareAsList(3);
		final Set<Integer> asNumbersForTunnels = prepareAsList(2);
		snmpTrafficCollector.prepareErrorForAsNumbersValidation(asNumbersForLinks, asNumbersForTunnels);
	}
	
	@Test
	public void shouldCalculateInitialDelay() {
		assertEquals(11, snmpTrafficCollector.calculateInitialDelay(DAOFactory.getTimeScheduleParametersDAOInstance().findLast()));
	}
	
	private CounterValues prepareCounterValues() {
		final CounterValues counterValues = new CounterValues();
		counterValues.storeCounterValue(new SimpleLinkID(), 123456789);
		counterValues.storeCounterValue(new SimpleTunnelID(), 987654321);
		return counterValues;
	}
	
	private Set<Integer> prepareAsList(int numberOfAs) {
		final Set<Integer> asList = new HashSet<Integer>();
		for (int i = 0; i < numberOfAs; i++) {
			asList.add(i);
		}
		return asList;
	}
	
	private void prepareTimeScheduleParameters(TimeScheduleParameters tsp) {
		if(tsp == null) { 
			tsp = new TimeScheduleParameters();
			tsp.setAccountingPeriod(300);
			tsp.setReportingPeriod(30);
			tsp.setStartDate(new Date(DateTime.now().plusMillis(11500).getMillis()));
		}
		final TimeScheduleParametersDAO tspDAO = mock(TimeScheduleParametersDAO.class);
		when(tspDAO.findLast()).thenReturn(tsp);
		DAOFactory.setTimeScheduleParametersDAO(tspDAO);
	}
	
	private void prepareSystemControlParameters(SystemControlParameters scp) {
		SystemControlParametersDAO scpDAO = mock(SystemControlParametersDAO.class);
		if (scp == null)
			scp = new SystemControlParameters(ChargingRule.volume, null, 0.15);
    	when(scpDAO.findLast()).thenReturn(scp);
    	DAOFactory.setSCPDAOInstance(scpDAO);
	}
}
