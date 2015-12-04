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

import java.util.LinkedList;
import java.util.List;

import eu.smartenit.sbox.db.dto.CVector;

/**
 * Stores information about compensation vectors calculated over time.
 * Implements static methods for storing, retrieving and displaying entries
 * represented by {@link CVectorHistoryEntry}.
 * 
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public class CVectorHistory {
	
	private static List<CVectorHistoryEntry> history = new LinkedList<CVectorHistoryEntry>();

	/**
	 * Stores information about new compensation vector encapsulated in
	 * {@link CVectorHistoryEntry}.
	 * 
	 * @param vector
	 *            compensation vector to be stored in history
	 */
	public synchronized static void storeInHistory(CVector vector) {
		history.add(new CVectorHistoryEntry(vector));
	}
	
	/**
	 * Returns all entries from the history.
	 * 
	 * @return list of entries
	 */
	public static List<CVectorHistoryEntry> entries(){
		return history;
	}

	/**
	 * Prepares and returns printable representation of history content.
	 * 
	 * @return description of history content in String format
	 */
	public static String toText() {
		StringBuilder sb = new StringBuilder("Compensation vectors history:").append("\n");
		for(CVectorHistoryEntry entry : history)
			sb.append(entry.toString()).append("\n");
		return sb.toString();
	}

	/**
	 * Removes all entries from history.
	 */
	public static void clear() {
		history = new LinkedList<CVectorHistoryEntry>();
	}

}
