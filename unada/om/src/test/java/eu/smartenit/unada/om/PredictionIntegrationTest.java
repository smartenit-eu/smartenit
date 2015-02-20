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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import eu.smartenit.unada.db.dao.impl.ContentDAO;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Content;

public class PredictionIntegrationTest extends OverlayBaseTest{

	@Test
	public void test() {
		
		om1.advertiseContent(contentID1);
		om1.advertiseContent(contentID2);
		
		List<Content> all = new LinkedList<>();
		Content c1 = new Content();
		c1.setContentID(contentID1);
		all.add(c1);
		
		Content c2 = new Content();
		c2.setContentID(contentID1);
		all.add(c2);
		
		ContentDAO contentDAO = mock(ContentDAO.class);
		when(contentDAO.findAllNotPrefetched()).thenReturn(all);
		DAOFactory.setContentDAO(contentDAO);
		
		om2.advertiseContent(contentID1);
		om2.getPrediction();
		
	}

}
