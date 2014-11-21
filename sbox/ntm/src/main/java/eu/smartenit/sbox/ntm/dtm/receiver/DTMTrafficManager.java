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
package eu.smartenit.sbox.ntm.dtm.receiver;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.commons.SBoxThreadHandler;
import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.LocalVector;
import eu.smartenit.sbox.db.dto.XVector;

/**
 * Implements DTM external interface methods that are used by QoS Analyzer and
 * Economic Analyzer to update information about link traffic and reference
 * vectors calculated every reporting and accounting period, respectively.
 * 
 * @author Lukasz Lopatowski
 * @version 1.2
 * 
 */
public class DTMTrafficManager {

	private static final Logger logger = LoggerFactory.getLogger(DTMTrafficManager.class);
	
	private VectorsContainer vectors = new VectorsContainer();
	private RemoteSBoxContainer sboxes = new RemoteSBoxContainer();
	
	/**
	 * Method that should be invoked during component initialization to load
	 * required data from the database and populate internal structures.
	 */
	public void initialize() {
		logger.info("Initializing ...");
		sboxes.populateFromDB();
		logger.info("... initialization complete.");
	}
	
	/**
	 * DTM external interface method used to update information about link
	 * traffic vector calculated every reporting period. Traffic vector values
	 * are accumulated from the beginning of accounting period (reception of
	 * updated R vector). Triggers subsequent actions in a new thread.
	 * 
	 * @param xVector
	 *            new link traffic vector
	 * @throws IllegalArgumentException
	 *             when given xVector argument is invalid
	 */
	public void updateXVector(XVector xVector) throws IllegalArgumentException {
		validate(xVector);
		
		logger.info("Received new XVector for AS {}", xVector.getSourceAsNumber());
		XRVectorPair updatedPair = vectors.accumulateAndStoreUpdatedXVectorValues(xVector);
		if (!updatedPair.areBothVectorsSet())
			return;
		calculateCVectorAndUpdateRemoteSBoxes(
				new CVectorProcessingThread(updatedPair, sboxes.getRemoteSBoxes(updatedPair.getAsNumber())));
	}
	
	/**
	 * DTM external interface method used to update information about reference
	 * vector calculated every accounting period. Link traffic vector value for
	 * given AS is reseted. Triggers subsequent actions in a new thread.
	 * 
	 * @param rVector
	 *            new reference vector
	 * @throws IllegalArgumentException
	 *             when given rVector argument is invalid
	 */
	public void updateRVector(LocalRVector rVector) throws IllegalArgumentException {
		validate(rVector);
		
		logger.info("Received new RVector for AS {}", rVector.getSourceAsNumber());
		vectors.resetXVectorValues(rVector.getSourceAsNumber());
		XRVectorPair updatedPair = vectors.storeUpdatedRVector(rVector);
		if (!updatedPair.areBothVectorsSet()) 
			return;
		calculateCVectorAndUpdateRemoteSBoxes(
				new CRVectorProcessingThread(updatedPair, sboxes.getRemoteSBoxes(updatedPair.getAsNumber())));
	}
	
	private void validate(LocalVector vector) throws IllegalArgumentException {
		logger.debug("Validating received vector object ...");
		if (vector == null)
			throw new IllegalArgumentException("Vector is null");
		if (vector.getSourceAsNumber() == 0)
			throw new IllegalArgumentException("Source AS number not set in " + vector.getClass());
		if (vector.getVectorValues() == null)
			throw new IllegalArgumentException("Vector values not set in " + vector.getClass());
		logger.debug("... vector validated successfuly.");
	}
	
	private void calculateCVectorAndUpdateRemoteSBoxes(VectorProcessingThread thread) {
		logger.debug("Launching new vector compensation thread.");
		SBoxThreadHandler.getThreadService().schedule(thread, 0, TimeUnit.SECONDS);
	}
	
	void setSBoxContainer(RemoteSBoxContainer sboxes) {
		this.sboxes = sboxes;
	}

}
