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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;

import eu.smartenit.sbox.db.dto.util.ClassNameAndIntSequenceGenerator;

/**
 * The AS class.
 *
 * @author George Petropoulos
 * @version 1.0
 * 
 */
@JsonIdentityInfo(generator=ClassNameAndIntSequenceGenerator.class, scope=AS.class, property="@id")
public final class AS implements Serializable{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor.
	 */
	public AS() {
		this.sbox = new SBox();
	}
	

	/**
	 * The constructor with arguments.
	 * 
	 * @param asNumber
	 * @param local
	 * @param bgRouters
	 * @param communicationList
	 * @param sbox
	 * @param localClouds
	 */
	public AS(int asNumber, boolean local, List<BGRouter> bgRouters,
			List<DC2DCCommunicationID> communicationList, SBox sbox,
			List<CloudDC> localClouds) {
		
		this.asNumber = asNumber;
		this.local = local;
		this.bgRouters = bgRouters;
		this.sbox = sbox;
		this.localClouds = localClouds;
	}

	private int asNumber = 0;
	
	private boolean local;
	
	private List<BGRouter> bgRouters;
		
	private SBox sbox;
	
	private List<CloudDC> localClouds;
	

	public int getAsNumber() {
		return asNumber;
	}


	public void setAsNumber(int asNumber) {
		this.asNumber = asNumber;
	}


	public List<BGRouter> getBgRouters() {
		return bgRouters;
	}


	public void setBgRouters(List<BGRouter> bgRouters) {
		this.bgRouters = bgRouters;
	}


	public SBox getSbox() {
		return sbox;
	}


	public void setSbox(SBox sbox) {
		this.sbox = sbox;
	}


	public List<CloudDC> getLocalClouds() {
		return localClouds;
	}


	public void setLocalClouds(List<CloudDC> localClouds) {
		this.localClouds = localClouds;
	}


	public boolean isLocal() {
		return local;
	}


	public void setLocal(boolean local) {
		this.local = local;
	}


	@Override
	public String toString() {
		return "AS [asNumber=" + asNumber + ", local=" + local + ", bgRouters="
				+ bgRouters + ", sbox=" + sbox + ", localClouds=" + localClouds
				+ "]";
	}

	
}
