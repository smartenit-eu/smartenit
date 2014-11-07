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

import org.skife.jdbi.v2.DBI;
import org.sqlite.SQLiteConfig;

import eu.smartenit.sbox.db.dao.gen.AbstractPrefixDAO;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;

/**
 * The PrefixDAO class.
 *
 * @authors Antonis Makris, George Petropoulos
 * @version 1.0
 * 
 */
public class PrefixDAO {
	final AbstractPrefixDAO dao;
	
	/**
	 * The constructor.
	 */
	public PrefixDAO() {
		Properties connectionProperties = new Properties();
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		connectionProperties = config.toProperties(); 
		DBI dbi = new DBI(DbConstants.DBI_URL, connectionProperties);
		dao = dbi.onDemand(AbstractPrefixDAO.class);
	}

	/**
	 * The method that creates the Prefix table.
	 */
	public void createTable() {
		dao.createTable();
	}

	/**
	 * The method that deletes the Prefix table.
	 */
	public void deleteTable() {
		dao.deleteTable();
	}

	/**
	 * The method that inserts a Prefix into the Prefix table.
	 * 
	 * @param p The Prefix to be inserted.
	 */
	public void insert(NetworkAddressIPv4 p) throws Exception  {
		dao.insert(p);
	}
	
	/**
	 * The method that inserts a Prefix of a cloud into the Prefix table.
	 * 
	 * @param p The Prefix to be inserted.
	 * @param cloudName The cloud name.
	 */
	public int insertByCloudName(NetworkAddressIPv4 p, String cloudName) throws Exception {
		return dao.insertByCloudName(p, cloudName);
	}

	/**
	 * The method that finds all the stored Prefixes from the Prefix table.
	 * 
	 * @return The list of stored Prefixes.
	 */
	public List<NetworkAddressIPv4> findAll() {
		return dao.findAll();
	}
	
	/**
	 * The method that finds all the stored Prefixes of a cloud 
	 * from the Prefix table.
	 * 
	 * @param cloudName The cloud name.
	 * @return The list of stored Prefixes.
	 */
	public List<NetworkAddressIPv4> findAllByCloudName(String cloudName) {
		return dao.findAllByCloudName(cloudName);
	}
	
	/**
	 * The method that deletes all the stored Prefixes of a cloud 
	 * from the Prefix table.
	 * 
	 * @param cloudName The cloud name.
	 */
	public void deleteByCloudName(String cloudName) {
		dao.deleteByCloudName(cloudName);
	}

	/**
	 * The method that deletes all the stored Prefixes  
	 * from the Prefix table.
	 * 
	 */
	public void deleteAll() {
		dao.deleteAll();
	}

}
