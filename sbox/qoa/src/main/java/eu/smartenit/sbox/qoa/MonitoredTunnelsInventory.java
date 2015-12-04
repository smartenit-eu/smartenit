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
package eu.smartenit.sbox.qoa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.DC2DCCommunicationID;
import eu.smartenit.sbox.db.dto.Direction;
import eu.smartenit.sbox.db.dto.Tunnel;
import eu.smartenit.sbox.db.dto.TunnelID;

/**
 * Used to store information about tunnels. Provides specific query methods to
 * retrieve information.
 * 
 * @author Jakub Gutkowski
 * @author Lukasz Lopatowski
 * @version 3.1
 * 
 */
public class MonitoredTunnelsInventory {
	
	private Map<Integer, Set<DARouter>> asDARouters = new HashMap<Integer, Set<DARouter>>();

	private List<DC2DCCommunication> communications = new ArrayList<DC2DCCommunication>();
	
	/**
	 * Method to be called to populate inventory with monitored tunnels
	 * information that is obtained from list of {@link DC2DCCommunication}s
	 * terminated in local ASs.
	 * 
	 * @param communications
	 *            list of {@link DC2DCCommunication} instances carrying
	 *            information about DA routers and tunnels in local ASs
	 */
	public void populate(List<DC2DCCommunication> communications) {
		for(DC2DCCommunication communication : communications) {
			if (communication.getTrafficDirection().equals(Direction.incomingTraffic))
				this.communications.add(communication);
		}
		
		for(DC2DCCommunication communication : this.communications) { 
			updateDARouters(communication.getLocalCloud().getAs(), communication.getLocalCloud().getDaRouter());
		}
	}
	
	/**
	 * Retrieves all monitored tunnels from given local AS.
	 * 
	 * @param asNumber
	 *            number of the local AS
	 * @return list of {@link Tunnel} instances
	 */
	public List<Tunnel> getTunnels(int asNumber) {
		final List<Tunnel> tunnels = new ArrayList<Tunnel>();
		for(DC2DCCommunication communication : communications) {
			if (communication.getLocalCloud().getAs().getAsNumber() == asNumber)
				tunnels.addAll(communication.getConnectingTunnels());
		}
		return tunnels;
	}
	
	/**
	 * Retrieves {@link Tunnel} instance for given {@link TunnelID}.
	 * 
	 * @param tunnelID
	 *            identifier of the tunnel
	 * @param asNumber
	 *            number of the local AS
	 * @return {@link Tunnel} instance
	 */
	public Tunnel getTunnel(TunnelID tunnelID, int asNumber) {
		for(Tunnel tunnel : getTunnels(asNumber)) {
			if (tunnel.getTunnelID().equals(tunnelID))
				return tunnel;
		}
		return null;
	}
	
	/**
	 * Retrieves all monitored tunnels from given DA router.
	 * 
	 * @param daRouter
	 *            {@link DARouter} instance
	 * @return list of {@link Tunnel} instances
	 */
	public List<Tunnel> getTunnels(DARouter daRouter) {
		List<Tunnel> tunnels = new ArrayList<Tunnel>();
		for (Tunnel tunnel : getAllTunnels()) {
			if(daRouter.getManagementAddress() != null 
					&& daRouter.getManagementAddress().getPrefix().equals(tunnel.getLocalRouterAddress().getPrefix())) {
				tunnels.add(tunnel);
			}
		}
		return tunnels;
	}
	
	/**
	 * Retrieves all tunnels from inter-DC communication with given identifier.
	 * 
	 * @param id
	 *            {@link DC2DCCommunicationID} instance
	 * @return list of {@link Tunnel} instances
	 */
	public List<Tunnel> getTunnels(DC2DCCommunicationID id) {
		for(DC2DCCommunication communication : communications) {
			if (communication.getId().equals(id))
				return communication.getConnectingTunnels();
		}
		return null;
	}
	
	/**
	 * Retrieves all DA routers stored in the inventory.
	 * 
	 * @return list of {@link DARouter} instances
	 */
	public List<DARouter> getDARouters() {
		List<DARouter> daRouters = new ArrayList<DARouter>();
		for(Integer asNumber : asDARouters.keySet())
			daRouters.addAll(asDARouters.get(asNumber));
		return daRouters;
	}

	/**
	 * Retrieves all DA routers from given local AS.
	 * 
	 * @param asNumber
	 *            number of the local AS
	 * @return list of {@link DARouter} instances
	 */
	public List<DARouter> getDARoutersByAsNumber(int asNumber) {
		return new ArrayList<DARouter>(asDARouters.get(asNumber));
	}
	
	/**
	 * Retrieves identifiers of all inter-DC communications terminating in given
	 * local AS.
	 * 
	 * @param asNumber
	 *            number of the local AS
	 * @return list of {@link DC2DCCommunicationID} instances
	 */
	public List<DC2DCCommunicationID> getAllDC2DCCommunicationIDs(int asNumber) {
		List<DC2DCCommunicationID> ids = new ArrayList<DC2DCCommunicationID>();		
		for(DC2DCCommunication communication : communications) {
			if (communication.getLocalCloud().getAs().getAsNumber() == asNumber)
				ids.add(communication.getId());
		}
		return ids;
	}
	
	/**
	 * Retrieves numbers of all local ASs stored in the inventory.
	 * 
	 * @return list of AS numbers
	 */
	public Set<Integer> getAllAsNumbers() {
		Set<Integer> asNumbers = new HashSet<Integer>();		
		for(DC2DCCommunication communication : communications) {
			asNumbers.add(communication.getLocalCloud().getAs().getAsNumber());
		}
		return asNumbers;
	}

	private void updateDARouters(AS as, DARouter daRouter) {
		int asNumber = as.getAsNumber();
		if (asDARouters.containsKey(asNumber)) {
			asDARouters.get(asNumber).add(daRouter);
		} else {
			asDARouters.put(asNumber, new HashSet<DARouter>(Arrays.asList(daRouter)));
		}
	}
	
	/**
	 * Retrieves all monitored tunnels from given BG router.
	 * 
	 * @param bgRouter
	 *            {@link BGRouter} instance
	 * @return list of {@link Tunnel} instances
	 */
	public List<Tunnel> getTunnels(BGRouter bgRouter) {
		List<Tunnel> tunnels = new ArrayList<Tunnel>();
		for (Tunnel tunnel : getAllTunnels()) {
			if(bgRouter.getManagementAddress() != null 
					&& bgRouter.getManagementAddress().getPrefix().equals(tunnel.getLocalRouterAddress().getPrefix())) {
				tunnels.add(tunnel);
			}
		}
		return tunnels;
	}
	
	protected List<Tunnel> getAllTunnels() {
		final List<Tunnel> allTunnels = new ArrayList<Tunnel>();
		for(DC2DCCommunication communication : this.communications) { 
				allTunnels.addAll(communication.getConnectingTunnels());
		}
		return allTunnels;
	}
}
