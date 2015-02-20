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

import eu.smartenit.unada.db.dao.binders.BindTrustedUser;
import eu.smartenit.unada.db.dao.mappers.TrustedUserMapper;
import eu.smartenit.unada.db.dto.TrustedUser;

/**
 * The AbstractTrustedUserDAO class.
 * 
 * @authors George Petropoulos
 * @version 2.1
 * 
 */
public abstract class AbstractTrustedUserDAO {

	@SqlUpdate("CREATE TABLE IF NOT EXISTS trusteduser  "
			+ "(facebookid VARCHAR(255) PRIMARY KEY, " + "macaddress VARCHAR(255), "
            + "lastaccess BIGINT)")
	public abstract void createTable();

	@SqlUpdate("DROP TABLE IF EXISTS trusteduser")
	public abstract void deleteTable();

	@SqlUpdate("MERGE INTO trusteduser (facebookid, macaddress, lastaccess) "
			+ "VALUES (:t.facebookid, "
			+ "COALESCE((SELECT macaddress FROM trusteduser "
			+ "WHERE facebookid = :t.facebookid), :t.macaddress), "
            + "COALESCE((SELECT lastaccess FROM trusteduser "
            + "WHERE facebookid = :t.facebookid), :t.lastaccess)"
            + ")")
	public abstract void insert(@BindTrustedUser("t") TrustedUser t);
	
	@SqlBatch("MERGE INTO trusteduser (facebookid, macaddress, lastaccess) "
			+ "VALUES (:t.facebookid, "
			+ "COALESCE((SELECT macaddress FROM trusteduser "
			+ "WHERE facebookid = :t.facebookid), :t.macaddress), "
            + "COALESCE((SELECT lastaccess FROM trusteduser "
            + "WHERE facebookid = :t.facebookid), :t.lastaccess)"
			+ ")")
	@BatchChunkSize(1000)
	public abstract void insertAll(@BindTrustedUser("t") Iterator<TrustedUser> trustedUsers);

	@SqlUpdate("UPDATE trusteduser " + "SET macaddress = :t.macaddress, "
            + "lastaccess = :t.lastaccess "
			+ "WHERE facebookid = :t.facebookid")
	public abstract void update(@BindTrustedUser("t") TrustedUser t);

	@SqlQuery("SELECT * FROM trusteduser WHERE facebookid = :id")
	@Mapper(TrustedUserMapper.class)
	public abstract TrustedUser findById(@Bind("id") String facebookID);
	
	@SqlQuery("SELECT * FROM trusteduser WHERE macaddress = :macaddress LIMIT 1")
	@Mapper(TrustedUserMapper.class)
	public abstract TrustedUser findByMacAddress(@Bind("macaddress") String macAddress);

	@SqlQuery("SELECT * FROM trusteduser")
	@Mapper(TrustedUserMapper.class)
	public abstract List<TrustedUser> findAll();

	@SqlUpdate("DELETE FROM trusteduser WHERE facebookid = :id")
	public abstract void delete(@Bind("id") String facebookID);

	@SqlUpdate("DELETE FROM trusteduser")
	public abstract void deleteAll();

}
