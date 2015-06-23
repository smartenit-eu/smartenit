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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import eu.smartenit.sbox.db.dao.LinkDAO;
import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.LinkID;
import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.LocalVectorValue;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.ntm.dtm.DAOFactory;

/**
 * Tests functionalities related with handling links with R vector achieved.
 * 
 * @author Lukasz Lopatowski
 * @version 3.1
 */
public class LinksWithRVectorAchievedHandlingTest {

	private int asNumber1 = 1;
	private int asNumber2 = 2;
	private static final String LINK1_ID = "link1";
	private static final String LINK2_ID = "link2";
	private static final String ISP1_ID = "isp1";
	private SimpleLinkID linkID1 = new SimpleLinkID(LINK1_ID, ISP1_ID);
	private SimpleLinkID linkID2 = new SimpleLinkID(LINK2_ID, ISP1_ID);
	private NetworkAddressIPv4 tunnelsOnLink1 = new NetworkAddressIPv4("1.1.1.1", 24);
	private NetworkAddressIPv4 tunnelsOnLink2 = new NetworkAddressIPv4("2.2.2.2", 24);
	private LocalRVector rVector;
	
	private LinkDAO dao = mock(LinkDAO.class);
    
	@Before
	public void setup() {
    	rVector = new LocalRVector(Arrays.asList(new LocalVectorValue(2000, linkID1), new LocalVectorValue(3000, linkID2)), asNumber1);
    	
		when(dao.findById(linkID1)).thenReturn(
				new Link(linkID1, null, null, 0, null, null, null, null, null, tunnelsOnLink1));
		when(dao.findById(linkID2)).thenReturn(
				new Link(linkID2, null, null, 0, null, null, null, null, null, tunnelsOnLink2));
		DAOFactory.setLinkDAOInstance(dao);

		CVectorUpdateController.activate();
		CVectorUpdateController.getInstance().reset();
	}
	
	@Test
	public void shouldReturnProperStatusOfCVectorPreparation() {
		CVectorUpdateController controller = CVectorUpdateController.getInstance(); 
		
		assertFalse(controller.isCVectorPreparedByController(asNumber1));
		controller
			.updateLinksWithRVectorAchieved(asNumber2, Arrays.asList((LinkID)new SimpleLinkID()));
		assertFalse(controller.isCVectorPreparedByController(asNumber1));
		assertTrue(controller.isCVectorPreparedByController(asNumber2));
		controller
			.updateLinksWithRVectorAchieved(asNumber1, Arrays.asList((LinkID)new SimpleLinkID()));
		controller
			.updateLinksWithRVectorAchieved(asNumber2, Arrays.asList((LinkID)new SimpleLinkID()));
		assertTrue(controller.isCVectorPreparedByController(asNumber1));
		assertTrue(controller.isCVectorPreparedByController(asNumber2));
		
		controller.reset();
		assertFalse(controller.isCVectorPreparedByController(asNumber1));
		assertFalse(controller.isCVectorPreparedByController(asNumber2));
		controller
			.updateLinksWithRVectorAchieved(asNumber2, Arrays.asList((LinkID)new SimpleLinkID()));
		assertTrue(controller.isCVectorPreparedByController(asNumber2));
	}
	
	@Test
	public void shouldReturnPreparedCVector() {
		CVectorUpdateController controller = CVectorUpdateController.getInstance();
		controller.updateLinksWithRVectorAchieved(asNumber1, Arrays.asList((LinkID) linkID1));
		CVector result = controller.prepareCVector(asNumber1, rVector);
		assertEquals(100 * (2000 + 3000), result.getVectorValueForTunnelEndPrefix(tunnelsOnLink1));
		assertEquals(-1 * 100 * (2000 + 3000), result.getVectorValueForTunnelEndPrefix(tunnelsOnLink2));
		
		result = controller.prepareCVector(asNumber1, rVector);
		assertEquals(100 * (2000 + 3000), result.getVectorValueForTunnelEndPrefix(tunnelsOnLink1));
		assertEquals(-1 * 100 * (2000 + 3000), result.getVectorValueForTunnelEndPrefix(tunnelsOnLink2));
		
		controller.sent(result);
		controller.updateLinksWithRVectorAchieved(asNumber1, Arrays.asList((LinkID)linkID1, (LinkID)linkID2));
		result = controller.prepareCVector(asNumber1, rVector);
		assertEquals(-1 * 100 * (2000 + 3000), result.getVectorValueForTunnelEndPrefix(tunnelsOnLink1));
		assertEquals(100 * (2000 + 3000), result.getVectorValueForTunnelEndPrefix(tunnelsOnLink2));
	
		controller.sent(result);
		result = controller.prepareCVector(asNumber1, rVector);
		assertEquals(100 * (2000 + 3000), result.getVectorValueForTunnelEndPrefix(tunnelsOnLink1));
		assertEquals(-1 * 100 * (2000 + 3000), result.getVectorValueForTunnelEndPrefix(tunnelsOnLink2));
		
		controller.sent(result);
		result = controller.prepareCVector(asNumber1, rVector);
		assertEquals(-1 * 100 * (2000 + 3000), result.getVectorValueForTunnelEndPrefix(tunnelsOnLink1));
		assertEquals(100 * (2000 + 3000), result.getVectorValueForTunnelEndPrefix(tunnelsOnLink2));
		
		controller.reset();
		assertFalse(controller.isCVectorPreparedByController(asNumber1));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void shouldThrowExceptionOnInvalidASNumber() {
		CVectorUpdateController.getInstance().prepareCVector(0, null);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void shouldThrowExceptionOnRVectorNull() {
		CVectorUpdateController.getInstance().prepareCVector(1, null);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void shouldThrowExceptionOnInvalidRVector() {
		CVectorUpdateController.getInstance().prepareCVector(1, new LocalRVector(Arrays.asList(new LocalVectorValue()), 1));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void shouldThrowExceptionOnInvalidASNotFound() {
		CVectorUpdateController.getInstance()
			.updateLinksWithRVectorAchieved(asNumber1, Arrays.asList((LinkID)new SimpleLinkID(LINK1_ID, ISP1_ID)));
		CVectorUpdateController.getInstance()
			.prepareCVector(asNumber2, new LocalRVector(Arrays.asList(new LocalVectorValue(), new LocalVectorValue()), asNumber2));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void shouldThrowExceptionOnInvalidASWithZeroLinks() {
		CVectorUpdateController.getInstance()
			.updateLinksWithRVectorAchieved(asNumber1, new ArrayList<LinkID>());
		CVectorUpdateController.getInstance()
			.prepareCVector(asNumber1, new LocalRVector(Arrays.asList(new LocalVectorValue(), new LocalVectorValue()), asNumber1));
	}
	
}
