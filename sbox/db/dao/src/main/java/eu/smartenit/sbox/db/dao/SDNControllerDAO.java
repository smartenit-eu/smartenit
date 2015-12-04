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
import eu.smartenit.sbox.db.dto.SDNController;

import org.skife.jdbi.v2.DBI;
import org.sqlite.SQLiteConfig;

/**
 * The SDNControllerDAO class.
 *
 * @authors Antonis Makris, George Petropoulos
 * @version 1.0
 * 
 */
public class SDNControllerDAO {
	final AbstractSDNControllerDAO dao;
	
	/**
	 * The constructor.
	 */
	public SDNControllerDAO() {
		Properties connectionProperties = new Properties();
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		connectionProperties = config.toProperties(); 
		DBI dbi = new DBI(DbConstants.DBI_URL, connectionProperties);
		dao = dbi.onDemand(AbstractSDNControllerDAO.class);
	}

	/**
	 * The method that creates the SDNController table.
	 */
	public void createTable() {
		dao.createTable();
	}

	/**
	 * The method that deletes the SDNController table.
	 */
	public void deleteTable() {
		dao.deleteTable();
	}

	/**
	 * The method that inserts a SDNController into the SDNController table.
	 * 
	 * @param s The SDNController to be inserted.
	 */
	public void insert(SDNController s) throws Exception   {
		dao.insert(s);
	}
	
	/**
	 * The method that updates a stored SDNController into the SDNController table.
	 * 
	 * @param s The SDNController to be updated.
	 */
	public void update(SDNController s) {
		dao.update(s);
	}

	/**
	 * The method that finds all the stored SDNControllers from the SDNController table.
	 * 
	 * @return The list of stored SDNControllers.
	 */
	public List<SDNController> findAll() {
		return dao.findAll();
	}

	/**
	 * The method that finds a stored SDNController by its address.
	 * 
	 * @param address The SDNController address.
	 * @return The stored SDN Controller.
	 */
	public SDNController findById(String address) {
		return dao.findById(address);
	}
	
	/**
	 * The method that deletes a stored SDNController by its address.
	 * 
	 * @param address The SDNController address.
	 */
	public void deleteById(String address) {
		dao.deleteById(address);
	}

	/**
	 * The method that deletes all stored SDNControllers.
	 * 
	 */
	public void deleteAll() {
		dao.deleteAll();	
	}

}
