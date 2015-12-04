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
import java.util.ArrayList;
import java.util.List;

/**
 * The Vector class.
 *
 * @author George Petropoulos
 * @version 1.2
 * 
 */
public class Vector implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor.
	 */
	public Vector() {
		
	}

	/**
	 * The constructor with arguments.
	 * 
	 * @param vectorValues
	 * @param sourceAsNumber
	 */
	public Vector(List<VectorValue> vectorValues, int sourceAsNumber) {
		
		this.vectorValues = vectorValues;
		this.sourceAsNumber = sourceAsNumber;
	}


	private List<VectorValue> vectorValues;
		
	private int sourceAsNumber;
	
	public List<VectorValue> getVectorValues() {
		return vectorValues;
	}

	public void setVectorValues(List<VectorValue> vectorValues) {
		this.vectorValues = vectorValues;
	}

	public int getSourceAsNumber() {
		return sourceAsNumber;
	}

	public void setSourceAsNumber(int sourceAsNumber) {
		this.sourceAsNumber = sourceAsNumber;
	}

	/**
	 * It returns the vector value for a tunnelEndPrefix.
	 * 
	 * @param tunnelEndPrefix The tunnel End Prefix
	 * @return The vector value. O if the tunnelEndPrefix does not exist.
	 *
	 */
	public long getVectorValueForTunnelEndPrefix(NetworkAddressIPv4 tunnelEndPrefix) {
		if (vectorValues != null) {
			for (VectorValue v : vectorValues) {
				if (tunnelEndPrefix.equals(v.getTunnelEndPrefix())) {
					return v.getValue();
				}
			}
		}
		return 0;
	}
	
	/**
	 * It adds the vector value with tunnelEndPrefix and value to the 
	 * list.
	 * 
	 * @param tunnelEndPrefix The tunnel End Prefix
	 * @param value The value
	 *
	 */
	public void addVectorValueForTunnelEndPrefix(NetworkAddressIPv4 tunnelEndPrefix, 
			long value) {
		if (vectorValues == null) {
			vectorValues = new ArrayList<VectorValue>();
		}
		vectorValues.add(new VectorValue(value, tunnelEndPrefix));
	}

	@Override
	public String toString() {
		return "Vector [vectorValues=" + vectorValues + ", sourceAsNumber="
				+ sourceAsNumber + "]";
	}
	
	

}
