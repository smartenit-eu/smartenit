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
import eu.smartenit.sbox.db.dao.DARouterDAO;
import eu.smartenit.sbox.db.dao.SDNControllerDAO;
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.CloudDC;
import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SDNController;
import eu.smartenit.sbox.web.util.WebUtils;

/**
 * The LocalCloudsBean bean class.
 * 
 * It is the controlling class of the localclouds.xhtml page.
 * 
 * @author George Petropoulos
 * @version 1.2
 * 
 */
@ManagedBean
@ViewScoped
public class LocalCloudsBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory
			.getLogger(LocalCloudsBean.class);

	private CloudDCDAO cdao;
	
	private ASDAO asdao;
	
	private DARouterDAO dadao;
	
	private SDNControllerDAO sdndao;

	@PostConstruct
	public void init() {	
		cdao = new CloudDCDAO();
		asdao = new ASDAO();
		dadao = new DARouterDAO();
		sdndao = new SDNControllerDAO();

		cloud = new CloudDC();

		cloudsList = cdao.findLocalClouds();
		network = new NetworkAddressIPv4();

		asList = asdao.findLocalAs();
		asArray = WebUtils.listToArray(AS.class, asList);
		daRoutersList = dadao.findAll();
		daRoutersArray = WebUtils.listToArray(DARouter.class, daRoutersList);
		sdnControllersList = sdndao.findAll();
		sdnControllersArray = WebUtils.listToArray(SDNController.class,
				sdnControllersList);
		editable = true;
		insertLocalCloud = true;
		
		logger.debug("Navigating to localclouds page.");
	}

	public boolean editable = true;

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * Local Clouds-page relevant parameters and functions.
	 * 
	 */

	public CloudDC cloud;
	public List<CloudDC> cloudsList;
	public List<AS> asList;
	public AS[] asArray;
	public String selectedDARouter;
	public String selectedSDNController;
	public List<DARouter> daRoutersList;
	public DARouter[] daRoutersArray;
	public List<SDNController> sdnControllersList;
	public SDNController[] sdnControllersArray;
	public NetworkAddressIPv4 network;
	public boolean insertLocalCloud = true;

	public CloudDC getCloud() {
		return cloud;
	}

	public void setCloud(CloudDC cloud) {
		this.cloud = cloud;
	}

	public List<CloudDC> getCloudsList() {
		return cloudsList;
	}

	public void setCloudsList(List<CloudDC> cloudsList) {
		this.cloudsList = cloudsList;
	}

	public String getSelectedDARouter() {
		return selectedDARouter;
	}

	public void setSelectedDARouter(String selectedDARouter) {
		this.selectedDARouter = selectedDARouter;
	}

	public String getSelectedSDNController() {
		return selectedSDNController;
	}

	public void setSelectedSDNController(String selectedSDNController) {
		this.selectedSDNController = selectedSDNController;
	}

	public DARouter[] getDaRoutersArray() {
		return daRoutersArray;
	}

	public void setDaRoutersArray(DARouter[] daRoutersArray) {
		this.daRoutersArray = daRoutersArray;
	}

	public SDNController[] getSdnControllersArray() {
		return sdnControllersArray;
	}

	public void setSdnControllersArray(SDNController[] sdnControllersArray) {
		this.sdnControllersArray = sdnControllersArray;
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

	public List<DARouter> getDaRoutersList() {
		return daRoutersList;
	}

	public void setDaRoutersList(List<DARouter> daRoutersList) {
		this.daRoutersList = daRoutersList;
	}

	public List<SDNController> getSdnControllersList() {
		return sdnControllersList;
	}

	public void setSdnControllersList(List<SDNController> sdnControllersList) {
		this.sdnControllersList = sdnControllersList;
	}

	/**
	 * The method that adds a Network to the list of local cloud's networks.
	 * 
	 */
	public void addLocalNetwork() {
		logger.debug("Adding local network " + network + ".");
		cloud.getDcNetworks().add(network);
		network = new NetworkAddressIPv4();
	}

	/**
	 * The method that deletes a Network from the list of local cloud's
	 * networks.
	 * 
	 */
	public void deleteLocalNetwork(NetworkAddressIPv4 net) {
		logger.debug("Deleting local network " + net + ".");
		cloud.getDcNetworks().remove(net);
	}

	/**
	 * The method that inserts/updates a local CloudDC to the db.
	 * 
	 */
	public String updateCloud() {
		logger.debug("Updating local cloud " + cloud.getCloudDcName() + ".");
		if (insertLocalCloud) {
			try {
				cdao.insert(cloud);
			} catch (Exception e) {
				logger.warn("Error while inserting local cloud to database. "
						+ "Probably primary key already exists.");
			}
		} else {
			try {
				cdao.update(cloud);
			} catch (Exception e) {
				logger.warn("Error while updating local cloud to database. "
						+ "Probably primary key already exists.");
			}
		}

		cloudsList = cdao.findLocalClouds();

		cloud = new CloudDC();
		network = new NetworkAddressIPv4();

		editable = true;
		insertLocalCloud = true;
		logger.debug("Local cloud " + cloud.getCloudDcName() + " was successfully updated.");
		return "localclouds";
	}

	/**
	 * The method that edits a local CloudDC.
	 * 
	 */
	public void editCloud(CloudDC c) {
		logger.debug("Updating local cloud " + c.getCloudDcName() + ".");
		cloud = c;
		editable = false;
		insertLocalCloud = false;
	}

	/**
	 * The method that deletes a local CloudDC from the db.
	 * 
	 */
	public String deleteCloud(CloudDC c) {
		logger.debug("Deleting local cloud " + c.getCloudDcName() + ".");
		cdao.deleteById(c.getCloudDcName());
		cloudsList = cdao.findLocalClouds();
		cloud = new CloudDC();
		editable = true;
		insertLocalCloud = true;
		logger.debug("Local cloud " + c.getCloudDcName() + " was successfully deleted.");
		return "localclouds";
	}
	
}
