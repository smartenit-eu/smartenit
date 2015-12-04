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
package eu.smartenit.unada.ctm.cache.timers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.commons.constants.UnadaConstants;
import eu.smartenit.unada.commons.logging.UnadaLogger;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Content;
import eu.smartenit.unada.db.dto.Friend;
import eu.smartenit.unada.db.dto.UNaDaConfiguration;

/**
 * The Periodic6hTask class. It runs some periodic tasks for RBH trials.
 * 
 * @author George Petropoulos
 * @version 3.1
 * 
 */
public class Periodic6hTask implements Runnable {

    private static final Logger logger = LoggerFactory
            .getLogger(Periodic6hTask.class);

    /**
     * The method that executes the thread.
     * 
     */
    public void run() {
        logger.info("Running the 6h periodic task.");
        logger.debug("Logging the owner's friends in social log.");
        List<Friend> friends = DAOFactory.getFriendDAO().findAll();
        UNaDaConfiguration unadaConfig = DAOFactory.getuNaDaConfigurationDAO()
                .findLast();

        UnadaLogger.social.info("{}, {}", new Object[] {
                UnadaConstants.UNADA_OWNER_MD5,
                unadaConfig.getPrivateWifi().getSSID() });

        for (Friend f : friends) {
            UnadaLogger.social.info("{}",
                    UnadaConstants.md5Hash(f.getFacebookID()));
        }

        logger.debug("Logging the cache hits in overall log.");
        List<Content> contents = DAOFactory.getContentDAO()
                .findAllWithAccesses();
        for (Content c : contents) {
            UnadaLogger.overall.info(
                    "{}: Cache Hits ({}, {}, {})",
                    new Object[] {
                            UnadaConstants.UNADA_OWNER_MD5,
                            c.getContentID(),
                            System.currentTimeMillis()
                                    - c.getCacheDate().getTime(),
                            c.getAccessList() == null ? 0 : c.getAccessList()
                                    .size() });
        }
        logger.info("Done.");
    }

}
