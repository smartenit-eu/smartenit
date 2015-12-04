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
package eu.smartenit.sbox.eca;

/**
 * Object that holds the intervals that make up an area.
 * 
 * @author D. Doenni, K. Bhargav, T. Bocek
 *
 */
class Area {

	/**
	 * First interval that makes up the area
	 */
	private final DInterval d1;
	
	/**
	 * Second interval that makes up the area
	 */
	private final DInterval d2;
	
	/**
	 * Creates an area object.
	 * 
	 * @param d1 First interval that makes up the area
	 * @param d2 Second interval that makes up the area
	 */
	public Area(DInterval d1, DInterval d2) {
		this.d1 = d1;
		this.d2 = d2;
	}

	/**
	 * Returns the first interval that makes up the area
	 * 
	 * @return The first interval that makes up the area
	 */
	public DInterval getD1() {
		return d1;
	}

	
	/**
	 * Returns the second interval that makes up the area
	 * 
	 * @return The second interval that makes up the area
	 */
	public DInterval getD2() {
		return d2;
	}


	@Override
	public String toString() {
		return "Area [d1=" + d1 + ", d2=" + d2 + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((d1 == null) ? 0 : d1.hashCode());
		result = prime * result + ((d2 == null) ? 0 : d2.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Area other = (Area) obj;
		if (d1 == null) {
			if (other.d1 != null)
				return false;
		} else if (!d1.equals(other.d1))
			return false;
		if (d2 == null) {
			if (other.d2 != null)
				return false;
		} else if (!d2.equals(other.d2))
			return false;
		return true;
	}
	
	
	
	
	
	
	
	
	
	
}
