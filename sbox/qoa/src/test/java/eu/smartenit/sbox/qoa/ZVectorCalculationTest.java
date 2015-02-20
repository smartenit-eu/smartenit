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
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.smartenit.sbox.db.dto.DC2DCCommunicationID;
import eu.smartenit.sbox.db.dto.EndAddressPairTunnelID;
import eu.smartenit.sbox.db.dto.LinkID;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.ZVector;

/**
 * Includes methods that test Z vectors calculation logic implemented by
 * {@link MonitoringDataProcessor}.
 * 
 * @author Lukasz Lopatowski
 * @version 1.2
 * 
 */
public class ZVectorCalculationTest {

	private MonitoredTunnelsInventory monitoredTunnels;
	private CounterValues values, values2, values3;

	private LinkID link111ID, link112ID, link121ID, link122ID, link211ID;
	private EndAddressPairTunnelID tunnel1111ID, tunnel1121ID, tunnel1211ID, tunnel1212ID, tunnel1221ID, tunnel1222ID, tunnel1223ID, tunnel2111ID;
	private DC2DCCommunicationID com1ID, com2ID, com3ID;
	
	@Before
	public void setup() {
		monitoredTunnels = new MonitoredTunnelsInventory();
		monitoredTunnels.populate(DBStructuresBuilder.communications);
		
		link111ID = new SimpleLinkID("111", "isp1");
		link112ID = new SimpleLinkID("112", "isp1");
		link121ID = new SimpleLinkID("121", "isp1"); 
		link122ID = new SimpleLinkID("122", "isp1");
		link211ID = new SimpleLinkID("211", "isp1");
		
		tunnel1111ID = new EndAddressPairTunnelID("tunnel1111", new NetworkAddressIPv4("10.1.1.1", 32), new NetworkAddressIPv4("10.1.1.2", 32));
		tunnel1121ID = new EndAddressPairTunnelID("tunnel1121", new NetworkAddressIPv4("10.1.2.1", 32),	new NetworkAddressIPv4("10.1.2.2", 32));
		tunnel1211ID = new EndAddressPairTunnelID("tunnel1211", new NetworkAddressIPv4("10.2.1.1", 32), new NetworkAddressIPv4("10.2.1.2", 32));
		tunnel1212ID = new EndAddressPairTunnelID("tunnel1212", new NetworkAddressIPv4("10.2.1.2", 32),	new NetworkAddressIPv4("10.2.1.3", 32));
		tunnel1221ID = new EndAddressPairTunnelID("tunnel1221", new NetworkAddressIPv4("10.2.2.1", 32),	new NetworkAddressIPv4("10.2.2.2", 32));
		tunnel1222ID = new EndAddressPairTunnelID("tunnel1222",	new NetworkAddressIPv4("10.2.2.2", 32),	new NetworkAddressIPv4("10.2.2.3", 32));
		tunnel1223ID = new EndAddressPairTunnelID("tunnel1223", new NetworkAddressIPv4("10.2.2.4", 32),	new NetworkAddressIPv4("10.2.2.5", 32));
		tunnel2111ID = new EndAddressPairTunnelID("tunnel2111", new NetworkAddressIPv4("10.1.1.4", 32),	new NetworkAddressIPv4("10.1.1.5", 32));
		
		values = new CounterValues();
		values.storeCounterValue(tunnel1111ID, 120);
		values.storeCounterValue(tunnel1121ID, 200);
		values.storeCounterValue(tunnel1211ID, 145);
		values.storeCounterValue(tunnel1212ID, 325);
		values.storeCounterValue(tunnel1221ID, 210);
		values.storeCounterValue(tunnel1222ID, 50);
		values.storeCounterValue(tunnel1223ID, 80);
		
		values2 = new CounterValues();
		values2.storeCounterValue(tunnel1111ID, 120);
		values2.storeCounterValue(tunnel1121ID, 220);
		values2.storeCounterValue(tunnel1211ID, 345);
		values2.storeCounterValue(tunnel1212ID, 543);
		values2.storeCounterValue(tunnel1221ID, 254);
		values2.storeCounterValue(tunnel1222ID, 376);
		values2.storeCounterValue(tunnel1223ID, 2352);
		
		values3 = new CounterValues();
		values3.storeCounterValue(tunnel2111ID, 120);
		
		com1ID = new DC2DCCommunicationID(1, "id1", 1, "cloudLocal11", 200, "remoteCloud200");
		com2ID = new DC2DCCommunicationID(2, "id2", 1, "cloudLocal12", 300, "remoteCloud300");
		com3ID = new DC2DCCommunicationID(3, "id3", 2, "cloudLocal21", 400, "remoteCloud400");
	}
	
