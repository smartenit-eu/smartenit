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

import java.util.List;

import eu.smartenit.sbox.db.dto.LocalVectorValue;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.db.dto.ZVector;

/**
 * Used to handle monitoring data (i.e. counter values) and calculating link
 * traffic vector ({@link XVector}) and tunnel traffic vectors ({@link ZVector})
 * by means of {@link XVectorCalculator} and {@link ZVectorCalculator},
 * respectively.
 * 
 * @author Jakub Gutkowski
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public class MonitoringDataProcessor {
	
	private XVectorCalculator xVectorCalculator;
	private ZVectorCalculator zVectorCalculator;
	
	/**
	 * The constructor with arguments.
	 * 
	 * @param monitoredLinks
	 *            all monitored links
	 * @param monitoredTunnels
	 *            all monitored tunnels
	 */
	public MonitoringDataProcessor(MonitoredLinksInventory monitoredLinks, MonitoredTunnelsInventory monitoredTunnels) {
		xVectorCalculator = new XVectorCalculator();
		zVectorCalculator = new ZVectorCalculator(monitoredTunnels);
	}

	/**
	 * Calculates link traffic vector by means of {@link XVectorCalculator}.
	 * 
	 * @param asNumber
	 *            number of the AS for which link traffic vector calculation
	 *            should be performed
	 * @param lastValues
	 *            last counter values
	 * @param newValues
	 *            counter values collected from inter-domain links of given AS
	 *            to be used in link traffic vector calculation
	 * @return calculated {@link XVector}
	 */
	public XVector calculateXVector(int asNumber, CounterValues lastValues, CounterValues newValues) {
		return xVectorCalculator.calculateXVector(asNumber, lastValues, newValues);
	}

	/**
	 * Calculates tunnel traffic vectors by means of {@link ZVectorCalculator}.
	 * 
	 * @param asNumber
	 *            number of the AS for which tunnel traffic vectors calculation
	 *            should be performed
	 * @param lastValues
	 *            last counter values
	 * @param newValues
	 *            counter values collected from tunnels in given AS to be used
	 *            in tunnel traffic vectors calculation
	 * @return list of calculated {@link ZVector}s
	 */
	public List<ZVector> calculateZVectors(int asNumber, CounterValues lastValues, CounterValues newValues) {
		return zVectorCalculator.calculateZVectors(asNumber, lastValues, newValues);
	}

	/**
	 * Aggregates vector values from given tunnel traffic vectors and returns a
	 * single Z vector.
	 * 
	 * @param zVectors
	 *            list of Z vectors to be aggregated
	 * @return new Z vector
	 */
	public ZVector aggregateZVectors(List<ZVector> zVectors) {
		CounterValues counterValues = new CounterValues();
		for(ZVector zVector : zVectors) {
			for (LocalVectorValue vectorValue : zVector.getVectorValues()) {
				counterValues.addCounterValue(vectorValue.getLinkID(), vectorValue.getValue());
			}
		}
		return new ZVector(counterValues.toLocalVectorValues(), zVectors.get(0).getSourceAsNumber());
	}
	
}
