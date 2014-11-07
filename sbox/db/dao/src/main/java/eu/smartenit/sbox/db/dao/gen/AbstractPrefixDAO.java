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
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import eu.smartenit.sbox.db.dao.mappers.PrefixMapper;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;

/**
 * The abstract PrefixDAO class, including all SQL queries.
 *
 * @authors Antonis Makris, George Petropoulos
 * @version 1.0
 * 
 */
public abstract  class AbstractPrefixDAO {
    
	@SqlUpdate("CREATE TABLE IF NOT EXISTS PREFIXES " +
	 	"(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
		"CLOUDDCNAME STRING, " +
	 	"PREFIXLENGTH INTEGER, " +
    	"PREFIX STRING, " +
 		"FOREIGN KEY (CLOUDDCNAME) REFERENCES CLOUDDC(CLOUDDCNAME) ON DELETE CASCADE)")
    
    public abstract void createTable(); 
 
    @SqlUpdate("DROP TABLE IF EXISTS PREFIXES")
	public abstract void deleteTable();

    @SqlUpdate("INSERT INTO PREFIXES "
			+ "(PREFIX, PREFIXLENGTH) "
			+ "VALUES (:prefix, :prefixLength)")
	@GetGeneratedKeys
	public abstract int insert(@BindBean NetworkAddressIPv4 p);
    
	
	@SqlBatch("INSERT INTO PREFIXES "
			+ "(PREFIX, PREFIXLENGTH) "
			+ "VALUES (:prefix, :prefixLength)")
	@BatchChunkSize(1000)
	@GetGeneratedKeys
	public abstract void insertBatch(@BindBean List<NetworkAddressIPv4> plist);
	
	
	@SqlUpdate("INSERT INTO PREFIXES "
			+ "(CLOUDDCNAME, PREFIX, PREFIXLENGTH) "
			+ "VALUES (:cloudName, :p.prefix, :p.prefixLength)")
	@GetGeneratedKeys
	public abstract int insertByCloudName(@BindBean("p") NetworkAddressIPv4 p, 
			@Bind("cloudName") String cloudName);
	
	
	@SqlQuery("SELECT CLOUDDCNAME, PREFIX, PREFIXLENGTH  "
			+ "FROM PREFIXES WHERE CLOUDDCNAME = :cloudName")
	@Mapper(PrefixMapper.class)
	public abstract List<NetworkAddressIPv4> findAllByCloudName(@Bind("cloudName") String cloudName);
	
	
	@SqlQuery("SELECT PREFIX, PREFIXLENGTH  "
			+ "FROM PREFIXES")
	@Mapper(PrefixMapper.class)
	public abstract List<NetworkAddressIPv4> findAll();
	
	@SqlUpdate("DELETE FROM PREFIXES WHERE CLOUDDCNAME = :cloudName")
	public abstract void deleteByCloudName(@Bind("cloudName") String cloudName);

	@SqlUpdate("DELETE FROM PREFIXES")
	public abstract void deleteAll();
	
}
