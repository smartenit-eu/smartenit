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

import eu.smartenit.unada.db.dao.binders.BindVideoInfo;
import eu.smartenit.unada.db.dao.mappers.VideoInfoMapper;
import eu.smartenit.unada.db.dto.VideoInfo;

/**
 * The AbstractVideoInfoDAO class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public abstract class AbstractVideoInfoDAO {

	@SqlUpdate("CREATE TABLE IF NOT EXISTS videoinfo  "
			+ "(contentid BIGINT PRIMARY KEY, "
			+ "viewsnumber BIGINT, "
			+ "publishdate BIGINT)")
	public abstract void createTable();

	@SqlUpdate("DROP TABLE IF EXISTS videoinfo")
	public abstract void deleteTable();
	
	@SqlUpdate("MERGE INTO videoinfo (contentid, viewsnumber, publishdate) "
			+ "VALUES (:v.contentid, :v.viewsnumber, :v.publishdate)")
	public abstract void insert(@BindVideoInfo("v") VideoInfo v);
	
	@SqlBatch("MERGE INTO videoinfo (contentid, viewsnumber, publishdate) "
			+ "VALUES (:v.contentid, :v.viewsnumber, :v.publishdate)")
	@BatchChunkSize(1000)
	public abstract void insertAll(@BindVideoInfo("v") Iterator<VideoInfo> vList);
	
	@SqlUpdate("UPDATE videoinfo " 
			+ "SET viewsnumber = :v.viewsnumber, "
			+ "publishdate = :v.publishdate " 
			+ "WHERE contentid = :v.contentid")
	public abstract void update(@BindVideoInfo("v") VideoInfo v);

	@SqlQuery("SELECT * FROM videoinfo")
	@Mapper(VideoInfoMapper.class)
	public abstract List<VideoInfo> findAll();

	@SqlQuery("SELECT * FROM videoinfo WHERE contentid = :contentid ")
	@Mapper(VideoInfoMapper.class)
	public abstract VideoInfo findById(@Bind("contentid") long contentid);

	@SqlUpdate("DELETE FROM videoinfo")
	public abstract void deleteAll();

	@SqlUpdate("DELETE FROM videoinfo WHERE contentid = :contentid")
	public abstract void delete(@Bind("contentid") long contentid);

}
