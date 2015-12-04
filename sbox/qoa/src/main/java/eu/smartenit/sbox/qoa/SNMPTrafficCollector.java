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
package eu.smartenit.sbox.qoa;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.commons.SBoxThreadHandler;
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.ChargingRule;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.SystemControlParameters;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.db.dto.ZVector;

/**
 * Class used to schedule traffic counter collection tasks and calculating
 * traffic vectors once all counters are read after each reporting period.
 * Calculated vectors are provided to {@link DTMQosAnalyzer} for further
 * distribution.
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a
 *         href="http://psnc.pl">PSNC</a>)
 * @author <a href="mailto:llopat@man.poznan.pl">Lukasz Lopatowski</a> (<a
 *         href="http://psnc.pl">PSNC</a>)
 * @version 1.0
 * 
 */
public class SNMPTrafficCollector {
	private static final Logger logger = LoggerFactory.getLogger(SNMPTrafficCollector.class);
	
	protected MonitoredLinksInventory monitoredLinks = new MonitoredLinksInventory();
	protected MonitoredTunnelsInventory monitoredTunnels = new MonitoredTunnelsInventory();
	private DTMQosAnalyzer analyzer;
	protected MonitoringDataProcessor monitoringDataProcessor;
	protected SystemControlParameters systemControlParameters;
	protected TimeScheduleParameters timeScheduleParameters;
	protected long numberOfPeriods;
	protected long period;
	protected int reportingIterator = 1;
	protected CounterValues lastReportPeriodEACounterValues = new CounterValues();
	protected Map<Integer, Boolean> firstUpdate;
	
	/**
	 * The constructor with arguments.
	 * 
	 * @param analyzer
	 *            instance of the {@link DTMQosAnalyzer} which should be updated
	 *            with traffic vectors calculated after reporting period
	 */
	public SNMPTrafficCollector(DTMQosAnalyzer analyzer) {
		this.analyzer = analyzer;
		this.firstUpdate = new Hashtable<>();
		this.timeScheduleParameters = loadTimeScheduleParametersFromDB();
		this.systemControlParameters = loadSystemControlParametersFromDB();
		this.period = timeScheduleParameters.getReportingPeriod();
		if(this.systemControlParameters.getChargingRule().equals(ChargingRule.the95thPercentile)) {
			this.numberOfPeriods = (long) timeScheduleParameters.getReportPeriodEA() / timeScheduleParameters.getReportPeriodDTM();
			this.period = timeScheduleParameters.getReportPeriodDTM();
		}
	}

	/**
	 * The constructor with arguments used in unit testing.
	 */
	public SNMPTrafficCollector(DTMQosAnalyzer analyzer,
			MonitoringDataProcessor monitoringDataProcessor,
			MonitoredLinksInventory monitoredLinks,
			MonitoredTunnelsInventory monitoredTunnels) {
		this(analyzer);
		this.monitoringDataProcessor = monitoringDataProcessor;
		this.monitoredLinks = monitoredLinks;
		this.monitoredTunnels = monitoredTunnels;
	}
	
	/**
	 * Populates helper data structures with links and tunnels that will be
	 * monitored. Triggers collection of SNMP OID numbers for links and tunnels
	 * counters.
	 * 
	 * @param systems
	 *            list of all local ASs
	 * @param communications
	 *            list of all inter-DC communications
	 * @return false when configuration failed
	 */
	public boolean configure(List<AS> systems, List<DC2DCCommunication> communications) {
		try {
			logger.info("Running initial configuration ...");
			monitoredLinks.populate(systems);
			monitoredTunnels.populate(communications);
			monitoringDataProcessor = new MonitoringDataProcessor(monitoredLinks, monitoredTunnels);
		
			SNMPOIDCollector oidCollector = new SNMPOIDCollector(monitoredLinks, monitoredTunnels);
			oidCollector.collectOIDsForLinks();
			oidCollector.collectOIDsForTunnels();
			logger.info("... configuration completed.");
		} catch (Exception e) {
			logger.error("Problem occured during initial configuration of OIDs:\n" + e);
			return false;
		}
		return true;
	}
	
	/**
	 * Schedules counters monitoring task according to configured time schedule
	 * parameters.
	 * 
	 * @param tsp
	 *            time schedule parameters
	 */
	public void scheduleMonitoringTasks() {
		logger.info("Scheduling monitoring tasks ...");
		if(validateAsNumbers()) {
			for (int asNumber : monitoredLinks.getAllAsNumbers()) {
				SBoxThreadHandler.getThreadService().scheduleAtFixedRate(
						new TrafficCollectorTask(this, asNumber),
						calculateInitialDelay(timeScheduleParameters),
						period,
						timeScheduleParameters.getTimeUnit());
				logger.info(prepareLogForScheduleMonitoringTasks(
						asNumber,
						calculateInitialDelay(timeScheduleParameters),
						period,
						timeScheduleParameters.getTimeUnit()));
			}
		}
		logger.info("... completed.");
	}

