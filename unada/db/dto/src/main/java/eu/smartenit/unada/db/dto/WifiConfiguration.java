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
package eu.smartenit.unada.db.dto;

import java.io.Serializable;

/**
 * The WifiConfiguration class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public class WifiConfiguration implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8200954514548310661L;

	public WifiConfiguration() {
		
	}
	
	public WifiConfiguration(String sSID, String password) {
		this.SSID = sSID;
		this.password = password;
	}


	private String SSID;
	private String password;
	
	public String getSSID() {
		return SSID;
	}
	public void setSSID(String sSID) {
		SSID = sSID;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString() {
		return "WifiConfiguration [SSID=" + SSID + ", password=" + password
				+ "]";
	}

}
