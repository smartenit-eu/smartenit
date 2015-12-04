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

import org.skife.jdbi.v2.DBI;
import org.sqlite.SQLiteConfig;

import eu.smartenit.sbox.db.dao.gen.AbstractDARouterDAO;
import eu.smartenit.sbox.db.dto.DARouter;

/**
 * The DARouterDAO class.
 *
 * @authors Antonis Makris, George Petropoulos
 * @version 1.0
 * 
 */
public class DARouterDAO {
	final AbstractDARouterDAO dao;
	
	/**
	 * The constructor.
	 */
	public DARouterDAO() {
		Properties connectionProperties = new Properties();
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		connectionProperties = config.toProperties(); 
		DBI dbi = new DBI(DbConstants.DBI_URL, connectionProperties);
		dao = dbi.onDemand(AbstractDARouterDAO.class);
	}

	/**
	 * The method that creates the DARouter table.
	 */
	public void createTable() {
		dao.createTable();
	}

	/**
	 * The method that deletes the DARouter table.
	 */
	public void deleteTable() {
		dao.deleteTable();
	}

	/**
	 * The method that inserts a DARouter into the DARouter table.
	 * 
	 * @param d The DARouter to be inserted.
	 */
	public void insert(DARouter d) throws Exception {
		dao.insert(d);
	}
	
	/**
	 * The method that inserts a DARouter for a certain SDN controller address 
	 * into the DARouter table.
	 * 
	 * @param d The DARouter to be inserted.
	 * @param sdnAddress The SDN controller address.
	 */
	public void insertBySDNControllerAddress(DARouter d, String sdnAddress) throws Exception {
		dao.insertBySDNControllerAddress(d, sdnAddress);
	}

	/**
	 * The method that updates a stored DARouter into the DARouter table.
	 * 
	 * @param d The DARouter to be updated.
	 */
	public void update(DARouter d) {
		dao.update(d);
	}
	
	/**
	 * The method that updates a stored DARouter of a certain SDN controller address 
	 * into the DARouter table .
	 * 
	 * @param daRouterAddress The address of the DARouter to be updated.
	 * @param sdnAddress The SDN controller address.
	 */
	public void updateBySDNControllerAddress(String daRouterAddress, String sdnAddress) {
		dao.updateBySDNControllerAddress(daRouterAddress, sdnAddress);
	}

	/**
	 * The method that finds all the stored DARouters from the DARouter table.
	 * 
	 * @return The list of stored DARouters.
	 */
	public List<DARouter> findAll() {
		return dao.findAll();
	}

	/**
	 * The method that finds a stored DARouter by its address.
	 * 
	 * @param address The DARouter address.
	 * @return The stored DARouter.
	 */
	public DARouter findById(String address) {
		return dao.findById(address);
	}
	
	/**
	 * The method that finds a stored DARouter by its SDN controller address.
	 * 
	 * @param sdnAddress The SDN controller address.
	 * @return The list of stored DARouters.
	 */
	public List<DARouter> findBySDNControllerAddress(String sdnAddress) {
		return dao.findBySDNControllerAddress(sdnAddress);
	}
	
	/**
	 * The method that deletes a stored DARouter by its address.
	 * 
	 * @param address The DARouter address.
	 */
	public void deleteById(String address) {
		dao.deleteById(address);
	}

	/**
	 * The method that deletes all stored DARouters.
	 * 
	 */
	public void deleteAll() {
		dao.deleteAll();
	}
	
	

}
