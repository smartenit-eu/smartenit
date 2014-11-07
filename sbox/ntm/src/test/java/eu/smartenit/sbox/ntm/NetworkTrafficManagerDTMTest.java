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
package eu.smartenit.sbox.ntm;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.smartenit.sbox.db.dao.ASDAO;
import eu.smartenit.sbox.db.dao.DC2DCCommunicationDAO;
import eu.smartenit.sbox.ntm.dtm.DAOFactory;

/**
 * Includes test methods for {@link NetworkTrafficManager} initialization of DTM
 * modules in three modes as specified in {@link NetworkTrafficManagerDTMMode}.
 * 
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public class NetworkTrafficManagerDTMTest {
	
	@BeforeClass
	public static void setupDAOMocks() {
		ASDAO asDAO = mock(ASDAO.class);
		DC2DCCommunicationDAO dcDAO = mock(DC2DCCommunicationDAO.class);
    	DAOFactory.setASDAOInstance(asDAO);
		DAOFactory.setDC2DCCommunicationDAO(dcDAO);
	}
	
	@Test
	public void shouldCreateAndInitializeNTMInSenderOnlyMode() {
		NetworkTrafficManager ntm = new NetworkTrafficManager();
		ntm.initialize(NetworkTrafficManagerDTMMode.TRAFFIC_SENDER);
		assertNotNull(ntm.getDtmTrafficManager());
		assertNotNull(ntm.getDtmVectorsReceiver());
	}
	
	@Test
	public void shouldCreateAndInitializeNTMInReceiverOnlyMode() {
		NetworkTrafficManager ntm = new NetworkTrafficManager();
		ntm.initialize(NetworkTrafficManagerDTMMode.TRAFFIC_RECEIVER);
		assertNotNull(ntm.getDtmTrafficManager());
		assertNotNull(ntm.getDtmVectorsReceiver());
	}

	@Test
	public void shouldCreateAndInitializeNTMInGeneralMode() {
		NetworkTrafficManager ntm = new NetworkTrafficManager();
		ntm.initialize(NetworkTrafficManagerDTMMode.TRAFFIC_SENDER_AND_RECEIVER);
		assertNotNull(ntm.getDtmTrafficManager());
		assertNotNull(ntm.getDtmVectorsReceiver());
	}

	@Test
	public void shouldCreateAndInitializeNTMInDefaultMode() {
		NetworkTrafficManager ntm = new NetworkTrafficManager();
		ntm.initialize();
		assertNotNull(ntm.getDtmTrafficManager());
		assertNotNull(ntm.getDtmVectorsReceiver());
	}
	
}
