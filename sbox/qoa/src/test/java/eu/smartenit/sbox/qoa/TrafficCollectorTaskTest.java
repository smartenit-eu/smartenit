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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import eu.smartenit.sbox.commons.SBoxProperties;
import eu.smartenit.sbox.commons.SBoxThreadHandler;
import eu.smartenit.sbox.commons.ThreadFactory;
import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SimpleLinkID;

/**
 * Test class for {@link TrafficCollectorTask} class.
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a href="http://psnc.pl">PSNC</a>)
 *
 */
public class TrafficCollectorTaskTest {
	private static final String IF_HC_IN_OCTETS_OID = "1.3.6.1.2.1.31.1.1.1.6";
	private static final String IF_HC_OUT_OCTETS_OID = "1.3.6.1.2.1.31.1.1.1.10";
	private final static String BG_ROUTER_PREFIX = "192.168.100.1";
	private final static String DA_ROUTER_PREFIX = "192.168.100.2";
	private static final String COMMUNITY = "smit";
	private final static int AS_NUMBER = 1;
	private TrafficCollectorTask trafficCollectorTask;
	private SNMPTrafficCollector snmpTrafficCollector;

	@Before
	public void setup() {
		snmpTrafficCollector = mock(SNMPTrafficCollector.class);
		SBoxThreadHandler.threadService = Executors.newScheduledThreadPool(SBoxProperties.CORE_POOL_SIZE, new ThreadFactory());
		
		MonitoredLinksInventory monitoredLinks = mock(MonitoredLinksInventory.class);
		when(snmpTrafficCollector.getMonitoredLinks()).thenReturn(monitoredLinks);
		when(monitoredLinks.getBGRoutersByAsNumber(any(Integer.class))).thenReturn(Arrays.asList(prepareBGRouter(), prepareBGRouter(), prepareBGRouter()));
		
		MonitoredTunnelsInventory monitoredTunnels = mock(MonitoredTunnelsInventory.class);
		when(snmpTrafficCollector.getMonitoredTunnels()).thenReturn(monitoredTunnels);
		when(monitoredTunnels.getDARoutersByAsNumber(any(Integer.class))).thenReturn(Arrays.asList(prepareDARouter()));
	}
	
	@Test
	public void shouldNotifyNewCounterValues() {
		trafficCollectorTask = new TrafficCollectorTask(snmpTrafficCollector, AS_NUMBER);
		trafficCollectorTask.run();
		
		ArgumentCaptor<Integer> asNumberArgument = ArgumentCaptor.forClass(Integer.class);
		
		verify(snmpTrafficCollector, times(1)).notifyNewCounterValues(asNumberArgument.capture(), any(CounterValues.class));
		assertEquals(Integer.valueOf(AS_NUMBER), asNumberArgument.getValue());
	}
	
	private BGRouter prepareBGRouter() {
		return new BGRouter(new NetworkAddressIPv4(BG_ROUTER_PREFIX, 32), COMMUNITY, Arrays.asList(preapreLink()));
	}
	
	private DARouter prepareDARouter() {
		return new DARouter(new NetworkAddressIPv4(DA_ROUTER_PREFIX, 32), COMMUNITY);
	}

	private Link preapreLink() {
		final Link link = new Link();
		link.setLinkID(new SimpleLinkID("" + System.nanoTime(), ""));
		link.setInboundInterfaceCounterOID(IF_HC_IN_OCTETS_OID);
		link.setOutboundInterfaceCounterOID(IF_HC_OUT_OCTETS_OID);
		link.setPhysicalInterfaceName("eth0");
		return link;
	}
}
