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
package eu.smartenit.sbox.ntm.dtm.sender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.smartenit.sbox.db.dao.DC2DCCommunicationDAO;
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.CloudDC;
import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.DC2DCCommunicationID;
import eu.smartenit.sbox.db.dto.Direction;
import eu.smartenit.sbox.db.dto.EndAddressPairTunnelID;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SDNController;
import eu.smartenit.sbox.db.dto.Tunnel;

/**
 * Helper class for creation of structures that can be returned by mocked
 * {@link DC2DCCommunicationDAO}.
 * 
 * @author Lukasz Lopatowski
 * @version 1.2
 * 
 */
class DBStructuresBuilder {

	public static List<DC2DCCommunication> communicationsOnTrafficSender;
	
	public static String sdnController1Address = "100.1.1.1";
	public static String sdnController2Address = "101.1.1.1";
	
	public static String remoteCloudDCNetwork11 = "1.1.10.0";
	public static String remoteCloudDCNetwork12 = "1.1.11.0";
	public static String remoteCloudDCNetwork21 = "1.2.10.0";
	public static String remoteCloudDCNetwork31 = "2.1.10.0";
	public static String remoteCloudDCNetwork32 = "2.1.11.0";
	
	public static String localDARDPID1 = "00:00:00:00:00:00:00:01";
	public static String localDARDPID2 = "00:00:00:00:00:00:00:02";
	public static String localDARDPID3 = "00:00:00:00:00:00:00:03";
	
	private static String localTEndDAR1 = "100.1.3.1";
	private static String localTEndDAR2 = "100.1.3.2";
	private static String localTEndDAR3 = "101.1.3.1";
	private static String remoteTEndAS11 = "1.1.3.1";
	private static String remoteTEndAS12 = "1.1.4.1";
	private static String remoteTEndAS21 = "2.1.3.1";
	private static String remoteTEndAS22 = "2.1.4.1";

	private static Tunnel tunnel1 = new Tunnel(new EndAddressPairTunnelID("100-1 to 1 (1)", address(localTEndDAR1), address(remoteTEndAS11)), null, null, null, null, 1);
	private static Tunnel tunnel2 = new Tunnel(new EndAddressPairTunnelID("100-1 to 1 (2)", address(localTEndDAR1), address(remoteTEndAS12)), null, null, null, null, 2);
	private static Tunnel tunnel3 = new Tunnel(new EndAddressPairTunnelID("100-1 to 2 (1)", address(localTEndDAR1), address(remoteTEndAS21)), null, null, null, null, 3);
	private static Tunnel tunnel4 = new Tunnel(new EndAddressPairTunnelID("100-1 to 2 (2)", address(localTEndDAR1), address(remoteTEndAS22)), null, null, null, null, 4);
	private static Tunnel tunnel5 = new Tunnel(new EndAddressPairTunnelID("100-2 to 1 (1)", address(localTEndDAR2), address(remoteTEndAS11)), null, null, null, null, 1);
	private static Tunnel tunnel6 = new Tunnel(new EndAddressPairTunnelID("100-2 to 1 (2)", address(localTEndDAR2), address(remoteTEndAS12)), null, null, null, null, 2);
	private static Tunnel tunnel7 = new Tunnel(new EndAddressPairTunnelID("100-2 to 2 (1)", address(localTEndDAR2), address(remoteTEndAS21)), null, null, null, null, 3);
	private static Tunnel tunnel8 = new Tunnel(new EndAddressPairTunnelID("100-2 to 2 (2)", address(localTEndDAR2), address(remoteTEndAS22)), null, null, null, null, 4);
	private static Tunnel tunnel9 = new Tunnel(new EndAddressPairTunnelID("101-1 to 2 (1)", address(localTEndDAR3), address(remoteTEndAS21)), null, null, null, null, 1);
	private static Tunnel tunnel10 = new Tunnel(new EndAddressPairTunnelID("101-1 to 2 (2)", address(localTEndDAR3), address(remoteTEndAS22)), null, null, null, null, 2);
	
