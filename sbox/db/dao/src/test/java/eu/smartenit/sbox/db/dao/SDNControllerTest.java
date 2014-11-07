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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dao.util.Tables;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SDNController;

public class SDNControllerTest {

	private static final Logger logger = LoggerFactory
			.getLogger(SDNControllerTest.class);

	private SDNControllerDAO sdao;

	@Before
	public void setupDb() {
		logger.info("Creating all tables for tests.");
		Tables tables = new Tables();
		tables.createAll();
		
		logger.info("Preparing SDNController table.");
		sdao = new SDNControllerDAO();
		sdao.deleteAll();
	}

	@After
	public void dropDb() {

		logger.info("Deleting all entries from SDNController table.");
		sdao.deleteAll();
	}

	@Test
	public void testFunctions() throws Exception {
		logger.info("Testing simple SDNControllerDAO functions.");
		List<SDNController> list = sdao.findAll();
		assertEquals(list.size(), 0);
		
		SDNController s = new SDNController();
		s.setManagementAddress(new NetworkAddressIPv4("1.2.3.4", 0));
		s.setOpenflowHost(new NetworkAddressIPv4("1.2.3.5", 0));
		s.setOpenflowPort(8080);
		s.setRestHost(new NetworkAddressIPv4("1.2.3.3", 0));
		s.setRestPort(8081);
		  
		sdao.insert(s);
		list = sdao.findAll();
		assertEquals(list.size(), 1);
		s = sdao.findById("1.2.3.1");
		assertNull(s);
		s = sdao.findById("1.2.3.4");
		assertNotNull(s);
		assertEquals(s.getOpenflowHost().getPrefix(), "1.2.3.5");
		
		s.setOpenflowPort(12345);
		sdao.update(s);
		
		s = sdao.findById("1.2.3.4");
		assertNotNull(s);
		assertEquals(s.getOpenflowHost().getPrefix(), "1.2.3.5");
		assertEquals(s.getOpenflowPort(), 12345);
		
		sdao.deleteById("1.2.3.4");
		assertEquals(sdao.findAll().size(), 0);
	}
}
