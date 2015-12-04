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
package eu.smartenit.sbox.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import eu.smartenit.sbox.db.dao.gen.AbstractCostFunctionDAO;
import eu.smartenit.sbox.db.dto.CostFunction;
import eu.smartenit.sbox.db.dto.Segment;
import eu.smartenit.sbox.db.dto.SimpleLinkID;

import org.skife.jdbi.v2.DBI;
import org.sqlite.SQLiteConfig;

/**
 * The CostFunctionDAO class.
 *
 * @authors Antonis Makris, George Petropoulos
 * @version 1.0
 * 
 */
public class CostFunctionDAO {
	
	final AbstractCostFunctionDAO dao;
	
	private SegmentDAO sdao;

	/**
	 * The constructor.
	 */
	public CostFunctionDAO() {
		Properties connectionProperties = new Properties();
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		connectionProperties = config.toProperties(); 
		DBI dbi = new DBI(DbConstants.DBI_URL, connectionProperties);
		dao = dbi.onDemand(AbstractCostFunctionDAO.class);
	}

	/**
	 * The method that creates the CostFunction table.
	 */
	public void createTable() {
		dao.createTable();
	}

	/**
	 * The method that deletes the CostFunction table.
	 */
	public void deleteTable() {
		dao.deleteTable();
	}
	
	/**
	 * The method that inserts a CostFunction into the CostFunction table.
	 * 
	 * @param c The CostFunction to be inserted.
	 * @param l The SimpleLinkID.
	 */
	public void insertByLinkId(CostFunction c, SimpleLinkID l) throws Exception {
		dao.insertByLinkId(c, l);
		if (c.getSegments() != null && c.getSegments().size() != 0) {
			sdao = new SegmentDAO();
			for (Segment s : c.getSegments()) {
				sdao.insertByLinkId(s, l);
			}
		}
	}
	
	/**
	 * The method that deletes a stored CostFunction by its linkID.
	 * 
	 * @param linkID The SimpleLinkID.
	 */
	public void deleteByLinkId(SimpleLinkID linkID) {
		dao.deleteByLinkId(linkID);
	}

	/**
	 * The method that finds all the stored CostFunctions from the CostFunction table.
	 * 
	 * @return The list of stored CostFunctions.
	 */
	public List<CostFunction> findAll() {
		List<CostFunctionSegmentJoinRow> joinRows = dao.findAll();
		return mapJoinRowsToCostFunctions(joinRows);
	}
	
	/**
	 * The method that finds a stored CostFunction by its its linkID.
	 * 
	 * @param l The SimpleLinkID.
	 */
	public CostFunction findByLinkId(SimpleLinkID l) {
		List<CostFunctionSegmentJoinRow> joinRows = dao.findAllCostSegmentsByLinkD(l);
		List<CostFunction> clist = mapJoinRowsToCostFunctions(joinRows);
		if (clist != null && clist.size() != 0)
			return clist.get(0);
		else 
			return null;
	}
	
	public List<CostFunction> mapJoinRowsToCostFunctions(List<CostFunctionSegmentJoinRow> joinRows) {
		Map<SimpleLinkID, CostFunction> map = new HashMap<SimpleLinkID, CostFunction>();
		
		for (CostFunctionSegmentJoinRow row : joinRows) {
			if (map.containsKey(new SimpleLinkID(row.linkID, row.ispName))) {
				if (row.leftBorder + row.rightBorder + row.a + row.b != 0) {
					map.get(new SimpleLinkID(row.linkID, row.ispName)).getSegments()
						.add(new Segment(row.leftBorder, row.rightBorder, row.a, row.b));
				}
			}
			else {
				CostFunction c = new CostFunction();
				c.setType(row.type);
				c.setSubtype(row.subtype);
				c.setInputUnit(row.inputUnit);
				c.setOutputUnit(row.outputUnit);
				if (row.leftBorder + row.rightBorder + row.a + row.b != 0) {
					c.getSegments()
						.add(new Segment(row.leftBorder, row.rightBorder, row.a, row.b));
				}
				map.put(new SimpleLinkID(row.linkID, row.ispName), c);
			}
		}
		
		return new ArrayList<CostFunction>(map.values());
	}
	
	public static class CostFunctionSegmentJoinRow {
		public String linkID;
		public String ispName;
		public String type;
		public String subtype;
		public String inputUnit;	
		public String outputUnit;
		public long leftBorder;
		public long rightBorder;
		public float a;	
		public float b;
		
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getSubtype() {
			return subtype;
		}
		public void setSubtype(String subtype) {
			this.subtype = subtype;
		}
		public String getInputUnit() {
			return inputUnit;
		}
		public void setInputUnit(String inputUnit) {
			this.inputUnit = inputUnit;
		}
		public String getOutputUnit() {
			return outputUnit;
		}
		public void setOutputUnit(String outputUnit) {
			this.outputUnit = outputUnit;
		}
		public long getLeftBorder() {
			return leftBorder;
		}
		public void setLeftBorder(long leftBorder) {
			this.leftBorder = leftBorder;
		}
		public long getRightBorder() {
			return rightBorder;
		}
		public void setRightBorder(long rightBorder) {
			this.rightBorder = rightBorder;
		}
		public float getA() {
			return a;
		}
		public void setA(float a) {
			this.a = a;
		}
		public float getB() {
			return b;
		}
		public void setB(float b) {
			this.b = b;
		}
		public String getLinkID() {
			return linkID;
		}
		public void setLinkID(String linkID) {
			this.linkID = linkID;
		}
		public String getIspName() {
			return ispName;
		}
		public void setIspName(String ispName) {
			this.ispName = ispName;
		}
		
		
	}

	/**
	 * The method that deletes all the stored CostFunctions from the CostFunction table.
	 * 
	 */
	public void deleteAll() {
		dao.deleteAll();
	}
	
}
