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
import java.util.List;

/**
 * The RVector class.
 *
 * @author George Petropoulos
 * @version 1.0
 * 
 */
public final class RVector extends Vector implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	
	/**
	 * The constructor.
	 */
	public RVector() {
		super();
	}

	/**
	 * The constructor with arguments.
	 * 
	 * @param vectorValues
	 * @param sourceAsNumber
	 * @param thetaCollection
	 */
	public RVector(List<VectorValue> vectorValues, int sourceAsNumber,
			List<ThetaCoefficient> thetaCollection) {
		super(vectorValues, sourceAsNumber);
		this.thetaCollection = thetaCollection;
	}


	private List<ThetaCoefficient> thetaCollection;

	public List<ThetaCoefficient> getThetaCollection() {
		return thetaCollection;
	}

	public void setThetaCollection(List<ThetaCoefficient> thetaCollection) {
		this.thetaCollection = thetaCollection;
	}

	@Override
	public String toString() {
		return "RVector [thetaCollection=" + thetaCollection
				+ ", getVectorValues()=" + getVectorValues()
				+ ", getSourceAsNumber()=" + getSourceAsNumber() + "]";
	}
	
}
