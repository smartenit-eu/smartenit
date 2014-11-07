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

import org.joda.time.DateTime;

import eu.smartenit.sbox.db.dto.CVector;

/**
 * Encapsulates {@link CVector} in order to be placed in {@link CVectorHistory}.
 * 
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public class CVectorHistoryEntry {
	
	private DateTime timestamp;
	private CVector vector;
	
	/**
	 * The constructor with arguments.
	 * 
	 * @param vector
	 *            compensation vector to be stored in history
	 */
	public CVectorHistoryEntry(CVector vector) {
		this.vector = vector;
		timestamp = DateTime.now();
	}

	public DateTime getTimestamp() {
		return timestamp;
	}

	public CVector getVector() {
		return vector;
	}

	@Override
	public String toString() {
		return "CVectorHistoryEntry [timestamp=" + timestamp + ", vector="	+ vector + "]";
	}
	
}
