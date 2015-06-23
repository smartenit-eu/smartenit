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

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dao.util.Tables;
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SBox;

public class BGRouterTest {

	private static final Logger logger = LoggerFactory
			.getLogger(BGRouterTest.class);
	
	private static BGRouterDAO bgdao;
	
	private static ASDAO asdao;

	@BeforeClass
	public static void setupDb() {
		logger.info("Creating all tables for tests.");
		Tables tables = new Tables();
		tables.createAll();
		
		logger.info("Preparing AS and BGRouter tables.");
		asdao = new ASDAO();
		asdao.deleteAll();
		
		bgdao = new BGRouterDAO();
		bgdao.deleteAll();
	}

	
	@Test
	public void testSimpleFunctions() throws Exception {
		logger.info("Testing simple AS functions.");
		List<BGRouter> bglist = bgdao.findAll();
		assertEquals(bglist.size(), 0);
		
		BGRouter b = bgdao.findLast();
		assertNull(b);
		
		b = new BGRouter();
		b.setManagementAddress(new NetworkAddressIPv4("5.5.5.5", 0));
		b.setSnmpCommunity("snmp");
		b.setNetconfUsername("admin");
		b.setNetconfPassword("1234");
		
		bgdao.insert(b);
		
		bglist = bgdao.findAll();
		assertEquals(bglist.size(), 1);
		
		b = bgdao.findById("4.3.5.5");
		assertNull(b);
		b = bgdao.findById("5.5.5.5");
		assertNotNull(b);
		assertEquals(b.getSnmpCommunity(), "snmp");
		assertEquals(b.getNetconfPassword(), "1234");
		assertEquals(b.getNetconfUsername(), "admin");
		
		b = new BGRouter();
		b.setManagementAddress(new NetworkAddressIPv4("1.1.1.1", 0));
		b.setSnmpCommunity("abcd");
		b.setNetconfUsername("admin2");
		b.setNetconfPassword("123456");
		
		bgdao.insert(b);
		
		bglist = bgdao.findAll();
		assertEquals(bglist.size(), 2);
		
		b = bgdao.findLast();
		assertEquals(b.getManagementAddress().getPrefix(), "1.1.1.1");
		
		b = bgdao.findById("1.1.1.1");
		assertNotNull(b);
		assertEquals(b.getSnmpCommunity(), "abcd");
		assertEquals(b.getNetconfPassword(), "123456");
		assertEquals(b.getNetconfUsername(), "admin2");
		
		b.setSnmpCommunity("efg");	
		b.setNetconfPassword("123456789");
		bgdao.update(b);
		
		bglist = bgdao.findAll();
		assertEquals(bglist.size(), 2);
		
		b = bgdao.findById("1.1.1.1");
		assertNotNull(b);
		assertEquals(b.getSnmpCommunity(), "efg");
		assertEquals(b.getNetconfPassword(), "123456789");
		assertEquals(b.getNetconfUsername(), "admin2");
		
		bgdao.deleteById("1.1.1.1");
		
		bglist = bgdao.findAll();
		assertEquals(bglist.size(), 1);
		
		bgdao.deleteAll();
		bglist = bgdao.findAll();
		assertEquals(bglist.size(), 0);
	}
	
	@Test
	public void testASBGRouterFunctions() throws Exception {
		logger.info("Testing AS and BGRouter functions.");
		AS a = new AS();
		a.setAsNumber(2);
		SBox sbox= new SBox();
		sbox.setManagementAddress(new NetworkAddressIPv4("1.2.3.4", 0));
		a.setSbox(sbox);
		a.setLocal(true);
		asdao.insert(a);
		
		List<AS> aList = asdao.findAll();
		assertNotNull(aList);
		assertEquals(1, aList.size());
		
		BGRouter b = new BGRouter();
		b.setManagementAddress(new NetworkAddressIPv4("5.5.5.5", 0));
		b.setSnmpCommunity("snmp");
		b.setNetconfUsername("admin");
		b.setNetconfPassword("1234");
		
		bgdao.insertByASNumber(b, a.getAsNumber());
		
		List<BGRouter> bglist = bgdao.findAll();
		assertEquals(bglist.size(), 1);
		
		bglist = bgdao.findByASNumber(4);
		assertEquals(bglist.size(), 0);
		
		bglist = bgdao.findByASNumber(2);
		assertEquals(bglist.size(), 1);
		
		asdao.deleteById(a.getAsNumber());
		aList = asdao.findAll();
		assertEquals(0, aList.size());
		
		bglist = bgdao.findAll();
		assertEquals(bglist.size(), 0);
		
	}

	
	@AfterClass
	public static void dropDb() {
		
		logger.info("Deleting all entries from AS and BGRouter table.");
		asdao.deleteAll();
		bgdao.deleteAll();
		
	}
}
