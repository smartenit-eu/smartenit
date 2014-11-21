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
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import eu.smartenit.sbox.db.dao.ASDAO;
import eu.smartenit.sbox.db.dao.DC2DCCommunicationDAO;
import eu.smartenit.sbox.ntm.dtm.DAOFactory;
import eu.smartenit.sbox.ntm.dtm.receiver.RemoteSBoxContainer;

/**
 * Includes test methods for populate and get methods of
 * {@link RemoteSBoxContainer} class.
 * 
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public class RemoteSBoxContainerTest {
	
	private ASDAO asDAO = mock(ASDAO.class);
	private DC2DCCommunicationDAO dc2dcCommunicationDAO = mock(DC2DCCommunicationDAO.class); 
	
	@Before
	public void setup() {
		when(asDAO.findAllAsesCloudsBgRoutersLinks()).thenReturn(DBStructuresBuilder.systemsOnTrafficReceiver);
		when(dc2dcCommunicationDAO.findAllDC2DCCommunicationsCloudsTunnels()).thenReturn(DBStructuresBuilder.communicationsOnTrafficReceiver);
		DAOFactory.setASDAOInstance(asDAO);
		DAOFactory.setDC2DCCommunicationDAO(dc2dcCommunicationDAO);
	}
	
	@Test
	public void shouldPopulateContainerStructures() {
		RemoteSBoxContainer container = new RemoteSBoxContainer();
		container.populateFromDB();
		
		assertEquals(2, container.getRemoteSBoxes(1).size());
		assertEquals(1, container.getRemoteSBoxes(2).size());
		assertNull(container.getRemoteSBoxes(3));
		assertEquals("10.1.1.2", container.getRemoteSBoxes(2).get(0).getManagementAddress().getPrefix());
	}

}
