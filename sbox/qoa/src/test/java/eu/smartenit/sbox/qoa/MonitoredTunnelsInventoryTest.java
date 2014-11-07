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
package eu.smartenit.sbox.qoa;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.DC2DCCommunicationID;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;

/**
 * Includes test methods for population and data retrieval methods of
 * {@link MonitoredTunnelsInventory} class.
 * 
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public class MonitoredTunnelsInventoryTest {
	
	@Test
	public void shouldPopulateInventory() {
		MonitoredTunnelsInventory inventory = new MonitoredTunnelsInventory();
		inventory.populate(DBStructuresBuilder.communications);
		
		assertEquals(2, inventory.getDARoutersByAsNumber(1).size());
		assertEquals(1, inventory.getDARoutersByAsNumber(2).size());
		assertEquals(2, inventory.getTunnels(new DARouter(new NetworkAddressIPv4("1.1.1.1", 32), "")).size());
		assertEquals(7, inventory.getTunnels(1).size());
		assertEquals(1, inventory.getTunnels(2).size());
		assertEquals(2, inventory.getAllAsNumbers().size());
		assertEquals(2, inventory.getTunnels(new DC2DCCommunicationID(1, "id1", 1, "cloudLocal11", 200, "remoteCloud200")).size());
		assertEquals(5, inventory.getTunnels(new DC2DCCommunicationID(2, "id2", 1, "cloudLocal12", 300, "remoteCloud300")).size());
	}

}
