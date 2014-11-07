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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.annotation.PostConstruct;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dao.ASDAO;
import eu.smartenit.sbox.db.dao.BGRouterDAO;
import eu.smartenit.sbox.db.dao.CloudDCDAO;
import eu.smartenit.sbox.db.dao.DARouterDAO;
import eu.smartenit.sbox.db.dao.DC2DCCommunicationDAO;
import eu.smartenit.sbox.db.dao.DbConstants;
import eu.smartenit.sbox.db.dao.ISPDAO;
import eu.smartenit.sbox.db.dao.LinkDAO;
import eu.smartenit.sbox.db.dao.SDNControllerDAO;
import eu.smartenit.sbox.db.dao.TimeScheduleParametersDAO;
import eu.smartenit.sbox.db.dao.TunnelDAO;
import eu.smartenit.sbox.db.dao.util.Tables;
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.CloudDC;
import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.DC2DCCommunicationID;
import eu.smartenit.sbox.db.dto.Direction;
import eu.smartenit.sbox.db.dto.ISP;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SDNController;
import eu.smartenit.sbox.db.dto.Segment;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.SimpleTunnelID;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;
import eu.smartenit.sbox.db.dto.Tunnel;

/**
 * The Dashboard bean class.
 * 
 * It is the controlling class of the sbox web application, including all
 * parameters, and functions that are performed in the UI.
 * 
 * @author George Petropoulos
 * @version 1.0
 * 
 */
