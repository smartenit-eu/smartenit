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

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import eu.smartenit.unada.db.dao.binders.BindOwner;
import eu.smartenit.unada.db.dao.mappers.OwnerMapper;
import eu.smartenit.unada.db.dto.Owner;

/**
 * The AbstractOwnerDAO class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public abstract class AbstractOwnerDAO {

	@SqlUpdate("CREATE TABLE IF NOT EXISTS owner  "
			+ "(facebookid VARCHAR(255) PRIMARY KEY, " + "oauthtoken VARCHAR(255))")
	public abstract void createTable();

	@SqlUpdate("DROP TABLE IF EXISTS owner")
	public abstract void deleteTable();

	@SqlUpdate("INSERT INTO owner (facebookid, oauthtoken) "
			+ "VALUES (:o.facebookid, :o.oauthtoken)")
	public abstract void insert(@BindOwner("o") Owner o);

	@SqlUpdate("UPDATE owner " + "SET oauthtoken = :o.oauthtoken "
			+ "WHERE facebookid = :o.facebookid")
	public abstract void update(@BindOwner("o") Owner o);

	@SqlQuery("SELECT * FROM owner WHERE facebookid = :id")
	@Mapper(OwnerMapper.class)
	public abstract Owner findById(@Bind("id") String facebookID);

	@SqlQuery("SELECT * FROM owner")
	@Mapper(OwnerMapper.class)
	public abstract List<Owner> findAll();
	
	@SqlQuery("SELECT * FROM owner LIMIT 1")
	@Mapper(OwnerMapper.class)
	public abstract Owner findLast();

	@SqlUpdate("DELETE FROM owner WHERE facebookid = :id")
	public abstract void delete(@Bind("id") String facebookID);

	@SqlUpdate("DELETE FROM owner")
	public abstract void deleteAll();

}
