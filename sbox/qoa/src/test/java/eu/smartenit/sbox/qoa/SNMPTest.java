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
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.snmp4j.smi.OID;

/**
 * Unit tests for SNMP.
 * All tests are for specific VM thus all tests are Ignored.
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a href="http://psnc.pl">PSNC</a>)
 */
public class SNMPTest {
	private static final String COMMUNITY = "smit";
	private static final String IP_ADDRESS = "192.168.56.101";
	private static final String GET_OID = "1.3.6.1.2.1.31.1.1.1.6.2";
	private static final String WALK_OID = "1.3.6.1.2.1.31.1.1.1.1";
	private SNMPWrapper snmpWrapper;
	private SNMPOIDCollector snmpOidCollector;
	
	@Before
	public void init() throws IOException {
		this.snmpWrapper = new SNMPWrapper();
		this.snmpOidCollector = new SNMPOIDCollector(null, null);
		snmpWrapper.startClient();
	}
	
	@Test @Ignore
	public void shouldTriggerSnmpget() {
		assertTrue(snmpWrapper.snmpGet(new OID(GET_OID), snmpWrapper.prepareTarget(IP_ADDRESS, COMMUNITY)).contains("[1.3.6.1.2.1.31.1.1.1.6.2 ="));
	}
	
	@Test @Ignore
	public void shouldTriggerSnmpwalk() {
		assertEquals("[1.3.6.1.2.1.31.1.1.1.1.1 = lo, 1.3.6.1.2.1.31.1.1.1.1.2 = eth0]", snmpWrapper.snmpWalk(new OID(WALK_OID), snmpWrapper.prepareTarget(IP_ADDRESS, COMMUNITY)).toString());
	}
	
	@Test @Ignore
	public void shouldReturnInterfaceNumber() {
		assertEquals("2", snmpOidCollector.parseInterface("eth0", snmpWrapper.snmpWalk(new OID(WALK_OID), snmpWrapper.prepareTarget(IP_ADDRESS, COMMUNITY))));
	}
	
	@Test @Ignore
	public void shouldParseCommunityTarget() {
		// This test fails because CommunityTarget class don't have proper equals method
		assertEquals(snmpWrapper.prepareTarget(IP_ADDRESS, COMMUNITY), snmpWrapper.prepareTarget(IP_ADDRESS, COMMUNITY));
	}
	
	@After
	public void clean() throws IOException {
		snmpWrapper.stopClient();
	}

}
