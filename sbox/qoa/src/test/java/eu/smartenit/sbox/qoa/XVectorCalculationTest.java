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

import org.junit.Before;
import org.junit.Test;

import eu.smartenit.sbox.db.dto.LinkID;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.Vector;

/**
 * Includes methods that test X vector calculation logic implemented by
 * {@link MonitoringDataProcessor}.
 * 
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public class XVectorCalculationTest {
	
	private CounterValues values1;
	private CounterValues values2;
	private CounterValues values3;
	
	private LinkID linkID1;
	private LinkID linkID2;
	private LinkID linkID3;
	
	@Before
	public void setup() {
		linkID1 = new SimpleLinkID("link1", "isp1");
		linkID2 = new SimpleLinkID("link2", "isp1");
		linkID3 = new SimpleLinkID("link3", "isp1");
		values1 = new CounterValues();
		values1.storeCounterValue(linkID1, 1);
		values1.storeCounterValue(linkID2, 2);
		values1.storeCounterValue(linkID3, 3);
		values2 = new CounterValues();
		values2.storeCounterValue(linkID1, 10);
		values2.storeCounterValue(linkID2, 15);
		values2.storeCounterValue(linkID3, 16);
		values3 = new CounterValues();
		values3.storeCounterValue(linkID1, 28);
		values3.storeCounterValue(linkID2, 20);
		values3.storeCounterValue(linkID3, 25);
	}
	
	@Test
	public void shouldCalculateFirstXVector() {
		MonitoringDataProcessor processor = new MonitoringDataProcessor(null, null);
		Vector result = processor.calculateXVector(1, values1);
		
		assertEquals(3, result.getVectorValues().size());
		assertEquals(1, result.getVectorValueForLink(linkID1));
		assertEquals(2, result.getVectorValueForLink(linkID2));
		assertEquals(3, result.getVectorValueForLink(linkID3));
	}
	
	@Test
	public void shouldCalculateSecondXVector() {
		MonitoringDataProcessor processor = new MonitoringDataProcessor(null, null);
		processor.calculateXVector(1, values1);
		Vector result = processor.calculateXVector(1, values2);
		
		assertEquals(3, result.getVectorValues().size());
		assertEquals(9, result.getVectorValueForLink(linkID1));
		assertEquals(13, result.getVectorValueForLink(linkID2));
		assertEquals(13, result.getVectorValueForLink(linkID3));
	}
	
	@Test
	public void shouldCalculateLaterXVector() {
		MonitoringDataProcessor processor = new MonitoringDataProcessor(null, null);
		processor.calculateXVector(1, values1);
		processor.calculateXVector(1, values2);
		Vector result = processor.calculateXVector(1, values3);
		
		assertEquals(3, result.getVectorValues().size());
		assertEquals(18, result.getVectorValueForLink(linkID1));
		assertEquals(5, result.getVectorValueForLink(linkID2));
		assertEquals(9, result.getVectorValueForLink(linkID3));
	}
	
	@Test
	public void shouldCalculateXVectorEvenIfCounterValueIsLessThanZero() {
		linkID1 = new SimpleLinkID("link1", "isp1");
		linkID2 = new SimpleLinkID("link2", "isp1");
		values1 = new CounterValues();
		values1.storeCounterValue(linkID1, 100);
		values1.storeCounterValue(linkID2, 200);
		values2 = new CounterValues();
		values2.storeCounterValue(linkID1, 110);
		values2.storeCounterValue(linkID2, 190);
		
		MonitoringDataProcessor processor = new MonitoringDataProcessor(null, null);
		processor.calculateXVector(1, values1);
		Vector result = processor.calculateXVector(1, values2);
		
		assertEquals(2, result.getVectorValues().size());
		assertEquals(10, result.getVectorValueForLink(linkID1));
		assertEquals(0, result.getVectorValueForLink(linkID2));
	}

}
