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
package eu.smartenit.sdn.interfaces.sboxsdn;

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.RVector;

/**
 * URLs shared between SBox and SDN.
 *
 * @author Piotr Wydrych
 * @version 1.0
 */
public class URLs {

    /**
     * Base path URL.
     */
    public static final String BASE_PATH = "/smartenit";

    /**
     * Path used by server-side resource used for editing and retrieving
     * {@link RCVectors}, i.e., {@link RVector} and {@link CVector}.
     */
    public static final String DTM_R_C_VECTORS_PATH = "/dtm/r-c-vectors";

    public static final String DTM_R_VECTORS_MAP_PATH = "/dtm/r-vectors-map";

    /**
     * Path used by server-side resource used for editing and retrieving
     * {@link ConfigData}.
     */
    public static final String DTM_CONFIG_DATA_PATH = "/dtm/config-data";

    private URLs() {
    }
}
