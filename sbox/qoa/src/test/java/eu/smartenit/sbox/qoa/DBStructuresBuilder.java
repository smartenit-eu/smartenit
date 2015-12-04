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
package eu.smartenit.sbox.qoa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.smartenit.sbox.db.dao.ASDAO;
import eu.smartenit.sbox.db.dao.DC2DCCommunicationDAO;
import eu.smartenit.sbox.db.dao.TimeScheduleParametersDAO;
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.CloudDC;
import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.DC2DCCommunicationID;
import eu.smartenit.sbox.db.dto.Direction;
import eu.smartenit.sbox.db.dto.EndAddressPairTunnelID;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;
import eu.smartenit.sbox.db.dto.Tunnel;

/**
 * Helper class for creation of structures that can be returned by mocked DAOs:
 * {@link DC2DCCommunicationDAO}, {@link ASDAO} or {@link TimeScheduleParametersDAO}.
 * 
 * @author Lukasz Lopatowski
 * @version 3.1
 * 
 */
public class DBStructuresBuilder {

	public static TimeScheduleParameters tsp;
	public static List<AS> systems;
	public static List<DC2DCCommunication> communications;
	public static List<DC2DCCommunication> communicationsWithTunnelsOnBGRouters;
	
	static {
		tsp = new TimeScheduleParameters(null, 300, 30);
	}
	
