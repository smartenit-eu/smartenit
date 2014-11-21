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

import eu.smartenit.sbox.db.dao.gen.AbstractTunnelDAO;
import eu.smartenit.sbox.db.dto.Tunnel;
import eu.smartenit.sbox.db.dto.EndAddressPairTunnelID;
import eu.smartenit.sbox.db.dto.DC2DCCommunicationID;
import eu.smartenit.sbox.db.dto.SimpleLinkID;

import org.skife.jdbi.v2.DBI;
import org.sqlite.SQLiteConfig;

/**
 * The TunnelDAO class.
 * 
 * @authors Antonis Makris, George Petropoulos
 * @version 1.2
 * 
 */
public class TunnelDAO {
	final AbstractTunnelDAO dao;
	
	private LinkDAO ldao;

	/**
	 * The constructor.
	 */
	public TunnelDAO() {
		Properties connectionProperties = new Properties();
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		connectionProperties = config.toProperties(); 
		DBI dbi = new DBI(DbConstants.DBI_URL, connectionProperties);
		dao = dbi.onDemand(AbstractTunnelDAO.class);
	}

	/**
	 * The method that creates the Tunnel table.
	 */
	public void createTable() {
		dao.createTable();
	}

	/**
	 * The method that deletes the Tunnel table.
	 */
	public void deleteTable() {
		dao.deleteTable();
	}

	/**
	 * The method that inserts a Tunnel into the Tunnel
	 * table.
	 * 
	 * @param t The Tunnel to be inserted.
	 */
	public void insert(Tunnel t) throws Exception {
		dao.insert(t);
	}

	/**
	 * The method that inserts a Tunnel into the Tunnel
	 * table.
	 * 
	 * @param t The Tunnel to be inserted.
	 */
	public void update(Tunnel t) {
		dao.update(t);
	}

	/**
	 * The method that finds all the stored Tunnels from the
	 * Tunnel table.
	 * 
	 * @return The list of stored Tunnels.
	 */
	public List<Tunnel> findAll() {
		return dao.findAll();
	}

	/**
	 * The method that finds a stored Tunnel by its id.
	 * 
	 * @param id The SimpleTunnelID.
	 */
	public Tunnel findById(EndAddressPairTunnelID id) {
		return dao.findById(id);
	}
	
	/**
	 * The method that finds all stored Tunnels by their linkID.
	 * 
	 * @param linkID The SimpleLinkID.
	 */
	public List<Tunnel> findAllByLinkID(SimpleLinkID linkID) {
		return dao.findAllByLinkID(linkID);
	}
	
	/**
	 * The method that deletes all stored Tunnels.
	 * 
	 */
	public void deleteAll() {
		dao.deleteAll();
	}
	
	/**
	 * The method that deletes a stored Tunnel by its id.
	 * 
	 * @param id The SimpleTunnelID.
	 */
	public void deleteById(EndAddressPairTunnelID id) {
		dao.deleteById(id);
	}
	
	/**
	 * The method that finds all stored Tunnels by their DC2DCCommunicationID.
	 * 
	 * @param dcID The DC2DCCommunicationID.
	 */
	public List<Tunnel> findAllByDC2DCCommunicationID(DC2DCCommunicationID dcID) {
		List<Tunnel> tunnels = dao.findAllByDC2DCCommunicationID(dcID);
		ldao = new LinkDAO();
		for (Tunnel t : tunnels) {
			t.setLink(ldao.findById((SimpleLinkID)t.getLink().getLinkID()));
		}
		return tunnels;
	}
	
	/**
	 * The method that deletes all stored Tunnels by their DC2DCCommunicationID.
	 * 
	 * @param dcID The DC2DCCommunicationID.
	 */
	public void deleteByDC2DCCommunicationID(DC2DCCommunicationID dcID) {
		dao.deleteByDC2DCCommunicationID(dcID);
	}
	
	/**
	 * The method that updates a stored Tunnel with its DC2DCCommunicationID.
	 * 
	 * @param tunnelID The SimpleTunnelID.
	 * @param dcID The DC2DCCommunicationID.
	 */
	public void updateByDC2DCCommunicationID(EndAddressPairTunnelID tunnelID, DC2DCCommunicationID dcID) {
		dao.updateByDC2DCCommunicationID(tunnelID, dcID);
	}

}
