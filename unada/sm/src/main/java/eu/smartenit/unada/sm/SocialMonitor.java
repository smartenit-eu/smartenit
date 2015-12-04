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
package eu.smartenit.unada.sm;

import eu.smartenit.unada.commons.constants.UnadaConstants;
import eu.smartenit.unada.commons.threads.UnadaThreadService;
import eu.smartenit.unada.db.dto.Owner;
import eu.smartenit.unada.db.dto.UNaDaConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Class which must be used to invoke the monitoring process.
 */
public class SocialMonitor {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(SocialMonitor.class);

    /**
     * Map containing the owners for which content is currently being retrieved.
     */
    private final ConcurrentMap<ScheduledFuture<?>, Owner> ownerMap = new ConcurrentHashMap<ScheduledFuture<?>, Owner>();

    /**
     * ExecutorService that controls the execution of MonitorRunner processes.
     */
    private final ScheduledExecutorService executorService;

    /**
     *
     */
    private final long INTERVAL;

    /**
     * Initializes the SocialMonitor.
     */
    public SocialMonitor(UNaDaConfiguration configuration) {
        INTERVAL = configuration.getSocialInterval();
        executorService = UnadaThreadService.getThreadService();
    }

    /**
     * Starts retrieving data for a given owner.
     *
     * @param owner The owner for which data is to be retrieved.
     * @return The ScheduledFuture object that can be used to stop the MonitorRunner.
     */
    public ScheduledFuture<?> startMonitoring(Owner owner) {
        return startMonitoring(new MonitorRunner(owner));
    }

    /**
     * Starts retrieving data for a given runner instance. Should be used for testing purposes only.
     *
     * @param runner The MonitorRunner used to retrieve data.
     * @return The ScheduledFuture object that can be used to stop the MonitorRunner.
     */
    ScheduledFuture<?> startMonitoring(MonitorRunner runner) {
        logger.info("Starting SocialMonitor for facebook id " + runner.getOwner().getFacebookID());
        ScheduledFuture<?> sf = executorService.scheduleAtFixedRate(runner, UnadaConstants.INITIAL_DELAY, INTERVAL, TimeUnit.MILLISECONDS);
        ownerMap.put(sf, runner.getOwner());
        logger.info("Done.");
        return sf;
    }

    /**
     * Stops retrieving data for a given ScheduledFuture instance.
     *
     * @param sf The ScheduledFuture containing the reference to the MonitorRunner that is to be stopped.
     */
    public void stopMonitoring(ScheduledFuture<?> sf) {
        logger.info("Stopping SocialMonitor for facebook id " + ownerMap.get(sf).getFacebookID());
        sf.cancel(true);
        ownerMap.remove(sf);
        logger.info("Done.");
    }

}