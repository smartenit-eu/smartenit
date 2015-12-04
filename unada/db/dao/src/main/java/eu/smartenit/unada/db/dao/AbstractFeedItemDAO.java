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

import java.util.Iterator;
import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import eu.smartenit.unada.db.dao.binders.BindFeedItem;
import eu.smartenit.unada.db.dao.mappers.FeedItemMapper;
import eu.smartenit.unada.db.dto.FeedItem;

/**
 * The AbstractFeedItemDAO class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public abstract class AbstractFeedItemDAO {

	@SqlUpdate("CREATE TABLE IF NOT EXISTS feeditem  "
			+ "(feeditemid VARCHAR(255) PRIMARY KEY, "
			+ "contentid BIGINT, " 
			+ "userid VARCHAR(255), "
			+ "type VARCHAR(255), "
			+ "time BIGINT, "
			+ "feedtype VARCHAR(255))")
	public abstract void createTable();

	@SqlUpdate("DROP TABLE IF EXISTS feeditem")
	public abstract void deleteTable();

	@SqlUpdate("MERGE INTO feeditem (feeditemid, contentid, userid, type, time, feedtype) "
			+ "VALUES (:f.feeditemid, :f.contentid, :f.userid, :f.type, :f.time, :f.feedtype)")
	public abstract void insert(@BindFeedItem("f") FeedItem f);
	
	@SqlBatch("MERGE INTO feeditem (feeditemid, contentid, userid, type, time, feedtype) "
			+ "VALUES (:f.feeditemid, :f.contentid, :f.userid, :f.type, :f.time, :f.feedtype)")
	@BatchChunkSize(1000)
	public abstract void insertAll(@BindFeedItem("f") Iterator<FeedItem> feedItems);

	@SqlUpdate("UPDATE feeditem " + "SET contentid = :f.contentid, "
			+ "userid = :f.userid, "
			+ "type = :f.type, "
			+ "time = :f.time,"
			+ "feedtype = :f.feedtype "
			+ "WHERE feeditemid = :f.feeditemid")
	public abstract void update(@BindFeedItem("f") FeedItem f);

	@SqlQuery("SELECT * FROM feeditem WHERE feeditemid = :feeditemid")
	@Mapper(FeedItemMapper.class)
	public abstract FeedItem findById(@Bind("feeditemid") String feedItemID);

	@SqlQuery("SELECT * FROM feeditem")
	@Mapper(FeedItemMapper.class)
	public abstract List<FeedItem> findAll();

    @SqlQuery("SELECT * FROM feeditem WHERE contentid = :contentid")
    @Mapper(FeedItemMapper.class)
    public abstract List<FeedItem> findAllByContentID(@Bind("contentid") long contentID);

	@SqlUpdate("DELETE FROM feeditem WHERE feeditemid = :feeditemid")
	public abstract void delete(@Bind("feeditemid") String feedItemID);

	@SqlUpdate("DELETE FROM feeditem")
	public abstract void deleteAll();

}
