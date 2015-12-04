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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.smartenit.unada.db.dao.impl.UNaDaConfigurationDAO;
import eu.smartenit.unada.db.dto.Location;
import eu.smartenit.unada.db.dto.UNaDaConfiguration;
import eu.smartenit.unada.db.dto.WifiConfiguration;

public class UNaDaConfigurationDAOTest {
	
private UNaDaConfigurationDAO uDao;
	
	@Before
	public void setUp() {
		uDao = new UNaDaConfigurationDAO();
		uDao.createTable();
	}
	
	@Test
	public void testFunctions() throws Exception {
		assertEquals(uDao.findAll().size(), 0);
		assertNull(uDao.findLast());
		
		UNaDaConfiguration u = new UNaDaConfiguration();
		u.setIpAddress("1.2.3.4");
		u.setMacAddress("00::33");
		u.setLocation(new Location(23.344242, 43.44232432));
		u.setOpenWifi(new WifiConfiguration("open", ""));
		u.setPrivateWifi(new WifiConfiguration("private", "123456"));
		u.setPort(6767);
		u.setSocialInterval(100000);
		u.setOverlayInterval(40000);
		u.setPredictionInterval(3000);
		uDao.insert(u);
		
		assertEquals(uDao.findAll().size(), 1);
		u = uDao.findLast();
		assertNotNull(u);
		assertEquals(u.getIpAddress(), "1.2.3.4");
		assertEquals(u.getLocation().getLongitude(), 43.44232432, 0.01);
		assertEquals(u.getPrivateWifi().getPassword(), "123456");
		assertEquals(u.getPredictionInterval(), 3000);
		assertTrue(u.isBootstrapNode());
        assertTrue(u.isOverlayPredictionEnabled());
        assertTrue(u.isSocialPredictionEnabled());
        assertEquals(u.getChunkSize(), 102400);
		
		u = new UNaDaConfiguration();
		u.setIpAddress("1.2.3.4");
		u.setMacAddress("00:aa:bb:33:33:33");
		u.setLocation(new Location(23.344242, 43.44232432));
		u.setOpenWifi(new WifiConfiguration("open", ""));
		u.setPrivateWifi(new WifiConfiguration("private", "1234567890"));
		u.setPort(8888);
		u.setSocialInterval(10000);
		u.setOverlayInterval(40000);
		u.setPredictionInterval(9000);
		u.setBootstrapNode(false);
        u.setOverlayPredictionEnabled(false);
        u.setSocialPredictionEnabled(false);
        u.setChunkSize(512000);
		uDao.insert(u);
		
		assertEquals(uDao.findAll().size(), 1);
		u = uDao.findLast();
		assertNotNull(u);
		assertEquals(u.getIpAddress(), "1.2.3.4");
		assertEquals(u.getLocation().getLongitude(), 43.44232432, 0.01);
		assertEquals(u.getPrivateWifi().getPassword(), "1234567890");
		assertEquals(u.getPredictionInterval(), 9000);
        assertFalse(u.isBootstrapNode());
        assertFalse(u.isOverlayPredictionEnabled());
        assertFalse(u.isSocialPredictionEnabled());
        assertEquals(u.getChunkSize(), 512000);
		
		uDao.deleteAll();
		assertEquals(uDao.findAll().size(), 0);
	}
	
	@After
	public void tearDown() {
		uDao.deleteTable();
	}

}
