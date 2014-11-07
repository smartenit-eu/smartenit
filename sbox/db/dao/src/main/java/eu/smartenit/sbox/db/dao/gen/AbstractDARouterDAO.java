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

import eu.smartenit.sbox.db.dao.binders.BindDARouter;
import eu.smartenit.sbox.db.dao.mappers.DARouterMapper;
import eu.smartenit.sbox.db.dto.DARouter;

/**
 * The abstract DARouterDAO class, including all SQL queries.
 *
 * @authors Antonis Makris, George Petropoulos
 * @version 1.0
 * 
 */
public abstract class AbstractDARouterDAO {

	@SqlUpdate("CREATE TABLE IF NOT EXISTS DAROUTER  "
			+ "(ADDRESS STRING PRIMARY KEY, " 
			+ "SDNCONTROLLERADDRESS STRING, "
			+ "SNMPCOMMUNITY STRING, "
			+ "FOREIGN KEY (SDNCONTROLLERADDRESS) "
			+ "REFERENCES SDNCONTROLLER(ADDRESS) ON DELETE CASCADE)")
	public abstract void createTable();

	
	@SqlUpdate("DROP TABLE IF EXISTS DAROUTER")
	public abstract void deleteTable();

	
	@SqlUpdate("INSERT INTO DAROUTER  "
			+ "(ADDRESS, " 
			+ "SNMPCOMMUNITY) "
			+ "VALUES (:d.address, :d.snmpCommunity)")
	public abstract void insert(@BindDARouter("d") DARouter d);
	
	@SqlUpdate("INSERT INTO DAROUTER  "
			+ "(ADDRESS, " 
			+ "SNMPCOMMUNITY, "
			+ "SDNCONTROLLERADDRESS) "
			+ "VALUES (:d.address, :d.snmpCommunity, :sdnAddress)")
	public abstract void insertBySDNControllerAddress(@BindDARouter("d") DARouter d, 
			@Bind("sdnAddress") String sdnAddress);
	
	
	@SqlUpdate("UPDATE DAROUTER  "
			+ "SET SNMPCOMMUNITY=:d.snmpCommunity "
			+ "WHERE ADDRESS = :d.address")
	public abstract void update(@BindDARouter("d") DARouter d);
	
	
	@SqlUpdate("UPDATE DAROUTER  "
			+ "SET SDNCONTROLLERADDRESS = :sdnAddress "
			+ "WHERE ADDRESS = :daAddress")
	public abstract void updateBySDNControllerAddress(@Bind("daAddress") String daRouterAddress, 
			@Bind("sdnAddress") String sdnAddress);
	
	
	@SqlQuery("SELECT "
			+ "ADDRESS, " 
			+ "SNMPCOMMUNITY " 
			+ "FROM DAROUTER")
	@Mapper(DARouterMapper.class)
	public abstract List<DARouter> findAll();
	
	@SqlQuery("SELECT "
			+ "ADDRESS, " 
			+ "SNMPCOMMUNITY " 
			+ "FROM DAROUTER WHERE ADDRESS = :address")
	@Mapper(DARouterMapper.class)
	public abstract DARouter findById(@Bind("address") String address);
	
	@SqlQuery("SELECT "
			+ "ADDRESS, " 
			+ "SNMPCOMMUNITY " 
			+ "FROM DAROUTER WHERE SDNCONTROLLERADDRESS = :sdnAddress")
	@Mapper(DARouterMapper.class)
	public abstract List<DARouter> findBySDNControllerAddress(@Bind("sdnAddress") String sdnAddress);
	
	@SqlUpdate("DELETE FROM DAROUTER "
			+ "WHERE ADDRESS = :address")
	public abstract void deleteById(@Bind("address") String address);

	@SqlUpdate("DELETE FROM DAROUTER")
	public abstract void deleteAll();

}
