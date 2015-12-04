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
package eu.smartenit.sbox.db.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import eu.smartenit.sbox.db.dto.CostFunction;

/**
 * The CostFunctionMapper class.
 * 
 * @authors George Petropoulos
 * @version 1.0
 * 
 */
public class CostFunctionMapper implements ResultSetMapper<CostFunction> {
	
	/**
	 * The method that translates a received resultset into a CostFunction object.
	 * 
	 * @param index The index.
	 * @param r The received resultset.
	 * @param ctx The statement context.
	 * 
	 * @return The CostFunction object.
	 * 
	 * @throws SQLException
	 * 
	 */
	public CostFunction map(int index, ResultSet r, StatementContext ctx)
			throws SQLException {
		CostFunction costFunction = new CostFunction();
		costFunction.setType(r.getString("TYPE"));
		costFunction.setSubtype(r.getString("SUBTYPE"));
		costFunction.setInputUnit(r.getString("INPUTUNIT"));
		costFunction.setOutputUnit(r.getString("OUTPUTUNIT"));

		return costFunction;
	}
}
