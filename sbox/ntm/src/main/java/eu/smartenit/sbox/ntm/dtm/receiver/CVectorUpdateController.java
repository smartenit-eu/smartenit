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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.LinkID;
import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;
import eu.smartenit.sbox.db.dto.VectorValue;
import eu.smartenit.sbox.ntm.dtm.DAOFactory;

/**
 * Implements compensation vector updates handling logic. If controller is
 * activated, compensation vector updates are sent to remote SBoxes only if
 * required and not necessarily every time new vector is calculated. Updates are
 * sent periodically according to the value of {@link TimeScheduleParameters}
 * <code>.compensationPeriod</code> as well as if the current vector differs
 * significantly from the one previously sent.
 * 
 * Moreover, if certain conditions are met, i.e. the reference vector on
 * specific links is already achieved in the current accounting period, the
 * controller itself calculates new compensation vectors according to specific
 * algorithm.
 * 
 * @author Lukasz Lopatowski
 * @version 3.1
 * 
 */
public class CVectorUpdateController {

	private static final Logger logger = LoggerFactory.getLogger(CVectorUpdateController.class);
	
	private static CVectorUpdateController instance;
	private static boolean isActivated = false;
	
	/**
	 * Returns the single existing instance of the controller. If such instance
	 * does not yet exists it is created.
	 * 
	 * @return instance of the controller
	 */
	public static CVectorUpdateController getInstance() {
		if (instance == null)
			instance = new CVectorUpdateController();
		return instance;
	}
	
	/**
	 * Activates the controller.
	 */
	public static void activate() {
		isActivated = true;
	}
	
	/**
	 * Deactivates the controller.
	 */
	public static void deactivate() {
		isActivated = false;
		instance = null;
	}
	
	private Map<Integer,CVector> asPreviousCVectors = new HashMap<>();
	private Map<Integer, List<SimpleLinkID>> asLinksWithRVectorAchieved = new HashMap<>();
	private int updateCount = 0;
	
	private CVectorUpdateController() {}

	/**
	 * Method called after each reporting period.
	 */
	public void increaseCount() {
		updateCount++;
	}
	
	/**
	 * Method called to inform about new compensation vector that was just sent.
	 * This vector is stored as the previous vector for given AS.
	 * 
	 * @param cVector
	 *            compensation vector just sent
	 */
	public void sent(CVector cVector) {
		asPreviousCVectors.put(cVector.getSourceAsNumber(), cVector);
	}
	
	/**
	 * Method called to reset stored values when new accounting period begins.
	 */
	public void reset() {
		asPreviousCVectors = new HashMap<>();
		asLinksWithRVectorAchieved = new HashMap<>();
		updateCount = 0;
	}

	/**
	 * Method checks whether it is required to send an update of given
	 * compensation vector to remote SBoxes. It checks if the compensation
	 * period elapsed or if the values of current compensation vector in
	 * comparison with the one previously sent meet some predefined conditions.
	 * 
	 * @param cVector
	 *            compensation vector to be sent
	 * @return <code>true</code> if given compensation vector should be sent
	 */
	public synchronized boolean updateRequired(CVector cVector) {
		if(!isActivated)
			return true;
		if (checkCompensationPeriodElapsed() || checkCompensationThresholdExceeded(cVector))
			return true;
		
		if (cVector.getVectorValues().get(0).getValue() == 0) {
			// if compensation vector values are zero, 
			// 	vector should be put on the previous vectors list here since it won't be sent
			asPreviousCVectors.put(cVector.getSourceAsNumber(), cVector);
		}
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
		
		long currentCVectorValue = cVector.getVectorValueForTunnelEndPrefix(prefix);
		long previousCVectorValue = asPreviousCVectors.get(cVector.getSourceAsNumber()).getVectorValueForTunnelEndPrefix(prefix);

		if (previousCVectorValue == 0) {
			logger.debug("Compensation vector should be updated since previous value was 0.");
			return true;
		}
		
		double value = (double) currentCVectorValue / previousCVectorValue * -1; 
		
		if (value > DAOFactory.getSCPDAOInstance().findLast().getCompensationThreshold())
			return true;
		
		logger.debug("Compensation value/threshold : " + value + "/" + DAOFactory.getSCPDAOInstance().findLast().getCompensationThreshold());
		return false;
	}

