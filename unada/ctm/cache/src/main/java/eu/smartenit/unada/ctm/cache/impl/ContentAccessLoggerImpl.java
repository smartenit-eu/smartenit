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
package eu.smartenit.unada.ctm.cache.impl;

import eu.smartenit.unada.commons.constants.UnadaConstants;
import eu.smartenit.unada.commons.logging.UnadaLogger;
import eu.smartenit.unada.ctm.cache.ContentAccessLogger;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.ContentAccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * The ContentAccessLogger class. It updates the content access log for a
 * specific content and user.
 * 
 * @author George Petropoulos
 * @version 2.1
 * 
 */
public class ContentAccessLoggerImpl implements ContentAccessLogger, Runnable {

    private static final Logger logger = LoggerFactory
            .getLogger(ContentAccessLoggerImpl.class);
    private final long sameVideoRequestInterval = 60000;
    private long contentID;
    private String ipAddress;

    public ContentAccessLoggerImpl(long contentID, String ipAddress) {
        this.contentID = contentID;
        this.ipAddress = ipAddress;
    }

    /**
     * The method that updates the access log for a specific content and user.
     * If the recent accesses are very near to the current time, then the access
     * log is not updated.
     * 
     * @return The boolean outcome of the update.
     * 
     */
    public boolean updateAccessLog() {
        logger.debug("Checking last accesses of content " + contentID);
        // get last access of content
        ContentAccess contentAccess = DAOFactory.getContentAccessDAO()
                .findLatestByContentID(contentID);

        // if there are no accesses or last access is more than 1 min ago, then
        // insert new one.
        // this is done because sometimes Vimeo performs 2-3 consequent requests
        // for one video request.
        if (contentAccess == null
                || contentAccess.getTimeStamp().getTime() < System
                        .currentTimeMillis() - sameVideoRequestInterval) {

            contentAccess = new ContentAccess();
            contentAccess.setContentID(contentID);
            contentAccess.setTimeStamp(new Date(System.currentTimeMillis()));
            try {
                DAOFactory.getContentAccessDAO().insert(contentAccess);
                logger.info("Request for video " + contentAccess.getContentID()
                        + " is served from local cache.");
                logger.debug("Inserting content access " + contentAccess
                        + " from IP " + ipAddress);

                UnadaLogger.overall.info(
                        "{}: Video request ({}, {})",
                        new Object[] { UnadaConstants.UNADA_OWNER_MD5,
                                System.currentTimeMillis(), contentID });

                return true;
            } catch (Exception e) {
                logger.error("Error inserting content access for " + contentID
                        + ": " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    @Override
    public void run() {
        updateAccessLog();
    }
}
