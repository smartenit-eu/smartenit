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
package eu.smartenit.unada.interfaces.enduser;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import eu.smartenit.unada.commons.threads.UnadaThreadService;
import eu.smartenit.unada.ctm.cache.impl.ContentAccessLoggerImpl;
import eu.smartenit.unada.ctm.cache.impl.VimeoDownloader;
import eu.smartenit.unada.db.dao.util.DAOFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The download REST API of uNaDa. It listens to URL:
 * http://<unada-ip-address>:<unada-port>/unada/rest/download/<contentID> to
 * download a specific video with contentID.
 * 
 * @author George Petropoulos
 * @version 3.1
 */
@Path("/download")
public class DownloadContentService {

    private static final Logger logger = LoggerFactory
            .getLogger(DownloadContentService.class);

    /**
     * The download GET method. It downloads a Vimeo video with contentID.
     * 
     * @param contentID
     *            The content identifier
     * 
     */
    @GET
    @Path("/{contentID}")
    public boolean download(@PathParam("contentID") String contentID,
            @Context HttpServletRequest request) {
        logger.debug("Attempting to download content " + contentID);
        VimeoDownloader vim = new VimeoDownloader("https://vimeo.com/"
                + contentID, false);
        try {
            // download the vimeo video
            UnadaThreadService.getThreadService().execute(vim);

            // if content already exists, update access log
            if (DAOFactory.getContentDAO().findById(Long.valueOf(contentID)) != null) {
                String ipAddress = request.getRemoteAddr();
                ContentAccessLoggerImpl contentAccessLogger = new ContentAccessLoggerImpl(
                        Long.valueOf(contentID), ipAddress);
                contentAccessLogger.updateAccessLog();
            }
        } catch (Exception e) {
            logger.error("Download of " + contentID + " failed.");
        }
        return true;
    }

}
