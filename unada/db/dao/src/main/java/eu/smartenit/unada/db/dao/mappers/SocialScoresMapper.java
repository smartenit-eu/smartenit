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
package eu.smartenit.unada.db.dao.mappers;

import eu.smartenit.unada.db.dto.SocialPredictionParameters;
import eu.smartenit.unada.db.dto.SocialScores;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The SocialScoresMapper class.
 * 
 * @authors George Petropoulos
 * @version 3.1
 * 
 */
public class SocialScoresMapper
	implements ResultSetMapper<SocialScores> {

	/**
	 * The method that translates a received resultset into an SocialScores
	 * object.
	 *
	 * @param index The index.
	 * @param r The received resultset.
	 * @param ctx The statement context.
	 *
	 * @return The SocialScores object.
	 *
	 * @throws java.sql.SQLException
	 * 
	 */
	public SocialScores map(int index, ResultSet r, StatementContext ctx)
			throws SQLException {

		SocialScores s = new SocialScores();
        s.setContentID(r.getLong("contentid"));
        s.setAlpha(r.getDouble("alpha"));
        s.setDelta(r.getDouble("delta"));
        s.setEta(r.getDouble("eta"));
        s.setGamma(r.getDouble("gamma"));
        s.setPhi(r.getDouble("phi"));
		return s;

	}
}