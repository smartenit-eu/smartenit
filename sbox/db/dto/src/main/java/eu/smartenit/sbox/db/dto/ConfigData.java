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
package eu.smartenit.sbox.db.dto;

import java.io.Serializable;
import java.util.List;

/**
 * The ConfigData class.
 *
 * @author George Petropoulos
 * @version 3.0
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
		
		this.entries = entries;
	}


	/**
	 * The constructor with arguments.
	 * 
	 * @param entries
	 * @param operationModeSDN
	 * @param localDCPortsConfig
	 */
	public ConfigData(List<ConfigDataEntry> entries,
			OperationModeSDN operationModeSDN,
			List<LocalDCOfSwitchPorts> localDCPortsConfig) {
		
		this.entries = entries;
		this.operationModeSDN = operationModeSDN;
		this.localDCPortsConfig = localDCPortsConfig;
	}


	private List<ConfigDataEntry> entries;
	
	private OperationModeSDN operationModeSDN;
	
	private List<LocalDCOfSwitchPorts> localDCPortsConfig;

	public List<ConfigDataEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<ConfigDataEntry> entries) {
		this.entries = entries;
	}

	public OperationModeSDN getOperationModeSDN() {
		return operationModeSDN;
	}

	public void setOperationModeSDN(OperationModeSDN operationModeSDN) {
		this.operationModeSDN = operationModeSDN;
	}

	public List<LocalDCOfSwitchPorts> getLocalDCPortsConfig() {
		return localDCPortsConfig;
	}

	public void setLocalDCPortsConfig(List<LocalDCOfSwitchPorts> localDCPortsConfig) {
		this.localDCPortsConfig = localDCPortsConfig;
	}

	
	@Override
	public String toString() {
		return "ConfigData [entries=" + entries + ", operationModeSDN="
				+ operationModeSDN + ", localDCPortsConfig="
				+ localDCPortsConfig + "]";
	}

}
