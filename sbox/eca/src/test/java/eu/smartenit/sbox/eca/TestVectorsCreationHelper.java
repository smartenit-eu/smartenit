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

import java.util.ArrayList;
import java.util.List;

import eu.smartenit.sbox.db.dto.LocalVectorValue;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.VectorValue;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.db.dto.ZVector;

public class TestVectorsCreationHelper {

	/**
	 * Creates an {@link XVector} object from traffic volumes and link ids.
	 * 
	 * @param x1 Traffic volume on link 1
	 * @param x2 Traffic volume on link 2
	 * @param link1 {@link SimpleLinkID} of link 1.
	 * @param link2 {@link SimpleLinkID} of link 2.
	 * @param sourceAsNumber The number of the source AS
	 * @return The {@link XVector} object.
	 */
	public static XVector createXVector(long x1, long x2, SimpleLinkID link1, SimpleLinkID link2, int sourceAsNumber) {
		List<LocalVectorValue> vectorValues = createVectorValues(x1, x2, link1, link2);
		return new XVector(vectorValues, sourceAsNumber);
	}
	
	/**
	 * Creates a {@link List} {@link ZVector} objects from traffic volumes and link ids
	 * 
	 * @param z1 Traffic volume on link 1
	 * @param z2 Traffic volume on link 2
	 * @param link1 {@link SimpleLinkID} of link 1.
	 * @param link2 {@link SimpleLinkID} of link 2.
	 * @param sourceAsNumber The number of the source AS
	 * @return The {@link List} containing the {@link ZVector} objects.
	 */
	public static List<ZVector> createZVectorList(long z1, long z2, SimpleLinkID link1, SimpleLinkID link2, int sourceAsNumber) {
		List<ZVector> zVectorList = new ArrayList<ZVector>();
		List<LocalVectorValue> vectorValues = createVectorValues(z1, z2, link1, link2);
		zVectorList.add(new ZVector(vectorValues, sourceAsNumber));
		return zVectorList;
	}
	
	/**
	 * Creates a {@link List} of {@link VectorValue}s from traffic volumes and link ids
	 * 
	 * @param p Traffic volume on link 1
	 * @param q Traffic volume on link 2
	 * @param link1 {@link SimpleLinkID} of link 1.
	 * @param link2 {@link SimpleLinkID} of link 1.
	 * @return A {@link List} of {@link VectorValue}s
	 */
	public static List<LocalVectorValue> createVectorValues(long p, long q, SimpleLinkID link1, SimpleLinkID link2) {
		List<LocalVectorValue> values = new ArrayList<LocalVectorValue>();
		values.add(new LocalVectorValue(p, link1));
		values.add(new LocalVectorValue(q, link2));
		return values;
	}
	
}
