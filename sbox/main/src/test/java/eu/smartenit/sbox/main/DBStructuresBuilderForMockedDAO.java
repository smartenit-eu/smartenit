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
package eu.smartenit.sbox.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.ChargingRule;
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
import eu.smartenit.sbox.db.dto.SystemControlParameters;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;
import eu.smartenit.sbox.db.dto.Tunnel;

/**
 * 
 * @author Lukasz Lopatowski
 * @version 3.1
 */
public class DBStructuresBuilderForMockedDAO {
	
	public static CostFunction receiverCostFunction1;
	public static CostFunction receiverCostFunction2;

	public static CostFunction receiverCostFunction3;
	public static CostFunction receiverCostFunction4;
	
	public static List<AS> receiverSystems1;
	public static List<AS> receiverSystems2;
	public static List<DC2DCCommunication> receiverCommunications1;
	public static List<DC2DCCommunication> receiverCommunications2;
	
	public static List<DC2DCCommunication> senderCommunications1DC;
	public static List<DC2DCCommunication> senderCommunications2DCs;
	
	public static TimeScheduleParameters tspForVolumeRule;
	public static TimeScheduleParameters tspFor95thPercentileRule;
	public static TimeScheduleParameters tspForLong95thPercentileRule;
	
	public static SystemControlParameters scpWithVolumeRule;
	public static SystemControlParameters scpWith95thPercentileRule;
	
	static {
		List<Segment> segments1 = new ArrayList<Segment>();
		segments1.add(new Segment(0, 10000, 5000, 1));
		segments1.add(new Segment(10000, 20000, 15000, 0));
		segments1.add(new Segment(20000, -1, 0, 0.75f));
		receiverCostFunction1 = new CostFunction();
		receiverCostFunction1.setSegments(segments1);

		List<Segment> segments2 = new ArrayList<Segment>();
		segments2.add(new Segment(0, 14000, 10000, 1));
		segments2.add(new Segment(14000, 30000, 20500, 0.25f));
		segments2.add(new Segment(30000, -1, -2000, 1));
		receiverCostFunction2 = new CostFunction();
		receiverCostFunction2.setSegments(segments2);
		
		List<Segment> segments3 = new ArrayList<Segment>();
		segments3.add(new Segment(0, 6000, 3000, 0.5f));
		segments3.add(new Segment(6000, 22000, 1500, 0.75f));
		segments3.add(new Segment(22000, -1, 7000, 0.5f));
		receiverCostFunction3 = new CostFunction();
		receiverCostFunction3.setSegments(segments3);
		
		List<Segment> segments4 = new ArrayList<Segment>();
		segments4.add(new Segment(0, 10000, 2000, 0));
		segments4.add(new Segment(10000, 20000, -18000, 2));
		segments4.add(new Segment(20000, -1, 22000, 0));
		receiverCostFunction4 = new CostFunction();
		receiverCostFunction4.setSegments(segments4);
	}

	static {
		tspForVolumeRule = new TimeScheduleParameters(new Date(new Date().getTime() + 6*1000), 12, 4);
		tspForVolumeRule.setCompensationPeriod(20);
		tspForVolumeRule.setReportPeriodDTM(4);
		tspForVolumeRule.setTol1(1.0);
		tspForVolumeRule.setTol2(1.0);
	
		tspFor95thPercentileRule = new TimeScheduleParameters(new Date(new Date().getTime() + 6*1000), 24, 4);
		tspFor95thPercentileRule.setCompensationPeriod(20);
		tspFor95thPercentileRule.setSamplingPeriod(12);
		tspFor95thPercentileRule.setReportPeriodEA(12);
		tspFor95thPercentileRule.setReportPeriodDTM(4);
		tspFor95thPercentileRule.setTol1(1.0);
		tspFor95thPercentileRule.setTol2(1.0);
		
		tspForLong95thPercentileRule = new TimeScheduleParameters(new Date(new Date().getTime() + 6*1000), 300, 1);
		tspForLong95thPercentileRule.setCompensationPeriod(9);
		tspForLong95thPercentileRule.setSamplingPeriod(3);
		tspForLong95thPercentileRule.setReportPeriodEA(3);
		tspForLong95thPercentileRule.setReportPeriodDTM(1);
		tspForLong95thPercentileRule.setTol1(1.0);
		tspForLong95thPercentileRule.setTol2(1.0);
	
		scpWithVolumeRule = new SystemControlParameters(ChargingRule.volume, null, 0.12);

		scpWith95thPercentileRule = new SystemControlParameters(ChargingRule.the95thPercentile, null, 0.12);
	}
	
