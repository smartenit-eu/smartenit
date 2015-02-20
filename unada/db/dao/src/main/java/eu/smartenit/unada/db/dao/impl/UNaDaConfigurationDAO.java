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
package eu.smartenit.unada.db.dao.impl;

import java.util.Properties;
import java.util.List;

import org.skife.jdbi.v2.DBI;

import eu.smartenit.unada.db.dao.AbstractUNaDaConfigurationDAO;
import eu.smartenit.unada.db.dao.util.Constants;
import eu.smartenit.unada.db.dto.UNaDaConfiguration;

/**
 * The UNaDaConfigurationDAO class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public class UNaDaConfigurationDAO {
	final AbstractUNaDaConfigurationDAO dao;

	/**
	 * The constructor.
	 */
	public UNaDaConfigurationDAO() {
		Properties connectionProperties = new Properties();
		DBI dbi = new DBI(Constants.DBI_URL, connectionProperties);
		dao = dbi.onDemand(AbstractUNaDaConfigurationDAO.class);
	}

	/**
	 * The method that creates the UNaDaConfiguration table.
	 */
	public void createTable() {
		dao.createTable();
	}

	/**
	 * The method that deletes the UNaDaConfiguration table.
	 */
	public void deleteTable() {
		dao.deleteTable();
	}

	/**
	 * The method that inserts a UNaDaConfiguration entry.
	 */
	public void insert(UNaDaConfiguration u) throws Exception {
		dao.deleteAll();
		dao.insert(u);
	}
	

	/**
	 * The method that finds all UNaDaConfiguration entries.
	 */
	public List<UNaDaConfiguration> findAll() {
		return dao.findAll();
	}
	
	/**
	 * The method that finds the last UNaDaConfiguration entry.
	 */
	public UNaDaConfiguration findLast() {
		return dao.findLast();

	}

	/**
	 * The method that deletes all UNaDaConfiguration entries.
	 */
	public void deleteAll() {
		dao.deleteAll();
	}
}
