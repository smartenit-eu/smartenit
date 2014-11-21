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
package eu.smartenit.sbox.ntm.dtm.sender;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.ConfigData;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.db.dto.SDNController;
import eu.smartenit.sbox.interfaces.sboxsdn.SboxSdnClient;

/**
 * Implements methods that forward requests for SDN Controller vector
 * information update or configuration to the {@link SboxSdnClient} component.
 * 
 * @author Lukasz Lopatowski
 * @version 1.2
 * 
 */
public class SDNConfigPusher {
	
	private static final Logger logger = LoggerFactory.getLogger(SDNConfigPusher.class);
	
	private SboxSdnClient sdnClient = SDNClientFactory.getInstance();
	private Set<SDNController> controllers;
	
	/**
	 * The constructor with arguments.
	 * 
	 * @param controllers
	 *            set of {@link SDNController}s to which subsequent requests
	 *            called on this object will be directed
	 */
	public SDNConfigPusher(Set<SDNController> controllers) {
		this.controllers = controllers;
	}

	/**
	 * The constructor with arguments.
	 * 
	 * @param controller
	 *            {@link SDNController} to which subsequent requests called on
	 *            this object will be directed
	 */
	public SDNConfigPusher(SDNController controller) {
		this.controllers = new HashSet<SDNController>(Arrays.asList(controller));
	}
	
	/**
	 * Method uses {@link SboxSdnClient} to update compensation vector
	 * information in selected SDN controllers.
	 * 
	 * @param cVector
	 *            new compensation vector
	 */
	public void updateVectorInfo(CVector cVector) {
		logger.info("Received request to update CVector ...");
		if (controllers == null || controllers.isEmpty()) {
			logger.warn("... but target SDN controllers list is empty. Done.");
			return;
		}
		
		for(SDNController controller : controllers)
			sdnClient.distribute(controller, cVector);
		logger.info("... executed.");
	}
	
	/**
	 * Method uses {@link SboxSdnClient} to update compensation and reference
	 * vectors information in selected SDN controllers.
	 * 
	 * @param cVector
	 *            new compensation vector
	 * @param rVector
	 *            new reference vector
	 */
	public void updateVectorInfo(CVector cVector, RVector rVector) {
		logger.info("Received request to update CVector and RVector ...");
		if (controllers == null || controllers.isEmpty()) {
			logger.warn("... but target SDN controllers list is empty. Done.");
			return;
		}
		
		for(SDNController controller : controllers)
			sdnClient.distribute(controller, cVector, rVector);
		logger.info("... executed.");
	}
	
	/**
	 * Method uses {@link SboxSdnClient} to initialize selected SDN controllers
	 * with configuration data.
	 * 
	 * @param data
	 *            configuration data including information about inter-DC
	 *            communications
	 */
	public void initializeSDNController(ConfigData data) {
		logger.debug("Received request to update ConfigData ...");
		if (controllers == null || controllers.isEmpty()) {
			logger.debug("... but target SDN controllers list is empty. Done.");
			return;
		}
		
		for(SDNController controller : controllers)
			sdnClient.configure(controller, data);
		logger.debug("... executed.");
	}
	
}
