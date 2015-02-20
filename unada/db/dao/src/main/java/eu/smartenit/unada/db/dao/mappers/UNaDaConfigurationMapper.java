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
package eu.smartenit.unada.db.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import eu.smartenit.unada.db.dto.Location;
import eu.smartenit.unada.db.dto.UNaDaConfiguration;
import eu.smartenit.unada.db.dto.WifiConfiguration;

/**
 * The UNaDaConfigurationMapper class.
 * 
 * @authors George Petropoulos
 * @version 2.1
 * 
 */
public class UNaDaConfigurationMapper implements ResultSetMapper<UNaDaConfiguration> {

	/**
	 * The method that translates a received resultset into an UNaDaConfiguration
	 * object.
	 * 
	 * @param index The index.
	 * @param r The received resultset.
	 * @param ctx The statement context.
	 * 
	 * @return The UNaDaConfiguration object.
	 * 
	 * @throws SQLException
	 * 
	 */
	public UNaDaConfiguration map(int index, ResultSet r, StatementContext ctx)
			throws SQLException {

		UNaDaConfiguration u = new UNaDaConfiguration();
		u.setIpAddress(r.getString("ipaddress"));
		u.setMacAddress(r.getString("macaddress"));
		u.setPort(r.getInt("port"));
		u.setLocation(new Location(r.getDouble("latitude"), r.getDouble("longtitude")));
		u.setOpenWifi(new WifiConfiguration(r.getString("openssid"), ""));
		u.setPrivateWifi(new WifiConfiguration(r.getString("privatessid"), 
				r.getString("privatessidpassword")));
		u.setSocialInterval(r.getLong("socialinterval"));
		u.setOverlayInterval(r.getLong("overlayinterval"));
		u.setPredictionInterval(r.getLong("predictioninterval"));
		u.setBootstrapNode(r.getBoolean("bootstrap"));
        u.setSocialPredictionEnabled(r.getBoolean("social"));
        u.setOverlayPredictionEnabled(r.getBoolean("overlay"));
        u.setChunkSize(r.getLong("chunksize"));
		return u;

	}
}