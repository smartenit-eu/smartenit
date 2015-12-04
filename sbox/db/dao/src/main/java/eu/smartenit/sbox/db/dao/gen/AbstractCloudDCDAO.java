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

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

import eu.smartenit.sbox.db.dao.CloudDCDAO.CloudPrefixJoinRow;
import eu.smartenit.sbox.db.dao.binders.BindCloudDC;
import eu.smartenit.sbox.db.dto.CloudDC;

/**
 * The abstract CloudDCDAO class, including all SQL queries.
 *
 * @authors Antonis Makris, George Petropoulos
 * @version 1.0
 * 
 */
public abstract class AbstractCloudDCDAO {

	@SqlUpdate("CREATE TABLE IF NOT EXISTS CLOUDDC "
			+ "(CLOUDDCNAME STRING PRIMARY KEY, "
			+ "ASNUMBER INTEGER, "
			+ "DAROUTERADDRESS STRING, "
			+ "SDNADDRESS STRING, "
			+ "FOREIGN KEY (SDNADDRESS) REFERENCES SDNCONTROLLER(ADDRESS) ON DELETE CASCADE, "
			+ "FOREIGN KEY (DAROUTERADDRESS) REFERENCES DAROUTER(ADDRESS) ON DELETE CASCADE, "
			+ "FOREIGN KEY (ASNUMBER) REFERENCES ASYSTEM(ASNUMBER) ON DELETE CASCADE)")
	public abstract void createTable();

	@SqlUpdate("DROP TABLE IF EXISTS CLOUDDC")
	public abstract void deleteTable();

	@SqlUpdate("INSERT INTO CLOUDDC "
			+ "(CLOUDDCNAME, ASNUMBER, DAROUTERADDRESS, SDNADDRESS)"
			+ " VALUES (:c.cloudName, :c.asNumber , :c.daRouterAddress, "
			+ ":c.sdnControllerAddress)")
	public abstract void insert(@BindCloudDC("c") CloudDC c);
	
	@SqlUpdate("UPDATE CLOUDDC "
			+ "SET ASNUMBER = :c.asNumber, "
			+ "DAROUTERADDRESS = :c.daRouterAddress, "
			+ "SDNADDRESS = :c.sdnControllerAddress "
			+ "WHERE CLOUDDCNAME = :c.cloudName")
	public abstract void update(@BindCloudDC("c") CloudDC c);
	
	@SqlQuery("SELECT c.CLOUDDCNAME AS cloudName, c.ASNUMBER AS asNumber, "
			+ "c.DAROUTERADDRESS AS daRouterAddress, c.SDNADDRESS AS sdnAddress, "
			+ "p.PREFIX AS prefix, p.PREFIXLENGTH AS prefixLength, a.SBOXADDRPREFIX AS sboxAddress "
			+ "FROM CLOUDDC c "
			+ "LEFT JOIN SDNCONTROLLER s ON c.SDNADDRESS = s.ADDRESS "
			+ "LEFT JOIN DAROUTER d ON c.DAROUTERADDRESS = d.ADDRESS "
			+ "LEFT JOIN PREFIXES p ON c.CLOUDDCNAME = p.CLOUDDCNAME "
			+ "JOIN ASYSTEM a ON a.ASNUMBER=c.ASNUMBER")
	@MapResultAsBean
	public abstract List<CloudPrefixJoinRow> findAll();
	
	
	@SqlQuery("SELECT c.CLOUDDCNAME AS cloudName, c.ASNUMBER AS asNumber, "
			+ "c.DAROUTERADDRESS AS daRouterAddress, c.SDNADDRESS AS sdnAddress, "
			+ "p.PREFIX AS prefix, p.PREFIXLENGTH AS prefixLength, a.SBOXADDRPREFIX AS sboxAddress "
			+ "FROM CLOUDDC c "
			+ "LEFT JOIN SDNCONTROLLER s ON c.SDNADDRESS = s.ADDRESS "
			+ "LEFT JOIN DAROUTER d ON c.DAROUTERADDRESS = d.ADDRESS "
			+ "LEFT JOIN PREFIXES p ON c.CLOUDDCNAME = p.CLOUDDCNAME "
			+ "JOIN ASYSTEM a ON a.ASNUMBER=c.ASNUMBER "
			+ "WHERE a.LOCAL = 1")
	@MapResultAsBean
	public abstract List<CloudPrefixJoinRow> findLocalClouds();
	
