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
 * The DC2DCCommunicationID class.
 *
 * @author George Petropoulos
 * @version 1.0
 * 
 */
public final class DC2DCCommunicationID implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor.
	 */
	public DC2DCCommunicationID() {
		
	}

	/**
	 * The constructor with arguments.
	 * 
	 * @param communicationNumber
	 * @param communicationSymbol
	 * @param localAsNumber
	 * @param localCloudDCName
	 * @param remoteAsNumber
	 * @param remoteCloudDCName
	 */
	public DC2DCCommunicationID(int communicationNumber,
			String communicationSymbol, int localAsNumber,
			String localCloudDCName, int remoteAsNumber,
			String remoteCloudDCName) {
		
		this.communicationNumber = communicationNumber;
		this.communicationSymbol = communicationSymbol;
		this.localAsNumber = localAsNumber;
		this.localCloudDCName = localCloudDCName;
		this.remoteAsNumber = remoteAsNumber;
		this.remoteCloudDCName = remoteCloudDCName;
	}


	private int communicationNumber;
	
	private String communicationSymbol;
	
	private int  localAsNumber;
	
	private String localCloudDCName;
	
	private int remoteAsNumber;
	
	private String remoteCloudDCName;

	public int getCommunicationNumber() {
		return communicationNumber;
	}

	public void setCommunicationNumber(int communicationNumber) {
		this.communicationNumber = communicationNumber;
	}

	public String getCommunicationSymbol() {
		return communicationSymbol;
	}

	public void setCommunicationSymbol(String communicationSymbol) {
		this.communicationSymbol = communicationSymbol;
	}

	public int getLocalAsNumber() {
		return localAsNumber;
	}

	public void setLocalAsNumber(int localAsNumber) {
		this.localAsNumber = localAsNumber;
	}

	public String getLocalCloudDCName() {
		return localCloudDCName;
	}

	public void setLocalCloudDCName(String localCloudDCName) {
		this.localCloudDCName = localCloudDCName;
	}

	public int getRemoteAsNumber() {
		return remoteAsNumber;
	}

	public void setRemoteAsNumber(int remoteAsNumber) {
		this.remoteAsNumber = remoteAsNumber;
	}

	public String getRemoteCloudDCName() {
		return remoteCloudDCName;
	}

	public void setRemoteCloudDCName(String remoteCloudDCName) {
		this.remoteCloudDCName = remoteCloudDCName;
	}

	@Override
	public String toString() {
		return "DC2DCCommunicationID [communicationNumber="
				+ communicationNumber + ", communicationSymbol="
				+ communicationSymbol + ", localAsNumber=" + localAsNumber
				+ ", localCloudDCName=" + localCloudDCName
				+ ", remoteAsNumber=" + remoteAsNumber + ", remoteCloudDCName="
				+ remoteCloudDCName + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + communicationNumber;
		result = prime
				* result
				+ ((communicationSymbol == null) ? 0 : communicationSymbol
						.hashCode());
		result = prime * result + localAsNumber;
		result = prime
				* result
				+ ((localCloudDCName == null) ? 0 : localCloudDCName.hashCode());
		result = prime * result + remoteAsNumber;
		result = prime
				* result
				+ ((remoteCloudDCName == null) ? 0 : remoteCloudDCName
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DC2DCCommunicationID))
			return false;
		DC2DCCommunicationID other = (DC2DCCommunicationID) obj;
		if (communicationNumber != other.communicationNumber)
			return false;
		if (communicationSymbol == null) {
			if (other.communicationSymbol != null)
				return false;
		} else if (!communicationSymbol.equals(other.communicationSymbol))
			return false;
		if (localAsNumber != other.localAsNumber)
			return false;
		if (localCloudDCName == null) {
			if (other.localCloudDCName != null)
				return false;
		} else if (!localCloudDCName.equals(other.localCloudDCName))
			return false;
		if (remoteAsNumber != other.remoteAsNumber)
			return false;
		if (remoteCloudDCName == null) {
			if (other.remoteCloudDCName != null)
				return false;
		} else if (!remoteCloudDCName.equals(other.remoteCloudDCName))
			return false;
		return true;
	}
	
	
	
}