	static {
		BGRouter bgRouter11 = new BGRouter(new NetworkAddressIPv4("1.2.1.1", 32), "smit", null);
		BGRouter bgRouter12 = new BGRouter(new NetworkAddressIPv4("1.2.1.2", 32), "smit", null);

		NetworkAddressIPv4 daRouterAddress = new NetworkAddressIPv4("1.1.1.1", 32); 
		DARouter daRouter1 = new DARouter(daRouterAddress, "smit", "00:00:00:00:00:00:00:01");
		
		Link link111 = new Link();
		link111.setLinkID(new SimpleLinkID("111", "isp1"));
		link111.setBgRouter(bgRouter11);
		link111.setPhysicalInterfaceName("eth0");
		link111.setCostFunction(receiverCostFunction1);
		link111.setTunnelEndPrefix(new NetworkAddressIPv4("10.1.1.0", 24));
		
		Link link121 = new Link();
		link121.setLinkID(new SimpleLinkID("121", "isp1"));
		link121.setBgRouter(bgRouter12);
		link121.setPhysicalInterfaceName("eth1");
		link121.setCostFunction(receiverCostFunction2);
		link121.setTunnelEndPrefix(new NetworkAddressIPv4("10.1.2.0", 24));
		
		Tunnel tunnel1111 = new Tunnel();
		tunnel1111.setTunnelID(new EndAddressPairTunnelID("1111", new NetworkAddressIPv4("10.1.1.1", 32), null));
		tunnel1111.setLink(link111);
		tunnel1111.setPhysicalLocalInterfaceName("eth0");
		tunnel1111.setLocalRouterAddress(daRouterAddress);
		
		Tunnel tunnel1211 = new Tunnel();
		tunnel1211.setTunnelID(new EndAddressPairTunnelID("1211", new NetworkAddressIPv4("10.1.2.1", 32), null));
		tunnel1211.setLink(link121);
		tunnel1211.setPhysicalLocalInterfaceName("eth1");
		tunnel1211.setLocalRouterAddress(daRouterAddress);
				
		link111.setTraversingTunnels(Arrays.asList(tunnel1111));
		link121.setTraversingTunnels(Arrays.asList(tunnel1211));

		bgRouter11.setInterDomainLinks(Arrays.asList(link111));
		bgRouter12.setInterDomainLinks(Arrays.asList(link121));

		AS as1 = new AS();
		as1.setLocal(true);
		as1.setAsNumber(10);
		as1.setBgRouters(Arrays.asList(bgRouter11, bgRouter12));

		AS as2 = new AS();
		as2.setLocal(false);
		as2.setAsNumber(20);
		as2.setSbox(new SBox(new NetworkAddressIPv4("127.0.0.1", 32)));
		
		CloudDC cloudLocal11 = new CloudDC("cloudLocal11", as1, daRouter1, null, null);
		as1.setLocalClouds(Arrays.asList(cloudLocal11));
		
		CloudDC cloudRemote21 = new CloudDC("remoteCloud21", as2, null, null, null);
		as2.setLocalClouds(Arrays.asList(cloudRemote21));

		DC2DCCommunicationID id1 = new DC2DCCommunicationID(1, "id1",
				as1.getAsNumber(), cloudLocal11.getCloudDcName(),
				as2.getAsNumber(), cloudRemote21.getCloudDcName());
		DC2DCCommunication communication1 = new DC2DCCommunication(id1,
				Direction.incomingTraffic, cloudRemote21, cloudLocal11, null,
				Arrays.asList(tunnel1111, tunnel1211));

		receiverSystems1 = new ArrayList<AS>(Arrays.asList(as1, as2));
		receiverCommunications1 = new ArrayList<DC2DCCommunication>(Arrays.asList(communication1));
	}
	
