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
package eu.smartenit.sbox.qoa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.LinkID;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.XVector;

/**
 * Implements link traffic vector ({@link XVector}) calculation based on current
 * counter values collected from links in given AS and previous values of those
 * counters.
 * 
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public class XVectorCalculator extends VectorCalculator {
	private static final Logger logger = LoggerFactory.getLogger(XVectorCalculator.class);

	/**
	 * Method calculates new link traffic vector for given AS. It should be
	 * launched after each reporting period.
	 * 
	 * @param asNumber
	 *            number of the AS for which new vector is calculated
	 * @param values
	 *            counter values from all monitored links in given AS
	 *            represented by {@link CounterValues}
	 * @return new value of link traffic vector ({@link XVector}) for given AS
	 */
	public XVector calculateXVector(int asNumber, CounterValues values) {
		logger.debug("Calculating X vector for AS {} ...", asNumber);
		CounterValues latestCounterValues = getOrCreateLatestVectorValues(asNumber);
		XVector xVector = calculateXVector(values, latestCounterValues);
		xVector.setSourceAsNumber(asNumber);
		logger.debug("... done.");
		return xVector;
	}

	private XVector calculateXVector(CounterValues values, CounterValues latestCounterValues) {
		XVector newXVector = new XVector();
		for(LinkID linkID : values.getAllLinkIds()) {
			long currentCounterValue = values.getCounterValue(linkID); 
			long xVectorValue = currentCounterValue;
			
			if (latestCounterValues.getCounterValue(linkID) != null)
				xVectorValue -= latestCounterValues.getCounterValue(linkID);
		
			if (xVectorValue < 0) {
				logger.warn("Calculated X vector value for link is less than zero. " +
						"This is incorrect. Will set it to zero. Affected link: {}", ((SimpleLinkID)linkID).toString());
				xVectorValue = 0;
			}
			newXVector.addVectorValueForLink(linkID, xVectorValue);
			latestCounterValues.storeCounterValue(linkID, currentCounterValue);
		}
		
		return newXVector;
	}
	
}
