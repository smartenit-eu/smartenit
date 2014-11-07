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

import eu.smartenit.sbox.db.dao.gen.*;
import eu.smartenit.sbox.db.dto.ISP;

import org.skife.jdbi.v2.DBI;
import org.sqlite.SQLiteConfig;

/**
 * The ISPDAO class.
 *
 * @authors Antonis Makris, George Petropoulos
 * @version 1.0
 * 
 */
public class ISPDAO {
	final AbstractISPDAO dao;

	/**
	 * The constructor.
	 */
	public ISPDAO() {
		Properties connectionProperties = new Properties();
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		connectionProperties = config.toProperties(); 
		DBI dbi = new DBI(DbConstants.DBI_URL, connectionProperties);
		dao = dbi.onDemand(AbstractISPDAO.class);
	}

	/**
	 * The method that creates the ISP table.
	 */
	public void createTable() {
		dao.createTable();
	}

	/**
	 * The method that deletes the ISP table.
	 */
	public void deleteTable() {
		dao.deleteTable();
	}

	/**
	 * The method that inserts a ISP into the ISP table.
	 * 
	 * @param isp The ISP to be inserted.
	 */
	public void insert(ISP isp) {
		dao.insert(isp);
	}

	/**
	 * The method that finds all the stored ISPs from the ISP table.
	 * 
	 * @return The list of stored ISPs.
	 */
	public List<ISP> findAll() {
		return (dao.findAll());
	}

	/**
	 * The method that deletes all the stored ISPs from the ISP table.
	 * 
	 */
	public void deleteAll() {
		dao.deleteAll();
	}

	/**
	 * The method that finds the last stored ISP.
	 * 
	 * @return The last stored ISP.
	 */
	public ISP findLast() {
		ISP t = new ISP();
		List<ISP> list = findAll();
		if (list.size() != 0 && list != null)
			t = list.get(list.size() - 1);
		return t;
	}


	/**
	 * The method that finds a stored ISP by its name.
	 * 
	 * @param ispName The ISP name.
	 * @return The stored ISP.
	 */
	public ISP findById(String ispName) {
		return dao.findById(ispName);
	}

}
