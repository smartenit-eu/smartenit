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
 * 
 */
public abstract class TrafficSamplesContainer {

	/**
	 * Contains the link IDs for links 1
	 */
	protected final SimpleLinkID link1;
	
	/**
	 * Contains the link IDs for links 2
	 */
	protected final SimpleLinkID link2;

	public TrafficSamplesContainer(SimpleLinkID link1, SimpleLinkID link2) {
		this.link1 = link1;
		this.link2 = link2;
	}
	
	/**
	 * Stores traffic values from report/sample.
	 * 
	 * @param xVector
	 *            The X vector
	 * @param zVectors
	 *            The list of Z vectors
	 */
	public abstract void storeTrafficValues(XVector xVector, List<ZVector> zVectors);

	/**
	 * Resets aggregated traffic information (after the accounting period has
	 * expired)
	 * 
	 */
	public abstract void resetTrafficValues();

	public abstract long[] getTrafficValuesForLinks();

	public abstract long[] getTrafficValuesForTunnels();

}
