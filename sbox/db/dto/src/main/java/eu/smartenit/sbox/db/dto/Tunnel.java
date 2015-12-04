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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;

import eu.smartenit.sbox.db.dto.util.ClassNameAndIntSequenceGenerator;
/**
 * The Tunnel class.
 *
 * @author George Petropoulos
 * @author Jakub Gutkowski
 * @version 3.1
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
		this.tunnelID = new EndAddressPairTunnelID();
		this.link = new Link();
		this.localRouterAddress = new NetworkAddressIPv4();
	}

	/**
	 * The constructor with arguments.
	 *
	 * @param tunnelID
	 * @param link
	 * @param physicalLocalInterfaceName
	 * @param inboundInterfaceCounterOID
	 * @param outboundInterfaceCounterOID
	 * @param ofSwitchPortNumber
	 */
	public Tunnel(EndAddressPairTunnelID tunnelID, Link link,
			String physicalLocalInterfaceName,
			String inboundInterfaceCounterOID,
			String outboundInterfaceCounterOID, int ofSwitchPortNumber) {
		super();
		this.tunnelID = tunnelID;
		this.link = link;
		this.physicalLocalInterfaceName = physicalLocalInterfaceName;
		this.inboundInterfaceCounterOID = inboundInterfaceCounterOID;
		this.outboundInterfaceCounterOID = outboundInterfaceCounterOID;
		this.ofSwitchPortNumber = ofSwitchPortNumber;
	}
	

	private EndAddressPairTunnelID tunnelID;

	private Link link;
		
	private String physicalLocalInterfaceName;
	
	private String inboundInterfaceCounterOID;
	
	private String outboundInterfaceCounterOID;
	
	private int ofSwitchPortNumber;
	
	private NetworkAddressIPv4 localRouterAddress;
	
	/* Extended to support packet counters*/
	private String inboundInterfaceCounterOID_UcastPkts;
	private String outboundInterfaceCounterOID_UcastPkts;
	private String inboundInterfaceCounterOID_MulticastPkts;
	private String outboundInterfaceCounterOID_MulticastPkts;
	private String inboundInterfaceCounterOID_BroadcastPkts;
	private String outboundInterfaceCounterOID_BroadcastPkts;
	
	public EndAddressPairTunnelID getTunnelID() {
		return tunnelID;
	}

	public void setTunnelID(EndAddressPairTunnelID tunnelID) {
		this.tunnelID = tunnelID;
	}

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
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

	public int getOfSwitchPortNumber() {
		return ofSwitchPortNumber;
	}

	public void setOfSwitchPortNumber(int ofSwitchPortNumber) {
		this.ofSwitchPortNumber = ofSwitchPortNumber;
	}

	public NetworkAddressIPv4 getLocalRouterAddress() {
		return localRouterAddress;
	}

	public void setLocalRouterAddress(NetworkAddressIPv4 localRouterAddress) {
		this.localRouterAddress = localRouterAddress;
	}

	public String getInboundInterfaceCounterOID_UcastPkts() {
		return inboundInterfaceCounterOID_UcastPkts;
	}

	public void setInboundInterfaceCounterOID_UcastPkts(
			String inboundInterfaceCounterOID_UcastPkts) {
		this.inboundInterfaceCounterOID_UcastPkts = inboundInterfaceCounterOID_UcastPkts;
	}

	public String getOutboundInterfaceCounterOID_UcastPkts() {
		return outboundInterfaceCounterOID_UcastPkts;
	}

	public void setOutboundInterfaceCounterOID_UcastPkts(
			String outboundInterfaceCounterOID_UcastPkts) {
		this.outboundInterfaceCounterOID_UcastPkts = outboundInterfaceCounterOID_UcastPkts;
	}

	public String getInboundInterfaceCounterOID_MulticastPkts() {
		return inboundInterfaceCounterOID_MulticastPkts;
	}

	public void setInboundInterfaceCounterOID_MulticastPkts(
			String inboundInterfaceCounterOID_MulticastPkts) {
		this.inboundInterfaceCounterOID_MulticastPkts = inboundInterfaceCounterOID_MulticastPkts;
	}

	public String getOutboundInterfaceCounterOID_MulticastPkts() {
		return outboundInterfaceCounterOID_MulticastPkts;
	}

	public void setOutboundInterfaceCounterOID_MulticastPkts(
			String outboundInterfaceCounterOID_MulticastPkts) {
		this.outboundInterfaceCounterOID_MulticastPkts = outboundInterfaceCounterOID_MulticastPkts;
	}

	public String getInboundInterfaceCounterOID_BroadcastPkts() {
		return inboundInterfaceCounterOID_BroadcastPkts;
	}

	public void setInboundInterfaceCounterOID_BroadcastPkts(
			String inboundInterfaceCounterOID_BroadcastPkts) {
		this.inboundInterfaceCounterOID_BroadcastPkts = inboundInterfaceCounterOID_BroadcastPkts;
	}

	public String getOutboundInterfaceCounterOID_BroadcastPkts() {
		return outboundInterfaceCounterOID_BroadcastPkts;
	}

	public void setOutboundInterfaceCounterOID_BroadcastPkts(
			String outboundInterfaceCounterOID_BroadcastPkts) {
		this.outboundInterfaceCounterOID_BroadcastPkts = outboundInterfaceCounterOID_BroadcastPkts;
	}

	@Override
	public String toString() {
		return "Tunnel [tunnelID=" + tunnelID + ", link=" + link
				+ ", physicalLocalInterfaceName=" + physicalLocalInterfaceName
				+ ", inboundInterfaceCounterOID=" + inboundInterfaceCounterOID
				+ ", outboundInterfaceCounterOID="
				+ outboundInterfaceCounterOID + ", ofSwitchPortNumber="
				+ ofSwitchPortNumber + ", localRouterAddress="
				+ localRouterAddress
				+ ", inboundInterfaceCounterOID_UcastPkts="
				+ inboundInterfaceCounterOID_UcastPkts
				+ ", outboundInterfaceCounterOID_UcastPkts="
				+ outboundInterfaceCounterOID_UcastPkts
				+ ", inboundInterfaceCounterOID_MulticastPkts="
				+ inboundInterfaceCounterOID_MulticastPkts
				+ ", outboundInterfaceCounterOID_MulticastPkts="
				+ outboundInterfaceCounterOID_MulticastPkts
				+ ", inboundInterfaceCounterOID_BroadcastPkts="
				+ inboundInterfaceCounterOID_BroadcastPkts
				+ ", outboundInterfaceCounterOID_BroadcastPkts="
				+ outboundInterfaceCounterOID_BroadcastPkts + "]";
	}
	
}
