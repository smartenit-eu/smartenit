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
package eu.smartenit.unada.db.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import eu.smartenit.unada.db.dto.SocialPredictionParameters;

/**
 * The SocialPredictionParametersMapper class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public class SocialPredictionParametersMapper 
	implements ResultSetMapper<SocialPredictionParameters> {

	/**
	 * The method that translates a received resultset into an SocialPredictionParameters
	 * object.
	 * 
	 * @param index The index.
	 * @param r The received resultset.
	 * @param ctx The statement context.
	 * 
	 * @return The SocialPredictionParameters object.
	 * 
	 * @throws SQLException
	 * 
	 */
	public SocialPredictionParameters map(int index, ResultSet r, StatementContext ctx)
			throws SQLException {

		SocialPredictionParameters s = new SocialPredictionParameters();
		s.setLambda1(r.getDouble("lambda1"));
		s.setLambda2(r.getDouble("lambda2"));
		s.setLambda3(r.getDouble("lambda3"));
		s.setLambda4(r.getDouble("lambda4"));
		s.setLambda5(r.getDouble("lambda5"));
		s.setLambda6(r.getDouble("lambda6"));
		return s;

	}
}