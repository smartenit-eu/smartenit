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
package eu.smartenit.sbox.commons;

import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The global SBox thread handling class. 
 *
 * @author George Petropoulos
 * @version 1.0
 * 
 */
public class SBoxThreadHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(SBoxThreadHandler.class);
	
	public static ScheduledExecutorService threadService;
	
	/**
	 * The method that shutdowns all threads. 
	 * 
	 */
	public static void shutdownNowThreads() {
		if(threadService != null) {
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
