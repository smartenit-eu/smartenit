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
package eu.smartenit.sbox.db.dao.gen;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import eu.smartenit.sbox.db.dao.binders.BindTunnel;
import eu.smartenit.sbox.db.dao.mappers.TunnelMapper;
import eu.smartenit.sbox.db.dto.DC2DCCommunicationID;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.SimpleTunnelID;
import eu.smartenit.sbox.db.dto.Tunnel;

/**
 * The abstract TunnelDAO class, including all SQL queries.
 *
 * @authors Antonis Makris, George Petropoulos
 * @version 1.0
 * 
 */
public abstract class AbstractTunnelDAO {

	@SqlUpdate("CREATE TABLE IF NOT EXISTS TUNNEL "
			+ "(TUNNELNAME STRING, "
			+ "TUNNELNUMBER INTEGER, "
			+ "SOURCEPREFIX STRING, "
			+ "DESTINATIONPREFIX STRING, "
			+ "PHYSICALLOCALINTERFACENAME STRING, "
			+ "INBOUNDINTERFACECOUNTEROID STRING, "
			+ "OUTBOUNDINTERFACECOUNTEROID STRING, "
			+ "LOCALLINKID STRING, "
			+ "LOCALISPNAME STRING, "
			+ "COMMUNICATIONNUMBER INTEGER, " 
			+ "COMMUNICATIONSYMBOL STRING, "
			+ "TRAFFICDIRECTION STRING, " 
			+ "REMOTECLOUDNAME STRING, "
			+ "REMOTEASNUMBER INTEGER, " 
			+ "LOCALCLOUDNAME STRING, "
			+ "LOCALASNUMBER INTEGER, "
			+ "PRIMARY KEY (TUNNELNAME, TUNNELNUMBER), "
			+ "FOREIGN KEY (LOCALLINKID, LOCALISPNAME) "
			+ "REFERENCES LINK (LOCALLINKID, LOCALISPNAME) "
			+ "ON DELETE CASCADE) ")
	public abstract void createTable();

	@SqlUpdate("DROP TABLE IF EXISTS TUNNEL")
	public abstract void deleteTable();
	
	@SqlUpdate("INSERT INTO TUNNEL "
			+ "(TUNNELNAME, TUNNELNUMBER, SOURCEPREFIX, DESTINATIONPREFIX, "
			+ "PHYSICALLOCALINTERFACENAME, INBOUNDINTERFACECOUNTEROID, "
			+ "OUTBOUNDINTERFACECOUNTEROID, LOCALLINKID, LOCALISPNAME) "
			+ "VALUES (:t.tunnelName, :t.tunnelNumber, :t.sourcePrefix, "
			+ ":t.destinationPrefix, :t.physicalLocalInterfaceName, "
			+ ":t.inboundInterfaceCounterOID, :t.outboundInterfaceCounterOID, :t.localLinkID,"
			+ ":t.localIspName)")
	public abstract void insert(@BindTunnel("t") Tunnel tunnel);
	
	@SqlUpdate("UPDATE TUNNEL "
			+ "SET SOURCEPREFIX = :t.sourcePrefix, "
			+ "DESTINATIONPREFIX = :t.destinationPrefix, "
			+ "PHYSICALLOCALINTERFACENAME = :t.physicalLocalInterfaceName, "
			+ "INBOUNDINTERFACECOUNTEROID = :t.inboundInterfaceCounterOID, "
			+ "LOCALLINKID = :t.localLinkID, "
			+ "LOCALISPNAME = :t.localIspName "
			+ "WHERE TUNNELNAME = :t.tunnelName AND TUNNELNUMBER = :t.tunnelNumber")
	public abstract void update(@BindTunnel("t") Tunnel tunnel);
	
	
	@SqlQuery("SELECT TUNNELNAME, TUNNELNUMBER, SOURCEPREFIX, DESTINATIONPREFIX, "
			+ "PHYSICALLOCALINTERFACENAME, INBOUNDINTERFACECOUNTEROID, "
			+ "OUTBOUNDINTERFACECOUNTEROID, LOCALLINKID, LOCALISPNAME "
			+ "FROM TUNNEL WHERE TUNNELNAME = :tunnelName AND TUNNELNUMBER = :tunnelNumber")
	@Mapper(TunnelMapper.class)
	public abstract Tunnel findById(@BindBean SimpleTunnelID tunnelID);
	
	
	@SqlQuery("SELECT TUNNELNAME, TUNNELNUMBER, SOURCEPREFIX, DESTINATIONPREFIX, "
			+ "PHYSICALLOCALINTERFACENAME, INBOUNDINTERFACECOUNTEROID, "
			+ "OUTBOUNDINTERFACECOUNTEROID, LOCALLINKID, LOCALISPNAME "
			+ "FROM TUNNEL")
	@Mapper(TunnelMapper.class)
	public abstract List<Tunnel> findAll();
	
