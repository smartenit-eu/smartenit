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
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import eu.smartenit.sbox.db.dao.binders.BindSDNController;
import eu.smartenit.sbox.db.dao.mappers.SDNControllerMapper;
import eu.smartenit.sbox.db.dto.SDNController;

/**
 * The abstract SDNControllerDAO class, including all SQL queries.
 *
 * @authors Antonis Makris, George Petropoulos
 * @version 1.0
 * 
 */
public abstract class AbstractSDNControllerDAO {

	@SqlUpdate("CREATE TABLE IF NOT EXISTS SDNCONTROLLER "
			+ "(RESTPORT INTEGER, " 
			+ "OPENFLOWPORT INTEGER, "
			+ "ADDRESS STRING PRIMARY KEY, " 
			+ "RESTHOSTADDRESS STRING, "
			+ "OPENFLOWHOSTADDRESS STRING)")
	public abstract void createTable();
	

	@SqlUpdate("DROP TABLE IF EXISTS SDNCONTROLLER")
	public abstract void deleteTable();

	
	@SqlUpdate("INSERT INTO SDNCONTROLLER "
			+ "(RESTPORT, " 
			+ "OPENFLOWPORT, "
			+ "ADDRESS, " 
			+ "RESTHOSTADDRESS, "
			+ "OPENFLOWHOSTADDRESS) " 
			+ "VALUES (:s.restPort, :s.openflowPort, :s.address, :s.restHost, :s.openflowHost)")
	public abstract void insert (@BindSDNController("s") SDNController s);
	
	@SqlUpdate("UPDATE SDNCONTROLLER "
			+ "SET RESTPORT = :s.restPort, " 
			+ "OPENFLOWPORT = :s.openflowPort, "
			+ "RESTHOSTADDRESS = :s.restHost, "
			+ "OPENFLOWHOSTADDRESS = :s.openflowHost " 
			+ "WHERE ADDRESS = :s.address")
	public abstract void update (@BindSDNController("s") SDNController s);
	
	@SqlQuery("SELECT "
			+ "RESTPORT, " 
			+ "OPENFLOWPORT, "
			+ "ADDRESS, " 
			+ "RESTHOSTADDRESS, "
			+ "OPENFLOWHOSTADDRESS " 
			+ "FROM SDNCONTROLLER")
	@Mapper(SDNControllerMapper.class)
	public abstract List<SDNController> findAll();
	
	@SqlQuery("SELECT "
			+ "RESTPORT, " 
			+ "OPENFLOWPORT, "
			+ "ADDRESS, " 
			+ "RESTHOSTADDRESS, "
			+ "OPENFLOWHOSTADDRESS " 
			+ "FROM SDNCONTROLLER WHERE ADDRESS = :address")
	@Mapper(SDNControllerMapper.class)
	public abstract SDNController findById(@Bind("address") String address);
	
	
	@SqlUpdate("DELETE FROM SDNCONTROLLER WHERE ADDRESS = :address")
	public abstract void deleteById(@Bind("address") String address);

	@SqlUpdate("DELETE FROM SDNCONTROLLER")
	public abstract void deleteAll();
}
