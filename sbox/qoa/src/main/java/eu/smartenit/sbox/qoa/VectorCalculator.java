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

import java.util.HashMap;
import java.util.Map;

/**
 * Base abstract class for traffic vector calculators comprising data structure
 * used to store counter values from previous reporting periods.
 * 
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public abstract class VectorCalculator {
	
	protected  Map<Integer, CounterValues> asLatestCounterValues = new HashMap<Integer, CounterValues>();

	/**
	 * Returns stored instance of {@link CounterValues} for given AS if exists.
	 * If not, creates and returns new instance.
	 * 
	 * @param asNumber
	 *            number of the AS from which counter values are requested
	 * @return instance of {@link CounterValues}
	 */
	protected CounterValues getOrCreateLatestVectorValues(int asNumber) {
		CounterValues latestCounterValues = asLatestCounterValues.get(asNumber);
		
		if (latestCounterValues == null) {
			latestCounterValues = new CounterValues();
			asLatestCounterValues.put(asNumber, latestCounterValues);
		}
		return latestCounterValues;
	}
	
}
