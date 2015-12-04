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
package eu.smartenit.sdn.floodlight090.dtm.restlet;

import eu.smartenit.sdn.interfaces.sboxsdn.URLs;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import net.floodlightcontroller.restserver.RestletRoutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configures RESTlet resources.
 *
 * @author Grzegorz Rzym
 * @author Piotr Wydrych
 * @version 1.0
 */
public class DTMRestletRoutable implements RestletRoutable {

    private static final Logger logger = LoggerFactory.getLogger(DTMRestletRoutable.class);

    /**
     * Get the restlet that will map to the resources. Two resources are mapped:
     * {@link RCVectorsResource} and {@link ConfigDataResource}.
     *
     * @param context the context for constructing the restlet
     * @return the restlet
     * @see URLs
     */
    @Override
    public Restlet getRestlet(Context context) {
        logger.debug("getRestlet(Context) begin");
        logger.debug("Creating DTM restlet");
        Router router = new Router(context);
        router.attach(URLs.DTM_R_C_VECTORS_PATH, RCVectorsResource.class);
        router.attach(URLs.DTM_R_VECTORS_MAP_PATH, RVectorsMapResource.class);
        router.attach(URLs.DTM_CONFIG_DATA_PATH, ConfigDataResource.class);
        logger.debug("getRestlet(Context) end");
        return router;
    }

    /**
     * Get the base path URL where the router should be registered.
     *
     * @return the base path URL where the router should be registered
     * @see URLs
     */
    @Override
    public String basePath() {
        logger.debug("basePath() one-liner");
        return URLs.BASE_PATH;
    }

}
