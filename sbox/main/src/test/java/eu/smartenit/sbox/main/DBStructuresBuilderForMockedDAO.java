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
package eu.smartenit.sbox.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.CloudDC;
import eu.smartenit.sbox.db.dto.CostFunction;
import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.DC2DCCommunicationID;
import eu.smartenit.sbox.db.dto.Direction;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SBox;
import eu.smartenit.sbox.db.dto.SDNController;
import eu.smartenit.sbox.db.dto.Segment;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.SimpleTunnelID;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;
import eu.smartenit.sbox.db.dto.Tunnel;

public class DBStructuresBuilderForMockedDAO {
	
	public static CostFunction receiverCostFunction1;
	public static CostFunction receiverCostFunction2;

	public static TimeScheduleParameters receiverTsp;
	
	public static List<AS> receiverSystems;
	public static List<DC2DCCommunication> receiverCommunications;
	
	public static List<DC2DCCommunication> senderCommunications;

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
	}

	static {
		receiverTsp = new TimeScheduleParameters(new Date(new Date().getTime() + 6*1000), 12, 4);
		receiverTsp.setTol1(1.0);
		receiverTsp.setTol2(1.0);
	}
	
	static {
		BGRouter bgRouter11 = new BGRouter(new NetworkAddressIPv4("1.2.1.1", 32), "smit", null);
		BGRouter bgRouter12 = new BGRouter(new NetworkAddressIPv4("1.2.1.2", 32), "smit", null);

		Link link111 = new Link();
		link111.setLinkID(new SimpleLinkID("111", "isp1"));
		link111.setBgRouter(bgRouter11);
		link111.setPhysicalInterfaceName("eth0");
		link111.setCostFunction(receiverCostFunction1);
		Link link121 = new Link();
		link121.setLinkID(new SimpleLinkID("121", "isp1"));
		link121.setBgRouter(bgRouter12);
		link121.setPhysicalInterfaceName("eth1");
		link121.setCostFunction(receiverCostFunction2);

		Tunnel tunnel1111 = new Tunnel(new SimpleTunnelID("tunnel1111", 1111),
				link111, null, null, null, null, null);
		tunnel1111.setPhysicalLocalInterfaceName("eth0");
		Tunnel tunnel1211 = new Tunnel(new SimpleTunnelID("tunnel1211", 1211),
				link121, null, null, null, null, null);
		tunnel1211.setPhysicalLocalInterfaceName("eth1");

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
		
		DARouter daRouter1 = new DARouter(new NetworkAddressIPv4("1.1.1.1", 32), "smit");

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

		receiverSystems = new ArrayList<AS>(Arrays.asList(as1, as2));
		receiverCommunications = new ArrayList<DC2DCCommunication>(Arrays.asList(communication1));
	}
	
	static { 

		SDNController controller1 = new SDNController(null, null, new NetworkAddressIPv4("2.1.1.1", 32), 30, null, 40);

		AS localAS = new AS();
		localAS.setAsNumber(20);
		localAS.setLocal(true);
		CloudDC localCloud1 = new CloudDC("", localAS, null, controller1, null);

		AS remoteAS = new AS();
		remoteAS.setAsNumber(10);
		remoteAS.setLocal(false);
		CloudDC remoteCloud = new CloudDC("", remoteAS, null, null, null);

		DC2DCCommunicationID id1 = new DC2DCCommunicationID(1, "", localAS.getAsNumber(), "", remoteAS.getAsNumber(), "");
		DC2DCCommunication communication1 = new DC2DCCommunication(id1, Direction.outcomingTraffic, remoteCloud, localCloud1, null, null);

		senderCommunications = new ArrayList<DC2DCCommunication>(Arrays.asList(communication1));
	}
}
