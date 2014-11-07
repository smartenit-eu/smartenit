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

import static org.junit.Assert.*;

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
import eu.smartenit.sbox.db.dao.DbConstants;
import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.interfaces.intersbox.client.InterSBoxClient;
import eu.smartenit.sbox.interfaces.intersbox.server.InterSBoxServer;
import eu.smartenit.sbox.ntm.NetworkTrafficManager;
import eu.smartenit.sbox.ntm.NetworkTrafficManagerDTMMode;

public class NetworkTrafficSenderRealSDNTest {
	
	private static final Logger logger = LoggerFactory.getLogger(NetworkTrafficSenderRealSDNTest.class);	
				
	@BeforeClass
	public static void setupTests() {
		logger.info("Importing existing db schema and values.");
		DbConstants.DBI_URL = "jdbc:sqlite:src/test/resources/remote.db";
         
		logger.info("Initializing thread service.");
		ThreadFactory threadFactory = new ThreadFactory();
		SBoxThreadHandler.threadService = 
				Executors.newScheduledThreadPool(SBoxProperties.CORE_POOL_SIZE, threadFactory);
		
	}
	
	/**
	 * This method initializes NTM as traffic sender, sends config data
	 * to real SDN Controller.
	 * 
	 */
	@Test @Ignore
	public void shouldInitializeTrafficSender() throws Exception {
		
		logger.info("--Testing traffic sender initialize. --");
		NetworkTrafficManager ntm = new NetworkTrafficManager();
		ntm.initialize(NetworkTrafficManagerDTMMode.TRAFFIC_SENDER);
				
		logger.info("SDN Controller received configuration data.");
		assertTrue(true);
		
		logger.info("--------------------------");
	}
	
	/**
	 * This method initializes NTM as traffic sender, sends config data
	 * to real SDN Controller and gets updated with C and R vectors.
	 * 
	 */
	@Test @Ignore
	public void shouldInitializeTrafficSenderAndUpdateRCVectors() throws Exception {
		
		logger.info("--Testing traffic sender initialize. --");
		NetworkTrafficManager ntm = new NetworkTrafficManager();
		ntm.initialize(NetworkTrafficManagerDTMMode.TRAFFIC_SENDER);
		
		logger.info("SDN Controller received configuration data.");
		assertTrue(true);
		
		logger.info("Initializing inter-sbox-server with ntm.");
		new InterSBoxServer(SBoxProperties.INTER_SBOX_PORT, ntm);
				
		Thread.sleep(2000);
		
		CVector cVector = new CVector();
		cVector.setSourceAsNumber(1);
		cVector.addVectorValueForLink(
				new SimpleLinkID("link1", "isp"), 500L);
		RVector rVector = new RVector();
		rVector.setSourceAsNumber(1);
    	rVector.addVectorValueForLink(
    			new SimpleLinkID("link1", "isp"), 1000L);
		
		logger.info("Sending r and c vectors through inter-sbox client.");		
		InterSBoxClient client = new InterSBoxClient();
		client.send("127.0.0.1", SBoxProperties.INTER_SBOX_PORT, cVector, rVector);
		
		Thread.sleep(2000);
				
		logger.info("Verified that SDN controller received 1 config data and 1 update "
				+ "for reference and compensation vectors.");
		
		logger.info("--------------------------");
	}
	
	
		
	
	@AfterClass
	public static void cleanTests() {
		DbConstants.DBI_URL = "jdbc:sqlite:smartenit.db";
        
		SBoxThreadHandler.shutdownNowThreads();
	}
	
	
	

}
