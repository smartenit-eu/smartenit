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
package eu.smartenit.sbox.ntm.dtm.receiver;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.LocalVectorValue;
import eu.smartenit.sbox.db.dto.XVector;

/**
 * Used to store {@link XVector} and {@link LocalRVector} objects representing link traffic 
 * vectors and reference vectors organized in pairs ({@link XRVectorPair}) by AS number.
 * 
 * @author Lukasz Lopatowski
 * @version 1.2
 * 
 */
public class VectorsContainer {
	
	private static final Logger logger = LoggerFactory.getLogger(VectorsContainer.class);
	
	private Map<Integer, XRVectorPair> asVectors = new HashMap<Integer, XRVectorPair>();
	
	/**
	 * Stores data from new link traffic vector ({@link XVector}) in the
	 * container. If {@link XRVectorPair} for given AS already exists it is
	 * updated, meaning that the current counter values are increased by the
	 * values from the new link traffic vector. If not, new {@link XRVectorPair}
	 * is created.
	 * 
	 * @param xVector
	 *            object with values to be stored in the container
	 * @return updated pair object
	 */
	public XRVectorPair accumulateAndStoreUpdatedXVectorValues(XVector xVector) {
		int asNumber = xVector.getSourceAsNumber();
		if (isAsNumberNotSet(asNumber)) {
			logger.warn("AS number is not set. Will not update xVector.");
			return null;
		}
		
		XRVectorPair pair = asVectors.get(asNumber);
		if (pair == null)
			insertNewVectorPairWithXVector(asNumber, xVector);
		else
			accumulateAndUpdateExistingVectorPairWithXVector(pair, xVector);

		logger.debug("Information about new X vector stored successfuly.");
		return asVectors.get(asNumber);
	}
	
	/**
	 * Resets the counter values of link traffic vector stored for given
	 * asNumber.
	 * 
	 * @param asNumber
	 *            number of the AS
	 */
	public void resetXVectorValues(int asNumber) {
		if (isAsNumberNotSet(asNumber)) {
			logger.warn("AS number is not set. Will not reset any xVector values.");
			return;
		}
		XRVectorPair pair = asVectors.get(asNumber);
		if (pair != null)
			resetXVectorValuesInExistingPair(pair);
		else
			logger.warn("Pair for given asNumber not found. Will not reset any xVector values.");
	}

	/**
	 * Stores new reference vector ({@link LocalRVector}) in the container. If
	 * {@link XRVectorPair} for given AS already exists it is updated. If not,
	 * new {@link XRVectorPair} is created.
	 * 
	 * @param rVector
	 *            object to be stored in the container
	 * @return updated pair object
	 */
	public XRVectorPair storeUpdatedRVector(LocalRVector rVector) {
		int asNumber = rVector.getSourceAsNumber();
		if (isAsNumberNotSet(asNumber)) {
			logger.warn("AS number is not set. Will not store rVector");
			return null;
		}
		
		XRVectorPair pair = asVectors.get(asNumber);
		if (pair == null) 
			insertNewVectorPairWithRVector(asNumber, rVector);
		else 
			updateExistingVectorPairRVector(pair, rVector);

		logger.debug("Information about new R vector stored successfuly.");
		return asVectors.get(asNumber);
	}

	/**
	 * Retrieves {@link XRVectorPair} for given AS from the container.
	 * 
	 * @param asNumber
	 *            number of the AS
	 * @return pair object or <code>null</code> if no entry for given AS exists
	 */
	public XRVectorPair loadCurrentVectorPair(int asNumber) {
		return asVectors.get(asNumber);
	}
	
	/**
	 * Retrieves a list of all ASs for which pairs are currently stored in the
	 * container.
	 * 
	 * @return set of AS numbers
	 */
	public Set<Integer> getListOfAsNumbers() {
		return asVectors.keySet();
	}
	
	private boolean isAsNumberNotSet(int asNumber) {
		return asNumber == -1 || asNumber == 0;
	}
	
	private void insertNewVectorPairWithXVector(int asNumber, XVector xVector) {
		XRVectorPair pair = new XRVectorPair(xVector, null);
		asVectors.put(asNumber, pair);
	}
	
	private void insertNewVectorPairWithRVector(int asNumber, LocalRVector rVector) {
		XRVectorPair pair = new XRVectorPair(null, rVector);
		asVectors.put(asNumber, pair);
	}
	
	private void accumulateAndUpdateExistingVectorPairWithXVector(XRVectorPair pair, XVector xVector) {
		long accumulatedCounterValue = 0;
		if (pair.getXVector() == null) {
			pair.setXVector(xVector);
			return;
		}
		
		for (LocalVectorValue currentValue : pair.getXVector().getVectorValues()){
			accumulatedCounterValue = currentValue.getValue() + xVector.getVectorValueForLink(currentValue.getLinkID()); 
			currentValue.setValue(accumulatedCounterValue);
		}
	}
	
	private void resetXVectorValuesInExistingPair(XRVectorPair pair) {
		for(LocalVectorValue currentValue : pair.getXVector().getVectorValues()) {
			currentValue.setValue(0);
		}
	}
	
	private void updateExistingVectorPairRVector(XRVectorPair pair,	LocalRVector rVector) {
		pair.setRVector(rVector);
	}
	
}
