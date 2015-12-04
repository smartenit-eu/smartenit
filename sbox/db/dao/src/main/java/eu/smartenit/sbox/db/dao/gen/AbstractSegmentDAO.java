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
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import eu.smartenit.sbox.db.dao.mappers.SegmentMapper;
import eu.smartenit.sbox.db.dto.Segment;
import eu.smartenit.sbox.db.dto.SimpleLinkID;

/**
 * The abstract SegmentDAO class, including all SQL queries.
 *
 * @authors Antonis Makris, George Petropoulos
 * @version 1.0
 * 
 */
public abstract class AbstractSegmentDAO {

	@SqlUpdate("CREATE TABLE IF NOT EXISTS SEGMENT "
			+ "(ID INTEGER AUTO_INCREMENT," 
			+ "LEFTBORDER INTEGER,"
			+ "RIGHTBORDER INTEGER," 
			+ "A REAL," 
			+ "B REAL,"
			+ "LOCALLINKID STRING, "
			+ "LOCALISPNAME STRING, "
			+ "PRIMARY KEY (ID), "
			+ "FOREIGN KEY (LOCALLINKID, LOCALISPNAME) "
			+ "REFERENCES COSTFUNCTION(LOCALLINKID, LOCALISPNAME) ON DELETE CASCADE)")
	public abstract void createTable();

	
	@SqlUpdate("DROP TABLE IF EXISTS SEGMENT")
	public abstract void deleteTable();
	
	
	@SqlUpdate("INSERT INTO SEGMENT "
			+ "(LOCALLINKID, LOCALISPNAME, LEFTBORDER, RIGHTBORDER, A, B) "
			+ "VALUES (:l.localLinkID, :l.localIspName, "
			+ ":s.leftBorder, :s.rightBorder, :s.a, :s.b)")
	public abstract void insertByLinkId(@BindBean("s") Segment s, 
			@BindBean("l") SimpleLinkID linkID);
	
	
	@SqlBatch("INSERT INTO SEGMENT "
			+ "(LOCALLINKID, LOCALISPNAME, LEFTBORDER, RIGHTBORDER, A, B) "
			+ "VALUES (:l.localLinkID, :l.localIspName, "
			+ ":costFunctionID , :s.leftBorder, :s.rightBorder, :s.a, :s.b)")
	@BatchChunkSize(1000)
	public abstract void insertBatchByLinkId(@BindBean("s") List<Segment> slist, 
			@BindBean("l") SimpleLinkID linkID);
	
	
	@SqlQuery("SELECT LOCALLINKID, LOCALISPNAME, LEFTBORDER, RIGHTBORDER, A, B  "
			+ "FROM SEGMENT WHERE LOCALLINKID = :localLinkID AND "
			+ "LOCALISPNAME = :localIspName")
	@Mapper(SegmentMapper.class)
	public abstract List<Segment> findAllByLinkId(@BindBean("l") SimpleLinkID linkID);
	
	
	@SqlQuery("SELECT LEFTBORDER, RIGHTBORDER, A, B  "
			+ "FROM SEGMENT")
	@Mapper(SegmentMapper.class)
	public abstract List<Segment> findAll();
	
	@SqlUpdate("DELETE FROM SEGMENT "
			+ "WHERE LOCALLINKID = :localLinkID AND LOCALISPNAME = :localIspName")
	public abstract void deleteByLinkId(@BindBean SimpleLinkID linkID);

	@SqlUpdate("DELETE FROM SEGMENT ")
	public abstract void deleteAll();
}
