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

import eu.smartenit.unada.db.dao.impl.CacheDAO;
import eu.smartenit.unada.db.dto.Cache;

public class CacheDAOTest {
	
	private CacheDAO cDao;
			
	@Before
	public void setUp() {
		cDao = new CacheDAO();
		cDao.createTable();
	}
	
	@Test
	public void testFunctions() throws Exception {
		assertEquals(cDao.findAll().size(), 0);
		
		Cache cache = new Cache();
		cache.setSize(321323232);
		cache.setSizeThreshold(10000);
		cache.setTimeToLive(1000000000);
		cache.setSocialThreshold(0.0224);
		cache.setOverlayThreshold(3.244);
		cDao.insert(cache);
				
		assertEquals(cDao.findAll().size(), 1);
		cache = cDao.findLast();
		assertEquals(cache.getSize(), 321323232);
		assertEquals(cache.getTimeToLive(), 1000000000);
		assertEquals(cache.getOverlayThreshold(), 3.244, 0.01);
		assertEquals(cache.getSocialThreshold(), 0.0224, 0.01);
		
		cache.setSize(12121212);
		cache.setOverlayThreshold(9.1);
		cDao.insert(cache);
		
		assertEquals(cDao.findAll().size(), 1);
		cache = cDao.findLast();
		assertEquals(cache.getSize(), 12121212);
		assertEquals(cache.getOverlayThreshold(), 9.1, 0.01);
		
		cDao.deleteAll();
		assertEquals(cDao.findAll().size(), 0);
	}
	
	@After
	public void tearDown() {
		cDao.deleteTable();
	}

}
