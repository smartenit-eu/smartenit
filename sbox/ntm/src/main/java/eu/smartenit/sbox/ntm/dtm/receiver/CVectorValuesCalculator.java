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
package eu.smartenit.sbox.ntm.dtm.receiver;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.LinkID;
import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.LocalVector;
import eu.smartenit.sbox.db.dto.LocalVectorValue;
import eu.smartenit.sbox.db.dto.XVector;

/**
 * Implements compensation vector values calculation logic.
 * 
 * @author Lukasz Lopatowski
 * @version 1.2
 * 
 */
public class CVectorValuesCalculator {

	private static final Logger logger = LoggerFactory.getLogger(CVectorValuesCalculator.class);
	
	private final XVector xVector;
	private final LocalRVector rVector;
	
	/**
	 * The constructor with arguments.
	 * 
	 * @param xVector
	 *            link traffic vector to be used during calculation
	 * @param rVector
	 *            reference vector to be used during calculation
	 */
	public CVectorValuesCalculator(XVector xVector, LocalRVector rVector) {
		this.xVector = xVector;
		this.rVector = rVector;
	}
	
	/**
	 * Calculates compensation vector values.
	 * 
	 * @return calculated list of compensation vector values
	 */
	public List<LocalVectorValue> calculate() {
		logger.debug("Calculating C vector values for AS {}", xVector.getSourceAsNumber());
		List<LocalVectorValue> cVectorValues = new ArrayList<>();
		
    	double factor = (getSumOfValues(xVector) / (double) getSumOfValues(rVector));
    	
    	for (LinkID linkId : getLinkIDs(rVector)) {
    		long value = (long) (factor*rVector.getVectorValueForLink(linkId) - xVector.getVectorValueForLink(linkId));
    		cVectorValues.add(new LocalVectorValue(value, linkId));
    	}
    	
    	logger.debug("... done.");
    	return cVectorValues;
	}
	
	private long getSumOfValues(LocalVector vector) {
		long sum = 0;
		for (LocalVectorValue value : vector.getVectorValues()) {
			sum += value.getValue();
		}
		return sum;
	}

	private List<LinkID> getLinkIDs(LocalVector vector) {
		List<LinkID> ids = new ArrayList<LinkID>();
		for(LocalVectorValue value : vector.getVectorValues()) {
			ids.add(value.getLinkID());
		}
		return ids;
	}
	
}
