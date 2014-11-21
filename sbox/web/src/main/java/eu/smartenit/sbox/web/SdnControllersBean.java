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
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dao.DARouterDAO;
import eu.smartenit.sbox.db.dao.SDNControllerDAO;
import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.SDNController;
import eu.smartenit.sbox.web.util.WebUtils;

/**
 * The SdnControllersBean bean class.
 * 
 * It is the controlling class of the sdncontrollers.xhtml page.
 * 
 * @author George Petropoulos
 * @version 1.2
 * 
 */
@ManagedBean
@ViewScoped
public class SdnControllersBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory
			.getLogger(SdnControllersBean.class);
	
	private DARouterDAO dadao;
	
	private SDNControllerDAO sdndao;


	@PostConstruct
	public void init() {	
		dadao = new DARouterDAO();
		sdndao = new SDNControllerDAO();

		sdn = new SDNController();
		sdnList = sdndao.findAll();
		daRoutersList = dadao.findAll();
		daRoutersArray = WebUtils.listToArray(DARouter.class, daRoutersList);
		editable = true;
		insertSDNController = true;
		
		logger.debug("Navigating to sdncontrollers page.");
	}

	public boolean editable = true;

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * SDN Controllers-page relevant parameters and functions.
	 * 
	 */

	public SDNController sdn;
	public List<SDNController> sdnList;
	public String[] selectedDARouters;
	public boolean insertSDNController = true;
	public List<DARouter> daRoutersList;
	public DARouter[] daRoutersArray;

	public SDNController getSdn() {
		return sdn;
	}

	public void setSdn(SDNController sdn) {
		this.sdn = sdn;
	}

	public List<SDNController> getSdnList() {
		return sdnList;
	}

	public void setSdnList(List<SDNController> sdnList) {
		this.sdnList = sdnList;
	}

	public String[] getSelectedDARouters() {
		return selectedDARouters;
	}

	public void setSelectedDARouters(String[] selectedDARouters) {
		this.selectedDARouters = selectedDARouters;
	}

	public List<DARouter> getDaRoutersList() {
		return daRoutersList;
	}

	public void setDaRoutersList(List<DARouter> daRoutersList) {
		this.daRoutersList = daRoutersList;
	}

	public DARouter[] getDaRoutersArray() {
		return daRoutersArray;
	}

	public void setDaRoutersArray(DARouter[] daRoutersArray) {
		this.daRoutersArray = daRoutersArray;
	}

	/**
	 * The method that inserts/updates an SDN Controller to the db.
	 * 
	 */
	public String updateSDNController() {
		logger.debug("Updating SDNController " + sdn.getManagementAddress().getPrefix() + ".");
		if (insertSDNController) {
			try {
				sdndao.insert(sdn);
			} catch (Exception e) {
				logger.warn("Error while inserting SDN controller to database. "
						+ "Probably primary key already exists.");
			}
		} else {
			sdndao.update(sdn);
		}
		sdnList = sdndao.findAll();

		for (int i = 0; i < daRoutersArray.length; i++) {
			if (contains(selectedDARouters, daRoutersArray[i]
					.getManagementAddress().getPrefix()))
				dadao.updateBySDNControllerAddress(daRoutersArray[i]
						.getManagementAddress().getPrefix(), sdn
						.getManagementAddress().getPrefix());
			else
				dadao.updateBySDNControllerAddress(daRoutersArray[i]
						.getManagementAddress().getPrefix(), null);
		}

		sdn = new SDNController();
		editable = true;
		insertSDNController = true;
		daRoutersArray = WebUtils.listToArray(DARouter.class, daRoutersList);
		selectedDARouters = new String[0];
		logger.debug("SDNController " + sdn.getManagementAddress().getPrefix() + " was successfully updated.");
		return "sdncontrollers";
	}

	/**
	 * The method that edits an SDN Controller.
	 * 
	 */
	public void editSDNController(SDNController s) {
		logger.debug("Editing SDNController " + s.getManagementAddress().getPrefix() + ".");
		selectedDARouters = daRoutersListToStringArray(dadao
				.findBySDNControllerAddress(s.getManagementAddress()
						.getPrefix()));
		sdn = s;
		editable = false;
		insertSDNController = false;
	}

	public boolean contains(String[] array, String s) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(s))
				return true;
		}
		return false;
	}

	public String[] daRoutersListToStringArray(List<DARouter> daList) {
		String[] stringArray;
		if (daList == null)
			stringArray = new String[0];
		else {
			stringArray = new String[daList.size()];
			for (int i = 0; i < daList.size(); i++)
				stringArray[i] = daList.get(i).getManagementAddress()
						.getPrefix();
		}
		return stringArray;
	}

	/**
	 * The method that deletes an SDN Controller from the db.
	 * 
	 */
	public String deleteSDNController(SDNController s) {
		logger.debug("Deleting SDNController " + s.getManagementAddress().getPrefix() + ".");
		sdndao.deleteById(s.getManagementAddress().getPrefix());
		sdnList = sdndao.findAll();
		sdn = new SDNController();
		editable = true;
		logger.debug("SDNController " + s.getManagementAddress().getPrefix() + " was successfully deleted.");
		return "sdncontrollers";
	}

}
