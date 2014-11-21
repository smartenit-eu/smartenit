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
package eu.smartenit.sbox.db.dto;

import java.io.Serializable;
import java.util.List;

/**
 * The ConfigData class.
 *
 * @author George Petropoulos
 * @version 1.2
 * 
 */
public final class ConfigData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor.
	 */
	public ConfigData() {
		
	}
	
	/**
	 * The constructor with arguments.
	 * 
	 * @param entries
	 */
	public ConfigData(List<ConfigDataEntry> entries) {
		super();
		this.entries = entries;
	}



	private List<ConfigDataEntry> entries;

	public List<ConfigDataEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<ConfigDataEntry> entries) {
		this.entries = entries;
	}

	@Override
	public String toString() {
		return "ConfigData [entries=" + entries + "]";
	}
	
	

}
