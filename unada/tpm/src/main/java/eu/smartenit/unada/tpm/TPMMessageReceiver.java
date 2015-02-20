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
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Interface for processing TPM-related messages received from other UNaDas.
 *
 * @author Piotr Wydrych
 */
public interface TPMMessageReceiver {

    /**
     * Processes a remote traceroute request message received from another
     * UNaDa. The traceroute is executed from {@code dst} towards {@code src}.
     *
     * @param src UNaDa info of message sender (source), i.e., the traceroute
     * requestor.
     * @param dst UNaDa info of message receiver (destination), i.e., the
     * traceroute executor.
     * @throws UnknownHostException if hop IP cannot be parsed
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the current thread is
     * {@linkplain Thread#interrupt() interrupted} by another thread while it is
     * waiting, then the wait is ended and an {@link InterruptedException} is
     * thrown.
     */
    public void processRemoteTracerouteRequest(UnadaInfo src, UnadaInfo dst) throws UnknownHostException, IOException, InterruptedException;

    /**
     * Processes a remote traceroute reply message received from another UNaDa.
     * The message contains information on AS hops between {@code src} and
     * {@code dst}.
     *
     * @param src UNaDa info of message sender (source), i.e., the traceroute
     * executor.
     * @param dst UNaDa info of message receiver (destination), i.e., the
     * traceroute requestor.
     * @param asvector list of AS hops.
     */
    public void processRemoteTracerouteReply(UnadaInfo src, UnadaInfo dst, List<Integer> asvector);
}