	/**
	 * Checks if there are already some links in given AS for which the R vector
	 * is already achieved. This would mean that the compensation vector for
	 * given AS should be calculated by the {@link CVectorUpdateController}.
	 * 
	 * @param asNumber
	 *            number of the AS in question
	 * @return <code>true</code> if at least one link for given AS has R vector
	 *         achieved
	 */
	public boolean isCVectorPreparedByController(int asNumber) {
		if (isActivated == false)
			return false;
		
		List<SimpleLinkID> links = asLinksWithRVectorAchieved.get(asNumber); 
		if (links != null && links.size() > 0)
			return true;
		return false;
	}

	/**
	 * Calculates and returns proper compensation vector to be used for given
	 * AS. This method should be called if at least one link in given AS has the
	 * reference vector already achieved.
	 * 
	 * @param asNumber
	 *            number of AS
	 * @param rVector
	 *            current reference vector for given AS
	 * @return {@link CVector} object
	 */
	public CVector prepareCVector(int asNumber, LocalRVector rVector) throws IllegalArgumentException {
		validateArguments(asNumber, rVector);
		
		long value1 = 0;
		long value2 = 0;
		SimpleLinkID link1 = (SimpleLinkID)rVector.getVectorValues().get(0).getLinkID();
		SimpleLinkID link2 = (SimpleLinkID)rVector.getVectorValues().get(1).getLinkID();
		List<SimpleLinkID> links = asLinksWithRVectorAchieved.get(asNumber);
		
		if (links.contains(link1) && !links.contains(link2)) {
			value1 = 100 * (rVector.getVectorValueForLink(link1) + rVector.getVectorValueForLink(link2));
			value2 = -1 * value1;
		} else if (links.contains(link2) && !links.contains(link1)) {
			value2 = 100 * (rVector.getVectorValueForLink(link1) + rVector.getVectorValueForLink(link2));
			value1 = -1 * value2;
		} else if (links.contains(link2) && links.contains(link1)) {
			value1 = -1 * asPreviousCVectors.get(asNumber).getVectorValueForTunnelEndPrefix(tunnelEndNetworkPrefix(link1));
			value2 = -1 * asPreviousCVectors.get(asNumber).getVectorValueForTunnelEndPrefix(tunnelEndNetworkPrefix(link2));
		}
		
		VectorValue cVectorValue1 = new VectorValue(value1, tunnelEndNetworkPrefix(link1));
		VectorValue cVectorValue2 = new VectorValue(value2, tunnelEndNetworkPrefix(link2));
		CVector cVector = new CVector(Arrays.asList(cVectorValue1, cVectorValue2), asNumber);
		return cVector;
	}

	private void validateArguments(int asNumber, LocalRVector rVector) throws IllegalArgumentException {
		if (asNumber == 0)
			throw new IllegalArgumentException("Number of AS can not be zero.");
		if (!asLinksWithRVectorAchieved.keySet().contains(asNumber))
			throw new IllegalArgumentException("Given AS not found on the list.");
		if (asLinksWithRVectorAchieved.get(asNumber).size() == 0)
			throw new IllegalArgumentException("The are no links with R vector achieved in this AS.");
		if (rVector == null)
			throw new IllegalArgumentException("Reference vector can not be null.");
		if (rVector.getVectorValues().size() != 2)
			throw new IllegalArgumentException("Reference vector has to have values for exactly two links.");
	}

	/**
	 * Updates the list of links with R vector achieved in given AS.
	 * 
	 * @param asNumber
	 *            number of the AS
	 * @param links
	 *            list of {@link LinkID}s
	 */
	public void updateLinksWithRVectorAchieved(int asNumber, List<LinkID> links) {
		for(LinkID linkID : links) {
			SimpleLinkID simpleLinkID = (SimpleLinkID)linkID;
			List<SimpleLinkID> list = asLinksWithRVectorAchieved.get(asNumber);
			if (list == null) {
				list = new ArrayList<>(Arrays.asList(simpleLinkID));
				asLinksWithRVectorAchieved.put(asNumber, list);
				return;
			}
			if (!list.contains(simpleLinkID)) {
				list.add(simpleLinkID);
			}
		}
	}
	
	private NetworkAddressIPv4 tunnelEndNetworkPrefix(LinkID linkId) {
		Link link = DAOFactory.getLinkDAOInstance().findById((SimpleLinkID)linkId); 
		if (link == null) throw new IllegalArgumentException("Link not found in DB (LinkID: " + linkId.toString() + ")");
		return link.getTunnelEndPrefix();
	}
	
}
