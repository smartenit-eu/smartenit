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

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dao.TimeScheduleParametersDAO;
import eu.smartenit.sbox.db.dao.util.Tables;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;

public class TimeScheduleParametersTest {
	
	private static final Logger logger = LoggerFactory.getLogger(TimeScheduleParametersTest.class);

	private TimeScheduleParametersDAO tdao;
	
	@Before
	public void setupDb() {
		logger.info("Creating all tables for tests.");
		Tables tables = new Tables();
		tables.createAll();
		
		logger.info("Preparing TimeScheduleParameters table.");
		tdao = new TimeScheduleParametersDAO();
		tdao.deleteAll();
	}
	
	@After
	public void dropDb() {
		logger.info("Deleting all entries from TimeScheduleParameters table.");
		tdao.deleteAll();
	}
	
	@Test
	public void testFunctions() throws Exception {
		logger.info("Testing TimeScheduleParameters functions.");
		List<TimeScheduleParameters> tList = tdao.findAll();
		assertNotNull(tList);
		assertEquals(tList.size(), 0);
		
		//Inserting TimeScheduleParameters.");
		TimeScheduleParameters t = new TimeScheduleParameters();
		t.setStartDate(new Date(System.currentTimeMillis()));
		t.setAccountingPeriod(300);
		t.setReportingPeriod(30);
		t.setTol1(0.2);
		t.setTol2(0.553);
		tdao.insert(t);
		
		//Checking non empty TimeScheduleParameters table
		tList = tdao.findAll();
		assertNotNull(tList);
		assertEquals(tList.size(), 1);
		
		t = new TimeScheduleParameters();
		t.setStartDate(new Date(System.currentTimeMillis()));
		t.setAccountingPeriod(4000);
		t.setReportingPeriod(40);
		t.setTol1(2.2);
		t.setTol2(3.3);
		tdao.insert(t);
		
		tList = tdao.findAll();
		assertNotNull(tList);
		assertEquals(tList.size(), 2);
		
		TimeScheduleParameters t2 = tdao.findLast();
		assertNotNull(t2);
		assertEquals(t2.getAccountingPeriod(), 4000);
		assertEquals(t2.getTol1(), 2.2, 0.01);
		assertEquals(t2.getTol2(), 3.3, 0.01);
		
		tdao.deleteAll();
		tList = tdao.findAll();
		assertNotNull(tList);
		assertEquals(tList.size(), 0);
		
	}
}
