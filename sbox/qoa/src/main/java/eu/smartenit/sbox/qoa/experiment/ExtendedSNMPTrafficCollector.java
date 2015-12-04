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
package eu.smartenit.sbox.qoa.experiment;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.commons.SBoxProperties;
import eu.smartenit.sbox.commons.SBoxThreadHandler;
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.ChargingRule;
import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.EndAddressPairTunnelID;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.LinkID;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;
import eu.smartenit.sbox.db.dto.Tunnel;
import eu.smartenit.sbox.db.dto.TunnelID;
import eu.smartenit.sbox.qoa.CounterValues;
import eu.smartenit.sbox.qoa.DTMQosAnalyzer;
import eu.smartenit.sbox.qoa.MonitoredLinksInventory;
import eu.smartenit.sbox.qoa.MonitoredTunnelsInventory;
import eu.smartenit.sbox.qoa.MonitoringDataProcessor;
import eu.smartenit.sbox.qoa.SNMPTrafficCollector;

/**
 * Extends {@link SNMPTrafficCollector} to enable writing traffic details to file.
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a
 *         href="http://psnc.pl">PSNC</a>)
 * @author <a href="mailto:llopat@man.poznan.pl">Lukasz Lopatowski</a> (<a
 *         href="http://psnc.pl">PSNC</a>)
 * @version 1.2
 * 
 */
public class ExtendedSNMPTrafficCollector extends SNMPTrafficCollector {
	private static final Logger logger = LoggerFactory.getLogger(ExtendedSNMPTrafficCollector.class);
	private ExtendedCounterValues prevAccountingPeriodValues;
	private ExtendedCounterValues prevReportingPeriodValues;
	private int reportingIteratorTrafficDetails;
	private long numberOfPeriodsForTrafficDetails;
	private static boolean PUT_HEADLINE_TO_FILE = true;
	private static LinkID[] linkIds;
	private static TunnelID[] tunnelIds;

	/**
	 * The constructor with arguments.
	 * 
	 * @param analyzer
	 *            instance of the {@link DTMQosAnalyzer} which should be updated
	 *            with traffic vectors calculated after reporting period
	 */
	public ExtendedSNMPTrafficCollector(DTMQosAnalyzer analyzer) {
		super(analyzer);
		initilaizeCounters();
	}
	
