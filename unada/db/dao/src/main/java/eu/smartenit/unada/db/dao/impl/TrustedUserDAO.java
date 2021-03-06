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
package eu.smartenit.unada.db.dao.impl;

import java.util.Iterator;
import java.util.Properties;
import java.util.List;

import org.skife.jdbi.v2.DBI;

import eu.smartenit.unada.db.dao.AbstractTrustedUserDAO;
import eu.smartenit.unada.db.dao.util.Constants;
import eu.smartenit.unada.db.dto.TrustedUser;

/**
 * The TrustedUserDAO class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public class TrustedUserDAO {
	final AbstractTrustedUserDAO dao;

	/**
	 * The constructor.
	 */
	public TrustedUserDAO() {
		Properties connectionProperties = new Properties();
		DBI dbi = new DBI(Constants.DBI_URL, connectionProperties);
		dao = dbi.onDemand(AbstractTrustedUserDAO.class);
	}

	/**
	 * The method that creates the TrustedUser table.
	 */
	public void createTable() {
		dao.createTable();
	}

	/**
	 * The method that deletes the TrustedUser table.
	 */
	public void deleteTable() {
		dao.deleteTable();
	}

	/**
	 * The method that inserts a TrustedUser entry.
	 */
	public void insert(TrustedUser t) throws Exception {
		dao.insert(t);
	}
	
	/**
	 * The method that inserts a TrustedUser entry.
	 */
	public synchronized void insertAll(Iterator<TrustedUser> trustedUsers) throws Exception {
		dao.insertAll(trustedUsers);
	}
	
	/**
	 * The method that finds a TrustedUser entry by its identifier.
	 */
	public synchronized TrustedUser findById(String facebookID) {
		return dao.findById(facebookID);

	}
	
	/**
	 * The method that finds a TrustedUser entry by its MAC address.
	 */
	public TrustedUser findByMacAddress(String macAddress) {
		return dao.findByMacAddress(macAddress);

	}

	/**
	 * The method that finds all TrustedUser entries.
	 */
	public List<TrustedUser> findAll() {
		return dao.findAll();
	}
	

	/**
	 * The method that updates a TrustedUser entry.
	 */
	public void update(TrustedUser t) {
		dao.update(t);
	}

	/**
	 * The method that deletes a TrustedUser entry by its identifier.
	 */
	public void delete(String facebookID) {
		dao.delete(facebookID);
	}

	/**
	 * The method that deletes all TrustedUser entries.
	 */
	public void deleteAll() {
		dao.deleteAll();
	}
}
