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
import eu.smartenit.sbox.db.dto.CostFunction;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.Segment;
import eu.smartenit.sbox.db.dto.SimpleLinkID;

public class CostFunctionTest {

	private static final Logger logger = LoggerFactory
			.getLogger(CostFunctionTest.class);

	private static LinkDAO ldao;
	
	private static CostFunctionDAO cdao;
	
	private static SegmentDAO sdao;
	
	
	@BeforeClass
	public static void setupDb() {
		logger.info("Creating all tables for tests.");
		Tables tables = new Tables();
		tables.createAll();
		
		logger.info("Preparing Link, CostFunction, and Segment tables.");
		
		ldao = new LinkDAO();
		ldao.deleteAll();
		
		cdao = new CostFunctionDAO();
		cdao.deleteAll();
		
		sdao = new SegmentDAO();
		sdao.deleteAll();
		
	}


	@Test
	public void testFunctions() throws Exception {
		logger.info("Testing simple CostFunctionDAO functions.");
		List<CostFunction> clist = cdao.findAll();
		assertEquals(clist.size(), 0);
		
		CostFunction c = cdao.findByLinkId(new SimpleLinkID("link", "ote"));
		assertNull(c);
		
		//inserting normal link and costfunction
		Link l = new Link();
		l.setLinkID(new SimpleLinkID("link", "ote"));
		l.setPhysicalInterfaceName("name");
		NetworkAddressIPv4 address = new NetworkAddressIPv4();
		address.setPrefix("1.2.3.4");
		address.setPrefixLength(2);
		l.setAddress(address);
		l.setVlan(3);		
		c = new CostFunction("cost", "subtype", "bw", "cost", null);
		List<Segment> segments = new ArrayList<Segment>();
		segments.add(new Segment(242, 4242, (float)0.5, (float)0.6));
		segments.add(new Segment(2422, 42425, (float)0.9, (float)4.3));
		c.setSegments(segments);
		l.setCostFunction(c);
		ldao.insert(l);

		clist = cdao.findAll();
		assertEquals(clist.size(), 1);
		
		c = cdao.findByLinkId(new SimpleLinkID("link", "ote"));
		assertNotNull(c);
		assertEquals(c.getInputUnit(), "bw");
		assertEquals(c.getSegments().size(), 2);
		assertEquals(c.getSegments().get(0).getA(), 0.5, 0.01);
		assertEquals(c.getSegments().get(1).getB(), 4.3, 0.01);
		
		//inserting link with no segments
		l = new Link();
		l.setLinkID(new SimpleLinkID("link2", "ote"));
		l.setPhysicalInterfaceName("name");
		address = new NetworkAddressIPv4();
		address.setPrefix("1.2.3.54");
		address.setPrefixLength(2);
		l.setAddress(address);
		l.setVlan(4);		
		c = new CostFunction("cost", "subtype", "bw", "cost", null);
		l.setCostFunction(c);
		ldao.insert(l);

		clist = cdao.findAll();
		assertEquals(clist.size(), 2);
		
		c = cdao.findByLinkId(new SimpleLinkID("link2", "ote"));
		assertNotNull(c);
		assertEquals(c.getInputUnit(), "bw");
		assertEquals(c.getSegments().size(), 0);

		assertEquals(sdao.findAll().size(), 2);
		assertEquals(ldao.findAll().size(), 2);
		
		cdao.deleteByLinkId(new SimpleLinkID("link", "ote"));
		assertEquals(cdao.findAll().size(), 1);
		assertEquals(sdao.findAll().size(), 0);
		assertEquals(ldao.findAll().size(), 2);
		
		cdao.deleteAll();
		assertEquals(cdao.findAll().size(), 0);
		assertEquals(sdao.findAll().size(), 0);
		assertEquals(ldao.findAll().size(), 2);
		
	}
	
	
	
	@AfterClass
	public static void dropDb() {
		logger.info("Deleting Link, CostFunction, and Segment tables.");
		
		ldao.deleteAll();
		
		cdao.deleteAll();
		
		sdao.deleteAll();
	}

}
