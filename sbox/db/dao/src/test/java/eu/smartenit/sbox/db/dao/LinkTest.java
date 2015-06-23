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

import eu.smartenit.sbox.db.dao.LinkDAO;
import eu.smartenit.sbox.db.dao.util.Tables;
import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.CostFunction;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.Segment;
import eu.smartenit.sbox.db.dto.SimpleLinkID;

public class LinkTest {

	private static final Logger logger = LoggerFactory
			.getLogger(LinkTest.class);

	private static LinkDAO ldao;
	
	private static CostFunctionDAO cdao;
	
	private static SegmentDAO sdao;
	
	private static BGRouterDAO bgdao;
	
	private static TunnelDAO tdao;

	@BeforeClass
	public static void setupDb() {
		logger.info("Creating all tables for tests.");
		Tables tables = new Tables();
		tables.createAll();
		
		logger.info("Preparing BGRouter, Link, CostFunction, Segment "
				+ "and Tunnel tables.");
		bgdao = new BGRouterDAO();
		bgdao.deleteAll();
		
		ldao = new LinkDAO();
		ldao.deleteAll();
		
		cdao = new CostFunctionDAO();
		cdao.deleteAll();
		
		sdao = new SegmentDAO();
		sdao.deleteAll();
		
		tdao = new TunnelDAO();
		tdao.deleteAll();
	}


	@Test
	public void testFunctions() throws Exception {
		logger.info("Testing simple LinkDAO functions.");
		List<Link> list = ldao.findAll();
		assertEquals(list.size(), 0);
		
		BGRouter bg = new BGRouter();
		bg.setManagementAddress(new NetworkAddressIPv4("5.5.5.5", 0));
		bg.setSnmpCommunity("snmp");
		bg.setNetconfPassword("12133");
		bg.setNetconfUsername("admin");
		bgdao.insert(bg);
		
		Link l = new Link();
		l.setLinkID(new SimpleLinkID("link", "ote"));
		l.setPhysicalInterfaceName("name");
		NetworkAddressIPv4 address = new NetworkAddressIPv4();
		address.setPrefix("1.2.3.4");
		address.setPrefixLength(2);
		l.setAddress(address);
		l.setVlan(3);
		l.setTunnelEndPrefix(new NetworkAddressIPv4("1.1.1.0", 16));
		CostFunction c = new CostFunction("cost", "subtype", "bw", "cost", null);
		List<Segment> segments = new ArrayList<Segment>();
		segments.add(new Segment(242, 4242, (float)0.5, (float)0.6));
		segments.add(new Segment(2422, 42425, (float)0.9, (float)4.3));
		c.setSegments(segments);
		l.setCostFunction(c);
		l.setBgRouter(bg);
		ldao.insert(l);

		list = ldao.findAll();
		assertEquals(list.size(), 1);
		Link stored = list.get(0);
		assertEquals(stored.getAddress().getPrefix(), "1.2.3.4");
		assertEquals(stored.getAddress().getPrefixLength(), 32);
		assertEquals(((SimpleLinkID) stored.getLinkID()).getLocalIspName(), "ote");
		assertEquals(stored.getVlan(), 3);
		assertEquals(stored.getTunnelEndPrefix().getPrefix(), "1.1.1.0");
		assertEquals(stored.getTunnelEndPrefix().getPrefixLength(), 16);
		assertEquals(stored.getCostFunction().getInputUnit(), "bw");
		assertEquals(stored.getCostFunction().getSegments().size(), 2);
		assertEquals(stored.getCostFunction().getSegments().get(0).getA(), 0.5, 0.01);
		assertEquals(stored.getCostFunction().getSegments().get(1).getB(), 4.3, 0.01);
		assertEquals(stored.getPolicerBandwidthLimitFactor(), 1, 0.001);
		assertEquals(stored.getBgRouter().getManagementAddress().getPrefix(), "5.5.5.5");
		
		l = new Link();
		l.setLinkID(new SimpleLinkID("link2", "ote"));
		l.setPhysicalInterfaceName("name");
		address = new NetworkAddressIPv4();
		address.setPrefix("1.2.3.54");
		address.setPrefixLength(2);
		l.setAddress(address);
		l.setVlan(4);
		l.setTunnelEndPrefix(new NetworkAddressIPv4("2.2.2.0", 16));
		l.setPolicerBandwidthLimitFactor(0.5);
		c = new CostFunction("cost", "subtype", "bw", "cost", null);
		segments = new ArrayList<Segment>();
		segments.add(new Segment(343, 5555, (float)5.5, (float)8.4));
		c.setSegments(segments);
		l.setCostFunction(c);
		l.setBgRouter(bg);
		ldao.insert(l);

		list = ldao.findAll();
		assertEquals(list.size(), 2);
		
		l = ldao.findById(new SimpleLinkID("link3", "ote"));
		assertNull(l);
		
		l = ldao.findById(new SimpleLinkID("link2", "ote"));
		assertNotNull(l);
		assertEquals(l.getVlan(), 4);
		assertEquals(l.getTunnelEndPrefix().getPrefix(), "2.2.2.0");
		assertEquals(l.getTunnelEndPrefix().getPrefixLength(), 16);
		assertEquals(l.getCostFunction().getInputUnit(), "bw");
		assertEquals(l.getCostFunction().getSegments().size(), 1);
		assertEquals(l.getCostFunction().getSegments().get(0).getA(), 5.5, 0.01);
		assertEquals(l.getPolicerBandwidthLimitFactor(), 0.5, 0.001);
		assertEquals(l.getBgRouter().getManagementAddress().getPrefix(), "5.5.5.5");
		assertEquals(l.getBgRouter().getNetconfUsername(), "admin");
		
		l.setVlan(4242);
		l.setTunnelEndPrefix(new NetworkAddressIPv4("2.2.0.0", 16));
		l.setPolicerBandwidthLimitFactor(0.7);
		ldao.update(l);
		l = ldao.findById(new SimpleLinkID("link2", "ote"));
		assertNotNull(l);
		assertEquals(l.getVlan(), 4242);
		assertEquals(l.getTunnelEndPrefix().getPrefix(), "2.2.0.0");
		assertEquals(l.getTunnelEndPrefix().getPrefixLength(), 16);
		assertEquals(l.getPolicerBandwidthLimitFactor(), 0.7, 0.001);
		
		ldao.deleteById(new SimpleLinkID("link2", "ote"));
		list = ldao.findAll();
		assertEquals(list.size(), 1);
		
		assertEquals(cdao.findAll().size(), 1);
		assertEquals(sdao.findAll().size(), 2);
		
		l = ldao.findById(new SimpleLinkID("link", "ote"));
		segments = new ArrayList<Segment>();
		segments.add(new Segment(757, 64664, (float)1.5, (float)0.4));
		segments.add(new Segment(74, 424, (float)1.9, (float)4.3));
		segments.add(new Segment(575, 3, (float)1.5, (float)20.6));
		segments.add(new Segment(42424, 131, (float)13.3, (float)5.3));
		c.setSegments(segments);
		l.setCostFunction(c);
		
		ldao.update(l);		
		assertEquals(ldao.findAll().size(), 1);
		assertEquals(cdao.findAll().size(), 1);
		assertEquals(sdao.findAll().size(), 4);
		
		l = ldao.findById(new SimpleLinkID("link", "ote"));
		assertEquals(l.getCostFunction().getSegments().size(), 4);
		assertEquals(l.getCostFunction().getSegments().get(1).getA(), 1.9, 0.01);
		assertEquals(l.getCostFunction().getSegments().get(3).getA(), 13.3, 0.01);
		
	}
	
