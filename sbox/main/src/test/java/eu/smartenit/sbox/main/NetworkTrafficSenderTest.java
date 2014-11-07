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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.*;

import java.util.concurrent.Executors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;

import eu.smartenit.sbox.commons.SBoxProperties;
import eu.smartenit.sbox.commons.SBoxThreadHandler;
import eu.smartenit.sbox.commons.ThreadFactory;
import eu.smartenit.sbox.db.dao.DbConstants;
import eu.smartenit.sbox.db.dao.SDNControllerDAO;
import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.db.dto.SDNController;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.interfaces.intersbox.client.InterSBoxClient;
import eu.smartenit.sbox.interfaces.intersbox.server.InterSBoxServer;
import eu.smartenit.sbox.ntm.NetworkTrafficManager;
import eu.smartenit.sbox.ntm.NetworkTrafficManagerDTMMode;
import eu.smartenit.sdn.interfaces.sboxsdn.URLs;

public class NetworkTrafficSenderTest {
	
	@ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(8888);
	
	private static final Logger logger = LoggerFactory.getLogger(NetworkTrafficSenderTest.class);	
		
	private static SDNControllerDAO sdao;
	
	private static SDNController sdn;
	
	@BeforeClass
	public static void setupTests() {
		logger.info("Importing existing db schema and values.");
		DbConstants.DBI_URL = "jdbc:sqlite:src/test/resources/remote.db";
		
		logger.info("Mocking SDN Controller REST APIs");
		stubFor(post(urlEqualTo(URLs.BASE_PATH + URLs.DTM_R_C_VECTORS_PATH))
        		.withHeader("Accept", equalTo("application/json; q=0.9,*/*;q=0.8"))
        		.withHeader("Content-Type", equalTo( "application/json; charset=UTF-8"))
        		.willReturn(aResponse()
        				.withStatus(200)));
        
        stubFor(post(urlEqualTo(URLs.BASE_PATH + URLs.DTM_CONFIG_DATA_PATH))
        		.withHeader("Accept", equalTo("application/json; q=0.9,*/*;q=0.8"))
        		.withHeader("Content-Type", equalTo( "application/json; charset=UTF-8"))
        		.willReturn(aResponse()
        				.withStatus(200)));
        
        //Modifying sdn controller address and port, to point to the mock
        sdao = new SDNControllerDAO();
        sdn = sdao.findById("146.124.2.178");
        sdn.setRestPort(8888);
        sdn.setRestHost(new NetworkAddressIPv4("127.0.0.1", 0));
        sdao.update(sdn);
         
		logger.info("Initializing thread service.");
		ThreadFactory threadFactory = new ThreadFactory();
		SBoxThreadHandler.threadService = 
				Executors.newScheduledThreadPool(SBoxProperties.CORE_POOL_SIZE, threadFactory);
		
	}
	
	/**
	 * This method initializes NTM as traffic sender, sends config data
	 * to mock SDN Controller.
	 * 
	 */
	@Test @Ignore
	public void shouldInitializeTrafficSender() throws Exception {
		
		logger.info("--Testing traffic sender initialize. --");
		NetworkTrafficManager ntm = new NetworkTrafficManager();
		ntm.initialize(NetworkTrafficManagerDTMMode.TRAFFIC_SENDER);
		
		Thread.sleep(2000);
		
		verify(postRequestedFor(urlMatching("/smartenit/dtm/config-data"))
				.withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
				.withHeader("Accept", equalTo("application/json; q=0.9,*/*;q=0.8"))
				);
		logger.info("SDN Controller received configuration data.");
		assertTrue(true);
		
		logger.info("--------------------------");
	}
	
	/**
	 * This method initializes NTM as traffic sender, sends config data
	 * to mock SDN Controller and gets updated with C and R vectors.
	 * 
	 */
	@Test @Ignore
	public void shouldInitializeTrafficSenderAndUpdateRCVectors() throws Exception {
		
		logger.info("--Testing traffic sender initialize. --");
		NetworkTrafficManager ntm = new NetworkTrafficManager();
		ntm.initialize(NetworkTrafficManagerDTMMode.TRAFFIC_SENDER);
		
		verify(postRequestedFor(urlMatching("/smartenit/dtm/config-data"))
				.withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
				.withHeader("Accept", equalTo("application/json; q=0.9,*/*;q=0.8"))
				);
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
		
		verify(1, postRequestedFor(urlEqualTo("/smartenit/dtm/config-data")));
		verify(1, postRequestedFor(urlEqualTo("/smartenit/dtm/r-c-vectors")));
		
		logger.info("Verified that SDN controller received 1 config data and 1 update "
				+ "for reference and compensation vectors.");
		
		logger.info("--------------------------");
	}
	
	/**
	 * This method initializes NTM as traffic sender, sends config data
	 * to mock SDN Controller and gets updated with C and R vectors multiple times.
	 * 
	 */
	@Test @Ignore
	public void shouldInitializeTrafficSenderAndUpdateRCVectorsMultipleTimes() throws Exception {
		
		logger.info("--Testing traffic sender initialize. --");
		NetworkTrafficManager ntm = new NetworkTrafficManager();
		ntm.initialize(NetworkTrafficManagerDTMMode.TRAFFIC_SENDER);
		
		verify(postRequestedFor(urlMatching("/smartenit/dtm/config-data"))
				.withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
				.withHeader("Accept", equalTo("application/json; q=0.9,*/*;q=0.8"))
				);
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
		
		for (int i=0; i<10; i++) {
			InterSBoxClient client = new InterSBoxClient();
			client.send("127.0.0.1", SBoxProperties.INTER_SBOX_PORT, cVector, rVector);
		
			Thread.sleep(200);
		}
		
		verify(1, postRequestedFor(urlEqualTo("/smartenit/dtm/config-data")));
		verify(10, postRequestedFor(urlEqualTo("/smartenit/dtm/r-c-vectors")));
		
		//verify(postRequestedFor(urlMatching("/smartenit/dtm/r-c-vectors"))
		//		.withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
		//		.withHeader("Accept", equalTo("application/json; q=0.9,*/*;q=0.8"))
		//		);
		
		logger.info("Verified that SDN controller received 1 config data and 10 updates "
				+ "for reference and compensation vectors.");
		
		logger.info("--------------------------");
	}
		
	
	@AfterClass
	public static void cleanTests() {
		DbConstants.DBI_URL = "jdbc:sqlite:smartenit.db";
		
		sdn = sdao.findById("146.124.2.178");
        sdn.setRestPort(8080);
        sdn.setRestHost(new NetworkAddressIPv4("146.124.2.178", 0));
        sdao.update(sdn);
        
		SBoxThreadHandler.shutdownNowThreads();
	}
	
	
	

}
