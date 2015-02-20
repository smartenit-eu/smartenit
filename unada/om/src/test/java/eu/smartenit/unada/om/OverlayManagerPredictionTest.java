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
package eu.smartenit.unada.om;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.smartenit.unada.db.dao.impl.ContentDAO;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Content;
import eu.smartenit.unada.db.dto.UnadaInfo;
import eu.smartenit.unada.om.messages.BaseMessage;

public class OverlayManagerPredictionTest {
	
	int peerNumber = 7;
	OverlayManager om;
	
	
	@Before
	public void setUp(){
		OverlayBaseTest.fakeUnadaConfig();
		om = new OverlayManager("localOM");
		Overlay overlay = mock(Overlay.class);
		when(overlay.sendMessage(any(UnadaInfo.class), any(BaseMessage.class))).thenReturn(false);
		om.setOverlay(overlay);
		
		UnadaInfo[] infos = new UnadaInfo[peerNumber];
		LinkedList<List<Content>> contents = new LinkedList<List<Content>>();
		for(int i = 0 ; i < peerNumber ; i++){
			infos[i] = new UnadaInfo();
			infos[i].setUnadaID("p" + i);
			contents.add(new LinkedList<Content>());			
		}
		om.setuNaDaInfo(infos[0]);
		
		
		
		//A=1, B=2, C=3, X=24, Y=25
		addContentsTo(new long[]{1,2,3}, contents.get(0));
		addContentsTo(new long[]{1,3,25}, contents.get(1));
		addContentsTo(new long[]{1,24}, contents.get(2));
		addContentsTo(new long[]{1,2,24,25}, contents.get(3));
		addContentsTo(new long[]{2,24}, contents.get(4));
		addContentsTo(new long[]{2,3,25}, contents.get(5));
		addContentsTo(new long[]{3,24}, contents.get(6));
		
		//Mock the local content from the database
		ContentDAO contentDAO = mock(ContentDAO.class);
		when(contentDAO.findAllNotPrefetched()).thenReturn(contents.get(0));
		DAOFactory.setContentDAO(contentDAO);
		
		for(int i = 1 ; i < peerNumber ; i++){
			om.addUnadaInfoForTest(infos[i]);
			om.addContentInfo(infos[i], contents.get(i));
		}
	}
	
	@Test
	public void testPrediction() {
		List<Content> rank = om.getPrediction();
		assertTrue(rank.size() > 0);
		assertEquals(6, rank.get(0).getCacheScore(), 0);
		assertEquals(25, rank.get(0).getContentID());
		rank.remove(0);
		for(Content c : rank){
			assertEquals(5, c.getCacheScore(), 0);
		}
	}
	
	private void addContentsTo(long[] contentIDs, List<Content> contents){
		for(long id : contentIDs){
			Content c = new Content();
			c.setContentID(id);
			contents.add(c);
		}
	}

}
