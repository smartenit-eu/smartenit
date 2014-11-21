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

import eu.smartenit.sbox.db.dao.BGRouterDAO;
import eu.smartenit.sbox.db.dao.LinkDAO;
import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.Segment;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.web.util.WebUtils;

/**
 * The LinksBean bean class.
 * 
 * It is the controlling class of the links.xhtml page.
 * 
 * @author George Petropoulos
 * @version 1.2
 * 
 */
@ManagedBean
@ViewScoped
public class LinksBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory
			.getLogger(LinksBean.class);
	
	private BGRouterDAO bgdao;

	private LinkDAO linkdao;


	@PostConstruct
	public void init() {	
		linkdao = new LinkDAO();
		bgdao = new BGRouterDAO();

		link = new Link();
		link.setLinkID(new SimpleLinkID());
		segment = new Segment();

		linksList = linkdao.findAll();
		bgRoutersList = bgdao.findAll();
		bgRoutersArray = WebUtils.listToArray(BGRouter.class, bgRoutersList);
		editable = true;
		insertLink = true;
		
		logger.debug("Navigating to links page.");
	}

	public boolean editable = true;

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * Links-page relevant parameters and functions.
	 * 
	 */

	public Link link;
	public List<Link> linksList;
	public List<BGRouter> bgRoutersList;
	public BGRouter[] bgRoutersArray;
	public String selectedBGRouter;
	public Segment segment;
	public boolean insertLink = true;

	public Segment getSegment() {
		return segment;
	}

	public void setSegment(Segment segment) {
		this.segment = segment;
	}

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	public List<Link> getLinksList() {
		return linksList;
	}

	public void setLinksList(List<Link> linksList) {
		this.linksList = linksList;
	}

	public BGRouter[] getBgRoutersArray() {
		return bgRoutersArray;
	}

	public void setBgRoutersArray(BGRouter[] bgRoutersArray) {
		this.bgRoutersArray = bgRoutersArray;
	}

	public String getSelectedBGRouter() {
		return selectedBGRouter;
	}

	public void setSelectedBGRouter(String selectedBGRouter) {
		this.selectedBGRouter = selectedBGRouter;
	}

	public List<BGRouter> getBgRoutersList() {
		return bgRoutersList;
	}

	public void setBgRoutersList(List<BGRouter> bgRoutersList) {
		this.bgRoutersList = bgRoutersList;
	}

	/**
	 * The method that inserts/updates a Link to the db.
	 * 
	 */
	public String updateLink() {
		logger.debug("Updating Link " + link.getLinkID() + ".");
		BGRouter bgRouter = new BGRouter();
		bgRouter.setManagementAddress(new NetworkAddressIPv4(selectedBGRouter,
				0));
		link.setBgRouter(bgRouter);

		link.getCostFunction().setType("Cost-function");
		link.getCostFunction().setInputUnit("bps");
		link.getCostFunction().setOutputUnit("euros");
		if (insertLink) {
			try {
				linkdao.insert(link);
			} catch (Exception e) {
				logger.warn("Error while inserting link to database. "
						+ "Probably primary key already exists.");
			}
		} else {
			try {
				linkdao.update(link);
			} catch (Exception e) {
				logger.warn("Error while updating link to database. "
						+ "Probably primary key already exists.");
			}
		}

		linksList = linkdao.findAll();

		link = new Link();
		link.setLinkID(new SimpleLinkID());
		editable = true;
		segment = new Segment();
		
		logger.debug("Link " + link + " was successfully updated.");
		return "links";
	}

	/**
	 * The method that edits a Link.
	 * 
	 */
	public void editLink(Link l) {
		logger.debug("Editing Link " + l + ".");
		link = l;
		editable = false;
		insertLink = false;
	}

	/**
	 * The method that deletes a Link from the db.
	 * 
	 */
	public String deleteLink(Link l) {
		logger.debug("Deleting Link " + l.getLinkID() + ".");
		linkdao.deleteById((SimpleLinkID) l.getLinkID());
		linksList = linkdao.findAll();
		link = new Link();
		link.setLinkID(new SimpleLinkID());
		editable = true;
		logger.debug("Link " + l.getLinkID() + " was successfully deleted.");
		return "links";
	}

	/**
	 * The method that adds a Segment to the list of link's segments.
	 * 
	 */
	public void addSegment() {
		logger.debug("Adding segment " + segment + ".");
		link.getCostFunction().getSegments().add(segment);
		segment = new Segment();
	}

	/**
	 * The method that deletes a Segment from the list of link's segments.
	 * 
	 */
	public void deleteSegment(Segment s) {
		logger.debug("Deleting segment " + s + ".");
		link.getCostFunction().getSegments().remove(s);
	}

}
