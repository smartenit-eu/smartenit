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
package eu.smartenit.sbox.db.dao.gen;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import eu.smartenit.sbox.db.dao.binders.BindAS;
import eu.smartenit.sbox.db.dao.mappers.ASMapper;
import eu.smartenit.sbox.db.dto.AS;

/**
 * The abstract ASDAO class, including all SQL queries.
 *
 * @authors Antonis Makris, George Petropoulos
 * @version 1.0
 * 
 */
public abstract class AbstractASDAO {

	@SqlUpdate("CREATE TABLE IF NOT EXISTS ASYSTEM  " 
			+ "(ASNUMBER INTEGER, "
			+ "LOCAL BOOLEAN," + "SBOXADDRPREFLENGTH INTEGER,"
			+ "SBOXADDRPREFIX STRING," + "ISPNAME STRING,"
			+ "PRIMARY KEY (ASNUMBER),"
			+ "FOREIGN KEY (ISPNAME) REFERENCES ISP(ISPNAME))")
	public abstract void createTable();

	
	@SqlUpdate("drop table IF EXISTS ASYSTEM")
	public abstract void deleteTable();


	@SqlUpdate("INSERT INTO ASYSTEM (ASNUMBER, LOCAL, SBOXADDRPREFLENGTH, SBOXADDRPREFIX, ISPNAME)"
			+ " VALUES (:a.asNumber, :a.local, 0, :a.prfx, NULL)")
	public abstract void insert(@BindAS("a") AS as);

	@SqlQuery("select * from ASYSTEM")
	@Mapper(ASMapper.class)
	public abstract List<AS> findAll();

	@SqlQuery("select * from ASYSTEM  where local  =  1")
	@Mapper(ASMapper.class)
	public abstract List<AS> findLocalAs();

	@SqlQuery("select * from ASYSTEM  where local  =  0 ")
	@Mapper(ASMapper.class)
	public abstract List<AS> findRemoteAs();

	@SqlQuery("SELECT * from ASYSTEM where ASNUMBER =:asNumber")
	@Mapper(ASMapper.class)
	public abstract AS findById(@Bind("asNumber") int asNumber);

	@SqlUpdate("UPDATE ASYSTEM SET SBOXADDRPREFIX = :a.prfx "
			+ "WHERE ASNUMBER =:a.asNumber")
	public abstract void update(@BindAS("a") AS as);

	@SqlUpdate("DELETE FROM ASYSTEM WHERE ASNUMBER =:asNumber")
	public abstract void deleteById(@Bind("asNumber") int asNumber);

	@SqlUpdate("DELETE FROM ASYSTEM")
	public abstract void deleteAll();

}
