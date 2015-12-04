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
 * The ConfigDataEntry class.
 *
 * @author George Petropoulos
 * @version 1.2
 * 
 */
public class ConfigDataEntry implements Serializable {

	/**
	 * The constructor.
	 */
	public ConfigDataEntry() {
		super();
	}
	
	

	/**
	 * @param remoteDcPrefix
	 * @param daRouterOfDPID
	 * @param tunnels
	 */
	public ConfigDataEntry(NetworkAddressIPv4 remoteDcPrefix,
			String daRouterOfDPID, List<TunnelInfo> tunnels) {
		super();
		this.remoteDcPrefix = remoteDcPrefix;
		this.daRouterOfDPID = daRouterOfDPID;
		this.tunnels = tunnels;
	}



	/**
	 * 
	 */
	private static final long serialVersionUID = -8612270870356275940L;
	
	private NetworkAddressIPv4 remoteDcPrefix;
	
	private String daRouterOfDPID;
	
	private List<TunnelInfo> tunnels;

	public NetworkAddressIPv4 getRemoteDcPrefix() {
		return remoteDcPrefix;
	}

	public void setRemoteDcPrefix(NetworkAddressIPv4 remoteDcPrefix) {
		this.remoteDcPrefix = remoteDcPrefix;
	}

	public String getDaRouterOfDPID() {
		return daRouterOfDPID;
	}

	public void setDaRouterOfDPID(String daRouterOfDPID) {
		this.daRouterOfDPID = daRouterOfDPID;
	}

	public List<TunnelInfo> getTunnels() {
		return tunnels;
	}

	public void setTunnels(List<TunnelInfo> tunnels) {
		this.tunnels = tunnels;
	}



	@Override
	public String toString() {
		return "ConfigDataEntry [remoteDcPrefix=" + remoteDcPrefix
				+ ", daRouterOfDPID=" + daRouterOfDPID + ", tunnels=" + tunnels
				+ "]";
	}
	
	

}