	static { 
		BGRouter bgRouter11 = new BGRouter(new NetworkAddressIPv4("1.2.1.1", 32), "", null);
		BGRouter bgRouter12 = new BGRouter(new NetworkAddressIPv4("1.2.1.2", 32), "", null);
		BGRouter bgRouter21 = new BGRouter(new NetworkAddressIPv4("1.2.2.1", 32), "", null);
		
		DARouter daRouter1 = new DARouter(new NetworkAddressIPv4("1.1.1.1", 32), "", "");
		DARouter daRouter2 = new DARouter(new NetworkAddressIPv4("2.1.1.1", 32), "", "");
		DARouter daRouter3 = new DARouter(new NetworkAddressIPv4("3.1.1.1", 32), "", "");
		
		Link link111 = new Link(); link111.setLinkID(new SimpleLinkID("111", "isp1")); link111.setBgRouter(bgRouter11); 
		link111.setPhysicalInterfaceName("eth0");
		Link link112 = new Link(); link112.setLinkID(new SimpleLinkID("112", "isp1")); link112.setBgRouter(bgRouter11);
		link112.setPhysicalInterfaceName("eth1");
		Link link121 = new Link(); link121.setLinkID(new SimpleLinkID("121", "isp1")); link121.setBgRouter(bgRouter12);
		link121.setPhysicalInterfaceName("eth2");
		Link link122 = new Link(); link122.setLinkID(new SimpleLinkID("122", "isp1")); link122.setBgRouter(bgRouter12);
		link122.setPhysicalInterfaceName("eth3");
		Link link211 = new Link(); link211.setLinkID(new SimpleLinkID("211", "isp1")); link211.setBgRouter(bgRouter21);
		link211.setPhysicalInterfaceName("eth4");
			
		Tunnel tunnel1111 = new Tunnel(new EndAddressPairTunnelID("tunnel1111", 
				new NetworkAddressIPv4("10.1.1.1", 32), 
				new NetworkAddressIPv4("10.1.1.2", 32)),
				link111, null, null, null, 0);
		tunnel1111.setPhysicalLocalInterfaceName("eth0");
		tunnel1111.setLocalRouterAddress(new NetworkAddressIPv4("1.1.1.1", 32));
		Tunnel tunnel1121 = new Tunnel(new EndAddressPairTunnelID("tunnel1121", 
				new NetworkAddressIPv4("10.1.2.1", 32), 
				new NetworkAddressIPv4("10.1.2.2", 32)),
				link112, null, null, null, 0);
		tunnel1121.setPhysicalLocalInterfaceName("eth1");
		tunnel1121.setLocalRouterAddress(new NetworkAddressIPv4("1.1.1.1", 32));
		Tunnel tunnel1211 = new Tunnel(new EndAddressPairTunnelID("tunnel1211", 
				new NetworkAddressIPv4("10.2.1.1", 32), 
				new NetworkAddressIPv4("10.2.1.2", 32)),
				link121, null, null, null, 0);
		tunnel1211.setPhysicalLocalInterfaceName("eth2");
		tunnel1211.setLocalRouterAddress(new NetworkAddressIPv4("2.1.1.1", 32));
		Tunnel tunnel1212 = new Tunnel(new EndAddressPairTunnelID("tunnel1212", 
				new NetworkAddressIPv4("10.2.1.2", 32), 
				new NetworkAddressIPv4("10.2.1.3", 32)),
				link121, null, null, null, 0);
		tunnel1212.setPhysicalLocalInterfaceName("eth3");
		tunnel1212.setLocalRouterAddress(new NetworkAddressIPv4("2.1.1.1", 32));
		Tunnel tunnel1221 = new Tunnel(new EndAddressPairTunnelID("tunnel1221", 
				new NetworkAddressIPv4("10.2.2.1", 32), 
				new NetworkAddressIPv4("10.2.2.2", 32)),
				link122, null, null, null, 0);
		tunnel1221.setPhysicalLocalInterfaceName("eth4");
		tunnel1221.setLocalRouterAddress(new NetworkAddressIPv4("2.1.1.1", 32));
		Tunnel tunnel1222 = new Tunnel(new EndAddressPairTunnelID("tunnel1222", 
				new NetworkAddressIPv4("10.2.2.2", 32), 
				new NetworkAddressIPv4("10.2.2.3", 32)),
				link122, null, null, null, 0);
		tunnel1222.setPhysicalLocalInterfaceName("eth5");
		tunnel1222.setLocalRouterAddress(new NetworkAddressIPv4("2.1.1.1", 32));
		Tunnel tunnel1223 = new Tunnel(new EndAddressPairTunnelID("tunnel1223", 
				new NetworkAddressIPv4("10.2.2.4", 32), 
				new NetworkAddressIPv4("10.2.2.5", 32)),
				link122, null, null, null, 0);
		tunnel1223.setPhysicalLocalInterfaceName("eth6");
		tunnel1223.setLocalRouterAddress(new NetworkAddressIPv4("2.1.1.1", 32));
		Tunnel tunnel2111 = new Tunnel(new EndAddressPairTunnelID("tunnel2111", 
				new NetworkAddressIPv4("10.1.1.4", 32), 
				new NetworkAddressIPv4("10.1.1.5", 32)),
				link211, null, null, null, 0);
		tunnel2111.setPhysicalLocalInterfaceName("eth7");
		tunnel2111.setLocalRouterAddress(new NetworkAddressIPv4("3.1.1.1", 32));
		
		link111.setTraversingTunnels(Arrays.asList(tunnel1111));
		link112.setTraversingTunnels(Arrays.asList(tunnel1121));
		link121.setTraversingTunnels(Arrays.asList(tunnel1211, tunnel1212));
		link122.setTraversingTunnels(Arrays.asList(tunnel1221, tunnel1222, tunnel1223));
		link211.setTraversingTunnels(Arrays.asList(tunnel2111));
		
		bgRouter11.setInterDomainLinks(Arrays.asList(link111, link112));
		bgRouter12.setInterDomainLinks(Arrays.asList(link121, link122));
		bgRouter21.setInterDomainLinks(Arrays.asList(link211));
		
		AS as1 = new AS(); as1.setLocal(true); as1.setAsNumber(1); as1.setBgRouters(Arrays.asList(bgRouter11, bgRouter12));
		AS as2 = new AS(); as2.setLocal(true); as2.setAsNumber(2); as2.setBgRouters(Arrays.asList(bgRouter21));
		
		CloudDC cloudLocal11 = new CloudDC("cloudLocal11", as1, daRouter1, null, null);
		CloudDC cloudLocal12 = new CloudDC("cloudLocal12", as1, daRouter2, null, null);
		CloudDC cloudLocal21 = new CloudDC("cloudLocal21", as2, daRouter3, null, null);
		as1.setLocalClouds(Arrays.asList(cloudLocal11, cloudLocal12));
		as2.setLocalClouds(Arrays.asList(cloudLocal21));
		
		DC2DCCommunicationID id1 = new DC2DCCommunicationID(1, "id1", as1.getAsNumber(), cloudLocal11.getCloudDcName(), 200, "remoteCloud200");
		DC2DCCommunicationID id2 = new DC2DCCommunicationID(2, "id2", as1.getAsNumber(), cloudLocal12.getCloudDcName(), 300, "remoteCloud300");
		DC2DCCommunicationID id3 = new DC2DCCommunicationID(3, "id3", as2.getAsNumber(), cloudLocal21.getCloudDcName(), 400, "remoteCloud400");
		DC2DCCommunication communication1 = new DC2DCCommunication(
				id1, Direction.incomingTraffic, null, cloudLocal11, null, Arrays.asList(tunnel1111, tunnel1121));
		DC2DCCommunication communication2 = new DC2DCCommunication(
				id2, Direction.incomingTraffic, null, cloudLocal12, null, Arrays.asList(tunnel1211, tunnel1212, tunnel1221, tunnel1222, tunnel1223));
		DC2DCCommunication communication3 = new DC2DCCommunication(
				id3, Direction.incomingTraffic, null, cloudLocal21, null, Arrays.asList(tunnel2111));
		
		systems = new ArrayList<AS>(Arrays.asList(as1, as2));
		communications = new ArrayList<DC2DCCommunication>(Arrays.asList(communication1, communication2, communication3));
	}
	
