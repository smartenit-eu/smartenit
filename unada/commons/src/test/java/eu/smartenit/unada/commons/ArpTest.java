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
package eu.smartenit.unada.commons;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import eu.smartenit.unada.commons.commands.ARP;

/**
 * The ArpTest class implementing tests for the ARP command execution.
 *
 * @author George Petropoulos
 * @version 2.0
 */
public class ArpTest {
	
	@Test @Ignore
	public void testExecuteArp() {
		String ipAddress = "192.168.1.1";		
		String macAddress;
		
		macAddress = ARP.getArpInstance().execute(ipAddress);
		assertNull(macAddress);
		
		ipAddress = "10.124.82.1";
		macAddress = ARP.getArpInstance().execute(ipAddress);
		assertNotNull(macAddress);
		assertEquals("00:00:0c:9f:f0:00", macAddress);
	}

}
