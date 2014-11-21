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
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dao.ASDAO;
import eu.smartenit.sbox.db.dao.BGRouterDAO;
import eu.smartenit.sbox.db.dao.DARouterDAO;
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.web.util.WebUtils;

/**
 * The RoutersBean bean class.
 * 
 * It is the controlling class of the routers.xhtml page.
 * 
 * @author George Petropoulos
 * @version 1.2
 * 
 */
@ManagedBean
@ViewScoped
public class RoutersBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory
			.getLogger(RoutersBean.class);
	
	private ASDAO asdao;

	private DARouterDAO dadao;

	private BGRouterDAO bgdao;


	@PostConstruct
	public void init() {	
		asdao = new ASDAO();
		dadao = new DARouterDAO();
		bgdao = new BGRouterDAO();

		asList = asdao.findLocalAs();
		asArray = WebUtils.listToArray(AS.class, asList);
		daRoutersList = dadao.findAll();
		bgRoutersList = bgdao.findAll();
		
		logger.debug("Navigating to routers page.");
	}

	public boolean editable = true;

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * Routers-page relevant parameters and functions.
	 * 
	 */

	public BGRouter bgRouter = new BGRouter();
	public DARouter daRouter = new DARouter();
	public AS[] asArray;
	public List<BGRouter> bgRoutersList = new ArrayList<BGRouter>();
	public List<DARouter> daRoutersList = new ArrayList<DARouter>();
	public String selectedAS;
	public List<AS> asList;

	public BGRouter getBgRouter() {
		return bgRouter;
	}

	public void setBgRouter(BGRouter bgRouter) {
		this.bgRouter = bgRouter;
	}

	public AS[] getAsArray() {
		return asArray;
	}

	public void setAsArray(AS[] asArray) {
		this.asArray = asArray;
	}

	public List<BGRouter> getBgRoutersList() {
		return bgRoutersList;
	}

	public void setBgRoutersList(List<BGRouter> bgRoutersList) {
		this.bgRoutersList = bgRoutersList;
	}

	public String getSelectedAS() {
		return selectedAS;
	}

	public void setSelectedAS(String selectedAS) {
		this.selectedAS = selectedAS;
	}

	public DARouter getDaRouter() {
		return daRouter;
	}

	public void setDaRouter(DARouter daRouter) {
		this.daRouter = daRouter;
	}

	public List<DARouter> getDaRoutersList() {
		return daRoutersList;
	}

	public void setDaRoutersList(List<DARouter> daRoutersList) {
		this.daRoutersList = daRoutersList;
	}

	/**
	 * The method that inserts/updates a BG Router to the db.
	 * 
	 */
	public String updateBGRouter() {
		logger.debug("Updating BGRouter " + bgRouter.getManagementAddress().getPrefix() + ".");
		try {
			bgdao.insertByASNumber(bgRouter, Integer.valueOf(selectedAS));
		} catch (Exception e) {
			logger.warn("Error while inserting BG router to database. "
					+ "Probably primary key already exists.");
		}
		bgRoutersList = bgdao.findAll();
		bgRouter = new BGRouter();
		logger.debug("BGRouter " + bgRouter.getManagementAddress().getPrefix() + " was successfully updated.");
		return "routers";
	}

	/**
	 * The method that deletes a BG Router from the db.
	 * 
	 */
	public String deleteBGRouter(BGRouter bg) {
		logger.debug("Deleting BGRouter " + bg.getManagementAddress().getPrefix() + ".");
		bgdao.deleteById(bg.getManagementAddress().getPrefix());
		bgRoutersList = bgdao.findAll();
		logger.debug("BGRouter " + bg.getManagementAddress().getPrefix() + " was successfully deleted.");
		return "routers";
	}

	/**
	 * The method that inserts/updates a DA Router to the db.
	 * 
	 */
	public String updateDARouter() {
		logger.debug("Updating DARouter " + daRouter.getManagementAddress().getPrefix() + ".");
		try {
			dadao.insert(daRouter);
		} catch (Exception e) {
			logger.warn("Error while inserting DA router to database. "
					+ "Probably primary key already exists.");
		}
		daRoutersList = dadao.findAll();
		daRouter = new DARouter();
		logger.debug("DARouter " + daRouter.getManagementAddress().getPrefix() + " was successfully updated.");
		return "routers";
	}

	/**
	 * The method that deletes a DA Router from the db.
	 * 
	 */
	public String deleteDARouter(DARouter da) {
		logger.debug("Deleting DARouter " + da.getManagementAddress().getPrefix() + ".");
		dadao.deleteById(da.getManagementAddress().getPrefix());
		daRoutersList = dadao.findAll();
		logger.debug("DARouter " + da.getManagementAddress().getPrefix() + " was successfully deleted.");
		return "routers";
	}

}
