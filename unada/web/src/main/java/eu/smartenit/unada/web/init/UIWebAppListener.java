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
package eu.smartenit.unada.web.init;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.db.dao.util.Constants;
import eu.smartenit.unada.db.dao.util.Tables;

/**
 * The UIWebAppListener class. It initializes all the required functions of 
 * the uNaDa UI application.
 * 
 * @author George Petropoulos
 * @version 2.0
 * 
 */
@WebListener
public class UIWebAppListener implements ServletContextListener{
	
	private static final Logger logger = LoggerFactory.getLogger(UIWebAppListener.class);
	
	/**
	 * The method that initializes all functions, when context is initialized.
	 * 
	 * @param arg0 The servlet context event
	 * 
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		logger.info("uNada UI application is initialized.");
		
		Constants.DBI_URL = "jdbc:h2:" + System.getenv("HOME") + "/unada";
		logger.info("Updated db url to " + Constants.DBI_URL);
		
		Tables t = new Tables();
		t.createTables();
	}

	/**
	 * The method that finalizes all functions, when context is destroyed.
	 * 
	 * @param arg0 The servlet context event
	 * 
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.info("uNada web application is finalized.");
		
	}

}
