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
package eu.smartenit.sdn.floodlight090.dtm.restlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.smartenit.sbox.db.dto.ConfigData;
import eu.smartenit.sdn.floodlight090.dtm.DTM;
import eu.smartenit.sdn.interfaces.sboxsdn.Serialization;
import java.io.IOException;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Server-side resource used for editing and retrieving {@link ConfigData}.
 *
 * @author Piotr Wydrych
 * @version 1.0
 */
public class ConfigDataResource extends ServerResource {

    private static final Logger logger = LoggerFactory.getLogger(ConfigDataResource.class);

    /**
     * Returns current DTM config data
     *
     * @return serialized DTM config data
     * @throws JsonProcessingException when DTM config data cannot be serialized
     */
    @Get
    // do not allow RESTlet to return JSON - it uses jackson v1.8 instead of v2!
    public String handleGet() throws JsonProcessingException {
        logger.debug("handleGet() begin");
        DTM dtm = DTM.getInstance();
        logger.debug("handleGet() end");
        return Serialization.serialize(dtm.getConfigData());
    }

    /**
     * Sets new DTM config data. Returns current DTM config data.
     *
     * @param text serialized DTM config data to be set
     * @return serialized DTM config data
     * @throws JsonProcessingException when DTM config data cannot be serialized
     * @throws IOException when DTM config data cannot be parsed
     */
    @Post
    // do not allow RESTlet to parse or return JSON - it uses jackson v1.8 instead of v2!
    public String handlePost(String text) throws JsonProcessingException, IOException {
        logger.debug("handlePost(String) begin");
        logger.debug("Received configuration (as String): " + text);
        ConfigData configData = Serialization.deserialize(text, ConfigData.class);
        DTM dtm = DTM.getInstance();
        dtm.setConfigData(configData);
        logger.debug("handlePost(String) end");
        return Serialization.serialize(dtm.getConfigData());
    }
}
