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
package eu.smartenit.unada.db.dto;

import java.io.Serializable;

/**
 * The Location class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public class Location implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7718040806274986605L;
	
	public Location() {
		
	}
	

	public Location(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}


	private double latitude;
	private double longitude;
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	@Override
	public String toString() {
		return "Location [latitude=" + latitude + ", longitude=" + longitude
				+ "]";
	}
	
}
