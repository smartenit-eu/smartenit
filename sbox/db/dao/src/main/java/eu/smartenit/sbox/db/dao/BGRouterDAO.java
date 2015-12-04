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

import eu.smartenit.sbox.db.dao.gen.AbstractBGRouterDAO;
import eu.smartenit.sbox.db.dto.BGRouter;

import org.skife.jdbi.v2.DBI;
import org.sqlite.SQLiteConfig;

/**
 * The BGRouterDAO class.
 *
 * @authors Antonis Makris, George Petropoulos
 * @version 1.0
 * 
 */
public class BGRouterDAO {
	final AbstractBGRouterDAO dao;

	/**
	 * The constructor.
	 */
	public BGRouterDAO() {
		Properties connectionProperties = new Properties();
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		connectionProperties = config.toProperties(); 
		DBI dbi = new DBI(DbConstants.DBI_URL, connectionProperties);
		dao = dbi.onDemand(AbstractBGRouterDAO.class);
	}

	/**
	 * The method that creates the BGRouter table.
	 */
	public void createTable() {
		dao.createTable();
	}

	/**
	 * The method that deletes the BGRouter table.
	 */
	public void deleteTable() {
		dao.deleteTable();
	}

	/**
	 * The method that inserts a BGRouter into the BGRouter table.
	 * 
	 * @param bg The BGRouter to be inserted.
	 */
	public void insert(BGRouter bg) throws Exception {
		dao.insert(bg);
	}
	
	/**
	 * The method that inserts a BGRouter for an asNumber into the BGRouter table.
	 * 
	 * @param bgrouter The BGRouter to be inserted.
	 * @param asNumber The as number
	 */
	public void insertByASNumber(BGRouter bgrouter, int asNumber) {
		dao.insertByASNumber(bgrouter, asNumber);
	}

	/**
	 * The method that finds all the stored BGRouters from the BGRouter table.
	 * 
	 * @return The list of stored BGRouters.
	 */
	public List<BGRouter> findAll() {
		return (dao.findAll());
	}

	/**
	 * The method that deletes all the stored BGRouters from the BGRouter table.
	 * 
	 */
	public void deleteAll() {
		dao.deleteAll();
	}

	/**
	 * The method that finds the last stored BGRouter.
	 * 
	 * @return The last stored BGRouter.
	 */
	public BGRouter findLast() {
		BGRouter t = null;
		List<BGRouter> list = findAll();
		if (list.size() != 0 && list != null)
			t = list.get(list.size() - 1);
		return t;
	}

	/**
	 * The method that updates a stored BGRouter into the BGRouter table.
	 * 
	 * @param bg The BGRouter to be updated.
	 */
	public void update(BGRouter bg) {
		dao.update(bg);
	}

	/**
	 * The method that finds a stored BGRouter by its address.
	 * 
	 * @param address The BGRouter address.
	 * @return The stored BGRouter.
	 */
	public BGRouter findById(String address) {
		return dao.findById(address);
	}
	
	/**
	 * The method that deletes a stored BGRouter by its address.
	 * 
	 * @param address The BGRouter address.
	 */
	public void deleteById(String address) {
		dao.deleteById(address);
	}
	
	/**
	 * The method that finds the list of BGRouters by their AS number.
	 * 
	 * @param asNumber The AS number.
	 * @return The list of stored BGRouters.
	 */
	public List<BGRouter> findByASNumber(int asNumber) {
		return dao.findByASNumber(asNumber);
	}

}
