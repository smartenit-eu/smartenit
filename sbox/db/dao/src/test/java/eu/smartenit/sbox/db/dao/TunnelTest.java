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

import eu.smartenit.sbox.db.dto.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dao.LinkDAO;
import eu.smartenit.sbox.db.dao.util.Tables;

public class TunnelTest {

	private static final Logger logger = LoggerFactory
			.getLogger(TunnelTest.class);

	private LinkDAO ldao;
	
	private CostFunctionDAO cdao;
	
	private SegmentDAO sdao;
	
	private TunnelDAO tdao;
	
	private DC2DCCommunicationDAO ddao;
	
	private ASDAO asdao;
	
	private CloudDCDAO cldao;

	@Before
	public void setupDb() {	
		logger.info("Creating all tables for tests.");
		Tables tables = new Tables();
		tables.createAll();
		
		logger.info("Preparing Link, CostFunction, Segment, Tunnel, AS, "
				+ "Cloud and DC2DCCommunication tables.");
		asdao = new ASDAO();
		asdao.deleteAll();
		
		cldao = new CloudDCDAO();
		cldao.deleteAll();
		
		ldao = new LinkDAO();
		ldao.deleteAll();
		
		cdao = new CostFunctionDAO();
		cdao.deleteAll();
		
		sdao = new SegmentDAO();
		sdao.deleteAll();
		
		tdao = new TunnelDAO();
		tdao.deleteAll();
		
		ddao = new DC2DCCommunicationDAO();
		ddao.deleteAll();
	}

	@After
	public void dropDb() {
		logger.info("Deleting all entries from Link, CostFunction, Segment, "
				+ "Tunnel, AS, Cloud and DC2DCCommunication tables.");
		asdao.deleteAll();
		
		cldao.deleteAll();
		
		ldao.deleteAll();
		
		cdao.deleteAll();
		
		sdao.deleteAll();
		
		tdao.deleteAll();

		ddao.deleteAll();
	}

	@Test
	public void testFunctions() throws Exception {
		logger.info("Testing simple and complex TunnelDAO functions");
		List<Tunnel> list = tdao.findAll();
		assertEquals(list.size(), 0);
		
		Link l = new Link();
		l.setLinkID(new SimpleLinkID("link", "ote"));
		l.setPhysicalInterfaceName("name");
		NetworkAddressIPv4 address = new NetworkAddressIPv4();
		address.setPrefix("1.2.3.4");
		address.setPrefixLength(2);
		l.setAddress(address);
		l.setVlan(3);		
		CostFunction c = new CostFunction("cost", "subtype", "bw", "cost", null);
		List<Segment> segments = new ArrayList<Segment>();
		segments.add(new Segment(242, 4242, (float)0.5, (float)0.6));
		c.setSegments(segments);
		l.setCostFunction(c);
		ldao.insert(l);

		Tunnel t = new Tunnel();
        EndAddressPairTunnelID tunnelID = new EndAddressPairTunnelID();
        tunnelID.setTunnelName("tunnel1");
        tunnelID.setLocalTunnelEndAddress(new NetworkAddressIPv4("10.0.1.0", 24));
        tunnelID.setRemoteTunnelEndAddress(new NetworkAddressIPv4("20.0.2.0", 24));
		t.setTunnelID(tunnelID);
		t.setPhysicalLocalInterfaceName("p");
		t.setInboundInterfaceCounterOID("1.2.3.4.4.5.6");
		t.setOutboundInterfaceCounterOID("1.3.6.7");
        t.setOfSwitchPortNumber(5678);
		t.setLink(l);
		
		tdao.insert(t);
		list = tdao.findAll();
		assertEquals(list.size(), 1);
		
		t = tdao.findById(tunnelID);
		assertNotNull(t);
        assertEquals(t.getTunnelID().getLocalTunnelEndAddress().getPrefix(), "10.0.1.0");
        assertEquals(t.getTunnelID().getRemoteTunnelEndAddress().getPrefix(), "20.0.2.0");
        assertEquals(t.getOfSwitchPortNumber(), 5678);
        assertEquals(t.getPhysicalLocalInterfaceName(), "p");
		assertEquals(((SimpleLinkID)t.getLink().getLinkID()).getLocalIspName(), "ote");
		
		t.setPhysicalLocalInterfaceName("physical-interface");
		t.setOutboundInterfaceCounterOID("1.2.3.4.5.6.7");
        t.setOfSwitchPortNumber(56789);
		
		tdao.update(t);
		t = tdao.findById(tunnelID);
		assertNotNull(t);
        assertEquals(t.getTunnelID().getLocalTunnelEndAddress().getPrefix(), "10.0.1.0");
        assertEquals(t.getTunnelID().getRemoteTunnelEndAddress().getPrefix(), "20.0.2.0");
        assertEquals(t.getOfSwitchPortNumber(), 56789);
        assertEquals(t.getPhysicalLocalInterfaceName(), "physical-interface");
        assertEquals(t.getOutboundInterfaceCounterOID(), "1.2.3.4.5.6.7");
        assertEquals(((SimpleLinkID)t.getLink().getLinkID()).getLocalIspName(), "ote");
		
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
		cldao.insert(c1);
		
		CloudDC c2 = new CloudDC();
		c2.setCloudDcName("dropbox");
		c2.setAs(as2);
		cldao.insert(c2);
		assertEquals(cldao.findAll().size(), 2);

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
		
		tdao.updateByDC2DCCommunicationID(t.getTunnelID(), id);
		
		list = tdao.findAllByDC2DCCommunicationID(id);
		assertEquals(list.size(), 1);
		assertEquals(list.get(0).getLink().getPhysicalInterfaceName(), "name");
		assertEquals(list.get(0).getLink().getCostFunction().getSegments().size(), 1);
		
		tdao.deleteByDC2DCCommunicationID(id);
		list = tdao.findAll();
		assertEquals(list.size(), 0);

        t = new Tunnel();
        tunnelID = new EndAddressPairTunnelID();
        tunnelID.setTunnelName("tunnel2");
        tunnelID.setLocalTunnelEndAddress(new NetworkAddressIPv4("10.0.1.0", 24));
        tunnelID.setRemoteTunnelEndAddress(new NetworkAddressIPv4("20.0.2.0", 24));
        t.setTunnelID(tunnelID);
		t.setPhysicalLocalInterfaceName("p");
		t.setInboundInterfaceCounterOID("1.2.3.4.4.5.6");
		t.setOutboundInterfaceCounterOID("1.3.6.7");
		t.setLink(l);
		
		tdao.insert(t);
		
		tdao.deleteById(tunnelID);
		list = tdao.findAll();
		assertEquals(list.size(), 0);
	}
	
	

}
