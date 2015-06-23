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

import eu.smartenit.sbox.db.dao.binders.BindBGRouter;
import eu.smartenit.sbox.db.dao.mappers.*;
import eu.smartenit.sbox.db.dto.BGRouter;

/**
 * The abstract BGRouterDAO class, including all SQL queries.
 *
 * @authors Antonis Makris, George Petropoulos
 * @version 3.1
 * 
 */
public abstract class AbstractBGRouterDAO {

	@SqlUpdate("CREATE TABLE IF NOT EXISTS BGRouter "
			+ "(managementAddressPrefix String," 
			+ "snmpCommunity String, "
			+ "ASNUMBER INTEGER, "
			+ "netconfUsername STRING, "
			+ "netconfPassword STRING, "
			+ "PRIMARY KEY (managementAddressPrefix), "
			+ "FOREIGN KEY (ASNUMBER) REFERENCES ASYSTEM(ASNUMBER) ON DELETE CASCADE)")
	public abstract void createTable();

	@SqlUpdate("drop table IF EXISTS BGRouter")
	public abstract void deleteTable();

	@SqlUpdate("INSERT INTO BGRouter (managementAddressPrefix, snmpCommunity, "
			+ "netconfUsername, netconfPassword) "
			+ "VALUES (:b.address, :b.snmpCommunity, :b.netconfUsername, :b.netconfPassword)")
	public abstract void insert(@BindBGRouter("b") BGRouter bgrouter);
	
	@SqlUpdate("INSERT INTO BGRouter (managementAddressPrefix, snmpCommunity, ASNUMBER, "
			+ "netconfUsername, netconfPassword) "
			+ "VALUES (:b.address, :b.snmpCommunity, :asNumber, "
			+ ":b.netconfUsername, :b.netconfPassword)")
	public abstract void insertByASNumber(@BindBGRouter("b") BGRouter bgrouter, 
			@Bind("asNumber") int asNumber);

	@SqlQuery("select * from BGRouter")
	@Mapper(BGRouterMapper.class)
	public abstract List<BGRouter> findAll();

	@SqlQuery("SELECT * from BGRouter "
			+ "where managementAddressPrefix =:address")
	@Mapper(BGRouterMapper.class)
	public abstract BGRouter findById(@Bind("address") String address);

	@SqlUpdate("UPDATE BGRouter "
			+ "SET snmpCommunity = :b.snmpCommunity, "
			+ "netconfUsername = :b.netconfUsername, "
			+ "netconfPassword = :b.netconfPassword "
			+ "WHERE managementAddressPrefix = :b.address")
	public abstract void update(@BindBGRouter("b") BGRouter bgr);

	@SqlUpdate("DELETE FROM BGRouter "
			+ "WHERE managementAddressPrefix = :address")
	public abstract void deleteById(@Bind("address") String address);

	@SqlUpdate("DELETE FROM BGRouter")
	public abstract void deleteAll();
	
	@SqlQuery("SELECT * FROM BGRouter WHERE ASNUMBER = :asNumber")
	@Mapper(BGRouterMapper.class)
	public abstract List<BGRouter> findByASNumber(@Bind("asNumber") int asNumber);

}
