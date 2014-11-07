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
package eu.smartenit.sdn.floodlight090.dtm;

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.ConfigData;
import eu.smartenit.sbox.db.dto.LinkID;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.db.dto.Tunnel;
import eu.smartenit.sbox.db.dto.TunnelID;
import eu.smartenit.sbox.db.dto.VectorValue;
import eu.smartenit.sdn.interfaces.sboxsdn.SimpleTunnelIDToPortConverter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import net.floodlightcontroller.core.IOFSwitch;
import org.openflow.protocol.OFStatisticsRequest;
import org.openflow.protocol.statistics.OFPortStatisticsReply;
import org.openflow.protocol.statistics.OFPortStatisticsRequest;
import org.openflow.protocol.statistics.OFStatistics;
import org.openflow.protocol.statistics.OFStatisticsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main DTM class. Includes port statistics poller. Holds information on current
 * {@link CVector compensation vector}, {@link RVector reference vector}, and
 * traffic transmitted.
 *
 * @author Grzegorz Rzym
 * @author Piotr Wydrych
 * @version 1.0
 */
public class DTM {

    /**
     * Timer task responsible for getting swithchport statistics every
     * {@link DTM#PORT_STATISTICS_POLLING_INTERVAL} ms.
     */
    public class PortStatisticsPoller extends TimerTask {

        private final Logger logger = LoggerFactory.getLogger(PortStatisticsPoller.class);

        /**
         * Timeout in ms.
         */
        private static final int TIMEOUT = PORT_STATISTICS_POLLING_INTERVAL / 2;

        private PortStatisticsPoller() {
            logger.debug("PortStatisticsPoller() begin/end");
        }

