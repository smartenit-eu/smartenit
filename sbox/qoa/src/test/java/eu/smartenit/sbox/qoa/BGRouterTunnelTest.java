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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.snmp4j.CommunityTarget;
import org.snmp4j.smi.OID;

import eu.smartenit.sbox.commons.SBoxProperties;
import eu.smartenit.sbox.commons.SBoxThreadHandler;
import eu.smartenit.sbox.commons.ThreadFactory;
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.CloudDC;
import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.DC2DCCommunicationID;
import eu.smartenit.sbox.db.dto.Direction;
import eu.smartenit.sbox.db.dto.EndAddressPairTunnelID;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.Tunnel;

/**
 * Test class for tunnels terminated in BGRouters.
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a href="http://psnc.pl">PSNC</a>)
 *
 */
public class BGRouterTunnelTest {
	private static final String IF_HC_IN_OCTETS_OID_ETH0 = "1.3.6.1.2.1.31.1.1.1.6.0";
	private static final String IF_HC_IN_OCTETS_OID_ETH1 = "1.3.6.1.2.1.31.1.1.1.6.1";
	private static final String IF_HC_IN_OCTETS_OID_TUNNEL0 = "1.3.6.1.2.1.31.1.1.1.6.2";
	private static final String IF_HC_IN_OCTETS_OID_TUNNEL1 = "1.3.6.1.2.1.31.1.1.1.6.3";
	private static final int AS_NUMBER = 1;
	private SNMPWrapper snmpWrapper = mock(SNMPWrapper.class);
	private SNMPTrafficCollector snmpTrafficCollector = Mockito.mock(SNMPTrafficCollector.class);
	private TrafficCollectorTask trafficCollectorTask = new TrafficCollectorTask(snmpTrafficCollector, AS_NUMBER);
	
	@Before
	public void setup() {
		setupSNMPWrapper();
		setupSNMPTrafficCollector();
		SBoxThreadHandler.threadService = 
				Executors.newScheduledThreadPool(SBoxProperties.CORE_POOL_SIZE, new ThreadFactory());
		
		trafficCollectorTask.setAsNumber(AS_NUMBER);
		trafficCollectorTask.run();
	}
	
	@After
	public void cleanUp() {
		SBoxThreadHandler.shutdownNowThreads();
	}
	
	@Test
	public void shouldFetchDataForTunnels() {
		Mockito.verify(snmpTrafficCollector, Mockito.times(1)).notifyNewCounterValues(AS_NUMBER, setupExpectedCounterValues());
	}
	
	private CounterValues setupExpectedCounterValues() {
		final CounterValues cv = new CounterValues();
		cv.storeCounterValue(new SimpleLinkID("link0", ""), 100);
		cv.storeCounterValue(new SimpleLinkID("link1", ""), 200);
		cv.storeCounterValue(new EndAddressPairTunnelID("tunnel0", 
				new NetworkAddressIPv4("192.168.1.1", 32), 
				new NetworkAddressIPv4("192.168.10.1", 32)), 10);
		cv.storeCounterValue(new EndAddressPairTunnelID("tunnel1", 
				new NetworkAddressIPv4("192.168.1.1", 32),
				new NetworkAddressIPv4("192.168.10.1", 32)), 20);
		return cv;
	}

