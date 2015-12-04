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

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dao.SystemControlParametersDAO;
import eu.smartenit.sbox.db.dao.util.Tables;
import eu.smartenit.sbox.db.dto.ChargingRule;
import eu.smartenit.sbox.db.dto.OperationModeSDN;
import eu.smartenit.sbox.db.dto.SystemControlParameters;

public class SystemControlParametersTest {
	
	private static final Logger logger = 
			LoggerFactory.getLogger(SystemControlParametersTest.class);

	private SystemControlParametersDAO sdao;
	
	@Before
	public void setupDb() {
		logger.info("Creating all tables for tests.");
		Tables tables = new Tables();
		tables.deleteAll();
		tables.createAll();
		
		sdao = new SystemControlParametersDAO();
	}
	
	@After
	public void dropDb() {
		logger.info("Deleting all entries from SystemControlParameters table.");
		sdao.deleteAll();
	}
	
	@Test
	public void testFunctions() throws Exception {
		logger.info("Testing SystemControlParameters functions.");
		List<SystemControlParameters> sList = sdao.findAll();
		assertNotNull(sList);
		assertEquals(sList.size(), 0);
		
		//Inserting SystemControlParameters.");
		SystemControlParameters s = new SystemControlParameters();
		s.setChargingRule(ChargingRule.the95thPercentile);
		s.setCompensationThreshold(22.34);
		s.setOperationModeSDN(OperationModeSDN.reactiveWithReferenceVector);
		s.setDelayTolerantTrafficManagement(true);
		sdao.insert(s);
		
		//Checking non empty SystemControlParameters table
		sList = sdao.findAll();
		assertNotNull(sList);
		assertEquals(sList.size(), 1);
		assertEquals(sList.get(0).getChargingRule(), 
				ChargingRule.the95thPercentile);
		assertEquals(sList.get(0).getCompensationThreshold(), 22.34, 0.001);
		assertEquals(sList.get(0).getOperationModeSDN(), 
				OperationModeSDN.reactiveWithReferenceVector);
		assertTrue(sList.get(0).isDelayTolerantTrafficManagement());
		
		s = new SystemControlParameters();
		s.setChargingRule(ChargingRule.volume);
		s.setCompensationThreshold(55.88);
		s.setOperationModeSDN(OperationModeSDN.proactiveWithoutReferenceVector);
		sdao.insert(s);
		
		sList = sdao.findAll();
		assertNotNull(sList);
		assertEquals(sList.size(), 2);
		
		SystemControlParameters s2 = sdao.findLast();
		assertNotNull(s2);
		assertEquals(s2.getChargingRule(), 
				ChargingRule.volume);
		assertEquals(s2.getCompensationThreshold(), 55.88, 0.001);
		assertEquals(s2.getOperationModeSDN(), 
				OperationModeSDN.proactiveWithoutReferenceVector);
		assertFalse(s2.isDelayTolerantTrafficManagement());
		
		sdao.deleteAll();
		sList = sdao.findAll();
		assertNotNull(sList);
		assertEquals(sList.size(), 0);
		
	}
}
