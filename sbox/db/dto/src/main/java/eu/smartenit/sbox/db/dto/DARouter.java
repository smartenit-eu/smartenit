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

/**
 * The DARouter class.
 *
 * @author George Petropoulos
 * @version 1.2
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
	}

	/**
	 * @param managementAddress
	 * @param snmpCommunity
	 * @param ofSwitchDPID
	 */
	public DARouter(NetworkAddressIPv4 managementAddress, String snmpCommunity,
			String ofSwitchDPID) {
		super();
		this.managementAddress = managementAddress;
		this.snmpCommunity = snmpCommunity;
		this.ofSwitchDPID = ofSwitchDPID;
	}

	private NetworkAddressIPv4 managementAddress;
	
	private String snmpCommunity;
	
	private String ofSwitchDPID;

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

	@Override
	public String toString() {
		return "DARouter [managementAddress=" + managementAddress
				+ ", snmpCommunity=" + snmpCommunity + ", ofSwitchDPID="
				+ ofSwitchDPID + "]";
	}
	
	

}
