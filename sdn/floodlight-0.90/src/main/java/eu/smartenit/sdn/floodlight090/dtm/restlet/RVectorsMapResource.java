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
import eu.smartenit.sdn.floodlight090.dtm.DTM;
import eu.smartenit.sdn.interfaces.sboxsdn.Serialization;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 *
 * @author Piotr Wydrych
 * @version $Id$
 */
public class RVectorsMapResource extends ServerResource {

    @Get
    // do not allow RESTlet to return JSON - it uses jackson v1.8 instead of v2!
    public String handleGet() throws JsonProcessingException {
        DTM dtm = DTM.getInstance();
        return Serialization.serialize(dtm.getReferenceVectorMap());
    }
}
