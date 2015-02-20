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
 * The RemoteLoginReply class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public class RemoteLoginReply implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4909372873727913732L;

	public RemoteLoginReply() {
		
	}
	
	private String type;
    private int outcome;
    private WifiConfiguration wifiConfiguration;

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getOutcome() {
		return outcome;
	}
	public void setOutcome(int outcome) {
		this.outcome = outcome;
	}
	public WifiConfiguration getWifiConfiguration() {
		return wifiConfiguration;
	}
	public void setWifiConfiguration(WifiConfiguration wifiConfiguration) {
		this.wifiConfiguration = wifiConfiguration;
	}
	
	@Override
	public String toString() {
		return "RemoteLoginReply [type=" + type + ", outcome=" + outcome
				+ ", wifiConfiguration=" + wifiConfiguration + "]";
	}
    
    

}
