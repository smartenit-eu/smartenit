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

import eu.smartenit.sbox.db.dao.gen.AbstractTimeScheduleParametersDAO;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;

import org.skife.jdbi.v2.DBI;
import org.sqlite.SQLiteConfig;

/**
 * The TimeScheduleParametersDAO class.
 *
 * @authors Antonis Makris, George Petropoulos
 * @version 1.0
 * 
 */
public class TimeScheduleParametersDAO {
	final AbstractTimeScheduleParametersDAO dao;
	
	/**
	 * The constructor.
	 */
	public TimeScheduleParametersDAO() {
		Properties connectionProperties = new Properties();
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		connectionProperties = config.toProperties(); 
		DBI dbi = new DBI(DbConstants.DBI_URL, connectionProperties);
		dao = dbi.onDemand(AbstractTimeScheduleParametersDAO.class);
	}

	/**
	 * The method that creates the TimeScheduleParameters table.
	 */
	public void createTable() {
		dao.createTable();
	}

	/**
	 * The method that deletes the TimeScheduleParameters table.
	 */
	public void deleteTable() {
		dao.deleteTable();
	}

	/**
	 * The method that inserts a TimeScheduleParameters 
	 * into the TimeScheduleParameters table.
	 * 
	 * @param tscp The TimeScheduleParameters to be inserted.
	 */
	public long insert(TimeScheduleParameters tscp) throws Exception {
		return dao.insert(tscp);
	}

	/**
	 * The method that finds all the stored TimeScheduleParameterss 
	 * from the TimeScheduleParameters table.
	 * 
	 * @return The list of stored TimeScheduleParameterss.
	 */
	public List<TimeScheduleParameters> findAll() {
		return dao.findAll();
	}
	
	/**
	 * The method that deletes all the stored TimeScheduleParameterss 
	 * from the TimeScheduleParameters table.
	 * 
	 */
	public void deleteAll() {
		dao.deleteAll();
	}
	
	/**
	 * The method that finds the last stored TimeScheduleParameters.
	 * 
	 * @return The last stored TimeScheduleParameters.
	 */
	public TimeScheduleParameters findLast() {
		TimeScheduleParameters t = new TimeScheduleParameters();
		List<TimeScheduleParameters> list = findAll();
		if (list.size() != 0 && list != null)
			t = list.get(list.size()-1);
		return t;
	}

}
