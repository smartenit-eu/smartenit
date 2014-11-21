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
package eu.smartenit.sbox.web;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dao.CloudDCDAO;
import eu.smartenit.sbox.db.dao.DC2DCCommunicationDAO;
import eu.smartenit.sbox.db.dao.TunnelDAO;
import eu.smartenit.sbox.db.dto.CloudDC;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.DC2DCCommunicationID;
import eu.smartenit.sbox.db.dto.Direction;
import eu.smartenit.sbox.db.dto.EndAddressPairTunnelID;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.Tunnel;
import eu.smartenit.sbox.web.util.WebUtils;

/**
 * The InterdatacenterBean bean class.
 * 
 * It is the controlling class of the interdatacenter.xhtml page.
 * 
 * @author George Petropoulos
 * @version 1.2
 * 
 */
@ManagedBean
@ViewScoped
public class InterdatacenterBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory
			.getLogger(InterdatacenterBean.class);

	private CloudDCDAO cdao;
	
	private TunnelDAO tudao;

	private DC2DCCommunicationDAO dcdao;

	@PostConstruct
	public void init() {	
		cdao = new CloudDCDAO();
		tudao = new TunnelDAO();
		dcdao = new DC2DCCommunicationDAO();

		dc2dc = new DC2DCCommunication();
		dc2dcList = dcdao.findAll();

		cloudsList = cdao.findLocalClouds();
		remoteCloudsList = cdao.findRemoteClouds();
		localCloudsArray = WebUtils.listToArray(CloudDC.class, cloudsList);
		remoteCloudsArray = WebUtils.listToArray(CloudDC.class, remoteCloudsList);

		directionArray = new Direction[] { Direction.incomingTraffic,
				Direction.outcomingTraffic };

		tunnelsList = tudao.findAll();
		tunnelsArray = WebUtils.listToArray(Tunnel.class, tunnelsList);

		// map clouds to ases
		localCloudAses = cloudListToMap(cloudsList);
		if (cloudsList == null || cloudsList.size() == 0)
			dc2dc.getId().setLocalCloudDCName("");
		else
			dc2dc.getId().setLocalCloudDCName(
					cloudsList.get(0).getCloudDcName());
		changeLocalAS();

		remoteCloudAses = cloudListToMap(remoteCloudsList);
		if (remoteCloudsList == null || remoteCloudsList.size() == 0)
			dc2dc.getId().setRemoteCloudDCName("");
		else
			dc2dc.getId().setRemoteCloudDCName(
					remoteCloudsList.get(0).getCloudDcName());
		changeRemoteAS();

		selectedTunnels = new String[0];
		
		logger.debug("Navigating to interdatacenter page.");
	}

	public boolean editable = true;

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * Inter-datacenters-page relevant parameters and functions.
	 * 
	 */

	public DC2DCCommunication dc2dc;
	public List<DC2DCCommunication> dc2dcList;
	public String[] selectedTunnels;
	public Direction[] directionArray;
	public List<CloudDC> cloudsList;
	public CloudDC[] localCloudsArray;
	public List<CloudDC> remoteCloudsList;
	public CloudDC[] remoteCloudsArray;
	public List<Tunnel> tunnelsList;
	public Tunnel[] tunnelsArray;
	public Map<String, Integer> localCloudAses;
	public Map<String, Integer> remoteCloudAses;

	public List<CloudDC> getCloudsList() {
		return cloudsList;
	}

	public void setCloudsList(List<CloudDC> cloudsList) {
		this.cloudsList = cloudsList;
	}

	public List<CloudDC> getRemoteCloudsList() {
		return remoteCloudsList;
	}

	public void setRemoteCloudsList(List<CloudDC> remoteCloudsList) {
		this.remoteCloudsList = remoteCloudsList;
	}

	public List<Tunnel> getTunnelsList() {
		return tunnelsList;
	}

	public void setTunnelsList(List<Tunnel> tunnelsList) {
		this.tunnelsList = tunnelsList;
	}

	public Map<String, Integer> getLocalCloudAses() {
		return localCloudAses;
	}

	public void setLocalCloudAses(Map<String, Integer> localCloudAses) {
		this.localCloudAses = localCloudAses;
	}

	public Map<String, Integer> getRemoteCloudAses() {
		return remoteCloudAses;
	}

	public void setRemoteCloudAses(Map<String, Integer> remoteCloudAses) {
		this.remoteCloudAses = remoteCloudAses;
	}

	public DC2DCCommunication getDc2dc() {
		return dc2dc;
	}

	public void setDc2dc(DC2DCCommunication dc2dc) {
		this.dc2dc = dc2dc;
	}

	public List<DC2DCCommunication> getDc2dcList() {
		return dc2dcList;
	}

	public void setDc2dcList(List<DC2DCCommunication> dc2dcList) {
		this.dc2dcList = dc2dcList;
	}

	public String[] getSelectedTunnels() {
		return selectedTunnels;
	}

	public void setSelectedTunnels(String[] selectedTunnels) {
		this.selectedTunnels = selectedTunnels;
	}

	public Direction[] getDirectionArray() {
		return directionArray;
	}

	public void setDirectionArray(Direction[] directionArray) {
		this.directionArray = directionArray;
	}

	public CloudDC[] getLocalCloudsArray() {
		return localCloudsArray;
	}

	public void setLocalCloudsArray(CloudDC[] localCloudsArray) {
		this.localCloudsArray = localCloudsArray;
	}

	public CloudDC[] getRemoteCloudsArray() {
		return remoteCloudsArray;
	}

	public void setRemoteCloudsArray(CloudDC[] remoteCloudsArray) {
		this.remoteCloudsArray = remoteCloudsArray;
	}

	public Tunnel[] getTunnelsArray() {
		return tunnelsArray;
	}

	public void setTunnelsArray(Tunnel[] tunnelsArray) {
		this.tunnelsArray = tunnelsArray;
	}

	/**
	 * The method that inserts/updates a DC2DCCommunication to db.
	 * 
	 */
	public String updateDC2DC() {
		logger.debug("Updating DC2DC " + dc2dc.getId() + ".");
		try {
			dcdao.insert(dc2dc);
		} catch (Exception e) {
			logger.warn("Error while inserting DC-DC communication to database. "
					+ "Probably primary key already exists.");
		}
		EndAddressPairTunnelID tunnelID;
		for (int i = 0; i < selectedTunnels.length; i++) {
			/*tudao.updateByDC2DCCommunicationID(
					new SimpleTunnelID(selectedTunnels[i].split(",")[0],
							Integer.valueOf(selectedTunnels[i].split(",")[1])),
					dc2dc.getId());*/
			tunnelID = new EndAddressPairTunnelID(selectedTunnels[i].split(",")[0], 
					new NetworkAddressIPv4(selectedTunnels[i].split(",")[1], 32), 
					new NetworkAddressIPv4(selectedTunnels[i].split(",")[2], 32));
			tudao.updateByDC2DCCommunicationID(tunnelID, dc2dc.getId());
		}
		logger.debug("DC2DC " + dc2dc.getId() + " was successfully updated.");
		return "interdatacenter";
	}

	/**
	 * The method that edits a DC2DCCommunication.
	 * 
	 */
	public void editDC2DC(DC2DCCommunication dc) {
		logger.debug("Editing DC2DC " + dc.getId() + ".");
		dc2dc = dc;
	}

	/**
	 * The method that deletes a DC2DCCommunication from the db.
	 * 
	 */
	public String deleteDC2DC(DC2DCCommunication dc) {
		logger.debug("Deleting DC2DC " + dc.getId() + ".");
		List<Tunnel> dcTunnels = tudao.findAllByDC2DCCommunicationID(dc.getId());
		for (Tunnel t : dcTunnels) {
			tudao.updateByDC2DCCommunicationID(t.getTunnelID(), new DC2DCCommunicationID());		
		}
		dcdao.deleteById(dc.getId());
		logger.debug("DC2DC " + dc.getId() + " was successfully deleted.");
		return "interdatacenter";
	}

	/**
	 * The method that maps a specific local cloud name to the local AS number.
	 * 
	 */
	public void changeLocalAS() {
		logger.debug("Changing local as for local cloud name " + 
				dc2dc.getId().getLocalCloudDCName() + ".");
		if (dc2dc.getId().getLocalCloudDCName() != null
				&& !dc2dc.getId().getLocalCloudDCName().isEmpty())
			dc2dc.getId().setLocalAsNumber(
					localCloudAses.get(dc2dc.getId().getLocalCloudDCName()));
		else
			dc2dc.getId().setLocalAsNumber(0);
	}

	/**
	 * The method that maps a specific remote cloud name to the remote AS
	 * number.
	 * 
	 */
	public void changeRemoteAS() {
		logger.debug("Changing remote as for remote cloud name " + 
				dc2dc.getId().getRemoteCloudDCName() + ".");
		if (dc2dc.getId().getRemoteCloudDCName() != null
				&& !dc2dc.getId().getRemoteCloudDCName().isEmpty())
			dc2dc.getId().setRemoteAsNumber(
					remoteCloudAses.get(dc2dc.getId().getRemoteCloudDCName()));
		else
			dc2dc.getId().setRemoteAsNumber(0);
	}
	
	/**
	 * The method that transforms an arraylist of CloudDC objects to a map of
	 * <cloud name, as number>
	 * 
	 * @param cloudList
	 *            The arraylist of CloudDC objects
	 * 
	 * @return The map of <cloud name, as number>
	 * 
	 */
	public Map<String, Integer> cloudListToMap(List<CloudDC> cloudList) {
		logger.debug("Converting list of clouds into map.");
		Map<String, Integer> cloudMap = new HashMap<String, Integer>();
		if (cloudList != null) {
			for (CloudDC c : cloudList) {
				cloudMap.put(c.getCloudDcName(), c.getAs().getAsNumber());
			}
		}
		return cloudMap;
	}
	
}
