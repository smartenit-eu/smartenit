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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.db.dto.ZVector;

/**
 * Container for accumulated traffic samples.
 * 
 * @author Lukasz Lopatowski
 * @version 3.0
 * 
 */
public class TotalVolumeSamplesContainer extends TrafficSamplesContainer {

	private static final Logger logger = LoggerFactory.getLogger(TotalVolumeSamplesContainer.class);
	
	private final long[] X_V = new long[2];

	private final long[] Z_V = new long[2];
	
	/**
	 * The constructor with arguments.
	 * 
	 * @param link1
	 *            identifier of the first link
	 * @param link2
	 *            identifier of the second link
	 */
	public TotalVolumeSamplesContainer(SimpleLinkID link1, SimpleLinkID link2) {
		super(link1, link2);
	}

	/**
	 * Accumulates and stores link and tunnel traffic values.
	 * 
	 * @param xVector
	 *            link traffic vector
	 * @param zVectors
	 *            list of tunnel traffic vectors
	 */
	@Override
	public void storeTrafficValues(XVector xVector, List<ZVector> zVectors) {
		logger.info("Accumulating volume...");
		
		X_V[EconomicAnalyzerInternal.x1] = X_V[EconomicAnalyzerInternal.x1] + xVector.getVectorValueForLink(link1);
		X_V[EconomicAnalyzerInternal.x2] = X_V[EconomicAnalyzerInternal.x2] + xVector.getVectorValueForLink(link2);
		
		// TODO Only one Z vector from the list is taken in consideration.
		//		This might have to be improved in future releases.
		Z_V[EconomicAnalyzerInternal.x1] = Z_V[EconomicAnalyzerInternal.x1] + zVectors.get(0).getVectorValueForLink(link1);
		Z_V[EconomicAnalyzerInternal.x2] = Z_V[EconomicAnalyzerInternal.x2] + zVectors.get(0).getVectorValueForLink(link2);
		
		logger.info("Volume X_V: [" + X_V[EconomicAnalyzerInternal.x1] + " / " + X_V[EconomicAnalyzerInternal.x2] + "]");
		logger.info("Volume Z_V: [" + Z_V[EconomicAnalyzerInternal.x1] + " / " + Z_V[EconomicAnalyzerInternal.x2]  + "]");
	}
	
	/**
	 * Resets aggregated traffic information (after the accounting period has
	 * expired)
	 */
	@Override
	public void resetTrafficValues() {
		X_V[EconomicAnalyzerInternal.x1] = 0;
		X_V[EconomicAnalyzerInternal.x2] = 0;

		Z_V[EconomicAnalyzerInternal.x1] = 0;
		Z_V[EconomicAnalyzerInternal.x2] = 0;
	}

	/**
	 * Returns a vector of link traffic values to be used in reference vector
	 * calculation. These values correspond to traffic accumulated over the
	 * whole accounting period.
	 * 
	 * @return table of samples (number of bytes per link)
	 */
	@Override
	public long[] getTrafficValuesForLinks() {
		return X_V;
	}
	
	/**
	 * Returns a vector of tunnel traffic samples to be used in reference vector
	 * calculation. These values correspond to traffic accumulated over the
	 * whole accounting period.
	 * 
	 * @return table of samples (number of bytes per tunnel)
	 */
	@Override
	public long[] getTrafficValuesForTunnels() {
		return Z_V;
	}
	
}
