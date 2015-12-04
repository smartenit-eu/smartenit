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
 * The SimpleLinkID class.
 * 
 * @author George Petropoulos
 * @version 1.0
 * 
 */
public final class SimpleLinkID extends LinkID implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor.
	 */
	public SimpleLinkID() {

		super();
	}

	/**
	 * The constructor with arguments.
	 * 
	 * @param localLinkID
	 * @param localIspName
	 */
	public SimpleLinkID(String localLinkID, String localIspName) {

		super();
		this.localLinkID = localLinkID;
		this.localIspName = localIspName;
	}

	private String localLinkID;

	private String localIspName;

	public String getLocalLinkID() {
		return localLinkID;
	}

	public void setLocalLinkID(String localLinkID) {
		this.localLinkID = localLinkID;
	}

	public String getLocalIspName() {
		return localIspName;
	}

	public void setLocalIspName(String localIspName) {
		this.localIspName = localIspName;
	}

	@Override
	public String toString() {
		return "SimpleLinkID [localLinkID=" + localLinkID + ", localIspName="
				+ localIspName + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((localIspName == null) ? 0 : localIspName.hashCode());
		result = prime * result
				+ ((localLinkID == null) ? 0 : localLinkID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SimpleLinkID))
			return false;
		SimpleLinkID other = (SimpleLinkID) obj;
		if (localIspName == null) {
			if (other.localIspName != null)
				return false;
		} else if (!localIspName.equals(other.localIspName))
			return false;
		if (localLinkID == null) {
			if (other.localLinkID != null)
				return false;
		} else if (!localLinkID.equals(other.localLinkID))
			return false;
		return true;
	}
	
	
	
}
