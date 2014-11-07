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
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.CloudDC;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.DC2DCCommunicationID;
import eu.smartenit.sbox.db.dto.Direction;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SBox;

public class DC2DCCommunicationTest {

	private static final Logger logger = LoggerFactory
			.getLogger(DC2DCCommunicationTest.class);

	private DC2DCCommunicationDAO ddao;
	
	private CloudDCDAO cdao;
	
	private ASDAO asdao;

	@Before
	public void setupDb() throws ClassNotFoundException {
		logger.info("Creating all tables for tests.");
		Tables tables = new Tables();
		tables.createAll();
		
		logger.info("Preparing DC2DCCommunication, AS and CloudDC tables.");
		asdao = new ASDAO();
		asdao.deleteAll();
		
		cdao = new CloudDCDAO();
		cdao.deleteAll();
		
		ddao = new DC2DCCommunicationDAO();
		ddao.deleteAll();
	}

	@Test
	public void testFunctions() throws Exception {
		logger.info("Testing simple DC2DCCommunication functions.");
		List<DC2DCCommunication> dcList = ddao.findAll();
		assertEquals(dcList.size(), 0);
		
		AS as1 = new AS();
		as1.setAsNumber(2);
		as1.setLocal(true);
		SBox sbox = new SBox();
		sbox.setManagementAddress(new NetworkAddressIPv4("99.99.99.99", 0));
		as1.setSbox(sbox);
		asdao.insert(as1);
		
		AS as2 = new AS();
		as2.setAsNumber(44);
		as2.setLocal(false);
		sbox = new SBox();
		sbox.setManagementAddress(new NetworkAddressIPv4("7.7.7.7", 0));
		as2.setSbox(sbox);
		asdao.insert(as2);

		CloudDC c1 = new CloudDC();
		c1.setCloudDcName("fb");
		c1.setAs(as1);
		cdao.insert(c1);
		
		CloudDC c2 = new CloudDC();
		c2.setCloudDcName("dropbox");
		c2.setAs(as2);
		cdao.insert(c2);
		assertEquals(cdao.findAll().size(), 2);
		
		DC2DCCommunication d = new DC2DCCommunication();
		DC2DCCommunicationID id = new DC2DCCommunicationID();
		id.setCommunicationNumber(1);
		id.setCommunicationSymbol("c");
		id.setLocalAsNumber(2);
		id.setLocalCloudDCName("fb");
		id.setRemoteAsNumber(44);
		id.setRemoteCloudDCName("dropbox");
		d.setId(id);
		d.setTrafficDirection(Direction.incomingTraffic);

		ddao.insert(d);
		dcList = ddao.findAll();
		assertEquals(dcList.size(), 1);
		
		CloudDC c3 = new CloudDC();
		c3.setCloudDcName("twitter");
		c3.setAs(as1);
		cdao.insert(c3);
		assertEquals(cdao.findAll().size(), 3);
		
		d = new DC2DCCommunication();
		DC2DCCommunicationID id2 = new DC2DCCommunicationID();
		id2.setCommunicationNumber(2);
		id2.setCommunicationSymbol("d");
		id2.setLocalAsNumber(2);
		id2.setLocalCloudDCName("twitter");
		id2.setRemoteAsNumber(44);
		id2.setRemoteCloudDCName("dropbox");
		d.setId(id2);
		d.setTrafficDirection(Direction.incomingTraffic);
		
		ddao.insert(d);
		dcList = ddao.findAll();
		assertEquals(dcList.size(), 2);
		
		ddao.deleteById(id2);
		
		dcList = ddao.findAll();
		assertEquals(dcList.size(), 1);
		
		d = ddao.findById(id);
		assertEquals(d.getId().getCommunicationNumber(), 1);
		
		asdao.deleteById(2);
		assertEquals(asdao.findAll().size(), 1);
		assertEquals(cdao.findAll().size(), 1);
		assertEquals(ddao.findAll().size(), 0);
		
		asdao.deleteAll();
		assertEquals(asdao.findAll().size(), 0);
		assertEquals(cdao.findAll().size(), 0);
		assertEquals(ddao.findAll().size(), 0);
	}

	@After
	public void dropDb() {
		logger.info("Deleting all entries from DC2DCCommunication "
				+ "and CloudDC tables.");
		asdao.deleteAll();
		
		cdao.deleteAll();
		
		ddao.deleteAll();	
	}

}
