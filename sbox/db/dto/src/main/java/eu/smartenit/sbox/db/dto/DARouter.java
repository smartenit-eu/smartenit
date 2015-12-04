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
import java.util.ArrayList;
import java.util.List;

/**
 * The DARouter class.
 *
 * @author George Petropoulos
 * @version 3.0
 * 
 */
public final class DARouter implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor.
	 */
	public DARouter() {
		this.managementAddress = new NetworkAddressIPv4();
		this.localDCOfSwitchPortNumbers = new ArrayList<Integer>();
	}

	/**
	 * The constructor with fields.
	 * 
	 * @param managementAddress
	 * @param snmpCommunity
	 * @param ofSwitchDPID
	 */
	public DARouter(NetworkAddressIPv4 managementAddress, String snmpCommunity,
			String ofSwitchDPID) {
		
		this.managementAddress = managementAddress;
		this.snmpCommunity = snmpCommunity;
		this.ofSwitchDPID = ofSwitchDPID;
	}
	

	/**
	 * The constructor with fields.
	 * 
	 * @param managementAddress
	 * @param snmpCommunity
	 * @param ofSwitchDPID
	 * @param localDCOfSwitchPortNumbers
	 */
	public DARouter(NetworkAddressIPv4 managementAddress, String snmpCommunity,
			String ofSwitchDPID, List<Integer> localDCOfSwitchPortNumbers) {
		
		this.managementAddress = managementAddress;
		this.snmpCommunity = snmpCommunity;
		this.ofSwitchDPID = ofSwitchDPID;
		this.localDCOfSwitchPortNumbers = localDCOfSwitchPortNumbers;
	}


	private NetworkAddressIPv4 managementAddress;
	
	private String snmpCommunity;
	
	private String ofSwitchDPID;
	
	private List<Integer> localDCOfSwitchPortNumbers;

	public NetworkAddressIPv4 getManagementAddress() {
		return managementAddress;
	}

	public void setManagementAddress(NetworkAddressIPv4 managementAddress) {
		this.managementAddress = managementAddress;
	}

	public String getSnmpCommunity() {
		return snmpCommunity;
	}

	public void setSnmpCommunity(String snmpCommunity) {
		this.snmpCommunity = snmpCommunity;
	}

	public String getOfSwitchDPID() {
		return ofSwitchDPID;
	}

	public void setOfSwitchDPID(String ofSwitchDPID) {
		this.ofSwitchDPID = ofSwitchDPID;
	}

	public List<Integer> getLocalDCOfSwitchPortNumbers() {
		return localDCOfSwitchPortNumbers;
	}

	public void setLocalDCOfSwitchPortNumbers(
			List<Integer> localDCOfSwitchPortNumbers) {
		this.localDCOfSwitchPortNumbers = localDCOfSwitchPortNumbers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((managementAddress == null) ? 0 : managementAddress
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DARouter other = (DARouter) obj;
		if (managementAddress == null) {
			if (other.managementAddress != null)
				return false;
		} else if (!managementAddress.equals(other.managementAddress))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DARouter [managementAddress=" + managementAddress
				+ ", snmpCommunity=" + snmpCommunity + ", ofSwitchDPID="
				+ ofSwitchDPID + ", localDCOfSwitchPortNumbers="
				+ localDCOfSwitchPortNumbers + "]";
	}

}
