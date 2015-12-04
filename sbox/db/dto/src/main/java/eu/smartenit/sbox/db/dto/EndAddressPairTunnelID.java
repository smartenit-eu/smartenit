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


/**
 * The EndAddressPairTunnelID class.
 *
 * @author George Petropoulos
 * @version 1.2
 * 
 */
public class EndAddressPairTunnelID extends TunnelID implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3486146712048893647L;
	
	
	/**
	 * The constructor.
	 */
	public EndAddressPairTunnelID() {
		super();
		this.localTunnelEndAddress = new NetworkAddressIPv4();
		this.remoteTunnelEndAddress = new NetworkAddressIPv4();
	}


	/**
	 * The constructor with arguments.
	 * 
	 * @param tunnelName
	 * @param localTunnelEndAddress
	 * @param remoteTunnelEndAddress
	 */
	public EndAddressPairTunnelID(String tunnelName,
			NetworkAddressIPv4 localTunnelEndAddress,
			NetworkAddressIPv4 remoteTunnelEndAddress) {
		super();
		this.tunnelName = tunnelName;
		this.localTunnelEndAddress = localTunnelEndAddress;
		this.remoteTunnelEndAddress = remoteTunnelEndAddress;
	}

	private String tunnelName;
	
	private NetworkAddressIPv4 localTunnelEndAddress;
	
	private NetworkAddressIPv4 remoteTunnelEndAddress;

	public String getTunnelName() {
		return tunnelName;
	}

	public void setTunnelName(String tunnelName) {
		this.tunnelName = tunnelName;
	}

	public NetworkAddressIPv4 getLocalTunnelEndAddress() {
		return localTunnelEndAddress;
	}

	public void setLocalTunnelEndAddress(NetworkAddressIPv4 localTunnelEndAddress) {
		this.localTunnelEndAddress = localTunnelEndAddress;
	}

	public NetworkAddressIPv4 getRemoteTunnelEndAddress() {
		return remoteTunnelEndAddress;
	}

	public void setRemoteTunnelEndAddress(NetworkAddressIPv4 remoteTunnelEndAddress) {
		this.remoteTunnelEndAddress = remoteTunnelEndAddress;
	}

	@Override
	public String toString() {
		return "EndAddressPairTunnelID [tunnelName=" + tunnelName
				+ ", localTunnelEndAddress=" + localTunnelEndAddress
				+ ", remoteTunnelEndAddress=" + remoteTunnelEndAddress + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((localTunnelEndAddress == null) ? 0 : localTunnelEndAddress
						.hashCode());
		result = prime
				* result
				+ ((remoteTunnelEndAddress == null) ? 0
						: remoteTunnelEndAddress.hashCode());
		result = prime * result
				+ ((tunnelName == null) ? 0 : tunnelName.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EndAddressPairTunnelID))
			return false;
		EndAddressPairTunnelID other = (EndAddressPairTunnelID) obj;
		if (localTunnelEndAddress == null) {
			if (other.localTunnelEndAddress != null)
				return false;
		} else if (!localTunnelEndAddress.equals(other.localTunnelEndAddress))
			return false;
		if (remoteTunnelEndAddress == null) {
			if (other.remoteTunnelEndAddress != null)
				return false;
		} else if (!remoteTunnelEndAddress.equals(other.remoteTunnelEndAddress))
			return false;
		if (tunnelName == null) {
			if (other.tunnelName != null)
				return false;
		} else if (!tunnelName.equals(other.tunnelName))
			return false;
		return true;
	}
	
	

}