	/**
	 * Calls {@link MonitoringDataProcessor} to calculate new link traffic
	 * vectors with data fetched from counters. Updates {@link DTMQosAnalyzer}
	 * with the new link and tunnel traffic vectors.
	 * 
	 * @param asNumber
	 *            number of AS which links were monitored
	 * @param counterValues
	 *            counter values from monitored links in given AS
	 */
	public void notifyNewCounterValues(int asNumber, CounterValues counterValues) {
		List<ZVector> zVectors = null;
		ZVector aggregatedZVector = null;
		boolean zVectorsReady = false;

		XVector xVector = monitoringDataProcessor.calculateXVector(asNumber, null, counterValues);
		if(shouldUpdateVectors(asNumber)) {
			logCalculatedVectors(xVector, null);
			analyzer.updateXVector(xVector);
		}
		
		CounterValues collectedCounterValues = new CounterValues();
		if(this.systemControlParameters.getChargingRule().equals(ChargingRule.the95thPercentile)) {
			collectedCounterValues.updateLinksAndTunnels(counterValues);
			if((reportingIterator -1) % numberOfPeriods == 0) {
				xVector = monitoringDataProcessor.calculateXVector(asNumber, lastReportPeriodEACounterValues, collectedCounterValues);
				zVectors = monitoringDataProcessor.calculateZVectors(asNumber, lastReportPeriodEACounterValues, collectedCounterValues);
				zVectorsReady = true;
				lastReportPeriodEACounterValues = collectedCounterValues;
			}
			reportingIterator++;
		} else {
			zVectors = monitoringDataProcessor.calculateZVectors(asNumber, null, counterValues);
			zVectorsReady = true;
		}

		if(shouldUpdateVectors(asNumber) && zVectorsReady) {
			aggregatedZVector = monitoringDataProcessor.aggregateZVectors(zVectors);
			logCalculatedVectors(xVector, zVectors);
			analyzer.updateXZVectors(xVector, Arrays.asList(aggregatedZVector));
		}
		
		firstUpdate.put(asNumber, true);
	}

	private Boolean shouldUpdateVectors(int asNumber) {
		return firstUpdate.get(asNumber) != null && firstUpdate.get(asNumber);
	}
	
	void logCalculatedVectors(XVector xVector, List<ZVector> zVectors) {
		logger.info("Updating DTMQosAnalyzer with vectors:");
		
		if (xVector != null)
			logger.info(xVector.toString());
		
		if (zVectors != null)
			logger.info(zVectors.toString());

		if (xVector != null && zVectors != null) {
			if (zVectors.get(0) != null && (xVector.getVectorValues().size() != zVectors.get(0).getVectorValues().size())) {
				logger.warn("X and Z vectors for given AS have different sizes! This is not correct.");
			}
		}
	}
	
	protected long calculateInitialDelay(TimeScheduleParameters tsp) {
		long divideBy = 1;
		if(tsp.getTimeUnit().equals(TimeUnit.SECONDS)) {
			divideBy = 1000;
		}
		return (tsp.getStartDate().getTime() - DateTime.now().getMillis()) / divideBy;
	}
	
	/**
	 * Getter method for monitored links inventory.
	 * 
	 * @return {@link MonitoredLinksInventory}
	 */
	public MonitoredLinksInventory getMonitoredLinks() {
		return monitoredLinks;
	}

	/**
	 * Getter method for monitored tunnels inventory.
	 * 
	 * @return {@link MonitoredLinksInventory}
	 */
	public MonitoredTunnelsInventory getMonitoredTunnels() {
		return monitoredTunnels;
	}
	
	protected boolean validateAsNumbers() {
		if(!monitoredLinks.getAllAsNumbers().containsAll(monitoredTunnels.getAllAsNumbers())) {
			logger.error(prepareErrorForAsNumbersValidation(monitoredLinks.getAllAsNumbers(), monitoredTunnels.getAllAsNumbers()));
			return false;
		}
		return true;
	}
	
	protected String prepareLogForScheduleMonitoringTasks(int asNumber, long initialDelay, long raportingPeriod, TimeUnit timeUnit) {
		final StringBuilder log = new StringBuilder();
		log.append("Scheduled counters monitoring task:\n");
		log.append("\tAS number: ").append(Integer.toString(asNumber));
		log.append("\n\tInitial delay: ").append(Long.toString(initialDelay));
		log.append("\n\tReporting period: ").append(Long.toString(raportingPeriod));
		log.append("\n\t time unit: ").append(timeUnit.toString()).append("\n");
		return log.toString();
	}
	
	String prepareErrorForAsNumbersValidation(Set<Integer> asNumbersForLinks, Set<Integer> asNumbersForTunnels) {
		final StringBuilder error = new StringBuilder();
		error.append("The lists of Autonomus Systems for links and tunnels are not equal:\n");
		error.append("\tAS numbers for links: ").append(asNumbersForLinks.toString());
		error.append("\n\t AS numbers for tunnels: ").append(asNumbersForTunnels.toString());
		error.append("\n");
		return error.toString();
	}
	
	private TimeScheduleParameters loadTimeScheduleParametersFromDB() {
		return DAOFactory.getTimeScheduleParametersDAOInstance().findLast();
	}
	
	
	private SystemControlParameters loadSystemControlParametersFromDB() {
		return DAOFactory.getSCPDAOInstance().findLast();
	}
}
