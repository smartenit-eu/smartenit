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
package eu.smartenit.sbox.db.dao;

import java.util.List;
import java.util.Properties;

import eu.smartenit.sbox.db.dao.gen.*;
import eu.smartenit.sbox.db.dto.CloudDC;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.DC2DCCommunicationID;
import eu.smartenit.sbox.db.dto.SDNController;

import org.skife.jdbi.v2.DBI;
import org.sqlite.SQLiteConfig;

/**
 * The DC2DCCommunicationDAO class.
 * 
 * @authors Antonis Makris, George Petropoulos
 * @version 3.0
 * 
 */
public class DC2DCCommunicationDAO {
	final AbstractDC2DCCommunicationDAO dao;

	private CloudDCDAO cdao;
	
	private TunnelDAO tudao;
	
	private DARouterDAO dadao;
	
	private SDNControllerDAO sdndao;
	
	private ASDAO asdao;

	/**
	 * The constructor.
	 */
	public DC2DCCommunicationDAO() {
		Properties connectionProperties = new Properties();
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		connectionProperties = config.toProperties(); 
		DBI dbi = new DBI(DbConstants.DBI_URL, connectionProperties);
		dao = dbi.onDemand(AbstractDC2DCCommunicationDAO.class);
	}

	/**
	 * The method that creates the DC2DCCommunication table.
	 */
	public void createTable() {
		dao.createTable();
	}

	/**
	 * The method that deletes the DC2DCCommunication table.
	 */
	public void deleteTable() {
		dao.deleteTable();
	}

	/**
	 * The method that inserts a DC2DCCommunication into the DC2DCCommunication
	 * table.
	 * 
	 * @param d The DC2DCCommunication to be inserted.
	 */
	public void insert(DC2DCCommunication d) throws Exception {
		dao.insert(d);
	}

	/**
	 * The method that finds all the stored DC2DCCommunications from the
	 * DC2DCCommunication table.
	 * 
	 * @return The list of stored DC2DCCommunications.
	 */
	public List<DC2DCCommunication> findAll() {
		return dao.findAll();
	}

	/**
	 * The method that finds a stored DC2DCCommunication by its id.
	 * 
	 * @param id The DC2DCCommunicationID.
	 */
	public DC2DCCommunication findById(DC2DCCommunicationID id) {
		return dao.findById(id);
	}

	/**
	 * The method that deletes a stored DC2DCCommunication by its id.
	 * 
	 * @param id The DC2DCCommunicationID.
	 */
	public void deleteById(DC2DCCommunicationID id) {
		dao.deleteById(id);
	}
	
	/**
	 * The method that finds all the stored DC2DCCommunications with their attached clouds 
	 * from the DC2DCCommunication table.
	 * 
	 * @return The list of stored DC2DCCommunications.
	 */
	public List<DC2DCCommunication> findAllDC2DCCommunicationsCloudsTunnels() {
		List<DC2DCCommunication> dc2dcList = dao.findAll();
		cdao = new CloudDCDAO();
		dadao = new DARouterDAO();
		sdndao = new SDNControllerDAO();
		asdao = new ASDAO();
		tudao = new TunnelDAO();
		for (DC2DCCommunication dc2dc : dc2dcList) {
			
			CloudDC localCloud = cdao.findById(dc2dc.getId().getLocalCloudDCName());
			//setting da router
			localCloud.setDaRouter(
					dadao.findById(localCloud.getDaRouter().getManagementAddress().getPrefix()));
			//setting sdn controller and its da routers
			SDNController sdnController = sdndao.findById(localCloud.getSdnController().getManagementAddress().getPrefix());
			sdnController.setDaRouters(dadao.findBySDNControllerAddress(sdnController.getManagementAddress().getPrefix()));
			localCloud.setSdnController(sdnController);
			//setting as with all required information
			localCloud.setAs(
					asdao.findASCloudsBgRoutersLinksByASNumber(dc2dc.getId().getLocalAsNumber()));
			dc2dc.setLocalCloud(localCloud);
			
			//setting remote cloud
			CloudDC remoteCloud = cdao.findById(dc2dc.getId().getRemoteCloudDCName());
			dc2dc.setRemoteCloud(remoteCloud);
			
			//setting connecting tunnels
			dc2dc.setConnectingTunnels(tudao.findAllByDC2DCCommunicationID(dc2dc.getId()));
		}
		return dc2dcList;
	}

	/**
	 * The method that deletes all stored DC2DCCommunications.
	 * 
	 */
	public void deleteAll() {
		dao.deleteAll();		
	}

}
