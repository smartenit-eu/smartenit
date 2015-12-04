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
 * The TunnelInfo class.
 *
 * @author George Petropoulos
 * @version 1.2
 * 
 */
public class TunnelInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3802021470060469617L;
	
	
	
	/**
	 * The constructor.
	 */
	public TunnelInfo() {
		super();
	}

	/**
	 * The constructor with arguments
	 * 
	 * @param tunnelID
	 * @param daRouterOfPortNumber
	 */
	public TunnelInfo(EndAddressPairTunnelID tunnelID, int daRouterOfPortNumber) {
		super();
		this.tunnelID = tunnelID;
		this.daRouterOfPortNumber = daRouterOfPortNumber;
	}

	private EndAddressPairTunnelID tunnelID;
	
	private int daRouterOfPortNumber;

	public EndAddressPairTunnelID getTunnelID() {
		return tunnelID;
	}

	public void setTunnelID(EndAddressPairTunnelID tunnelID) {
		this.tunnelID = tunnelID;
	}

	public int getDaRouterOfPortNumber() {
		return daRouterOfPortNumber;
	}

	public void setDaRouterOfPortNumber(int daRouterOfPortNumber) {
		this.daRouterOfPortNumber = daRouterOfPortNumber;
	}

	@Override
	public String toString() {
		return "TunnelInfo [tunnelID=" + tunnelID + ", daRouterOfPortNumber="
				+ daRouterOfPortNumber + "]";
	}
	
	

}
