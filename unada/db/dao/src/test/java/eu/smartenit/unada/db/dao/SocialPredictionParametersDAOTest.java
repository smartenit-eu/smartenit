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
package eu.smartenit.unada.db.dao;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.smartenit.unada.db.dao.impl.SocialPredictionParametersDAO;
import eu.smartenit.unada.db.dto.SocialPredictionParameters;

public class SocialPredictionParametersDAOTest {
	
private SocialPredictionParametersDAO sDao;
	
	@Before
	public void setUp() {
		sDao = new SocialPredictionParametersDAO();
		sDao.createTable();
	}
	
	@Test
	public void testFunctions() throws Exception {
		assertEquals(sDao.findAll().size(), 0);
		assertNull(sDao.findLast());
		
		SocialPredictionParameters s = new SocialPredictionParameters();
		s.setLambda1(0.1);
		s.setLambda2(0.2);
		s.setLambda3(0.3);
		s.setLambda4(0.4);
		s.setLambda5(0.5);
		s.setLambda6(0.6);
		sDao.insert(s);
		
		assertEquals(sDao.findAll().size(), 1);
		s = sDao.findLast();
		assertNotNull(s);
		assertEquals(s.getLambda1(), 0.1, 0.01);
		assertEquals(s.getLambda2(), 0.2, 0.01);
		assertEquals(s.getLambda3(), 0.3, 0.01);
		assertEquals(s.getLambda4(), 0.4, 0.01);
		assertEquals(s.getLambda5(), 0.5, 0.01);
		assertEquals(s.getLambda6(), 0.6, 0.01);
		
		s = new SocialPredictionParameters();
		s.setLambda1(0.01);
		s.setLambda2(0.02);
		s.setLambda3(0.03);
		s.setLambda4(0.04);
		s.setLambda5(0.05);
		s.setLambda6(0.06);
		sDao.insert(s);
		
		assertEquals(sDao.findAll().size(), 1);
		s = sDao.findLast();
		assertNotNull(s);
		assertEquals(s.getLambda1(), 0.01, 0.001);
		assertEquals(s.getLambda2(), 0.02, 0.001);
		assertEquals(s.getLambda3(), 0.03, 0.001);
		assertEquals(s.getLambda4(), 0.04, 0.001);
		assertEquals(s.getLambda5(), 0.05, 0.001);
		assertEquals(s.getLambda6(), 0.06, 0.001);
		
		sDao.deleteAll();
		assertEquals(sDao.findAll().size(), 0);
	}
	
	@After
	public void tearDown() {
		sDao.deleteTable();
	}

}
