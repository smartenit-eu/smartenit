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

import java.util.List;

import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.db.dto.ZVector;

/**
 * Base class for traffic data containers used by
 * {@link EconomicAnalyzerInternal} to store data received from QoS Analyzer.
 * 
 * @author Lukasz Lopatowski
 * @version 3.0
 */
public abstract class TrafficSamplesContainer {

	/**
	 * Contains the identifier of the first link
	 */
	protected final SimpleLinkID link1;
	
	/**
	 * Contains the identifier of the second link
	 */
	protected final SimpleLinkID link2;

	/**
	 * The constructor with arguments.
	 * 
	 * @param link1
	 *            identifier of the first link
	 * @param link2
	 *            identifier of the second link
	 */
	public TrafficSamplesContainer(SimpleLinkID link1, SimpleLinkID link2) {
		this.link1 = link1;
		this.link2 = link2;
	}
	
	/**
	 * Stores traffic values from report/sample.
	 * 
	 * @param xVector
	 *            link traffic vector
	 * @param zVectors
	 *            list of tunnel traffic vectors
	 */
	public abstract void storeTrafficValues(XVector xVector, List<ZVector> zVectors);

	/**
	 * Resets aggregated traffic information (after the accounting period has
	 * expired).
	 */
	public abstract void resetTrafficValues();

	/**
	 * Returns a vector of link traffic samples to be used in reference vector
	 * calculation.
	 * 
	 * @return table of samples (number of bytes per link)
	 */
	public abstract long[] getTrafficValuesForLinks();

	/**
	 * Returns a vector of tunnel traffic samples to be used in reference vector
	 * calculation.
	 * 
	 * @return table of samples (number of bytes per tunnel)
	 */
	public abstract long[] getTrafficValuesForTunnels();

}
