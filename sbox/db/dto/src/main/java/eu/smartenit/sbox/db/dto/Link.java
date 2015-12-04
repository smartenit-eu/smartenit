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
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import eu.smartenit.sbox.db.dto.util.ClassNameAndIntSequenceGenerator;

/**
 * The Link class.
 * 
 * @author George Petropoulos
 * @author Jakub Gutkowski
 * @version 1.2
 * 
 */
@JsonIdentityInfo(generator=ClassNameAndIntSequenceGenerator.class, scope=Link.class, property="@id")
public final class Link implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor.
	 */
	public Link() {
		this.linkID = new LinkID();
		this.address = new NetworkAddressIPv4();
		this.costFunction = new CostFunction();
		this.bgRouter = new BGRouter();
		this.tunnelEndPrefix = new NetworkAddressIPv4();
		this.policerBandwidthLimitFactor = 1.0;
		this.vlan = 0;
		this.aggregateLeakageFactor = 0;
	}


	/**
	 * @param linkID
	 * @param address
	 * @param physicalInterfaceName
	 * @param vlan
	 * @param inboundInterfaceCounterOID
	 * @param outboundInterfaceCounterOID
	 * @param costFunction
	 * @param bgRouter
	 * @param traversingTunnels
	 * @param tunnelEndPrefix
	 */
	public Link(LinkID linkID, NetworkAddressIPv4 address,
			String physicalInterfaceName, int vlan,
			String inboundInterfaceCounterOID,
			String outboundInterfaceCounterOID, CostFunction costFunction,
			BGRouter bgRouter, List<Tunnel> traversingTunnels,
			NetworkAddressIPv4 tunnelEndPrefix) {
		super();
		this.linkID = linkID;
		this.address = address;
		this.physicalInterfaceName = physicalInterfaceName;
		this.vlan = vlan;
		this.inboundInterfaceCounterOID = inboundInterfaceCounterOID;
		this.outboundInterfaceCounterOID = outboundInterfaceCounterOID;
		this.costFunction = costFunction;
		this.bgRouter = bgRouter;
		this.traversingTunnels = traversingTunnels;
		this.tunnelEndPrefix = tunnelEndPrefix;
		this.policerBandwidthLimitFactor = 1.0;
		this.vlan = 0;
		this.aggregateLeakageFactor = 0;
	}



	@JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY, property="class")
	private LinkID linkID;

	private NetworkAddressIPv4 address;

	private String physicalInterfaceName;

	private int vlan;

	private String inboundInterfaceCounterOID;

	private String outboundInterfaceCounterOID;

	private CostFunction costFunction;

	private BGRouter bgRouter;

	private List<Tunnel> traversingTunnels;
	
	private NetworkAddressIPv4 tunnelEndPrefix;
	
	private double policerBandwidthLimitFactor;
	
	private String filterInterfaceName;
	
	private double aggregateLeakageFactor;
	
	/* Extended to support packet counters*/
	private String inboundInterfaceCounterOID_UcastPkts;
	private String outboundInterfaceCounterOID_UcastPkts;
	private String inboundInterfaceCounterOID_MulticastPkts;
	private String outboundInterfaceCounterOID_MulticastPkts;
	private String inboundInterfaceCounterOID_BroadcastPkts;
	private String outboundInterfaceCounterOID_BroadcastPkts;

	public LinkID getLinkID() {
		return linkID;
	}

	public void setLinkID(LinkID linkID) {
		this.linkID = linkID;
	}

	public NetworkAddressIPv4 getAddress() {
		return address;
	}

	public void setAddress(NetworkAddressIPv4 address) {
		this.address = address;
	}

	public String getPhysicalInterfaceName() {
		return physicalInterfaceName;
	}

	public void setPhysicalInterfaceName(String physicalInterfaceName) {
		this.physicalInterfaceName = physicalInterfaceName;
	}

	public int getVlan() {
		return vlan;
	}

	public void setVlan(int vlan) {
		this.vlan = vlan;
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

	public void setOutboundInterfaceCounterOID(
			String outboundInterfaceCounterOID) {
		this.outboundInterfaceCounterOID = outboundInterfaceCounterOID;
	}

	public CostFunction getCostFunction() {
		return costFunction;
	}

	public void setCostFunction(CostFunction costFunction) {
		this.costFunction = costFunction;
	}

	public BGRouter getBgRouter() {
		return bgRouter;
	}

	public void setBgRouter(BGRouter bgRouter) {
		this.bgRouter = bgRouter;
	}

	public List<Tunnel> getTraversingTunnels() {
		return traversingTunnels;
	}

	public void setTraversingTunnels(List<Tunnel> traversingTunnels) {
		this.traversingTunnels = traversingTunnels;
	}

	public NetworkAddressIPv4 getTunnelEndPrefix() {
		return tunnelEndPrefix;
	}

	public void setTunnelEndPrefix(NetworkAddressIPv4 tunnelEndPrefix) {
		this.tunnelEndPrefix = tunnelEndPrefix;
	}
	
	public double getPolicerBandwidthLimitFactor() {
		return policerBandwidthLimitFactor;
	}

	public void setPolicerBandwidthLimitFactor(double policerBandwidthLimitFactor) {
		this.policerBandwidthLimitFactor = policerBandwidthLimitFactor;
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
	
	public String getFilterInterfaceName() {
		return filterInterfaceName;
	}

	public void setFilterInterfaceName(String filterInterfaceName) {
		this.filterInterfaceName = filterInterfaceName;
	}

	public double getAggregateLeakageFactor() {
		return aggregateLeakageFactor;
	}

	public void setAggregateLeakageFactor(double aggregateLeakageFactor) {
		this.aggregateLeakageFactor = aggregateLeakageFactor;
	}


	@Override
	public String toString() {
		return "Link [linkID=" + linkID + ", address=" + address
				+ ", physicalInterfaceName=" + physicalInterfaceName
				+ ", vlan=" + vlan + ", inboundInterfaceCounterOID="
				+ inboundInterfaceCounterOID + ", outboundInterfaceCounterOID="
				+ outboundInterfaceCounterOID + ", costFunction="
				+ costFunction + ", bgRouter=" + bgRouter
				+ ", traversingTunnels=" + traversingTunnels
				+ ", tunnelEndPrefix=" + tunnelEndPrefix
				+ ", policerBandwidthLimitFactor="
				+ policerBandwidthLimitFactor + ", filterInterfaceName="
				+ filterInterfaceName + ", aggregateLeakageFactor="
				+ aggregateLeakageFactor
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
