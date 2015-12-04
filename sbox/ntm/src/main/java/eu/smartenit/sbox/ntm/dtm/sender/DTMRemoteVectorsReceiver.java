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
package eu.smartenit.sbox.ntm.dtm.sender;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.db.dto.SDNController;
import eu.smartenit.sbox.db.dto.Vector;

/**
 * Implements DTM external interface methods that are used by inter-SBox
 * communication server side component to update information about reference and
 * compensation vectors calculated in remote SBoxes.
 * 
 * @author Lukasz Lopatowski
 * @version 1.2
 * 
 */
public class DTMRemoteVectorsReceiver {
	
	private static final Logger logger = LoggerFactory.getLogger(DTMRemoteVectorsReceiver.class);
	private Map<Integer, RVector> asRVectors = new HashMap<Integer, RVector>();
	
	private SDNControllerContainer controllers;
	private ThetaCoefficientHandler tch;
	
	public DTMRemoteVectorsReceiver() {
		tch = new ThetaCoefficientHandler();
		controllers = new SDNControllerContainer();
	}

	/**
	 * Method that should be invoked during component initialization to load
	 * required data from the database, populate internal structures and
	 * initialize all SDN Controllers.
	 */
	public void initialize() {
		logger.info("Initializing ...");
		controllers.populateControllersFromDB();
		configureAllControllers();
		logger.info("... initialization complete.");
	}

	/**
	 * DTM external interface method used to update information about
	 * compensation vector calculated every reporting period in remote SBox.
	 * 
	 * @param cVector
	 *            compensation vector calculated in remote SBox
	 * @throws IllegalArgumentException
	 *             when given cVector argument is invalid
	 */
	public void receive(CVector cVector) throws IllegalArgumentException {
		validate(cVector);
		
		logger.info("Received new C vector from AS {}", cVector.getSourceAsNumber());
		CVector updatedCVector = cVector;
		if (asRVectors.get(cVector.getSourceAsNumber()) != null) {
			updatedCVector = tch.normalizeCVector(cVector, asRVectors.get(cVector.getSourceAsNumber()));
		}
		
		logger.info("Forwarding new vectors to appropriate SDN controllers.");
		SDNConfigPusher configPusher = new SDNConfigPusher(controllers.getControllersByRemoteASNumber(cVector.getSourceAsNumber()));
		configPusher.updateVectorInfo(updatedCVector);
	}

	/**
	 * DTM external interface method used to update information about
	 * compensation and reference vectors calculated every reporting and
	 * accounting period, respectively in remote SBox.
	 * 
	 * @param cVector
	 *            compensation vector calculated in remote SBox
	 * @param rVector
	 *            reference vector calculated in remote SBox
	 * @throws IllegalArgumentException
	 *             when either of given vector arguments is invalid
	 */
	public void receive(CVector cVector, RVector rVector) throws IllegalArgumentException {
		validate(cVector, rVector);
		
		logger.info("Received new C and R vectors from AS {}", cVector.getSourceAsNumber());
		asRVectors.put(rVector.getSourceAsNumber(), rVector);
		CVector updatedCVector = tch.normalizeCVector(cVector, asRVectors.get(cVector.getSourceAsNumber()));
		
		logger.info("Forwarding new vectors to appropriate SDN controllers.");
		SDNConfigPusher configPusher = new SDNConfigPusher(controllers.getControllersByRemoteASNumber(rVector.getSourceAsNumber()));
		configPusher.updateVectorInfo(updatedCVector, rVector);
	}

	private void configureAllControllers() {
		logger.info("Configuring local SDN controllers at startup ...");
		for(SDNController controller : controllers.getAllControllers()) {
			SDNConfigPusher configPusher = new SDNConfigPusher(controller);
			configPusher.initializeSDNController(SDNControllerConfigBuilder.prepareConfig(controller));
		}
		logger.info("... done");
	}
	
	private void validate(Vector... vectors) throws IllegalArgumentException {
		logger.debug("Validating received vector objects ...");
		for(Vector vector : vectors) {
			if (vector == null)
				throw new IllegalArgumentException("Vector is null");
			if (vector.getSourceAsNumber() == 0)
				throw new IllegalArgumentException("Source AS number not set in " + vector.getClass());
			if (vector.getVectorValues() == null)
				throw new IllegalArgumentException("Vector values not set in " + vector.getClass());
		}
		logger.debug("... vectors validated successfuly.");
	}

}
