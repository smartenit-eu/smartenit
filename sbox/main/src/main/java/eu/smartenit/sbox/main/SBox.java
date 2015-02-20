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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.commons.SBoxProperties;
import eu.smartenit.sbox.commons.SBoxThreadHandler;
import eu.smartenit.sbox.commons.ThreadFactory;
import eu.smartenit.sbox.db.dao.DbConstants;
import eu.smartenit.sbox.eca.EconomicAnalyzer;
import eu.smartenit.sbox.interfaces.intersbox.server.InterSBoxServer;
import eu.smartenit.sbox.ntm.NetworkTrafficManager;
import eu.smartenit.sbox.qoa.DTMQosAnalyzer;

/**
 * The SBox class that initiates all mechanism processes. 
 *
 * @author George Petropoulos
 * @version 1.0
 * 
 */
public class SBox {
	
	private static final Logger logger = LoggerFactory.getLogger(SBox.class);

	private static ThreadFactory threadFactory = new ThreadFactory();
	
	/**
	 * The main method.
	 * 
	 *  @param args The main method arguments
	 * 
	 */
	public static void main(String[] args) {
		logger.info("Initialing SBox service.");
		
		// Read configuration parameters from file.
		readConfigurationProperties();

		// Set db file url.
		DbConstants.DBI_URL = "jdbc:sqlite:" + SBoxProperties.DB_FILE;
		
		// Initialize threads pool.
		threadFactory = new ThreadFactory();
		SBoxThreadHandler.threadService = 
				Executors.newScheduledThreadPool(SBoxProperties.CORE_POOL_SIZE, threadFactory);
							
		// Initialize network traffic manager with default mode.
		NetworkTrafficManager ntm = new NetworkTrafficManager();
		ntm.initialize();
		
		// Initialize inter-sbox server at configured port and constructed NTM.
		new InterSBoxServer(SBoxProperties.INTER_SBOX_PORT, ntm);
		
		// Initialize economic analyzer.
		EconomicAnalyzer eca = new EconomicAnalyzer(ntm.getDtmTrafficManager());
		
		// Initialize qos analyzer.
		DTMQosAnalyzer analyzer = new DTMQosAnalyzer(ntm.getDtmTrafficManager(), eca);
		analyzer.initialize();
		
	}
	
	/**
	 * The method that reads and sets the configuration parameters from file. 
	 * 
	 */
	private static void readConfigurationProperties () {
		logger.debug("Reading configuration parameters.");
		PropertiesConfiguration config = null;
		
		try {
			config = new PropertiesConfiguration(SBoxProperties.PROPERTIES_FILE_NAME);
			SBoxProperties.CONNECTION_RETRIES = config.getInt("connection.retries");
			SBoxProperties.CONNECTION_TIMEOUT = config.getLong("connection.timeout");
			SBoxProperties.CORE_POOL_SIZE = config.getInt("core.pool.size");
			SBoxProperties.DB_FILE = config.getString("db.file");
			SBoxProperties.INTER_SBOX_PORT = config.getInt("inter.sbox.port");
			SBoxProperties.MAX_FETCHING_TIME = config.getLong("max.fetching.time");
			
			SBoxProperties.LOG_TRAFFIC_DETAILS = config.getBoolean("log.traffic.details");
			SBoxProperties.TRAFFIC_DETAILS_FILE_PATH = config.getString("traffic.details.file.path");
			SBoxProperties.TRAFFIC_DETAILS_FILE_NAME = config.getString("traffic.details.file.name");
			
			SBoxProperties.DEFAULT_REF_VECTOR_FILE = config.getString("default.ref.vector.file");
			
		} catch (ConfigurationException e) {
			logger.warn("Exception while loading configuration file " 
					+ SBoxProperties.PROPERTIES_FILE_NAME + ".");
			
		}  catch (ConversionException e2) {
			logger.warn("Invalid format of configuration parameters, using default.");
			
		}
		
		logger.debug("Configuration parameters: \n" 
				+ "connection.retries: " + SBoxProperties.CONNECTION_RETRIES + "\n"
				+ "connection.timeout: " + SBoxProperties.CONNECTION_TIMEOUT + "\n"
				+ "core.pool.size: " + SBoxProperties.CORE_POOL_SIZE + "\n"
				+ "db.file: " + SBoxProperties.DB_FILE + "\n"
				+ "inter.sbox.port: " + SBoxProperties.INTER_SBOX_PORT + "\n"
				+ "max.fetching.time: " + SBoxProperties.MAX_FETCHING_TIME + "\n"
				+ "log.traffic.details: " + SBoxProperties.LOG_TRAFFIC_DETAILS + "\n"
				+ "traffic.details.file.path: " + SBoxProperties.TRAFFIC_DETAILS_FILE_PATH + "\n"
				+ "traffic.details.file.name: " + SBoxProperties.TRAFFIC_DETAILS_FILE_NAME + "\n"
				+ "default.ref.vector.file: " + SBoxProperties.DEFAULT_REF_VECTOR_FILE);
	}
	
}
