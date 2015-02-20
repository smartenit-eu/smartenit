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

import eu.smartenit.unada.db.dao.impl.OwnerDAO;
import eu.smartenit.unada.db.dto.Owner;

public class OwnerDAOTest {
	
	private OwnerDAO ownerDao;
	
	@Before
	public void setUp() {
		ownerDao = new OwnerDAO();
		ownerDao.createTable();
	}
	
	@Test
	public void testFunctions() throws Exception {
		assertEquals(ownerDao.findAll().size(), 0);
		assertNull(ownerDao.findLast());
		assertNull(ownerDao.findById("342343213"));
		
		Owner o = new Owner();
		o.setFacebookID("312123412");
		o.setOauthToken("fdsfv32r3dcwsc23d2d32r23fc2d3f3dsafasf3r23rfecscxzhtyjytj56y34t6gf");
		ownerDao.insert(o);
		
		assertEquals(ownerDao.findAll().size(), 1);
		o = ownerDao.findLast();
		assertNotNull(o);
		assertEquals(o.getFacebookID(), "312123412");
		assertEquals(o.getOauthToken(), 
				"fdsfv32r3dcwsc23d2d32r23fc2d3f3dsafasf3r23rfecscxzhtyjytj56y34t6gf");
		
		o = new Owner();
		o.setFacebookID("424124214");
		o.setOauthToken("dgfasdgads32rtjulil9;809;78l8;rfsdsa43423");
		ownerDao.insert(o);
		
		assertEquals(ownerDao.findAll().size(), 1);
		o = ownerDao.findById("424124214");
		assertNotNull(o);
		assertEquals(o.getOauthToken(), 
				"dgfasdgads32rtjulil9;809;78l8;rfsdsa43423");
		
		o.setOauthToken("asdsafwqfawc<wfwfefvxzc");
		ownerDao.update(o);
		assertEquals(ownerDao.findAll().size(), 1);
		o = ownerDao.findLast();
		assertNotNull(o);
		assertEquals(o.getOauthToken(), 
				"asdsafwqfawc<wfwfefvxzc");
		
		ownerDao.deleteAll();
		assertEquals(ownerDao.findAll().size(), 0);
	}
	
	@After
	public void tearDown() {
		ownerDao.deleteTable();
	}

}
