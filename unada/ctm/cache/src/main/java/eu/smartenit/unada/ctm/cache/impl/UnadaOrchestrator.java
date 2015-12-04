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
package eu.smartenit.unada.ctm.cache.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.smartenit.unada.ctm.cache.timers.Periodic24hTask;
import eu.smartenit.unada.om.IOverlayManager;
import eu.smartenit.unada.om.OverlayManager;
import eu.smartenit.unada.om.exceptions.OverlayException;
import eu.smartenit.unada.sa.SocialAnalyzer;
import eu.smartenit.unada.sm.SocialMonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.commons.constants.UnadaConstants;
import eu.smartenit.unada.commons.threads.UnadaThreadService;
import eu.smartenit.unada.ctm.cache.timers.CacheCleanerTask;
import eu.smartenit.unada.ctm.cache.timers.Periodic6hTask;
import eu.smartenit.unada.ctm.cache.timers.PredictionTask;
import eu.smartenit.unada.ctm.cache.util.OverlayFactory;
import eu.smartenit.unada.ctm.cache.util.SocialFactory;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Owner;
import eu.smartenit.unada.db.dto.UNaDaConfiguration;

/**
 * The UnadaOrchestrator class. It bootstraps the uNaDa web application
 * functions.
 * 
 * @author George Petropoulos
 * @version 3.1
 * 
 */
public class UnadaOrchestrator implements Runnable {

    private static final Logger logger = LoggerFactory
            .getLogger(UnadaOrchestrator.class);

    public UnadaOrchestrator() {

    }

    /**
     * The method running the bootstrap method. If its outcome is successful,
     * then thread is over, otherwise runs the bootstrap again after 2 minutes.
     * 
     */
    @Override
    public void run() {
        boolean bootstrap = bootstrap();
        if (!bootstrap) {
            logger.error("uNaDa configuration is missing! Login to the web application "
                    + "with your Facebook credentials!");
            UnadaThreadService.getThreadService().schedule(
                    new UnadaOrchestrator(), 120, TimeUnit.SECONDS);
        }
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
        logger.info("Bootstrapping the uNaDa Application.");
        // Retrieve unada configuration from database. If not there, or empty
        // then abort.
        UNaDaConfiguration unadaConfiguration = DAOFactory
                .getuNaDaConfigurationDAO().findLast();
        if (unadaConfiguration == null
                || unadaConfiguration.getMacAddress() == null
                || unadaConfiguration.getMacAddress().isEmpty()) {
            logger.warn("There is no overlay configuration stored, "
                    + "hence overlay functions cannot start.");
            return false;
        }

        // Check if there is an owner
        Owner owner = DAOFactory.getOwnerDAO().findLast();
        if (owner == null) {
            logger.warn("There is no owner, hence the social functions cannot start.");
            return false;
        }

        // Set unada owner facebook ID
        UnadaConstants.UNADA_OWNER = owner.getFacebookID();
        UnadaConstants.UNADA_OWNER_MD5 = UnadaConstants
                .md5Hash(UnadaConstants.UNADA_OWNER);
        logger.debug("Owner ID " + UnadaConstants.UNADA_OWNER
                + " and its MD5 hash " + UnadaConstants.UNADA_OWNER_MD5);

        // execute cache cleaning task every 1 hour
        UnadaThreadService.getThreadService().scheduleAtFixedRate(
                new CacheCleanerTask(), 0, 3600 * 1000, TimeUnit.MILLISECONDS);

        // start overlay join task
        IOverlayManager overlayManager = OverlayFactory.getOverlayManager();
        if (overlayManager == null) {
            logger.info("Creating new overlay manager.");
            overlayManager = new OverlayManager(
                    unadaConfiguration.getMacAddress());
            OverlayFactory.setOverlayManager(overlayManager);
        }

        if (!unadaConfiguration.isBootstrapNode()) {
            logger.info("Joining existing overlay network ...");
            try {
                overlayManager.joinOverlay(InetAddress
                        .getByName(unadaConfiguration.getIpAddress()),
                        unadaConfiguration.getPort());
            } catch (OverlayException e) {
                logger.error("Exception joining the overlay: " + e.getMessage());
                overlayManager.shutDown();
                return false;
            } catch (UnknownHostException e) {
                logger.error("Unknown host exception: " + e.getMessage());
                overlayManager.shutDown();
                return false;
            }
        } else {
            try {
                logger.info("Creating new overlay network ...");
                overlayManager.createOverlay("eth0");
            } catch (OverlayException e) {
                logger.error("Exception creating the overlay: "
                        + e.getMessage());
                overlayManager.shutDown();
                return false;
            } catch (Exception e) {// UnknownHostException e) {
                logger.error("Unknown host exception: " + e.getMessage());
                overlayManager.shutDown();
                return false;
            }
        }

        // advertise content to overlay task
        UnadaThreadService.getThreadService().execute(new Runnable() {
            @Override
            public void run() {
                List<Long> contentIdsList = DAOFactory.getContentDAO()
                        .findAllIDs();
                OverlayFactory.getOverlayManager().advertiseContent(
                        longListToArray(contentIdsList));
            }
        });

        // retrieve facebook and vimeo data task
        SocialMonitor socialMonitor = SocialFactory.getSocialMonitor();
        if (socialMonitor == null) {
            logger.info("Creating new social monitor.");
            socialMonitor = new SocialMonitor(unadaConfiguration);
            socialMonitor.startMonitoring(owner);
            SocialFactory.setSocialMonitor(socialMonitor);
        }

        SocialAnalyzer socialAnalyzer = SocialFactory.getSocialAnalyzer();
        if (socialAnalyzer == null) {
            logger.info("Creating new social analyzer.");
            socialAnalyzer = new SocialAnalyzer(
                    OverlayFactory.getOverlayManager());
            SocialFactory.setSocialAnalyzer(socialAnalyzer);
        }

        // execute overlay and social prediction algorithms and manage cache
        UnadaThreadService.getThreadService().scheduleAtFixedRate(
                new PredictionTask(), 60000,
                unadaConfiguration.getPredictionInterval(),
                TimeUnit.MILLISECONDS);

        // Every 6 hours
        UnadaThreadService.getThreadService().scheduleAtFixedRate(
                new Periodic6hTask(), 10, 6 * 60, TimeUnit.MINUTES);

        // Every 24 hours
        UnadaThreadService.getThreadService().scheduleAtFixedRate(
                new Periodic24hTask(), 20, 24 * 60, TimeUnit.MINUTES);

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
