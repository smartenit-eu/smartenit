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
package eu.smartenit.unada.tpm;

import eu.smartenit.unada.db.dto.UnadaInfo;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Piotr Wydrych
 * @version $Id$
 */
public class ASVector implements Comparable<ASVector> {

    private final UnadaInfo remoteAddress;
    private final List<Integer> vector;

    public ASVector(UnadaInfo remoteAddress, List<Integer> vector) {
        this.remoteAddress = remoteAddress;
        this.vector = vector;
    }

    public UnadaInfo getRemoteAddress() {
        return remoteAddress;
    }

    public List<Integer> getVector() {
        return vector;
    }

    @Override
    public int compareTo(ASVector o) {
        return Integer.compare(this.vector.size(), o.vector.size());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.remoteAddress.getInetAddress());
        hash = 97 * hash + Objects.hashCode(this.vector);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ASVector other = (ASVector) obj;
        return Objects.equals(this.remoteAddress, other.remoteAddress) && Objects.equals(this.vector, other.vector);
    }

    @Override
    public String toString() {
        return "from " + remoteAddress.toString() + " via " + vector.toString();
    }

}
