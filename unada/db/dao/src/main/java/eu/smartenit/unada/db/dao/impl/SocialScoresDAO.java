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

import eu.smartenit.unada.db.dao.AbstractSocialPredictionParametersDAO;
import eu.smartenit.unada.db.dao.AbstractSocialScoresDAO;
import eu.smartenit.unada.db.dao.util.Constants;
import eu.smartenit.unada.db.dto.SocialPredictionParameters;
import eu.smartenit.unada.db.dto.SocialScores;
import org.skife.jdbi.v2.DBI;

import java.util.List;
import java.util.Properties;

/**
 * The SocialScoresDAO class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public class SocialScoresDAO {
	final AbstractSocialScoresDAO dao;

	/**
	 * The constructor.
	 */
	public SocialScoresDAO() {
		Properties connectionProperties = new Properties();
		DBI dbi = new DBI(Constants.DBI_URL, connectionProperties);
		dao = dbi.onDemand(AbstractSocialScoresDAO.class);
	}

	/**
	 * The method that creates the SocialScores table.
	 */
	public void createTable() {
		dao.createTable();
	}

	/**
	 * The method that deletes the SocialScores table.
	 */
	public void deleteTable() {
		dao.deleteTable();
	}

	/**
	 * The method that inserts a SocialScores entry.
	 */
	public void insert(SocialScores s) throws Exception {
		dao.insert(s);
	}
	

	/**
	 * The method that finds all SocialScores entries.
	 */
	public List<SocialScores> findAll() {
		return dao.findAll();
	}


	/**
	 * The method that deletes all SocialScores entries.
	 */
	public void deleteAll() {
		dao.deleteAll();
	}
}
