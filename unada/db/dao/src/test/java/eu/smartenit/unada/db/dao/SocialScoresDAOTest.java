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
package eu.smartenit.unada.db.dao;

import eu.smartenit.unada.db.dao.impl.SocialScoresDAO;
import eu.smartenit.unada.db.dto.SocialScores;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SocialScoresDAOTest {
	
private SocialScoresDAO sDao;
	
	@Before
	public void setUp() {
		sDao = new SocialScoresDAO();
		sDao.createTable();
	}
	
	@Test
	public void testFunctions() throws Exception {
		assertEquals(sDao.findAll().size(), 0);

		SocialScores s = new SocialScores(123456, 1, 2, 3, 4, 5);
		sDao.insert(s);
		
		assertEquals(sDao.findAll().size(), 1);
        s = sDao.findAll().get(0);
		assertNotNull(s);
		assertEquals(s.getAlpha(), 1, 0.01);
		assertEquals(s.getDelta(), 2, 0.01);
		assertEquals(s.getEta(), 3, 0.01);
		assertEquals(s.getPhi(), 4, 0.01);
		assertEquals(s.getGamma(), 5, 0.01);

        s = new SocialScores(123456, 5, 4, 3, 2, 1);
		sDao.insert(s);

        assertEquals(sDao.findAll().size(), 1);
        s = sDao.findAll().get(0);
        assertNotNull(s);
        assertEquals(s.getAlpha(), 5, 0.01);
        assertEquals(s.getDelta(), 4, 0.01);
        assertEquals(s.getEta(), 3, 0.01);
        assertEquals(s.getPhi(), 2, 0.01);
        assertEquals(s.getGamma(), 1, 0.01);

        s = new SocialScores(1234567, 1, 4, 3, 2, 1);
        sDao.insert(s);

        assertEquals(sDao.findAll().size(), 2);

        sDao.deleteAll();
        assertEquals(sDao.findAll().size(), 0);
	}
	
	@After
	public void tearDown() {
		sDao.deleteTable();
	}

}
