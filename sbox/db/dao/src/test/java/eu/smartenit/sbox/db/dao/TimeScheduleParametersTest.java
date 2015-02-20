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
		tables.deleteAll();
		tables.createAll();
		
		tdao = new TimeScheduleParametersDAO();
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
		t.setStartDate(new Date(1000000));
		t.setAccountingPeriod(300);
		t.setReportingPeriod(30);
		t.setTol1(0.2);
		t.setTol2(0.553);
		t.setCompensationPeriod(600);
		t.setReportPeriodDTM(900);
		t.setReportPeriodEA(1000);
		t.setSamplingPeriod(100);
		tdao.insert(t);
		
		//Checking non empty TimeScheduleParameters table
		tList = tdao.findAll();
		assertNotNull(tList);
		assertEquals(tList.size(), 1);
		assertEquals(tList.get(0).getAccountingPeriod(), 300);
		assertEquals(tList.get(0).getReportingPeriod(), 30);
		assertEquals(tList.get(0).getTol1(), 0.2, 0.001);
		assertEquals(tList.get(0).getTol2(), 0.553, 0.001);
		assertEquals(tList.get(0).getCompensationPeriod(), 600);
		assertEquals(tList.get(0).getReportPeriodDTM(), 900);
		assertEquals(tList.get(0).getReportPeriodEA(), 1000);
		assertEquals(tList.get(0).getSamplingPeriod(), 100);
		assertEquals(tList.get(0).getStartDate().getTime(), 1000000);
		
		t = new TimeScheduleParameters();
		t.setStartDate(new Date(2000000));
		t.setAccountingPeriod(600);
		t.setReportingPeriod(60);
		t.setTol1(2.999);
		t.setTol2(6.222);
		t.setCompensationPeriod(600);
		t.setReportPeriodDTM(2200);
		t.setReportPeriodEA(4000);
		t.setSamplingPeriod(600);
		tdao.insert(t);
		
		tList = tdao.findAll();
		assertNotNull(tList);
		assertEquals(tList.size(), 2);
		
		TimeScheduleParameters t2 = tdao.findLast();
		assertNotNull(t2);
		assertEquals(t2.getAccountingPeriod(), 600);
		assertEquals(t2.getReportingPeriod(), 60);
		assertEquals(t2.getTol1(), 2.999, 0.001);
		assertEquals(t2.getTol2(), 6.222, 0.001);
		assertEquals(t2.getCompensationPeriod(), 600);
		assertEquals(t2.getReportPeriodDTM(), 2200);
		assertEquals(t2.getReportPeriodEA(), 4000);
		assertEquals(t2.getSamplingPeriod(), 600);
		assertEquals(t2.getStartDate().getTime(), 2000000);
		
		tdao.deleteAll();
		tList = tdao.findAll();
		assertNotNull(tList);
		assertEquals(tList.size(), 0);
		
	}
}
