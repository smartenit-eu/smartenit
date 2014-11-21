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
package eu.smartenit.sbox.ntm.dtm.sender;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import eu.smartenit.sbox.db.dao.DC2DCCommunicationDAO;
import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.interfaces.sboxsdn.SboxSdnClient;
import eu.smartenit.sbox.ntm.dtm.DAOFactory;

/**
 * Includes test methods for store and load methods of
 * {@link SDNControllerConfigBuilder} class.
 * 
 * @author Lukasz Lopatowski
 * @version 1.2
 * 
 */
public class SDNControllerContainerTest {

	private static final String TUNNEL_END_PREFIX = "1.1.1.1";
	
	private CVector cVector = new CVector();
	private RVector rVector = new RVector();
	
	private SboxSdnClient sdnClient = mock(SboxSdnClient.class);
	private DC2DCCommunicationDAO dao = mock(DC2DCCommunicationDAO.class);
	
	@Before
	public void setup() {
		cVector.setSourceAsNumber(1);
		cVector.addVectorValueForTunnelEndPrefix(new NetworkAddressIPv4(TUNNEL_END_PREFIX, 24), 500L);
		rVector.setSourceAsNumber(1);
    	rVector.addVectorValueForTunnelEndPrefix(new NetworkAddressIPv4(TUNNEL_END_PREFIX, 24), 1000L);
    	
    	SDNClientFactory.disableUniqueClientCreationMode();
    	SDNClientFactory.setClientInstance(sdnClient);
    	
    	when(dao.findAllDC2DCCommunicationsCloudsTunnels()).thenReturn(DBStructuresBuilder.communicationsOnTrafficSender);
    	DAOFactory.setDC2DCCommunicationDAO(dao);
	}
	
	@Test
	public void shouldPopulateContainerStructures() {
		SDNControllerContainer container = new SDNControllerContainer();
		container.populateControllersFromDB();
		
		assertEquals(2, container.getAllControllers().size());
	}
	
}
