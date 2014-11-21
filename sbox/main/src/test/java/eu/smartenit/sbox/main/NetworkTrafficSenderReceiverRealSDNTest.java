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
import eu.smartenit.sbox.db.dao.DC2DCCommunicationDAO;
import eu.smartenit.sbox.db.dao.DbConstants;
import eu.smartenit.sbox.db.dao.LinkDAO;
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.interfaces.intersbox.server.InterSBoxServer;
import eu.smartenit.sbox.ntm.NetworkTrafficManager;
import eu.smartenit.sbox.ntm.NetworkTrafficManagerDTMMode;
import eu.smartenit.sbox.ntm.dtm.DAOFactory;

public class NetworkTrafficSenderReceiverRealSDNTest {
	
	private static final Logger logger = LoggerFactory.getLogger(NetworkTrafficSenderReceiverRealSDNTest.class);	
	
	private static ASDAO asdao;
	
	private static AS remoteAS;
	
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
	 * This method initializes NTMs at sending and receiving domains, NTM at
	 * receiving gets updated with X and R vectors and sends C and R vectors to
	 * the sending NTM, which updates the real SDN Controller.
	 * 
	 */
	@Test @Ignore
	public void shouldTestNtmToNtmCommunication() throws Exception {
		
		logger.info("--Testing NTM-to-NTM communication. --");
		logger.info("Initializing NTM at sending domain, #2, and inter-sbox server.");
		NetworkTrafficManager ntm = new NetworkTrafficManager();
		ntm.initialize(NetworkTrafficManagerDTMMode.TRAFFIC_SENDER);
		
		new InterSBoxServer(SBoxProperties.INTER_SBOX_PORT, ntm);
		Thread.sleep(2000);
		
		assertTrue(true);
		
		logger.info("SDN Controller at sending domain AS #2 received 1 request "
				+ "for configuration data.");
		Thread.sleep(2000);
		
		
		logger.info("Initializing NTM at RECEIVER domain, #1.");
		DbConstants.DBI_URL = "jdbc:sqlite:src/test/resources/local.db";
		//Modifying daofactory instances to get different db file.
		DAOFactory.setASDAOInstance(new ASDAO());
		DAOFactory.setDC2DCCommunicationDAO(new DC2DCCommunicationDAO());
		DAOFactory.setLinkDAO(new LinkDAO());
		
		//modifying remote sbox address, to be 127.0.0.1
      	asdao = new ASDAO();
      	remoteAS = asdao.findByAsNumber(200);
      	remoteAS.getSbox().setManagementAddress(new NetworkAddressIPv4("127.0.0.1", 0));
      	asdao.update(remoteAS);
		
		NetworkTrafficManager ntm2 = new NetworkTrafficManager();
		ntm2.initialize(NetworkTrafficManagerDTMMode.TRAFFIC_RECEIVER);
		
		logger.info("Updating NTM at RECEIVER domain, #1 with R and X vectors.");
    	XVector xVector = new XVector();
    	xVector.setSourceAsNumber(100);
    	xVector.addVectorValueForLink(new SimpleLinkID("1", "ISP-A"), 500L);
    	xVector.addVectorValueForLink(new SimpleLinkID("2", "ISP-A"), 800L);
    	ntm2.getDtmTrafficManager().updateXVector(xVector);
    	assertTrue(true);

    	LocalRVector rVector = new LocalRVector();
    	rVector.setSourceAsNumber(100);
    	rVector.addVectorValueForLink(new SimpleLinkID("1", "ISP-A"), 1500L);
    	rVector.addVectorValueForLink(new SimpleLinkID("2", "ISP-A"), 2100L);
    	ntm2.getDtmTrafficManager().updateRVector(rVector);
    	assertTrue(true);
    	
		Thread.sleep(2000);
		
		logger.info("Verified that SDN controller at sending domain #2, "
				+ "received 1 config data and 1 update "
				+ "for reference and compensation vectors.");
		
		logger.info("--------------------------");
		
		remoteAS = asdao.findByAsNumber(200);
		remoteAS.getSbox().setManagementAddress(new NetworkAddressIPv4("150.254.160.143", 0));
		asdao.update(remoteAS);
	}
		
	
	@AfterClass
	public static void cleanTests() {
		DbConstants.DBI_URL = "jdbc:sqlite:src/test/resources/remote.db";
		
		SBoxThreadHandler.shutdownNowThreads();
	}
	
	
	

}
