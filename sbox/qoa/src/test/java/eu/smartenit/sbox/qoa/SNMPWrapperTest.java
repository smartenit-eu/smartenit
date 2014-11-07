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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.OID;

/**
 * Test class for SNMP4j wrapper.
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a href="http://psnc.pl">PSNC</a>)
 *
 */
public class SNMPWrapperTest {
	private final static String OID = "1.2.3.4.5.6.7.8.9.0";
	private static final String ROUTER_ADDRESS = "192.168.100.1";
	private static final String SNMP_COMMUNITY = "smit";
	private Snmp snmpClient;
	private SNMPWrapper snmpWrapper;
	private OID oid;
	private Target target;
	
	@Before
	public void init() {
		snmpClient = mock(Snmp.class);
		snmpWrapper = new SNMPWrapper(snmpClient);
		oid = snmpWrapper.prepareOID(OID);
		target = snmpWrapper.prepareTarget(ROUTER_ADDRESS, SNMP_COMMUNITY);
	}
	
	@Test(expected=IllegalStateException.class)
	public void shouldThrowExceptionForTimedOutForSnmpget() throws Exception {
		snmpWrapper.snmpGet(oid, target);
	}
	
	@Test(expected=IllegalStateException.class)
	public void shouldThrowExceptionForNullResonseForSnmpget() throws Exception {
		when(snmpClient.send(any(PDU.class), any(Target.class))).thenReturn(mock(ResponseEvent.class));
		snmpWrapper.snmpGet(oid, target);
	}
	
	@Test(expected=IllegalStateException.class)
	public void shouldThrowExceptionBecauseOfErrorForSnmpget() throws Exception {
		final ResponseEvent responseEvent = mock(ResponseEvent.class);
		final PDU responsePDU = mock(PDU.class);
		when(snmpClient.send(any(PDU.class), any(Target.class))).thenReturn(responseEvent);
		when(responseEvent.getResponse()).thenReturn(responsePDU);
		when(responsePDU.getErrorStatus()).thenReturn(PDU.wrongEncoding);
		snmpWrapper.snmpGet(oid, target);
	}
	
	@Test
	public void shouldStarClient() throws Exception {
		snmpWrapper.startClient();
		verify(snmpClient, times(1)).listen();
	}
	
	@Test
	public void shouldStopClient() throws Exception {
		snmpWrapper.stopClient();
		verify(snmpClient, times(1)).close();
	}
	
	@Test
	public void shouldNotStopClient() throws Exception {
		snmpWrapper = new SNMPWrapper();
		snmpWrapper.stopClient();
		verify(snmpClient, times(0)).close();
	}
	
	@Test
	public void shouldPrepareTargetForTheAddressWithPort() {
		assertEquals(target.getAddress(), snmpWrapper.prepareTarget(ROUTER_ADDRESS + "/161", SNMP_COMMUNITY).getAddress());
	}
	
	@Test
	public void shouldThrowExceptionBecauseOfSnmpSendException() throws IOException {
		Mockito.doThrow(new IOException()).when(snmpClient).send(any(PDU.class), any(Target.class));
		try {
			snmpWrapper.snmpGet(oid, target);
		} catch (Exception e) {
			assertTrue(e.getMessage().contains("SNMPWrapper Error"));
		}
	}
	
	@Test
	public void shouldThrowExceptionBecauseOfLackOfSnmpClient() throws Exception {
		snmpWrapper = new SNMPWrapper();
		try {
			snmpWrapper.snmpGet(oid, target);
		} catch (Exception e) {
			assertEquals("SNMPWrapper Error: SNMP client is null", e.getMessage());
		}
	}
	
	//TODO create tests for snmpWalk
} 
