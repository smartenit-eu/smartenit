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

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.Link;

public class ComplexQueriesTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ComplexQueriesTest.class);

	private ASDAO asdao;

	private DC2DCCommunicationDAO dc2dcdao;

	@BeforeClass
	public static void importDb() {
		logger.info("Importing existing db schema and values.");
		DbConstants.DBI_URL = "jdbc:sqlite:src/test/resources/test.db";
	}

	@AfterClass
	public static void restoreDbConstant() {
		DbConstants.DBI_URL = "jdbc:sqlite:smartenit.db";

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
		assertEquals(as1.getAsNumber(), 2);

		// checking sbox
		assertEquals(as1.getSbox().getManagementAddress().getPrefix(),
				"2.2.2.2");

		// checking routers
		assertEquals(as1.getBgRouters().size(), 2);
		assertEquals(as1.getBgRouters().get(0).getSnmpCommunity(), "SNMP1");
		assertEquals(as1.getBgRouters().get(0).getManagementAddress()
				.getPrefix(), "2.2.2.3");
		assertEquals(as1.getBgRouters().get(1).getSnmpCommunity(), "SNMP1");

		// checking inter-domain links
		assertEquals(as1.getBgRouters().get(0).getInterDomainLinks().size(), 1);
		Link l = as1.getBgRouters().get(0).getInterDomainLinks().get(0);
		assertEquals(l.getAddress().getPrefix(), "2.2.2.7");
		assertEquals(l.getVlan(), 213);

		// checking bgrouter for link
		assertEquals(l.getBgRouter().getManagementAddress().getPrefix(),
				"2.2.2.3");
		assertEquals(l.getBgRouter().getSnmpCommunity(), "SNMP1");
		// checking tunnels for link
		assertEquals(l.getTraversingTunnels().size(), 1);
		assertEquals(l.getTraversingTunnels().get(0).getSourceEndAddress()
				.getPrefix(), "55.55.55.55");

		// checking local clouds
		assertEquals(as1.getLocalClouds().size(), 2);
		assertEquals(as1.getLocalClouds().get(0).getCloudDcName(), "facebook");
		assertEquals(as1.getLocalClouds().get(1).getCloudDcName(), "google+");

		AS as2 = asList.get(1);
		assertEquals(as2.getAsNumber(), 44);

		// checking sbox
		assertEquals(as2.getSbox().getManagementAddress().getPrefix(),
				"44.44.44.44");

		// checking routers
		assertEquals(as2.getBgRouters().size(), 0);

		// checking local clouds
		assertEquals(as2.getLocalClouds().size(), 1);
		assertEquals(as2.getLocalClouds().get(0).getCloudDcName(), "dropbox");
	}

	@Test
	public void testFindAllDC2DCCommunicationsCloudsTunnels() {
		dc2dcdao = new DC2DCCommunicationDAO();
		List<DC2DCCommunication> dc2dcList = dc2dcdao.findAll();
		assertEquals(dc2dcList.size(), 2);

		long before = System.currentTimeMillis();
		dc2dcList = dc2dcdao.findAllDC2DCCommunicationsCloudsTunnels();
		long after = System.currentTimeMillis();
		logger.info("DC2DC full query required " + (after - before) + "ms.");
		assertEquals(dc2dcList.size(), 2);

		// checking first dc2dc
		DC2DCCommunication dc1 = dc2dcList.get(0);
		// checking remote cloud
		assertEquals(dc1.getRemoteCloud().getCloudDcName(), "dropbox");
		assertEquals(dc1.getRemoteCloud().getAs().getAsNumber(), 44);

		// checking local cloud
		assertEquals(dc1.getLocalCloud().getCloudDcName(), "facebook");
		// checking as
		AS as = dc1.getLocalCloud().getAs();
		assertEquals(as.getAsNumber(), 2);
		assertEquals(as.getSbox().getManagementAddress().getPrefix(), "2.2.2.2");
		assertEquals(as.getLocalClouds().size(), 2);
		assertEquals(as.getBgRouters().size(), 2);
		assertEquals(as.getBgRouters().get(0).getSnmpCommunity(), "SNMP1");
		assertEquals(as.getBgRouters().get(0).getInterDomainLinks().size(), 1);
		assertEquals(as.getBgRouters().get(0).getInterDomainLinks().get(0)
				.getBgRouter().getManagementAddress().getPrefix(), "2.2.2.3");
		assertEquals(as.getBgRouters().get(0).getInterDomainLinks().get(0)
				.getTraversingTunnels().size(), 1);
		assertEquals(as.getBgRouters().get(0).getInterDomainLinks().get(0)
				.getTraversingTunnels().get(0).getSourceEndAddress()
				.getPrefix(), "55.55.55.55");
		assertEquals(dc1.getLocalCloud().getSdnController().getRestPort(), 8080);
		assertEquals(dc1.getLocalCloud().getDaRouter().getSnmpCommunity(),
				"SNMP2");
		// checking connecting tunnels
		assertEquals(dc1.getConnectingTunnels().size(), 1);
		assertEquals(dc1.getConnectingTunnels().get(0).getSourceEndAddress()
				.getPrefix(), "55.55.55.55");
		assertEquals(dc1.getConnectingTunnels().get(0)
				.getDestinationEndAddress().getPrefix(), "44.44.44.44");
		assertEquals(dc1.getConnectingTunnels().get(0)
				.getPhysicalLocalInterfaceName(), "eth33");
		assertEquals(dc1.getConnectingTunnels().get(0).getLink().getAddress()
				.getPrefix(), "2.2.2.7");
		assertEquals(dc1.getConnectingTunnels().get(0).getLink().getVlan(), 213);
		assertEquals(dc1.getConnectingTunnels().get(0).getLink()
				.getCostFunction().getSegments().size(), 1);
		assertEquals(dc1.getConnectingTunnels().get(0).getLink()
				.getTraversingTunnels().size(), 1);
		assertEquals(dc1.getConnectingTunnels().get(0).getLink()
				.getTraversingTunnels().get(0).getSourceEndAddress()
				.getPrefix(), "55.55.55.55");
		assertEquals(dc1.getConnectingTunnels().get(0).getLink()
				.getTraversingTunnels().get(0).getDestinationEndAddress()
				.getPrefix(), "44.44.44.44");
		assertEquals(dc1.getConnectingTunnels().get(0).getLink()
				.getTraversingTunnels().get(0).getPhysicalLocalInterfaceName(),
				"eth33");

		// checking first dc2dc
		DC2DCCommunication dc2 = dc2dcList.get(1);
		// checking remote cloud
		assertEquals(dc2.getRemoteCloud().getCloudDcName(), "dropbox");
		assertEquals(dc2.getRemoteCloud().getAs().getAsNumber(), 44);

		// checking local cloud
		assertEquals(dc2.getLocalCloud().getCloudDcName(), "google+");
		// checking as
		as = dc2.getLocalCloud().getAs();
		assertEquals(as.getAsNumber(), 2);
		assertEquals(as.getSbox().getManagementAddress().getPrefix(), "2.2.2.2");
		assertEquals(as.getLocalClouds().size(), 2);
		assertEquals(as.getBgRouters().size(), 2);
		assertEquals(as.getBgRouters().get(1).getSnmpCommunity(), "SNMP1");
		assertEquals(as.getBgRouters().get(1).getInterDomainLinks().size(), 1);
		assertEquals(as.getBgRouters().get(1).getInterDomainLinks().get(0)
				.getBgRouter().getManagementAddress().getPrefix(), "2.2.2.4");
		assertEquals(as.getBgRouters().get(1).getInterDomainLinks().get(0)
				.getTraversingTunnels().size(), 1);
		assertEquals(as.getBgRouters().get(1).getInterDomainLinks().get(0)
				.getTraversingTunnels().get(0).getSourceEndAddress()
				.getPrefix(), "77.7.7.7");
		assertEquals(dc2.getLocalCloud().getSdnController().getRestPort(), 8080);
		assertEquals(dc2.getLocalCloud().getDaRouter().getSnmpCommunity(),
				"SNMP2");
		
		// checking connecting tunnels
		assertEquals(dc2.getConnectingTunnels().size(), 1);
		assertEquals(dc2.getConnectingTunnels().get(0).getSourceEndAddress()
				.getPrefix(), "77.7.7.7");
		assertEquals(dc2.getConnectingTunnels().get(0)
				.getDestinationEndAddress().getPrefix(), "88.8.8.8");
		assertEquals(dc2.getConnectingTunnels().get(0)
				.getPhysicalLocalInterfaceName(), "eth3");
		assertEquals(dc2.getConnectingTunnels().get(0).getLink().getAddress()
				.getPrefix(), "2.2.2.8");
		assertEquals(dc2.getConnectingTunnels().get(0).getLink().getVlan(),
				4322);
		assertEquals(dc2.getConnectingTunnels().get(0).getLink()
				.getCostFunction().getSegments().size(), 2);
		assertEquals(dc2.getConnectingTunnels().get(0).getLink()
				.getTraversingTunnels().size(), 1);
		assertEquals(dc2.getConnectingTunnels().get(0).getLink()
				.getTraversingTunnels().get(0).getSourceEndAddress()
				.getPrefix(), "77.7.7.7");
		assertEquals(dc2.getConnectingTunnels().get(0).getLink()
				.getTraversingTunnels().get(0).getDestinationEndAddress()
				.getPrefix(), "88.8.8.8");
		assertEquals(dc2.getConnectingTunnels().get(0).getLink()
				.getTraversingTunnels().get(0).getPhysicalLocalInterfaceName(),
				"eth3");
	}

}