        /**
         * Requests port statistics (tx bytes) from switch and saves the state
         * upon success.
         *
         * @see DTM#getTransmittedBytes()
         */
        @Override
        public void run() {
            logger.debug("run() begin");
            long timeoutTime = System.nanoTime() + TIMEOUT * 1000000;
            synchronized (DTM.this) {
                if (sw == null) { // no switch
                    logger.debug("run() end (no switch)");
                    return;
                }
                if (tunnelIDs == null) { // not configured
                    logger.debug("run() end (not configured)");
                    return;
                }
                try {
                    List<Future<List<OFStatistics>>> futures = new ArrayList<>(LINK_COUNT);
                    for (int i = 0; i < LINK_COUNT; i++) {
                        OFStatisticsRequest req = new OFStatisticsRequest();
                        req.setStatisticType(OFStatisticsType.PORT);
                        int requestLength = req.getLengthU();

                        OFPortStatisticsRequest specificReq = new OFPortStatisticsRequest();
                        specificReq.setPortNumber(SimpleTunnelIDToPortConverter.toPort(tunnelIDs[i]));
                        requestLength += specificReq.getLength();
                        req.setStatistics(Collections.singletonList((OFStatistics) specificReq));
                        req.setLengthU(requestLength);

                        futures.add(sw.getStatistics(req));
                    }
                    long[] transmittedBytes = new long[LINK_COUNT];
                    for (int i = 0; i < LINK_COUNT; i++) {
                        long left = timeoutTime - System.nanoTime();
                        List<OFStatistics> result = futures.get(i).get(left > 0 ? left : 0, TimeUnit.NANOSECONDS);
                        if (result.isEmpty()) {
                            logger.debug("run() end (no result)");
                            return;
                        }
                        transmittedBytes[i] = ((OFPortStatisticsReply) result.get(0)).getTransmitBytes();
                    }
                    setTransmittedBytes(transmittedBytes);
                } catch (IOException | InterruptedException | ExecutionException | TimeoutException ex) {
                    logger.error("Error during statistics polling", ex);
                }
            }
            logger.debug("run() end");
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(DTM.class);

    /**
     * Number of links. Currently, only the two-uplink scenario is supported.
     */
    public static final int LINK_COUNT = 2;

    /**
     * Polling interval in ms.
     */
    public static final int PORT_STATISTICS_POLLING_INTERVAL = 1000;

    private static DTM singleton;

    private ConfigData configData;
    private LinkID[] linkIDs;
    private TunnelID[] tunnelIDs;
    private RVector referenceVector;
    private CVector compensationVector;
    private IOFSwitch sw;
    private long[] transmittedBytes;
    private long[] transmittedBytesStart;
    private short compensatingToTunnel = 0; // compensate = compensatingToTunnel != 0

    private DTM() {
        logger.debug("DTM() begin");
        new Timer().scheduleAtFixedRate(new PortStatisticsPoller(), 0, PORT_STATISTICS_POLLING_INTERVAL);
        logger.debug("DTM() end");
    }

    /**
     * Returns DTM singleton. Creates one if necessary.
     *
     * @return DTM singleton
     */
    public static DTM getInstance() {
        logger.debug("getInstance() begin");
        synchronized (DTM.class) {
            if (singleton == null) {
                logger.debug("Creating DTM singleton");
                singleton = new DTM();
            }
        }
        logger.debug("getInstance() end");
        return singleton;
    }

    /**
     * Updates configuration.
     *
     * @param configData configuration
     */
    public synchronized void setConfigData(ConfigData configData) {
        logger.debug("setConfigData(ConfigData) begin");
        logger.debug("Updating configuration...");
        if (configData == null) {
            throw new IllegalArgumentException("Configuration data cannot be null");
        }
        if (configData.getInCommunicationList() != null) {
            logger.debug("Ignoring in communication list");
        }
        if (configData.getOutCommunicationList() == null) {
            throw new IllegalArgumentException("Out communication list cannot be null");
        }
        if (configData.getOutCommunicationList().size() != 1) {
            throw new IllegalArgumentException("Out communication list has to contain exactly one entry");
        }
        if (configData.getOutCommunicationList().get(0) == null) {
            throw new IllegalArgumentException("Out communication cannot be null");
        }
        if (configData.getOutCommunicationList().get(0).getConnectingTunnels() == null) {
            throw new IllegalArgumentException("Connecting tunnels for out communication cannot be null");
        }
        if (configData.getOutCommunicationList().get(0).getConnectingTunnels().size() != LINK_COUNT) {
            throw new IllegalArgumentException("DTM v. 1.0 supports only exactly " + LINK_COUNT + " unique tunnels on unique links");
        }
        List<TunnelID> configDataTunnelIDs = new ArrayList<>();
        List<LinkID> configDataLinkIDs = new ArrayList<>();
        for (Tunnel tunnel : configData.getOutCommunicationList().get(0).getConnectingTunnels()) {
            if (configDataTunnelIDs.contains(tunnel.getTunnelID())) {
                throw new IllegalArgumentException("Tunnel IDs are not unique");
            }
            configDataTunnelIDs.add(tunnel.getTunnelID());
            if (configDataLinkIDs.contains(tunnel.getLink().getLinkID())) {
                throw new IllegalArgumentException("Link IDs are not unique");
            }
            configDataLinkIDs.add(tunnel.getLink().getLinkID());
        }
        this.configData = configData;
        tunnelIDs = Arrays.copyOf(configDataTunnelIDs.toArray(), LINK_COUNT, TunnelID[].class);
        linkIDs = Arrays.copyOf(configDataLinkIDs.toArray(), LINK_COUNT, LinkID[].class);

        // initialize
        referenceVector = new RVector();
        compensationVector = new CVector();
        for (int i = 0; i < LINK_COUNT; i++) {
            referenceVector.addVectorValueForLink(linkIDs[i], 1);
            compensationVector.addVectorValueForLink(linkIDs[i], 0);
        }
        transmittedBytes = new long[LINK_COUNT];
        transmittedBytesStart = new long[LINK_COUNT];
        compensatingToTunnel = 0;
        logger.debug("setConfigData(ConfigData) end");
    }

    /**
     * Returns configuration.
     *
     * @return configuration
     */
    public ConfigData getConfigData() {
        logger.debug("getConfigData() one-liner");
        return configData;
    }

    /**
     * Updates reference vector. Does nothing if provided with null value.
     *
     * @param referenceVector reference vector
     */
    public synchronized void setReferenceVector(RVector referenceVector) {
        logger.debug("setReferenceVector(RVector) begin");
        if (configData == null) {
            throw new IllegalArgumentException("DTM has not been configured yet");
        }
        if (referenceVector == null) {
            logger.debug("Not updating Reference vector");
            logger.debug("setReferenceVector(RVector) end");
            return;
        }
        if (configData == null) {
            throw new IllegalArgumentException("DTM has not been configured yet");
        }
        if (referenceVector.getVectorValues() == null) {
            throw new IllegalArgumentException("Reference vector values cannot be null");
        }
        if (referenceVector.getVectorValues().size() != LINK_COUNT) {
            throw new IllegalArgumentException("DTM v. 1.0 supports only exactly " + LINK_COUNT + " links");
        }
        for (LinkID linkID : linkIDs) {
            boolean found = false;
            for (VectorValue vectorValue : referenceVector.getVectorValues()) {
                if (linkID.equals(vectorValue.getLinkID())) {
                    found = true;
                }
            }
            if (!found) {
                throw new IllegalArgumentException("Reference vector does not contain value for " + linkID);
            }
        }
        for (VectorValue vectorValue : referenceVector.getVectorValues()) {
            if (vectorValue.getValue() <= 0) {
                throw new IllegalArgumentException(vectorValue + " is not strictly positive");
            }
        }
        this.referenceVector = referenceVector;
        logger.debug("Reference vector set to " + referenceVector);
        logger.debug("setReferenceVector(RVector) end");
    }

    /**
     * Returns current reference vector.
     *
     * @return current reference vector
     */
    public RVector getReferenceVector() {
        logger.debug("getReferenceVector() one-liner");
        return referenceVector;
    }

    /**
     * Updates compensation vector.
     *
     * @param compensationVector compensation vector
     */
    public synchronized void setCompensationVector(CVector compensationVector) {
        logger.debug("setCompensationVector(CVector) begin");
        if (configData == null) {
            throw new IllegalArgumentException("DTM has not been configured yet");
        }
        if (compensationVector == null) {
            throw new IllegalArgumentException("Compensation vector cannot be null");
        }
        if (compensationVector.getVectorValues() == null) {
            throw new IllegalArgumentException("Compensation vector values cannot be null");
        }
        if (compensationVector.getVectorValues().size() != LINK_COUNT) {
            throw new IllegalArgumentException("DTM v. 1.0 supports only exactly " + LINK_COUNT + " links");
        }
        for (LinkID linkID : linkIDs) {
            boolean found = false;
            for (VectorValue vectorValue : compensationVector.getVectorValues()) {
                if (linkID.equals(vectorValue.getLinkID())) {
                    found = true;
                }
            }
            if (!found) {
                throw new IllegalArgumentException("Compensation vector does not contain value for " + linkID);
            }
        }
        long sum = 0;
        for (VectorValue vectorValue : compensationVector.getVectorValues()) {
            sum += vectorValue.getValue();
        }
        if (sum != 0) {
            throw new IllegalArgumentException("Compensation vector does not sum to zero");
        }
        this.compensationVector = compensationVector;
        logger.debug("Compensation vector set to " + compensationVector);
        resetTransmittedBytesStart();
        if (compensationVector.getVectorValueForLink(linkIDs[0]) > 0) {
            compensatingToTunnel = 1;
        } else if (compensationVector.getVectorValueForLink(linkIDs[1]) > 0) {
            compensatingToTunnel = 2;
        } else {
            compensatingToTunnel = 0;
        }
        logger.debug("Compensating to tunnel " + compensatingToTunnel + (compensatingToTunnel == 0 ? " (not compensating)" : ""));
        logger.debug("setCompensationVector(CVector) end");
    }

    /**
     * Returns current compensation vector.
     *
     * @return current compensation vector
     */
    public CVector getCompensationVector() {
        logger.debug("getCompensationVector() one-liner");
        return compensationVector;
    }

    /**
     * Updates switch on which DTM operates. Currently, only one switch is
     * supported.
     *
     * @param sw OpenFlow switch
     * @throws IllegalArgumentException when the function is requested to change
     * the switch without setting it to null first
     */
    public synchronized void setSwitch(IOFSwitch sw) {
        logger.debug("setSwitch(IOFSwitch) begin");
        if (this.sw != null && sw != null && !sw.equals(this.sw)) {
            throw new IllegalArgumentException("DTM v1.0 supports only one switch");
        }
        this.sw = sw;
        logger.debug("setSwitch(IOFSwitch) end");
    }

    /**
     * Returns OpenFlow switch on which DTM operates.
     *
     * @return OpenFlow switch on which DTM operates
     */
    public IOFSwitch getSwitch() {
        logger.debug("getSwitch() one-liner");
        return sw;
    }

    /**
     * Updates traffic counters on switch ports 1..{@link #LINK_COUNT}.
     *
     * @param transmittedBytes traffic counters (in bytes)
     */
    private synchronized void setTransmittedBytes(long[] transmittedBytes) {
        logger.debug("setTransmittedBytes(long[]) begin");
        this.transmittedBytes = transmittedBytes;
        logger.debug("setTransmittedBytes(long[]) end");
    }

    /**
     * Returns traffic counters on switch ports 1..{@link #LINK_COUNT}.
     *
     * @return traffic counters (in bytes)
     */
    public long[] getTransmittedBytes() {
        logger.debug("getTransmittedBytes() one-liner");
        return transmittedBytes;
    }

    /**
     * Resets traffic start counters on switch ports 1..{@link #LINK_COUNT}.
     */
    private synchronized void resetTransmittedBytesStart() {
        logger.debug("resetTransmittedBytesStart() begin");
        transmittedBytesStart = transmittedBytes;
        logger.debug("Transmitted bytes start counters set to " + Arrays.toString(transmittedBytesStart));
        logger.debug("resetTransmittedBytesStart() end");
    }

    /**
     * Returns traffic start counters on switch ports 1..{@link #LINK_COUNT}.
     *
     * @return traffic counters (in bytes)
     */
    public long[] getTransmittedBytesStart() {
        logger.debug("getTransmittedBytesStart() one-liner");
        return transmittedBytesStart;
    }

    /**
     * Returns true if DTM is compensating the traffic.
     *
     * @return true if DTM is compensating the traffic
     * @see DTM#getCompensatingToTunnel()
     */
    public boolean isCompensating() {
        logger.debug("isCompensating() one-liner");
        return compensatingToTunnel != 0;
    }

    /**
     * Returns the id of the tunnel to which DTM is compensating. Returns null
     * if the traffic is not being compensated.
     *
     * @return id of the tunnel to which DTM is compensating; null if the
     * traffic is not being compensated
     * @see DTM#isCompensating()
     */
    public TunnelID getCompensatingToTunnel() {
        logger.debug("getCompensatingToTunnel() begin");
        if (compensatingToTunnel == 0) {
            return null;
        }
        logger.debug("getCompensatingToTunnel() end");
        return tunnelIDs[compensatingToTunnel - 1];
    }

    /**
     * Get tunnel for new flow.
     *
     * @return tunnel id.
     */
    public synchronized TunnelID getTunnel() {
        logger.debug("getTunnel() begin");
        if (configData == null) {
            throw new IllegalArgumentException("DTM has not been configured yet");
        }
        double[] C = {
            compensationVector.getVectorValueForLink(linkIDs[0]),
            compensationVector.getVectorValueForLink(linkIDs[1])
        };
        double[] R = {
            referenceVector.getVectorValueForLink(linkIDs[0]),
            referenceVector.getVectorValueForLink(linkIDs[1])
        };
        double[] normalizedR = {
            R[0] / (R[0] + R[1]),
            R[1] / (R[0] + R[1])
        };
        double[] invNormalizedR = {
            normalizedR[1],
            normalizedR[0]
        };
        double[] traffic = new double[LINK_COUNT];
        double trafficDAB = 0;
        for (int i = 0; i < LINK_COUNT; i++) {
            traffic[i] = transmittedBytes[i] - transmittedBytesStart[i];
            trafficDAB += traffic[i];
        }
        short tunnel;
        if (compensatingToTunnel != 0) { // compensate == true
            int ti = compensatingToTunnel - 1;
            if (traffic[ti] < C[ti] / invNormalizedR[ti]) {
                tunnel = compensatingToTunnel;
                logger.debug("Compensating to tunnel " + tunnel);
            } else {
                resetTransmittedBytesStart();
                if (compensatingToTunnel == 1) {
                    tunnel = 2;
                } else {
                    tunnel = 1;
                }
                logger.debug("Turning off compensation, sending flow to tunnel " + tunnel);
                compensatingToTunnel = 0; // compensate = false
            }
        } else {
            if (traffic[0] / trafficDAB <= normalizedR[0]) {
                tunnel = 1;
            } else {
                tunnel = 2;
            }
            logger.debug("Not compensating, sending flow to tunnel " + tunnel);
        }
        logger.debug("C: " + Arrays.toString(C));
        logger.debug("normalizedR: " + Arrays.toString(normalizedR));
        logger.debug("transmittedBytesStart: " + Arrays.toString(transmittedBytesStart));
        logger.debug("transmittedBytes: " + Arrays.toString(transmittedBytes));
        logger.debug("traffic: " + Arrays.toString(traffic) + ", trafficDAB: " + trafficDAB);
        logger.debug("tunnel: " + tunnel);
        logger.debug("getTunnel() end");
        return tunnelIDs[tunnel - 1];
    }
}
