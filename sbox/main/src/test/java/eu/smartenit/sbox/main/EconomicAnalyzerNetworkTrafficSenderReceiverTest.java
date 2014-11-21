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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
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
import eu.smartenit.sbox.db.dao.ASDAO;
import eu.smartenit.sbox.db.dao.DC2DCCommunicationDAO;
import eu.smartenit.sbox.db.dao.DbConstants;
import eu.smartenit.sbox.db.dao.LinkDAO;
import eu.smartenit.sbox.db.dao.SDNControllerDAO;
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SDNController;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.db.dto.ZVector;
import eu.smartenit.sbox.eca.EconomicAnalyzer;
import eu.smartenit.sbox.interfaces.intersbox.server.InterSBoxServer;
import eu.smartenit.sbox.ntm.NetworkTrafficManager;
import eu.smartenit.sbox.ntm.NetworkTrafficManagerDTMMode;
import eu.smartenit.sbox.ntm.dtm.DAOFactory;
import eu.smartenit.sdn.interfaces.sboxsdn.URLs;

public class EconomicAnalyzerNetworkTrafficSenderReceiverTest {
	
	@ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(8888);
	
	private static final Logger logger = LoggerFactory.getLogger(EconomicAnalyzerNetworkTrafficSenderReceiverTest.class);	
		
	private static SDNControllerDAO sdao;
	
	private static SDNController sdn;
	
	private static ASDAO asdao;
	
	private static AS remoteAS;
	
	@BeforeClass
	public static void setupTests() {
		logger.info("Importing existing db schema and values.");
		DbConstants.DBI_URL = "jdbc:sqlite:src/test/resources/remote.db";
		
		logger.info("Mocking SDN Controller REST API");
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
        sdn = sdao.findById("192.168.122.105");
        sdn.setRestPort(8888);
        sdn.setRestHost(new NetworkAddressIPv4("127.0.0.1", 0));
        sdao.update(sdn);
        
		logger.info("Initializing thread service.");
		ThreadFactory threadFactory = new ThreadFactory();
		SBoxThreadHandler.threadService = 
				Executors.newScheduledThreadPool(SBoxProperties.CORE_POOL_SIZE, threadFactory);
		
	}
	

	/**
	 * This method initializes NTMs at sending and receiving domains,
	 * NTM at receiving gets updated with X and R vectors and sends them to 
	 * the sending NTM, which updates the mock SDN Controller. 
	 * 
	 */
	@Test @Ignore
	public void shouldTestNtmToNtmCommunication() throws Exception {
		
		logger.info("--Testing ECA, NTM-to-NTM communication. --");
		logger.info("Initializing NTM at sending domain, #2, and inter-sbox server.");
		NetworkTrafficManager ntm = new NetworkTrafficManager();
		ntm.initialize(NetworkTrafficManagerDTMMode.TRAFFIC_SENDER);
		
		SBoxProperties.INTER_SBOX_PORT++;
		new InterSBoxServer(SBoxProperties.INTER_SBOX_PORT, ntm);
		Thread.sleep(2000);
		
		verify(postRequestedFor(urlMatching("/smartenit/dtm/config-data"))
				.withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
				.withHeader("Accept", equalTo("application/json; q=0.9,*/*;q=0.8"))
				);					
		verify(1, postRequestedFor(urlEqualTo("/smartenit/dtm/config-data")));
		verify(0, postRequestedFor(urlEqualTo("/smartenit/dtm/r-c-vectors")));
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
		
      	logger.info("--Initializing economic analyzer and traffic receiver . --");
		NetworkTrafficManager ntm2 = new NetworkTrafficManager();
		ntm2.initialize(NetworkTrafficManagerDTMMode.TRAFFIC_RECEIVER);
		assertTrue(true);

		EconomicAnalyzer eca = new EconomicAnalyzer(ntm2.getDtmTrafficManager());

		for (int i = 0; i < 10; i++) {
			XVector xVector = new XVector();
			xVector.setSourceAsNumber(100);
			xVector.addVectorValueForLink(new SimpleLinkID("1", "ISP-A"), 500L);
			xVector.addVectorValueForLink(new SimpleLinkID("2", "ISP-A"), 800L);
			ntm2.getDtmTrafficManager().updateXVector(xVector);

			List<ZVector> zVectorList = new ArrayList<ZVector>();
			ZVector zVector = new ZVector();
			zVector.setSourceAsNumber(100);
			zVector.addVectorValueForLink(new SimpleLinkID("1", "ISP-A"), 100L);
			zVector.addVectorValueForLink(new SimpleLinkID("2", "ISP-A"), 100L);
			zVectorList.add(zVector);
			eca.updateXZVectors(xVector, zVectorList);
		}
		
		Thread.sleep(2000);
		
		verify(1, postRequestedFor(urlEqualTo("/smartenit/dtm/config-data")));
		verify(1, postRequestedFor(urlEqualTo("/smartenit/dtm/r-c-vectors")));
		
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
		
		sdao = new SDNControllerDAO();
		sdn = sdao.findById("192.168.122.105");
        sdn.setRestPort(8080);
        sdn.setRestHost(new NetworkAddressIPv4("192.168.122.105", 0));
        sdao.update(sdn);
		
		SBoxThreadHandler.shutdownNowThreads();
	}

}