	static {
		BGRouter bgRouter31 = new BGRouter(new NetworkAddressIPv4("3.2.1.1", 32), "smit", null);
		BGRouter bgRouter32 = new BGRouter(new NetworkAddressIPv4("3.2.1.2", 32), "smit", null);

		NetworkAddressIPv4 daRouterAddress = new NetworkAddressIPv4("3.1.1.1", 32); 
		DARouter daRouter3 = new DARouter(daRouterAddress, "smit", "00:00:00:00:00:00:00:03");

		Link link311 = new Link();
		link311.setLinkID(new SimpleLinkID("311", "isp3"));
		link311.setBgRouter(bgRouter31);
		link311.setPhysicalInterfaceName("eth0");
		link311.setCostFunction(receiverCostFunction3);
		link311.setTunnelEndPrefix(new NetworkAddressIPv4("10.3.1.0", 24));
		
		Link link321 = new Link();
		link321.setLinkID(new SimpleLinkID("321", "isp3"));
		link321.setBgRouter(bgRouter32);
		link321.setPhysicalInterfaceName("eth1");
		link321.setCostFunction(receiverCostFunction4);
		link321.setTunnelEndPrefix(new NetworkAddressIPv4("10.3.2.0", 24));
		
		Tunnel tunnel3111 = new Tunnel();
		tunnel3111.setTunnelID(new EndAddressPairTunnelID("3111", new NetworkAddressIPv4("10.3.1.1", 32), null));
		tunnel3111.setLink(link311);
		tunnel3111.setPhysicalLocalInterfaceName("eth0");
		tunnel3111.setLocalRouterAddress(daRouterAddress);
		
		Tunnel tunnel3211 = new Tunnel();
		tunnel3211.setTunnelID(new EndAddressPairTunnelID("3211", new NetworkAddressIPv4("10.3.2.1", 32), null));
		tunnel3211.setLink(link321);
		tunnel3211.setPhysicalLocalInterfaceName("eth1");
		tunnel3211.setLocalRouterAddress(daRouterAddress);

		link311.setTraversingTunnels(Arrays.asList(tunnel3111));
		link321.setTraversingTunnels(Arrays.asList(tunnel3211));

		bgRouter31.setInterDomainLinks(Arrays.asList(link311));
		bgRouter32.setInterDomainLinks(Arrays.asList(link321));

		AS as3 = new AS();
		as3.setLocal(true);
		as3.setAsNumber(30);
		as3.setBgRouters(Arrays.asList(bgRouter31, bgRouter32));

		AS as2 = new AS();
		as2.setLocal(false);
		as2.setAsNumber(20);
		as2.setSbox(new SBox(new NetworkAddressIPv4("127.0.0.1", 32)));

		CloudDC cloudLocal31 = new CloudDC("cloudLocal31", as3, daRouter3, null, null);
		as3.setLocalClouds(Arrays.asList(cloudLocal31));
		
		CloudDC cloudRemote21 = new CloudDC("remoteCloud21", as2, null, null, null);
		as2.setLocalClouds(Arrays.asList(cloudRemote21));

		DC2DCCommunicationID id2 = new DC2DCCommunicationID(2, "id2",
				as3.getAsNumber(), cloudLocal31.getCloudDcName(),
				as2.getAsNumber(), cloudRemote21.getCloudDcName());
		DC2DCCommunication communication2 = new DC2DCCommunication(id2,
				Direction.incomingTraffic, cloudRemote21, cloudLocal31, null,
				Arrays.asList(tunnel3111, tunnel3211));

		receiverSystems2 = new ArrayList<AS>(Arrays.asList(as3, as2));
		receiverCommunications2 = new ArrayList<DC2DCCommunication>(Arrays.asList(communication2));
	}
	
	static { 
		SDNController controller1 = new SDNController(new NetworkAddressIPv4("2.1.1.1", 32), null, new NetworkAddressIPv4("2.1.1.1", 32), 30, null, 40);
		DARouter daRouter1 = new DARouter(new NetworkAddressIPv4("2.1.1.2", 32), "testCommunity", "00:00:00:00:00:00:00:01");
		controller1.setDaRouters(Arrays.asList(daRouter1));
		
		AS localAS = new AS();
		localAS.setAsNumber(20);
		localAS.setLocal(true);
		CloudDC localCloud1 = new CloudDC("", localAS, daRouter1, controller1, null);

		AS remoteAS = new AS();
		remoteAS.setAsNumber(10);
		remoteAS.setLocal(false);
		CloudDC remoteCloud = new CloudDC("", remoteAS, null, null, Arrays.asList(new NetworkAddressIPv4("10.1.10.0", 26)));

		EndAddressPairTunnelID tunnelID11 = new EndAddressPairTunnelID("1111", new NetworkAddressIPv4("2.1.1.10", 32), new NetworkAddressIPv4("10.1.1.1", 32));
		EndAddressPairTunnelID tunnelID12 = new EndAddressPairTunnelID("1211", new NetworkAddressIPv4("2.1.1.10", 32), new NetworkAddressIPv4("10.1.2.1", 32));
		
		List<Tunnel> tunnels1 = new ArrayList<Tunnel>(Arrays.asList(
				new Tunnel(tunnelID11, null, null, null, null, 1),
				new Tunnel(tunnelID12, null, null, null, null, 2)));
		DC2DCCommunicationID id1 = new DC2DCCommunicationID(1, "", localAS.getAsNumber(), "", remoteAS.getAsNumber(), "");
		DC2DCCommunication communication1 = new DC2DCCommunication(id1, Direction.outcomingTraffic, remoteCloud, localCloud1, null, tunnels1);

		senderCommunications1DC = new ArrayList<DC2DCCommunication>(Arrays.asList(communication1));
	}
	