	static { 
		BGRouter bgRouter11 = new BGRouter(new NetworkAddressIPv4("1.2.1.1", 32), "", null);
		BGRouter bgRouter12 = new BGRouter(new NetworkAddressIPv4("1.2.1.2", 32), "", null);
		
		Link link111 = new Link(); link111.setLinkID(new SimpleLinkID("111", "isp1")); link111.setBgRouter(bgRouter11); 
		link111.setPhysicalInterfaceName("eth0");
		Link link112 = new Link(); link112.setLinkID(new SimpleLinkID("112", "isp1")); link112.setBgRouter(bgRouter11);
		link112.setPhysicalInterfaceName("eth1");
		Link link121 = new Link(); link121.setLinkID(new SimpleLinkID("121", "isp1")); link121.setBgRouter(bgRouter12);
		link121.setPhysicalInterfaceName("eth2");
		Link link122 = new Link(); link122.setLinkID(new SimpleLinkID("122", "isp1")); link122.setBgRouter(bgRouter12);
		link122.setPhysicalInterfaceName("eth3");
			
		Tunnel tunnel1111 = new Tunnel(new EndAddressPairTunnelID("tunnel1111", 
				new NetworkAddressIPv4("10.1.1.1", 32), 
				new NetworkAddressIPv4("10.1.1.2", 32)),
				link111, null, null, null, 0);
		tunnel1111.setPhysicalLocalInterfaceName("eth0");
		tunnel1111.setLocalRouterAddress(new NetworkAddressIPv4("1.2.1.1", 32));
		Tunnel tunnel1121 = new Tunnel(new EndAddressPairTunnelID("tunnel1121", 
				new NetworkAddressIPv4("10.1.2.1", 32), 
				new NetworkAddressIPv4("10.1.2.2", 32)),
				link112, null, null, null, 0);
		tunnel1121.setPhysicalLocalInterfaceName("eth1");
		tunnel1121.setLocalRouterAddress(new NetworkAddressIPv4("1.2.1.1", 32));
		Tunnel tunnel1211 = new Tunnel(new EndAddressPairTunnelID("tunnel1211", 
				new NetworkAddressIPv4("10.2.1.1", 32), 
				new NetworkAddressIPv4("10.2.1.2", 32)),
				link121, null, null, null, 0);
		tunnel1211.setPhysicalLocalInterfaceName("eth2");
		tunnel1211.setLocalRouterAddress(new NetworkAddressIPv4("2.1.1.1", 32));
		Tunnel tunnel1212 = new Tunnel(new EndAddressPairTunnelID("tunnel1212", 
				new NetworkAddressIPv4("10.2.1.2", 32), 
				new NetworkAddressIPv4("10.2.1.3", 32)),
				link121, null, null, null, 0);
		tunnel1212.setPhysicalLocalInterfaceName("eth3");
		tunnel1212.setLocalRouterAddress(new NetworkAddressIPv4("2.1.1.1", 32));
		Tunnel tunnel1221 = new Tunnel(new EndAddressPairTunnelID("tunnel1221", 
				new NetworkAddressIPv4("10.2.2.1", 32), 
				new NetworkAddressIPv4("10.2.2.2", 32)),
				link122, null, null, null, 0);
		tunnel1221.setPhysicalLocalInterfaceName("eth4");
		tunnel1221.setLocalRouterAddress(new NetworkAddressIPv4("2.1.1.1", 32));
		Tunnel tunnel1222 = new Tunnel(new EndAddressPairTunnelID("tunnel1222", 
				new NetworkAddressIPv4("10.2.2.2", 32), 
				new NetworkAddressIPv4("10.2.2.3", 32)),
				link122, null, null, null, 0);
		tunnel1222.setPhysicalLocalInterfaceName("eth5");
		tunnel1222.setLocalRouterAddress(new NetworkAddressIPv4("2.1.1.1", 32));
		Tunnel tunnel1223 = new Tunnel(new EndAddressPairTunnelID("tunnel1223", 
				new NetworkAddressIPv4("10.2.2.4", 32), 
				new NetworkAddressIPv4("10.2.2.5", 32)),
				link122, null, null, null, 0);
		tunnel1223.setPhysicalLocalInterfaceName("eth6");
		tunnel1223.setLocalRouterAddress(new NetworkAddressIPv4("2.1.1.1", 32));
		
		link111.setTraversingTunnels(Arrays.asList(tunnel1111));
		link112.setTraversingTunnels(Arrays.asList(tunnel1121));
		link121.setTraversingTunnels(Arrays.asList(tunnel1211, tunnel1212));
		link122.setTraversingTunnels(Arrays.asList(tunnel1221, tunnel1222, tunnel1223));
		
		bgRouter11.setInterDomainLinks(Arrays.asList(link111, link112));
		bgRouter12.setInterDomainLinks(Arrays.asList(link121, link122));
		
		AS as1 = new AS(); as1.setLocal(true); as1.setAsNumber(1); as1.setBgRouters(Arrays.asList(bgRouter11, bgRouter12));
		
		DARouter daRouter1 = new DARouter(new NetworkAddressIPv4("1.1.1.1", 32), "", "");
		DARouter daRouter2 = new DARouter(new NetworkAddressIPv4("2.1.1.1", 32), "", "");
		
		CloudDC cloudLocal11 = new CloudDC("cloudLocal11", as1, daRouter1, null, null);
		CloudDC cloudLocal12 = new CloudDC("cloudLocal12", as1, daRouter2, null, null);
		as1.setLocalClouds(Arrays.asList(cloudLocal11, cloudLocal12));
		
		DC2DCCommunicationID id1 = new DC2DCCommunicationID(1, "id1", as1.getAsNumber(), cloudLocal11.getCloudDcName(), 200, "remoteCloud200");
		DC2DCCommunicationID id2 = new DC2DCCommunicationID(2, "id2", as1.getAsNumber(), cloudLocal12.getCloudDcName(), 300, "remoteCloud300");
		DC2DCCommunication communication1 = new DC2DCCommunication(
				id1, Direction.incomingTraffic, null, cloudLocal11, null, Arrays.asList(tunnel1111, tunnel1121));
		DC2DCCommunication communication2 = new DC2DCCommunication(
				id2, Direction.incomingTraffic, null, cloudLocal12, null, Arrays.asList(tunnel1211, tunnel1212, tunnel1221, tunnel1222, tunnel1223));
		
		communicationsWithTunnelsOnBGRouters = new ArrayList<DC2DCCommunication>(Arrays.asList(communication1, communication2));
	}
	
}
