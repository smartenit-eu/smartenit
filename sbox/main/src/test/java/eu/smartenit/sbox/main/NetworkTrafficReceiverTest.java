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
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.interfaces.intersbox.server.InterSBoxServer;
import eu.smartenit.sbox.ntm.NetworkTrafficManager;
import eu.smartenit.sbox.ntm.NetworkTrafficManagerDTMMode;

public class NetworkTrafficReceiverTest {
	
	private static final Logger logger = LoggerFactory.getLogger(NetworkTrafficReceiverTest.class);	
	
	private static ASDAO asdao;
	
	private static AS remoteAS;
	
	@BeforeClass
	public static void setupTests() {
		logger.info("Importing existing db schema and values.");
		DbConstants.DBI_URL = "jdbc:sqlite:src/test/resources/local.db";
		
		//modifying remote sbox address, to be 127.0.0.1
		asdao = new ASDAO();
		remoteAS = asdao.findByAsNumber(200);
		remoteAS.getSbox().setManagementAddress(new NetworkAddressIPv4("127.0.0.1", 0));
		asdao.update(remoteAS);
		
		logger.info("Initializing thread service.");
		ThreadFactory threadFactory = new ThreadFactory();
		SBoxThreadHandler.threadService = 
				Executors.newScheduledThreadPool(SBoxProperties.CORE_POOL_SIZE, threadFactory);
		
		logger.info("Initializing inter-sbox-server.");
		new InterSBoxServer(SBoxProperties.INTER_SBOX_PORT);
	}
	
	
	/**
	 * This method initializes NTM as traffic receiver,
	 * gets updated with X and R vectors and sends them to 
	 * mock inter-sbox server. 
	 * 
	 */
	@Test @Ignore
	public void shouldInitializeTrafficReceiverAndSendToInterSbox() throws InterruptedException {
		logger.info("--Testing traffic receiver initialize. --");
		NetworkTrafficManager ntm = new NetworkTrafficManager();
		ntm.initialize(NetworkTrafficManagerDTMMode.TRAFFIC_RECEIVER);
		assertTrue(true);
		
		LocalRVector rVector = new LocalRVector();
    	rVector.setSourceAsNumber(100);
    	rVector.addVectorValueForLink(new SimpleLinkID("1", "ISP-A"), 1500L);
    	rVector.addVectorValueForLink(new SimpleLinkID("2", "ISP-A"), 2100L);
    	ntm.getDtmTrafficManager().updateRVector(rVector);
    	assertTrue(true);
    	
    	XVector xVector = new XVector();
    	xVector.setSourceAsNumber(100);
    	xVector.addVectorValueForLink(new SimpleLinkID("1", "ISP-A"), 500L);
    	xVector.addVectorValueForLink(new SimpleLinkID("2", "ISP-A"), 800L);
    	ntm.getDtmTrafficManager().updateXVector(xVector);
    	assertTrue(true);
    	
    	Thread.sleep(5000);
    	logger.info("--------------------------");
	}
	
	/**
	 * This method initializes NTM as traffic receiver,
	 * gets updated with X and R vectors and sends them to 
	 * inter-sbox server multiple times. 
	 * 
	 */
	@Test @Ignore
	public void shouldInitializeTrafficReceiverAndSendToInterSboxMultipleTimes() throws InterruptedException {
		logger.info("--Testing traffic receiver initialize. --");
		NetworkTrafficManager ntm = new NetworkTrafficManager();
		ntm.initialize(NetworkTrafficManagerDTMMode.TRAFFIC_RECEIVER);
		assertTrue(true);
		
		for (int i = 0; i < 10; i++) {
			LocalRVector rVector = new LocalRVector();
			rVector.setSourceAsNumber(100);
			rVector.addVectorValueForLink(new SimpleLinkID("1", "ISP-A"), 1500L);
			rVector.addVectorValueForLink(new SimpleLinkID("2", "ISP-A"), 2100L);
			ntm.getDtmTrafficManager().updateRVector(rVector);
			assertTrue(true);

			XVector xVector = new XVector();
			xVector.setSourceAsNumber(100);
			xVector.addVectorValueForLink(new SimpleLinkID("1", "ISP-A"), 500L);
			xVector.addVectorValueForLink(new SimpleLinkID("2",	"ISP-A"), 800L);
			ntm.getDtmTrafficManager().updateXVector(xVector);
			assertTrue(true);

			Thread.sleep(1000);
		}
    	logger.info("--------------------------");
	}
	
	@AfterClass
	public static void cleanTests() {
		remoteAS = asdao.findByAsNumber(200);
		remoteAS.getSbox().setManagementAddress(new NetworkAddressIPv4("150.254.160.143", 0));
		asdao.update(remoteAS);
		
		DbConstants.DBI_URL = "jdbc:sqlite:smartenit.db";
		SBoxThreadHandler.shutdownNowThreads();
	}
	

}