	static { 
		SDNController controller1 = new SDNController(new NetworkAddressIPv4("2.1.1.1", 32), null, new NetworkAddressIPv4("2.1.1.1", 32), 30, null, 40);
		DARouter daRouter1 = new DARouter(new NetworkAddressIPv4("2.1.1.2", 32), "testCommunity", "00:00:00:00:00:00:00:01");
		controller1.setDaRouters(Arrays.asList(daRouter1));
		
		AS localAS = new AS();
		localAS.setAsNumber(20);
		localAS.setLocal(true);
		CloudDC localCloud1 = new CloudDC("", localAS, daRouter1, controller1, null);

		AS remoteAS1 = new AS();
		remoteAS1.setAsNumber(10);
		remoteAS1.setLocal(false);
		CloudDC remoteCloud1 = new CloudDC("", remoteAS1, null, null, Arrays.asList(new NetworkAddressIPv4("10.1.10.0", 26)));

		AS remoteAS2 = new AS();
		remoteAS2.setAsNumber(30);
		remoteAS2.setLocal(false);
		CloudDC remoteCloud2 = new CloudDC("", remoteAS2, null, null, Arrays.asList(new NetworkAddressIPv4("10.3.10.0", 26), new NetworkAddressIPv4("10.3.11.0", 26)));
		
		EndAddressPairTunnelID tunnelID11 = new EndAddressPairTunnelID("1111", new NetworkAddressIPv4("2.1.1.10", 32), new NetworkAddressIPv4("10.1.1.1", 32));
		EndAddressPairTunnelID tunnelID12 = new EndAddressPairTunnelID("1211", new NetworkAddressIPv4("2.1.1.11", 32), new NetworkAddressIPv4("10.1.2.1", 32));
		
		EndAddressPairTunnelID tunnelID13 = new EndAddressPairTunnelID("1311", new NetworkAddressIPv4("2.1.1.12", 32), new NetworkAddressIPv4("10.3.1.1", 32));
		EndAddressPairTunnelID tunnelID14 = new EndAddressPairTunnelID("1411", new NetworkAddressIPv4("2.1.1.13", 32), new NetworkAddressIPv4("10.3.2.1", 32));
		
		List<Tunnel> tunnels1 = new ArrayList<Tunnel>(Arrays.asList(
				new Tunnel(tunnelID11, null, null, null, null, 1),
				new Tunnel(tunnelID12, null, null, null, null, 2)));
		List<Tunnel> tunnels2 = new ArrayList<Tunnel>(Arrays.asList(
				new Tunnel(tunnelID13, null, null, null, null, 3),
				new Tunnel(tunnelID14, null, null, null, null, 4)));
		DC2DCCommunicationID id1 = new DC2DCCommunicationID(1, "", localAS.getAsNumber(), "", remoteAS1.getAsNumber(), "");
		DC2DCCommunication communication1 = new DC2DCCommunication(id1, Direction.outcomingTraffic, remoteCloud1, localCloud1, null, tunnels1);
		DC2DCCommunicationID id2 = new DC2DCCommunicationID(2, "", localAS.getAsNumber(), "", remoteAS2.getAsNumber(), "");
		DC2DCCommunication communication2 = new DC2DCCommunication(id2, Direction.outcomingTraffic, remoteCloud2, localCloud1, null, tunnels2);
		
		senderCommunications2DCs = new ArrayList<DC2DCCommunication>(Arrays.asList(communication1, communication2));
	}

}
