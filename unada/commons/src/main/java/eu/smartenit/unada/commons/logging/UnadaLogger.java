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
package eu.smartenit.unada.commons.logging;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

import java.io.File;
import java.io.IOException;

/**
 * The UnadaLogger class. It creates some additional logs for RBH trials.
 * 
 * @author George Petropoulos
 * @version 3.1
 * 
 */
public class UnadaLogger {
	
	public static Logger social;

	public static Logger overall;
	
	static {
		social = createLogger("social", System.getenv("HOME") + "/log/social.log");
		overall = createLogger("overall", System.getenv("HOME") + "/log/overall.log");
	}

	/**
	 * The method that creates a logger with a given name and at a given path.
	 * 
	 * @param string The logger name.
	 * @param file The logger path.
	 * 
	 * @return The created logger.
	 * 
	 */
	public static Logger createLogger(String string, String file) {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		PatternLayoutEncoder ple = new PatternLayoutEncoder();

		ple.setPattern("%msg%n");
		ple.setContext(lc);
		ple.start();
		FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
		fileAppender.setFile(file);
		fileAppender.setEncoder(ple);
		fileAppender.setContext(lc);
		fileAppender.start();

		Logger logger = (Logger) LoggerFactory.getLogger(string);
		logger.addAppender(fileAppender);
		logger.setLevel(Level.DEBUG);
		logger.setAdditive(false);  

		return logger;
	}

}
