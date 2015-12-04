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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.Direction;
import eu.smartenit.sbox.db.dto.SDNController;
import eu.smartenit.sbox.ntm.dtm.DAOFactory;

/**
 * Stores information about {@link SDNController}s that manage DA routers on
 * which tunnels were configured towards AS with given AS number.
 * 
 * @author Lukasz Lopatowski
 * @version 3.0
 * 
 */
public class SDNControllerContainer {
	private static final Logger logger = LoggerFactory.getLogger(SDNControllerContainer.class);
	
	private Map<Integer, Set<SDNController>> remoteASControllers = new HashMap<>();
	
	/**
	 * Used to initialize internal structures based on data stored in data base.
	 */
	public void populateControllersFromDB() {
		logger.debug("Populating internal structures ...");
		for (DC2DCCommunication communication : getAllCommunicationsFromDB()) {
			if (communication.getTrafficDirection().equals(Direction.outcomingTraffic))
				updateControllers(communication);
		}
		logger.debug("... completed.");
	}

	/**
	 * Returns a list of {@link SDNController}s that manage DA routers with
	 * tunnels towards given remote AS.
	 * 
	 * @param asNumber
	 *            remote AS number
	 * @return list of controllers that manage tunnels towards given AS
	 */
	public Set<SDNController> getControllersByRemoteASNumber(int asNumber) {
		return remoteASControllers.get(asNumber);
	}

	/**
	 * Returns a list of all {@link SDNController}s in local ASs.
	 * 
	 * @return list of controllers
	 */
	public Set<SDNController> getAllControllers() {
		Set<SDNController> allControllers = new HashSet<>();
		for(Integer asNumber : remoteASControllers.keySet())
			allControllers.addAll(remoteASControllers.get(asNumber));
		return allControllers;
	}
	
	private List<DC2DCCommunication> getAllCommunicationsFromDB() {
		return DAOFactory.getDCDC2DCComDAOInstance().findAllDC2DCCommunicationsCloudsTunnels();
	}

	private void updateControllers(DC2DCCommunication communication) {
		int asNumber = communication.getRemoteCloud().getAs().getAsNumber();
		SDNController controller = communication.getLocalCloud().getSdnController();
		if (remoteASControllers.containsKey(asNumber))
			updateExistingEntry(asNumber, controller);
		else
			createAndPutNewEntry(asNumber, controller);
	}
	
	private void createAndPutNewEntry(int asNumber, SDNController controller) {
		remoteASControllers.put(asNumber, new HashSet<SDNController>(Arrays.asList(controller)));
	}

	private void updateExistingEntry(int asNumber, SDNController controller) {
		remoteASControllers.get(asNumber).add(controller);
	}
	
}
