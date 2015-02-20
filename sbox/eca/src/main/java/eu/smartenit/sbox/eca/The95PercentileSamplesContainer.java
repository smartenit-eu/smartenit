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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.LocalVectorValue;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.db.dto.ZVector;

/**
 * Container for 95th percentile traffic samples. 
 * 
 * @author Lukasz Lopatowski
 * @version 3.0
 *
 */
public class The95PercentileSamplesContainer extends TrafficSamplesContainer {

	private static final Logger logger = LoggerFactory.getLogger(The95PercentileSamplesContainer.class);

	private int sampleCounter = 0;
	private Map<SimpleLinkID, List<TrafficSample>> linkTrafficSamples = new HashMap<>();
	private Map<SimpleLinkID, List<TrafficSample>> tunnelTrafficSamples = new HashMap<>();
	
	public The95PercentileSamplesContainer(SimpleLinkID link1, SimpleLinkID link2) {
		super(link1, link2);
	}

	@Override
	public void storeTrafficValues(XVector xVector, List<ZVector> zVectors) {
		logger.debug("Storing new traffic samples.");
		if (xVector == null || zVectors == null || zVectors.size() != 1)
			throw new IllegalArgumentException("Invalid input.");
		
		sampleCounter++;
		for (LocalVectorValue vectorValue : xVector.getVectorValues()) {
			TrafficSample sample = new TrafficSample();
			sample.sampleNumber = sampleCounter;
			sample.value = vectorValue.getValue();
			storeLinkTrafficSample((SimpleLinkID)vectorValue.getLinkID(), sample);
		}
			
		for (LocalVectorValue vectorValue : zVectors.get(0).getVectorValues()) {
			TrafficSample sample = new TrafficSample();
			sample.sampleNumber = sampleCounter;
			sample.value = vectorValue.getValue();
			storeTunnelTrafficSample((SimpleLinkID)vectorValue.getLinkID(), sample);
		}
	}

	@Override
	public void resetTrafficValues() {
		sampleCounter = 0;
		linkTrafficSamples = new HashMap<>();
		tunnelTrafficSamples = new HashMap<>();
	}

	@Override
	public long[] getTrafficValuesForLinks() {
		logger.debug("Returning 95th percentile sample from following lists: \n" 
				+ linkTrafficSamples.get(link1) + "\n"
				+ linkTrafficSamples.get(link2));
		long[] the95thPercentileXSamples = new long[2];
		
		Collections.sort(linkTrafficSamples.get(link1));
		Collections.sort(linkTrafficSamples.get(link2));
		
		the95thPercentileXSamples[EconomicAnalyzerInternal.x1] = 
				linkTrafficSamples.get(link1).get(calculate95thPercentileSampleIndex()).value;
		the95thPercentileXSamples[EconomicAnalyzerInternal.x2] = 
				linkTrafficSamples.get(link2).get(calculate95thPercentileSampleIndex()).value;
		
		logger.info("Returned 95th percentile X sample is [" + the95thPercentileXSamples[0] + ", " + the95thPercentileXSamples[1] + "]");
		return the95thPercentileXSamples;
	}

	@Override
	public long[] getTrafficValuesForTunnels() {
		logger.debug("Returning 95th percentile sample from following lists: \n" 
				+ tunnelTrafficSamples.get(link1) + "\n"
				+ tunnelTrafficSamples.get(link2));
		long[] the95thPercentileZSamples = new long[2];
		
		Collections.sort(tunnelTrafficSamples.get(link1));
		Collections.sort(tunnelTrafficSamples.get(link2));
		
		the95thPercentileZSamples[EconomicAnalyzerInternal.x1] = 
				tunnelTrafficSamples.get(link1).get(calculate95thPercentileSampleIndex()).value;
		the95thPercentileZSamples[EconomicAnalyzerInternal.x2] = 
				tunnelTrafficSamples.get(link2).get(calculate95thPercentileSampleIndex()).value;
		
		logger.info("Returned 95th percentile Z sample is [" + the95thPercentileZSamples[0] + ", " + the95thPercentileZSamples[1] + "]");
		return the95thPercentileZSamples;
	}
	
	private void storeLinkTrafficSample(SimpleLinkID linkID, TrafficSample sample) {
		if (!linkTrafficSamples.containsKey(linkID))
			linkTrafficSamples.put(linkID, new ArrayList<TrafficSample>());
		
		linkTrafficSamples.get(linkID).add(sample);
	}
	
	private void storeTunnelTrafficSample(SimpleLinkID linkID, TrafficSample sample) {
		if (!tunnelTrafficSamples.containsKey(linkID))
			tunnelTrafficSamples.put(linkID, new ArrayList<TrafficSample>());
		
		tunnelTrafficSamples.get(linkID).add(sample);
	}
	
	private int calculate95thPercentileSampleIndex() {
		return (int)(sampleCounter*0.95);
	}
	
	private class TrafficSample implements Comparable<TrafficSample>{
		public int sampleNumber = 0;
		public long value = 0;
		
		@Override
		public int compareTo(TrafficSample other) {
			return Long.compare(value, other.value);
		}
		
		@Override
		public String toString() {
			return "[" + sampleNumber + ", " + value + "]";
		}
	}

}
