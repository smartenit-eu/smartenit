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
package eu.smartenit.sbox.db.dao.gen;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import eu.smartenit.sbox.db.dao.binders.BindDC2DCCommunication;
import eu.smartenit.sbox.db.dao.mappers.DC2DCCommunicationMapper;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.DC2DCCommunicationID;

/**
 * The abstract DC2DCCommunicationDAO class, including all SQL queries.
 *
 * @authors Antonis Makris, George Petropoulos
 * @version 1.0
 * 
 */
public abstract class AbstractDC2DCCommunicationDAO {

	@SqlUpdate("CREATE TABLE IF NOT EXISTS DC2DCCOMMUNICATION "
			+ "(COMMUNICATIONNUMBER INTEGER, " 
			+ "COMMUNICATIONSYMBOL STRING, "
			+ "TRAFFICDIRECTION STRING, " 
			+ "REMOTECLOUDNAME STRING, "
			+ "REMOTEASNUMBER INTEGER, " 
			+ "LOCALCLOUDNAME STRING, "
			+ "LOCALASNUMBER INTEGER, "
			+ "PRIMARY KEY (COMMUNICATIONNUMBER, COMMUNICATIONSYMBOL, LOCALCLOUDNAME, LOCALASNUMBER, "
			+ "REMOTECLOUDNAME, REMOTEASNUMBER), "
			+ "FOREIGN KEY (LOCALCLOUDNAME) REFERENCES CLOUDDC(CLOUDDCNAME) ON DELETE CASCADE,"
			+ "FOREIGN KEY (REMOTECLOUDNAME) REFERENCES CLOUDDC(CLOUDDCNAME) ON DELETE CASCADE)")
	public abstract void createTable();
	
	
	@SqlUpdate("DROP TABLE IF EXISTS DC2DCCOMMUNICATION")
	public abstract void deleteTable();

	@SqlUpdate("INSERT INTO DC2DCCOMMUNICATION "
			+ "(COMMUNICATIONNUMBER, COMMUNICATIONSYMBOL, "
			+ "TRAFFICDIRECTION, LOCALCLOUDNAME, LOCALASNUMBER, " 
			+ "REMOTECLOUDNAME, REMOTEASNUMBER) "
			+ "VALUES (:d.communicationNumber, :d.communicationSymbol, "
			+ ":d.trafficDirection, :d.localCloudDCName, :d.localAsNumber, "
			+ ":d.remoteCloudDCName, :d.remoteAsNumber)")
	public abstract void insert(@BindDC2DCCommunication("d") DC2DCCommunication dc);
	
	@SqlQuery("SELECT COMMUNICATIONNUMBER, COMMUNICATIONSYMBOL, "
			+ "TRAFFICDIRECTION, REMOTECLOUDNAME, REMOTEASNUMBER, " 
			+ "LOCALCLOUDNAME, LOCALASNUMBER "
			+ "FROM DC2DCCOMMUNICATION "
			+ "WHERE COMMUNICATIONNUMBER = :d.communicationNumber AND "
			+ "COMMUNICATIONSYMBOL = :d.communicationSymbol AND "
			+ "LOCALCLOUDNAME = :d.localCloudDCName AND "
			+ "LOCALASNUMBER = :d.localAsNumber AND "
			+ "REMOTECLOUDNAME = :d.remoteCloudDCName AND "
			+ "REMOTEASNUMBER = :d.remoteAsNumber")
	@Mapper(DC2DCCommunicationMapper.class)
	public abstract DC2DCCommunication findById(@BindBean("d") DC2DCCommunicationID dcID);
	
	@SqlQuery("SELECT COMMUNICATIONNUMBER, COMMUNICATIONSYMBOL, "
			+ "TRAFFICDIRECTION, REMOTECLOUDNAME, REMOTEASNUMBER, " 
			+ "LOCALCLOUDNAME, LOCALASNUMBER "
			+ "FROM DC2DCCOMMUNICATION")
	@Mapper(DC2DCCommunicationMapper.class)
	public abstract List<DC2DCCommunication> findAll();
	
	@SqlUpdate("DELETE FROM DC2DCCOMMUNICATION "
			+ "WHERE COMMUNICATIONNUMBER = :d.communicationNumber AND "
			+ "COMMUNICATIONSYMBOL = :d.communicationSymbol AND "
			+ "LOCALCLOUDNAME = :d.localCloudDCName AND "
			+ "LOCALASNUMBER = :d.localAsNumber AND "
			+ "REMOTECLOUDNAME = :d.remoteCloudDCName AND "
			+ "REMOTEASNUMBER = :d.remoteAsNumber")
	public abstract void deleteById(@BindBean("d") DC2DCCommunicationID dcID);
	
	@SqlUpdate("DELETE FROM DC2DCCOMMUNICATION")
	public abstract void deleteAll();

}
