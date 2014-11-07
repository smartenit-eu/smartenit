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
package eu.smartenit.sbox.ntm.dtm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.VectorValue;
import eu.smartenit.sbox.db.dto.XVector;

/**
 * Includes test methods for store and load methods of {@link VectorsContainer}
 * class.
 * 
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public class VectorsContainerTest {
	
	private int asNumber1;
	private int asNumber2;

	private XVector xVector1;
	private XVector xVector2;
	private RVector rVector1;
	private RVector rVector2;
	
	@Before
	public void setup() {
		asNumber1 = 1;
		asNumber2 = 2;
		xVector1 = new XVector(null, asNumber1);
		xVector2 = new XVector(null, asNumber2);
		rVector1 = new RVector(null, asNumber1, null);
		rVector2 = new RVector(null, asNumber2, null);
	}
	
	@Test
	public void shouldCreateNewPairOnEmptyContainerWithUpdatedXVector() {
		VectorsContainer container = new VectorsContainer();
		XRVectorPair resultPair = container.accumulateAndStoreUpdatedXVectorValues(xVector1);
		assertNotNull(resultPair);
		assertNotNull(container.loadCurrentVectorPair(asNumber1));
		assertNotNull(container.loadCurrentVectorPair(asNumber1).getXVector());
	}
	
	@Test
	public void shouldCreateNewPairWithUpdatedXVector() {
		VectorsContainer container = new VectorsContainer();
		container.accumulateAndStoreUpdatedXVectorValues(xVector1);
		XRVectorPair resultPair = container.accumulateAndStoreUpdatedXVectorValues(xVector2);
		
		assertNotNull(resultPair);
		assertNotNull(container.loadCurrentVectorPair(asNumber1));
		assertNotNull(container.loadCurrentVectorPair(asNumber2));
		assertEquals(2, container.getListOfAsNumbers().size());
	}

	@Test
	public void shouldCreateNewPairOnEmptyContainerWithUpdatedRVector() {
		VectorsContainer container = new VectorsContainer();
		XRVectorPair resultPair = container.storeUpdatedRVector(rVector1);
		assertNotNull(resultPair);
		assertNotNull(container.loadCurrentVectorPair(asNumber1));
	}
	
	@Test
	public void shouldCreateNewPairWithUpdatedRVector() {
		VectorsContainer container = new VectorsContainer();
		container.storeUpdatedRVector(rVector1);
		XRVectorPair resultPair = container.storeUpdatedRVector(rVector2);
		
		assertNotNull(resultPair);
		assertNotNull(container.loadCurrentVectorPair(asNumber1));
		assertNotNull(container.loadCurrentVectorPair(asNumber2));
		assertEquals(2, container.getListOfAsNumbers().size());
	}

	@Test
	public void shouldAccumulateAndUpdateExistingPairWithUpdatedXVector() {
		VectorsContainer container = new VectorsContainer();
		container.storeUpdatedRVector(rVector1);
		
		VectorValue value1 = new VectorValue(12, new SimpleLinkID("id1", "isp1"));
		VectorValue value2 = new VectorValue(18, new SimpleLinkID("id2", "isp1"));
		XVector newXVector1 = new XVector(Arrays.asList(value1, value2) , asNumber1);
		container.accumulateAndStoreUpdatedXVectorValues(newXVector1);
		
		assertEquals(1, container.getListOfAsNumbers().size());
		assertEquals(12, container.loadCurrentVectorPair(asNumber1).getXVector().getVectorValueForLink(new SimpleLinkID("id1", "isp1")));
		assertEquals(18, container.loadCurrentVectorPair(asNumber1).getXVector().getVectorValueForLink(new SimpleLinkID("id2", "isp1")));
	
		VectorValue value3 = new VectorValue(30, new SimpleLinkID("id1", "isp1"));
		VectorValue value4 = new VectorValue(40, new SimpleLinkID("id2", "isp1"));
		XVector newXVector2 = new XVector(Arrays.asList(value3, value4) , asNumber1);
		container.accumulateAndStoreUpdatedXVectorValues(newXVector2);
		
		assertEquals(1, container.getListOfAsNumbers().size());
		assertEquals(42, container.loadCurrentVectorPair(asNumber1).getXVector().getVectorValueForLink(new SimpleLinkID("id1", "isp1")));
		assertEquals(58, container.loadCurrentVectorPair(asNumber1).getXVector().getVectorValueForLink(new SimpleLinkID("id2", "isp1")));	
	}
	
	@Test
	public void shouldResetXVectorValuesInExistingPair() {
		VectorsContainer container = new VectorsContainer();
		container.storeUpdatedRVector(rVector1);
		
		VectorValue value1 = new VectorValue(12, new SimpleLinkID("id1", "isp1"));
		VectorValue value2 = new VectorValue(18, new SimpleLinkID("id2", "isp1"));
		XVector newXVector1 = new XVector(Arrays.asList(value1, value2) , asNumber1);
		container.accumulateAndStoreUpdatedXVectorValues(newXVector1);
		assertEquals(12, container.loadCurrentVectorPair(asNumber1).getXVector().getVectorValueForLink(new SimpleLinkID("id1", "isp1")));
		assertEquals(18, container.loadCurrentVectorPair(asNumber1).getXVector().getVectorValueForLink(new SimpleLinkID("id2", "isp1")));
		
		container.resetXVectorValues(asNumber2);
		assertEquals(12, container.loadCurrentVectorPair(asNumber1).getXVector().getVectorValueForLink(new SimpleLinkID("id1", "isp1")));
		assertEquals(18, container.loadCurrentVectorPair(asNumber1).getXVector().getVectorValueForLink(new SimpleLinkID("id2", "isp1")));
		
		container.resetXVectorValues(asNumber1);
		assertEquals(0, container.loadCurrentVectorPair(asNumber1).getXVector().getVectorValueForLink(new SimpleLinkID("id1", "isp1")));
		assertEquals(0, container.loadCurrentVectorPair(asNumber1).getXVector().getVectorValueForLink(new SimpleLinkID("id2", "isp1")));
	}
	
	@Test
	public void shouldUpdateExistingPairWithUpdatedRVector() {
		VectorsContainer container = new VectorsContainer();
		container.storeUpdatedRVector(rVector2);
		
		RVector newRVector = new RVector(null, asNumber2, null);
		container.storeUpdatedRVector(newRVector);
		assertEquals(1, container.getListOfAsNumbers().size());
	}
	
	@Test
	public void shouldReturnNullIfAsNumberIsNotSet() {
		VectorsContainer container = new VectorsContainer();
		assertNull(container.storeUpdatedRVector(new RVector(null, -1, null)));
		assertNull(container.accumulateAndStoreUpdatedXVectorValues(new XVector(null, -1)));
		assertNull(container.storeUpdatedRVector(new RVector(null, 0, null)));
		assertNull(container.accumulateAndStoreUpdatedXVectorValues(new XVector(null, 0)));
	}

}
