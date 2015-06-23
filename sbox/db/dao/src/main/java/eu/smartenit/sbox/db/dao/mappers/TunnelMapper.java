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

import eu.smartenit.sbox.db.dto.*;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * The TunnelMapper class.
 * 
 * @authors George Petropoulos
 * @version 1.2
 * 
 */
public class TunnelMapper implements ResultSetMapper<Tunnel> {
	
	/**
	 * The method that translates a received resultset into a Tunnel object.
	 * 
	 * @param index The index.
	 * @param r The received resultset.
	 * @param ctx The statement context.
	 * 
	 * @return The Tunnel object.
	 * 
	 * @throws SQLException
	 * 
	 */
	public Tunnel map(int index, ResultSet r, StatementContext ctx)
			throws SQLException {
		Tunnel t = new Tunnel();
        EndAddressPairTunnelID tunnelID = new EndAddressPairTunnelID();
        tunnelID.setTunnelName(r.getString("TUNNELNAME"));
        tunnelID.setLocalTunnelEndAddress(new NetworkAddressIPv4(r.getString("LOCALTUNNELENDADDRESS"), 32));
        tunnelID.setRemoteTunnelEndAddress(new NetworkAddressIPv4(r.getString("REMOTETUNNELENDADDRESS"), 32));
		t.setTunnelID(tunnelID);
		t.setPhysicalLocalInterfaceName(r
				.getString("PHYSICALLOCALINTERFACENAME"));
		t.setInboundInterfaceCounterOID(r
				.getString("INBOUNDINTERFACECOUNTEROID"));
		t.setOutboundInterfaceCounterOID(r
				.getString("OUTBOUNDINTERFACECOUNTEROID"));
        t.setOfSwitchPortNumber(r.getInt("OFSWITCHPORTNUMBER"));
		Link l = new Link();
		l.setLinkID(new SimpleLinkID(r.getString("LOCALLINKID"), r
				.getString("LOCALISPNAME")));
		t.setLink(l);
		t.setLocalRouterAddress(new NetworkAddressIPv4(r.getString("LOCALROUTERADDRESS"), 32));
		return t;

	}
}
