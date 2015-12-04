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
 * The SDNController class.
 * 
 * @author George Petropoulos
 * @version 1.0
 * 
 */
public final class SDNController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor.
	 */
	public SDNController() {
		this.managementAddress = new NetworkAddressIPv4();
		this.restHost = new NetworkAddressIPv4();
		this.openflowHost = new NetworkAddressIPv4();
	}

	/**
	 * The constructor with arguments.
	 * 
	 * @param managementAddress
	 * @param daRouters
	 * @param restHost
	 * @param restPort
	 * @param openflowHost
	 * @param openflowPort
	 */
	public SDNController(NetworkAddressIPv4 managementAddress,
			List<DARouter> daRouters, NetworkAddressIPv4 restHost,
			int restPort, NetworkAddressIPv4 openflowHost, int openflowPort) {
		super();
		this.managementAddress = managementAddress;
		this.daRouters = daRouters;
		this.restHost = restHost;
		this.restPort = restPort;
		this.openflowHost = openflowHost;
		this.openflowPort = openflowPort;
	}

	private NetworkAddressIPv4 managementAddress;

	private List<DARouter> daRouters;

	private NetworkAddressIPv4 restHost;

	private int restPort;

	private NetworkAddressIPv4 openflowHost;

	private int openflowPort;

	public NetworkAddressIPv4 getManagementAddress() {
		return managementAddress;
	}

	public void setManagementAddress(NetworkAddressIPv4 managementAddress) {
		this.managementAddress = managementAddress;
	}

	public List<DARouter> getDaRouters() {
		return daRouters;
	}

	public void setDaRouters(List<DARouter> daRouters) {
		this.daRouters = daRouters;
	}

	public NetworkAddressIPv4 getRestHost() {
		return restHost;
	}

	public void setRestHost(NetworkAddressIPv4 restHost) {
		this.restHost = restHost;
	}

	public int getRestPort() {
		return restPort;
	}

	public void setRestPort(int restPort) {
		this.restPort = restPort;
	}

	public NetworkAddressIPv4 getOpenflowHost() {
		return openflowHost;
	}

	public void setOpenflowHost(NetworkAddressIPv4 openflowHost) {
		this.openflowHost = openflowHost;
	}

	public int getOpenflowPort() {
		return openflowPort;
	}

	public void setOpenflowPort(int openflowPort) {
		this.openflowPort = openflowPort;
	}

	@Override
	public String toString() {
		return "SDNController [managementAddress=" + managementAddress
				+ ", restHost=" + restHost + ", restPort=" + restPort
				+ ", openflowHost=" + openflowHost + ", openflowPort="
				+ openflowPort + "]";
	}

}
