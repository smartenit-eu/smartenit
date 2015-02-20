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
package eu.smartenit.unada.ctm.main.init;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.smartenit.unada.om.OverlayManager;
import eu.smartenit.unada.om.exceptions.OverlayException;
import eu.smartenit.unada.sa.SocialAnalyzer;
import eu.smartenit.unada.sm.SocialMonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.commons.threads.UnadaThreadService;
import eu.smartenit.unada.ctm.cache.util.OverlayFactory;
import eu.smartenit.unada.ctm.main.timers.CacheCleanerTask;
import eu.smartenit.unada.ctm.main.timers.PredictionTask;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Owner;
import eu.smartenit.unada.db.dto.UNaDaConfiguration;

/**
 * The UnadaOrchestrator class. It bootstraps the uNaDa web application
 * functions.
 * 
 * @author George Petropoulos
 * @version 2.1
 * 
 */
public class UnadaOrchestrator {

	private static UnadaOrchestrator unadaOrchestratorInstance = new UnadaOrchestrator();
	private static final Logger logger = LoggerFactory
			.getLogger(UnadaOrchestrator.class);

	private UnadaOrchestrator() {

	}

	public static UnadaOrchestrator getUnadaOrchestratorInstance() {
		return unadaOrchestratorInstance;
	}

	public static void setUnadaOrchestratorInstance(
			UnadaOrchestrator unadaOrchestratorInstance) {
		UnadaOrchestrator.unadaOrchestratorInstance = unadaOrchestratorInstance;
	}

	/**
	 * The method bootstrapping all the uNaDa functionalities: (a) starting or
	 * joining the overlay, (b) advertising content to the overlay, (c) start
	 * social monitoring and, (d) start social and overlay prediction.
	 * 
	 * @return True if everything is initialized successfully, otherwise False.
	 * 
	 */
	public boolean bootstrap() {
		logger.info("Bootstrapping uNaDa Web Application.");

		// Retrieve unada configuration from database. If not there, or empty then abort.
		UNaDaConfiguration unadaConfiguration = DAOFactory
				.getuNaDaConfigurationDAO().findLast();
		if (unadaConfiguration == null || unadaConfiguration.getMacAddress() == null ||
                unadaConfiguration.getMacAddress().isEmpty()) {
			return false;
		}

        //Check if there is an owner
		Owner owner = DAOFactory.getOwnerDAO().findLast();
		if (owner == null) {
			return false;
		}

        // execute cache cleaning task every 1 hour
        UnadaThreadService.getThreadService().scheduleAtFixedRate(
                new CacheCleanerTask(), 0, 3600 * 1000,
                TimeUnit.MILLISECONDS);

		// start overlay join task
		OverlayManager overlayManager = new OverlayManager(
				unadaConfiguration.getMacAddress());

		if (!unadaConfiguration.isBootstrapNode()) {
			try {
                overlayManager.joinOverlay(
								InetAddress.getByName(unadaConfiguration
										.getIpAddress()),
								unadaConfiguration.getPort());
			} catch (OverlayException e) {
				logger.error("Exception joining the overlay: " + e.getMessage());
				return false;
			} catch (UnknownHostException e) {
				logger.error("Unknown host exception: " + e.getMessage());
				return false;
			}
		} else {
			try {
                overlayManager.createOverlay(InetAddress.getByName(unadaConfiguration
                        .getIpAddress()));
			} catch (OverlayException e) {
				logger.error("Exception creating the overlay: "
						+ e.getMessage());
				return false;
			} catch (UnknownHostException e) {
                logger.error("Unknown host exception: " + e.getMessage());
                return false;
            }
        }
        OverlayFactory.setOverlayManager(overlayManager);

		// advertise content to overlay task
		List<Long> contentIdsList = DAOFactory.getContentDAO().findAllIDs();
		OverlayFactory.getOverlayManager().advertiseContent(
				longListToArray(contentIdsList));

		// retrieve facebook and vimeo data task
		SocialMonitor socialMonitor = new SocialMonitor(unadaConfiguration);
        socialMonitor.startMonitoring(owner);
        SocialFactory.setSocialMonitor(socialMonitor);

        SocialAnalyzer socialAnalyzer = new SocialAnalyzer(OverlayFactory.getOverlayManager());
        SocialFactory.setSocialAnalyzer(socialAnalyzer);

		// execute overlay and social prediction algorithms and manage cache
		UnadaThreadService.getThreadService().scheduleAtFixedRate(
				new PredictionTask(), 30000,
				unadaConfiguration.getPredictionInterval(),
				TimeUnit.MILLISECONDS);

		return true;
	}

	/**
	 * The method that maps a list of Long to an array of long.
	 * 
	 * @param longList
	 *            The list of Long
	 * 
	 * @return The array of long.
	 * 
	 */
	private long[] longListToArray(List<Long> longList) {
		long[] longArray = new long[longList.size()];
		for (int i = 0; i < longList.size(); i++) {
			longArray[i] = longList.get(i).longValue();
		}
		return longArray;
	}

}
