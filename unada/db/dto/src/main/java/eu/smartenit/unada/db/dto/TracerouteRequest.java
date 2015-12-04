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
package eu.smartenit.unada.db.dto;

import java.io.Serializable;

/**
 * The TracerouteRequest class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public class TracerouteRequest implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7621664324655810806L;

	public TracerouteRequest() {
		
	}
	
	private String type;
    private String requestorAddress;
    private String targetAddress;
    
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRequestorAddress() {
		return requestorAddress;
	}
	public void setRequestorAddress(String requestorAddress) {
		this.requestorAddress = requestorAddress;
	}
	public String getTargetAddress() {
		return targetAddress;
	}
	public void setTargetAddress(String targetAddress) {
		this.targetAddress = targetAddress;
	}
	
	@Override
	public String toString() {
		return "TracerouteRequest [type=" + type + ", requestorAddress="
				+ requestorAddress + ", targetAddress=" + targetAddress + "]";
	}
	  

}
