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
package eu.smartenit.sbox.eca;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dao.CostFunctionDAO;
import eu.smartenit.sbox.db.dao.TimeScheduleParametersDAO;
import eu.smartenit.sbox.db.dto.ChargingRule;
import eu.smartenit.sbox.db.dto.CostFunction;
import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.db.dto.ZVector;
import eu.smartenit.sbox.ntm.dtm.receiver.DTMTrafficManager;

/**
 * Class that implements Economic Analyzer operations within a single autonomous
 * system.
 * 
 * @author D. D&ouml;nni, K. Bhargav, T. Bocek
 * @author Lukasz Lopatowski
 * @version 3.0
 * 
 */
public class EconomicAnalyzerInternal {
	
	private static final Logger logger = LoggerFactory.getLogger(EconomicAnalyzerInternal.class);	

	/**
	 * Counter variable for the number of traffic updates
	 */
	private int updateCounter = 0;

	/**
	 * Index for axis x1
	 */	
	final static int x1 = 0;
	
	/**
	 * Index for axis x2
	 */
	final static int x2 = 1;

	/**
	 * Contains the link IDs for links 1
	 */
	final SimpleLinkID link1;
	
	/**
	 * Contains the link IDs for links 2
	 */
	final SimpleLinkID link2;
	
	/**
	 * The {@link DTMTrafficManager} instance.
	 */
	private final DTMTrafficManager dtmTrafficManager;

	final ChargingRule chargingRule;
	
	final ReferenceVectorCalculator calculator;

	final TrafficSamplesContainer trafficContainer;

	private int reportsInAccountingPeriod;
	
	/**
	 * Constructor for EconomicAnalyzerInternal
	 * 
	 * @param dtmTrafficManager
	 * @param link1
	 *            The {@link SimpleLinkID} of link 1
	 * @param link2
	 *            The {@link SimpleLinkID} of link 2
	 */
	EconomicAnalyzerInternal(DTMTrafficManager dtmTrafficManager, SimpleLinkID link1, SimpleLinkID link2) {
		this.dtmTrafficManager = dtmTrafficManager;
		this.link1 = link1;
		this.link2 = link2;
		chargingRule = DAOFactory.getSCPDAOInstance().findLast().getChargingRule();
		TimeScheduleParameters tsp = getTimeScheduleParametersFromDB();
		
		if (chargingRule.equals(ChargingRule.volume)) {
			long xzReportPeriod = (tsp.getReportPeriodEA() == 0) ? tsp.getReportingPeriod() : tsp.getReportPeriodEA();
			reportsInAccountingPeriod = (int) (tsp.getAccountingPeriod() / xzReportPeriod);
			trafficContainer = new TotalVolumeSamplesContainer(link1, link2);
		} else {
			if (tsp.getSamplingPeriod() != tsp.getReportPeriodEA()) {
				logger.error("In this release reportingPeriodEA must be equal to samplingPeriod (and should be equal to 300)");
				throw new IllegalArgumentException(
						"In this release reportingPeriodEA must be equal to samplingPeriod (and should be equal to 300)");
			}
			
			reportsInAccountingPeriod = (int) (tsp.getAccountingPeriod() / tsp.getSamplingPeriod());
			trafficContainer = new The95PercentileSamplesContainer(link1, link2);
		}
		
		calculator = new ReferenceVectorCalculator(link1, link2, getCostFunctionsFromDB(), tsp);
	}

	/**
	 * Updates link and tunnel traffic data. If accounting period elapsed,
	 * calculates new reference vector and sends is to configured NTM instance.
	 * 
	 * @param xVector
	 *            The link traffic vector
	 * @param zVectors
	 *            The list of tunnel traffic vectors
	 */
	void updateXZVectors(XVector xVector, List<ZVector> zVectors) {
		logger.debug("New update received.");
		updateCounter++;
		trafficContainer.storeTrafficValues(xVector, zVectors);

		if (updateCounter == reportsInAccountingPeriod) {
			logger.debug("Accounting period elapsed. Will calculate new reference vector.");
			LocalRVector rVector = calculator.calculate(
					trafficContainer.getTrafficValuesForLinks(), trafficContainer.getTrafficValuesForTunnels(), xVector.getSourceAsNumber());
			
			if(dtmTrafficManager != null) {
				dtmTrafficManager.updateRVector(rVector);
			}
			
			trafficContainer.resetTrafficValues();
			updateCounter = 0;
		}
	}

	/**
	 * Retrieves the time schedule parameters from the database
	 * 
	 * @return Returns a {@link TimeScheduleParameters} object holding the time schedule parameters
	 */
	private TimeScheduleParameters getTimeScheduleParametersFromDB() {
		TimeScheduleParametersDAO dao = DAOFactory.getTSPDAOInstance();
		TimeScheduleParameters tsp = dao.findLast();
		return tsp;
	}
	
	/**
	 * Retrieves the cost functions from the database
	 * 
	 * @return A {@link List} 
	 */
	private List<CostFunction> getCostFunctionsFromDB() {
		CostFunctionDAO cfdao = DAOFactory.getCostFunctionDAOInstance();
		CostFunction cf1 = cfdao.findByLinkId(link1);
		CostFunction cf2 = cfdao.findByLinkId(link2);
		
		List<CostFunction> costFunctions = new ArrayList<CostFunction>();
		
		costFunctions.add(cf1);
		costFunctions.add(cf2);
		
		return costFunctions;
	}
	
}
