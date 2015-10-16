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

import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

/**
 * The access REST API of uNaDa. It listens to URL:
 * http://<unada-ip-address>:<unada-port>/unada/rest/access/ to log an access of
 * a specific video request to local cache path.
 * 
 * @author George Petropoulos
 * @version 3.1
 */
@Path("/access")
public class AccessContentService {

    private static final Logger logger = LoggerFactory
            .getLogger(AccessContentService.class);

    /**
     * The access POST method. It inserts an access entry for the specified
     * content, if it exists in local cache path.
     * 
     * @param path
     *            The path of the video in the local cache.
     * @param request
     *            The http servlet request
     * 
     * @return True if content exists locally, otherwise false.
     * 
     */
    @POST
    @Consumes("text/plain")
    public boolean access(String path, @Context HttpServletRequest request) {
        logger.debug("Checking if content exists in path " + path);
        Content content = DAOFactory.getContentDAO().findByPath(path);

        // check if content exists in cache and is downloaded.
        if (content != null && content.isDownloaded()) {
            logger.info("Content " + content.getContentID() + " is cached, "
                    + "and will be served from local HTTP server.");
            return true;

        }
        return false;
    }

}
