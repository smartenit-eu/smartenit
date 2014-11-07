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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.LinkID;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.db.dto.Vector;
import eu.smartenit.sbox.db.dto.VectorValue;
import eu.smartenit.sbox.db.dto.XVector;

/**
 * Implements compensation vector calculation logic.
 * 
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public class CVectorCalculator {

	private static final Logger logger = LoggerFactory.getLogger(CVectorCalculator.class);
	
	/**
	 * Calculates compensation vector based on provided arguments: link traffic
	 * vector and reference vector.
	 * 
	 * @param xVector
	 *            link traffic vector to be used during calculation
	 * @param rVector
	 *            reference vector to be used during calculation
	 * @return calculated compensation vector
	 */
	public CVector calculateCompensationVector(XVector xVector, RVector rVector) {
		if (isInvalidInput(xVector, rVector)) {
			logger.warn("CVector calculation: Invalid input. Will return null.");
			return null;
		}
		logger.debug("Input arguments validated successfuly");
		
		logger.debug("Calculating C vector for AS {}", xVector.getSourceAsNumber());
		CVector cVector = new CVector();
		cVector.setSourceAsNumber(xVector.getSourceAsNumber());
		
    	double factor = (getSumOfValues(xVector) / (double) getSumOfValues(rVector));
    	
    	for (LinkID linkId : getLinkIDs(rVector)) {
    		long value = (long) (factor*rVector.getVectorValueForLink(linkId) - xVector.getVectorValueForLink(linkId));
    		cVector.addVectorValueForLink(linkId, value);
    	}
    	
    	logger.debug("... done.");
    	return cVector;
	}
	
	private long getSumOfValues(Vector vector) {
		long sum = 0;
		for (VectorValue value : vector.getVectorValues()) {
			sum += value.getValue();
		}
		return sum;
	}

	private List<LinkID> getLinkIDs(Vector vector) {
		List<LinkID> ids = new ArrayList<LinkID>();
		for(VectorValue value : vector.getVectorValues()) {
			ids.add(value.getLinkID());
		}
		return ids;
	}
	
	private boolean isInvalidInput(XVector xVector, RVector rVector) {
		logger.debug("Validating input arguments ...");
		if (rVector == null || xVector == null)
			return true;
		if (xVector.getVectorValues().size() != rVector.getVectorValues().size())
			return true;
		if (xVector.getVectorValues().size() < 2)
			return true;
		if (!getLinkIDs(xVector).containsAll(getLinkIDs(rVector)))
			return true;
		
		return false;
	}
	
}
