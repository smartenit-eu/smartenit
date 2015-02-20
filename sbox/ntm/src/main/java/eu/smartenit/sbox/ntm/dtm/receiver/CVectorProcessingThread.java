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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.SBox;
import eu.smartenit.sbox.db.dto.XVector;

/**
 * Implements new compensation vector calculation after new link traffic vector
 * is received followed by sending compensation vector to remote SBoxes. Those
 * actions are performed in separate thread.
 * 
 * @author Lukasz Lopatowski
 * @version 1.2
 * 
 */
public class CVectorProcessingThread extends VectorProcessingThread {
	
	private static final Logger logger = LoggerFactory.getLogger(CVectorProcessingThread.class);

	/**
	 * The constructor with arguments.
	 * 
	 * @param xVector
	 *            link traffic vector to be used in compensation vector
	 *            calculation
	 * @param rVector
	 *            reference vector to be used in compensation vector calculation
	 * @param remoteSboxes
	 *            list of target {@link SBox} which should be updated with
	 *            calculated compensation vector
	 */
	public CVectorProcessingThread(XVector xVector, LocalRVector rVector, List<SBox> remoteSboxes) {
		super(xVector, rVector, remoteSboxes);
	}
	
	/**
	 * The constructor with arguments.
	 * 
	 * @param vectorPair
	 *            contains vectors that should be used in compensation vector
	 *            calculation
	 * @param remoteSboxes
	 *            list of target {@link SBox} which should be updated with
	 *            calculated compensation vector
	 */
	public CVectorProcessingThread(XRVectorPair vectorPair, List<SBox> remoteSboxes) {
		super(vectorPair.getXVector(), vectorPair.getRVector(), remoteSboxes);
	}

	/**
	 * Method launched when thread is started. Constructs compensation vector
	 * with {@link CVectorConstructor} and sends it to remote {@link SBox}es
	 * using instance of {@link DTMVectorsSender}.
	 */
	public void run() {
		logger.info("Running CVector calculation thread.");
		try { 
			CVector cVector = new CVectorConstructor().construct(xVector, rVector);
			CVectorHistory.storeInHistory(cVector);
			
			if (!CVectorUpdateController.getInstance().updateRequired(cVector)) {
				logger.info("CVector update not required. Will not send any updates.");
				return;
			}
			
			if (remoteSboxes == null || remoteSboxes.size() == 0)
				logger.warn("List of remote SBoxes is null or empty. Will not send any updates.");
			for (SBox remoteSbox : remoteSboxes) {
				logger.info("Sending CVector to remote SBox: {}", remoteSbox.getManagementAddress().getPrefix());
				sender.send(remoteSbox, cVector);
			}
		} catch (IllegalArgumentException e) {
			logger.error("Compensation vector was not constructed properly: {}", e.getMessage());
		}
	}

}
