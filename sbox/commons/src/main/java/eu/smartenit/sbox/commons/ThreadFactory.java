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
package eu.smartenit.sbox.commons;

import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Factory for creating threads.
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a href="http://psnc.pl">PSNC</a>)
 * @version 1.0
 *
 */
public class ThreadFactory implements java.util.concurrent.ThreadFactory {
	private static final Logger logger = LoggerFactory.getLogger(ThreadFactory.class);
	private java.util.concurrent.ThreadFactory factory = Executors.defaultThreadFactory();

	public Thread newThread(Runnable task) {
		Thread result = factory.newThread(task);
		result.setName("SmartenIT " + result.getName());
		logger.info("Starting new thread: " + result.getName());
		return result;
	}
}