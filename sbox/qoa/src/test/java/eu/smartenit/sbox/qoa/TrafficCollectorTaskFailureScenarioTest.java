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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import eu.smartenit.sbox.commons.SBoxThreadHandler;
import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.LinkID;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.Tunnel;
import eu.smartenit.sbox.db.dto.TunnelID;

/**
 * Test class for {@link TrafficCollectorTask} class.
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a href="http://psnc.pl">PSNC</a>)
 *
 */
public class TrafficCollectorTaskFailureScenarioTest {

	private static final String IF_HC_IN_OCTETS_OID = "1.3.6.1.2.1.31.1.1.1.6";
	private static final String IF_HC_OUT_OCTETS_OID = "1.3.6.1.2.1.31.1.1.1.10";
	private static final String BG_ROUTER_PREFIX = "192.168.100.1";
	private static final String DA_ROUTER_PREFIX = "192.168.100.2";
	private static final String COMMUNITY = "smit";
	private static final String OF_SWITCH_DPID = "dpid";
	private static final int AS_NUMBER = 1;
	
	private TrafficCollectorTask trafficCollectorTask;
	private SNMPTrafficCollector snmpTrafficCollector;
	
	@Before
	public void setup() throws InterruptedException, ExecutionException {
		ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);
		@SuppressWarnings("unchecked")
		ScheduledFuture<CounterValues> scheduledFutureForTests = mock(ScheduledFutureForTests.class);
		snmpTrafficCollector = mock(SNMPTrafficCollector.class);
		
		MonitoredLinksInventory monitoredLinks = mock(MonitoredLinksInventory.class);
		when(snmpTrafficCollector.getMonitoredLinks()).thenReturn(monitoredLinks);
		when(monitoredLinks.getBGRoutersByAsNumber(any(Integer.class))).thenReturn(Arrays.asList(prepareBGRouter()));
		when(monitoredLinks.getLinks(any(BGRouter.class))).thenReturn(prepareBGRouter().getInterDomainLinks());
		
		MonitoredTunnelsInventory monitoredTunnels = mock(MonitoredTunnelsInventory.class);
		when(snmpTrafficCollector.getMonitoredTunnels()).thenReturn(monitoredTunnels);
		when(monitoredTunnels.getDARoutersByAsNumber(any(Integer.class))).thenReturn(Arrays.asList(prepareDARouter()));
		when(monitoredTunnels.getTunnels(any(DARouter.class))).thenReturn(Arrays.asList(new Tunnel()));
		
		when(scheduledExecutorService.schedule(any(CounterCollectorThread.class), any(Long.class), any(TimeUnit.class))).thenReturn(scheduledFutureForTests);
		
		SBoxThreadHandler.threadService = scheduledExecutorService;
	}

	@Test
	public void shouldMaxFetchingTimeExpire() throws InterruptedException, ExecutionException {
		trafficCollectorTask = new TrafficCollectorTask(snmpTrafficCollector, AS_NUMBER);
		trafficCollectorTask.run();
		ArgumentCaptor<CounterValues> counterValuesArgument = ArgumentCaptor.forClass(CounterValues.class);
		verify(snmpTrafficCollector, times(1)).notifyNewCounterValues(any(Integer.class), counterValuesArgument.capture());
		
		boolean allZeros = true;
		for (LinkID linkId : counterValuesArgument.getValue().getAllLinkIds()) {
			if(counterValuesArgument.getValue().getLinkCounterValues().get(linkId) != -1) {
				allZeros = false;
				break;
			}
		}
		
		for (TunnelID tunnelId : counterValuesArgument.getValue().getAllTunnelsIds()) {
			if(counterValuesArgument.getValue().getTunnelCounterValues().get(tunnelId) != -1) {
				allZeros = false;
				break;
			}
		}
		
		assertTrue(allZeros);
	}
	
	private BGRouter prepareBGRouter() {
		return new BGRouter(new NetworkAddressIPv4(BG_ROUTER_PREFIX, 32), COMMUNITY, Arrays.asList(preapreLink()));
	}
	
	private DARouter prepareDARouter() {
		return new DARouter(new NetworkAddressIPv4(DA_ROUTER_PREFIX, 32), COMMUNITY, OF_SWITCH_DPID);
	}
	
	private Link preapreLink() {
		final Link link = new Link();
		link.setLinkID(new SimpleLinkID());
		link.setInboundInterfaceCounterOID(IF_HC_IN_OCTETS_OID);
		link.setOutboundInterfaceCounterOID(IF_HC_OUT_OCTETS_OID);
		link.setPhysicalInterfaceName("eth0");
		return link;
	}
	
	public class ScheduledFutureForTests<V> implements ScheduledFuture<V> {
		public long getDelay(TimeUnit unit) {
			return 0;
		}

		public int compareTo(Delayed o) {
			return 0;
		}

		public boolean cancel(boolean mayInterruptIfRunning) {
			return false;
		}

		public boolean isCancelled() {
			return false;
		}

		public boolean isDone() {
			return false;
		}

		public V get() throws InterruptedException, ExecutionException {
			return null;
		}

		public V get(long timeout, TimeUnit unit) throws InterruptedException,
				ExecutionException, TimeoutException {
			return null;
		}
	}

}
