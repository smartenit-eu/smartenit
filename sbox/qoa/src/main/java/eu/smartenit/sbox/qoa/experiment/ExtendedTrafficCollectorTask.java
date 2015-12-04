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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.commons.SBoxProperties;
import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.qoa.CounterValues;
import eu.smartenit.sbox.qoa.SNMPTrafficCollector;
import eu.smartenit.sbox.qoa.TrafficCollectorTask;

/**
 * Extends {@link TrafficCollectorTask} to enable writing traffic details to file.
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a
 *         href="http://psnc.pl">PSNC</a>)
 * @author <a href="mailto:llopat@man.poznan.pl">Lukasz Lopatowski</a> (<a
 *         href="http://psnc.pl">PSNC</a>)
 * @version 1.2
 * 
 */
public class ExtendedTrafficCollectorTask extends TrafficCollectorTask {
	private static final Logger logger = LoggerFactory.getLogger(ExtendedTrafficCollectorTask.class);

	public ExtendedTrafficCollectorTask(SNMPTrafficCollector trafficCollector,
			int asNumber) {
		super(trafficCollector, asNumber);
	}
	
	/**
	 * Scheduled task responsible for fetching data from router counters and
	 * updating {@link SNMPTrafficCollector} with collected data. Thread waits
	 * {@link SBoxProperties#MAX_FETCHING_TIME} at the longest for all data.
	 */
	@Override
	public void run() {
		collectedCounterValues = new ExtendedCounterValues();
		super.run();
	}
	
	@Override
	protected List<ScheduledFuture<CounterValues>> createThreadsForBGRouters() {
		List<ScheduledFuture<CounterValues>> futures = new ArrayList<ScheduledFuture<CounterValues>>();
		for (BGRouter bgRouter : getBGRoutersByAsNumber()) {
			initilizeCollectedCounterValuesWithLinks(bgRouter);
			futures.add(getThreadService().schedule(new ExtendedCounterCollectorThread(getLinksByBGRouter(bgRouter), null, bgRouter), 0, TimeUnit.MICROSECONDS));
			initilizeCollectedCounterValuesWithTunnels(bgRouter);
			futures.add(getThreadService().schedule(new ExtendedCounterCollectorThread(null, getTunnelsByBGRouter(bgRouter), bgRouter), 0, TimeUnit.MICROSECONDS));
		}
		
		logger.info("Waiting for counter values from {} BGRouters ...", futures.size());
		return futures;
	}
	
	@Override
	protected List<ScheduledFuture<CounterValues>> createThreadsForDARouters() {
		List<ScheduledFuture<CounterValues>> futures = new ArrayList<ScheduledFuture<CounterValues>>();
		for (DARouter daRouter : getDARoutersByAsNumber()) {
			initilizeCollectedCounterValuesWithTunnels(daRouter);
			futures.add(getThreadService().schedule(new ExtendedCounterCollectorThread(getTunnelsByDARouter(daRouter), daRouter), 0, TimeUnit.MICROSECONDS));
		}
		logger.info("Waiting for counter values from {} DARouters ...", futures.size());
		return futures;
	}
}
