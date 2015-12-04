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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.smartenit.unada.db.dao.impl.TrustedUserDAO;
import eu.smartenit.unada.db.dto.TrustedUser;

public class TrustedUserDAOTest {
	
	private TrustedUserDAO tDao;
	
	@Before
	public void setUp() {
		tDao = new TrustedUserDAO();
		tDao.createTable();
	}
	
	@Test
	public void testFunctions() throws Exception {
		assertEquals(tDao.findAll().size(), 0);
		assertNull(tDao.findById("342343213"));
		assertNull(tDao.findByMacAddress("00:ff:ff:ff:ff:ff"));
		
		TrustedUser t = new TrustedUser();
		t.setFacebookID("312123412");
		t.setMacAddress("00:44::aa");
        t.setLastAccess(new Date(1000000));
		tDao.insert(t);
		
		assertEquals(tDao.findAll().size(), 1);
		t = tDao.findById("312123412");
		assertNotNull(t);
		assertEquals(t.getFacebookID(), "312123412");
		assertEquals(t.getMacAddress(), "00:44::aa");
        assertEquals(t.getLastAccess().getTime(), 1000000);
		
		t = tDao.findByMacAddress("00:44::aa");
		assertNotNull(t);
		assertEquals(t.getFacebookID(), "312123412");
        assertEquals(t.getLastAccess().getTime(), 1000000);
		
		t = new TrustedUser();
		t.setFacebookID("424124214");
		t.setMacAddress("aa:44:33:dd:33:33");
        t.setLastAccess(new Date(2000000));
		tDao.insert(t);
		
		assertEquals(tDao.findAll().size(), 2);
		t = tDao.findById("424124214");
		assertNotNull(t);
		assertEquals(t.getMacAddress(), "aa:44:33:dd:33:33");
        assertEquals(t.getLastAccess().getTime(), 2000000);
		
		t = tDao.findByMacAddress("aa:44:33:dd:33:33");
		assertNotNull(t);
		assertEquals(t.getFacebookID(), "424124214");
        assertEquals(t.getLastAccess().getTime(), 2000000);
		
		t.setMacAddress("aa:44:11:dd:11:11");
		tDao.update(t);
		
		assertEquals(tDao.findAll().size(), 2);
		t = tDao.findById("424124214");
		assertNotNull(t);
		assertEquals(t.getMacAddress(), "aa:44:11:dd:11:11");
		
		t = new TrustedUser();
		t.setFacebookID("424124214");
		tDao.insert(t);
		
		assertEquals(tDao.findAll().size(), 2);
		t = tDao.findById("424124214");
		assertNotNull(t);
		assertEquals(t.getMacAddress(), "aa:44:11:dd:11:11");
        assertEquals(t.getLastAccess().getTime(), 2000000);
		
		tDao.deleteAll();
		assertEquals(tDao.findAll().size(), 0);
		
		List<TrustedUser> trustedUsers = new ArrayList<TrustedUser>();
		for (int i=0; i<1000; i++) {
			TrustedUser u = new TrustedUser();
			u.setFacebookID(String.valueOf(i));
			u.setMacAddress("00::dd");
			trustedUsers.add(u);
		}
		tDao.insertAll(trustedUsers.listIterator());
		assertEquals(tDao.findAll().size(), 1000);
		
		tDao.deleteAll();
		assertEquals(tDao.findAll().size(), 0);
		
		trustedUsers = new ArrayList<TrustedUser>();
		for (int i=0; i<1000; i++) {
			TrustedUser u = new TrustedUser();
			u.setFacebookID("1");
			u.setMacAddress(String.valueOf(i));
			trustedUsers.add(u);
		}
		tDao.insertAll(trustedUsers.listIterator());
		assertEquals(tDao.findAll().size(), 1);
		assertEquals(tDao.findAll().get(0).getFacebookID(), "1");
		assertEquals(tDao.findAll().get(0).getMacAddress(), "0");
		
		tDao.deleteAll();
		assertEquals(tDao.findAll().size(), 0);
	}
	
	@After
	public void tearDown() {
		tDao.deleteTable();
	}

}
