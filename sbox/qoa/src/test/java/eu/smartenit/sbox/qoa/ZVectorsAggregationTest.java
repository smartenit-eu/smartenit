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
package eu.smartenit.sbox.qoa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import eu.smartenit.sbox.db.dto.DC2DCCommunicationID;
import eu.smartenit.sbox.db.dto.LocalVectorValue;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.ZVector;

/**
 * 
 * @author Lukasz Lopatowski
 * @version 1.2
 * 
 */
public class ZVectorsAggregationTest {

	@Test
	public void shouldAggregateZVectors() {
		MonitoringDataProcessor mdp = new MonitoringDataProcessor(null, null);
		List<ZVector> zVectors = new ArrayList<ZVector>();
		
		SimpleLinkID linkID1 = new SimpleLinkID("1", "1");
		SimpleLinkID linkID2 = new SimpleLinkID("2", "1");
		
		List<LocalVectorValue> vectorValues1 = new ArrayList<LocalVectorValue>(
				Arrays.asList(new LocalVectorValue(1000, linkID1), new LocalVectorValue(1500, linkID2)));		
		List<LocalVectorValue> vectorValues2 = new ArrayList<LocalVectorValue>(
				Arrays.asList(new LocalVectorValue(1100, linkID1), new LocalVectorValue(500, linkID2)));	
		List<LocalVectorValue> vectorValues3 = new ArrayList<LocalVectorValue>(
				Arrays.asList(new LocalVectorValue(1200, linkID1), new LocalVectorValue(1500, linkID2)));	
		
		ZVector zVector = new ZVector(vectorValues1, 1);
		zVector.setCommunicationID(new DC2DCCommunicationID(1, null, 1, null, 2, null));
		zVectors.add(zVector);

		zVector = new ZVector(vectorValues2, 1);
		zVector.setCommunicationID(new DC2DCCommunicationID(2, null, 1, null, 2, null));
		zVectors.add(zVector);

		zVector = new ZVector(vectorValues3, 1);
		zVector.setCommunicationID(new DC2DCCommunicationID(3, null, 1, null, 2, null));
		zVectors.add(zVector);

		ZVector result = mdp.aggregateZVectors(zVectors);
		assertNotNull(result);
		assertEquals(2, result.getVectorValues().size());
		assertEquals(3300, result.getVectorValueForLink(new SimpleLinkID("1", "1")));
		assertEquals(3500, result.getVectorValueForLink(new SimpleLinkID("2", "1")));
	}

}
