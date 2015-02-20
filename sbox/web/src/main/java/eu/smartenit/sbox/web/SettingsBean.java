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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dao.SystemControlParametersDAO;
import eu.smartenit.sbox.db.dao.TimeScheduleParametersDAO;
import eu.smartenit.sbox.db.dto.SystemControlParameters;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;

/**
 * The SettingsBean bean class.
 * 
 * It is the controlling class of the settings.xhtml page.
 * 
 * @author George Petropoulos
 * @version 3.0
 * 
 */
@ManagedBean
@RequestScoped
public class SettingsBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory
			.getLogger(SettingsBean.class);

	private TimeScheduleParametersDAO tspdao;
	
	private SystemControlParametersDAO scpdao;

	@PostConstruct
	public void init() {	
		tspdao = new TimeScheduleParametersDAO();
		scpdao = new SystemControlParametersDAO();

		timeParams = tspdao.findLast();
		if (timeParams == null)
			timeParams = new TimeScheduleParameters();
		
		systemParams = scpdao.findLast();
		if (systemParams == null)
			systemParams = new SystemControlParameters();
		
		logger.debug("Navigating to settings page.");
	}

	public boolean editable = true;

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	/**
	 * Settings-page relevant parameters and functions.
	 * 
	 */
	public TimeScheduleParameters timeParams;
	
	public SystemControlParameters systemParams;

	public TimeScheduleParameters getTimeParams() {
		return timeParams;
	}

	public void setTimeParams(TimeScheduleParameters timeParams) {
		this.timeParams = timeParams;
	}

	public SystemControlParameters getSystemParams() {
		return systemParams;
	}

	public void setSystemParams(SystemControlParameters systemParams) {
		this.systemParams = systemParams;
	}

	/**
	 * The method that inserts/updates the TimeScheduleParameters to the db.
	 * 
	 */
	public String updateTimeParams() {
		logger.debug("Updating TimeParams " + timeParams + ".");
		tspdao.deleteAll();
		try {
			tspdao.insert(timeParams);
		} catch (Exception e) {
			logger.warn("Error while inserting time parameters to database. "
					+ "Probably primary key already exists.");
		}
		logger.debug("TimeParams " + timeParams + " was successfully updated.");
		return "settings";
	}
	
	/**
	 * The method that inserts/updates the SystemControlParameters to the db.
	 * 
	 */
	public String updateSystemParams() {
		logger.debug("Updating SystemControlParameters " + systemParams + ".");
		scpdao.deleteAll();
		try {
			scpdao.insert(systemParams);
		} catch (Exception e) {
			logger.warn("Error while inserting system parameters to database. "
					+ "Probably primary key already exists.");
		}
		logger.debug("SystemControlParameters " + systemParams + " was successfully updated.");
		return "settings";
	}

	/**
	 * The method that redirects to the settings.xhtml.
	 * 
	 */
	public String gotoSettingsPage() {
		tspdao = new TimeScheduleParametersDAO();

		timeParams = tspdao.findLast();
		if (timeParams == null)
			timeParams = new TimeScheduleParameters();
		
		logger.debug("Navigating to settings page.");
		return "settings?faces-redirect=true";
	}
}
