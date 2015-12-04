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

import eu.smartenit.sbox.db.dao.mappers.TimeScheduleParametersMapper;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;

/**
 * The abstract TimeScheduleParametersDAO class, including all SQL queries.
 *
 * @author George Petropoulos
 * @version 3.0
 * 
 */
public abstract class AbstractTimeScheduleParametersDAO {
	
	@SqlUpdate("CREATE TABLE IF NOT EXISTS TimeScheduleParameters "
			+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "startDate DATE, "
			+ "accountingPeriod LONG, "
			+ "reportingPeriod LONG, "
			+ "tol1 REAL, "
			+ "tol2 REAL, "
			+ "samplingPeriod LONG, "
			+ "reportPeriodEA LONG, "
			+ "reportPeriodDTM LONG, "
			+ "compensationPeriod LONG)")
	public abstract void createTable();

	
	@SqlUpdate("DROP TABLE IF EXISTS TimeScheduleParameters")
	public abstract void deleteTable();
	
	
	@SqlUpdate("INSERT INTO TimeScheduleParameters "
			+ "(startDate, accountingPeriod , reportingPeriod, tol1, tol2, "
			+ "samplingPeriod, reportPeriodEA, reportPeriodDTM, compensationPeriod) "
			+ "VALUES (:startDate, :accountingPeriod, :reportingPeriod, :tol1, :tol2, "
			+ ":samplingPeriod, :reportPeriodEA, :reportPeriodDTM, :compensationPeriod)")
	@GetGeneratedKeys
	public abstract long insert(@BindBean TimeScheduleParameters tsp);

	
	@SqlQuery("SELECT id, startDate, accountingPeriod, reportingPeriod, tol1, tol2, "
			+ "samplingPeriod, reportPeriodEA, reportPeriodDTM, compensationPeriod  "
			+ "from TimeScheduleParameters")
	@Mapper(TimeScheduleParametersMapper.class)
	public abstract List<TimeScheduleParameters> findAll();

	
	@SqlQuery("SELECT id, startDate, accountingPeriod, reportingPeriod, tol1, tol2, "
			+ "samplingPeriod, reportPeriodEA, reportPeriodDTM, compensationPeriod "
			+ "from TimeScheduleParameters where ID =:id")
	@Mapper(TimeScheduleParametersMapper.class)
	public abstract TimeScheduleParameters findById(@Bind("id") long id);

	
	@SqlUpdate("UPDATE TimeScheduleParameters SET startDate = :startDate, "
			+ "accountingPeriod = :accountingPeriod, reportingPeriod = :reportingPeriod, "
			+ "tol1 = :tol1, tol2 = :tol2, samplingPeriod = :samplingPeriod, "
			+ "reportPeriodEA = :reportPeriodEA, reportPeriodDTM = :reportPeriodDTM, "
			+ "compensationPeriod = :compensationPeriod "
			+ "WHERE ID = :id")
	public abstract void update(@Bind("id") long id, @BindBean TimeScheduleParameters tsp);

	
	@SqlUpdate("DELETE FROM TimeScheduleParameters  WHERE ID = :id")
	public abstract void deleteById(@Bind("id") long id);

	
	@SqlUpdate("DELETE FROM TimeScheduleParameters ")
	public abstract void deleteAll();

}