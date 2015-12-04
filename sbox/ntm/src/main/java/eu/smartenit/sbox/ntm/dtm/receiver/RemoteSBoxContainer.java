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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.SBox;
import eu.smartenit.sbox.ntm.dtm.DAOFactory;

/**
 * Stores {@link SBox} objects representing SBoxes in remote ASs from which
 * traffic is sent to the local AS. Those SBoxes are grouped on per local AS
 * number basis.
 * 
 * @author Lukasz Lopatowski
 * @version 3.0
 * 
 */
public class RemoteSBoxContainer {
	private static final Logger logger = LoggerFactory.getLogger(RemoteSBoxContainer.class);
	
	private Map<Integer, List<SBox>> asRemoteSBoxes = new HashMap<Integer, List<SBox>>();
	
	/**
	 * Method populates structure containing SBoxes grouped on per local AS
	 * basis with data loaded from data base.
	 */
	public void populateFromDB() {
		logger.debug("Populating internal structures ...");
		List<AS> localASs = getAllLocalASsFromDB();
		List<DC2DCCommunication> communications = getAllDC2DCCommunicationsFromDB();
		for(AS localAS : localASs) {
			asRemoteSBoxes.put(localAS.getAsNumber(), getRemoteSBoxes(localAS, communications));
		}
		if (asRemoteSBoxes.keySet().isEmpty()) 
			logger.warn("No local ASs loaded from DB. This is not correct!");
		else 
			logger.debug("Loaded {} local ASs.", asRemoteSBoxes.keySet().size());
	}

	/**
	 * Method returns list of remote SBoxes (with no duplicates) for given AS
	 * obtained based on existing inter-DC communications.
	 * 
	 * @param asNumber
	 *            local AS number
	 * @return list of SBoxes that should be notified each time new compensation
	 *         and/or reference vectors are calculated for given AS
	 */
	public List<SBox> getRemoteSBoxes(int asNumber) {
		return asRemoteSBoxes.get(asNumber);
	}
	
	private List<AS> getAllLocalASsFromDB() {
		List<AS> localASs = new ArrayList<AS>();
		for(AS as : DAOFactory.getASDAOInstance().findAllAsesCloudsBgRoutersLinks()){
			if (as.isLocal()) {
				localASs.add(as);
			}
		}
		return localASs;
	}
	
	private List<DC2DCCommunication> getAllDC2DCCommunicationsFromDB() {
		return DAOFactory.getDCDC2DCComDAOInstance().findAllDC2DCCommunicationsCloudsTunnels();
	}

	private List<SBox> getRemoteSBoxes(AS localAS, List<DC2DCCommunication> communications) {
		List<SBox> sboxes = new ArrayList<SBox>();
		for(DC2DCCommunication communication : communications) {
			if (isLocalASInGivenCommunication(localAS, communication)) {
				SBox remoteSBox = communication.getRemoteCloud().getAs().getSbox();
				addToListWithNoDuplicates(remoteSBox, sboxes);
			}
		}
		return sboxes;
	}
	
	private boolean isLocalASInGivenCommunication(AS localAS, DC2DCCommunication communication) {
		return communication.getLocalCloud().getAs().getAsNumber() == localAS.getAsNumber();
	}
	
	private void addToListWithNoDuplicates(SBox sbox, List<SBox> sboxes) {
		for (SBox sBoxOnTheList : sboxes) {
			if (sBoxOnTheList.getManagementAddress().getPrefix().equals(sbox.getManagementAddress().getPrefix()))
				return;
		}
		sboxes.add(sbox);
	}

}