	@Test
	public void shouldCalculateFirstZVectors() {
		MonitoringDataProcessor processor = new MonitoringDataProcessor(null, monitoredTunnels);
		List<ZVector> result = processor.calculateZVectors(1, null, values);
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(1, result.get(0).getSourceAsNumber());
		ZVector zVector = getZVectorForDC2DCCommunicationID(result, com1ID);
		assertEquals(120, zVector.getVectorValueForLink(link111ID));
		assertEquals(200, zVector.getVectorValueForLink(link112ID));
		zVector = getZVectorForDC2DCCommunicationID(result, com2ID);
		assertEquals(470, zVector.getVectorValueForLink(link121ID));
		assertEquals(340, zVector.getVectorValueForLink(link122ID));
		
		result = processor.calculateZVectors(2, null, values3);
		assertNotNull(result);
		assertEquals(1, result.size());
		zVector = getZVectorForDC2DCCommunicationID(result, com3ID);
		assertEquals(120, zVector.getVectorValueForLink(link211ID));
	}
	
	@Test
	public void shouldCalculateSecondRoundOfZVectors() {
		MonitoringDataProcessor processor = new MonitoringDataProcessor(null, monitoredTunnels);
		processor.calculateZVectors(1, null, values);
		
		List<ZVector> result = processor.calculateZVectors(1, null, values2);
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(1, result.get(0).getSourceAsNumber());
		ZVector zVector = getZVectorForDC2DCCommunicationID(result, com1ID);
		assertEquals(0, zVector.getVectorValueForLink(link111ID));
		assertEquals(20, zVector.getVectorValueForLink(link112ID));
		zVector = getZVectorForDC2DCCommunicationID(result, com2ID);
		assertEquals(418, zVector.getVectorValueForLink(link121ID));
		assertEquals(2642, zVector.getVectorValueForLink(link122ID));
	}
	
	@Test
	public void shouldCalculateZVectorsEvenIfCounterValueIsLessThanZero() {
		values = new CounterValues();
		values.storeCounterValue(tunnel1111ID, 120);
		values.storeCounterValue(tunnel1121ID, 200);
		values.storeCounterValue(tunnel1211ID, 145);
		values.storeCounterValue(tunnel1212ID, 325);
		values.storeCounterValue(tunnel1221ID, 210);
		values.storeCounterValue(tunnel1222ID, 50);
		values.storeCounterValue(tunnel1223ID, 80);
		
		values2 = new CounterValues();
		values2.storeCounterValue(tunnel1111ID, 120);
		values2.storeCounterValue(tunnel1121ID, 220);
		values2.storeCounterValue(tunnel1211ID, 345);
		values2.storeCounterValue(tunnel1212ID, 300);
		values2.storeCounterValue(tunnel1221ID, 254);
		values2.storeCounterValue(tunnel1222ID, 376);
		values2.storeCounterValue(tunnel1223ID, 2352);
		
		MonitoringDataProcessor processor = new MonitoringDataProcessor(null, monitoredTunnels);
		processor.calculateZVectors(1, null, values);
		List<ZVector> result = processor.calculateZVectors(1, null, values2);
		
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(1, result.get(0).getSourceAsNumber());
		ZVector zVector = getZVectorForDC2DCCommunicationID(result, com1ID);
		assertEquals(0, zVector.getVectorValueForLink(link111ID));
		zVector = getZVectorForDC2DCCommunicationID(result, com2ID);
		assertEquals(200, zVector.getVectorValueForLink(link121ID));
	}
	
	@Test
	public void shouldCalculateZVectorsWithLastZVectors() {
		MonitoringDataProcessor processor = new MonitoringDataProcessor(null, monitoredTunnels);
		List<ZVector> result = processor.calculateZVectors(1, values, values2);
		
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(1, result.get(0).getSourceAsNumber());
		ZVector zVector = getZVectorForDC2DCCommunicationID(result, com1ID);
		assertEquals(0, zVector.getVectorValueForLink(link111ID));
		assertEquals(20, zVector.getVectorValueForLink(link112ID));
		zVector = getZVectorForDC2DCCommunicationID(result, com2ID);
		assertEquals(418, zVector.getVectorValueForLink(link121ID));
		assertEquals(2642, zVector.getVectorValueForLink(link122ID));
	}

	private ZVector getZVectorForDC2DCCommunicationID(List<ZVector> zVectors, DC2DCCommunicationID comID) {
		for(ZVector zVector : zVectors) {
			if (zVector.getCommunicationID().equals(comID))
				return zVector;
		}
		return null;
	}
	
}
