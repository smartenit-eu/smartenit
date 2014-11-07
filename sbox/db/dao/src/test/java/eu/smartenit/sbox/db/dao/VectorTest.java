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

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.Vector;

/**
 * The VectorTest class.
 * 
 * It includes methods that test certain support Vector functions.
 *
 * @author George Petropoulos
 * @version 1.0
 * 
 */
public class VectorTest {
	
	private static final Logger logger = LoggerFactory.getLogger(VectorTest.class);
	
	/**
	 * It tests the getVectorValueForLink(LinkID linkId) and 
	 * addVectorValueForLink(LinkID linkId, long value) methods.
	 * 
	 */
	@Test
	public void testVectorSupportFunctions () {
		logger.info("Testing vector support functions.");
		Vector vector = new Vector();
		
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
	 * for a sample sub-vector, CVector.
	 * 
	 */
	@Test
	public void testCVectorSupportFunctions () {
		logger.info("Testing c vector support functions.");
		CVector cVector = new CVector();
		
		assertNull(cVector.getVectorValues());
		
		SimpleLinkID link1 = new SimpleLinkID("1", "ote");
		cVector.addVectorValueForLink(link1, 10000);
		assertNotNull(cVector.getVectorValues());
		assertEquals(cVector.getVectorValues().size(), 1);
		
		long value = cVector.getVectorValueForLink(link1);
		assertEquals(value, 10000);
		
		SimpleLinkID link2 = new SimpleLinkID("2", "ote");
		cVector.addVectorValueForLink(link2, 100000);
		assertNotNull(cVector.getVectorValues());
		assertEquals(cVector.getVectorValues().size(), 2);
		
		value = cVector.getVectorValueForLink(link2);
		assertEquals(value, 100000);
		
		SimpleLinkID link3 = new SimpleLinkID("3", "hol");
		value = cVector.getVectorValueForLink(link3);
		assertEquals(value, 0);
	}
}
