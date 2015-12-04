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
 * The VectorValue class.
 *
 * @author George Petropoulos
 * @version 1.2
 * 
 */
public final class VectorValue implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor.
	 */
	public VectorValue() {
		
	}

	
	/**
	 * The constructor with arguments.
	 * 
	 * @param value
	 * @param tunnelEndPrefix
	 */
	public VectorValue(long value, NetworkAddressIPv4 tunnelEndPrefix) {
		super();
		this.value = value;
		this.tunnelEndPrefix = tunnelEndPrefix;
	}

	private long value;
	
	private NetworkAddressIPv4 tunnelEndPrefix;

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public NetworkAddressIPv4 getTunnelEndPrefix() {
		return tunnelEndPrefix;
	}

	public void setTunnelEndPrefix(NetworkAddressIPv4 tunnelEndPrefix) {
		this.tunnelEndPrefix = tunnelEndPrefix;
	}


	@Override
	public String toString() {
		return "VectorValue [value=" + value + ", tunnelEndPrefix="
				+ tunnelEndPrefix + "]";
	}

	

}
