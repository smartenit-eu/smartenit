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
package eu.smartenit.sbox.qoa;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.DC2DCCommunicationID;
import eu.smartenit.sbox.db.dto.LinkID;
import eu.smartenit.sbox.db.dto.SimpleTunnelID;
import eu.smartenit.sbox.db.dto.Tunnel;
import eu.smartenit.sbox.db.dto.TunnelID;
import eu.smartenit.sbox.db.dto.VectorValue;
import eu.smartenit.sbox.db.dto.ZVector;

/**
 * Implements tunnel traffic vector ({@link ZVector}) calculation based on
 * current counter values collected from tunnels in given AS and previous values
 * of those counters.
 * 
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public class ZVectorCalculator extends VectorCalculator {
	private static final Logger logger = LoggerFactory.getLogger(ZVectorCalculator.class);
	
	private MonitoredTunnelsInventory monitoredTunnels;
	
	/**
	 * The constructor with arguments.
	 * 
	 * @param monitoredTunnels
	 *            additional data required during tunnel traffic vectors
	 *            calculation
	 */
	public ZVectorCalculator(MonitoredTunnelsInventory monitoredTunnels) {
		this.monitoredTunnels = monitoredTunnels;
	}

	/**
	 * Method calculates new set of tunnel traffic vectors for given AS. Each
	 * {@link ZVector} object corresponds to a single {@link DC2DCCommunication}.
	 * It should be launched after each reporting period.
	 * 
	 * @param asNumber
	 *            number of the AS for which new set of vectors is calculated
	 * @param values
	 *            counter values from all monitored tunnels in given AS
	 *            represented by {@link CounterValues}
	 * @return new set of tunnel traffic vectors ({@link ZVector}) for given AS
	 */
	public List<ZVector> calculateZVectors(int asNumber, CounterValues values) {
		CounterValues latestCounterValues = getOrCreateLatestVectorValues(asNumber);
		return calculateZVectors(values, latestCounterValues, asNumber);
	}

	private List<ZVector> calculateZVectors(CounterValues values, CounterValues latestCounterValues, int asNumber) {
		if (values == null) return null;
		
		logger.debug("Calculating Z vectors for AS {} ...", asNumber);
		List<ZVector> newZVectors = new ArrayList<ZVector>();
		for (DC2DCCommunicationID id : monitoredTunnels.getAllDC2DCCommunicationIDs(asNumber)) {
			List<VectorValue> vectorValues = calculateVectorValues(filterCounterValues(values, id), latestCounterValues, asNumber);
			newZVectors.add(new ZVector(vectorValues, asNumber, id));
		}
		logger.debug("... done.");
		return newZVectors;
	}

	private CounterValues filterCounterValues(CounterValues allValues, DC2DCCommunicationID id) {
		CounterValues filteredValues = new CounterValues();
		for (Tunnel tunnel : monitoredTunnels.getTunnels(id)) {
			filteredValues.storeCounterValue(tunnel.getTunnelID(), allValues.getCounterValue(tunnel.getTunnelID()));
		}
		return filteredValues;
	}

	private List<VectorValue> calculateVectorValues(CounterValues values, CounterValues latestCounterValues, int asNumber) {
		List<VectorValue> vectorValues = new ArrayList<VectorValue>();
		for (TunnelID tunnelID : values.getAllTunnelsIds()) {
			long currentCounterValue = values.getCounterValue(tunnelID);
			long periodCounterValue = currentCounterValue;
			
			if (latestCounterValues.getCounterValue(tunnelID) != null)
				periodCounterValue -= latestCounterValues.getCounterValue(tunnelID);
		
			if (periodCounterValue < 0) {
				logger.warn("Calculated period counter value for tunnel is less than zero. " +
						"This is incorrect. Will set it to zero. Affected tunnel: {}", ((SimpleTunnelID)tunnelID).toString());
				periodCounterValue = 0;
			}
			latestCounterValues.storeCounterValue(tunnelID, currentCounterValue);
			updateVectorValues(vectorValues, monitoredTunnels.getTunnel(tunnelID, asNumber), periodCounterValue);
		}
		return vectorValues;
	}

	private void updateVectorValues(List<VectorValue> vectorValues, Tunnel tunnel, long periodCounterValue) {
		if (tunnel == null) {
			logger.warn("Tunnel is null.");
			return;
		}
		
		LinkID linkID = tunnel.getLink().getLinkID();
		for (VectorValue vectorValue : vectorValues) {
			if (vectorValue.getLinkID().equals(linkID)) {
				vectorValue.setValue(vectorValue.getValue() + periodCounterValue);
				return;
			}
		}
		vectorValues.add(new VectorValue(periodCounterValue, linkID));		
	}

}
