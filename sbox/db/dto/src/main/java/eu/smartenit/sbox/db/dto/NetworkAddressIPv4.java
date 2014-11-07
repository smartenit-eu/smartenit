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
 * The NetworkAddressIPv4 class.
 *
 * @author George Petropoulos
 * @version 1.0
 * 
 */
public final class NetworkAddressIPv4 implements Serializable{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor.
	 */
	public NetworkAddressIPv4() {

	}

	/**
	 * The constructor with arguments.
	 * 
	 * @param prefix
	 * @param prefixLength
	 */
	public NetworkAddressIPv4(String prefix, int prefixLength) {
		
		this.prefix = prefix;
		this.prefixLength = prefixLength;
	}

	private String prefix;
	
	private int prefixLength = 0;

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public int getPrefixLength() {
		return prefixLength;
	}

	public void setPrefixLength(int prefixLength) {
		this.prefixLength = prefixLength;
	}

	@Override
	public String toString() {
		return "NetworkAddressIPv4 [prefix=" + prefix + ", prefixLength="
				+ prefixLength + "]";
	}
	
	

}
