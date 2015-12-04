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
 * The ThetaCoefficient class.
 *
 * @author George Petropoulos
 * @version 1.0
 * 
 */
public final class ThetaCoefficient implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	

	/**
	 * The constructor.
	 */
	public ThetaCoefficient() {
		this.communicationID = new DC2DCCommunicationID();
	}
	

	/**
	 * The constructor with arguments.
	 * 
	 * @param value
	 * @param communicationID
	 */
	public ThetaCoefficient(double value, DC2DCCommunicationID communicationID) {
		
		this.value = value;
		this.communicationID = communicationID;
	}

	private double value;
	
	private DC2DCCommunicationID communicationID;

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public DC2DCCommunicationID getCommunicationID() {
		return communicationID;
	}

	public void setCommunicationID(DC2DCCommunicationID communicationID) {
		this.communicationID = communicationID;
	}

	@Override
	public String toString() {
		return "ThetaCoefficient [value=" + value + ", communicationID="
				+ communicationID + "]";
	}
	
	

}
