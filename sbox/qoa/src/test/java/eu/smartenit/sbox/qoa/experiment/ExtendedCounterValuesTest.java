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
package eu.smartenit.sbox.qoa.experiment;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import eu.smartenit.sbox.db.dto.EndAddressPairTunnelID;
import eu.smartenit.sbox.db.dto.SimpleLinkID;

/**
 * Includes test methods for store and get methods of {@link ExtendedCounterValues}
 * class.
 * 
 * @author Lukasz Lopatowski
 * @version 1.2
 * 
 */
public class ExtendedCounterValuesTest {

	private SimpleLinkID linkID1;
	private EndAddressPairTunnelID tunnelID1;
	
	@Before
	public void setup() {
		linkID1 = new SimpleLinkID("link1", "isp1");
		tunnelID1 = new EndAddressPairTunnelID("tunnel1", null, null);
	}
	
	@Test
	public void shouldStoreOneLinkAndTunnelEntry() {
		ExtendedCounterValues values = new ExtendedCounterValues();
		values.storeCounterValue(linkID1, 100);
		values.storeCounterValue(tunnelID1, 1000);
		values.storeReceivedPackets(linkID1, new ReceivedPackets(50L, 100L, 150L));
		values.storeReceivedPackets(tunnelID1, new ReceivedPackets(5L, 10L, 15L));
		
		assertEquals(100, (long) values.getCounterValue(new SimpleLinkID("link1", "isp1")));
		assertEquals(1000, (long) values.getCounterValue(new EndAddressPairTunnelID("tunnel1", null, null)));
		assertEquals(150L, (long) values.getReceivedPackets(new SimpleLinkID("link1", "isp1")).getBroadcast());
		assertEquals(10L, (long) values.getReceivedPackets(new EndAddressPairTunnelID("tunnel1", null, null)).getMulticast());
		assertEquals(1, values.getAllLinkIds().size());
		assertEquals(1, values.getAllTunnelsIds().size());
	}

	@Test
	public void shouldReplaceOneOfTwoEntries() {
		ExtendedCounterValues values = new ExtendedCounterValues();
		values.storeReceivedPackets(linkID1, new ReceivedPackets(10L, 20L, 30L));
		values.storeReceivedPackets(linkID1, new ReceivedPackets(12L, 20L, 30L));
		
		assertEquals(12L, (long) values.getReceivedPackets(new SimpleLinkID("link1", "isp1")).getUnicast());
	}
	
	@Test
	public void shouldCalculateDifference() {
		ExtendedCounterValues values = new ExtendedCounterValues();
		values.storeCounterValue(linkID1, 100);
		values.storeCounterValue(tunnelID1, 1000);
		values.storeReceivedPackets(linkID1, new ReceivedPackets(50L, 100L, 150L));
		values.storeReceivedPackets(tunnelID1, new ReceivedPackets(5L, 10L, 15L));
		
		ExtendedCounterValues toBeSubtracted = new ExtendedCounterValues();
		toBeSubtracted.storeCounterValue(linkID1, 90);
		toBeSubtracted.storeCounterValue(tunnelID1, 800);
		toBeSubtracted.storeReceivedPackets(linkID1, new ReceivedPackets(46L, 93L, 142L));
		toBeSubtracted.storeReceivedPackets(tunnelID1, new ReceivedPackets(2L, 7L, 13L));
		
		ExtendedCounterValues result = values.calculateDifference(toBeSubtracted);
		assertEquals(10L, (long) result.getCounterValue(new SimpleLinkID("link1", "isp1")));
		assertEquals(200L, (long) result.getCounterValue(new EndAddressPairTunnelID("tunnel1", null, null)));
		
		assertEquals(4L, (long) result.getReceivedPackets(new SimpleLinkID("link1", "isp1")).getUnicast());
		assertEquals(7L, (long) result.getReceivedPackets(new SimpleLinkID("link1", "isp1")).getMulticast());
		assertEquals(8L, (long) result.getReceivedPackets(new SimpleLinkID("link1", "isp1")).getBroadcast());
		assertEquals(3L, (long) result.getReceivedPackets(new EndAddressPairTunnelID("tunnel1", null, null)).getUnicast());
		assertEquals(3L, (long) result.getReceivedPackets(new EndAddressPairTunnelID("tunnel1", null, null)).getMulticast());
		assertEquals(2L, (long) result.getReceivedPackets(new EndAddressPairTunnelID("tunnel1", null, null)).getBroadcast());
	}
	
}
