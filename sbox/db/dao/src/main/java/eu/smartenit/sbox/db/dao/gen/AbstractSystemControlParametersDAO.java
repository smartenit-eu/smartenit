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
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import eu.smartenit.sbox.db.dao.mappers.SystemControlParametersMapper;
import eu.smartenit.sbox.db.dto.SystemControlParameters;

/**
 * The abstract SystemControlParametersDAO class, including all SQL queries.
 *
 * @author George Petropoulos
 * @version 3.1
 * 
 */
public abstract class AbstractSystemControlParametersDAO {
	
	@SqlUpdate("CREATE TABLE IF NOT EXISTS SystemControlParameters "
			+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "chargingRule TEXT, "
			+ "operationModeSDN TEXT, "
			+ "compensationThreshold REAL, "
			+ "delayTolerantTrafficManagement BOOLEAN)")
	public abstract void createTable();

	
	@SqlUpdate("DROP TABLE IF EXISTS SystemControlParameters")
	public abstract void deleteTable();
	
	
	@SqlUpdate("INSERT INTO SystemControlParameters "
			+ "(chargingRule, operationModeSDN, compensationThreshold, delayTolerantTrafficManagement) "
			+ "VALUES (:chargingRule, :operationModeSDN, :compensationThreshold, "
			+ ":delayTolerantTrafficManagement)")
	@GetGeneratedKeys
	public abstract long insert(@BindBean SystemControlParameters s);

	
	@SqlQuery("SELECT chargingRule, operationModeSDN, compensationThreshold, delayTolerantTrafficManagement "
			+ "FROM SystemControlParameters")
	@Mapper(SystemControlParametersMapper.class)
	public abstract List<SystemControlParameters> findAll();

	
	@SqlQuery("SELECT chargingRule, operationModeSDN, compensationThreshold, delayTolerantTrafficManagement "
			+ "FROM SystemControlParameters where ID =:id")
	@Mapper(SystemControlParametersMapper.class)
	public abstract SystemControlParameters findById(@Bind("id") long id);

	
	@SqlUpdate("UPDATE SystemControlParameters SET chargingRule = :chargingRule, "
			+ "operationModeSDN = :operationModeSDN, "
			+ "compensationThreshold = :compensationThreshold,"
			+ "delayTolerantTrafficManagement = :delayTolerantTrafficManagement "
			+ "WHERE ID = :id")
	public abstract void update(@Bind("id") long id, @BindBean SystemControlParameters s);

	
	@SqlUpdate("DELETE FROM SystemControlParameters  WHERE ID = :id")
	public abstract void deleteById(@Bind("id") long id);

	
	@SqlUpdate("DELETE FROM SystemControlParameters ")
	public abstract void deleteAll();

}