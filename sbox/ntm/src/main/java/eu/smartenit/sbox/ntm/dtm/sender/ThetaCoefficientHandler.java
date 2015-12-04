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
package eu.smartenit.sbox.ntm.dtm.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.RVector;

/**
 * Implements logic for manipulating compensation vector values based on theta
 * coefficients.
 * 
 * This class will be enhances in future releases.
 * 
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public class ThetaCoefficientHandler {

	private static final Logger logger = LoggerFactory.getLogger(ThetaCoefficientHandler.class);

	/**
	 * Method normalizes compensation vector values based on theta coefficients
	 * provided as part of reference vector for given AS.
	 * 
	 * In this implementation it is assumed that a single SDN Controller manages
	 * all DA routers (hence, all clouds) in given AS. This implies that
	 * coefficients for all the communications originating in any cloud of given
	 * AS are added together. Received C Vector values are multiplied by
	 * accumulated coefficient value.
	 * 
	 * @param cVector
	 *            compensation vector object which values will be normalized
	 * @param rVector
	 *            reference vector carrying information on theta coefficients
	 * @return compensation vector object with updated values
	 */
	public CVector normalizeCVector(CVector cVector, RVector rVector) {
		
		if (rVector.getThetaCollection() == null) {
			logger.debug("Theta collection in received reference vector is not set.");
			logger.debug("Compensation vector normalization will not be performed.");
			return cVector;
		}
		
		// TODO Implement this logic in future software release
		return cVector;
	}

}