	@Test
	public void testComplexFunctions () throws Exception {
		logger.info("Testing complex LinkDAO functions.");
		List<Link> list = ldao.findAll();
		assertEquals(list.size(), 1);
		
		List<BGRouter> bglist = bgdao.findAll();
		assertEquals(bglist.size(), 1);
		
		BGRouter b = bgdao.findById("5.5.5.5");
		assertNotNull(b);
		assertEquals(b.getManagementAddress().getPrefix(), "5.5.5.5");
		
		Link l = ldao.findById(new SimpleLinkID("link", "ote"));
		assertNotNull(l);
		assertEquals(l.getVlan(), 3);
		l.setBgRouter(b);
		
		ldao.update(l);
		l = ldao.findById(new SimpleLinkID("link", "ote"));
		assertNotNull(l);
		assertEquals(l.getBgRouter().getManagementAddress().getPrefix(), "5.5.5.5");
		
		list = ldao.findByBGRouterAddress("2.5.5.5");
		assertEquals(list.size(), 0);
		
		list = ldao.findByBGRouterAddress("5.5.5.5");
		assertEquals(list.size(), 1);
		assertEquals(list.get(0).getBgRouter().getManagementAddress().getPrefix(), "5.5.5.5");
		assertEquals(list.get(0).getCostFunction().getSegments().size(), 4);
		assertEquals(list.get(0).getPolicerBandwidthLimitFactor(), 1, 0.001);
		
		l.getCostFunction().setSegments(new ArrayList<Segment>());
		ldao.update(l);
		assertEquals(ldao.findAll().size(), 1);
		l = ldao.findById(new SimpleLinkID("link", "ote"));
		assertEquals(l.getCostFunction().getInputUnit(), "bw");
		assertEquals(l.getCostFunction().getSegments().size(), 0);
		
		bgdao.deleteById("5.5.5.5");
		bglist = bgdao.findAll();
		assertEquals(bglist.size(), 0);
		
		assertEquals(ldao.findAll().size(), 0);
		assertEquals(cdao.findAll().size(), 0);
		assertEquals(sdao.findAll().size(), 0);
	}
	
	
	@AfterClass
	public static void dropDb() {
		logger.info("Deleting BGRouter, Link, CostFunction, Segment and Tunnel tables.");
		bgdao.deleteAll();
		
		ldao.deleteAll();
		
		cdao.deleteAll();
		
		sdao.deleteAll();
		
		tdao.deleteAll();
	}

}
