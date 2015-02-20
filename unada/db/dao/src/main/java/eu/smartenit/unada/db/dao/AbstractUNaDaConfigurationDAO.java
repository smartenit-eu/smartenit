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
package eu.smartenit.unada.db.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import eu.smartenit.unada.db.dao.binders.BindUNaDaConfiguration;
import eu.smartenit.unada.db.dao.mappers.UNaDaConfigurationMapper;
import eu.smartenit.unada.db.dto.UNaDaConfiguration;

/**
 * The AbstractUNaDaConfigurationDAO class.
 * 
 * @authors George Petropoulos
 * @version 2.1
 * 
 */
public abstract class AbstractUNaDaConfigurationDAO {

	@SqlUpdate("CREATE TABLE IF NOT EXISTS unadaconfiguration  "
			+ "(id BIGINT AUTO_INCREMENT PRIMARY KEY, " + "ipaddress VARCHAR(255), "
			+ "macaddress VARCHAR(255), " + "port BIGINT, " + "latitude REAL, "
			+ "longtitude REAL, " + "openssid VARCHAR(255), " + "privatessid VARCHAR(255), "
			+ "privatessidpassword VARCHAR(255), " + "socialinterval BIGINT, "
			+ "overlayinterval BIGINT, "
			+ "predictioninterval BIGINT, "
			+ "bootstrap BOOLEAN, "
            + "social BOOLEAN, "
            + "overlay BOOLEAN, "
            + "chunksize BIGINT)")
	public abstract void createTable();

	@SqlUpdate("DROP TABLE IF EXISTS unadaconfiguration")
	public abstract void deleteTable();

	@SqlUpdate("INSERT INTO unadaconfiguration (ipaddress, macaddress, port, latitude, "
			+ "longtitude, openssid, privatessid, privatessidpassword, socialinterval, "
			+ "overlayinterval, predictioninterval, bootstrap, social, overlay, chunksize) "
			+ "VALUES (:u.ipaddress, :u.macaddress, :u.port, :u.latitude, "
			+ ":u.longtitude, :u.openssid, :u.privatessid, :u.privatessidpassword, "
			+ ":u.socialinterval, :u.overlayinterval, :u.predictioninterval, :u.bootstrap, "
            + ":u.social, :u.overlay, :u.chunksize)")
	public abstract void insert(@BindUNaDaConfiguration("u") UNaDaConfiguration u);

	@SqlQuery("SELECT * FROM unadaconfiguration")
	@Mapper(UNaDaConfigurationMapper.class)
	public abstract List<UNaDaConfiguration> findAll();

	@SqlUpdate("DELETE FROM unadaconfiguration")
	public abstract void deleteAll();

	@SqlQuery("SELECT * FROM unadaconfiguration LIMIT 1")
	@Mapper(UNaDaConfigurationMapper.class)
	public abstract UNaDaConfiguration findLast();

}