	private void setupSNMPWrapper() {
		SNMPWrapperFactory.disableUniqueSnmpWrappperCreationMode();
		SNMPWrapperFactory.setSNMPWrapperInstance(snmpWrapper);
		Mockito.doCallRealMethod().when(snmpWrapper).prepareOID(any(String.class));
		Mockito.doCallRealMethod().when(snmpWrapper).prepareTarget(any(String.class), any(String.class));
		
		//link0
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEquals("1.3.6.1.2.1.31.1.1.1.6.0")), Mockito.argThat(new IsAddressEquals("10.10.10.1"))))
			.thenReturn("1.3.6.1.2.1.31.1.1.1.6.0 = 100]");
		
		//link1
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEquals("1.3.6.1.2.1.31.1.1.1.6.1")), Mockito.argThat(new IsAddressEquals("10.10.20.1"))))
			.thenReturn("1.3.6.1.2.1.31.1.1.1.6.1 = 200]");
		
		//tunnel0
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEquals("1.3.6.1.2.1.31.1.1.1.6.2")), Mockito.argThat(new IsAddressEquals("10.10.10.12"))))
			.thenReturn("1.3.6.1.2.1.31.1.1.1.6.2 = 10]");
		
		//tunnel1
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEquals("1.3.6.1.2.1.31.1.1.1.6.3")), Mockito.argThat(new IsAddressEquals("10.10.20.1"))))
			.thenReturn("1.3.6.1.2.1.31.1.1.1.6.2 = 20]");
	}
	
	private void setupSNMPTrafficCollector() {
		final MonitoredTunnelsInventory monitoredTunnels = setupMonitoredTunnels();
		Mockito.when(snmpTrafficCollector.getMonitoredTunnels()).thenReturn(monitoredTunnels);
		final MonitoredLinksInventory monitoredLinks = setupMonitoredLinks();
		Mockito.when(snmpTrafficCollector.getMonitoredLinks()).thenReturn(monitoredLinks);
	}

	private MonitoredLinksInventory setupMonitoredLinks() {
		final MonitoredLinksInventory monitoredLinks = new MonitoredLinksInventory();
		monitoredLinks.populate(Arrays.asList(setupAS()));
		return monitoredLinks;
	}

	private MonitoredTunnelsInventory setupMonitoredTunnels() {
		MonitoredTunnelsInventory monitoredTunnels = new MonitoredTunnelsInventory();
		
		DARouter daRouter1 = new DARouter(
				new NetworkAddressIPv4("10.10.10.12", 32), "smit", "00:00:00:00:00:00:00:01");
		
		CloudDC cloudLocal11 = new CloudDC("cloudLocal11", setupAS(), daRouter1, null, null);
		setupAS().setLocalClouds(Arrays.asList(cloudLocal11));
		
		DC2DCCommunicationID id1 = new DC2DCCommunicationID(1, "id1",
				setupAS().getAsNumber(), cloudLocal11.getCloudDcName(), 200,
				"remoteCloud200");
		
		DC2DCCommunication communication1 = new DC2DCCommunication(id1,
				Direction.incomingTraffic, null, cloudLocal11, null,
				Arrays.asList(setupTunnel(0), setupTunnel(1)));
		
		monitoredTunnels.populate(Arrays.asList(communication1));
		return monitoredTunnels;
	}
	
	private Link setupLink(int number) {
		final Link link = new Link();
		link.setLinkID(new SimpleLinkID("link" + number, ""));
		if (number == 0)
			link.setInboundInterfaceCounterOID(IF_HC_IN_OCTETS_OID_ETH0);
		else 
			link.setInboundInterfaceCounterOID(IF_HC_IN_OCTETS_OID_ETH1);
		link.setPhysicalInterfaceName("eth" + number);
		return link;
	}
	
	private Tunnel setupTunnel(int number) {
		Tunnel tunnel =	new Tunnel(new EndAddressPairTunnelID("tunnel" + number, 
				new NetworkAddressIPv4("192.168.1.1", 32), 
				new NetworkAddressIPv4("192.168.10.1", 32)),
				setupLink(number), null, null, null, 0);
		tunnel.setPhysicalLocalInterfaceName("eth0");
		if (number == 0) {
			tunnel.setLocalRouterAddress(new NetworkAddressIPv4("10.10.10.12", 32));
			tunnel.setInboundInterfaceCounterOID(IF_HC_IN_OCTETS_OID_TUNNEL0);
		}
		else {
			tunnel.setLocalRouterAddress(new NetworkAddressIPv4("10.10.20.1", 32));
			tunnel.setInboundInterfaceCounterOID(IF_HC_IN_OCTETS_OID_TUNNEL1);
		}
		return  tunnel;
	}
	
	private AS setupAS() {
		BGRouter bgRouter1 = new BGRouter(
				new NetworkAddressIPv4("10.10.10.1", 32), "smit", null);
		bgRouter1.setInterDomainLinks(Arrays.asList(setupLink(0)));
		BGRouter bgRouter2 = new BGRouter(
				new NetworkAddressIPv4("10.10.20.1", 32), "smit", null);
		bgRouter2.setInterDomainLinks(Arrays.asList(setupLink(1)));
		
		AS as1 = new AS();
		as1.setLocal(true);
		as1.setAsNumber(1);
		as1.setBgRouters(Arrays.asList(bgRouter1, bgRouter2));
		
		return as1;
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
}
