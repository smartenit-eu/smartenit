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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.SimpleTunnelID;

/**
 * Includes test methods for store and get methods of {@link CounterValue}
 * class.
 * 
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public class CounterValuesTest {
	
	private SimpleLinkID linkID1;
	private SimpleLinkID linkID2;
	private SimpleTunnelID tunnelID1;
	
	@Before
	public void setup() {
		linkID1 = new SimpleLinkID("link1", "isp1");
		linkID2 = new SimpleLinkID("link2", "isp1");
		tunnelID1 = new SimpleTunnelID("tunnel1", 1);
	}
	
	@Test
	public void shouldStoreOneLinkAndTunnelEntry() {
		CounterValues values = new CounterValues();
		values.storeCounterValue(linkID1, 100);
		values.storeCounterValue(tunnelID1, 1000);
		
		assertEquals(100, (long) values.getCounterValue(new SimpleLinkID("link1", "isp1")));
		assertEquals(1000, (long) values.getCounterValue(new SimpleTunnelID("tunnel1", 1)));
		assertEquals(1, values.getAllLinkIds().size());
		assertEquals(1, values.getAllTunnelsIds().size());
	}

	@Test
	public void shouldStoreTwoEntries() {
		CounterValues values = new CounterValues();
		values.storeCounterValue(linkID1, 100);
		values.storeCounterValue(linkID2, 110);
		
		assertEquals(100, (long) values.getCounterValue(new SimpleLinkID("link1", "isp1")));
		assertEquals(110, (long) values.getCounterValue(new SimpleLinkID("link2", "isp1")));
		assertEquals(2, values.getAllLinkIds().size());
	}
	
	@Test
	public void shouldReplaceOneOfTwoEntries() {
		CounterValues values = new CounterValues();
		values.storeCounterValue(linkID1, 100);
		values.storeCounterValue(linkID1, 120);
		
		assertEquals(120, (long) values.getCounterValue(new SimpleLinkID("link1", "isp1")));
		assertEquals(1, values.getAllLinkIds().size());
	}
	
	@Test
	public void shouldValidateTwoCounterValueInstancesAsEqual() {
		CounterValues values1 = new CounterValues();
		values1.storeCounterValue(new SimpleLinkID("link1", "isp1"), 100);
		values1.storeCounterValue(new SimpleLinkID("link2", "isp1"), 110);

		CounterValues values2 = new CounterValues();
		values2.storeCounterValue(new SimpleLinkID("link2", "isp1"), 110);
		values2.storeCounterValue(new SimpleLinkID("link1", "isp1"), 100);
		
		assertTrue(values1.equals(values2));
		assertTrue(values1.hashCode() == values2.hashCode());
	}
	
}
