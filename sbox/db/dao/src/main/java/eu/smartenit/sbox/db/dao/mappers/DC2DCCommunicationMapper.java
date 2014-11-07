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

import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.DC2DCCommunicationID;
import eu.smartenit.sbox.db.dto.Direction;

/**
 * The DC2DCCommunicationMapper class.
 * 
 * @authors George Petropoulos
 * @version 1.0
 * 
 */
public class DC2DCCommunicationMapper implements
		ResultSetMapper<DC2DCCommunication> {
	
	/**
	 * The method that translates a received resultset into a DC2DCCommunication object.
	 * 
	 * @param index The index.
	 * @param r The received resultset.
	 * @param ctx The statement context.
	 * 
	 * @return The DC2DCCommunication object.
	 * 
	 * @throws SQLException
	 * 
	 */
	public DC2DCCommunication map(int index, ResultSet r, StatementContext ctx)
			throws SQLException {
		DC2DCCommunication d = new DC2DCCommunication();
		DC2DCCommunicationID id = new DC2DCCommunicationID();
		id.setCommunicationNumber(r.getInt("COMMUNICATIONNUMBER"));
		id.setCommunicationSymbol(r.getString("COMMUNICATIONSYMBOL"));
		id.setLocalAsNumber(r.getInt("LOCALASNUMBER"));
		id.setLocalCloudDCName(r.getString("LOCALCLOUDNAME"));
		id.setRemoteAsNumber(r.getInt("REMOTEASNUMBER"));
		id.setRemoteCloudDCName(r.getString("REMOTECLOUDNAME"));
		d.setId(id);
		d.setTrafficDirection(Direction.valueOf(r.getString("TRAFFICDIRECTION")));
		return d;

	}
}
