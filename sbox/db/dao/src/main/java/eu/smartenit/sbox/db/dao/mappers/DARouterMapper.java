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
import java.util.ArrayList;
import java.util.List;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;

/**
 * The DARouterMapper class.
 * 
 * @authors George Petropoulos
 * @version 3.0
 * 
 */
public class DARouterMapper implements ResultSetMapper<DARouter> {
	
	/**
	 * The method that translates a received resultset into a DARouter object.
	 * 
	 * @param index The index.
	 * @param r The received resultset.
	 * @param ctx The statement context.
	 * 
	 * @return The DARouter object.
	 * 
	 * @throws SQLException
	 * 
	 */
	public DARouter map(int index, ResultSet r, StatementContext ctx)
			throws SQLException {
		DARouter d = new DARouter();
		d.setManagementAddress(new NetworkAddressIPv4(r.getString("ADDRESS"), 0));
		d.setSnmpCommunity(r.getString("SNMPCOMMUNITY"));
        d.setOfSwitchDPID(r.getString("OFSWITCHDPID"));
        d.setLocalDCOfSwitchPortNumbers(
        		stringToList(r.getString("LOCALDCOFSWITCHPORTNUMBERS")));
		return d;

	}
	
	private List<Integer> stringToList(String str) {
		List<Integer> list = new ArrayList<Integer>();
		String[] ints = str.substring(1, str.length()-1).split(",");
		for (String s : ints) {
			if (!s.isEmpty())
				list.add(Integer.valueOf(s.trim()));
		}
		return list;
	}
}
