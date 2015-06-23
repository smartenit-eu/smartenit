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
package eu.smartenit.sbox.db.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import eu.smartenit.sbox.db.dto.ChargingRule;
import eu.smartenit.sbox.db.dto.OperationModeSDN;
import eu.smartenit.sbox.db.dto.SystemControlParameters;

/**
 * The SystemControlParametersMapper class.
 * 
 * @authors George Petropoulos
 * @version 3.1
 * 
 */
public class SystemControlParametersMapper implements
		ResultSetMapper<SystemControlParameters> {
	
	/**
	 * The method that translates a received resultset into a SystemControlParameters object.
	 * 
	 * @param index The index.
	 * @param r The received resultset.
	 * @param ctx The statement context.
	 * 
	 * @return The SystemControlParameters object.
	 * 
	 * @throws SQLException
	 * 
	 */
	public SystemControlParameters map(int index, ResultSet r,
			StatementContext ctx) throws SQLException {
		SystemControlParameters s = new SystemControlParameters();
		s.setChargingRule(ChargingRule.valueOf(r.getString("chargingRule")));
		s.setCompensationThreshold(r.getDouble("compensationThreshold"));
		s.setOperationModeSDN(OperationModeSDN.valueOf(r.getString("operationModeSDN")));
		s.setDelayTolerantTrafficManagement(r.getBoolean("delayTolerantTrafficManagement"));
		return s;
	}
}
