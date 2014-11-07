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
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

import eu.smartenit.sbox.db.dao.CostFunctionDAO.CostFunctionSegmentJoinRow;
import eu.smartenit.sbox.db.dto.CostFunction;
import eu.smartenit.sbox.db.dto.SimpleLinkID;

/**
 * The abstract CostFunctionDAO class, including all SQL queries.
 *
 * @authors Antonis Makris, George Petropoulos
 * @version 1.0
 * 
 */
public abstract class AbstractCostFunctionDAO {

	@SqlUpdate("CREATE TABLE IF NOT EXISTS COSTFUNCTION "
			+ "(TYPE STRING, "
			+ "SUBTYPE STRING, " 
			+ "INPUTUNIT STRING, " 
			+ "OUTPUTUNIT STRING, "
			+ "LOCALLINKID STRING  NOT NULL, "
			+ "LOCALISPNAME STRING  NOT NULL, "
			+ "PRIMARY KEY (LOCALLINKID, LOCALISPNAME), "
			+ "FOREIGN KEY (LOCALLINKID, LOCALISPNAME) "
			+ "REFERENCES LINK(LOCALLINKID, LOCALISPNAME) ON DELETE CASCADE)")
	public abstract void createTable();

	
	@SqlUpdate("DROP TABLE IF EXISTS COSTFUNCTION")
	public abstract void deleteTable();
	
	
	@SqlUpdate("INSERT INTO COSTFUNCTION "
			+ "(TYPE, SUBTYPE, INPUTUNIT, OUTPUTUNIT, LOCALLINKID, LOCALISPNAME) "
			+ "VALUES (:c.type, :c.subtype, :c.inputUnit, :c.outputUnit, "
			+ ":l.localLinkID, :l.localIspName)")
	public abstract void insertByLinkId(@BindBean("c") CostFunction c, 
			@BindBean("l") SimpleLinkID linkID);
	
	
	@SqlQuery("SELECT c.TYPE as type, c.SUBTYPE as subtype, c.INPUTUNIT as inputUnit, "
			+ "c.OUTPUTUNIT as outputUnit, LEFTBORDER as leftBorder, seg.RIGHTBORDER as rightBorder, "
			+ "seg.A as a, seg.B as b, c.LOCALLINKID as linkID, c.LOCALISPNAME as ispName "
			+ "FROM COSTFUNCTION c "
			+ "LEFT JOIN SEGMENT seg "
			+ "ON (c.LOCALLINKID = seg.LOCALLINKID AND c.LOCALISPNAME = seg.LOCALISPNAME) "
			+ "WHERE c.LOCALLINKID = :localLinkID AND c.LOCALISPNAME = :localIspName")
	@MapResultAsBean
	public abstract List<CostFunctionSegmentJoinRow> findAllCostSegmentsByLinkD(@BindBean SimpleLinkID linkID);

	@SqlQuery("SELECT c.TYPE as type, c.SUBTYPE as subtype, c.INPUTUNIT as inputUnit, "
			+ "c.OUTPUTUNIT as outputUnit, LEFTBORDER as leftBorder, seg.RIGHTBORDER as rightBorder, "
			+ "seg.A as a, seg.B as b, c.LOCALLINKID as linkID, c.LOCALISPNAME as ispName "
			+ "FROM COSTFUNCTION c "
			+ "LEFT JOIN SEGMENT seg "
			+ "ON (c.LOCALLINKID = seg.LOCALLINKID AND c.LOCALISPNAME = seg.LOCALISPNAME) ")
	@MapResultAsBean
	public abstract List<CostFunctionSegmentJoinRow> findAll();
	
	@SqlUpdate("DELETE FROM COSTFUNCTION "
			+ "WHERE LOCALLINKID = :localLinkID AND LOCALISPNAME = :localIspName")
	public abstract void deleteByLinkId(@BindBean SimpleLinkID linkID);

	@SqlUpdate("DELETE FROM COSTFUNCTION")
	public abstract void deleteAll();
}
