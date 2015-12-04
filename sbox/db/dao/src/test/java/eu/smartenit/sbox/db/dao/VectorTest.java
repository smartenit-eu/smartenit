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
package eu.smartenit.sbox.db.dao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.Vector;

/**
 * The VectorTest class.
 * 
 * It includes methods that test certain support Vector functions.
 *
 * @author George Petropoulos
 * @version 2.0
 * 
 */
public class VectorTest {
	
	private static final Logger logger = LoggerFactory.getLogger(VectorTest.class);
	
	/**
	 * It tests the getVectorValueForTunnelEndPrefix(NetworkAddressIPv4 tunnelEndPrefix) and 
	 * addVectorValueForTunnelEndPrefix(NetworkAddressIPv4 tunnelEndPrefix, long value) methods.
	 * 
	 */
	@Test 
	public void testVectorSupportFunctions () {
		logger.info("Testing vector support functions.");
		Vector vector = new Vector();
		
		assertNull(vector.getVectorValues());
		
		NetworkAddressIPv4 tunnelEndPrefix = new NetworkAddressIPv4("1.1.1.1", 32);
		vector.addVectorValueForTunnelEndPrefix(tunnelEndPrefix, 10000);
		assertNotNull(vector.getVectorValues());
		assertEquals(vector.getVectorValues().size(), 1);
		
		NetworkAddressIPv4 testEndPrefix = new NetworkAddressIPv4("1.1.1.1", 32);
		long value = vector.getVectorValueForTunnelEndPrefix(testEndPrefix);
		assertEquals(value, 10000);
		
		tunnelEndPrefix = new NetworkAddressIPv4("2.2.2.2", 32);
		vector.addVectorValueForTunnelEndPrefix(tunnelEndPrefix, 20000);
		assertNotNull(vector.getVectorValues());
		assertEquals(vector.getVectorValues().size(), 2);
		
		testEndPrefix = new NetworkAddressIPv4("2.2.2.2", 32);
		value = vector.getVectorValueForTunnelEndPrefix(testEndPrefix);
		assertEquals(value, 20000);
		
		testEndPrefix = new NetworkAddressIPv4("2.2.2.2", 12);
		value = vector.getVectorValueForTunnelEndPrefix(testEndPrefix);
		assertEquals(value, 0);
	}
	
	/**
	 * It tests the getVectorValueForTunnelEndPrefix(NetworkAddressIPv4 tunnelEndPrefix) and 
	 * addVectorValueForTunnelEndPrefix(NetworkAddressIPv4 tunnelEndPrefix, long value) methods
	 * for a sample sub-vector, CVector.
	 * 
	 */
	@Test 
	public void testCVectorSupportFunctions () {
		logger.info("Testing c vector support functions.");
		CVector cVector = new CVector();
		
		assertNull(cVector.getVectorValues());
		
		assertNull(cVector.getVectorValues());
		
		NetworkAddressIPv4 tunnelEndPrefix = new NetworkAddressIPv4("1.1.1.1", 32);
		cVector.addVectorValueForTunnelEndPrefix(tunnelEndPrefix, 10000);
		assertNotNull(cVector.getVectorValues());
		assertEquals(cVector.getVectorValues().size(), 1);
		
		NetworkAddressIPv4 testEndPrefix = new NetworkAddressIPv4("1.1.1.1", 32);
		long value = cVector.getVectorValueForTunnelEndPrefix(testEndPrefix);
		assertEquals(value, 10000);
		
		tunnelEndPrefix = new NetworkAddressIPv4("2.2.2.2", 32);
		cVector.addVectorValueForTunnelEndPrefix(tunnelEndPrefix, 20000);
		assertNotNull(cVector.getVectorValues());
		assertEquals(cVector.getVectorValues().size(), 2);
		
		testEndPrefix = new NetworkAddressIPv4("2.2.2.2", 32);
		value = cVector.getVectorValueForTunnelEndPrefix(testEndPrefix);
		assertEquals(value, 20000);
		
		testEndPrefix = new NetworkAddressIPv4("2.2.2.2", 12);
		value = cVector.getVectorValueForTunnelEndPrefix(testEndPrefix);
		assertEquals(value, 0);
	}
}
