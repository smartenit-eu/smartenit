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
package eu.smartenit.sbox.eca;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.LocalVectorValue;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.db.dto.ZVector;

/**
 * Tests handling of 95th percentile samples by the
 * {@link The95PercentileSamplesContainer} with the focus on samples with values
 * less than corresponding reference vector component value.
 * 
 * @author Lukasz Lopatowski
 * @version 3.1
 * 
 */
public class HandlingSamplesBelowRVectorTest {

	private SimpleLinkID link1;
	private SimpleLinkID link2;
	
	private XVector xVectorLowLow, xVectorLowHigh, xVectorHighLow, xVectorHighHigh; 
	private ZVector zVector;
	
	private LocalRVector rVector;
	
	@Before
	public void setup() {
		link1 = new SimpleLinkID("link1", "isp1");
		link2 = new SimpleLinkID("link2", "isp1");
		
		rVector = new LocalRVector(Arrays.asList(new LocalVectorValue(1250, link1), new LocalVectorValue(1250, link2)), 1);
		
		xVectorLowLow = new XVector(Arrays.asList(new LocalVectorValue(1000, link1), new LocalVectorValue(1000, link2)), 1);
		xVectorLowHigh = new XVector(Arrays.asList(new LocalVectorValue(1000, link1), new LocalVectorValue(1300, link2)), 1);
		xVectorHighLow = new XVector(Arrays.asList(new LocalVectorValue(1300, link1), new LocalVectorValue(1000, link2)), 1);
		xVectorHighHigh = new XVector(Arrays.asList(new LocalVectorValue(1300, link1), new LocalVectorValue(1300, link2)), 1);
		
		zVector = new ZVector(Arrays.asList(new LocalVectorValue(600, link1), new LocalVectorValue(10, link2)), 1);
	}
	
	@Test
	public void shouldStoreSamplesAndHandleSamplesBelowRVector() {
		The95PercentileSamplesContainer container = new The95PercentileSamplesContainer(link1, link2, 100);
		container.setCurrentRVector(rVector);
		for (int i=0; i < 94; i++)
			container.storeTrafficValues(xVectorLowLow, Arrays.asList(zVector));
		assertFalse(container.isChangeInLinksWithRVectorAchieved());
		container.storeTrafficValues(xVectorLowLow, Arrays.asList(zVector));
		// reference vector achieved on both links
		assertTrue(container.isChangeInLinksWithRVectorAchieved());
		assertTrue(container.getLinksWithRVectorAchieved().size() == 2);
		// no change in the subsequent query
		assertFalse(container.isChangeInLinksWithRVectorAchieved());
		for (int i=0; i < 5; i++)
			container.storeTrafficValues(xVectorHighHigh, Arrays.asList(zVector));
		
		container.resetTrafficValues();
		// should reset the list at the beginning of new accounting period
		assertTrue(container.getLinksWithRVectorAchieved().size() == 0);
		assertFalse(container.isChangeInLinksWithRVectorAchieved());
		
		// should have stored on entry in history
		assertTrue(container.getHistory().getEntries().size() == 1);
		
		container.setCurrentRVector(rVector);
		for (int i=0; i < 94; i++)
			container.storeTrafficValues(xVectorLowLow, Arrays.asList(zVector));
		assertFalse(container.isChangeInLinksWithRVectorAchieved());
		container.storeTrafficValues(xVectorLowHigh, Arrays.asList(zVector));
		// reference vector achieved on first link
		assertTrue(container.isChangeInLinksWithRVectorAchieved());
		assertTrue(container.getLinksWithRVectorAchieved().size() == 1);
		assertEquals(link1.getLocalLinkID(), ((SimpleLinkID)container.getLinksWithRVectorAchieved().get(0)).getLocalLinkID());
		assertFalse(container.isChangeInLinksWithRVectorAchieved());
		container.storeTrafficValues(xVectorHighLow, Arrays.asList(zVector));
		// reference vector achieved on second link
		assertTrue(container.isChangeInLinksWithRVectorAchieved());
		assertTrue(container.getLinksWithRVectorAchieved().size() == 2);
		
		container.resetTrafficValues();
		// should reset the list at the beginning of new accounting period
		assertTrue(container.getLinksWithRVectorAchieved().size() == 0);
		assertFalse(container.isChangeInLinksWithRVectorAchieved());
		// should have stored another entry in history
		assertTrue(container.getHistory().getEntries().size() == 2);
	}
	
	@Test
	public void shouldStoreAndHandleDifferentNumberOfSamples() {
		The95PercentileSamplesContainer container = new The95PercentileSamplesContainer(link1, link2, 4);
		container.setCurrentRVector(rVector);
		for (int i=0; i < 3; i++)
			container.storeTrafficValues(xVectorLowLow, Arrays.asList(zVector));
		assertFalse(container.isChangeInLinksWithRVectorAchieved());
		container.storeTrafficValues(xVectorLowLow, Arrays.asList(zVector));
		assertTrue(container.isChangeInLinksWithRVectorAchieved());
		assertTrue(container.getLinksWithRVectorAchieved().size() == 2);
	}

}
