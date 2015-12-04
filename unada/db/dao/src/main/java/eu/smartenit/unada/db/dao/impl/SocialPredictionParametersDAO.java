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

import eu.smartenit.unada.db.dao.AbstractSocialPredictionParametersDAO;
import eu.smartenit.unada.db.dao.util.Constants;
import eu.smartenit.unada.db.dto.SocialPredictionParameters;

/**
 * The SocialPredictionParametersDAO class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public class SocialPredictionParametersDAO {
	final AbstractSocialPredictionParametersDAO dao;

	/**
	 * The constructor.
	 */
	public SocialPredictionParametersDAO() {
		Properties connectionProperties = new Properties();
		DBI dbi = new DBI(Constants.DBI_URL, connectionProperties);
		dao = dbi.onDemand(AbstractSocialPredictionParametersDAO.class);
	}

	/**
	 * The method that creates the SocialPredictionParameters table.
	 */
	public void createTable() {
		dao.createTable();
	}

	/**
	 * The method that deletes the SocialPredictionParameters table.
	 */
	public void deleteTable() {
		dao.deleteTable();
	}

	/**
	 * The method that inserts a SocialPredictionParameters entry.
	 */
	public void insert(SocialPredictionParameters s) throws Exception {
		dao.deleteAll();
		dao.insert(s);
	}
	

	/**
	 * The method that finds all SocialPredictionParameters entries.
	 */
	public List<SocialPredictionParameters> findAll() {
		return dao.findAll();
	}
	
	/**
	 * The method that finds the last SocialPredictionParameters entry.
	 */
	public SocialPredictionParameters findLast() {
		return dao.findLast();

	}

	/**
	 * The method that deletes all SocialPredictionParameters entries.
	 */
	public void deleteAll() {
		dao.deleteAll();
	}
}
