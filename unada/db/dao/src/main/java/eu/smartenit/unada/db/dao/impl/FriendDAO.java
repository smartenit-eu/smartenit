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

import eu.smartenit.unada.db.dao.AbstractFriendDAO;
import eu.smartenit.unada.db.dao.util.Constants;
import eu.smartenit.unada.db.dto.Friend;

/**
 * The FriendDAO class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public class FriendDAO {
	final AbstractFriendDAO dao;

	/**
	 * The constructor.
	 */
	public FriendDAO() {
		Properties connectionProperties = new Properties();
		DBI dbi = new DBI(Constants.DBI_URL, connectionProperties);
		dao = dbi.onDemand(AbstractFriendDAO.class);
	}

	/**
	 * The method that creates the Friend table.
	 */
	public void createTable() {
		dao.createTable();
	}

	/**
	 * The method that deletes the Friend table.
	 */
	public void deleteTable() {
		dao.deleteTable();
	}

	/**
	 * The method that inserts a Friend entry.
	 */
	public void insert(Friend f) throws Exception {
		dao.insert(f);
	}
	
	/**
	 * The method that inserts a batch of Friend entries.
	 */
	public synchronized void insertAll(Iterator<Friend> friends) throws Exception {
		dao.insertAll(friends);
	}
	
	/**
	 * The method that finds a Friend entry by its identifier.
	 */
	public Friend findById(String facebookID) {
		return dao.findById(facebookID);

	}

	/**
	 * The method that finds all Friend entries.
	 */
	public List<Friend> findAll() {
		return dao.findAll();
	}
	

	/**
	 * The method that updates a Friend entry.
	 */
	public void update(Friend f) {
		dao.update(f);
	}

	/**
	 * The method that deletes a Friend entry by its identifier.
	 */
	public void delete(String facebookID) {
		dao.delete(facebookID);
	}

	/**
	 * The method that deletes all Friend entries.
	 */
	public void deleteAll() {
		dao.deleteAll();
	}
}
