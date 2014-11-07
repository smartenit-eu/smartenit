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
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;

import eu.smartenit.sbox.db.dto.util.ClassNameAndIntSequenceGenerator;

/**
 * The BGRouter class.
 *
 * @author George Petropoulos
 * @version 1.0
 * 
 */
@JsonIdentityInfo(generator=ClassNameAndIntSequenceGenerator.class, scope=BGRouter.class, property="@id")
public final class BGRouter implements Serializable {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor.
	 */
	public BGRouter() {
		this.managementAddress = new NetworkAddressIPv4();
	}

	/**
	 * The constructor with arguments.
	 * 
	 * @param managementAddress
	 * @param snmpCommunity
	 * @param interDomainLinks
	 */
	public BGRouter(NetworkAddressIPv4 managementAddress, String snmpCommunity,
			List<Link> interDomainLinks) {
		
		this.managementAddress = managementAddress;
		this.snmpCommunity = snmpCommunity;
		this.interDomainLinks = interDomainLinks;
	}

	private NetworkAddressIPv4 managementAddress;
	
	private String snmpCommunity;
	
	private List<Link> interDomainLinks;

	public NetworkAddressIPv4 getManagementAddress() {
		return managementAddress;
	}

	public void setManagementAddress(NetworkAddressIPv4 managementAddress) {
		this.managementAddress = managementAddress;
	}

	public List<Link> getInterDomainLinks() {
		return interDomainLinks;
	}

	public void setInterDomainLinks(List<Link> interDomainLinks) {
		this.interDomainLinks = interDomainLinks;
	}

	public String getSnmpCommunity() {
		return snmpCommunity;
	}

	public void setSnmpCommunity(String snmpCommunity) {
		this.snmpCommunity = snmpCommunity;
	}

	@Override
	public String toString() {
		return "BGRouter [managementAddress=" + managementAddress
				+ ", snmpCommunity=" + snmpCommunity + ", interDomainLinks="
				+ interDomainLinks + "]";
	}
	
	
}