	@SqlQuery("SELECT TUNNELNAME, TUNNELNUMBER, SOURCEPREFIX, DESTINATIONPREFIX, "
			+ "PHYSICALLOCALINTERFACENAME, INBOUNDINTERFACECOUNTEROID, "
			+ "OUTBOUNDINTERFACECOUNTEROID, LOCALLINKID, LOCALISPNAME "
			+ "FROM TUNNEL WHERE LOCALLINKID = :localLinkID AND LOCALISPNAME = :localIspName")
	@Mapper(TunnelMapper.class)
	public abstract List<Tunnel> findAllByLinkID(@BindBean SimpleLinkID linkID);
	
	@SqlUpdate("DELETE FROM TUNNEL")
	public abstract void deleteAll();
	
	@SqlUpdate("DELETE FROM TUNNEL "
			+ "WHERE TUNNELNAME = :tunnelName AND TUNNELNUMBER = :tunnelNumber")
	public abstract void deleteById(@BindBean SimpleTunnelID linkID);
	
	
	@SqlQuery("SELECT TUNNELNAME, TUNNELNUMBER, SOURCEPREFIX, DESTINATIONPREFIX, "
			+ "PHYSICALLOCALINTERFACENAME, INBOUNDINTERFACECOUNTEROID, "
			+ "OUTBOUNDINTERFACECOUNTEROID, LOCALLINKID, LOCALISPNAME "
			+ "FROM TUNNEL WHERE COMMUNICATIONNUMBER = :d.communicationNumber AND "
			+ "COMMUNICATIONSYMBOL = :d.communicationSymbol AND "
			+ "LOCALCLOUDNAME = :d.localCloudDCName AND "
			+ "LOCALASNUMBER = :d.localAsNumber AND "
			+ "REMOTECLOUDNAME = :d.remoteCloudDCName AND "
			+ "REMOTEASNUMBER = :d.remoteAsNumber")
	@Mapper(TunnelMapper.class)
	public abstract List<Tunnel> findAllByDC2DCCommunicationID(@BindBean("d") DC2DCCommunicationID dcID);
	
	@SqlUpdate("DELETE FROM TUNNEL WHERE COMMUNICATIONNUMBER = :d.communicationNumber AND "
			+ "COMMUNICATIONSYMBOL = :d.communicationSymbol AND "
			+ "LOCALCLOUDNAME = :d.localCloudDCName AND "
			+ "LOCALASNUMBER = :d.localAsNumber AND "
			+ "REMOTECLOUDNAME = :d.remoteCloudDCName AND "
			+ "REMOTEASNUMBER = :d.remoteAsNumber")
	public abstract void deleteByDC2DCCommunicationID(@BindBean("d") DC2DCCommunicationID dcID);
	
	@SqlUpdate("UPDATE TUNNEL SET COMMUNICATIONNUMBER = :d.communicationNumber, "
			+ "COMMUNICATIONSYMBOL = :d.communicationSymbol, "
			+ "LOCALCLOUDNAME = :d.localCloudDCName, "
			+ "LOCALASNUMBER = :d.localAsNumber, "
			+ "REMOTECLOUDNAME = :d.remoteCloudDCName, "
			+ "REMOTEASNUMBER = :d.remoteAsNumber "
			+ "WHERE TUNNELNAME = :t.tunnelName AND TUNNELNUMBER = :t.tunnelNumber")
	public abstract void updateByDC2DCCommunicationID(@BindBean("t") SimpleTunnelID tunnelID, 
			@BindBean("d") DC2DCCommunicationID dcID);
}
