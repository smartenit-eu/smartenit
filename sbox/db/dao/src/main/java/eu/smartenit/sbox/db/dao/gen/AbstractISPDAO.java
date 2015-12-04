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
package eu.smartenit.sbox.db.dao.gen;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import eu.smartenit.sbox.db.dao.mappers.*;
import eu.smartenit.sbox.db.dto.ISP;

/**
 * The abstract ISPDAO class, including all SQL queries.
 *
 * @authors Antonis Makris, George Petropoulos
 * @version 1.0
 * 
 */
public abstract class AbstractISPDAO {

	@SqlUpdate("CREATE TABLE IF NOT EXISTS ISP " 
			+ "(ISPNAME STRING, "
			+ "PRIMARY KEY (ISPNAME))")
	public abstract void createTable();

	@SqlUpdate("drop table IF EXISTS ISP")
	public abstract void deleteTable();

	@SqlUpdate("INSERT INTO ISP (ISPNAME) " 
			+ "VALUES (:ispName)")
	public abstract void insert(@BindBean ISP isp);

	@SqlQuery("select * from ISP")
	@Mapper(ISPMapper.class)
	public abstract List<ISP> findAll();

	@SqlQuery("SELECT * from ISP where ISPNAME =:ispName")
	@Mapper(ISPMapper.class)
	public abstract ISP findById(@Bind("ispName") String ispName);

	@SqlUpdate("DELETE FROM ISP WHERE ISPNAME = :ispName")
	public abstract void deleteById(@Bind("ispName") String ispName);

	@SqlUpdate("DELETE FROM ISP ")
	public abstract void deleteAll();

}
