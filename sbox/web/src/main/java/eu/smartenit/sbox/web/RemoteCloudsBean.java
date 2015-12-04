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
package eu.smartenit.sbox.web;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dao.ASDAO;
import eu.smartenit.sbox.db.dao.CloudDCDAO;
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.CloudDC;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.web.util.WebUtils;

/**
 * The RemoteCloudsBean bean class.
 * 
 * It is the controlling class of the remoteclouds.xhtml page.
 * 
 * @author George Petropoulos
 * @version 1.2
 * 
 */
@ManagedBean
@ViewScoped
public class RemoteCloudsBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory
			.getLogger(RemoteCloudsBean.class);

	private CloudDCDAO cdao;
	
	private ASDAO asdao;
	

	@PostConstruct
	public void init() {	
		cdao = new CloudDCDAO();
		asdao = new ASDAO();

		remoteCloud = new CloudDC();
		remoteCloudsList = cdao.findRemoteClouds();

		asList = asdao.findRemoteAs();
		asArray = WebUtils.listToArray(AS.class, asList);
		network = new NetworkAddressIPv4();
		editable = true;
		insertRemoteCloud = true;
		
		logger.debug("Navigating to remoteclouds page.");
	}

	public boolean editable = true;

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * Remote Clouds-page relevant parameters and functions.
	 * 
	 */
	public CloudDC remoteCloud;
	public List<CloudDC> remoteCloudsList;
	public NetworkAddressIPv4 network;
	public List<AS> asList;
	public AS[] asArray;
	public boolean insertRemoteCloud = true;

	public CloudDC getRemoteCloud() {
		return remoteCloud;
	}

	public void setRemoteCloud(CloudDC remoteCloud) {
		this.remoteCloud = remoteCloud;
	}

	public List<CloudDC> getRemoteCloudsList() {
		return remoteCloudsList;
	}

	public void setRemoteCloudsList(List<CloudDC> remoteCloudsList) {
		this.remoteCloudsList = remoteCloudsList;
	}

	public NetworkAddressIPv4 getNetwork() {
		return network;
	}

	public void setNetwork(NetworkAddressIPv4 network) {
		this.network = network;
	}

	public List<AS> getAsList() {
		return asList;
	}

	public void setAsList(List<AS> asList) {
		this.asList = asList;
	}

	public AS[] getAsArray() {
		return asArray;
	}

	public void setAsArray(AS[] asArray) {
		this.asArray = asArray;
	}

	/**
	 * The method that inserts/updates a remote CloudDC to the db.
	 * 
	 */
	public String updateRemoteCloud() {
		logger.debug("Updating remote cloud " + remoteCloud.getCloudDcName() + ".");
		if (insertRemoteCloud) {
			try {
				cdao.insert(remoteCloud);
			} catch (Exception e) {
				logger.warn("Error while inserting remote cloud to database. "
						+ "Probably primary key already exists.");
			}
		} else {
			try {
				cdao.update(remoteCloud);
			} catch (Exception e) {
				logger.warn("Error while inserting remote cloud to database. "
						+ "Probably primary key already exists.");
			}
		}

		remoteCloudsList = cdao.findRemoteClouds();
		remoteCloud = new CloudDC();
		network = new NetworkAddressIPv4();
		editable = true;
		insertRemoteCloud = true;
		logger.debug("Remote cloud " + remoteCloud.getCloudDcName() + " was successfully updated.");
		return "remoteclouds";
	}

	/**
	 * The method that edits a remote CloudDC.
	 * 
	 */
	public void editRemoteCloud(CloudDC c) {
		logger.debug("Editing remote cloud " + c.getCloudDcName() + ".");
		remoteCloud = c;
		editable = false;
		insertRemoteCloud = false;
	}

	/**
	 * The method that deletes a remote CloudDC from the db.
	 * 
	 */
	public String deleteRemoteCloud(CloudDC c) {
		logger.debug("Deleting remote cloud " + c.getCloudDcName() + ".");
		cdao.deleteById(c.getCloudDcName());
		remoteCloudsList = cdao.findRemoteClouds();

		editable = true;
		insertRemoteCloud = true;
		logger.debug("Remote cloud " + c.getCloudDcName() + " was successfully deleted.");
		return "remoteclouds";
	}

	/**
	 * The method that adds a Network to the list of remote cloud's networks.
	 * 
	 */
	public void addRemoteNetwork() {
		logger.debug("Adding remote network " + network + ".");
		remoteCloud.getDcNetworks().add(network);
		network = new NetworkAddressIPv4();
	}

	/**
	 * The method that deletes a Network from the list of remote cloud's
	 * networks.
	 * 
	 */
	public void deleteRemoteNetwork(NetworkAddressIPv4 net) {
		logger.debug("Deleting remote network " + net + ".");
		remoteCloud.getDcNetworks().remove(net);
	}
}
