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

import eu.smartenit.sbox.db.dto.SimpleTunnelID;
import eu.smartenit.sbox.db.dto.TunnelID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Piotr Wydrych
 * @version 1.0
 */
public class SimpleTunnelIDToPortConverter {

    private static final Logger logger = LoggerFactory.getLogger(SimpleTunnelIDToPortConverter.class);

    private SimpleTunnelIDToPortConverter() {
    }

    public static short toPort(TunnelID tunnelID) {
        logger.debug("toPort(TunnelID) begin");
        if (tunnelID == null) {
            throw new IllegalArgumentException("Tunnel ID cannot be null");
        }
        if (!(tunnelID instanceof SimpleTunnelID)) {
            throw new IllegalArgumentException(tunnelID + " is not of SimpleTunnelID type");
        }
        logger.debug("toPort(TunnelID) end");
        return (short) ((SimpleTunnelID) tunnelID).getTunnelNumber();
    }
}
