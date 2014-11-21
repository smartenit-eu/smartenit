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
package eu.smartenit.sbox.ntm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.ntm.dtm.receiver.DTMTrafficManager;
import eu.smartenit.sbox.ntm.dtm.sender.DTMRemoteVectorsReceiver;

/**
 * Main class of the Network Traffic Manager component. Represents an entry
 * point for the NTM component common for all the implemented traffic management
 * mechanisms.
 * <p>
 * Manages the lifecycle of the major NTM internal objects that implement
 * traffic management mechanisms interfaces and logic.
 * 
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public class NetworkTrafficManager {
	private static final Logger logger = LoggerFactory.getLogger(NetworkTrafficManager.class);
	
	private DTMTrafficManager dtmTrafficManager;
	private DTMRemoteVectorsReceiver dtmVectorsReceiver;
	
	public NetworkTrafficManager() {
		this.dtmTrafficManager = new DTMTrafficManager();
		this.dtmVectorsReceiver = new DTMRemoteVectorsReceiver();
	}
	
	/**
	 * Method that should be called in order to initialize the component (e.g.
	 * read required data from data base) in default DTM mode.
	 */
	public void initialize() {
		initialize(NetworkTrafficManagerDTMMode.TRAFFIC_SENDER_AND_RECEIVER);
	}

	/**
	 * Method that should be called in order to initialize the component in
	 * provided {@link NetworkTrafficManagerDTMMode} mode.
	 * 
	 * @param mode
	 *            DTM mode for which the component should be initialized
	 */
	public void initialize(NetworkTrafficManagerDTMMode mode) {
		logger.info("Initialising NTM component ...");
		switch(mode) {
		case TRAFFIC_RECEIVER: 
			dtmTrafficManager.initialize();
			break;
		case TRAFFIC_SENDER:
			dtmVectorsReceiver.initialize();
			break;
		case TRAFFIC_SENDER_AND_RECEIVER: 
			dtmTrafficManager.initialize();
			dtmVectorsReceiver.initialize();
		}
		logger.info("... NTM component initialization complete.");
	}
	
	/**
	 * Returns an instance of {@link DTMTrafficManager} that should be used by
	 * QoS Analyzer and Economic Analyzer to provide updated vectors to DTM
	 * logic implemented as part of the Network Traffic Manager.
	 * 
	 * @return instance of {@link DTMTrafficManager} class
	 */
	public DTMTrafficManager getDtmTrafficManager() {
		return dtmTrafficManager;
	}

	/**
	 * Returns an instance of {@link DTMRemoteVectorsReceiver} that should be
	 * used by Inter-SBox communication server to provide received remote
	 * vectors to DTM logic implemented as part of the Network Traffic Manager.
	 * 
	 * @return instance of {@link DTMRemoteVectorsReceiver} class
	 */
	public DTMRemoteVectorsReceiver getDtmVectorsReceiver() {
		return dtmVectorsReceiver;
	}

}
