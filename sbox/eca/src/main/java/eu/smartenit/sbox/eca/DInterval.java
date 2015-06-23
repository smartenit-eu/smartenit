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
package eu.smartenit.sbox.eca;

/**
 * Represents an interval bounded by two alpha end points.
 * 
 * @author D. Doenni, K. Bhargav, T. Bocek
 *
 */
class DInterval {

	/**
	 * The lower bound of the interval
	 */
	private final long lowerBound;
	
	/**
	 * The upper bound of the interval
	 */
	private final long upperBound;
	
	/**
	 * The constructor with arguments.
	 * 
	 * @param lowerBound
	 *            The lower bound of the interval
	 * @param upperBound
	 *            The upper bound of the interval
	 */
	public DInterval(long lowerBound, long upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;		
	}
	
	/**
	 * Returns the lower bound of the interval
	 * @return The lower bound of the interval
	 */
	public long getLowerBound() {
		return lowerBound;
	}
	
	/**
	 * Returns the upper bound of the interval
	 * @return The upper bound of the interval
	 */
	public long getUpperBound() {
		return upperBound;
	}
	
	/**
	 * Returns true if the parameter value passed as an argument is contained in
	 * the two bounds (including boundaries)
	 * 
	 * @param x
	 *            The parameter to be checked for
	 * @return true if the parameter value passed as an argument is contained in
	 *         the two bounds (including boundaries)
	 */
	public boolean contains(long x) {
		if(x >= lowerBound && x <= upperBound) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "DInterval [lowerBound=" + lowerBound + ", upperBound=" + upperBound + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (lowerBound ^ (lowerBound >>> 32));
		result = prime * result + (int) (upperBound ^ (upperBound >>> 32));
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
		DInterval other = (DInterval) obj;
		if (lowerBound != other.lowerBound)
			return false;
		if (upperBound != other.upperBound)
			return false;
		return true;
	}
	
}
