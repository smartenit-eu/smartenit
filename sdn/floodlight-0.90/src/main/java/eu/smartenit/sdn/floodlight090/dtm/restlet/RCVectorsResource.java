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

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sdn.floodlight090.dtm.DTM;
import eu.smartenit.sdn.interfaces.sboxsdn.RCVectors;
import eu.smartenit.sdn.interfaces.sboxsdn.Serialization;
import java.io.IOException;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Server-side resource used for editing and retrieving {@link RCVectors}, i.e.,
 * {@link RVector} and {@link CVector}.
 *
 * @author Piotr Wydrych
 * @version 1.0
 */
public class RCVectorsResource extends ServerResource {

    private static final Logger logger = LoggerFactory.getLogger(RCVectorsResource.class);

    /**
     * Returns current R and C vectors
     *
     * @return serialized R and C vectors
     * @throws JsonProcessingException when R and C vectors cannot be serialized
     */
    @Get
    // do not allow RESTlet to return JSON - it uses jackson v1.8 instead of v2!
    public String handleGet() throws JsonProcessingException {
        logger.debug("handleGet() begin");
        DTM dtm = DTM.getInstance();
        logger.debug("handleGet() end");
        return Serialization.serialize(new RCVectors(dtm.getReferenceVector(), dtm.getCompensationVector()));
    }

    /**
     * Sets new R and C vectors. Returns current R and C vectors.
     *
     * @param text serialized R and C vectors to be set; if R vector is null, it
     * is not updated
     * @return serialized R and C vectors
     * @throws JsonProcessingException when R and C vectors cannot be serialized
     * @throws IOException when R and C vectors cannot be parsed
     */
    @Post
    // do not allow RESTlet to parse or return JSON - it uses jackson v1.8 instead of v2!
    public String handlePost(String text) throws JsonProcessingException, IOException {
        logger.debug("handlePost(String) begin");
        logger.debug("Received R&C vectors (as String): " + text);
        RCVectors vectors = Serialization.deserialize(text, RCVectors.class);
        DTM dtm = DTM.getInstance();
        dtm.setReferenceVector(vectors.getReferenceVector());
        dtm.setCompensationVector(vectors.getCompensationVector());
        logger.debug("handlePost(String) end");
        return Serialization.serialize(new RCVectors(dtm.getReferenceVector(), dtm.getCompensationVector()));
    }
}
