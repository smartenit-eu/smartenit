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
package eu.smartenit.sdn.interfaces.sboxsdn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serialization helper.
 *
 * @author Piotr Wydrych
 * @version 1.0
 */
public class Serialization {

    private static final Logger logger = LoggerFactory.getLogger(Serialization.class);

    private static final ObjectMapper mapper;

    static {
        logger.debug("static begin");
        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        logger.debug("static end");
    }

    /**
     * Generic serializator.
     *
     * @param <T> type of object being serialized
     * @param object object to be serialized.
     * @return JSON representation of object
     * @throws JsonProcessingException if the object cannot be serialized
     */
    public static <T extends Serializable> String serialize(T object) throws JsonProcessingException {
        logger.debug("serialize(T) one-liner");
        return mapper.writeValueAsString(object);
    }

    /**
     * Generic deserializator.
     *
     * @param <T> type of object being deserialized
     * @param text JSON representation of object
     * @param clazz class of {@code T}
     * @return deserialized object
     * @throws IOException when the JSON string cannot be parsed
     */
    public static <T extends Serializable> T deserialize(String text, Class<T> clazz) throws IOException {
        logger.debug("deserialize(String,Class<T>) one-liner");
        return mapper.readValue(text, clazz);
    }

    private Serialization() {
    }
}
