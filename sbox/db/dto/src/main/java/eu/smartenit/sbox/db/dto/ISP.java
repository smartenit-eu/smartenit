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
import java.util.ArrayList;
import java.util.List;

/**
 * The ISP class.
 *
 * @author George Petropoulos
 * @version 1.0
 * 
 */
public final class ISP implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor.
	 */
	public ISP() {
		this.asList = new ArrayList<AS>();
	}

	/**
	 * The constructor with arguments.
	 * 
	 * @param ispName
	 * @param asList
	 */
	public ISP(String ispName, List<AS> asList) {

		this.ispName = ispName;
		this.asList = asList;
	}

	private String ispName;
	
	private List<AS> asList;

	public String getIspName() {
		return ispName;
	}

	public void setIspName(String ispName) {
		this.ispName = ispName;
	}

	public List<AS> getAsList() {
		return asList;
	}

	public void setAsList(List<AS> asList) {
		this.asList = asList;
	}
	
	public void addAs(AS as) {
		if (this.asList == null) 
			this.asList = new ArrayList<AS>();
		if (this.asList.contains(as))
			this.asList.set(this.asList.indexOf(as), as);
		else
			this.asList.add(as);
	}
	
	public void deleteAs(AS as) {
		if (this.asList != null && this.asList.contains(as)) 
			this.asList.remove(as);
	}

	@Override
	public String toString() {
		return "ISP [ispName=" + ispName + ", asList=" + asList + "]";
	}
	
	
	
}
