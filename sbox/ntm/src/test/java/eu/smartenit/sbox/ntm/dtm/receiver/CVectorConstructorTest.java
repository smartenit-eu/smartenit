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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import eu.smartenit.sbox.db.dao.LinkDAO;
import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.ntm.dtm.DAOFactory;
import eu.smartenit.sbox.ntm.dtm.receiver.CVectorConstructor;
import eu.smartenit.sbox.ntm.dtm.receiver.CVectorValuesCalculator;

/**
 * Includes test methods for compensation vector construction from values
 * calculated by {@link CVectorValuesCalculator}.
 * 
 * @author Lukasz Lopatowski
 * @version 3.0
 * 
 */
public class CVectorConstructorTest
{
	private static final String LINK1_ID = "link1";
	private static final String LINK2_ID = "link2";
	private static final String LINK4_ID = "link4";
	private static final String LINK5_ID = "link5";
	private static final String ISP1_ID = "isp1";
	
	private SimpleLinkID linkID1 = new SimpleLinkID(LINK1_ID, ISP1_ID);
	private SimpleLinkID linkID2 = new SimpleLinkID(LINK2_ID, ISP1_ID);
	private SimpleLinkID linkID4 = new SimpleLinkID(LINK4_ID, ISP1_ID);
	private SimpleLinkID linkID5 = new SimpleLinkID(LINK5_ID, ISP1_ID);
	
	private LocalRVector rVector;
	private XVector xVector;
	private LinkDAO dao = mock(LinkDAO.class);
    
	@Before
	public void setup() {
    	rVector = new LocalRVector();
    	rVector.setSourceAsNumber(1);
    	xVector = new XVector();
    	xVector.setSourceAsNumber(1);
    	
		when(dao.findById(linkID1)).thenReturn(
				new Link(null, null, null, 0, null, null, null, null, null, new NetworkAddressIPv4("1.1.1.1", 24)));
		when(dao.findById(linkID2)).thenReturn(
				new Link(null, null, null, 0, null, null, null, null, null, new NetworkAddressIPv4("2.2.2.2", 24)));
		DAOFactory.setLinkDAOInstance(dao);
	}
	
	@Test
	public void shouldConstructCVector() {
    	rVector.addVectorValueForLink(linkID1, 1500L);
    	rVector.addVectorValueForLink(linkID2, 2100L);
    	
    	xVector.addVectorValueForLink(linkID1, 500L);
    	xVector.addVectorValueForLink(linkID2, 800L);
		
		CVector cVector = new CVectorConstructor().construct(xVector, rVector);
		
		assertEquals(2, cVector.getVectorValues().size());
		assertEquals(41, cVector.getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("1.1.1.1", 24)));
		assertEquals(-41, cVector.getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("2.2.2.2", 24)));
		assertEquals(1, cVector.getSourceAsNumber());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIAESinceXVectorNull() {
    	rVector.addVectorValueForLink(linkID1, 1500L);
    	rVector.addVectorValueForLink(linkID5, 1000L);
    	
    	new CVectorConstructor().construct(null, rVector);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIAESinceRVectorNull() {
    	xVector.addVectorValueForLink(linkID1, 500L);
    	xVector.addVectorValueForLink(linkID2, 800L);

    	new CVectorConstructor().construct(xVector, null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIAESinceBothVectorsNull() {
    	new CVectorConstructor().construct(null, null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIAESinceInvalidVectorSize() {
    	rVector.addVectorValueForLink(linkID1, 1500L);
    	rVector.addVectorValueForLink(linkID5, 1000L);
    	xVector.addVectorValueForLink(linkID1, 300L);
    	
    	new CVectorConstructor().construct(xVector, rVector);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIAESinceVectorSizeBelowTwo() {
    	rVector.addVectorValueForLink(linkID1, 1500L);
    	xVector.addVectorValueForLink(linkID1, 300L);
    	
    	new CVectorConstructor().construct(xVector, rVector);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIAESinceInvalidLinkIDs() {
    	rVector.addVectorValueForLink(linkID1, 1500L);
    	rVector.addVectorValueForLink(linkID5, 1000L);
    	xVector.addVectorValueForLink(linkID1, 300L);
    	xVector.addVectorValueForLink(linkID4, 1000L);
    	
    	new CVectorConstructor().construct(xVector, rVector);
	}
	
}
