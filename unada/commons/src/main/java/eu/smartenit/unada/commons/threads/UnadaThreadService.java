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
package eu.smartenit.unada.commons.threads;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnadaThreadService {

    private static final Logger logger = LoggerFactory
            .getLogger(UnadaThreadService.class);

    private static ScheduledExecutorService threadService = Executors
            .newScheduledThreadPool(30);

    /**
     * The method that shutdowns all threads.
     * 
     */
    public static void shutdownNowThreads() {
        if (threadService != null) {
            logger.info("Shutting down all threads.");
            threadService.shutdownNow();
        }
    }

    /**
     * The method that returns the thread service
     * 
     */
    public static ScheduledExecutorService getThreadService() {
        return threadService;
    }
}
