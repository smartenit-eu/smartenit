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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.eca.The95PercentileSamplesContainer.TrafficSample;

/**
 * Stores 95th percentile traffic samples collected during past accounting
 * periods.
 * 
 * @author Lukasz Lopatowski
 * @version 3.1
 */
public class The95thPercentileSamplesHistory {

	private int currentEntryId = 0;
	private List<Entry> entries = new ArrayList<>();

	/**
	 * Adds new entry to the history.
	 * 
	 * @param linkTrafficSamples 
	 * @param tunnelTrafficSamples
	 */
	public void store(
			Map<SimpleLinkID, List<TrafficSample>> linkTrafficSamples,
			Map<SimpleLinkID, List<TrafficSample>> tunnelTrafficSamples) {
		entries.add(new Entry(++currentEntryId, linkTrafficSamples, tunnelTrafficSamples));
	}
	
	/**
	 * Returns the whole history.
	 * 
	 * @return list of entries
	 */
	public List<Entry> getEntries() {
		return entries;
	}

	public class Entry {
		
		public final int id;
		public final DateTime timestamp;
		public final Map<SimpleLinkID, List<TrafficSample>> linkTrafficSamples;
		public final Map<SimpleLinkID, List<TrafficSample>> tunnelTrafficSamples;
		
		public Entry(int id
				, Map<SimpleLinkID, List<TrafficSample>> linkTrafficSamples
				, Map<SimpleLinkID, List<TrafficSample>> tunnelTrafficSamples) {
			this.id = id;
			this.timestamp = DateTime.now();
			this.linkTrafficSamples = linkTrafficSamples;
			this.tunnelTrafficSamples = tunnelTrafficSamples;
		}

	}

}
