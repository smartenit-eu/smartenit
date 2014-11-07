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
package eu.smartenit.sbox.db.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dao.ISPDAO;
import eu.smartenit.sbox.db.dao.util.Tables;
import eu.smartenit.sbox.db.dto.ISP;

public class ISPTest {
	
	private static final Logger logger = LoggerFactory.getLogger(ISPTest.class);

	private ISPDAO tdao;
	
	@Before
	public void setupDb() throws ClassNotFoundException {
		logger.info("Creating all tables for tests.");
		Tables tables = new Tables();
		tables.createAll();
		
		logger.info("Preparing ISP table.");
		tdao = new ISPDAO();
		tdao.deleteAll();
	}
	
	@After
	public void dropDb() {
		logger.info("Deleting all entries from ISP table.");
		tdao.deleteAll();
	}
	
	@Test
	public void testFunctions() throws Exception {
		logger.info("Testing ISP functions.");
		List<ISP> tList = tdao.findAll();
		assertNotNull(tList);
		assertEquals(0, tList.size());
		
		ISP t = tdao.findLast();
		assertNotNull(t);
		
		//Inserting ISP.;
		t = new ISP();
		t.setIspName("HOL");
		tdao.insert(t);
		
		//Checking non empty ISP table
		tList = tdao.findAll();
		assertNotNull(tList);
		assertEquals(1, tList.size());
		
		ISP t1 = new ISP();
		t1.setIspName("WIND");
		tdao.insert(t1);
		
		tList = tdao.findAll();
		assertNotNull(tList);
		assertEquals(2, tList.size());
		
		t1 = tdao.findLast();
		assertEquals(t1.getIspName(), "WIND");
		
		t1 = tdao.findById("HOL");
		assertNotNull(t1);
		
		tdao.deleteAll();
		tList = tdao.findAll();
		assertNotNull(tList);
		assertEquals(tList.size(), 0);
		
		
	}
}
