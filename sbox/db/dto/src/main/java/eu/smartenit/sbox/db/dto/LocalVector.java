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
 * The LocalVector class.
 *
 * @author George Petropoulos
 * @version 1.2
 * 
 */
public class LocalVector implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor.
	 */
	public LocalVector() {
		
	}

	/**
	 * The constructor with arguments.
	 * 
	 * @param vectorValues
	 * @param sourceAsNumber
	 */
	public LocalVector(List<LocalVectorValue> vectorValues, int sourceAsNumber) {
		
		this.vectorValues = vectorValues;
		this.sourceAsNumber = sourceAsNumber;
	}


	private List<LocalVectorValue> vectorValues;
		
	private int sourceAsNumber;
	
	public List<LocalVectorValue> getVectorValues() {
		return vectorValues;
	}

	public void setVectorValues(List<LocalVectorValue> vectorValues) {
		this.vectorValues = vectorValues;
	}

	public int getSourceAsNumber() {
		return sourceAsNumber;
	}

	public void setSourceAsNumber(int sourceAsNumber) {
		this.sourceAsNumber = sourceAsNumber;
	}

	/**
	 * It returns the vector value for a linkId.
	 * 
	 * @param linkId The link ID
	 * @return The vector value. O if the linkId does not exist.
	 *
	 */
	public long getVectorValueForLink(LinkID linkId) {
		if (vectorValues != null) {
			for (LocalVectorValue v : vectorValues) {
				if (v.getLinkID().equals(linkId)) {
					return v.getValue();
				}
			}
		}
		return 0;
	}
	
	/**
	 * It adds the vector value with linkId and value to the 
	 * list.
	 * 
	 * @param linkId The link ID
	 * @param value The value
	 *
	 */
	public void addVectorValueForLink(LinkID linkId, long value) {
		if (vectorValues == null) {
			vectorValues = new ArrayList<LocalVectorValue>();
		}
		vectorValues.add(new LocalVectorValue(value, linkId));
	}

	@Override
	public String toString() {
		return "LocalVector [vectorValues=" + vectorValues + ", sourceAsNumber="
				+ sourceAsNumber + "]";
	}
	
	

}
