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
package eu.smartenit.sbox.ntm.dtm.receiver;

import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.XVector;

/**
 * Used to store two related vector objects ({@link XVector} and {@link LocalRVector}) 
 * representing link traffic vector and reference vector, respectively.
 * 
 * @author Lukasz Lopatowski
 * @version 1.2
 * 
 */
public class XRVectorPair {
	
	private XVector xVector;
	private LocalRVector rVector;
	
	/**
	 * The constructor with arguments.
	 * 
	 * @param xVector
	 *            link traffic vector
	 * @param rVector
	 *            reference vector
	 */
	public XRVectorPair(XVector xVector, LocalRVector rVector) {
		this.xVector = xVector;
		this.rVector = rVector;
	}

	public XVector getXVector() {
		return xVector;
	}

	public void setXVector(XVector xVector) {
		this.xVector = xVector;
	}

	public LocalRVector getRVector() {
		return rVector;
	}

	public void setRVector(LocalRVector rVector) {
		this.rVector = rVector;
	}
	
	/**
	 * Checks whether both vectors comprising the pair are already set.
	 * 
	 * @return <code>true</code> if both vectors are not <code>null</code>
	 * 
	 */
	public boolean areBothVectorsSet() {
		return (xVector != null && rVector != null) ? true : false;
	}

	/**
	 * Returns the number of the AS for which both of the vectors were
	 * calculated. Note: Both vectors should have the same value of source AS
	 * number field.
	 * 
	 * @return number of the AS
	 */
	public int getAsNumber() {
		return xVector.getSourceAsNumber();
	}

}
