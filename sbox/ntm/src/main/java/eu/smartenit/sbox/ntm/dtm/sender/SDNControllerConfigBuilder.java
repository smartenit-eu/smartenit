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
package eu.smartenit.sbox.ntm.dtm.sender;

import java.util.ArrayList;
import java.util.List;

import eu.smartenit.sbox.db.dto.ConfigData;
import eu.smartenit.sbox.db.dto.ConfigDataEntry;
import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.Direction;
import eu.smartenit.sbox.db.dto.LocalDCOfSwitchPorts;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SDNController;
import eu.smartenit.sbox.db.dto.Tunnel;
import eu.smartenit.sbox.db.dto.TunnelInfo;
import eu.smartenit.sbox.ntm.dtm.DAOFactory;

/**
 * Prepares {@link ConfigData} specific for given {@link SDNController} based on
 * {@link DC2DCCommunication}s stored in the data base.
 * 
 * @author Lukasz Lopatowski
 * @version 3.0
 */
public class SDNControllerConfigBuilder {
	
	/**
	 * Method prepares configuration data for given SDN Controller.
	 * 
	 * @param controller
	 *            SDN Controller for which configuration data is prepared
	 * @return configuration data for given SDN controller
	 */
	public static ConfigData prepareConfig(SDNController controller) {
		List<ConfigDataEntry> entries = new ArrayList<>();
		for (DC2DCCommunication communication : communications(controller)) {
			for (NetworkAddressIPv4 dcNetwork : communication.getRemoteCloud().getDcNetworks())
				updateOrAddNewEntry(entries, communication, dcNetwork);
		}
		
		ConfigData configData = new ConfigData();
		configData.setOperationModeSDN(DAOFactory.getSCPDAOInstance().findLast().getOperationModeSDN());
		
		List<LocalDCOfSwitchPorts> localDCPortsConfig = new ArrayList<>();
		for (DARouter daRouter : controller.getDaRouters()) { 
			LocalDCOfSwitchPorts dcPorts = new LocalDCOfSwitchPorts(daRouter.getOfSwitchDPID(), daRouter.getLocalDCOfSwitchPortNumbers());
			localDCPortsConfig.add(dcPorts);
		}
		configData.setLocalDCPortsConfig(localDCPortsConfig);
		configData.setEntries(entries);
		return configData;
	}

	private static void updateOrAddNewEntry(List<ConfigDataEntry> entries, DC2DCCommunication communication, NetworkAddressIPv4 dcNetwork) {
		ConfigDataEntry entry = null;
		if ((entry = getEntryIfAlreadyOnTheList(dcNetwork, communication.getLocalCloud().getDaRouter().getOfSwitchDPID(), entries)) == null) {
			entry = new ConfigDataEntry();
			entry.setRemoteDcPrefix(dcNetwork);
			entry.setDaRouterOfDPID(communication.getLocalCloud().getDaRouter().getOfSwitchDPID());
			entry.setTunnels(new ArrayList<TunnelInfo>());
			entries.add(entry);
		}
		
		List<TunnelInfo> tunnels = entry.getTunnels();
		for(Tunnel tunnel : communication.getConnectingTunnels()) {
			TunnelInfo newTunnelInfo = new TunnelInfo(tunnel.getTunnelID(), tunnel.getOfSwitchPortNumber()); 
			if (!isAlreadyOnList(newTunnelInfo, tunnels))	
				tunnels.add(newTunnelInfo);
		}
		entry.setTunnels(tunnels);
	}

	private static List<DC2DCCommunication> communications(SDNController controller) {
		List<DC2DCCommunication> communications = new ArrayList<DC2DCCommunication>();
		for(DC2DCCommunication communication : DAOFactory.getDCDC2DCComDAOInstance().findAllDC2DCCommunicationsCloudsTunnels()) {
			if(communication.getTrafficDirection().equals(Direction.outcomingTraffic) 
					&& communication.getLocalCloud().getSdnController().getManagementAddress().equals(controller.getManagementAddress()))
				communications.add(communication);
		}
		return communications;
	}
	
	private static ConfigDataEntry getEntryIfAlreadyOnTheList(NetworkAddressIPv4 dcNetwork, String dpid, List<ConfigDataEntry> entries) {
		for(ConfigDataEntry entry : entries) {
			if (entry.getRemoteDcPrefix().equals(dcNetwork) 
					&& entry.getDaRouterOfDPID().equals(dpid))
				return entry;
		}
		return null;
	}
	
	private static boolean isAlreadyOnList(TunnelInfo newTunnelInfo, List<TunnelInfo> tunnels) {
		for(TunnelInfo tunnelInfo : tunnels)
			if (tunnelInfo.getTunnelID().equals(newTunnelInfo.getTunnelID())
					&& tunnelInfo.getDaRouterOfPortNumber() == newTunnelInfo.getDaRouterOfPortNumber())
				return true;
		
		return false;
	}

}
