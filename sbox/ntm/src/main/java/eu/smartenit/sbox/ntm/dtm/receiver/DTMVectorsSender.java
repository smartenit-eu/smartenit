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
package eu.smartenit.sbox.ntm.dtm.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.commons.SBoxProperties;
import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.db.dto.SBox;
import eu.smartenit.sbox.interfaces.intersbox.client.InterSBoxClient;

/**
 * Implements methods that forward requests for inter-SBox vector information
 * update to the {@link InterSBoxClient} component.
 * 
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public class DTMVectorsSender {
	
	private static final Logger logger = LoggerFactory.getLogger(DTMVectorsSender.class);
	private InterSBoxClient client = InterSBoxClientFactory.getInstance();

	/**
	 * Method calls proper method of the {@link InterSBoxClient} to send
	 * information about updated compensation vector to given remote SBox.
	 * 
	 * @param sbox
	 *            target {@link SBox} instance
	 * @param cVector
	 *            compensation vector to be sent
	 */
	public void send(SBox sbox, CVector cVector) {
		 try {
			 logger.debug("Sending vector to SBox with address {} ...", sbox.getManagementAddress().getPrefix());
			 client.send(sbox.getManagementAddress().getPrefix(), SBoxProperties.INTER_SBOX_PORT, cVector);
			 logger.debug("... done.");
			 
		 } catch (Exception e) {
			logger.warn("Exception caught while sending C vector to remote SBox with description: {}", e.getMessage());
		}
	}

	/**
	 * Method calls proper method of the {@link InterSBoxClient} to send
	 * information about updated compensation and reference vectors to given
	 * remote SBox.
	 * 
	 * @param sbox
	 *            target {@link SBox} instance
	 * @param cVector
	 *            compensation vector to be sent
	 * @param rVector
	 *            reference vector to be sent
	 */
	public void send(SBox sbox, CVector cVector, RVector rVector) {
		try {
			logger.debug("Sending vectors to SBox with address {} ...", sbox.getManagementAddress().getPrefix());
			client.send(sbox.getManagementAddress().getPrefix(), SBoxProperties.INTER_SBOX_PORT, cVector, rVector);
			logger.debug("... done.");
			
		 } catch (Exception e) {
			logger.warn("Exception caught while sending C and R vectors to remote SBox with description: {}", e.getMessage());
		 }
	}
	
}
