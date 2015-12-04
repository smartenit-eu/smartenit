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
package eu.smartenit.sbox.web.init;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dao.DbConstants;
import eu.smartenit.sbox.db.dao.util.Tables;

/**
 * The SBoxWebAppListener class. It initializes all the required functions of 
 * the SBox UI application.
 * 
 * @author George Petropoulos
 * @version 1.2
 * 
 */
@WebListener
public class SBoxWebAppListener implements ServletContextListener{
	
	private static final Logger logger = LoggerFactory.getLogger(SBoxWebAppListener.class);
	
	/**
	 * The method that initializes all functions, when context is initialized.
	 * 
	 * @param arg0 The servlet context event
	 * 
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		logger.info("SBox UI application is initialized.");
		String dbPathFile = System.getenv("HOME") + "/smartenit.db";
		DbConstants.DBI_URL = "jdbc:sqlite:" + dbPathFile;
		
		logger.info("DB Constants URL = " + DbConstants.DBI_URL);
		
		Tables t = new Tables();
		t.createAll();
	}

	/**
	 * The method that finalizes all functions, when context is destroyed.
	 * 
	 * @param arg0 The servlet context event
	 * 
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.info("SBox web application is finalized.");
		
	}

}
