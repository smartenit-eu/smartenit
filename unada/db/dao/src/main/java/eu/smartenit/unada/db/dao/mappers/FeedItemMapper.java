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

import eu.smartenit.unada.db.dto.FeedItem;
import eu.smartenit.unada.db.dto.FeedItem.FeedType;

/**
 * The FeedItemMapper class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public class FeedItemMapper implements ResultSetMapper<FeedItem> {

	/**
	 * The method that translates a received resultset into an FeedItem
	 * object.
	 * 
	 * @param index The index.
	 * @param r The received resultset.
	 * @param ctx The statement context.
	 * 
	 * @return The FeedItem object.
	 * 
	 * @throws SQLException
	 * 
	 */
	public FeedItem map(int index, ResultSet r, StatementContext ctx)
			throws SQLException {

		FeedItem f = new FeedItem();
		f.setFeedItemID(r.getString("feeditemid"));
		f.setContentID(r.getLong("contentid"));
		f.setTime(new Date(r.getLong("time")));
		f.setType(r.getString("type"));
		f.setUserID(r.getString("userid"));
		String feedtype = r.getString("feedtype");
		if (feedtype != null && !feedtype.isEmpty()) {
			f.setFeedType(FeedType.valueOf(feedtype));
		}
		return f;

	}
}