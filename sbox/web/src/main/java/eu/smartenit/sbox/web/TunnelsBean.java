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

import eu.smartenit.sbox.db.dao.LinkDAO;
import eu.smartenit.sbox.db.dao.TunnelDAO;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.SimpleTunnelID;
import eu.smartenit.sbox.db.dto.Tunnel;
import eu.smartenit.sbox.web.util.WebUtils;

/**
 * The TunnelsBean bean class.
 * 
 * It is the controlling class of the tunnels.xhtml page.
 * 
 * @author George Petropoulos
 * @version 1.2
 * 
 */
@ManagedBean
@ViewScoped
public class TunnelsBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory
			.getLogger(TunnelsBean.class);
	
	private TunnelDAO tudao;

	private LinkDAO linkdao;


	@PostConstruct
	public void init() {	
		tudao = new TunnelDAO();
		linkdao = new LinkDAO();

		tunnel = new Tunnel();

		tunnelsList = tudao.findAll();
		linksList = linkdao.findAll();
		linksArray = WebUtils.listToArray(Link.class, linksList);
		editable = true;
		insertTunnel = true;
		
		logger.debug("Navigating to tunnels page.");
	}

	public boolean editable = true;

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * Tunnels-page relevant parameters and functions.
	 * 
	 */
	public Tunnel tunnel;
	public SimpleTunnelID simpleTunnelID;
	public List<Tunnel> tunnelsList;
	public String selectedLink;
	public List<Link> linksList;
	public Link[] linksArray;
	public boolean insertTunnel = true;

	public Tunnel getTunnel() {
		return tunnel;
	}

	public void setTunnel(Tunnel tunnel) {
		this.tunnel = tunnel;
	}

	public SimpleTunnelID getSimpleTunnelID() {
		return simpleTunnelID;
	}

	public void setSimpleTunnelID(SimpleTunnelID simpleTunnelID) {
		this.simpleTunnelID = simpleTunnelID;
	}

	public List<Tunnel> getTunnelsList() {
		return tunnelsList;
	}

	public void setTunnelsList(List<Tunnel> tunnelsList) {
		this.tunnelsList = tunnelsList;
	}

	public String getSelectedLink() {
		return selectedLink;
	}

	public void setSelectedLink(String selectedLink) {
		this.selectedLink = selectedLink;
	}

	public Link[] getLinksArray() {
		return linksArray;
	}

	public void setLinksArray(Link[] linksArray) {
		this.linksArray = linksArray;
	}

	public List<Link> getLinksList() {
		return linksList;
	}

	public void setLinksList(List<Link> linksList) {
		this.linksList = linksList;
	}

	/**
	 * The method that inserts/updates a Tunnel to the db.
	 * 
	 */
	public String updateTunnel() {
		logger.debug("Updating Tunnel " + tunnel.getTunnelID() + ".");
		Link l = new Link();
		l.setLinkID(new SimpleLinkID(selectedLink.split(",")[0], selectedLink
				.split(",")[1]));
		tunnel.setLink(l);
		if (insertTunnel) {
			try {
				tudao.insert(tunnel);
			} catch (Exception e) {
				logger.warn("Error while inserting tunnel to database. "
						+ "Probably primary key already exists.");
			}
		} 
		else {
			tudao.update(tunnel);
		}
		tunnelsList = tudao.findAll();

		tunnel = new Tunnel();
		editable = true;
		insertTunnel = true;
		logger.debug("Tunnel " + tunnel.getTunnelID() + " was successfully updated.");
		return "tunnels";
	}

	/**
	 * The method that edits a Tunnel.
	 * 
	 */
	public void editTunnel(Tunnel t) {
		logger.debug("Editing Tunnel " + t.getTunnelID() + ".");
		tunnel = t;
		selectedLink = ((SimpleLinkID) t.getLink().getLinkID())
				.getLocalLinkID()
				+ ","
				+ ((SimpleLinkID) t.getLink().getLinkID()).getLocalIspName();
		editable = false;
		insertTunnel = false;
	}

	/**
	 * The method that deletes a Tunnel from the db.
	 * 
	 */
	public String deleteTunnel(Tunnel t) {
		logger.debug("Deleting Tunnel " + t.getTunnelID() + ".");
		tudao.deleteById(t.getTunnelID());
		tunnelsList = tudao.findAll();

		tunnel = new Tunnel();
		editable = true;
		insertTunnel = true;
		logger.debug("Tunnel " + t.getTunnelID() + " was successfully deleted.");
		return "tunnels";
	}


}