@ManagedBean
@SessionScoped
public class DashboardBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory
			.getLogger(DashboardBean.class);

	private ASDAO asdao;

	private ISPDAO ispdao;

	private DARouterDAO dadao;

	private BGRouterDAO bgdao;

	private SDNControllerDAO sdndao;

	private LinkDAO linkdao;

	private TunnelDAO tudao;

	private CloudDCDAO cdao;

	private TimeScheduleParametersDAO tspdao;

	private DC2DCCommunicationDAO dcdao;

	@PostConstruct
	public void init() {	
		logger.debug("Initializing sbox web application.");
		String dbPathFile = "smartenit.db";
		
		String jettyProperty = System.getProperty("db");
		String homeDbFile = System.getenv("HOME") + "/smartenit.db";
		// Check if file exists in home folder
		if (new File(homeDbFile).exists()) {
			dbPathFile = homeDbFile;
		}
		// Otherwise check if jetty property with different filename is set.
		else if (jettyProperty != null && !jettyProperty.isEmpty()) {
			dbPathFile = jettyProperty;
		}
		// If no jetty property is set, then read from internal file.
		else {
			PropertiesConfiguration properties = null;
			try {
				properties = new PropertiesConfiguration("db.properties");
			} catch (ConfigurationException e) {
				logger.warn("Error when reading from local db.properties file: "
						+ e.getMessage());
			}
			dbPathFile = properties.getString("db.file");
		}
		
		File dbFile = new File(dbPathFile);
		DbConstants.DBI_URL = "jdbc:sqlite:" + dbPathFile;
		if (!dbFile.exists()) {
			try {
				dbFile.createNewFile();
			} catch (IOException e) {
				logger.warn("Error creating db file " + dbFile + ": " + e.getMessage());
			}
			Tables t = new Tables();
			t.createAll();
		}
		logger.debug("DBI URL " + DbConstants.DBI_URL + ". Initialization complete.");
	}

	/**
	 * Home-page relevant parameters and functions.
	 * 
	 */

	public boolean editable = true;

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * The method that redirects to the index.xhtml.
	 * 
	 */
	public String gotoIndexPage() {
		logger.debug("Navigating to index page.");
		return "index?faces-redirect=true";
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
	public String editAS(AS a) {
		logger.debug("Editing AS " + a.getAsNumber() + ".");
		as = a;
		editable = false;
		insertAs = false;
		return "isp";
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

	/**
	 * The method that redirects to the isp.xhtml.
	 * 
	 */
	public String gotoIspPage() {
		ispdao = new ISPDAO();
		asdao = new ASDAO();

		as = new AS();
		isp = ispdao.findLast();
		asList = asdao.findAll();
		editable = true;
		logger.debug("Navigating to isp page.");
		return "isp?faces-redirect=true";
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

	/**
	 * The method that redirects to the routers.xhtml.
	 * 
	 */
	public String gotoRoutersPage() {
		asdao = new ASDAO();
		dadao = new DARouterDAO();
		bgdao = new BGRouterDAO();

		asList = asdao.findLocalAs();
		asArray = listToArray(AS.class, asList);
		daRoutersList = dadao.findAll();
		bgRoutersList = bgdao.findAll();
		
		logger.debug("Navigating to routers page.");
		return "routers?faces-redirect=true";
	}

	/**
	 * SDN Controllers-page relevant parameters and functions.
	 * 
	 */

	public SDNController sdn;
	public List<SDNController> sdnList;
	public String[] selectedDARouters;
	public boolean insertSDNController = true;

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
		daRoutersArray = listToArray(DARouter.class, daRoutersList);
		selectedDARouters = new String[0];
		logger.debug("SDNController " + sdn.getManagementAddress().getPrefix() + " was successfully updated.");
		return "sdncontrollers";
	}

	/**
	 * The method that edits an SDN Controller.
	 * 
	 */
	public String editSDNController(SDNController s) {
		logger.debug("Editing SDNController " + s.getManagementAddress().getPrefix() + ".");
		selectedDARouters = daRoutersListToStringArray(dadao
				.findBySDNControllerAddress(s.getManagementAddress()
						.getPrefix()));
		sdn = s;
		editable = false;
		insertSDNController = false;
		return "sdncontrollers";
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

	/**
	 * The method that redirects to the sdncontrollers.xhtml.
	 * 
	 */
	public String gotoSDNControllersPage() {
		dadao = new DARouterDAO();
		sdndao = new SDNControllerDAO();

		sdn = new SDNController();
		sdnList = sdndao.findAll();
		daRoutersList = dadao.findAll();
		daRoutersArray = listToArray(DARouter.class, daRoutersList);
		editable = true;
		insertSDNController = true;
		
		logger.debug("Navigating to sdncontrollers page.");
		return "sdncontrollers?faces-redirect=true";
	}

	/**
	 * Links-page relevant parameters and functions.
	 * 
	 */

	public Link link;
	public List<Link> linksList;
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
		
		logger.debug("Link " + link.getLinkID() + " was successfully updated.");
		return "links";
	}

	/**
	 * The method that edits a Link.
	 * 
	 */
	public String editLink(Link l) {
		logger.debug("Editing Link " + l.getLinkID() + ".");
		link = l;
		editable = false;
		insertLink = false;
		return "links";
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

	/**
	 * The method that redirects to the links.xhtml.
	 * 
	 */
	public String gotoLinksPage() {
		linkdao = new LinkDAO();
		bgdao = new BGRouterDAO();

		link = new Link();
		link.setLinkID(new SimpleLinkID());
		segment = new Segment();

		linksList = linkdao.findAll();
		bgRoutersList = bgdao.findAll();
		bgRoutersArray = listToArray(BGRouter.class, bgRoutersList);
		editable = true;
		insertLink = true;
		
		logger.debug("Navigating to links page.");
		return "links?faces-redirect=true";
	}

	/**
	 * Tunnels-page relevant parameters and functions.
	 * 
	 */
	public Tunnel tunnel;
	public SimpleTunnelID simpleTunnelID;
	public List<Tunnel> tunnelsList;
	public String selectedLink;
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
		} else
			tudao.update(tunnel);
		tunnelsList = tudao.findAll();

		tunnel = new Tunnel();
		tunnel.setTunnelID(new SimpleTunnelID());
		editable = true;
		insertTunnel = true;
		logger.debug("Tunnel " + tunnel.getTunnelID() + " was successfully updated.");
		return "tunnels";
	}

	/**
	 * The method that edits a Tunnel.
	 * 
	 */
	public String editTunnel(Tunnel t) {
		logger.debug("Editing Tunnel " + t.getTunnelID() + ".");
		tunnel = t;
		selectedLink = ((SimpleLinkID) t.getLink().getLinkID())
				.getLocalLinkID()
				+ ","
				+ ((SimpleLinkID) t.getLink().getLinkID()).getLocalIspName();
		editable = false;
		insertTunnel = false;
		return "tunnels";
	}

	/**
	 * The method that deletes a Tunnel from the db.
	 * 
	 */
	public String deleteTunnel(Tunnel t) {
		logger.debug("Deleting Tunnel " + t.getTunnelID() + ".");
		tudao.deleteById((SimpleTunnelID) t.getTunnelID());
		tunnelsList = tudao.findAll();

		tunnel = new Tunnel();
		tunnel.setTunnelID(new SimpleTunnelID());
		editable = true;
		insertTunnel = true;
		logger.debug("Tunnel " + t.getTunnelID() + " was successfully deleted.");
		return "tunnels";
	}

	/**
	 * The method that redirects to the tunnels.xhtml.
	 * 
	 */
	public String gotoTunnelsPage() {
		tudao = new TunnelDAO();
		linkdao = new LinkDAO();

		tunnel = new Tunnel();
		tunnel.setTunnelID(new SimpleTunnelID());

		tunnelsList = tudao.findAll();
		linksList = linkdao.findAll();
		linksArray = listToArray(Link.class, linksList);
		editable = true;
		insertTunnel = true;
		
		logger.debug("Navigating to tunnels page.");
		return "tunnels?faces-redirect=true";
	}

	/**
	 * Local Clouds-page relevant parameters and functions.
	 * 
	 */

	public CloudDC cloud;
	public List<CloudDC> cloudsList;
	public String selectedDARouter;
	public String selectedSDNController;
	public DARouter[] daRoutersArray;
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
	public String editCloud(CloudDC c) {
		logger.debug("Updating local cloud " + c.getCloudDcName() + ".");
		cloud = c;
		editable = false;
		insertLocalCloud = false;
		return "localclouds";
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

	/**
	 * The method that redirects to the localclouds.xhtml.
	 * 
	 */
	public String gotoLocalCloudsPage() {
		cdao = new CloudDCDAO();
		asdao = new ASDAO();
		dadao = new DARouterDAO();
		sdndao = new SDNControllerDAO();

		cloud = new CloudDC();

		cloudsList = cdao.findLocalClouds();
		network = new NetworkAddressIPv4();

		asList = asdao.findLocalAs();
		asArray = listToArray(AS.class, asList);
		daRoutersList = dadao.findAll();
		daRoutersArray = listToArray(DARouter.class, daRoutersList);
		sdnControllersList = sdndao.findAll();
		sdnControllersArray = listToArray(SDNController.class,
				sdnControllersList);
		editable = true;
		insertLocalCloud = true;
		
		logger.debug("Navigating to localclouds page.");
		return "localclouds?faces-redirect=true";
	}

	/**
	 * Remote Clouds-page relevant parameters and functions.
	 * 
	 */
	public CloudDC remoteCloud;
	public List<CloudDC> remoteCloudsList;
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
	public String editRemoteCloud(CloudDC c) {
		logger.debug("Editing remote cloud " + c.getCloudDcName() + ".");
		remoteCloud = c;
		editable = false;
		insertRemoteCloud = false;
		return "remoteclouds";
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

	/**
	 * The method that redirects to the remoteclouds.xhtml.
	 * 
	 */
	public String gotoRemoteCloudsPage() {
		cdao = new CloudDCDAO();
		asdao = new ASDAO();

		remoteCloud = new CloudDC();
		remoteCloudsList = cdao.findRemoteClouds();

		asList = asdao.findRemoteAs();
		asArray = listToArray(AS.class, asList);
		network = new NetworkAddressIPv4();
		editable = true;
		insertRemoteCloud = true;
		
		logger.debug("Navigating to remoteclouds page.");
		return "remoteclouds?faces-redirect=true";
	}

	/**
	 * Inter-datacenters-page relevant parameters and functions.
	 * 
	 */

	public DC2DCCommunication dc2dc;
	public List<DC2DCCommunication> dc2dcList;
	public String[] selectedTunnels;
	public Direction[] directionArray;
	public CloudDC[] localCloudsArray;
	public CloudDC[] remoteCloudsArray;
	public Tunnel[] tunnelsArray;
	public Map<String, Integer> localCloudAses;
	public Map<String, Integer> remoteCloudAses;

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

		for (int i = 0; i < selectedTunnels.length; i++) {
			tudao.updateByDC2DCCommunicationID(
					new SimpleTunnelID(selectedTunnels[i].split(",")[0],
							Integer.valueOf(selectedTunnels[i].split(",")[1])),
					dc2dc.getId());
		}
		logger.debug("DC2DC " + dc2dc.getId() + " was successfully updated.");
		return gotoInterDatacenterPage();
	}

	/**
	 * The method that edits a DC2DCCommunication.
	 * 
	 */
	public String editDC2DC(DC2DCCommunication dc) {
		logger.debug("Editing DC2DC " + dc.getId() + ".");
		dc2dc = dc;
		return "interdatacenter";
	}

	/**
	 * The method that deletes a DC2DCCommunication from the db.
	 * 
	 */
	public String deleteDC2DC(DC2DCCommunication dc) {
		logger.debug("Deleting DC2DC " + dc.getId() + ".");
		List<Tunnel> dcTunnels = tudao.findAllByDC2DCCommunicationID(dc.getId());
		for (Tunnel t : dcTunnels) 
			tudao.updateByDC2DCCommunicationID((SimpleTunnelID) t.getTunnelID(), 
					new DC2DCCommunicationID());
		dcdao.deleteById(dc.getId());
		logger.debug("DC2DC " + dc.getId() + " was successfully deleted.");
		return gotoInterDatacenterPage();
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
	 * The method that redirects to the interdatacenter.xhtml.
	 * 
	 */
	public String gotoInterDatacenterPage() {
		cdao = new CloudDCDAO();
		tudao = new TunnelDAO();
		dcdao = new DC2DCCommunicationDAO();

		dc2dc = new DC2DCCommunication();
		dc2dcList = dcdao.findAll();

		cloudsList = cdao.findLocalClouds();
		remoteCloudsList = cdao.findRemoteClouds();
		localCloudsArray = listToArray(CloudDC.class, cloudsList);
		remoteCloudsArray = listToArray(CloudDC.class, remoteCloudsList);

		directionArray = new Direction[] { Direction.incomingTraffic,
				Direction.outcomingTraffic };

		tunnelsList = tudao.findAll();
		tunnelsArray = listToArray(Tunnel.class, tunnelsList);

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
		return "interdatacenter?faces-redirect=true";
	}

	/**
	 * Settings-page relevant parameters and functions.
	 * 
	 */
	public TimeScheduleParameters timeParams;

	public TimeScheduleParameters getTimeParams() {
		return timeParams;
	}

	public void setTimeParams(TimeScheduleParameters timeParams) {
		this.timeParams = timeParams;
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

	/**
	 * Support functions.
	 * 
	 */

	/**
	 * The method that transforms an arraylist of objects to an array of objects
	 * 
	 * @param c
	 *            The object class
	 * @param objectsList
	 *            The arraylist of objects
	 * 
	 * @return The array of objects
	 * 
	 */
	@SuppressWarnings({ "unchecked" })
	public <K> K[] listToArray(Class<K> c, List<K> objectsList) {
		logger.debug("Converting list of class " + c.getName() + " into array of objects.");
		K[] objectsArray;
		if (objectsList == null)
			objectsArray = (K[]) Array.newInstance(c, 0);
		else {
			objectsArray = (K[]) Array.newInstance(c, objectsList.size());
			for (int i = 0; i < objectsList.size(); i++)
				objectsArray[i] = objectsList.get(i);
		}
		return objectsArray;
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
