/**
 * Copyright (C) 2015 The SmartenIT consortium (http://www.smartenit.eu)
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import eu.smartenit.sbox.db.dto.LocalVectorValue;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.db.dto.ZVector;

/**
 * Includes tests methods for handling 95th percentile samples implemented by
 * the {@link The95PercentileSamplesContainer}.
 */
public class The95thPercentileSamplesContainerTest {
	
	private SimpleLinkID link1;
	private SimpleLinkID link2;
	
	private XVector xVector1, xVector2, xVector3, xVector4; 
	private ZVector zVector1, zVector2, zVector3, zVector4;
	
	@Before
	public void setup() {
		link1 = new SimpleLinkID("link1", "isp1");
		link2 = new SimpleLinkID("link2", "isp1");
		
		xVector1 = new XVector(Arrays.asList(new LocalVectorValue(1000, link1), new LocalVectorValue(1200, link2)), 1);
		xVector2 = new XVector(Arrays.asList(new LocalVectorValue(1100, link1), new LocalVectorValue(1100, link2)), 1);
		xVector3 = new XVector(Arrays.asList(new LocalVectorValue(1200, link1), new LocalVectorValue(1000, link2)), 1);
		xVector4 = new XVector(Arrays.asList(new LocalVectorValue(1300, link1), new LocalVectorValue(900, link2)), 1);
		
		zVector1 = new ZVector(Arrays.asList(new LocalVectorValue(600, link1), new LocalVectorValue(10, link2)), 1);
		zVector2 = new ZVector(Arrays.asList(new LocalVectorValue(700, link1), new LocalVectorValue(9, link2)), 1);
		zVector3 = new ZVector(Arrays.asList(new LocalVectorValue(650, link1), new LocalVectorValue(11, link2)), 1);
		zVector4 = new ZVector(Arrays.asList(new LocalVectorValue(900, link1), new LocalVectorValue(8, link2)), 1);
	}
	
	@Test
	public void shouldStoreXAndZVectorTrafficSamples() {
		The95PercentileSamplesContainer container = new The95PercentileSamplesContainer(link1, link2);
		container.storeTrafficValues(xVector1, Arrays.asList(zVector1));
		container.storeTrafficValues(xVector2, Arrays.asList(zVector2));
		container.storeTrafficValues(xVector3, Arrays.asList(zVector3));
		container.storeTrafficValues(xVector4, Arrays.asList(zVector4));
		
		long[] xResult = container.getTrafficValuesForLinks();
		long[] zResult = container.getTrafficValuesForTunnels();
		
		assertEquals(1200, xResult[0]);
		assertEquals(1100, xResult[1]);
		assertEquals(700, zResult[0]);
		assertEquals(10, zResult[1]);
	}

	@Test
	public void shouldStoreManyXAndZVectorTrafficSamples() {
		The95PercentileSamplesContainer container = new The95PercentileSamplesContainer(link1, link2);
		
		for (int i = 0; i < 30; i++)
			container.storeTrafficValues(xVector1, Arrays.asList(zVector1));
		
		container.storeTrafficValues(xVector2, Arrays.asList(zVector2));
		container.storeTrafficValues(xVector3, Arrays.asList(zVector3));
		container.storeTrafficValues(xVector4, Arrays.asList(zVector4));
		
		long[] xResult = container.getTrafficValuesForLinks();
		long[] zResult = container.getTrafficValuesForTunnels();
		
		assertEquals(1100, xResult[0]);
		assertEquals(1200, xResult[1]);
		assertEquals(650, zResult[0]);
		assertEquals(10, zResult[1]);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowExceptionOnInvalidInput() {
		The95PercentileSamplesContainer container = new The95PercentileSamplesContainer(link1, link2);
		container.storeTrafficValues(null, Arrays.asList(zVector2));
	}
	
	@Test
	public void shouldCalculate95thPercentileSampleIndex() {
		int sampleCounter = 100;
		assertTrue(94 == ((int)(sampleCounter*0.95)) - 1);
		sampleCounter = 1000;
		assertTrue(949 == ((int)(sampleCounter*0.95)) - 1);
		sampleCounter = 99;
		assertTrue(93 == ((int)(sampleCounter*0.95)) - 1);
	}
	
}
