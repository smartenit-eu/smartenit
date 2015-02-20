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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.smartenit.sbox.commons.SBoxProperties;
import eu.smartenit.sbox.commons.SBoxThreadHandler;
import eu.smartenit.sbox.db.dto.ChargingRule;
import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.LocalVector;
import eu.smartenit.sbox.db.dto.LocalVectorValue;
import eu.smartenit.sbox.db.dto.SystemControlParameters;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.ntm.dtm.DAOFactory;

/**
 * Implements DTM external interface methods that are used by QoS Analyzer and
 * Economic Analyzer to update information about link traffic and reference
 * vectors calculated every reporting and accounting period, respectively.
 * 
 * @author Lukasz Lopatowski
 * @version 3.0
 * 
 */
public class DTMTrafficManager {

	private static final Logger logger = LoggerFactory.getLogger(DTMTrafficManager.class);
	
	private int updateCount;
	private VectorsContainer vectors = new VectorsContainer();
	private RemoteSBoxContainer sboxes = new RemoteSBoxContainer();
	
	/**
	 * Method that should be invoked during component initialization to load
	 * required data from the database and populate internal structures.
	 */
	public void initialize() {
		logger.info("Initializing ...");
		updateCount = 0;
		CVectorUpdateController.activate();
		sboxes.populateFromDB();
		loadDefaultRefVectorFromFile();
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
		updateCount++;
		CVectorUpdateController.getInstance().increaseCount();
		validate(xVector);
		
		logger.info("Received new XVector for AS {}", xVector.getSourceAsNumber());
		vectors.accumulateAndStoreUpdatedXVectorValues(xVector);
		
		if (checkXVectorResetRequired()) {
			logger.debug("Reseting link traffic vector values for AS number {} after sampling period end.", xVector.getSourceAsNumber());
			vectors.resetXVectorValues(xVector.getSourceAsNumber());
		}
		
		XRVectorPair updatedPair = vectors.loadCurrentVectorPair(xVector.getSourceAsNumber());
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
		updateCount = 0;
		validate(rVector);
		
		logger.info("Received new RVector for AS {}", rVector.getSourceAsNumber());
		logger.debug("Reseting link traffic vector values for AS number {} after accounting period end.", rVector.getSourceAsNumber());
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
	
	private boolean checkXVectorResetRequired() {
		SystemControlParameters scp = DAOFactory.getSCPDAOInstance().findLast();
		TimeScheduleParameters tsp = DAOFactory.getTSPDAOInstance().findLast();
		
		if (scp.getChargingRule().equals(ChargingRule.the95thPercentile)) {
			if (updateCount % (int) (tsp.getSamplingPeriod() / tsp.getReportPeriodDTM()) == 0)
				return true;
		}
		return false;
	}
	
	/**
	 * Loads serialized reference vector from file. If the file exists a pair of
	 * reference and compensation vectors is sent to all remote SBoxes.
	 */
	private void loadDefaultRefVectorFromFile() {
		ObjectMapper mapper = new ObjectMapper();
		StringBuilder sb = new StringBuilder();
		
		try {
			logger.info("Trying to local default reference vector from " + Paths.get(SBoxProperties.DEFAULT_REF_VECTOR_FILE).toAbsolutePath());
			List<String> allLines = Files.readAllLines(Paths.get(SBoxProperties.DEFAULT_REF_VECTOR_FILE), Charset.defaultCharset());
			for (String line : allLines)
				sb.append(line);

			LocalRVector defaultRVector = mapper.readValue(sb.toString(), LocalRVector.class);

			XVector emptyXVector = new XVector();
			emptyXVector.setSourceAsNumber(defaultRVector.getSourceAsNumber());
			for(LocalVectorValue value : defaultRVector.getVectorValues())
				emptyXVector.addVectorValueForLink(value.getLinkID(), 0);
			
			updateXVector(emptyXVector);
			updateRVector(defaultRVector);
			
			logger.info("Reference vector set during initializtion.");
			logger.debug(((LocalVector)defaultRVector).toString());

		} catch (JsonMappingException e) {
			logger.debug(e.toString());
			logger.info("Reference vector will not be set during initializtion.");
			
		} catch (IOException e) {
			logger.info("File with default reference vector does not exist.");
			logger.info("Reference vector will not be set during initializtion.");
		}
	}
	
	void setSBoxContainer(RemoteSBoxContainer sboxes) {
		this.sboxes = sboxes;
	}

}
