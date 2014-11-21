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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.commons.SBoxProperties;
import eu.smartenit.sbox.commons.SBoxThreadHandler;
import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.Tunnel;

/**
 * Task class responsible for collecting counter values from BG and DA routers.
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a
 *         href="http://psnc.pl">PSNC</a>)
 * @version 1.0
 * 
 */
public class TrafficCollectorTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(TrafficCollectorTask.class);
	
	private SNMPTrafficCollector trafficCollector;
	private int asNumber;
	protected CounterValues collectedCounterValues = new CounterValues();

	/**
	 * The constructor with arguments.
	 * 
	 * @param trafficCollector
	 *            instance of the {@link SNMPTrafficCollector} from which
	 *            information about monitored links and tunnels will be read and
	 *            which will be notified once all counters are read
	 * @param asNumber
	 *            number of the AS for which this task is launched (relevant if
	 *            SBox manages more than one AS)
	 */
	public TrafficCollectorTask(SNMPTrafficCollector trafficCollector, int asNumber) {
		this.trafficCollector = trafficCollector;
		this.asNumber = asNumber;
	}
	
	/**
	 * Scheduled task responsible for fetching data from router counters and
	 * updating {@link SNMPTrafficCollector} with collected data. Thread waits
	 * {@link SBoxProperties#MAX_FETCHING_TIME} at the longest for all data.
	 */
	public void run() {
		try {
			logger.debug("Running traffic collector task for AS {}", asNumber);
			List<ScheduledFuture<CounterValues>> routersCounterFutures = createThreadsForDARouters();
			routersCounterFutures.addAll(createThreadsForBGRouters());
		
			final long startTime = DateTime.now().getMillis();
			while(!areAllThreadsCompleted(routersCounterFutures)) {
				long currentTime = DateTime.now().getMillis();
				if(currentTime - startTime > SBoxProperties.MAX_FETCHING_TIME) {
					logger.warn("Fetching data from counters took to long (over {}).", SBoxProperties.MAX_FETCHING_TIME);
					break;
				}
				Thread.sleep(100);
			}
			logger.info("Collecting results from all completed threads.");
			collectResultsFromAllThreads(routersCounterFutures);
			
			logger.info("Updating trafficCollector with new counter values.");
			trafficCollector.notifyNewCounterValues(asNumber, collectedCounterValues);
			
		} catch (InterruptedException e) {
			logger.warn("Caught InterruptedException while collecting counter values.\n" + e);
		} catch (ExecutionException e) {
			logger.warn("Caught ExecutionException while collecting counter values.\n" + e);
		}
	}

	protected List<ScheduledFuture<CounterValues>> createThreadsForBGRouters() {
		List<ScheduledFuture<CounterValues>> futures = new ArrayList<ScheduledFuture<CounterValues>>();
		for (BGRouter bgRouter : getBGRoutersByAsNumber()) {
			initilizeCollectedCounterValuesWithLinks(bgRouter);
			futures.add(getThreadService().schedule(new CounterCollectorThread(getLinksByBGRouter(bgRouter), bgRouter), 0, TimeUnit.MICROSECONDS));
		}
		logger.info("Waiting for counter values from {} BGRouters ...", futures.size());
		return futures;
	}

	protected void initilizeCollectedCounterValuesWithLinks(BGRouter bgRouter) {
		for (Link link : getLinksByBGRouter(bgRouter)) {
			collectedCounterValues.storeCounterValue(link.getLinkID(), 0);
		}
	}
	
	protected List<ScheduledFuture<CounterValues>> createThreadsForDARouters() {
		List<ScheduledFuture<CounterValues>> futures = new ArrayList<ScheduledFuture<CounterValues>>();
		for (DARouter daRouter : getDARoutersByAsNumber()) {
			initilizeCollectedCounterValuesWithTunnels(daRouter);
			futures.add(getThreadService().schedule(new CounterCollectorThread(getTunnelsByDARouter(daRouter), daRouter), 0, TimeUnit.MICROSECONDS));
		}
		logger.info("Waiting for counter values from {} DARouters ...", futures.size());
		return futures;
	}

	protected void initilizeCollectedCounterValuesWithTunnels(DARouter daRouter) {
		for (Tunnel tunnel : getTunnelsByDARouter(daRouter)) {
			collectedCounterValues.storeCounterValue(tunnel.getTunnelID(), 0);
		}
	}

	protected boolean areAllThreadsCompleted(List<ScheduledFuture<CounterValues>> futures) {
		for(ScheduledFuture<CounterValues> future : futures) {
			if (future.isDone() == false) {
				return false;
			}
		}
		return true;
	}

	protected void collectResultsFromAllThreads(List<ScheduledFuture<CounterValues>> futures) throws InterruptedException, ExecutionException {
		for(ScheduledFuture<CounterValues> future : futures) {
			if(future.isDone()) {
				collectedCounterValues.addLinksAndTunnels(future.get());
			}
		}
	}
	
	protected ScheduledExecutorService getThreadService() {
		return SBoxThreadHandler.getThreadService();
	}
	
	protected List<BGRouter> getBGRoutersByAsNumber() {
		return trafficCollector.getMonitoredLinks().getBGRoutersByAsNumber(asNumber);
	}
	
	protected List<DARouter> getDARoutersByAsNumber() {
		return trafficCollector.getMonitoredTunnels().getDARoutersByAsNumber(asNumber);
	}
	
	protected List<Link> getLinksByBGRouter(BGRouter bgRouter) {
		return trafficCollector.getMonitoredLinks().getLinks(bgRouter);
	}
	
	protected List<Tunnel> getTunnelsByDARouter(DARouter daRouter) {
		return trafficCollector.getMonitoredTunnels().getTunnels(daRouter);
	}
}