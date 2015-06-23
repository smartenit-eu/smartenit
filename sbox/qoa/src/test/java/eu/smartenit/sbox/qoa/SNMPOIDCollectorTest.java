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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.snmp4j.Target;
import org.snmp4j.smi.OID;

/**
 * Unit tests for SNMPOIDCollector.
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a href="http://psnc.pl">PSNC</a>)
 */
public class SNMPOIDCollectorTest {
	private final static String IF_HC_IN_OCTETS_OID = "1.3.6.1.2.1.31.1.1.1.6";
	private final static String IF_HC_OUT_OCTETS_OID = "1.3.6.1.2.1.31.1.1.1.10";
	private SNMPWrapper snmpWrapper = mock(SNMPWrapper.class);
	private SNMPOIDCollector snmpOidCollector;
	private final static String PHY_INF_NAME = "eth0";
	private List<String> walkResponse;
	
	@Before
	public void init() {
		SNMPWrapperFactory.disableUniqueSnmpWrappperCreationMode();
		SNMPWrapperFactory.setSNMPWrapperInstance(snmpWrapper);
		
		snmpOidCollector = new SNMPOIDCollector(null, null);
		
		walkResponse = new ArrayList<String>();
		walkResponse.add("1.3.6.1.2.1.31.1.1.1.1.1 = lo");
		walkResponse.add("1.3.6.1.2.1.31.1.1.1.1.2 = eth0");
		walkResponse.add("1.3.6.1.2.1.31.1.1.1.1.3 = eth1");
		walkResponse.add("1.3.6.1.2.1.31.1.1.1.1.4 = eth2");
		walkResponse.add("1.3.6.1.2.1.31.1.1.1.1.5 = eth3");
		walkResponse.add("1.3.6.1.2.1.31.1.1.1.1.6 = eth4");
		walkResponse.add("1.3.6.1.2.1.31.1.1.1.1.7 = eth5");
		walkResponse.add("1.3.6.1.2.1.31.1.1.1.1.8 = eth6");
		walkResponse.add("1.3.6.1.2.1.31.1.1.1.1.9 = eth7");
		
		when(snmpWrapper.snmpWalk(any(OID.class), any(Target.class))).thenReturn(walkResponse);
	}
	
	@Test
	public void shouldParseInterfaceNumber() {
		assertEquals("2", snmpOidCollector.parseInterface(PHY_INF_NAME, walkResponse));
	}
	
	@Test
	public void shouldSetOutboundInterfaceCounterOID() throws IOException {
		assertEquals(IF_HC_OUT_OCTETS_OID + ".2", snmpOidCollector.getInterfaceCounterOID(IF_HC_OUT_OCTETS_OID, PHY_INF_NAME, walkResponse));
	}
	
	@Test
	public void shouldSetInboundInterfaceCounterOID() throws IOException {
		assertEquals(IF_HC_IN_OCTETS_OID + ".2", snmpOidCollector.getInterfaceCounterOID(IF_HC_IN_OCTETS_OID, PHY_INF_NAME, walkResponse));
	}
	
	@Test
	public void shouldCollectOIDsForLinks() throws IOException {
		final MonitoredLinksInventory monitoredLinks = new MonitoredLinksInventory();
		monitoredLinks.populate(DBStructuresBuilder.systems);
		final MonitoredTunnelsInventory monitoredTunnels = new MonitoredTunnelsInventory();
		monitoredTunnels.populate(DBStructuresBuilder.communications);
		
		snmpOidCollector = new SNMPOIDCollector(monitoredLinks, monitoredTunnels);
		snmpOidCollector.collectOIDsForLinks();
		verify(snmpWrapper, times(3)).snmpWalk(any(OID.class), any(Target.class));
	}
	
	@Test
	public void shouldCollectOIDsForTunnels() throws IOException {
		final MonitoredLinksInventory monitoredLinks = new MonitoredLinksInventory();
		monitoredLinks.populate(DBStructuresBuilder.systems);
		final MonitoredTunnelsInventory monitoredTunnels = new MonitoredTunnelsInventory();
		monitoredTunnels.populate(DBStructuresBuilder.communications);
		
		snmpOidCollector = new SNMPOIDCollector(monitoredLinks, monitoredTunnels);
		snmpOidCollector.collectOIDsForTunnels();
		verify(snmpWrapper, times(6)).snmpWalk(any(OID.class), any(Target.class));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowExceptionBecauseOfLackOfSnmpWalkResponse() {
		snmpOidCollector.parseInterface(PHY_INF_NAME, null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowExceptionBecauseOfLackOfPhysicalInterfaceName() {
		snmpOidCollector.parseInterface(null, null);
	}
}