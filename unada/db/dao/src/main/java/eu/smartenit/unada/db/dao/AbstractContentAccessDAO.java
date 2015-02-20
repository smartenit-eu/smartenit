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

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import eu.smartenit.unada.db.dao.binders.BindContentAccess;
import eu.smartenit.unada.db.dao.mappers.ContentAccessMapper;
import eu.smartenit.unada.db.dto.ContentAccess;

/**
 * The AbstractContentAccessDAO class.
 * 
 * @authors George Petropoulos
 * @version 2.1
 * 
 */
public abstract class AbstractContentAccessDAO {

	@SqlUpdate("CREATE TABLE IF NOT EXISTS contentaccess  "
			+ "(id BIGINT AUTO_INCREMENT PRIMARY KEY, "
			+ "timestamp BIGINT, "
			+ "facebookid VARCHAR(255), "
			+ "contentid BIGINT, "
            + "FOREIGN KEY (contentid) REFERENCES content(contentid) ON DELETE CASCADE)")
	public abstract void createTable();

	@SqlUpdate("DROP TABLE IF EXISTS contentaccess")
	public abstract void deleteTable();

	@SqlUpdate("INSERT INTO contentaccess (timestamp, facebookid, contentid) "
			+ "VALUES (:c.timestamp, :c.facebookid, :c.contentid)")
	public abstract void insert(@BindContentAccess("c") ContentAccess cont);

	@SqlQuery("SELECT * FROM contentaccess")
	@Mapper(ContentAccessMapper.class)
	public abstract List<ContentAccess> findAll();

	@SqlQuery("SELECT * FROM contentaccess WHERE contentid = :contentid ")
	@Mapper(ContentAccessMapper.class)
	public abstract List<ContentAccess> findByContentID(@Bind("contentid") long contentID);

	@SqlQuery("SELECT * FROM contentaccess WHERE contentid = :contentid AND "
			+ "facebookid = :facebookid ORDER BY timestamp DESC LIMIT 1")
	@Mapper(ContentAccessMapper.class)
	public abstract ContentAccess findLatestByContentIDFacebookID(@Bind("contentid") 
		long contentID, @Bind("facebookid") String facebookID);

    @SqlQuery("SELECT * FROM contentaccess WHERE contentid = :contentid "
            + "ORDER BY timestamp DESC LIMIT 1")
    @Mapper(ContentAccessMapper.class)
    public abstract ContentAccess findLatestByContentID(@Bind("contentid") long contentID);
	
	@SqlUpdate("DELETE FROM contentaccess")
	public abstract void deleteAll();

	@SqlUpdate("DELETE FROM contentaccess WHERE contentid = :contentid")
	public abstract void deleteByContentID(@Bind("contentid") long contentID);

}