	@SqlQuery("SELECT c.CLOUDDCNAME AS cloudName, c.ASNUMBER AS asNumber, "
			+ "c.DAROUTERADDRESS AS daRouterAddress, c.SDNADDRESS AS sdnAddress, "
			+ "p.PREFIX AS prefix, p.PREFIXLENGTH AS prefixLength, a.SBOXADDRPREFIX AS sboxAddress "
			+ "FROM CLOUDDC c "
			+ "LEFT JOIN SDNCONTROLLER s ON c.SDNADDRESS = s.ADDRESS "
			+ "LEFT JOIN DAROUTER d ON c.DAROUTERADDRESS = d.ADDRESS "
			+ "LEFT JOIN PREFIXES p ON c.CLOUDDCNAME = p.CLOUDDCNAME "
			+ "JOIN ASYSTEM a ON a.ASNUMBER=c.ASNUMBER "
			+ "WHERE a.LOCAL = 0")
	@MapResultAsBean
	public abstract List<CloudPrefixJoinRow> findRemoteClouds();
	
	@SqlQuery("SELECT c.CLOUDDCNAME AS cloudName, c.ASNUMBER AS asNumber, "
			+ "c.DAROUTERADDRESS AS daRouterAddress, c.SDNADDRESS AS sdnAddress, "
			+ "p.PREFIX AS prefix, p.PREFIXLENGTH as prefixLength, a.SBOXADDRPREFIX AS sboxAddress "
			+ "FROM CLOUDDC c "
			+ "LEFT JOIN SDNCONTROLLER s ON c.SDNADDRESS = s.ADDRESS "
			+ "LEFT JOIN DAROUTER d ON c.DAROUTERADDRESS = d.ADDRESS "
			+ "LEFT JOIN PREFIXES p ON c.CLOUDDCNAME = p.CLOUDDCNAME "
			+ "JOIN ASYSTEM a ON a.ASNUMBER=c.ASNUMBER "
			+ "WHERE c.CLOUDDCNAME = :name ")
	@MapResultAsBean
	public abstract List<CloudPrefixJoinRow> findById(@Bind("name") String name);
	
	
	@SqlQuery("SELECT c.CLOUDDCNAME AS cloudName, c.ASNUMBER AS asNumber, "
			+ "c.DAROUTERADDRESS AS daRouterAddress, c.SDNADDRESS AS sdnAddress, "
			+ "p.PREFIX AS prefix, p.PREFIXLENGTH as prefixLength, a.SBOXADDRPREFIX AS sboxAddress "
			+ "FROM CLOUDDC c "
			+ "LEFT JOIN SDNCONTROLLER s ON c.SDNADDRESS = s.ADDRESS "
			+ "LEFT JOIN DAROUTER d ON c.DAROUTERADDRESS = d.ADDRESS "
			+ "LEFT JOIN PREFIXES p ON c.CLOUDDCNAME = p.CLOUDDCNAME "
			+ "JOIN ASYSTEM a ON a.ASNUMBER=c.ASNUMBER "
			+ "WHERE c.ASNUMBER=:asNumber")
	@MapResultAsBean
	public abstract List<CloudPrefixJoinRow> findByASNumber(@Bind("asNumber") int asNumber);
	
	@SqlUpdate("DELETE FROM CLOUDDC "
			+ "WHERE CLOUDDCNAME = :name")
	public abstract void deleteById(@Bind("name") String name);
	
	@SqlUpdate("DELETE FROM CLOUDDC ")
	public abstract void deleteAll();

}
