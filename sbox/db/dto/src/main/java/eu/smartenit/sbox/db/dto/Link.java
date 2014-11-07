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
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import eu.smartenit.sbox.db.dto.util.ClassNameAndIntSequenceGenerator;

/**
 * The Link class.
 * 
 * @author George Petropoulos
 * @version 1.0
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
	}

	/**
	 * The constructor with arguments.
	 * 
	 * @param linkID
	 * @param address
	 * @param physicalInterfaceName
	 * @param vlan
	 * @param inboundInterfaceCounterOID
	 * @param outboundInterfaceCounterOID
	 * @param costFunction
	 * @param bgRouter
	 * @param traversingTunnels
	 */
	public Link(LinkID linkID, NetworkAddressIPv4 address,
			String physicalInterfaceName, int vlan,
			String inboundInterfaceCounterOID,
			String outboundInterfaceCounterOID, CostFunction costFunction,
			BGRouter bgRouter, List<Tunnel> traversingTunnels) {

		this.linkID = linkID;
		this.address = address;
		this.physicalInterfaceName = physicalInterfaceName;
		this.vlan = vlan;
		this.inboundInterfaceCounterOID = inboundInterfaceCounterOID;
		this.outboundInterfaceCounterOID = outboundInterfaceCounterOID;
		this.costFunction = costFunction;
		this.bgRouter = bgRouter;
		this.traversingTunnels = traversingTunnels;
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

	@Override
	public String toString() {
		return "Link [linkID=" + linkID + ", address=" + address
				+ ", physicalInterfaceName=" + physicalInterfaceName
				+ ", vlan=" + vlan + ", inboundInterfaceCounterOID="
				+ inboundInterfaceCounterOID + ", outboundInterfaceCounterOID="
				+ outboundInterfaceCounterOID + ", costFunction="
				+ costFunction + ", traversingTunnels=" + traversingTunnels
				+ "]";
	}

}
