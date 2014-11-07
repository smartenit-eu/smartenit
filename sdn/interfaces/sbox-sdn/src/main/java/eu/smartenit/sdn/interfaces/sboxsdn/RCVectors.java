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

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.RVector;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Container for {@link RVector reference} and {@link CVector compensation}
 * vectors.
 *
 * @author Piotr Wydrych
 * @version 1.0
 */
public final class RCVectors implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RCVectors.class);

    private static final long serialVersionUID = 1L;

    private RVector referenceVector;
    private CVector compensationVector;

    /**
     * Default constructor. Sets both fields to null.
     */
    public RCVectors() {
        logger.debug("RCVectors() begin/end");
    }

    /**
     * Specific constructor. Sets fields to given parameters.
     *
     * @param referenceVector reference vector
     * @param compensationVector compensation vector
     */
    public RCVectors(RVector referenceVector, CVector compensationVector) {
        logger.debug("RCVectors(RVector,CVector) begin");
        this.referenceVector = referenceVector;
        this.compensationVector = compensationVector;
        logger.debug("RCVectors(RVector,CVector) end");
    }

    /**
     * Sets reference vector.
     *
     * @param referenceVector reference vector
     */
    public void setReferenceVector(RVector referenceVector) {
        logger.debug("setReferenceVector(RVector) begin");
        this.referenceVector = referenceVector;
        logger.debug("setReferenceVector(RVector) end");
    }

    /**
     * Returns reference vector.
     *
     * @return reference vector
     */
    public RVector getReferenceVector() {
        logger.debug("getReferenceVector() one-liner");
        return referenceVector;
    }

    /**
     * Sets compensation vector.
     *
     * @param compensationVector compensation vector
     */
    public void setCompensationVector(CVector compensationVector) {
        logger.debug("setCompensationVector(CVector) begin");
        this.compensationVector = compensationVector;
        logger.debug("setCompensationVector(CVector) end");
    }

    /**
     * Returns compensation vector.
     *
     * @return compensation vector
     */
    public CVector getCompensationVector() {
        logger.debug("getCompensationVector() one-liner");
        return compensationVector;
    }
}
