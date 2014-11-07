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

import eu.smartenit.sbox.db.dao.ASDAO;
import eu.smartenit.sbox.db.dao.util.Tables;
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SBox;

public class ASTest {
	
	private static final Logger logger = LoggerFactory.getLogger(ASTest.class);

	private static ASDAO asdao;
	
	@BeforeClass
	public static void setupDb() throws ClassNotFoundException {
		logger.info("Creating all tables for tests.");
		Tables tables = new Tables();
		tables.createAll();
		
		logger.info("Preparing AS table.");
		asdao = new ASDAO();
		asdao.deleteAll();
	}
	
	
	@Test
	public void testFunctions() throws Exception {
		logger.info("Testing ASDAO functions.");
		List<AS> asList = asdao.findAll();
		assertNotNull(asList);
		assertEquals(0, asList.size());
		
		//Inserting AS.");
		AS a = new AS();
		a.setAsNumber(211);
		SBox sbox= new SBox();
		a.setSbox(sbox);
		a.setLocal(false);
		asdao.insert(a);
		
		//Checking non empty AS table
		asList = asdao.findAll();
		assertNotNull(asList);
		assertEquals(1, asList.size());
		
		a = new AS();
		a.setAsNumber(212);
		SBox sbox1= new SBox(new NetworkAddressIPv4("1.2.3.4", 0));
		a.setSbox(sbox1);
		a.setLocal(true);
		asdao.insert(a);
		
		asList = asdao.findAll();
		assertNotNull(asList);
		assertEquals(2, asList.size());
		
		AS a2 = asdao.findLast();
		assertNotNull(a2);
		assertEquals(212, a2.getAsNumber());
		assertEquals("1.2.3.4", a2.getSbox().getManagementAddress().getPrefix());
		
		a2 = asdao.findByAsNumber(2412);
		assertNull(a2);
		
		a2 = asdao.findByAsNumber(212);
		assertNotNull(a2);
		assertEquals(212, a2.getAsNumber());
		assertEquals("1.2.3.4", a2.getSbox().getManagementAddress().getPrefix());
		
		a2.setSbox(new SBox(new NetworkAddressIPv4("1.2.3.55", 0)));
		asdao.update(a2);
		a2 = asdao.findLast();
		assertNotNull(a2);
		assertEquals(212, a2.getAsNumber());
		assertEquals("1.2.3.55", a2.getSbox().getManagementAddress().getPrefix());
		
		List<AS> localAses = asdao.findLocalAs();
		assertEquals(localAses.size(), 1);
		
		List<AS> remoteAses = asdao.findRemoteAs();
		assertEquals(remoteAses.size(), 1);
		
		asdao.deleteById(212);
		asList = asdao.findAll();
		assertNotNull(asList);
		assertEquals(asList.size(), 1);
		
		asdao.deleteAll();
		asList = asdao.findAll();
		assertNotNull(asList);
		assertEquals(asList.size(), 0);
		
	}
	
	
	@AfterClass
	public static void dropDb() {
		logger.info("Deleting all entries from AS table.");
		asdao.deleteAll();
	}
}
