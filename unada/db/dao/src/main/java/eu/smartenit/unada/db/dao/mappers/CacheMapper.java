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

import eu.smartenit.unada.db.dto.Cache;

/**
 * The CacheMapper class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public class CacheMapper implements ResultSetMapper<Cache> {

	/**
	 * The method that translates a received resultset into an Cache
	 * object.
	 * 
	 * @param index The index.
	 * @param r The received resultset.
	 * @param ctx The statement context.
	 * 
	 * @return The Cache object.
	 * 
	 * @throws SQLException
	 * 
	 */
	public Cache map(int index, ResultSet r, StatementContext ctx)
			throws SQLException {

		Cache c = new Cache();
		c.setSizeThreshold(r.getLong("sizethreshold"));
		c.setSize(r.getLong("size"));
		c.setTimeToLive(r.getLong("timetolive"));
		c.setSocialThreshold(r.getDouble("socialthreshold"));
		c.setOverlayThreshold(r.getDouble("overlaythreshold"));
		return c;
	}
}