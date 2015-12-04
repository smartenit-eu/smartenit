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

import java.util.Properties;
import java.util.List;

import org.skife.jdbi.v2.DBI;

import eu.smartenit.unada.db.dao.AbstractOwnerDAO;
import eu.smartenit.unada.db.dao.util.Constants;
import eu.smartenit.unada.db.dto.Owner;

/**
 * The OwnerDAO class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public class OwnerDAO {
	final AbstractOwnerDAO dao;

	/**
	 * The constructor.
	 */
	public OwnerDAO() {
		Properties connectionProperties = new Properties();
		DBI dbi = new DBI(Constants.DBI_URL, connectionProperties);
		dao = dbi.onDemand(AbstractOwnerDAO.class);
	}

	/**
	 * The method that creates the Owner table.
	 */
	public void createTable() {
		dao.createTable();
	}

	/**
	 * The method that deletes the Owner table.
	 */
	public void deleteTable() {
		dao.deleteTable();
	}

	/**
	 * The method that inserts a Owner entry.
	 */
	public void insert(Owner o) throws Exception {
		dao.deleteAll();
		dao.insert(o);
	}
	
	/**
	 * The method that finds a Owner entry by its identifier.
	 */
	public Owner findById(String facebookID) {
		return dao.findById(facebookID);

	}

	/**
	 * The method that finds all Owner entries.
	 */
	public List<Owner> findAll() {
		return dao.findAll();
	}
	
	/**
	 * The method that finds the last Owner entry.
	 */
	public Owner findLast() {
		return dao.findLast();

	}

	/**
	 * The method that updates a Owner entry.
	 */
	public void update(Owner o) {
		dao.update(o);
	}

	/**
	 * The method that deletes a Owner entry by its identifier.
	 */
	public void delete(String facebookID) {
		dao.delete(facebookID);
	}

	/**
	 * The method that deletes all Owner entries.
	 */
	public void deleteAll() {
		dao.deleteAll();
	}
}
