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

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dao.util.Tables;
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.CloudDC;
import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SBox;
import eu.smartenit.sbox.db.dto.SDNController;

public class CloudDCTest {
	
	private static final Logger logger = LoggerFactory
			.getLogger(DARouterTest.class);
	
	private static DARouterDAO ddao;

	private static SDNControllerDAO sdao;
	
	private static PrefixDAO pdao;
	
	private static CloudDCDAO cdao;
	
	private static ASDAO asdao;

	@BeforeClass
	public static void setupDb() {
		logger.info("Creating all tables for tests.");
		Tables tables = new Tables();
		tables.createAll();

		logger.info("Preparing SDNController, DARouter, Prefix, CloudDC and AS tables.");
		
		ddao = new DARouterDAO();
		ddao.deleteAll();
		
		sdao = new SDNControllerDAO();
		sdao.deleteAll();
		
		pdao = new PrefixDAO();
		pdao.deleteAll();
		
		cdao = new CloudDCDAO();
		cdao.deleteAll();
		
		asdao = new ASDAO();
		asdao.deleteAll();
	}
	
	@Test
	public void testFunctions() throws Exception {
		logger.info("Testing simple CloudDCDAO functions.");
		List<CloudDC> clist = cdao.findAll();
		assertEquals(clist.size(), 0);
		
		SDNController s = new SDNController();
		s.setManagementAddress(new NetworkAddressIPv4("1.2.3.4", 0));
		s.setOpenflowHost(new NetworkAddressIPv4("1.2.3.5", 0));
		s.setOpenflowPort(8080);
		s.setRestHost(new NetworkAddressIPv4("1.2.3.3", 0));
		s.setRestPort(8081);  
		sdao.insert(s);
		
		DARouter d1 = new DARouter();
		d1.setManagementAddress(new NetworkAddressIPv4("1.5.5.5", 0));
		d1.setSnmpCommunity("snmp");
		ddao.insert(d1);
		
		DARouter d2 = new DARouter();
		d2.setManagementAddress(new NetworkAddressIPv4("5.5.5.5", 0));
		d2.setSnmpCommunity("snmp");
		ddao.insert(d2);
		
		CloudDC c1 = new CloudDC();
		c1.setCloudDcName("facebook");
		AS as = new AS();
		as.setAsNumber(2);
		as.setLocal(true);
		SBox sbox1 = new SBox();
		sbox1.setManagementAddress(new NetworkAddressIPv4("99.99.99.99", 0));
		as.setSbox(sbox1);
		asdao.insert(as);
		c1.setAs(as);
		c1.setDaRouter(d1);
		c1.setSdnController(s);	
		List<NetworkAddressIPv4> prefixes = new ArrayList<NetworkAddressIPv4>();
		prefixes.add(new NetworkAddressIPv4("73.7.8.0", 24));
		prefixes.add(new NetworkAddressIPv4("22.4.8.0", 14));
		c1.setDcNetworks(prefixes);
		
		cdao.insert(c1);
		
		clist = cdao.findAll();
		assertEquals(clist.size(), 1);
		assertEquals(clist.get(0).getCloudDcName(), "facebook");
		assertEquals(clist.get(0).getDcNetworks().size(), 2);
		
		CloudDC c2 = new CloudDC();
		c2.setCloudDcName("dropbox");
		AS as2 = new AS();
		as2.setAsNumber(3);
		as2.setLocal(true);
		SBox sbox2 = new SBox();
		sbox2.setManagementAddress(new NetworkAddressIPv4("88.88.88.88", 0));
		as.setSbox(sbox2);
		asdao.insert(as2);
		c2.setAs(as2);
		c2.setDaRouter(d2);
		c2.setSdnController(s);	
		prefixes = new ArrayList<NetworkAddressIPv4>();
		prefixes.add(new NetworkAddressIPv4("9.99.8.0", 24));
		c2.setDcNetworks(prefixes);
		
		cdao.insert(c2);
		
		clist = cdao.findAll();
		assertEquals(clist.size(), 2);
		
		clist = cdao.findLocalClouds();
		assertEquals(clist.size(), 2);
		
		clist = cdao.findRemoteClouds();
		assertEquals(clist.size(), 0);
		
		CloudDC c = cdao.findById("twitter");
		assertNull(c);
		
		c = cdao.findById("dropbox");
		assertNotNull(c);
		assertEquals(c.getDaRouter().getManagementAddress().getPrefix(), "5.5.5.5");
		assertEquals(c.getDcNetworks().size(), 1);
		
		
		clist = cdao.findByASNumber(2);
		assertNotNull(clist);
		assertEquals(clist.size(), 1);
		
		clist = cdao.findByASNumber(3);
		assertNotNull(clist);
		assertEquals(clist.size(), 1);
		
		c.setAs(as);
		c.setDaRouter(d1);
		cdao.update(c);
		
		c = cdao.findById("dropbox");
		assertNotNull(c);
		assertEquals(c.getDaRouter().getManagementAddress().getPrefix(), "1.5.5.5");
		assertEquals(c.getDcNetworks().size(), 1);
		assertEquals(c.getAs().getAsNumber(), 2);
		assertEquals(pdao.findAll().size(), 3);
		
		prefixes = new ArrayList<NetworkAddressIPv4>();
		prefixes.add(new NetworkAddressIPv4("2.2.3.0", 24));
		prefixes.add(new NetworkAddressIPv4("22.2.3.0", 24));
		prefixes.add(new NetworkAddressIPv4("55.45.3.0", 8));
		c.setDcNetworks(prefixes);
		cdao.update(c);
		assertEquals(cdao.findAll().size(), 2);
		assertEquals(pdao.findAll().size(), 5);
		
		c = cdao.findById("dropbox");
		assertNotNull(c);
		assertEquals(c.getDaRouter().getManagementAddress().getPrefix(), "1.5.5.5");
		assertEquals(c.getDcNetworks().size(), 3);
		assertEquals(c.getDcNetworks().get(1).getPrefix(), "22.2.3.0");
		
		clist = cdao.findByASNumber(2);
		assertNotNull(clist);
		assertEquals(clist.size(), 2);
		
		clist = cdao.findByASNumber(3);
		assertNotNull(clist);
		assertEquals(clist.size(), 0);
		
		cdao.deleteById("dropbox");
		clist = cdao.findAll();
		assertEquals(clist.size(), 1);
		assertEquals(pdao.findAll().size(), 2);
		
		cdao.deleteAll();
		clist = cdao.findAll();
		assertEquals(clist.size(), 0);
		
		//checking cloud insertion and find without prefixes, da router and sdn
		c2 = new CloudDC();
		c2.setCloudDcName("google+");
		c2.setAs(as2);
		//c2.setDaRouter(d2);
		//c2.setSdnController(s);	
		
		cdao.insert(c2);
		clist = cdao.findAll();
		assertEquals(clist.size(), 1);
		assertEquals(clist.get(0).getDcNetworks().size(), 0);
		assertNotNull(clist.get(0).getDaRouter());
		
		asdao.deleteAll();
		assertEquals(cdao.findAll().size(), 0);
		assertEquals(pdao.findAll().size(), 0);
	}
	
	
	@AfterClass
	public static void dropDb() {

		logger.info("Deleting all entries from "
				+ "SDNController, DARouter, Prefix, CloudDC and AS tables.");
		ddao.deleteAll();
		
		sdao.deleteAll();
		
		pdao.deleteAll();
		
		cdao.deleteAll();
		
		asdao.deleteAll();
	}

}
