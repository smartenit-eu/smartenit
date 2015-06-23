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
package eu.smartenit.unada.db.dao;

import eu.smartenit.unada.db.dao.binders.BindSocialPredictionParameters;
import eu.smartenit.unada.db.dao.binders.BindSocialScores;
import eu.smartenit.unada.db.dao.mappers.SocialPredictionParametersMapper;
import eu.smartenit.unada.db.dao.mappers.SocialScoresMapper;
import eu.smartenit.unada.db.dto.SocialPredictionParameters;
import eu.smartenit.unada.db.dto.SocialScores;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.List;

/**
 * The AbstractSocialPredictionParametersDAO class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public abstract class AbstractSocialScoresDAO {

	@SqlUpdate("CREATE TABLE IF NOT EXISTS socialscores  "
			+ "(contentid BIGINT PRIMARY KEY, "
			+ "alpha REAL, "
			+ "delta REAL, "
			+ "eta REAL, "
			+ "phi REAL, "
			+ "gamma REAL)")
	public abstract void createTable();

	@SqlUpdate("DROP TABLE IF EXISTS socialscores")
	public abstract void deleteTable();

	@SqlUpdate("MERGE INTO socialscores (contentid, alpha, delta, "
			+ "eta, phi, gamma) "
			+ "VALUES (:s.contentid, :s.alpha, :s.delta, " +
            ":s.eta, :s.phi, :s.gamma)")
	public abstract void insert(@BindSocialScores("s") SocialScores s);

	@SqlQuery("SELECT * FROM socialscores")
	@Mapper(SocialScoresMapper.class)
	public abstract List<SocialScores> findAll();

	@SqlUpdate("DELETE FROM socialscores")
	public abstract void deleteAll();
}
