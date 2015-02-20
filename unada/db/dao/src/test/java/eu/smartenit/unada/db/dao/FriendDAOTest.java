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

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.smartenit.unada.db.dao.impl.FriendDAO;
import eu.smartenit.unada.db.dto.Friend;

public class FriendDAOTest {
	
	private FriendDAO fDao;
	
	@Before
	public void setUp() {
		fDao = new FriendDAO();
		fDao.createTable();
	}
	
	@Test
	public void testFunctions() throws Exception {
		assertEquals(fDao.findAll().size(), 0);
		assertNull(fDao.findById("342343213"));
		
		Friend t = new Friend();
		t.setFacebookID("312123412");
		t.setFacebookName("George Petro");
		fDao.insert(t);
		
		assertEquals(fDao.findAll().size(), 1);
		t = fDao.findById("312123412");
		assertNotNull(t);
		assertEquals(t.getFacebookID(), "312123412");
		assertEquals(t.getFacebookName(), "George Petro");
		
		t = new Friend();
		t.setFacebookID("424124214");
		t.setFacebookName("Sergios Soursos");
		fDao.insert(t);
		
		assertEquals(fDao.findAll().size(), 2);
		t = fDao.findById("424124214");
		assertNotNull(t);
		assertEquals(t.getFacebookName(), "Sergios Soursos");
		
		t.setFacebookName("Sergios Soursos II");
		fDao.update(t);
		
		assertEquals(fDao.findAll().size(), 2);
		t = fDao.findById("424124214");
		assertNotNull(t);
		assertEquals(t.getFacebookName(), "Sergios Soursos II");
		
		t = new Friend();
		t.setFacebookID("424124214");
		t.setFacebookName("George Petro");
		fDao.insert(t);
		
		assertEquals(fDao.findAll().size(), 2);
		t = fDao.findById("424124214");
		assertNotNull(t);
		assertEquals(t.getFacebookName(), "George Petro");
		
		fDao.deleteAll();
		assertEquals(fDao.findAll().size(), 0);
		
		List<Friend> trustedUsers = new ArrayList<Friend>();
		for (int i=0; i<1000; i++) {
			Friend u = new Friend();
			u.setFacebookID(String.valueOf(i));
			u.setFacebookName("Random name");
			trustedUsers.add(u);
		}
		fDao.insertAll(trustedUsers.listIterator());
		assertEquals(fDao.findAll().size(), 1000);
		
		fDao.deleteAll();
		assertEquals(fDao.findAll().size(), 0);
		
		trustedUsers = new ArrayList<Friend>();
		for (int i=0; i<1000; i++) {
			Friend u = new Friend();
			u.setFacebookID("1");
			u.setFacebookName(String.valueOf(i));
			trustedUsers.add(u);
		}
		fDao.insertAll(trustedUsers.listIterator());
		assertEquals(fDao.findAll().size(), 1);
		assertEquals(fDao.findAll().get(0).getFacebookID(), "1");
		assertEquals(fDao.findAll().get(0).getFacebookName(), "999");
		
		fDao.deleteAll();
		assertEquals(fDao.findAll().size(), 0);
	}
	
	@After
	public void tearDown() {
		fDao.deleteTable();
	}

}
