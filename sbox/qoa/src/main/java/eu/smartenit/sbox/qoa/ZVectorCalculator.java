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
import eu.smartenit.sbox.db.dto.EndAddressPairTunnelID;
import eu.smartenit.sbox.db.dto.LinkID;
import eu.smartenit.sbox.db.dto.LocalVectorValue;
import eu.smartenit.sbox.db.dto.Tunnel;
import eu.smartenit.sbox.db.dto.TunnelID;
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
	 * @param lastValues
	 *            last counter values from all monitored tunnels in given AS
	 *            represented by {@link CounterValues}
	 * @param newValues
	 *            new counter values from all monitored tunnels in given AS
	 *            represented by {@link CounterValues}
	 * @return new set of tunnel traffic vectors ({@link ZVector}) for given AS
	 */
	public List<ZVector> calculateZVectors(int asNumber, CounterValues lastValues, CounterValues newValues) {
		CounterValues latestCounterValues = null;
		if(lastValues == null)
			latestCounterValues = getOrCreateLatestVectorValues(asNumber);
		else
			latestCounterValues = lastValues;
		return calculateZVectors(newValues, latestCounterValues, asNumber);
	}

	private List<ZVector> calculateZVectors(CounterValues values, CounterValues latestCounterValues, int asNumber) {
		if (values == null) return null;
		
		logger.debug("Calculating Z vectors for AS {} ...", asNumber);
		List<ZVector> newZVectors = new ArrayList<ZVector>();
		ZVector zVector;
		for (DC2DCCommunicationID id : monitoredTunnels.getAllDC2DCCommunicationIDs(asNumber)) {
			List<LocalVectorValue> localVectorValues = calculateLocalVectorValues(filterCounterValues(values, id), latestCounterValues, asNumber);
			zVector = new ZVector(localVectorValues, asNumber);
			zVector.setCommunicationID(id);
			newZVectors.add(zVector);
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

	private List<LocalVectorValue> calculateLocalVectorValues(CounterValues values, CounterValues latestCounterValues, int asNumber) {
		List<LocalVectorValue> localVectorValues = new ArrayList<LocalVectorValue>();
		for (TunnelID tunnelID : values.getAllTunnelsIds()) {
			long currentCounterValue = values.getCounterValue(tunnelID);
			long periodCounterValue = currentCounterValue;
			
			if (latestCounterValues.getCounterValue(tunnelID) != null)
				periodCounterValue -= latestCounterValues.getCounterValue(tunnelID);
		
			if (periodCounterValue < 0) {
				logger.warn("Calculated period counter value for tunnel is less than zero. " +
						"This is incorrect. Will set it to zero. Affected tunnel: {}", ((EndAddressPairTunnelID)tunnelID).toString());
				periodCounterValue = 0;
			}
			latestCounterValues.storeCounterValue(tunnelID, currentCounterValue);
			updateVectorValues(localVectorValues, monitoredTunnels.getTunnel(tunnelID, asNumber), periodCounterValue);
		}
		return localVectorValues;
	}

	private void updateVectorValues(List<LocalVectorValue> localVectorValues, Tunnel tunnel, long periodCounterValue) {
		if (tunnel == null) {
			logger.warn("Tunnel is null.");
			return;
		}
		
		LinkID linkID = tunnel.getLink().getLinkID();
		for (LocalVectorValue localVectorValue : localVectorValues) {
			if (localVectorValue.getLinkID().equals(linkID)) {
				localVectorValue.setValue(localVectorValue.getValue() + periodCounterValue);
				return;
			}
		}
		localVectorValues.add(new LocalVectorValue(periodCounterValue, linkID));		
	}

}
