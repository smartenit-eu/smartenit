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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.snmp4j.CommunityTarget;
import org.snmp4j.Target;
import org.snmp4j.smi.OID;

import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.EndAddressPairTunnelID;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.Tunnel;

/**
 * Test class for collecting counter values from specific interfaces of DARouters.
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a href="http://psnc.pl">PSNC</a>)
 *
 */
public class DARouterCounterCollectorThreadTest {
	private static final String IF_HC_IN_OCTETS_OID = "1.3.6.1.2.1.31.1.1.1.6";
	private static final String IF_HC_OUT_OCTETS_OID = "1.3.6.1.2.1.31.1.1.1.10";
	private static final String IF_HC_IN_OCTETS_OID_RES = "[1.3.6.1.2.1.31.1.1.1.6.2 = 6139713430]";
	private static final String ROUTER_ADDRESS = "192.168.101.1/161";
	private static final String COMMUNITY = "smit";
	private static final String OF_SWITCH_DPID = "dpid";
	private CounterCollectorThread counterCollectorThread;
	private DARouter daRouter;
	private SNMPWrapper snmpWrapper = mock(SNMPWrapper.class);
	private EndAddressPairTunnelID tunnelID;
	private Tunnel tunnel;
	
	@Before
	public void init() {
		SNMPWrapperFactory.disableUniqueSnmpWrappperCreationMode();
		SNMPWrapperFactory.setSNMPWrapperInstance(snmpWrapper);
		
		this.tunnelID = new EndAddressPairTunnelID();
		this.daRouter = prepareDARouter();
		this.tunnel = prepareTunnel();
		when(snmpWrapper.snmpGet(any(OID.class), any(Target.class))).thenReturn(IF_HC_IN_OCTETS_OID_RES);
		when(snmpWrapper.prepareOID(IF_HC_IN_OCTETS_OID)).thenReturn(new OID(IF_HC_IN_OCTETS_OID));
		when(snmpWrapper.prepareOID(IF_HC_OUT_OCTETS_OID)).thenReturn(new OID(IF_HC_OUT_OCTETS_OID));
		when(snmpWrapper.prepareTarget(ROUTER_ADDRESS, daRouter.getSnmpCommunity())).thenReturn(new CommunityTarget());
		counterCollectorThread = new CounterCollectorThread(Arrays.asList(tunnel), daRouter);
	}

	@Test
	public void shouldParseCounter() {
		assertEquals(Long.valueOf("6139713430").longValue(), (long)counterCollectorThread.parseCounterValue("[1.3.6.1.2.1.31.1.1.1.6.2 = 6139713430]"));
	}
	
	@Test(expected=NumberFormatException.class)
	public void shouldThrowExceptionForParseCounter() {
		counterCollectorThread.parseCounterValue("[1.3.6.1.2.1.31.1.1.1.6.2 = error]");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowExceptionBecauseOfLackOfDARouter() throws Exception {
		counterCollectorThread = new CounterCollectorThread(Arrays.asList(new Tunnel()), null);
		counterCollectorThread.collectCounterValues();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowExceptionBecauseOfLackOfTunnels() throws Exception {
		counterCollectorThread = new CounterCollectorThread(null, daRouter);
		counterCollectorThread.collectCounterValues();
	}
	
	@Test
	public void shouldCalculateCounterValues() throws Exception {
		final CounterValues expected = new CounterValues();
		expected.storeCounterValue(tunnelID, Long.valueOf("6139713430"));
		assertTrue(counterCollectorThread.validateDaRouter());
		assertFalse(counterCollectorThread.validateBgRouter());
		assertEquals(expected.toString(), counterCollectorThread.collectCounterValues().toString());
	}
	
	private DARouter prepareDARouter() {
		return new DARouter(new NetworkAddressIPv4(ROUTER_ADDRESS, 32), COMMUNITY, OF_SWITCH_DPID);
	}
	
	private Tunnel prepareTunnel() {
		final Tunnel tunnel = new Tunnel();
		tunnel.setTunnelID(tunnelID);
		tunnel.setInboundInterfaceCounterOID(IF_HC_IN_OCTETS_OID);
		tunnel.setOutboundInterfaceCounterOID(IF_HC_OUT_OCTETS_OID);
		tunnel.setPhysicalLocalInterfaceName("eth0");
		return tunnel;
	}
}
