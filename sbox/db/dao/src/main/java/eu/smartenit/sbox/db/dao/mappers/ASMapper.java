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

import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SBox;

/**
 * The ASMapper class. 
 *
 * @authors George Petropoulos
 * @version 1.0
 * 
 */
public class ASMapper implements ResultSetMapper<AS> {
	
	/**
	 * The method that translates a received resultset into an AS object.
	 *
	 *@param index The index.
	 *@param r The received resultset.
	 *@param ctx The statement context.
	 *
	 *@return The AS object.
	 *
	 *@throws SQLException
	 * 
	 */
	public AS map(int index, ResultSet r, StatementContext ctx)
			throws SQLException {
		NetworkAddressIPv4 managementAddress = new NetworkAddressIPv4(
				r.getString("SBOXADDRPREFIX"), r.getInt("SBOXADDRPREFLENGTH"));
		SBox sbox = new SBox(managementAddress);
		return new AS(r.getInt("ASNUMBER"), r.getBoolean("LOCAL"), null, null,
				sbox, null);
	}
}