	/**
	 * The constructor with arguments used in unit testing.
	 */
	public ExtendedSNMPTrafficCollector(DTMQosAnalyzer analyzer,
			MonitoringDataProcessor monitoringDataProcessor,
			MonitoredLinksInventory monitoredLinks,
			MonitoredTunnelsInventory monitoredTunnels,
			TimeScheduleParameters tsp) {
		super(analyzer, monitoringDataProcessor, monitoredLinks, monitoredTunnels);
		initilaizeCounters();
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
	@Override
	public boolean configure(List<AS> systems, List<DC2DCCommunication> communications) {
		try {
			logger.info("Running initial configuration ...");
			monitoredLinks.populate(systems);
			monitoredTunnels.populate(communications);
			
			monitoringDataProcessor = new MonitoringDataProcessor(monitoredLinks, monitoredTunnels);
			
			ExtendedSNMPOIDCollector oidCollector = new ExtendedSNMPOIDCollector(monitoredLinks, monitoredTunnels);
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
	@Override
	public void scheduleMonitoringTasks() {
		logger.info("Scheduling monitoring tasks ...");
		if(validateAsNumbers()) {
			for (int asNumber : monitoredLinks.getAllAsNumbers()) {
				SBoxThreadHandler.getThreadService().scheduleAtFixedRate(
						new ExtendedTrafficCollectorTask(this, asNumber),
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
	 * Extended to enable writing traffic details to file.
	 * 
	 * @param asNumber
	 *            number of AS which links were monitored
	 * @param counterValues
	 *            counter values from monitored links in given AS
	 */
	@Override
	public void notifyNewCounterValues(int asNumber, CounterValues counterValues) {
//		new FileManager().updateFile(
//				SBoxProperties.TRAFFIC_DETAILS_FILE_PATH, SBoxProperties.TRAFFIC_DETAILS_FILE_NAME, logToFile(asNumber, (ExtendedCounterValues) counterValues));
		new FileManager().updateFile(
				SBoxProperties.TRAFFIC_DETAILS_FILE_PATH, SBoxProperties.TRAFFIC_DETAILS_FILE_NAME, logToFileSecondVersion(asNumber, (ExtendedCounterValues) counterValues));
		super.notifyNewCounterValues(asNumber, counterValues);
	}
	
	protected String logToFileSecondVersion(int asNumber, ExtendedCounterValues counterValues) {
		logger.info("Writing traffic details to file...");
		final StringBuilder record = new StringBuilder();
		
		//HEADLINE
		if(PUT_HEADLINE_TO_FILE) {
			int numberOfLinks = monitoredLinks.getLinks(asNumber).size();
			int numberOfTunnels = monitoredTunnels.getTunnels(asNumber).size();
			linkIds = new LinkID[numberOfLinks];
			tunnelIds = new TunnelID[numberOfTunnels];
			record.append(setupHeadline(asNumber, linkIds, tunnelIds));
			PUT_HEADLINE_TO_FILE = false;
		}
		
		//RECORD		
		if(reportingIteratorTrafficDetails % numberOfPeriodsForTrafficDetails == 0) {
			record.append("\nA\t");
		} else {
			record.append("\nR\t");
		}
		
		record.append(System.currentTimeMillis());
		
		for(LinkID linkId : linkIds) {
			if (counterValues.getCounterValue(linkId) != null)
				record.append("\t").append(counterValues.getCounterValue(linkId));
			else {
				logger.error("No byte counter value available for link: " + ((SimpleLinkID)linkId).toString());
				record.append("\t").append(0);
			}
				
			if (counterValues.getReceivedPackets(linkId) != null)
				record.append("\t").append(counterValues.getReceivedPackets(linkId).aggregate());
			else {
				logger.error("No packet counter value available for link: " + ((SimpleLinkID)linkId).toString());
				record.append("\t").append(0);
			}
		}
		
		for(TunnelID tunnelId : tunnelIds) {
			if (counterValues.getCounterValue(tunnelId) != null)
				record.append("\t").append(counterValues.getCounterValue(tunnelId));
			else {
				logger.error("No byte counter value available for tunnel: " + ((EndAddressPairTunnelID)tunnelId).toString());
				record.append("\t").append(0);
			}
			
			if (counterValues.getReceivedPackets(tunnelId) != null)
				record.append("\t").append(counterValues.getReceivedPackets(tunnelId).aggregate());
			else {
				logger.error("No packet counter value available for tunnel: " + ((EndAddressPairTunnelID)tunnelId).toString());
				record.append("\t").append(0);
			}
		}
		
		reportingIteratorTrafficDetails++;
		logger.debug(record.toString());
		return record.toString();
	}
	
	protected String setupHeadline(int asNumber, LinkID[] linkIds, TunnelID[] tunnelIds) {
		int linkIdsIterator = 0;
		int tunnelIdsIterator = 0;
		final StringBuilder headline = new StringBuilder();
		
		headline.append("PERIOD\tTIMESTAMP");
		for (BGRouter bgRouter  : monitoredLinks.getBGRoutersByAsNumber(asNumber)) {
			if(monitoredLinks.getLinks(bgRouter).size() > 0) {
				for (Link link : monitoredLinks.getLinks(bgRouter)) {
					headline.append("\tLINK_B:").append(((SimpleLinkID)link.getLinkID()).getLocalLinkID());
					headline.append("\tLINK_P:").append(((SimpleLinkID)link.getLinkID()).getLocalLinkID());
					linkIds[linkIdsIterator++] = ((SimpleLinkID)link.getLinkID());
				}
			}
		}
		for (DARouter daRouter  : monitoredTunnels.getDARoutersByAsNumber(asNumber)) {
			if(monitoredTunnels.getTunnels(daRouter).size() > 0) {
				for (Tunnel tunnel : monitoredTunnels.getTunnels(daRouter)) {
					headline.append("\tTUNNEL_B:").append(tunnel.getTunnelID().getTunnelName());
					headline.append("\tTUNNEL_P:").append(tunnel.getTunnelID().getTunnelName());
					tunnelIds[tunnelIdsIterator++] = tunnel.getTunnelID();
				}
			}
		}
		for (BGRouter bgRouter  : monitoredLinks.getBGRoutersByAsNumber(asNumber)) {
			if(monitoredTunnels.getTunnels(bgRouter).size() > 0) {
				for (Tunnel tunnel : monitoredTunnels.getTunnels(bgRouter)) {
					headline.append("\tTUNNEL_B:").append(tunnel.getTunnelID().getTunnelName());
					headline.append("\tTUNNEL_P:").append(tunnel.getTunnelID().getTunnelName());
					tunnelIds[tunnelIdsIterator++] = tunnel.getTunnelID();
				}
			}
		}
		
		return headline.toString();
	}
	
	protected String logToFile(int asNumber, ExtendedCounterValues counterValues) {
		logger.debug("Writing traffic details to file.");
		final StringBuilder record = new StringBuilder();
		record.append("**************************************************\n");
		record.append("Timestamp : ").append(new Date(System.currentTimeMillis()).toString()).append("\n");
		record.append("AS : ").append(asNumber).append("\n");
		if(reportingIteratorTrafficDetails % numberOfPeriodsForTrafficDetails == 0) {
			record.append("New accounting period").append("\n");
			record.append(generateRecordForLinks(asNumber, counterValues, calculateDiffValues(prevAccountingPeriodValues, counterValues)));
			record.append(generateRecordForTunnels(asNumber, counterValues, calculateDiffValues(prevAccountingPeriodValues, counterValues)));
			prevAccountingPeriodValues = counterValues;
		}
		record.append("New reporting period").append("\n");
		record.append(generateRecordForLinks(asNumber, counterValues, calculateDiffValues(prevReportingPeriodValues, counterValues)));
		record.append(generateRecordForTunnels(asNumber, counterValues, calculateDiffValues(prevReportingPeriodValues, counterValues)));
		prevReportingPeriodValues = counterValues;
		reportingIteratorTrafficDetails++;
		return record.toString();
	}
	
	protected String generateRecordForTunnels(int asNumber, ExtendedCounterValues counterValues, ExtendedCounterValues diffCounterValues) {
		final StringBuilder tunnels = new StringBuilder();
		for (DARouter daRouter  : monitoredTunnels.getDARoutersByAsNumber(asNumber)) {
			if(monitoredTunnels.getTunnels(daRouter).size() > 0) {
				tunnels.append("  ").append("DA router : ").append(daRouter.getManagementAddress().getPrefix()).append("\n");
				for (Tunnel tunnel : monitoredTunnels.getTunnels(daRouter)) {
					tunnels.append("    ").append("Tunnel : ").append(tunnel.getTunnelID().getTunnelName()).append("\n");
					tunnels.append("      ").append("IF_IN_OCTETS : ").append(counterValues.getCounterValue(tunnel.getTunnelID())).append(" / ").append(diffCounterValues.getCounterValue(tunnel.getTunnelID())).append("\n");
					tunnels.append("      ").append("IF_IN_PKTS_S : ").append(counterValues.getReceivedPackets(tunnel.getTunnelID()).aggregate()).append(" / ").append(diffCounterValues.getReceivedPackets(tunnel.getTunnelID()).aggregate()).append("\n");
					tunnels.append("      ").append("IF_IN_PKTS_U : ").append(counterValues.getReceivedPackets(tunnel.getTunnelID()).getUnicast()).append(" / ").append(diffCounterValues.getReceivedPackets(tunnel.getTunnelID()).getUnicast()).append("\n");
					tunnels.append("      ").append("IF_IN_PKTS_M : ").append(counterValues.getReceivedPackets(tunnel.getTunnelID()).getMulticast()).append(" / ").append(diffCounterValues.getReceivedPackets(tunnel.getTunnelID()).getMulticast()).append("\n");
					tunnels.append("      ").append("IF_IN_PKTS_B : ").append(counterValues.getReceivedPackets(tunnel.getTunnelID()).getBroadcast()).append(" / ").append(diffCounterValues.getReceivedPackets(tunnel.getTunnelID()).getBroadcast()).append("\n");
				}
			}
		}
		
		for (BGRouter bgRouter  : monitoredLinks.getBGRoutersByAsNumber(asNumber)) {
			if(monitoredTunnels.getTunnels(bgRouter).size() > 0) {
				tunnels.append("  ").append("BG router : ").append(bgRouter.getManagementAddress().getPrefix()).append("\n");
				for (Tunnel tunnel : monitoredTunnels.getTunnels(bgRouter)) {
					tunnels.append("    ").append("Tunnel : ").append((tunnel.getTunnelID()).getTunnelName()).append("\n");
					tunnels.append("      ").append("IF_IN_OCTETS : ").append(counterValues.getCounterValue(tunnel.getTunnelID())).append(" / ").append(diffCounterValues.getCounterValue(tunnel.getTunnelID())).append("\n");
					tunnels.append("      ").append("IF_IN_PKTS_S : ").append(counterValues.getReceivedPackets(tunnel.getTunnelID()).aggregate()).append(" / ").append(diffCounterValues.getReceivedPackets(tunnel.getTunnelID()).aggregate()).append("\n");
					tunnels.append("      ").append("IF_IN_PKTS_U : ").append(counterValues.getReceivedPackets(tunnel.getTunnelID()).getUnicast()).append(" / ").append(diffCounterValues.getReceivedPackets(tunnel.getTunnelID()).getUnicast()).append("\n");
					tunnels.append("      ").append("IF_IN_PKTS_M : ").append(counterValues.getReceivedPackets(tunnel.getTunnelID()).getMulticast()).append(" / ").append(diffCounterValues.getReceivedPackets(tunnel.getTunnelID()).getMulticast()).append("\n");
					tunnels.append("      ").append("IF_IN_PKTS_B : ").append(counterValues.getReceivedPackets(tunnel.getTunnelID()).getBroadcast()).append(" / ").append(diffCounterValues.getReceivedPackets(tunnel.getTunnelID()).getBroadcast()).append("\n");
				}
			}
		}
		return tunnels.toString();
	}
	
	protected String generateRecordForLinks(int asNumber, ExtendedCounterValues counterValues, ExtendedCounterValues diffCounterValues) {
		final StringBuilder links = new StringBuilder();
		for (BGRouter bgRouter  : monitoredLinks.getBGRoutersByAsNumber(asNumber)) {
			if(monitoredLinks.getLinks(bgRouter).size() > 0) {
				links.append("  ").append("BG router : ").append(bgRouter.getManagementAddress().getPrefix()).append("\n");
				for (Link link : monitoredLinks.getLinks(bgRouter)) {
					links.append("    ").append("Link : ").append(((SimpleLinkID)link.getLinkID()).getLocalLinkID()).append("\n");
					links.append("      ").append("IF_IN_OCTETS : ").append(counterValues.getCounterValue(link.getLinkID())).append(" / ").append(diffCounterValues.getCounterValue(link.getLinkID())).append("\n");
					links.append("      ").append("IF_IN_PKTS_S : ").append(counterValues.getReceivedPackets(link.getLinkID()).aggregate()).append(" / ").append(diffCounterValues.getReceivedPackets(link.getLinkID()).aggregate()).append("\n");
					links.append("      ").append("IF_IN_PKTS_U : ").append(counterValues.getReceivedPackets(link.getLinkID()).getUnicast()).append(" / ").append(diffCounterValues.getReceivedPackets(link.getLinkID()).getUnicast()).append("\n");
					links.append("      ").append("IF_IN_PKTS_M : ").append(counterValues.getReceivedPackets(link.getLinkID()).getMulticast()).append(" / ").append(diffCounterValues.getReceivedPackets(link.getLinkID()).getMulticast()).append("\n");
					links.append("      ").append("IF_IN_PKTS_B : ").append(counterValues.getReceivedPackets(link.getLinkID()).getBroadcast()).append(" / ").append(diffCounterValues.getReceivedPackets(link.getLinkID()).getBroadcast()).append("\n");
				}
			}
		}
		return links.toString();
	}
	
	protected ExtendedCounterValues calculateDiffValues(ExtendedCounterValues prevValues, ExtendedCounterValues counterValues) {
		if(prevValues == null) {
			return counterValues;
		} else {
			return counterValues.calculateDifference(prevValues);
		}
	}
	
	private void initilaizeCounters() {
		if(this.systemControlParameters.getChargingRule().equals(ChargingRule.the95thPercentile))
			this.numberOfPeriodsForTrafficDetails = (long) timeScheduleParameters.getAccountingPeriod() / timeScheduleParameters.getReportPeriodDTM();
		else
			this.numberOfPeriodsForTrafficDetails = (long) timeScheduleParameters.getAccountingPeriod() / timeScheduleParameters.getReportingPeriod();
		this.reportingIteratorTrafficDetails = 0;
	}
}
