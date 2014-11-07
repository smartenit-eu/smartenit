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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import eu.smartenit.sbox.db.dto.util.ClassNameAndIntSequenceGenerator;
/**
 * The Tunnel class.
 *
 * @author George Petropoulos
 * @version 1.0
 * 
 */
@JsonIdentityInfo(generator=ClassNameAndIntSequenceGenerator.class, scope=Tunnel.class, property="@id")
public final class Tunnel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor.
	 */
	public Tunnel() {
		this.tunnelID = new TunnelID();
		this.link = new Link();
		this.sourceEndAddress = new NetworkAddressIPv4();
		this.destinationEndAddress = new NetworkAddressIPv4();
	}

	/**
	 * The constructor with arguments.
	 * 
	 * @param tunnelID
	 * @param link
	 * @param sourceEndAddress
	 * @param destinationEndAddress
	 * @param physicalLocalInterfaceName
	 * @param inboundInterfaceCounterOID
	 * @param outboundInterfaceCounterOID
	 */
	public Tunnel(TunnelID tunnelID, Link link,
			NetworkAddressIPv4 sourceEndAddress,
			NetworkAddressIPv4 destinationEndAddress,
			String physicalLocalInterfaceName,
			String inboundInterfaceCounterOID,
			String outboundInterfaceCounterOID) {
		
		this.tunnelID = tunnelID;
		this.link = link;
		this.sourceEndAddress = sourceEndAddress;
		this.destinationEndAddress = destinationEndAddress;
		this.physicalLocalInterfaceName = physicalLocalInterfaceName;
		this.inboundInterfaceCounterOID = inboundInterfaceCounterOID;
		this.outboundInterfaceCounterOID = outboundInterfaceCounterOID;
	}

	@JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY, property="class")
	private TunnelID tunnelID;
	
	private Link link;
	
	private NetworkAddressIPv4 sourceEndAddress;
	
	private NetworkAddressIPv4 destinationEndAddress;
	
	private String physicalLocalInterfaceName;
	
	private String inboundInterfaceCounterOID;
	
	private String outboundInterfaceCounterOID;

	public TunnelID getTunnelID() {
		return tunnelID;
	}

	public void setTunnelID(TunnelID tunnelID) {
		this.tunnelID = tunnelID;
	}

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	public NetworkAddressIPv4 getSourceEndAddress() {
		return sourceEndAddress;
	}

	public void setSourceEndAddress(NetworkAddressIPv4 sourceEndAddress) {
		this.sourceEndAddress = sourceEndAddress;
	}

	public NetworkAddressIPv4 getDestinationEndAddress() {
		return destinationEndAddress;
	}

	public void setDestinationEndAddress(NetworkAddressIPv4 destinationEndAddress) {
		this.destinationEndAddress = destinationEndAddress;
	}

	public String getPhysicalLocalInterfaceName() {
		return physicalLocalInterfaceName;
	}

	public void setPhysicalLocalInterfaceName(String physicalLocalInterfaceName) {
		this.physicalLocalInterfaceName = physicalLocalInterfaceName;
	}

	public String getInboundInterfaceCounterOID() {
		return inboundInterfaceCounterOID;
	}

	public void setInboundInterfaceCounterOID(String inboundInterfaceCounterOID) {
		this.inboundInterfaceCounterOID = inboundInterfaceCounterOID;
	}

	public String getOutboundInterfaceCounterOID() {
		return outboundInterfaceCounterOID;
	}

	public void setOutboundInterfaceCounterOID(String outboundInterfaceCounterOID) {
		this.outboundInterfaceCounterOID = outboundInterfaceCounterOID;
	}

	@Override
	public String toString() {
		return "Tunnel [tunnelID=" + tunnelID 
				+ ", sourceEndAddress=" + sourceEndAddress
				+ ", destinationEndAddress=" + destinationEndAddress
				+ ", physicalLocalInterfaceName=" + physicalLocalInterfaceName
				+ ", inboundInterfaceCounterOID=" + inboundInterfaceCounterOID
				+ ", outboundInterfaceCounterOID="
				+ outboundInterfaceCounterOID + "]";
	}
	
}
