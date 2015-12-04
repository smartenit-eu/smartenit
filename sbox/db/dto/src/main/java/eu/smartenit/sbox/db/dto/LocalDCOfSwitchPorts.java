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
 * The LocalDCOfSwitchPorts class.
 *
 * @author George Petropoulos
 * @version 3.0
 * 
 */
public class LocalDCOfSwitchPorts implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3152670496665084429L;
	
	
	/**
	 * The constructor.
	 */
	public LocalDCOfSwitchPorts() {
		
	}

	/**
	 * The constructor using fields.
	 * 
	 * @param daRouterOfDPID
	 * @param localDCOfSwitchPortNumbers
	 */
	public LocalDCOfSwitchPorts(String daRouterOfDPID,
			List<Integer> localDCOfSwitchPortNumbers) {

		this.daRouterOfDPID = daRouterOfDPID;
		this.localDCOfSwitchPortNumbers = localDCOfSwitchPortNumbers;
	}

	private String daRouterOfDPID;
	
	private List<Integer> localDCOfSwitchPortNumbers;

	public String getDaRouterOfDPID() {
		return daRouterOfDPID;
	}

	public void setDaRouterOfDPID(String daRouterOfDPID) {
		this.daRouterOfDPID = daRouterOfDPID;
	}

	public List<Integer> getLocalDCOfSwitchPortNumbers() {
		return localDCOfSwitchPortNumbers;
	}

	public void setLocalDCOfSwitchPortNumbers(
			List<Integer> localDCOfSwitchPortNumbers) {
		this.localDCOfSwitchPortNumbers = localDCOfSwitchPortNumbers;
	}


	@Override
	public String toString() {
		return "LocalDCOfSwitchPorts [daRouterOfDPID=" + daRouterOfDPID
				+ ", localDCOfSwitchPortNumbers=" + localDCOfSwitchPortNumbers
				+ "]";
	}

}
