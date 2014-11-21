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

import eu.smartenit.sbox.db.dao.ASDAO;
import eu.smartenit.sbox.db.dao.ISPDAO;
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.ISP;
import eu.smartenit.sbox.db.dto.SDNController;

/**
 * The ISPBean bean class.
 * 
 * It is the controlling class of the isp.xhtml page.
 * 
 * @author George Petropoulos
 * @version 1.2
 * 
 */
@ManagedBean
@ViewScoped
public class IspBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory
			.getLogger(IspBean.class);

	private ASDAO asdao;

	private ISPDAO ispdao;

	@PostConstruct
	public void init() {	
		ispdao = new ISPDAO();
		asdao = new ASDAO();

		as = new AS();
		isp = ispdao.findLast();
		asList = asdao.findAll();
		editable = true;
		logger.debug("Navigating to isp page.");
	}

	public boolean editable = true;

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * ISP-page relevant parameters and functions.
	 * 
	 */

	public ISP isp;
	public AS as;
	public List<AS> asList;
	public List<SDNController> sdnControllersList;
	public boolean insertAs = true;

	public ISP getIsp() {
		return isp;
	}

	public void setIsp(ISP isp) {
		this.isp = isp;
	}

	public AS getAs() {
		return as;
	}

	public void setAs(AS as) {
		this.as = as;
	}

	public List<SDNController> getSdnControllersList() {
		return sdnControllersList;
	}

	public void setSdnControllersList(List<SDNController> sdnControllersList) {
		this.sdnControllersList = sdnControllersList;
	}

	public List<AS> getAsList() {
		return asList;
	}

	public void setAsList(List<AS> asList) {
		this.asList = asList;
	}

	/**
	 * The method that inserts/updates the ISP information to the db.
	 * 
	 */
	public String updateIspName() {
		logger.debug("Updating ISP " + isp.getIspName() + ".");
		ispdao.deleteAll();
		ispdao.insert(isp);
		logger.debug("ISP " + isp.getIspName() + " was successfully updated.");
		return "isp";
	}

	/**
	 * The method that inserts/updates as AS to the db.
	 * 
	 */
	public String updateAS() {
		logger.debug("Updating AS " + as.getAsNumber() + ".");
		if (insertAs) {
			try {
				asdao.insert(as);
			} catch (Exception e) {
				logger.warn("Error while inserting AS to database. "
						+ "Probably primary key already exists.");
			}
		} else {
			asdao.update(as);
		}
		as = new AS();
		asList = asdao.findAll();
		editable = true;
		insertAs = true;
		
		logger.debug("AS " + as.getAsNumber() + " was successfully updated.");
		return "isp";
	}

	/**
	 * The method that edits as AS.
	 * 
	 */
	public void editAS(AS a) {
		logger.debug("Editing AS " + a.getAsNumber() + ".");
		as = a;
		editable = false;
		insertAs = false;
	}

	/**
	 * The method that deletes as AS from the db.
	 * 
	 */
	public String deleteAS(AS a) {
		logger.debug("Deleting AS " + a.getAsNumber() + ".");
		asdao.deleteById(a.getAsNumber());
		asList = asdao.findAll();
		editable = true;
		logger.debug("AS " + a.getAsNumber() + " was successfully deleted.");
		return "isp";
	}

}
