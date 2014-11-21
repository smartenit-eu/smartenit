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
package eu.smartenit.sbox.db.dao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.LocalVector;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.XVector;

/**
 * The LocalVectorTest class.
 * 
 * It includes methods that test certain support LocalVector functions.
 *
 * @author George Petropoulos
 * @version 2.0
 * 
 */
public class LocalVectorTest {
	
	private static final Logger logger = LoggerFactory.getLogger(LocalVectorTest.class);
	
	/**
	 * It tests the getVectorValueForLink(LinkID linkId) and 
	 * addVectorValueForLink(LinkID linkId, long value) methods.
	 * 
	 */
	@Test
	public void testLocalVectorSupportFunctions () {
		logger.info("Testing local vector support functions.");
		LocalVector vector = new LocalVector();
		
		assertNull(vector.getVectorValues());
		
		SimpleLinkID link1 = new SimpleLinkID("1", "ote");
		vector.addVectorValueForLink(link1, 10000);
		assertNotNull(vector.getVectorValues());
		assertEquals(vector.getVectorValues().size(), 1);
		
		long value = vector.getVectorValueForLink(link1);
		assertEquals(value, 10000);
		
		SimpleLinkID link2 = new SimpleLinkID("2", "ote");
		vector.addVectorValueForLink(link2, 100000);
		assertNotNull(vector.getVectorValues());
		assertEquals(vector.getVectorValues().size(), 2);
		
		value = vector.getVectorValueForLink(link2);
		assertEquals(value, 100000);
		
		SimpleLinkID link3 = new SimpleLinkID("3", "hol");
		value = vector.getVectorValueForLink(link3);
		assertEquals(value, 0);
	}
	
	/**
	 * It tests the getVectorValueForLink(LinkID linkId) and 
	 * addVectorValueForLink(LinkID linkId, long value) methods
	 * for a sample sub-vector, XVector.
	 * 
	 */
	@Test
	public void testXVectorSupportFunctions () {
		logger.info("Testing X vector support functions.");
		XVector xVector = new XVector();
		
		assertNull(xVector.getVectorValues());
		
		SimpleLinkID link1 = new SimpleLinkID("1", "ote");
		xVector.addVectorValueForLink(link1, 10000);
		assertNotNull(xVector.getVectorValues());
		assertEquals(xVector.getVectorValues().size(), 1);
		
		long value = xVector.getVectorValueForLink(link1);
		assertEquals(value, 10000);
		
		SimpleLinkID link2 = new SimpleLinkID("2", "ote");
		xVector.addVectorValueForLink(link2, 100000);
		assertNotNull(xVector.getVectorValues());
		assertEquals(xVector.getVectorValues().size(), 2);
		
		value = xVector.getVectorValueForLink(link2);
		assertEquals(value, 100000);
		
		SimpleLinkID link3 = new SimpleLinkID("3", "hol");
		value = xVector.getVectorValueForLink(link3);
		assertEquals(value, 0);
	}
}
