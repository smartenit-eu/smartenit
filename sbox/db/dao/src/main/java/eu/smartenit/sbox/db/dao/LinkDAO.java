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
package eu.smartenit.sbox.db.dao;

import java.util.List;
import java.util.Properties;

import eu.smartenit.sbox.db.dao.gen.AbstractLinkDAO;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.Segment;
import eu.smartenit.sbox.db.dto.SimpleLinkID;

import org.skife.jdbi.v2.DBI;
import org.sqlite.SQLiteConfig;

/**
 * The LinkDAO class.
 * 
 * @authors Antonis Makris, George Petropoulos
 * @version 1.0
 * 
 */
public class LinkDAO {
	
	final AbstractLinkDAO dao;
	
	private CostFunctionDAO cdao;
	
	private SegmentDAO sdao;
	
	private TunnelDAO tdao;
	
	private BGRouterDAO bgdao;
		
	/**
	 * The constructor.
	 */
	public LinkDAO() {
		Properties connectionProperties = new Properties();
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		connectionProperties = config.toProperties(); 
		DBI dbi = new DBI(DbConstants.DBI_URL, connectionProperties);
		dao = dbi.onDemand(AbstractLinkDAO.class);
	}

	/**
	 * The method that creates the Link table.
	 */
	public void createTable() {
		dao.createTable();
	}

	/**
	 * The method that deletes the Link table.
	 */
	public void deleteTable() {
		dao.deleteTable();
	}

	/**
	 * The method that inserts a Link into the Link
	 * table.
	 * 
	 * @param l The Link to be inserted.
	 */
	public void insert(Link l) throws Exception {
		dao.insert(l);
		if (l.getCostFunction() != null) {
			cdao = new CostFunctionDAO();
			cdao.insertByLinkId(l.getCostFunction(), (SimpleLinkID)l.getLinkID());
		}
	}

	/**
	 * The method that updates a Link into the Link
	 * table.
	 * 
	 * @param l The Link to be updated.
	 */
	public void update(Link l) throws Exception {
		dao.update(l);
		sdao = new SegmentDAO();
		sdao.deleteByLinkId((SimpleLinkID)l.getLinkID());
		if (l.getCostFunction() != null) {
			for (Segment s : l.getCostFunction().getSegments())
				sdao.insertByLinkId(s, (SimpleLinkID)l.getLinkID());
		}
	}

	/**
	 * The method that finds all the stored Links from the
	 * Link table.
	 * 
	 * @return The list of stored Links.
	 */
	public List<Link> findAll() {
		List<Link> linksList = dao.findAll();
		cdao = new CostFunctionDAO();
		tdao = new TunnelDAO();
		bgdao = new BGRouterDAO();
		
		for (Link l : linksList) {
			l.setCostFunction(cdao.findByLinkId((SimpleLinkID)l.getLinkID()));
			l.setTraversingTunnels(tdao.findAllByLinkID((SimpleLinkID)l.getLinkID()));
			l.setBgRouter(bgdao.findById(l.getBgRouter().getManagementAddress().getPrefix()));
		}
		return linksList;
	}

	/**
	 * The method that finds a stored Link by its id.
	 * 
	 * @param id The SimpleLinkID.
	 */
	public Link findById(SimpleLinkID id) {
		Link link = dao.findById(id);
		if (link != null) {
			cdao = new CostFunctionDAO();
			tdao = new TunnelDAO();
			bgdao = new BGRouterDAO();
			
			link.setCostFunction(cdao.findByLinkId((SimpleLinkID)link.getLinkID()));
			link.setTraversingTunnels(tdao.findAllByLinkID((SimpleLinkID)link.getLinkID()));
			link.setBgRouter(bgdao.findById(link.getBgRouter().getManagementAddress().getPrefix()));
		}
		return link;
	}
	
	/**
	 * The method that deletes all stored Links.
	 * 
	 */
	public void deleteAll() {
		dao.deleteAll();
	}
	
	/**
	 * The method that deletes a stored Link by its id.
	 * 
	 * @param linkID The SimpleLinkID.
	 */
	public void deleteById(SimpleLinkID linkID) {
		dao.deleteById(linkID);
	}
	
	/**
	 * The method that finds all the stored Links by their BG router
	 * address, as well as their traversing tunnels and assigned bg router.
	 * 
	 * @param bgAddress The BG router address.
	 * @return The list of stored Links.
	 */
	public List<Link> findByBGRouterAddress(String bgAddress) {
		List<Link> linksList = dao.findByBGRouterAddress(bgAddress);
		cdao = new CostFunctionDAO();
		tdao = new TunnelDAO();
		bgdao = new BGRouterDAO();
		
		for (Link l : linksList) {
			l.setCostFunction(cdao.findByLinkId((SimpleLinkID)l.getLinkID()));
			l.setTraversingTunnels(tdao.findAllByLinkID((SimpleLinkID)l.getLinkID()));
			l.setBgRouter(bgdao.findById(bgAddress));
		}
		return linksList;
	}
}
