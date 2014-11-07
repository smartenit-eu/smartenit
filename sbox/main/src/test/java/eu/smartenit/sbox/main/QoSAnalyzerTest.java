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
import eu.smartenit.sbox.eca.EconomicAnalyzer;
import eu.smartenit.sbox.ntm.NetworkTrafficManager;
import eu.smartenit.sbox.ntm.NetworkTrafficManagerDTMMode;
import eu.smartenit.sbox.qoa.DTMQosAnalyzer;

public class QoSAnalyzerTest {

	private static final Logger logger = LoggerFactory
			.getLogger(QoSAnalyzerTest.class);

	@BeforeClass
	public static void setupTests() {
		logger.info("Importing existing db schema and values.");
		DbConstants.DBI_URL = "jdbc:sqlite:src/test/resources/local.db";

		logger.info("Initializing thread service.");
		ThreadFactory threadFactory = new ThreadFactory();
		SBoxThreadHandler.threadService = Executors.newScheduledThreadPool(
				SBoxProperties.CORE_POOL_SIZE, threadFactory);

	}
	
	/**
	 * Initializes QoA, pointing to snmp daemon at integration testbed.
	 * 
	 * 
	 * @throws InterruptedException
	 */
	@Test @Ignore
	public void shouldInitializeQoSAnalyzer()
			throws InterruptedException {
		
		logger.info("--Testing qos analyzer initialize. --");
		NetworkTrafficManager ntm = new NetworkTrafficManager();
		ntm.initialize(NetworkTrafficManagerDTMMode.TRAFFIC_RECEIVER);

		// Initialize economic analyzer
		EconomicAnalyzer eca = new EconomicAnalyzer(ntm.getDtmTrafficManager());

		// Initialize qos analyzer
		DTMQosAnalyzer analyzer = new DTMQosAnalyzer(
				ntm.getDtmTrafficManager(), eca);
		analyzer.initialize();
		Thread.sleep(5000);
		logger.info("--------------------------");
	}

	@AfterClass
	public static void cleanTests() {
		DbConstants.DBI_URL = "jdbc:sqlite:smartenit.db";
		SBoxThreadHandler.shutdownNowThreads();
	}

}
