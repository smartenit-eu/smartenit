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
import java.util.Date;

/**
 * The TrustedUser class.
 * 
 * @authors George Petropoulos
 * @version 2.1
 * 
 */
public class TrustedUser implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2723905020920003189L;

	public TrustedUser() {
		this.lastAccess = new Date();
	}
	
	private String facebookID;
	private String macAddress;
    private Date lastAccess;
	
	public String getFacebookID() {
		return facebookID;
	}
	public void setFacebookID(String facebookID) {
		this.facebookID = facebookID;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
    public Date getLastAccess() {
        return lastAccess;
    }
    public void setLastAccess(Date lastAccess) {
        this.lastAccess = lastAccess;
    }

    @Override
    public String toString() {
        return "TrustedUser{" +
                "facebookID='" + facebookID + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", lastAccess=" + lastAccess +
                '}';
    }


}
