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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.snmp4j.CommunityTarget;
import org.snmp4j.Target;
import org.snmp4j.smi.OID;

import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SimpleLinkID;

/**
 * Test class for collecting counter values from specific interfaces for BGRouters.
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a href="http://psnc.pl">PSNC</a>)
 *
 */
public class BGRouterCounterCollectorThreadTest {
	private static final String IF_HC_IN_OCTETS_OID = "1.3.6.1.2.1.31.1.1.1.6";
	private static final String IF_HC_OUT_OCTETS_OID = "1.3.6.1.2.1.31.1.1.1.10";
	private static final String IF_HC_IN_OCTETS_OID_RES = "[1.3.6.1.2.1.31.1.1.1.6.2 = 6139713430]";
	private static final String ROUTER_ADDRESS = "192.168.101.1/161";
	private static final String COMMUNITY = "smit";
	private CounterCollectorThread counterCollectorThread;
	private BGRouter bgRouter;
	private SNMPWrapper snmpWrapper = mock(SNMPWrapper.class);
	private SimpleLinkID linkID;
	
	@Before
	public void init() {
		SNMPWrapperFactory.disableUniqueSnmpWrappperCreationMode();
		SNMPWrapperFactory.setSNMPWrapperInstance(snmpWrapper);
		
		this.linkID = new SimpleLinkID();
		this.bgRouter = prepareBgRouter();
		when(snmpWrapper.snmpGet(any(OID.class), any(Target.class))).thenReturn(IF_HC_IN_OCTETS_OID_RES);
		when(snmpWrapper.prepareOID(IF_HC_IN_OCTETS_OID)).thenReturn(new OID(IF_HC_IN_OCTETS_OID));
		when(snmpWrapper.prepareOID(IF_HC_OUT_OCTETS_OID)).thenReturn(new OID(IF_HC_OUT_OCTETS_OID));
		when(snmpWrapper.prepareTarget(ROUTER_ADDRESS, bgRouter.getSnmpCommunity())).thenReturn(new CommunityTarget());
		counterCollectorThread = new CounterCollectorThread(bgRouter.getInterDomainLinks(), null, bgRouter);
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
	public void shouldThrowExceptionBecauseOfLackOfBGRouter() throws Exception {
		counterCollectorThread = new CounterCollectorThread(Arrays.asList(new Link()), null, null);
		counterCollectorThread.collectCounterValues();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowExceptionBecauseOfLackOfLinks() throws Exception {
		counterCollectorThread = new CounterCollectorThread(null, null, bgRouter);
		counterCollectorThread.collectCounterValues();
	}
	
	@Test
	public void shouldCalculateCounterValues() throws Exception {
		final CounterValues expected = new CounterValues();
		expected.storeCounterValue(linkID, Long.valueOf("6139713430"));
		assertTrue(counterCollectorThread.validateBgRouter());
		assertFalse(counterCollectorThread.validateDaRouter());
		assertEquals(expected.toString(), counterCollectorThread.collectCounterValues().toString());
	}
	
	private BGRouter prepareBgRouter() {
		final Link link = new Link();
		link.setLinkID(linkID);
		link.setInboundInterfaceCounterOID(IF_HC_IN_OCTETS_OID);
		link.setOutboundInterfaceCounterOID(IF_HC_OUT_OCTETS_OID);
		link.setPhysicalInterfaceName("eth0");
		final List<Link> links = new ArrayList<Link>();
		links.add(link);
		return new BGRouter(new NetworkAddressIPv4(ROUTER_ADDRESS, 32), COMMUNITY, links);
	}
}
