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
 * The SimpleTunnelID class.
 *
 * @author George Petropoulos
 * @version 1.0
 * 
 */
public final class SimpleTunnelID extends TunnelID implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor.
	 */
	public SimpleTunnelID() {
		
		super();
	}
		
	
	/**
	 * The constructor with arguments.
	 * 
	 * @param tunnelName
	 * @param tunnelNumber
	 */
	public SimpleTunnelID(String tunnelName, int tunnelNumber) {
		super();
		this.tunnelName = tunnelName;
		this.tunnelNumber = tunnelNumber;
	}


	private String tunnelName;
	
	private int tunnelNumber;

	public String getTunnelName() {
		return tunnelName;
	}

	public void setTunnelName(String tunnelName) {
		this.tunnelName = tunnelName;
	}

	public int getTunnelNumber() {
		return tunnelNumber;
	}

	public void setTunnelNumber(int tunnelNumber) {
		this.tunnelNumber = tunnelNumber;
	}


	@Override
	public String toString() {
		return "SimpleTunnelID [tunnelName=" + tunnelName + ", tunnelNumber="
				+ tunnelNumber + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((tunnelName == null) ? 0 : tunnelName.hashCode());
		result = prime * result + tunnelNumber;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SimpleTunnelID))
			return false;
		SimpleTunnelID other = (SimpleTunnelID) obj;
		if (tunnelName == null) {
			if (other.tunnelName != null)
				return false;
		} else if (!tunnelName.equals(other.tunnelName))
			return false;
		if (tunnelNumber != other.tunnelNumber)
			return false;
		return true;
	}
	
	

}
