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
package eu.smartenit.sbox.ntm.dtm.receiver;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.smartenit.sbox.db.dto.LinkID;
import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.LocalVectorValue;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.ntm.dtm.receiver.CVectorValuesCalculator;

/**
 * Includes test methods for compensation vector values calculation logic
 * implemented in {@link CVectorValuesCalculator} class.
 * 
 * @author Lukasz Lopatowski
 * @version 1.2
 * 
 */
public class CVectorValuesCalculatorTest
{
	private static final String LINK1_ID = "link1";
	private static final String LINK2_ID = "link2";
	private static final String LINK3_ID = "link3";
	private static final String LINK4_ID = "link4";
	private static final String LINK5_ID = "link5";
	private static final String ISP1_ID = "isp1";
	
	private SimpleLinkID linkID1 = new SimpleLinkID(LINK1_ID, ISP1_ID);
	private SimpleLinkID linkID2 = new SimpleLinkID(LINK2_ID, ISP1_ID);
	private SimpleLinkID linkID3 = new SimpleLinkID(LINK3_ID, ISP1_ID);
	private SimpleLinkID linkID4 = new SimpleLinkID(LINK4_ID, ISP1_ID);
	private SimpleLinkID linkID5 = new SimpleLinkID(LINK5_ID, ISP1_ID);
	
	private LocalRVector rVector;
	private XVector xVector;
	
	@Before
	public void setup() {
    	rVector = new LocalRVector();
    	rVector.setSourceAsNumber(1);
    	xVector = new XVector();
    	xVector.setSourceAsNumber(1);
	}
	
	@Test
	public void shouldCalculateFromTwoDimentionVectors() {
    	rVector.addVectorValueForLink(linkID1, 1500L);
    	rVector.addVectorValueForLink(linkID2, 2100L);
    	
    	xVector.addVectorValueForLink(linkID1, 500L);
    	xVector.addVectorValueForLink(linkID2, 800L);
		
		List<LocalVectorValue> cVectorValues = new CVectorValuesCalculator(xVector, rVector).calculate();
		assertEquals(2, cVectorValues.size());
		assertEquals(41, getVectorValueForLink(cVectorValues, linkID1));
		assertEquals(-41, getVectorValueForLink(cVectorValues, linkID2));
	}
	
	@Test
	public void shouldCalculateWithOneXVectorValueZero() {
    	rVector.addVectorValueForLink(linkID1, 1500L);
    	rVector.addVectorValueForLink(linkID2, 2100L);
    	
    	xVector.addVectorValueForLink(linkID1, 500L);
    	xVector.addVectorValueForLink(linkID2, 0L);
		
		List<LocalVectorValue> cVectorValues = new CVectorValuesCalculator(xVector, rVector).calculate();
		assertEquals(2, cVectorValues.size());
		assertEquals(-291, getVectorValueForLink(cVectorValues, linkID1));
		assertEquals(291, getVectorValueForLink(cVectorValues, linkID2));
	}
	
	@Test
	public void shouldCalculateWithEqualRAndXVectorValuesRatio() {
    	rVector.addVectorValueForLink(linkID1, 2000L);
    	rVector.addVectorValueForLink(linkID2, 2000L);
    	
    	xVector.addVectorValueForLink(linkID1, 1000L);
    	xVector.addVectorValueForLink(linkID2, 1000L);
		
		List<LocalVectorValue> cVectorValues = new CVectorValuesCalculator(xVector, rVector).calculate();
		assertEquals(2, cVectorValues.size());
		assertEquals(0, getVectorValueForLink(cVectorValues, linkID1));
		assertEquals(0, getVectorValueForLink(cVectorValues, linkID2));
	}
	
	@Test
	public void shouldCalculateWithAllXVectorValuesZero() {
    	rVector.addVectorValueForLink(linkID1, 1500L);
    	rVector.addVectorValueForLink(linkID2, 2100L);
    	
    	xVector.addVectorValueForLink(linkID1, 0L);
    	xVector.addVectorValueForLink(linkID2, 0L);
		
		List<LocalVectorValue> cVectorValues = new CVectorValuesCalculator(xVector, rVector).calculate();
		assertEquals(2, cVectorValues.size());
		assertEquals(0, getVectorValueForLink(cVectorValues, linkID1));
		assertEquals(0, getVectorValueForLink(cVectorValues, linkID2));
	}
	
	@Test
	public void shouldCalculateWithXVectorEqualRVactor() {
    	rVector.addVectorValueForLink(linkID1, 1500L);
    	rVector.addVectorValueForLink(linkID2, 2100L);
    	
    	xVector.addVectorValueForLink(linkID1, 1500L);
    	xVector.addVectorValueForLink(linkID2, 2100L);
		
		List<LocalVectorValue> cVectorValues = new CVectorValuesCalculator(xVector, rVector).calculate();
		assertEquals(2, cVectorValues.size());
		assertEquals(0, getVectorValueForLink(cVectorValues, linkID1));
		assertEquals(0, getVectorValueForLink(cVectorValues, linkID2));
	}
	
	@Test
	public void shouldCalculateFromFiveDimentionVectors() {
    	rVector.addVectorValueForLink(linkID1, 1500L);
    	rVector.addVectorValueForLink(linkID2, 2500L);
    	rVector.addVectorValueForLink(linkID3, 2500L);
    	rVector.addVectorValueForLink(linkID4, 2000L);
    	rVector.addVectorValueForLink(linkID5, 1000L);
    	
    	xVector.addVectorValueForLink(linkID1, 300L);
    	xVector.addVectorValueForLink(linkID2, 400L);
    	xVector.addVectorValueForLink(linkID3, 500L);
    	xVector.addVectorValueForLink(linkID4, 600L);
    	xVector.addVectorValueForLink(linkID5, 700L);
    	
		List<LocalVectorValue> cVectorValues = new CVectorValuesCalculator(xVector, rVector).calculate();
		assertEquals(5, cVectorValues.size());
		assertEquals(94, getVectorValueForLink(cVectorValues, linkID1));
		assertEquals(257, getVectorValueForLink(cVectorValues, linkID2));
		assertEquals(157, getVectorValueForLink(cVectorValues, linkID3));
		assertEquals(-73, getVectorValueForLink(cVectorValues, linkID4));
		assertEquals(-436, getVectorValueForLink(cVectorValues, linkID5));
	}
	
	public long getVectorValueForLink(List<LocalVectorValue> vectorValues, LinkID linkId) {
		for (LocalVectorValue v : vectorValues) {
			if (v.getLinkID().equals(linkId))
				return v.getValue();
		}
		return -1;
	}

}
