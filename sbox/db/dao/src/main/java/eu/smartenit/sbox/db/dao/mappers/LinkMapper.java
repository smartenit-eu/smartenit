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

import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SimpleLinkID;

/**
 * The LinkMapper class.
 * 
 * @authors George Petropoulos
 * @version 1.2
 * 
 */
public class LinkMapper implements ResultSetMapper<Link> {
	
	/**
	 * The method that translates a received resultset into a Link object.
	 * 
	 * @param index The index.
	 * @param r The received resultset.
	 * @param ctx The statement context.
	 * 
	 * @return The Link object.
	 * 
	 * @throws SQLException
	 * 
	 */
	public Link map(int index, ResultSet r, StatementContext ctx)
			throws SQLException {
		Link l = new Link();
		l.setLinkID(new SimpleLinkID(r.getString("LOCALLINKID"), r
				.getString("LOCALISPNAME")));
		l.setPhysicalInterfaceName(r.getString("PHYSICALINTERFACENAME"));
		l.setAddress(new NetworkAddressIPv4(r.getString("ADDRESSPREFIX"), 32));
		l.setInboundInterfaceCounterOID(r
				.getString("INBOUNDINTERFACECOUNTEROID"));
		l.setOutboundInterfaceCounterOID(r
				.getString("OUTBOUNDINTERFACECOUNTEROID"));
		l.setVlan(r.getInt("VLAN"));
		BGRouter bgRouter = new BGRouter();
		bgRouter.setManagementAddress(new NetworkAddressIPv4(r
				.getString("BGROUTERADDRESS"), 32));
		l.setBgRouter(bgRouter);
		l.setTunnelEndPrefix(new NetworkAddressIPv4(r.getString("TUNNELENDPREFIX"), 
				r.getInt("TUNNELENDPREFIXLENGTH")));
		l.setPolicerBandwidthLimitFactor(r.getDouble("POLICERBANDWIDTHLIMITFACTOR"));
		return l;

	}
}
