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

import eu.smartenit.sbox.db.dao.gen.AbstractSystemControlParametersDAO;
import eu.smartenit.sbox.db.dto.SystemControlParameters;

import org.skife.jdbi.v2.DBI;
import org.sqlite.SQLiteConfig;

/**
 * The SystemControlParametersDAO class.
 *
 * @author George Petropoulos
 * @version 3.0
 * 
 */
public class SystemControlParametersDAO {
	final AbstractSystemControlParametersDAO dao;
	
	/**
	 * The constructor.
	 */
	public SystemControlParametersDAO() {
		Properties connectionProperties = new Properties();
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		connectionProperties = config.toProperties(); 
		DBI dbi = new DBI(DbConstants.DBI_URL, connectionProperties);
		dao = dbi.onDemand(AbstractSystemControlParametersDAO.class);
	}

	/**
	 * The method that creates the SystemControlParameters table.
	 */
	public void createTable() {
		dao.createTable();
	}

	/**
	 * The method that deletes the SystemControlParameters table.
	 */
	public void deleteTable() {
		dao.deleteTable();
	}

	/**
	 * The method that inserts a SystemControlParameters 
	 * into the SystemControlParameters table.
	 * 
	 * @param s The SystemControlParameters to be inserted.
	 */
	public long insert(SystemControlParameters s) throws Exception {
		return dao.insert(s);
	}

	/**
	 * The method that finds all the stored SystemControlParameters 
	 * from the SystemControlParameters table.
	 * 
	 * @return The list of stored SystemControlParameters.
	 */
	public List<SystemControlParameters> findAll() {
		return dao.findAll();
	}
	
	/**
	 * The method that deletes all the stored SystemControlParameters 
	 * from the SystemControlParameters table.
	 * 
	 */
	public void deleteAll() {
		dao.deleteAll();
	}
	
	/**
	 * The method that finds the last stored SystemControlParameters.
	 * 
	 * @return The last stored SystemControlParameters.
	 */
	public SystemControlParameters findLast() {
		SystemControlParameters s = new SystemControlParameters();
		List<SystemControlParameters> list = findAll();
		if (list.size() != 0 && list != null)
			s = list.get(list.size()-1);
		return s;
	}

}
