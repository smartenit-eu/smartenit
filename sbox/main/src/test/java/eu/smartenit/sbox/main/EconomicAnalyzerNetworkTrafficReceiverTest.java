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

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.commons.SBoxProperties;
import eu.smartenit.sbox.commons.SBoxThreadHandler;
import eu.smartenit.sbox.commons.ThreadFactory;
import eu.smartenit.sbox.db.dao.ASDAO;
import eu.smartenit.sbox.db.dao.DbConstants;
import eu.smartenit.sbox.db.dao.SystemControlParametersDAO;
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.ChargingRule;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.SystemControlParameters;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.db.dto.ZVector;
import eu.smartenit.sbox.eca.EconomicAnalyzer;
import eu.smartenit.sbox.interfaces.intersbox.server.InterSBoxServer;
import eu.smartenit.sbox.ntm.NetworkTrafficManager;
import eu.smartenit.sbox.ntm.NetworkTrafficManagerDTMMode;

public class EconomicAnalyzerNetworkTrafficReceiverTest {

	private static final Logger logger = LoggerFactory
			.getLogger(EconomicAnalyzerNetworkTrafficReceiverTest.class);
	
	private static ASDAO asdao;
	private static AS remoteAS;
	
	private static SystemControlParametersDAO scpDAO;
	private static SystemControlParameters scp;
	
	@BeforeClass
	public static void setupTests() throws Exception {
		logger.info("Importing existing db schema and values.");
		DbConstants.DBI_URL = "jdbc:sqlite:src/test/resources/local.db";
				
		logger.info("Initializing thread service.");
		ThreadFactory threadFactory = new ThreadFactory();
		SBoxThreadHandler.threadService = Executors.newScheduledThreadPool(
				SBoxProperties.CORE_POOL_SIZE, threadFactory);
		
		//modifying remote sbox address, to be 127.0.0.1
      	asdao = new ASDAO();
      	remoteAS = asdao.findByAsNumber(200);
      	remoteAS.getSbox().setManagementAddress(new NetworkAddressIPv4("127.0.0.1", 0));
      	asdao.update(remoteAS);
      	
      	//modifying charging rule to ChargingRule.volume
      	scpDAO = new SystemControlParametersDAO();
      	scp = scpDAO.findLast();
      	scp.setChargingRule(ChargingRule.volume);
      	scpDAO.insert(scp);
      	
		logger.info("Initializing inter-sbox-server.");
		SBoxProperties.INTER_SBOX_PORT++;
		new InterSBoxServer(SBoxProperties.INTER_SBOX_PORT);
	}

	/**
	 * Initializes ECA and NTM at receiving domain, and updates them
	 * with X and Z vectors, expecting to do the necessary calculations
	 * and send them to sending domain.
	 * 
	 * @throws InterruptedException
	 */
	@Test @Ignore
	public void shouldTestEcaNtmTrafficReceiver() throws InterruptedException {
		logger.info("--Testing economic analyzer and traffic receiver initialize. --");
		NetworkTrafficManager ntm = new NetworkTrafficManager();
		ntm.initialize(NetworkTrafficManagerDTMMode.TRAFFIC_RECEIVER);
		assertTrue(true);

		EconomicAnalyzer eca = new EconomicAnalyzer(ntm.getDtmTrafficManager());

		for (int i = 0; i < 25; i++) {
			XVector xVector = new XVector();
			xVector.setSourceAsNumber(100);
			xVector.addVectorValueForLink(new SimpleLinkID("1", "ISP-A"), 500L);
			xVector.addVectorValueForLink(new SimpleLinkID("2", "ISP-A"), 800L);
			ntm.getDtmTrafficManager().updateXVector(xVector);

			List<ZVector> zVectorList = new ArrayList<ZVector>();
			ZVector zVector = new ZVector();
			zVector.setSourceAsNumber(100);
			zVector.addVectorValueForLink(new SimpleLinkID("1", "ISP-A"), 100L);
			zVector.addVectorValueForLink(new SimpleLinkID("2", "ISP-A"), 100L);
			zVectorList.add(zVector);
			eca.updateXZVectors(xVector, zVectorList);
		}
		
		Thread.sleep(2000);
		logger.info("--------------------------");
	}

	@AfterClass
	public static void cleanTests() throws Exception {
		DbConstants.DBI_URL = "jdbc:sqlite:smartenit.db";
		
		remoteAS = asdao.findByAsNumber(200);
		remoteAS.getSbox().setManagementAddress(new NetworkAddressIPv4("150.254.160.143", 0));
		asdao.update(remoteAS);
		
      	scp = scpDAO.findLast();
      	scp.setChargingRule(ChargingRule.volume);
      	scpDAO.insert(scp);
		
		SBoxThreadHandler.shutdownNowThreads();
	}

}
