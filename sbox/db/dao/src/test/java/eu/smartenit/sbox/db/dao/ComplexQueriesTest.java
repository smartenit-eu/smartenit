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
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dao.util.Tables;
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.CloudDC;
import eu.smartenit.sbox.db.dto.CostFunction;
import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.DC2DCCommunicationID;
import eu.smartenit.sbox.db.dto.Direction;
import eu.smartenit.sbox.db.dto.EndAddressPairTunnelID;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SBox;
import eu.smartenit.sbox.db.dto.SDNController;
import eu.smartenit.sbox.db.dto.Segment;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.Tunnel;

public class ComplexQueriesTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ComplexQueriesTest.class);

	private static ASDAO asdao = new ASDAO();

	private static BGRouterDAO bgdao = new BGRouterDAO();

	private static CloudDCDAO cldao = new CloudDCDAO();

	private static DARouterDAO dadao = new DARouterDAO();

	private static DC2DCCommunicationDAO dcdao = new DC2DCCommunicationDAO();

	private static LinkDAO ldao = new LinkDAO();

	private static SDNControllerDAO sdndao = new SDNControllerDAO();

	private static TunnelDAO tdao = new TunnelDAO();

	@BeforeClass
	public static void setup() throws Exception {
		new Tables().createAll();
		prepareDatabase();
	}

	@AfterClass
	public static void teardown() {
		new Tables().deleteAll();
	}

	@Test
	public void testFindAllAsesCloudsBgRoutersLinks() {
		asdao = new ASDAO();
		List<AS> asList = asdao.findAll();
		assertEquals(asList.size(), 2);

		long before = System.currentTimeMillis();
		asList = asdao.findAllAsesCloudsBgRoutersLinks();
		long after = System.currentTimeMillis();
		logger.info("AS full query required " + (after - before) + "ms.");
		assertEquals(asList.size(), 2);

		// checking first as
		AS as1 = asList.get(0);
		assertEquals(as1.getAsNumber(), 1);

		// checking sbox
		assertEquals(as1.getSbox().getManagementAddress().getPrefix(),
				"1.1.1.1");

		// checking routers
		assertEquals(as1.getBgRouters().size(), 2);

		// ** First bg router
		assertEquals(as1.getBgRouters().get(0).getSnmpCommunity(), "smartenit");
		assertEquals(as1.getBgRouters().get(0).getManagementAddress()
				.getPrefix(), "1.1.1.2");
		assertEquals(as1.getBgRouters().get(1).getSnmpCommunity(), "smartenit");

		// checking inter-domain links
		assertEquals(as1.getBgRouters().get(0).getInterDomainLinks().size(), 1);
		Link l = as1.getBgRouters().get(0).getInterDomainLinks().get(0);
		assertEquals(l.getAddress().getPrefix(), "1.1.1.6");
		assertEquals(l.getVlan(), 3);
		assertEquals(l.getAggregateLeakageFactor(), 0.1, 0.001);

		// checking bgrouter for first link
		assertEquals(l.getBgRouter().getManagementAddress().getPrefix(),
				"1.1.1.2");
		assertEquals(l.getBgRouter().getSnmpCommunity(), "smartenit");
		// checking tunnels for first link
		assertEquals(l.getTraversingTunnels().size(), 1);

		assertEquals(l.getTraversingTunnels().get(0).getTunnelID()
				.getLocalTunnelEndAddress().getPrefix(), "1.1.1.10");
		assertEquals(l.getTraversingTunnels().get(0).getTunnelID()
				.getRemoteTunnelEndAddress().getPrefix(), "2.2.2.20");
		assertEquals(l.getTraversingTunnels().get(0).getOfSwitchPortNumber(),
				5678);

		// ** Second bg router
		assertEquals(as1.getBgRouters().get(1).getSnmpCommunity(), "smartenit");
		assertEquals(as1.getBgRouters().get(1).getManagementAddress()
				.getPrefix(), "1.1.1.3");
		assertEquals(as1.getBgRouters().get(1).getSnmpCommunity(), "smartenit");

		assertEquals(as1.getBgRouters().get(1).getInterDomainLinks().size(), 1);
		l = as1.getBgRouters().get(1).getInterDomainLinks().get(0);
		assertEquals(l.getAddress().getPrefix(), "1.1.1.7");
		assertEquals(l.getVlan(), 23);
		assertEquals(l.getPolicerBandwidthLimitFactor(), 0.2, 0.001);

		// checking bgrouter for second link
		assertEquals(l.getBgRouter().getManagementAddress().getPrefix(),
				"1.1.1.3");
		assertEquals(l.getBgRouter().getSnmpCommunity(), "smartenit");
		// checking tunnels for second link
		assertEquals(l.getTraversingTunnels().size(), 1);

		assertEquals(l.getTraversingTunnels().get(0).getTunnelID()
				.getLocalTunnelEndAddress().getPrefix(), "1.1.1.11");
		assertEquals(l.getTraversingTunnels().get(0).getTunnelID()
				.getRemoteTunnelEndAddress().getPrefix(), "2.2.2.22");
		assertEquals(l.getTraversingTunnels().get(0).getOfSwitchPortNumber(),
				1211);

		// checking local clouds
		assertEquals(as1.getLocalClouds().size(), 1);
		assertEquals(as1.getLocalClouds().get(0).getCloudDcName(), "facebook");

		// checking second as
		AS as2 = asList.get(1);
		assertEquals(as2.getAsNumber(), 2);

		// checking sbox
		assertEquals(as2.getSbox().getManagementAddress().getPrefix(),
				"2.2.2.2");

		// checking routers
		assertEquals(as2.getBgRouters().size(), 0);

		// checking local clouds
		assertEquals(as2.getLocalClouds().size(), 2);
		assertEquals(as2.getLocalClouds().get(0).getCloudDcName(), "dropbox");
		assertEquals(as2.getLocalClouds().get(1).getCloudDcName(), "google");
	}

	@Test
	public void testFindAllDC2DCCommunicationsCloudsTunnels() {
		List<DC2DCCommunication> dc2dcList = dcdao.findAll();
		assertEquals(dc2dcList.size(), 2);

		long before = System.currentTimeMillis();
		dc2dcList = dcdao.findAllDC2DCCommunicationsCloudsTunnels();
		long after = System.currentTimeMillis();
		logger.info("DC2DC full query required " + (after - before) + "ms.");
		assertEquals(dc2dcList.size(), 2);

		// checking first dc2dc
		DC2DCCommunication dc1 = dc2dcList.get(0);
		// checking remote cloud
		assertEquals(dc1.getRemoteCloud().getCloudDcName(), "dropbox");
		assertEquals(dc1.getRemoteCloud().getAs().getAsNumber(), 2);
		assertEquals(dc1.getRemoteCloud().getDcNetworks().size(), 1);
		assertEquals(dc1.getRemoteCloud().getDcNetworks().size(), 1);
		assertEquals(dc1.getRemoteCloud().getDcNetworks().get(0).getPrefix(), "2.2.1.0");

		// checking local cloud
		assertEquals(dc1.getLocalCloud().getCloudDcName(), "facebook");
		// checking as
		AS as = dc1.getLocalCloud().getAs();
		assertEquals(as.getAsNumber(), 1);
		assertEquals(as.getSbox().getManagementAddress().getPrefix(), "1.1.1.1");
		assertEquals(as.getLocalClouds().size(), 1);
		assertEquals(as.getBgRouters().size(), 2);
		assertEquals(as.getBgRouters().get(0).getSnmpCommunity(), "smartenit");
		assertEquals(as.getBgRouters().get(0).getInterDomainLinks().size(), 1);
		assertEquals(as.getBgRouters().get(0).getInterDomainLinks().get(0)
				.getBgRouter().getManagementAddress().getPrefix(), "1.1.1.2");
		assertEquals(as.getBgRouters().get(0).getInterDomainLinks().get(0)
				.getTraversingTunnels().size(), 1);

		assertEquals(as.getBgRouters().get(0).getInterDomainLinks().get(0)
				.getTraversingTunnels().get(0).getTunnelID()
				.getLocalTunnelEndAddress().getPrefix(), "1.1.1.10");

		assertEquals(dc1.getLocalCloud().getSdnController().getRestPort(), 8080);
		assertEquals(dc1.getLocalCloud().getSdnController().getOpenflowHost()
				.getPrefix(), "1.1.1.5");
		assertEquals(dc1.getLocalCloud().getSdnController().getDaRouters().size(), 1);
		assertEquals(dc1.getLocalCloud().getSdnController().getDaRouters().get(0)
				.getOfSwitchDPID(), "abcdefg");
		assertEquals(dc1.getLocalCloud().getSdnController().getDaRouters()
				.get(0).getLocalDCOfSwitchPortNumbers().size(), 4);
		assertEquals(dc1.getLocalCloud().getSdnController().getDaRouters()
				.get(0).getLocalDCOfSwitchPortNumbers().get(1).intValue(), 63);
		assertEquals(dc1.getLocalCloud().getDaRouter().getSnmpCommunity(),
				"smartenit");
		assertEquals(dc1.getLocalCloud().getDaRouter().getOfSwitchDPID(), "abcdefg");
		assertEquals(dc1.getLocalCloud().getDaRouter()
				.getLocalDCOfSwitchPortNumbers().size(), 4);
		assertEquals(dc1.getLocalCloud().getDaRouter()
				.getLocalDCOfSwitchPortNumbers().get(3).intValue(), 100);

		// checking connecting tunnels
		assertEquals(dc1.getConnectingTunnels().size(), 1);

		assertEquals(dc1.getConnectingTunnels().get(0).getTunnelID()
				.getLocalTunnelEndAddress().getPrefix(), "1.1.1.10");
		assertEquals(dc1.getConnectingTunnels().get(0).getTunnelID()
				.getRemoteTunnelEndAddress().getPrefix(), "2.2.2.20");

		assertEquals(dc1.getConnectingTunnels().get(0)
				.getPhysicalLocalInterfaceName(), "eth1");
		assertEquals(dc1.getConnectingTunnels().get(0).getLink().getAddress()
				.getPrefix(), "1.1.1.6");
		assertEquals(dc1.getConnectingTunnels().get(0).getLink().getVlan(), 3);
		assertEquals(dc1.getConnectingTunnels().get(0).getLink()
				.getCostFunction().getSegments().size(), 2);
		assertEquals(dc1.getConnectingTunnels().get(0).getLink()
				.getTraversingTunnels().size(), 1);

		assertEquals(dc1.getConnectingTunnels().get(0).getLink()
				.getTraversingTunnels().get(0).getTunnelID()
				.getLocalTunnelEndAddress().getPrefix(), "1.1.1.10");
		assertEquals(dc1.getConnectingTunnels().get(0).getLink()
				.getTraversingTunnels().get(0).getTunnelID()
				.getRemoteTunnelEndAddress().getPrefix(), "2.2.2.20");

		assertEquals(dc1.getConnectingTunnels().get(0).getLink()
				.getTraversingTunnels().get(0).getPhysicalLocalInterfaceName(),
				"eth1");

		// checking second dc2dc
		DC2DCCommunication dc2 = dc2dcList.get(1);
		// checking remote cloud
		assertEquals(dc2.getRemoteCloud().getCloudDcName(), "google");
		assertEquals(dc2.getRemoteCloud().getAs().getAsNumber(), 2);
		assertEquals(dc2.getRemoteCloud().getDcNetworks().size(), 3);
		assertEquals(dc2.getRemoteCloud().getDcNetworks().get(2).getPrefix(), "3.3.3.0");

		// checking local cloud
		assertEquals(dc2.getLocalCloud().getCloudDcName(), "facebook");
		// checking as
		as = dc2.getLocalCloud().getAs();
		assertEquals(as.getAsNumber(), 1);
		assertEquals(as.getSbox().getManagementAddress().getPrefix(), "1.1.1.1");
		assertEquals(as.getLocalClouds().size(), 1);
		assertEquals(as.getBgRouters().size(), 2);
		assertEquals(as.getBgRouters().get(1).getSnmpCommunity(), "smartenit");
		assertEquals(as.getBgRouters().get(1).getInterDomainLinks().size(), 1);
		assertEquals(as.getBgRouters().get(1).getInterDomainLinks().get(0)
				.getBgRouter().getManagementAddress().getPrefix(), "1.1.1.3");
		assertEquals(as.getBgRouters().get(1).getInterDomainLinks().get(0)
				.getTraversingTunnels().size(), 1);

		assertEquals(as.getBgRouters().get(1).getInterDomainLinks().get(0)
				.getTraversingTunnels().get(0).getTunnelID()
				.getLocalTunnelEndAddress().getPrefix(), "1.1.1.11");
		assertEquals(dc2.getLocalCloud().getSdnController().getRestPort(), 8080);
		assertEquals(dc2.getLocalCloud().getDaRouter().getSnmpCommunity(),
				"smartenit");
		assertEquals(dc2.getLocalCloud().getSdnController().getDaRouters()
				.get(0).getLocalDCOfSwitchPortNumbers().size(), 4);
		assertEquals(dc2.getLocalCloud().getSdnController().getDaRouters()
				.get(0).getLocalDCOfSwitchPortNumbers().get(1).intValue(), 63);
		assertEquals(dc2.getLocalCloud().getDaRouter().getOfSwitchDPID(), "abcdefg");
		assertEquals(dc2.getLocalCloud().getDaRouter()
				.getLocalDCOfSwitchPortNumbers().size(), 4);
		assertEquals(dc2.getLocalCloud().getDaRouter()
				.getLocalDCOfSwitchPortNumbers().get(2).intValue(), 1);

		// checking connecting tunnels
		assertEquals(dc2.getConnectingTunnels().size(), 1);
		assertEquals(dc2.getConnectingTunnels().get(0).getTunnelID()
				.getLocalTunnelEndAddress().getPrefix(), "1.1.1.11");
		assertEquals(dc2.getConnectingTunnels().get(0).getTunnelID()
				.getRemoteTunnelEndAddress().getPrefix(), "2.2.2.22");

		assertEquals(dc2.getConnectingTunnels().get(0)
				.getPhysicalLocalInterfaceName(), "eth2");
		assertEquals(dc2.getConnectingTunnels().get(0).getLink().getAddress()
				.getPrefix(), "1.1.1.7");
		assertEquals(dc2.getConnectingTunnels().get(0).getLink().getVlan(), 23);
		assertEquals(dc2.getConnectingTunnels().get(0).getLink().getFilterInterfaceName(), "filter2");
		assertEquals(dc2.getConnectingTunnels().get(0).getLink()
				.getCostFunction().getSegments().size(), 2);
		assertEquals(dc2.getConnectingTunnels().get(0).getLink()
				.getTraversingTunnels().size(), 1);

		assertEquals(dc2.getConnectingTunnels().get(0).getLink()
				.getTraversingTunnels().get(0).getPhysicalLocalInterfaceName(),
				"eth2");
	}

	
	private static void prepareDatabase() throws Exception {
		// Local AS 1
		AS as1 = new AS();
		as1.setAsNumber(1);
		SBox sbox = new SBox(new NetworkAddressIPv4("1.1.1.1", 32));
		as1.setSbox(sbox);
		as1.setLocal(true);
		asdao.insert(as1);

		// Remote AS 2
		AS as2 = new AS();
		as2.setAsNumber(2);
		sbox = new SBox(new NetworkAddressIPv4("2.2.2.2", 32));
		as2.setSbox(sbox);
		as2.setLocal(false);
		asdao.insert(as2);

		// BG router bg1 1.1.1.2
		BGRouter bg1 = new BGRouter();
		bg1.setManagementAddress(new NetworkAddressIPv4("1.1.1.2", 32));
		bg1.setSnmpCommunity("smartenit");
		bgdao.insertByASNumber(bg1, as1.getAsNumber());

		// BG router bg2 1.1.1.3
		BGRouter bg2 = new BGRouter();
		bg2.setManagementAddress(new NetworkAddressIPv4("1.1.1.3", 32));
		bg2.setSnmpCommunity("smartenit");
		bgdao.insertByASNumber(bg2, as1.getAsNumber());

		// DA Router da 1.1.1.4
		DARouter da = new DARouter();
		da.setManagementAddress(new NetworkAddressIPv4("1.1.1.4", 0));
		da.setSnmpCommunity("smartenit");
		da.setOfSwitchDPID("abcdefg");
		da.setLocalDCOfSwitchPortNumbers(Arrays.asList(52, 63, 1, 100));
		dadao.insert(da);

		// SDN Controller
		SDNController sdn1 = new SDNController();
		sdn1.setManagementAddress(new NetworkAddressIPv4("1.1.1.5", 32));
		sdn1.setOpenflowHost(new NetworkAddressIPv4("1.1.1.5", 32));
		sdn1.setOpenflowPort(6633);
		sdn1.setRestHost(new NetworkAddressIPv4("1.1.1.5", 32));
		sdn1.setRestPort(8080);
		sdndao.insert(sdn1);

		// update DA router with SDN controller IP address
		dadao.updateBySDNControllerAddress(da.getManagementAddress()
				.getPrefix(), sdn1.getManagementAddress().getPrefix());

		// Insert link l1
		Link l1 = new Link();
		l1.setLinkID(new SimpleLinkID("link1", "ote"));
		l1.setPhysicalInterfaceName("eth0");
		l1.setAddress(new NetworkAddressIPv4("1.1.1.6", 32));
		l1.setVlan(3);
		l1.setTunnelEndPrefix(new NetworkAddressIPv4("1.1.1.0", 8));
		l1.setBgRouter(bg1);
		l1.setAggregateLeakageFactor(0.1);
		l1.setFilterInterfaceName("filter");
		l1.setPolicerBandwidthLimitFactor(0.1);
		CostFunction c = new CostFunction("cost", "subtype", "bw", "cost", null);
		List<Segment> segments = new ArrayList<Segment>();
		segments.add(new Segment(0, 2000, (float) 0.5, (float) 0.8));
		segments.add(new Segment(2000, 100000, (float) 0.7, (float) 4.7));
		c.setSegments(segments);
		l1.setCostFunction(c);
		ldao.insert(l1);

		// Insert link l2
		Link l2 = new Link();
		l2.setLinkID(new SimpleLinkID("link2", "ote"));
		l2.setPhysicalInterfaceName("eth1");
		l2.setAddress(new NetworkAddressIPv4("1.1.1.7", 32));
		l2.setVlan(23);
		l2.setTunnelEndPrefix(new NetworkAddressIPv4("1.1.2.0", 8));
		l2.setBgRouter(bg2);
		l2.setAggregateLeakageFactor(0.2);
		l2.setFilterInterfaceName("filter2");
		l2.setPolicerBandwidthLimitFactor(0.2);
		c = new CostFunction("cost", "subtype", "bw", "cost", null);
		segments = new ArrayList<Segment>();
		segments.add(new Segment(0, 1000, (float) 2.5, (float) 2.6));
		segments.add(new Segment(1000, 2000000, (float) 2.9, (float) 2.3));
		c.setSegments(segments);
		l2.setCostFunction(c);
		ldao.insert(l2);

		// Insert tunnel t1 ==> assign to l1
		Tunnel t1 = new Tunnel();
		EndAddressPairTunnelID tunnelID = new EndAddressPairTunnelID();
		tunnelID.setTunnelName("tunnel1");
		tunnelID.setLocalTunnelEndAddress(new NetworkAddressIPv4("1.1.1.10", 32));
		tunnelID.setRemoteTunnelEndAddress(new NetworkAddressIPv4("2.2.2.20",
				32));
		t1.setTunnelID(tunnelID);
		t1.setPhysicalLocalInterfaceName("eth1");
		t1.setInboundInterfaceCounterOID("1.2.3.4.4.5.6");
		t1.setOutboundInterfaceCounterOID("1.3.7");
		t1.setOfSwitchPortNumber(5678);
		t1.setLink(l1);
		tdao.insert(t1);

		// Insert tunnel t2 ==> assign to l2
		Tunnel t2 = new Tunnel();
		tunnelID = new EndAddressPairTunnelID();
		tunnelID.setTunnelName("tunnel2");
		tunnelID.setLocalTunnelEndAddress(new NetworkAddressIPv4("1.1.1.11", 32));
		tunnelID.setRemoteTunnelEndAddress(new NetworkAddressIPv4("2.2.2.22",
				32));
		t2.setTunnelID(tunnelID);
		t2.setPhysicalLocalInterfaceName("eth2");
		t2.setInboundInterfaceCounterOID("1.2.3.43");
		t2.setOutboundInterfaceCounterOID("1.3.64.7");
		t2.setOfSwitchPortNumber(1211);
		t2.setLink(l2);
		tdao.insert(t2);

		// Insert local cloud cl1
		CloudDC cl1 = new CloudDC();
		cl1.setCloudDcName("facebook");
		cl1.setAs(as1);
		cl1.setSdnController(sdn1);
		cl1.setDaRouter(da);
		cl1.setDcNetworks(Arrays.asList(new NetworkAddressIPv4("1.1.1.0", 24), 
				new NetworkAddressIPv4("1.1.2.0", 24)));
		cldao.insert(cl1);

		// Insert remote cloud cl2
		CloudDC cl2 = new CloudDC();
		cl2.setCloudDcName("dropbox");
		cl2.setAs(as2);
		cl2.setDcNetworks(Arrays.asList(new NetworkAddressIPv4("2.2.1.0", 24)));
		cldao.insert(cl2);

		// Insert remote cloud cl3
		CloudDC cl3 = new CloudDC();
		cl3.setCloudDcName("google");
		cl3.setAs(as2);
		cl3.setDcNetworks(Arrays.asList(new NetworkAddressIPv4("3.3.1.0", 24), 
				new NetworkAddressIPv4("3.3.2.0", 24), new NetworkAddressIPv4("3.3.3.0", 24)));
		cldao.insert(cl3);

		// Insert dc to dc communication dc1
		DC2DCCommunication dc1 = new DC2DCCommunication();
		DC2DCCommunicationID id = new DC2DCCommunicationID();
		id.setCommunicationNumber(1);
		id.setCommunicationSymbol("+");
		id.setLocalAsNumber(1);
		id.setLocalCloudDCName("facebook");
		id.setRemoteAsNumber(2);
		id.setRemoteCloudDCName("dropbox");
		dc1.setId(id);
		dc1.setTrafficDirection(Direction.incomingTraffic);
		dc1.setLocalCloud(cl1);
		dc1.setRemoteCloud(cl2);
		dcdao.insert(dc1);

		// Update tunnel t1 with dc1 id
		tdao.updateByDC2DCCommunicationID(t1.getTunnelID(), id);

		// Insert dc to dc communication dc1
		DC2DCCommunication dc2 = new DC2DCCommunication();
		DC2DCCommunicationID id2 = new DC2DCCommunicationID();
		id2.setCommunicationNumber(1);
		id2.setCommunicationSymbol("+");
		id2.setLocalAsNumber(1);
		id2.setLocalCloudDCName("facebook");
		id2.setRemoteAsNumber(2);
		id2.setRemoteCloudDCName("google");
		dc2.setId(id2);
		dc2.setTrafficDirection(Direction.incomingTraffic);
		dc2.setLocalCloud(cl1);
		dc2.setRemoteCloud(cl3);
		dcdao.insert(dc2);
		
		// Update tunnel t2 with dc2 id
		tdao.updateByDC2DCCommunicationID(t2.getTunnelID(), id2);
	}

}
