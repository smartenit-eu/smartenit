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

import java.util.Iterator;
import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import eu.smartenit.unada.db.dao.binders.BindFriend;
import eu.smartenit.unada.db.dao.mappers.FriendMapper;
import eu.smartenit.unada.db.dto.Friend;

/**
 * The AbstractFriendDAO class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public abstract class AbstractFriendDAO {

	@SqlUpdate("CREATE TABLE IF NOT EXISTS friend  "
			+ "(facebookid VARCHAR(255) PRIMARY KEY, " 
			+ "facebookname VARCHAR(255))")
	public abstract void createTable();

	@SqlUpdate("DROP TABLE IF EXISTS friend")
	public abstract void deleteTable();

	@SqlUpdate("MERGE INTO friend (facebookid, facebookname) "
			+ "VALUES (:f.facebookid, :f.facebookname)")
	public abstract void insert(@BindFriend("f") Friend f);
	
	@SqlBatch("MERGE INTO friend (facebookid, facebookname) "
			+ "VALUES (:f.facebookid, :f.facebookname)")
	@BatchChunkSize(1000)
	public abstract void insertAll(@BindFriend("f") Iterator<Friend> friends);

	@SqlUpdate("UPDATE friend " + "SET facebookname = :f.facebookname "
			+ "WHERE facebookid = :f.facebookid")
	public abstract void update(@BindFriend("f") Friend t);

	@SqlQuery("SELECT * FROM friend WHERE facebookid = :id")
	@Mapper(FriendMapper.class)
	public abstract Friend findById(@Bind("id") String facebookID);

	@SqlQuery("SELECT * FROM friend")
	@Mapper(FriendMapper.class)
	public abstract List<Friend> findAll();

	@SqlUpdate("DELETE FROM friend WHERE facebookid = :id")
	public abstract void delete(@Bind("id") String facebookID);

	@SqlUpdate("DELETE FROM friend")
	public abstract void deleteAll();

}
