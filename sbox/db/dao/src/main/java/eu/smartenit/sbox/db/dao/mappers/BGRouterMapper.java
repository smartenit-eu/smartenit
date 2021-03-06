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

import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;

/**
 * The BGRouterMapper class.
 * 
 * @authors George Petropoulos
 * @version 3.1
 * 
 */
public class BGRouterMapper implements ResultSetMapper<BGRouter> {
	
	/**
	 * The method that translates a received resultset into a BGRouter object.
	 * 
	 * @param index The index.
	 * @param r The received resultset.
	 * @param ctx The statement context.
	 * 
	 * @return The BGRouter object.
	 * 
	 * @throws SQLException
	 * 
	 */
	public BGRouter map(int index, ResultSet r, StatementContext ctx)
			throws SQLException {
		NetworkAddressIPv4 managementAddress = new NetworkAddressIPv4(
				r.getString("managementAddressPrefix"), 0);
		BGRouter bg = new BGRouter(managementAddress, r.getString("snmpCommunity"),
				null);
		bg.setNetconfUsername(r.getString("netconfUsername"));
		bg.setNetconfPassword(r.getString("netconfPassword"));
		return bg;
	}
}