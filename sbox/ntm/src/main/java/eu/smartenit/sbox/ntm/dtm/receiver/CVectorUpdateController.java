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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.ntm.dtm.DAOFactory;

/**
 * Implements compensation vector updates handling logic.
 * 
 * @author Lukasz Lopatowski
 * @version 3.0
 * 
 */
public class CVectorUpdateController {

	private static final Logger logger = LoggerFactory.getLogger(CVectorUpdateController.class);
	
	private static CVectorUpdateController instance;
	private static boolean isActivated = false;
	
	public static CVectorUpdateController getInstance() {
		if (instance == null)
			instance = new CVectorUpdateController();
		return instance;
	}
	
	public static void activate() {
		isActivated = true;
	}
	
	public static void deactivate() {
		isActivated = false;
	}
	
	private Map<Integer,CVector> asPreviousCVectors = new HashMap<>();
	private int updateCount = 0;
	
	private CVectorUpdateController() {}

	public void increaseCount() {
		updateCount++;
	}
	
	public void sent(CVector cVector) {
		asPreviousCVectors.put(cVector.getSourceAsNumber(), cVector);
	}
	
	public void reset() {
		asPreviousCVectors = new HashMap<>();
		updateCount = 0;
	}

	public synchronized boolean updateRequired(CVector cVector) {
		if(!isActivated)
			return true;
		
		if (checkCompensationPeriodElapsed() || checkCompensationThresholdExceeded(cVector)) {
			asPreviousCVectors.put(cVector.getSourceAsNumber(), cVector);
			return true;
		}
		
		asPreviousCVectors.put(cVector.getSourceAsNumber(), cVector);
		return false;
	}
	
	private boolean checkCompensationPeriodElapsed() {
		if (updateCount % (int) (DAOFactory.getTSPDAOInstance().findLast().getCompensationPeriod()
				/ DAOFactory.getTSPDAOInstance().findLast().getReportPeriodDTM()) == 0)
			return true;
		
		return false;
	}
	
	private boolean checkCompensationThresholdExceeded(CVector cVector) {
		if (asPreviousCVectors.get(cVector.getSourceAsNumber()) == null)
			return true;
		
		NetworkAddressIPv4 prefix = cVector.getVectorValues().get(0).getTunnelEndPrefix();
		double value = (double) cVector.getVectorValueForTunnelEndPrefix(prefix) 
				/ asPreviousCVectors.get(cVector.getSourceAsNumber()).getVectorValueForTunnelEndPrefix(prefix) * -1; 
		
		if (value > DAOFactory.getSCPDAOInstance().findLast().getCompensationThreshold())
			return true;
		
		logger.debug("Compensation value/threshold : " + value + "/" + DAOFactory.getSCPDAOInstance().findLast().getCompensationThreshold());
		return false;
	}
	
}