	static { 
		AS remoteAS1 = new AS();
		remoteAS1.setAsNumber(1);
		remoteAS1.setLocal(false);
		CloudDC remoteCloud1 = new CloudDC("remoteCloud1", remoteAS1, null, null, Arrays.asList(new NetworkAddressIPv4(remoteCloudDCNetwork11, 28), new NetworkAddressIPv4(remoteCloudDCNetwork12, 28)));
		CloudDC remoteCloud2 = new CloudDC("remoteCloud2", remoteAS1, null, null, Arrays.asList(new NetworkAddressIPv4(remoteCloudDCNetwork21, 28)));
		
		AS remoteAS2 = new AS();
		remoteAS2.setAsNumber(2);
		remoteAS2.setLocal(false);
		CloudDC remoteCloud3 = new CloudDC("remoteCloud3", remoteAS2, null, null, Arrays.asList(new NetworkAddressIPv4(remoteCloudDCNetwork31, 28), new NetworkAddressIPv4(remoteCloudDCNetwork32, 28)));

		AS localAS100 = new AS();
		localAS100.setAsNumber(100);
		localAS100.setLocal(true);
		SDNController controller1 = new SDNController(new NetworkAddressIPv4(sdnController1Address, 32), null, new NetworkAddressIPv4(sdnController1Address, 32), 30, null, 40);
		DARouter daRouter1 = new DARouter(new NetworkAddressIPv4("100.1.2.1", 32), "testCommunity", localDARDPID1);
		DARouter daRouter2 = new DARouter(new NetworkAddressIPv4("100.1.2.2", 32), "testCommunity", localDARDPID2);
		controller1.setDaRouters(Arrays.asList(daRouter1, daRouter2));
		CloudDC localCloud1 = new CloudDC("localCloud1", localAS100, daRouter1, controller1, null);
		CloudDC localCloud2 = new CloudDC("localCloud2", localAS100, daRouter2, controller1, null);
		CloudDC localCloud3 = new CloudDC("localCloud3", localAS100, daRouter2, controller1, null);
		
		AS localAS101 = new AS();
		localAS101.setAsNumber(101);
		localAS101.setLocal(true);
		SDNController controller2 = new SDNController(new NetworkAddressIPv4(sdnController2Address, 32), null, new NetworkAddressIPv4(sdnController2Address, 32), 50, null, 60);
		DARouter daRouter3 = new DARouter(new NetworkAddressIPv4("101.1.2.1", 32), "testCommunity", localDARDPID3);
		daRouter3.setLocalDCOfSwitchPortNumbers(Arrays.asList(10, 11));
		controller2.setDaRouters(Arrays.asList(daRouter3));
		CloudDC localCloud4 = new CloudDC("localCloud4", localAS101, daRouter3, controller2, null);
		
		DC2DCCommunicationID id1 = new DC2DCCommunicationID(1, "localCloud1-remoteCloud1", 100, "localCloud1", 1, "remoteCloud1");
		List<Tunnel> tunnels1 = new ArrayList<>(Arrays.asList(tunnel1, tunnel2));
		DC2DCCommunication communication1 = new DC2DCCommunication(id1, Direction.outcomingTraffic, remoteCloud1, localCloud1, null, tunnels1);

		DC2DCCommunicationID id2 = new DC2DCCommunicationID(2, "localCloud1-remoteCloud3", 100, "localCloud1", 2, "remoteCloud3");
		List<Tunnel> tunnels2 = new ArrayList<>(Arrays.asList(tunnel3, tunnel4));
		DC2DCCommunication communication2 = new DC2DCCommunication(id2, Direction.outcomingTraffic, remoteCloud3, localCloud1, null, tunnels2);

		DC2DCCommunicationID id3 = new DC2DCCommunicationID(3, "localCloud2-remoteCloud3", 100, "localCloud2", 2, "remoteCloud3");
		List<Tunnel> tunnels3 = new ArrayList<>(Arrays.asList(tunnel7, tunnel8));
		DC2DCCommunication communication3 = new DC2DCCommunication(id3, Direction.outcomingTraffic, remoteCloud3, localCloud2, null, tunnels3);

		DC2DCCommunicationID id4 = new DC2DCCommunicationID(4, "localCloud3-remoteCloud1", 100, "localCloud3", 1, "remoteCloud1");
		List<Tunnel> tunnels4 = new ArrayList<>(Arrays.asList(tunnel5, tunnel6));
		DC2DCCommunication communication4 = new DC2DCCommunication(id4, Direction.outcomingTraffic, remoteCloud1, localCloud3, null, tunnels4);

		DC2DCCommunicationID id5 = new DC2DCCommunicationID(5, "localCloud3-remoteCloud2", 100, "localCloud3", 1, "remoteCloud2");
		List<Tunnel> tunnels5 = new ArrayList<>(Arrays.asList(tunnel5, tunnel6));
		DC2DCCommunication communication5 = new DC2DCCommunication(id5, Direction.outcomingTraffic, remoteCloud2, localCloud3, null, tunnels5);
		
		DC2DCCommunicationID id6 = new DC2DCCommunicationID(6, "localCloud3-remoteCloud3", 100, "localCloud3", 2, "remoteCloud3");
		List<Tunnel> tunnels6 = new ArrayList<>(Arrays.asList(tunnel7, tunnel8));
		DC2DCCommunication communication6 = new DC2DCCommunication(id6, Direction.outcomingTraffic, remoteCloud3, localCloud3, null, tunnels6);
		
		DC2DCCommunicationID id7 = new DC2DCCommunicationID(7, "localCloud4-remoteCloud3", 101, "localCloud4", 2, "remoteCloud3");
		List<Tunnel> tunnels7 = new ArrayList<>(Arrays.asList(tunnel9, tunnel10));
		DC2DCCommunication communication7 = new DC2DCCommunication(id7, Direction.outcomingTraffic, remoteCloud3, localCloud4, null, tunnels7);
		
		DC2DCCommunicationID id11 = new DC2DCCommunicationID(11, "remoteCloud3-localCloud4", 2, "remoteCloud3", 101, "localCloud4");
		DC2DCCommunication communication11 = new DC2DCCommunication(id11, Direction.incomingTraffic, remoteCloud3, localCloud4, null, null);

		DC2DCCommunicationID id12 = new DC2DCCommunicationID(12, "remoteCloud1-localCloud2", 1, "remoteCloud1", 100, "localCloud2");
		DC2DCCommunication communication12 = new DC2DCCommunication(id12, Direction.incomingTraffic, remoteCloud1, localCloud2, null, null);

		communicationsOnTrafficSender = new ArrayList<DC2DCCommunication>(
				Arrays.asList(communication1, communication2, communication3, communication4, communication5, communication6, communication7,
						communication11, communication12));
	}

	private static NetworkAddressIPv4 address(String address) {
		return new NetworkAddressIPv4(address, 32);
	}
	
}
