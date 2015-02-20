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

import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import eu.smartenit.unada.db.dao.binders.BindCache;
import eu.smartenit.unada.db.dao.mappers.CacheMapper;
import eu.smartenit.unada.db.dto.Cache;

/**
 * The AbstractCacheDAO class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public abstract class AbstractCacheDAO {

	@SqlUpdate("CREATE TABLE IF NOT EXISTS cache "
			+ "(id BIGINT AUTO_INCREMENT PRIMARY KEY, "
			+ "size BIGINT, "
			+ "sizethreshold BIGINT, "
			+ "timetolive BIGINT, "
			+ "socialthreshold REAL, "
			+ "overlaythreshold REAL)")
	public abstract void createTable();

	@SqlUpdate("DROP TABLE IF EXISTS cache")
	public abstract void deleteTable();

	@SqlUpdate("INSERT INTO cache (size, sizethreshold, timetolive, socialthreshold, "
			+ "overlaythreshold) "
			+ "VALUES (:c.size, :c.sizethreshold, :c.timetolive, :c.socialthreshold, "
			+ ":c.overlaythreshold)")
	public abstract void insert(@BindCache("c") Cache c);

	@SqlQuery("SELECT * FROM cache LIMIT 1")
	@Mapper(CacheMapper.class)
	public abstract Cache findLast();
	
	@SqlQuery("SELECT * FROM cache")
	@Mapper(CacheMapper.class)
	public abstract List<Cache> findAll();

	@SqlUpdate("DELETE FROM cache")
	public abstract void deleteAll();

}
