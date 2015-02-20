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
package eu.smartenit.sbox.ntm.dtm.receiver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.smartenit.sbox.db.dao.ASDAO;
import eu.smartenit.sbox.db.dao.DC2DCCommunicationDAO;
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.CloudDC;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.DC2DCCommunicationID;
import eu.smartenit.sbox.db.dto.Direction;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SBox;

/**
 * Helper class for creation of structures that can be returned by mocked DAOs:
 * {@link DC2DCCommunicationDAO} and/or {@link ASDAO}.
 * 
 * @author Lukasz Lopatowski
 * @version 3.0
 * 
 */
class DBStructuresBuilder {

	public static List<DC2DCCommunication> communications;
	public static List<AS> systems;
	
	static {
		SBox sboxLocal1 = new SBox(new NetworkAddressIPv4("1.1.1.1", 32));
		AS asLocal1 = new AS(1, true, null, null, sboxLocal1, null);
		SBox sboxLocal2 = new SBox(new NetworkAddressIPv4("1.1.1.2", 32));
		AS asLocal2 = new AS(2, true, null, null, sboxLocal2, null);
	
		SBox sboxRemote1 = new SBox(new NetworkAddressIPv4("10.1.1.1", 32));
		AS asRemote1 = new AS(3, false, null, null, sboxRemote1, null);
		SBox sboxRemote2 = new SBox(new NetworkAddressIPv4("10.1.1.2", 32));
		AS asRemote2 = new AS(4, false, null, null, sboxRemote2, null);
	
		CloudDC cloudLocal1 = new CloudDC("local1", asLocal1, null, null, null);
		CloudDC cloudLocal11 = new CloudDC("local11", asLocal1, null, null, null);
		CloudDC cloudLocal2 = new CloudDC("local2", asLocal2, null, null, null);
		CloudDC cloudRemote1 = new CloudDC("remote1", asRemote1, null, null, null);
		CloudDC cloudRemote2 = new CloudDC("remote2", asRemote2, null, null, null);
	
		asLocal1.setLocalClouds(Arrays.asList(cloudLocal1, cloudLocal11));
		asLocal2.setLocalClouds(Arrays.asList(cloudLocal2));
		asRemote1.setLocalClouds(Arrays.asList(cloudRemote1));
		asRemote2.setLocalClouds(Arrays.asList(cloudRemote2));
		
		DC2DCCommunicationID communnicationID1 = new DC2DCCommunicationID(1, "local1-remote1", 
				asLocal1.getAsNumber(), cloudLocal1.getCloudDcName(), 
				asRemote1.getAsNumber(), asRemote1.getLocalClouds().get(0).getCloudDcName());
		DC2DCCommunicationID communnicationID2 = new DC2DCCommunicationID(2, "local1-remote2", 
				asLocal1.getAsNumber(), cloudLocal11.getCloudDcName(),
				asRemote2.getAsNumber(), asRemote2.getLocalClouds().get(0).getCloudDcName());
		DC2DCCommunicationID communnicationID3 = new DC2DCCommunicationID(3, "local2-remote2", 
				asLocal2.getAsNumber(), asLocal2.getLocalClouds().get(0).getCloudDcName(), 
				asRemote2.getAsNumber(), asRemote2.getLocalClouds().get(0).getCloudDcName());
		
		DC2DCCommunication communication1 = new DC2DCCommunication(communnicationID1, Direction.incomingTraffic, 
				cloudRemote1, cloudLocal1, null, null);
		DC2DCCommunication communication2 = new DC2DCCommunication(communnicationID2, Direction.incomingTraffic, 
				cloudRemote2, cloudLocal11, null, null);
		DC2DCCommunication communication3 = new DC2DCCommunication(communnicationID3, Direction.incomingTraffic, 
				cloudRemote2, asLocal2.getLocalClouds().get(0), null, null);
		
		systems = new ArrayList<AS>(Arrays.asList(asLocal1, asLocal2, asRemote1, asRemote2));
		communications = new ArrayList<DC2DCCommunication>(Arrays.asList(communication1, communication2, communication3));
	}
	
}
