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
 * The ProviderRequest class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public class ProviderRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2635798848498915408L;

	public ProviderRequest() {
		
	}
	
	private String type;
    private String requestorAddress;
    private int requestorPort;
    private long contentID;
    
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
	public int getRequestorPort() {
		return requestorPort;
	}
	public void setRequestorPort(int requestorPort) {
		this.requestorPort = requestorPort;
	}
	public long getContentID() {
		return contentID;
	}
	public void setContentID(long contentID) {
		this.contentID = contentID;
	}
	
	@Override
	public String toString() {
		return "ProviderRequest [type=" + type + ", requestorAddress="
				+ requestorAddress + ", requestorPort=" + requestorPort
				+ ", contentID=" + contentID + "]";
	}
    

}
