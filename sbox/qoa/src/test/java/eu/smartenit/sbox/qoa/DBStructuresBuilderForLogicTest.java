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
package eu.smartenit.sbox.qoa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.smartenit.sbox.db.dao.ASDAO;
import eu.smartenit.sbox.db.dao.DC2DCCommunicationDAO;
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
import eu.smartenit.sbox.db.dto.Tunnel;

/**
 * Helper class for creation of structures that can be returned by mocked DAOs:
 * {@link DC2DCCommunicationDAO} and {@link ASDAO}.
 * 
 * @author Lukasz Lopatowski
 * @author Jakub Gutkowski
 * @version 1.2
 * 
 */
public class DBStructuresBuilderForLogicTest {

	public static List<AS> systems;
	public static List<DC2DCCommunication> communications;

	static {
		BGRouter bgRouter11 = new BGRouter(
				new NetworkAddressIPv4("1.2.1.1", 32), "smit", null);
		BGRouter bgRouter12 = new BGRouter(
				new NetworkAddressIPv4("1.2.1.2", 32), "smit", null);

		Link link111 = new Link();
		link111.setLinkID(new SimpleLinkID("111", "isp1"));
		link111.setBgRouter(bgRouter11);
		link111.setPhysicalInterfaceName("eth0");
		Link link121 = new Link();
		link121.setLinkID(new SimpleLinkID("121", "isp1"));
		link121.setBgRouter(bgRouter12);
		link121.setPhysicalInterfaceName("eth1");

		Tunnel tunnel1111 =	new Tunnel(new EndAddressPairTunnelID("tunnel1111", 
				new NetworkAddressIPv4("10.1.1.1", 32), 
				new NetworkAddressIPv4("10.1.1.2", 32)),
				link111, null, null, null, 0);
		tunnel1111.setPhysicalLocalInterfaceName("eth0");
		Tunnel tunnel1211 = new Tunnel(new EndAddressPairTunnelID("tunnel1211", 
				new NetworkAddressIPv4("10.2.1.1", 32), 
				new NetworkAddressIPv4("10.2.1.2", 32)),
				link121, null, null, null, 0);
		tunnel1211.setPhysicalLocalInterfaceName("eth1");

		link111.setTraversingTunnels(Arrays.asList(tunnel1111));
		link121.setTraversingTunnels(Arrays.asList(tunnel1211));

		bgRouter11.setInterDomainLinks(Arrays.asList(link111));
		bgRouter12.setInterDomainLinks(Arrays.asList(link121));

		AS as1 = new AS();
		as1.setLocal(true);
		as1.setAsNumber(1);
		as1.setBgRouters(Arrays.asList(bgRouter11, bgRouter12));

		DARouter daRouter1 = new DARouter(
				new NetworkAddressIPv4("1.1.1.1", 32), "smit", "00:00:00:00:00:00:00:01");

		CloudDC cloudLocal11 = new CloudDC("cloudLocal11", as1, daRouter1, null, null);
		as1.setLocalClouds(Arrays.asList(cloudLocal11));

		DC2DCCommunicationID id1 = new DC2DCCommunicationID(1, "id1",
				as1.getAsNumber(), cloudLocal11.getCloudDcName(), 200,
				"remoteCloud200");
		DC2DCCommunication communication1 = new DC2DCCommunication(id1,
				Direction.incomingTraffic, null, cloudLocal11, null,
				Arrays.asList(tunnel1111, tunnel1211));

		systems = new ArrayList<AS>(Arrays.asList(as1));
		communications = new ArrayList<DC2DCCommunication>(
				Arrays.asList(communication1));
	}
}
