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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import eu.smartenit.unada.db.dto.ContentAccess;

/**
 * The ContentAccessMapper class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public class ContentAccessMapper implements ResultSetMapper<ContentAccess> {

	/**
	 * The method that translates a received resultset into an ContentAccess
	 * object.
	 * 
	 * @param index The index.
	 * @param r The received resultset.
	 * @param ctx The statement context.
	 * 
	 * @return The ContentAccess object.
	 * 
	 * @throws SQLException
	 * 
	 */
	public ContentAccess map(int index, ResultSet r, StatementContext ctx)
			throws SQLException {

		ContentAccess c = new ContentAccess();
		c.setTimeStamp(new Date(r.getLong("timeStamp")));
		c.setFacebookID(r.getString("facebookID"));
		c.setContentID(r.getLong("contentID"));
		return c;
	}
}