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
package eu.smartenit.unada.db.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import eu.smartenit.unada.db.dao.binders.BindSocialPredictionParameters;
import eu.smartenit.unada.db.dao.mappers.SocialPredictionParametersMapper;
import eu.smartenit.unada.db.dto.SocialPredictionParameters;

/**
 * The AbstractSocialPredictionParametersDAO class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public abstract class AbstractSocialPredictionParametersDAO {

	@SqlUpdate("CREATE TABLE IF NOT EXISTS socialpredictionparameters  "
			+ "(id BIGINT AUTO_INCREMENT PRIMARY KEY, " 
			+ "lambda1 REAL, "
			+ "lambda2 REAL, " 
			+ "lambda3 REAL, " 
			+ "lambda4 REAL, "
			+ "lambda5 REAL, " 
			+ "lambda6 REAL)")
	public abstract void createTable();

	@SqlUpdate("DROP TABLE IF EXISTS socialpredictionparameters")
	public abstract void deleteTable();

	@SqlUpdate("INSERT INTO socialpredictionparameters (lambda1, lambda2, lambda3, "
			+ "lambda4, lambda5, lambda6) "
			+ "VALUES (:s.lambda1, :s.lambda2, :s.lambda3, :s.lambda4, "
			+ ":s.lambda5, :s.lambda6)")
	public abstract void insert(@BindSocialPredictionParameters("s") SocialPredictionParameters s);

	@SqlQuery("SELECT * FROM socialpredictionparameters")
	@Mapper(SocialPredictionParametersMapper.class)
	public abstract List<SocialPredictionParameters> findAll();

	@SqlUpdate("DELETE FROM socialpredictionparameters")
	public abstract void deleteAll();

	@SqlQuery("SELECT * FROM socialpredictionparameters LIMIT 1")
	@Mapper(SocialPredictionParametersMapper.class)
	public abstract SocialPredictionParameters findLast();

}
