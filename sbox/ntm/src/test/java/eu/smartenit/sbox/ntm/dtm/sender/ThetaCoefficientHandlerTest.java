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
package eu.smartenit.sbox.ntm.dtm.sender;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.db.dto.ThetaCoefficient;
import eu.smartenit.sbox.ntm.dtm.sender.ThetaCoefficientHandler;

/**
 * Includes test methods for the simplified logic implemented in
 * {@link ThetaCoefficientHandler}. This class and its tests will be enhanced in
 * future releases.
 * 
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public class ThetaCoefficientHandlerTest {

	@Test
	public void shouldReturnTheSameCVectorIfThetaNull() {
		ThetaCoefficientHandler tch = new ThetaCoefficientHandler();
		CVector cVector = new CVector(null, 1);
		RVector rVector = new RVector(null, 1, null);
		CVector normalizedCVector = tch.normalizeCVector(cVector, rVector);
		
		assertEquals(cVector, normalizedCVector);
	}

	// tests simplified behavior implemented for prototype v1
	@Test
	public void shouldReturnTheSameCVectorIfThetaNotNull() {
		ThetaCoefficientHandler tch = new ThetaCoefficientHandler();
		CVector cVector = new CVector(null, 1);
		RVector rVector = new RVector(null, 1, new ArrayList<ThetaCoefficient>());
		CVector normalizedCVector = tch.normalizeCVector(cVector, rVector);
		
		assertEquals(cVector, normalizedCVector);
	}
	
}
