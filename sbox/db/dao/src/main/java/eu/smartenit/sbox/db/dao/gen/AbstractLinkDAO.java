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

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import eu.smartenit.sbox.db.dao.binders.BindLink;
import eu.smartenit.sbox.db.dao.mappers.LinkMapper;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.SimpleLinkID;

/**
 * The abstract LinkDAO class, including all SQL queries.
 *
 * @authors Antonis Makris, George Petropoulos
 * @version 3.1
 * 
 */
public abstract class AbstractLinkDAO {

	
	@SqlUpdate("CREATE TABLE IF NOT EXISTS LINK " 
			+ "(LOCALLINKID STRING NOT NULL, "
			+ "LOCALISPNAME STRING NOT NULL, " 
			+ "ADDRESSPREFIX STRING, "
			+ "PHYSICALINTERFACENAME STRING, "
			+ "VLAN INTEGER, " 
			+ "INBOUNDINTERFACECOUNTEROID STRING, "
			+ "OUTBOUNDINTERFACECOUNTEROID STRING, " 
			+ "BGROUTERADDRESS STRING, "
			+ "TUNNELENDPREFIX STRING, "
			+ "TUNNELENDPREFIXLENGTH INTEGER, "
			+ "POLICERBANDWIDTHLIMITFACTOR REAL, "
			+ "PRIMARY KEY (LOCALLINKID, LOCALISPNAME), "
			+ "FOREIGN KEY (BGROUTERADDRESS) "
			+ "REFERENCES BGROUTER(managementAddressPrefix) ON DELETE CASCADE)")
	public abstract void createTable();
	

	@SqlUpdate("DROP TABLE IF EXISTS LINK")
	public abstract void deleteTable();
	
	@SqlUpdate("INSERT INTO LINK "
			+ "(LOCALLINKID, LOCALISPNAME, ADDRESSPREFIX, "
			+ "PHYSICALINTERFACENAME, VLAN, INBOUNDINTERFACECOUNTEROID, "
			+ "OUTBOUNDINTERFACECOUNTEROID, BGROUTERADDRESS, "
			+ "TUNNELENDPREFIX, TUNNELENDPREFIXLENGTH, POLICERBANDWIDTHLIMITFACTOR) "
			+ "VALUES (:localLinkID, :localIspName, :addressPrefix, "
			+ ":physicalInterfaceName, :vlan, :inboundInterfaceCounterOID, "
			+ ":outboundInterfaceCounterOID, :bgRouterAddress, "
			+ ":tunnelEndPrefix, :tunnelEndPrefixLength, :policerBandwidthLimitFactor)")
	public abstract void insert(@BindLink Link l);
	
	
	@SqlQuery("SELECT LOCALLINKID, LOCALISPNAME, ADDRESSPREFIX, "
			+ "PHYSICALINTERFACENAME, VLAN, INBOUNDINTERFACECOUNTEROID, "
			+ "OUTBOUNDINTERFACECOUNTEROID, BGROUTERADDRESS, "
			+ "TUNNELENDPREFIX, TUNNELENDPREFIXLENGTH, POLICERBANDWIDTHLIMITFACTOR "
			+ "FROM LINK")
	@Mapper(LinkMapper.class)
	public abstract List<Link> findAll();
	
	@SqlQuery("SELECT LOCALLINKID, LOCALISPNAME, ADDRESSPREFIX, "
			+ "PHYSICALINTERFACENAME, VLAN, INBOUNDINTERFACECOUNTEROID, "
			+ "OUTBOUNDINTERFACECOUNTEROID, BGROUTERADDRESS, "
			+ "TUNNELENDPREFIX, TUNNELENDPREFIXLENGTH, POLICERBANDWIDTHLIMITFACTOR "
			+ "FROM LINK "
			+ "WHERE LOCALLINKID = :localLinkID AND LOCALISPNAME = :localIspName")
	@Mapper(LinkMapper.class)
	public abstract Link findById(@BindBean SimpleLinkID linkID);

	
	@SqlUpdate("UPDATE LINK "
			+ "SET ADDRESSPREFIX = :addressPrefix, "
			+ "PHYSICALINTERFACENAME = :physicalInterfaceName, "
			+ "VLAN = :vlan, "
			+ "INBOUNDINTERFACECOUNTEROID = :inboundInterfaceCounterOID, "
			+ "OUTBOUNDINTERFACECOUNTEROID = :outboundInterfaceCounterOID, "
			+ "BGROUTERADDRESS = :bgRouterAddress,"
			+ "TUNNELENDPREFIX = :tunnelEndPrefix, "
			+ "TUNNELENDPREFIXLENGTH = :tunnelEndPrefixLength,"
			+ "POLICERBANDWIDTHLIMITFACTOR = :policerBandwidthLimitFactor "
			+ "WHERE LOCALLINKID = :localLinkID AND LOCALISPNAME = :localIspName")
	public abstract void update(@BindLink Link l);
	
	@SqlUpdate("DELETE FROM LINK")
	public abstract void deleteAll();
	
	@SqlUpdate("DELETE FROM LINK "
			+ "WHERE LOCALLINKID = :localLinkID AND LOCALISPNAME = :localIspName")
	public abstract void deleteById(@BindBean SimpleLinkID linkID);
	
	@SqlQuery("SELECT LOCALLINKID, LOCALISPNAME, ADDRESSPREFIX, "
			+ "PHYSICALINTERFACENAME, VLAN, INBOUNDINTERFACECOUNTEROID, "
			+ "OUTBOUNDINTERFACECOUNTEROID, BGROUTERADDRESS, "
			+ "TUNNELENDPREFIX, TUNNELENDPREFIXLENGTH, POLICERBANDWIDTHLIMITFACTOR "
			+ "FROM LINK "
			+ "WHERE BGROUTERADDRESS = :bgAddress")
	@Mapper(LinkMapper.class)
	public abstract List<Link> findByBGRouterAddress(@Bind("bgAddress") String bgAddress);
}
