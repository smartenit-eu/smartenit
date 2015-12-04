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

import eu.smartenit.unada.db.dto.Content;
import eu.smartenit.unada.db.dto.Content.ContentCategory;

/**
 * The ContentMapper class.
 * 
 * @authors George Petropoulos
 * @version 2.1
 * 
 */
public class ContentMapper implements ResultSetMapper<Content> {

	/**
	 * The method that translates a received resultset into an Content object.
	 * 
	 * @param index The index.
	 * @param r The received resultset.
	 * @param ctx The statement context.
	 * 
	 * @return The Content object.
	 * 
	 * @throws SQLException
	 * 
	 */
	public Content map(int index, ResultSet r, StatementContext ctx)
			throws SQLException {
		Content c = new Content();
		c.setCacheDate(new Date(r.getLong("cachedate")));
		c.setCacheType(r.getString("cachetype"));
		c.setCacheScore(r.getDouble("cachescore"));
		if (r.getString("category") != null && !r.getString("category").isEmpty()) {
			c.setCategory(ContentCategory.valueOf(r.getString("category")));
		}
		c.setContentID(r.getLong("contentid"));
		c.setDownloaded(r.getBoolean("downloaded"));
		c.setPrefetched(r.getBoolean("prefetched"));
		c.setPath(r.getString("path"));
		c.setQuality(r.getString("quality"));
		c.setSize(r.getLong("size"));
		c.setUrl(r.getString("url"));
        c.setPrefetchedVimeo(r.getBoolean("vimeoprefetched"));
		return c;
	}
}