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
package eu.smartenit.unada.om;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.tomp2p.dht.PeerDHT;

import org.junit.After;
import org.junit.Before;

import eu.smartenit.unada.db.dao.impl.UNaDaConfigurationDAO;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Location;
import eu.smartenit.unada.db.dto.UNaDaConfiguration;
import eu.smartenit.unada.db.dto.UnadaInfo;

public class OverlayBaseTest {

	private static int ipAdressCounter = 10; 
	private static Random rnd = new Random(23L);

	PeerDHT[] network;
	OverlayManager om1;
	OverlayManager om2;



	String om1ID = "TestManager1";
	String om2ID = "TestManager2";

	long contentID1 = 1234567890L;
	long contentID2 = 10987654321L;

	@Before
	public void setUp() throws Exception{
		network = OverlayTestUtils.createAndAttachPeersDHT(50, 5001);
		OverlayTestUtils.bootstrap(network);

		fakeUnadaConfig();


		om1 = new OverlayManager("TestManager1");
		om1.joinOverlay(network[0].peerAddress().inetAddress(), network[0].peerAddress().udpPort());

		om2 = new OverlayManager("TestManager2");
		om2.setPort(6003);
		om2.joinOverlay(network[42].peerAddress().inetAddress(), network[42].peerAddress().udpPort());
	}

	@After
	public void shutDown() throws InterruptedException{
		om1.shutDown();
		om2.shutDown();
		for(PeerDHT p : network){
			p.shutdown();
		}
		Thread.sleep(1000);
	}

	public static UnadaInfo getRandomUnadaInfo() throws UnknownHostException{
		UnadaInfo info = new UnadaInfo();
		int id = rnd.nextInt();
		info.setUnadaID(id+"");
		info.setUnadaAddress("127.0.0."+ipAdressCounter);
		info.setUnadaPort(rnd.nextInt(65000));
		info.setLatitude(rnd.nextLong());
		info.setLongitude(rnd.nextLong());
		ipAdressCounter++;
		return info;
	}

	public static void fakeUnadaConfig(){
		UNaDaConfigurationDAO configurationDAO = mock(UNaDaConfigurationDAO.class);
		Location loc =new Location();
		loc.setLatitude(47.987);
		loc.setLongitude(7.345);
		UNaDaConfiguration conf = new UNaDaConfiguration();
		conf.setLocation(loc);
		List<UNaDaConfiguration> list = new LinkedList<>();
		list.add(conf);
		when(configurationDAO.findAll()).thenReturn(list);
		when(configurationDAO.findLast()).thenReturn(conf);
		DAOFactory.setuNaDaConfigurationDAO(configurationDAO);
	}
}
