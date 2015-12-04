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
package eu.smartenit.sbox.qoa;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.commons.SBoxProperties;
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.db.dto.ZVector;
import eu.smartenit.sbox.eca.EconomicAnalyzer;
import eu.smartenit.sbox.ntm.dtm.receiver.DTMTrafficManager;
import eu.smartenit.sbox.qoa.experiment.ExtendedSNMPTrafficCollector;

/**
 * Manages the lifecycle of DTM related modules of QoE/QoS Analyzer component.
 * Communicates with DTM modules in Economic Analyzer ({@link EconomicAnalyzer})
 * and Network Traffic Manager ({@link DTMTrafficManager}).
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a
 *         href="http://psnc.pl">PSNC</a>)
 * @author <a href="mailto:llopat@man.poznan.pl">Lukasz Lopatowski</a> (<a
 *         href="http://psnc.pl">PSNC</a>)
 * @version 1.0
 * 
 */
public class DTMQosAnalyzer {
	private static final Logger logger = LoggerFactory.getLogger(DTMQosAnalyzer.class);
	
	private DTMTrafficManager trafficManager;
	private EconomicAnalyzer economicAnalyzer;
	private SNMPTrafficCollector trafficCollector;
	
	/**
	 * The constructor with arguments.
	 * 
	 * @param trafficManager
	 *            instance of the DTM external interface class of the Network
	 *            Traffic Manager component
	 * @param economicAnalyzer
	 *            instance of the DTM external interface class of the Economic
	 *            Analyzer component
	 */
	public DTMQosAnalyzer(DTMTrafficManager trafficManager, EconomicAnalyzer economicAnalyzer) {
		this.trafficManager = trafficManager;
		this.economicAnalyzer = economicAnalyzer;
	}
	
	/**
	 * Initializes {@link SNMPTrafficCollector} and starts scheduled traffic
	 * counters monitoring tasks according to configured time schedule
	 * parameters.
	 * 
	 * @return false when QOA initialization failed
	 */
	public boolean initialize() {
		logger.info("Initialising QOA component ...");
		if(SBoxProperties.LOG_TRAFFIC_DETAILS)
			trafficCollector = new ExtendedSNMPTrafficCollector(this);
		else
			trafficCollector = new SNMPTrafficCollector(this);
		if(trafficCollector.configure(loadAllLocalASsFromDB(), loadAllDCCommunicationsFromDB())) {
			trafficCollector.scheduleMonitoringTasks();
			logger.info("... QOA component initialization complete.");
			return true;
		}
		logger.warn("... QOA component initialization failed.");
		return false;
	}
	
	/**
	 * Updates {@link DTMTrafficManager} with {@link XVector}
	 */
	public void updateXVector(XVector xVector) {
		logger.debug("Received request to update X vector information in NTM component.");
		trafficManager.updateXVector(xVector);
		logger.debug("Update completed.");
	}
	
	/**
	 * Updates {@link EconomicAnalyzer} with {@link XVector} and list of {@link ZVector}
	 */
	public void updateXZVectors(XVector xVector, List<ZVector> zVectors) {
		logger.debug("Received request to update X and Z vectors information in Economic Analyzer component."); 
		economicAnalyzer.updateXZVectors(xVector, zVectors);
		logger.debug("Update completed.");
	}

	private List<AS> loadAllLocalASsFromDB() {
		List<AS> localSystems = new ArrayList<AS>();
		for(AS system : DAOFactory.getASDAOInstance().findAllAsesCloudsBgRoutersLinks()) {
			if(system.isLocal())
				localSystems.add(system);
		}
		return localSystems;
	}
	
	private List<DC2DCCommunication> loadAllDCCommunicationsFromDB() {
		return DAOFactory.getDCDC2DCCommunicationDAOInstance().findAllDC2DCCommunicationsCloudsTunnels();
	}
}
