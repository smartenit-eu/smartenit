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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.floodlightcontroller.core.IOFSwitch;

import org.apache.commons.net.util.SubnetUtils;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFStatisticsRequest;
import org.openflow.protocol.statistics.OFPortStatisticsReply;
import org.openflow.protocol.statistics.OFPortStatisticsRequest;
import org.openflow.protocol.statistics.OFStatistics;
import org.openflow.protocol.statistics.OFStatisticsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.ConfigData;
import eu.smartenit.sbox.db.dto.ConfigDataEntry;
import eu.smartenit.sbox.db.dto.LocalDCOfSwitchPorts;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.OperationModeSDN;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.db.dto.TunnelInfo;
import eu.smartenit.sbox.db.dto.VectorValue;
import net.floodlightcontroller.core.IFloodlightProviderService;

/**
 * Main DTM class. Includes port statistics poller. Holds information on current
 * {@link CVector compensation vector}, {@link RVector reference vector}, and
 * traffic transmitted.
 *
 * @author Grzegorz Rzym
 * @author Piotr Wydrych
 * @version 3.0
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
                if (configData == null) { // not configured
                    logger.debug("run() end (not configured)");
                    return;
                }

                if (configData.getOperationModeSDN() == OperationModeSDN.proactiveWithoutReferenceVector || configData.getOperationModeSDN() == OperationModeSDN.reactiveWithoutReferenceVector) {
                    logger.debug("run() finished - operation mode without statistcs");
                    return;
                }

                try {
                    List<Future<List<OFStatistics>>> futures = new ArrayList<>();
                    for (Short port : daRouterPorts) {
                        OFStatisticsRequest req = new OFStatisticsRequest();
                        req.setStatisticType(OFStatisticsType.PORT);
                        int requestLength = req.getLengthU();

                        OFPortStatisticsRequest specificReq = new OFPortStatisticsRequest();
                        specificReq.setPortNumber((short) port);
                        requestLength += specificReq.getLength();
                        req.setStatistics(Collections.singletonList((OFStatistics) specificReq));
                        req.setLengthU(requestLength);

                        futures.add(sw.getStatistics(req));
                    }
                    int i = 0;
                    for (Short port : daRouterPorts) {
                        long left = timeoutTime - System.nanoTime();
                        List<OFStatistics> result = futures.get(i).get(left > 0 ? left : 0, TimeUnit.NANOSECONDS);
                        if (result.isEmpty()) {
                            logger.debug("run() end (no result)");
                            return;
                        }
                        transmittedBytesMap.put(port, ((OFPortStatisticsReply) result.get(0)).getTransmitBytes());
                        //logger.debug("TRANSMITTED BYTES " + transmittedBytesMap.get(port) + " ON DA PORT " + port);
                        ++i;
                    }
                    DTM.this.transmittedBytesMapUpdated();
                } catch (IOException | InterruptedException | ExecutionException | TimeoutException ex) {
                    logger.error("Error during statistics polling", ex);
                }
            }
            logger.debug("run() end");
        }
    }
    private static final Logger logger = LoggerFactory.getLogger(DTM.class);

    /**
     * Number of inter-domain links per ISP. Current version of DTM supports
     * only ISPs with 2 inter-domain links
     */
    public static final int LINK_COUNT = 2;

    /**
     * Polling interval in ms.
     */
    public static final int PORT_STATISTICS_POLLING_INTERVAL = 1000;
    public OFPacketIn msg;

    private static DTM singleton;

    private ConfigData configData;
    private RVector referenceVector;
    private CVector compensationVector;
    private IOFSwitch sw;
    private int daRouterOfPortNumber;
    private short compensatingToTunnel; // compensate = compensatingToTunnel != 0

    private Set<Short> daRouterPorts;

    private IFloodlightProviderService floodlightProvider;

    Map<NetworkAddressIPv4, Long> cVectorMap = new HashMap<>();
    Map<Short, Long> daRouterCVectorMap = new HashMap<>();
    Map<NetworkAddressIPv4, Long> rVectorMap = new HashMap<>();
    Map<Short, Long> daRouterRVectorMap = new HashMap<>();
    Map<Short, Long> transmittedBytesMap = new HashMap<>();
    Map<Short, Long> transmittedBytesStartMap = new HashMap<>();
    Map<Short, NetworkAddressIPv4> compensatingToTunnelMap = new HashMap<>();
    Map<Short, Boolean> compensatingMap = new HashMap<>();
    Map<Short, Short> daRouterCompenstatingToTunnelMap = new HashMap<>();
    Map<NetworkAddressIPv4, Short> dcPrefixOutOfPortMap = new HashMap<>();
    Map<Integer, ArrayList<Short>> dcNumberOfPortsMap = new HashMap<>();
    Map<Integer, Short> dcNumberCompensatingToTunnelMap = new HashMap<>();
    Map<Integer, Boolean> dcNumberCompensateMap = new HashMap<>();
    Map<Integer, ArrayList<Double>> dcNumberInvRMap = new HashMap<>();
    Map<Integer, NetworkAddressIPv4> dcNumberDCAddress = new HashMap<>();

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
	 * Sets DTM instance to null. When the instance will be requested next time
	 * it will be created. This method is mainly meant to be using in tests.
	 * 
	 */
    public static void resetInstance() {
    	singleton = null;
    }
    
    public IFloodlightProviderService getFloodlightProvider() {
        return floodlightProvider;
    }

    public void setFloodlightProvider(IFloodlightProviderService floodlightProvider) {
        this.floodlightProvider = floodlightProvider;
    }

    /**
     * Updates configuration.
     *
     * @param configData configuration
     */
    public synchronized void setConfigData(ConfigData configData) throws IllegalArgumentException {
        logger.debug("setConfigData(ConfigData) begin");
        logger.debug("Updating configuration...");
        validateConfigData(configData);

        this.configData = configData;
        daRouterPorts = getAllDARouterPorts(configData);
        logger.debug("All DA Router ports: " + daRouterPorts.toString());

        int i = 1;
        for (ConfigDataEntry configDataEntry : configData.getEntries()) {
            ArrayList<Short> ports = new ArrayList<Short>();
            for (TunnelInfo tunnelInfo : configDataEntry.getTunnels()) {
                ports.add((short) tunnelInfo.getDaRouterOfPortNumber());
            }
            dcNumberOfPortsMap.put(i, ports);
            dcNumberDCAddress.put(i, configDataEntry.getRemoteDcPrefix());
            i++;
        }

        //init DA Router
        for (LocalDCOfSwitchPorts localDCOfSwitchPorts
                : configData.getLocalDCPortsConfig()) {
            for (Integer localDCOfSwitchPortNumber : localDCOfSwitchPorts.getLocalDCOfSwitchPortNumbers()) {
                for (ConfigDataEntry configDataEntry : configData.getEntries()) {
                    for (TunnelInfo tunnelInfo : configDataEntry.getTunnels()) {
                        Flows.init(sw, floodlightProvider, null, localDCOfSwitchPortNumber.shortValue(), (short) tunnelInfo.getDaRouterOfPortNumber());
                    }
                }
            }

        }

        logger.debug("setConfigData(ConfigData) end");
    }

    /**
     * Returns all switch OF port numbers
     *
     * @param configData
     * @return switch OF port numbers
     */
    private Set<Short> getAllDARouterPorts(ConfigData configData) {
        Set<Short> daRouterPorts = new HashSet<>();

        for (ConfigDataEntry configDataEntry : configData.getEntries()) {
            for (TunnelInfo tunnelInfo : configDataEntry.getTunnels()) {
                daRouterPorts.add((short) (tunnelInfo.getDaRouterOfPortNumber()));
            }
        }
        return daRouterPorts;
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
    public synchronized void setReferenceVector(RVector referenceVector) throws IllegalArgumentException {
        logger.debug("setReferenceVector(RVector) begin");
        if (configData == null) {
            throw new IllegalArgumentException("DTM has not been configured yet (ConfigData is null)");
        }

        if (referenceVector == null) {
            logger.debug("Not updating Reference vector");
            logger.debug("setReferenceVector(RVector) end");
            return;
        }

        validateReferenceVector(referenceVector);

        this.referenceVector = referenceVector;
        for (VectorValue vectorValue : referenceVector.getVectorValues()) {
            rVectorMap.put(vectorValue.getTunnelEndPrefix(), vectorValue.getValue());
        }

        for (VectorValue vectorValue : referenceVector.getVectorValues()) {
            String subnet = vectorValue.getTunnelEndPrefix().getPrefix() + "/" + Integer.toString(vectorValue.getTunnelEndPrefix().getPrefixLength());
            SubnetUtils utils = new SubnetUtils(subnet);
            for (ConfigDataEntry configDataEntry : configData.getEntries()) {
                for (TunnelInfo tunnelInfo : configDataEntry.getTunnels()) {
                    if (utils.getInfo().isInRange(tunnelInfo.getTunnelID().getRemoteTunnelEndAddress().getPrefix())) {
                        daRouterRVectorMap.put((short) tunnelInfo.getDaRouterOfPortNumber(), vectorValue.getValue());
                    }
                }
            }
        }
        calculateRInvMap(referenceVector);

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
            throw new IllegalArgumentException("DTM has not been configured yet (ConfigData is null)");
        }
        validateCompensationVector(compensationVector);

        this.compensationVector = compensationVector;
        logger.debug("Compensation vector set to " + compensationVector);

        int i = 1;
        for (VectorValue vectorValue : compensationVector.getVectorValues()) {
            String subnet = vectorValue.getTunnelEndPrefix().getPrefix() + "/" + Integer.toString(vectorValue.getTunnelEndPrefix().getPrefixLength());
            SubnetUtils utils = new SubnetUtils(subnet);
            for (ConfigDataEntry configDataEntry : configData.getEntries()) {
                dcNumberCompensateMap.put(i, Boolean.TRUE); //TODO
                for (TunnelInfo tunnelInfo : configDataEntry.getTunnels()) {
                    if (utils.getInfo().isInRange(tunnelInfo.getTunnelID().getRemoteTunnelEndAddress().getPrefix())) {
                        daRouterCVectorMap.put((short) tunnelInfo.getDaRouterOfPortNumber(), vectorValue.getValue());
                        if (configData.getOperationModeSDN() == OperationModeSDN.proactiveWithReferenceVector || configData.getOperationModeSDN() == OperationModeSDN.reactiveWithReferenceVector) {
                            transmittedBytesStartMap.put((short) tunnelInfo.getDaRouterOfPortNumber(), transmittedBytesMap.get((short) tunnelInfo.getDaRouterOfPortNumber()));
                            if (vectorValue.getValue() > 0) {
                                daRouterCompenstatingToTunnelMap.put((short) tunnelInfo.getDaRouterOfPortNumber(), (short) 1);
                            }
                            if (vectorValue.getValue() < 0) {
                                daRouterCompenstatingToTunnelMap.put((short) tunnelInfo.getDaRouterOfPortNumber(), (short) -1);
                            }
                            if (vectorValue.getValue() == 0) {
                                daRouterCompenstatingToTunnelMap.put((short) tunnelInfo.getDaRouterOfPortNumber(), (short) 0);
                            }
                        }
                    }
                }
                i++;
            }
        }

        for (VectorValue vectorValue : compensationVector.getVectorValues()) {
            cVectorMap.put(vectorValue.getTunnelEndPrefix(), vectorValue.getValue());
            if (vectorValue.getValue() > 0) {
                compensatingMap.put((short) compensationVector.getSourceAsNumber(), true);
                compensatingToTunnelMap.put((short) compensationVector.getSourceAsNumber(), vectorValue.getTunnelEndPrefix());
            }
        }

        if (configData.getOperationModeSDN() == OperationModeSDN.proactiveWithReferenceVector || configData.getOperationModeSDN() == OperationModeSDN.proactiveWithoutReferenceVector) {
            sendStaticFlowRule(getProactiveOutOfPortNumber());
        }
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

    private synchronized void transmittedBytesMapUpdated() {
        if (configData.getOperationModeSDN() == OperationModeSDN.proactiveWithReferenceVector) {
            logger.debug("transmittedBytesMapUpdated() begin");
            if (configData == null) {
                throw new IllegalArgumentException("DTM has not been configured yet");
            }

            if (cVectorMap.isEmpty() || rVectorMap.isEmpty()) {
                throw new IllegalArgumentException("Vectors are empty");
            }

            for (Map.Entry<Integer, ArrayList<Short>> entrySet : dcNumberOfPortsMap.entrySet()) {
                Integer dcNumber = entrySet.getKey();
                short i = 1;
                for (Short portNumber : entrySet.getValue()) {
                    if (daRouterCompenstatingToTunnelMap.get(portNumber) == 1) {
                        dcNumberCompensatingToTunnelMap.put(dcNumber, i);
                    }
                    ++i;
                }
            }

            for (Map.Entry<Integer, Boolean> compensate : dcNumberCompensateMap.entrySet()) {
                if (compensate.getValue()) {
                    if (transmittedBytesMap.get(dcNumberOfPortsMap.get(compensate.getKey()).get(dcNumberCompensatingToTunnelMap.get(compensate.getKey()) - 1))
                            - transmittedBytesStartMap.get(dcNumberOfPortsMap.get(compensate.getKey()).get(dcNumberCompensatingToTunnelMap.get(compensate.getKey()) - 1))
                            > daRouterCVectorMap.get(dcNumberOfPortsMap.get(compensate.getKey()).get(dcNumberCompensatingToTunnelMap.get(compensate.getKey()) - 1))
                            / dcNumberInvRMap.get(compensate.getKey()).get(dcNumberCompensatingToTunnelMap.get(compensate.getKey()) - 1)) {

                        resetTransmittedBytesStart(dcNumberOfPortsMap.get(compensate.getKey()).get(0));
                        resetTransmittedBytesStart(dcNumberOfPortsMap.get(compensate.getKey()).get(1));

                        dcNumberCompensatingToTunnelMap.remove(compensate.getKey());
                        int tunnel = dcNumberCompensatingToTunnelMap.get(compensate.getKey()).intValue();
                        tunnel = (tunnel % 2) + 1;
                        dcNumberCompensatingToTunnelMap.put(compensate.getKey(), (short) tunnel);
                        compensate.setValue(Boolean.FALSE);
                    }
                } else {
                    long traffic_1 = 0;
                    long trafficDA_B = 0;
                    traffic_1 = transmittedBytesMap.get(dcNumberOfPortsMap.get(compensate.getKey()).get(1))
                            - transmittedBytesStartMap.get(dcNumberOfPortsMap.get(compensate.getKey()).get(1));
                    trafficDA_B = traffic_1 + transmittedBytesMap.get(dcNumberOfPortsMap.get(compensate.getKey()).get(2))
                            - transmittedBytesStartMap.get(dcNumberOfPortsMap.get(compensate.getKey()).get(2));
                    dcNumberCompensatingToTunnelMap.remove(compensate.getKey());
                    if (traffic_1 / trafficDA_B <= dcNumberInvRMap.get(compensate.getKey()).get(1)) {
                        dcNumberCompensatingToTunnelMap.put(compensate.getKey(), (short) 1);
                    } else {
                        dcNumberCompensatingToTunnelMap.put(compensate.getKey(), (short) 2);
                    }

                }

                //send flow rule
                String dcIP = dcNumberDCAddress.get(compensate.getKey()).getPrefix();
                int dcMask = dcNumberDCAddress.get(compensate.getKey()).getPrefixLength();
                short outPort = dcNumberCompensatingToTunnelMap.get(dcNumberOfPortsMap.get(compensate.getKey()).get(dcNumberCompensatingToTunnelMap.get(compensate.getKey()) - 1));

                Flows.mod(sw, floodlightProvider, null, dcIP, dcMask, outPort);
            }

            logger.debug("transmittedBytesMapUpdated() end");
        }
    }

    private synchronized void calculateRInvMap(RVector referenceVector) {
        long R[] = new long[2];
        int dcNumber = 0;

        for (Map.Entry<Short, Long> rVectorMap : daRouterRVectorMap.entrySet()) {
            if (rVectorMap.getValue() == referenceVector.getVectorValues().get(0).getValue()) {
                R[0] = rVectorMap.getValue();
            } else if (rVectorMap.getValue() == referenceVector.getVectorValues().get(1).getValue()) {
                R[1] = rVectorMap.getValue();
            }

            for (Map.Entry<Integer, ArrayList<Short>> entrySet : dcNumberOfPortsMap.entrySet()) {
                if (rVectorMap.getKey() == entrySet.getValue().get(0)) {
                    dcNumber = entrySet.getKey();
                }
            }
        }

        ArrayList<Double> rInv = new ArrayList<>();
        rInv.add((double) (R[1] / (R[0] + R[1])));
        rInv.add((double) (R[0] / (R[0] + R[1])));

        dcNumberInvRMap.put(dcNumber, rInv);

    }

    /**
     * Returns traffic counter on selected switch's port
     *
     * @return traffic counters (in bytes)
     * @param daRouterOfPortNumber OpenFlow port tumber
     */
    public long getTransmittedBytes(int daRouterOfPortNumber) {
        logger.debug("getTransmittedBytes() one-liner");
        return transmittedBytesMap.get((short) daRouterOfPortNumber);
    }

    /**
     * Returns statistics of transmitted bytes on all switch ports
     *
     * @return transmitted bytes map
     */
    public Map<Short, Long> getTransmittedBytesOnAllPorts() {
        return transmittedBytesMap;
    }

    /**
     * Resets traffic start counter on selected switch's port
     *
     * @param daRouterOfPortNumber
     */
    private synchronized void resetTransmittedBytesStart(short daRouterOfPortNumber) {
        logger.debug("resetTransmittedBytesStart() begin for OF port: " + daRouterOfPortNumber);
        transmittedBytesStartMap.put((short) daRouterOfPortNumber, transmittedBytesMap.get(daRouterOfPortNumber));
        logger.debug("Transmitted bytes start counters set to " + transmittedBytesStartMap.toString());
        logger.debug("resetTransmittedBytesStart() end");
    }

    /**
     * Returns traffic start counter on selected switch's port
     *
     * @return traffic counters (in bytes)
     * @param daRouterPortNumber OpenFlow port number
     */
    public long getTransmittedBytesStart(int daRouterPortNumber) {
        logger.debug("getTransmittedBytesStart() one-liner");
        return transmittedBytesStartMap.get(daRouterPortNumber);
    }

    /**
     * Updates OpenFlow PacketIn message on witch DTM operates.
     *
     * @param msg OFPacketIn packet
     */
    public void setOFPacketIn(OFPacketIn msg) {
        logger.debug("setOFPacketIn() one-liner");
        this.msg = msg;
    }

    /**
     * Returns OpenFlow PacetIn message.
     *
     * @return OpenFlow PacetIn message
     */
    public OFPacketIn getOFPacketIn() {
        return msg;
    }

    /**
     * Parses integer IP address to string
     *
     * @param i
     * @return IP address as a string
     */
    public String intToStringIp(int i) {
        return ((i >> 24) & 0xFF) + "."
                + ((i >> 16) & 0xFF) + "."
                + ((i >> 8) & 0xFF) + "."
                + (i & 0xFF);
    }

    public void sendStaticFlowRule(Map<NetworkAddressIPv4, Short> dcPrefixOutOfPortMap) {
        for (NetworkAddressIPv4 netAddr : dcPrefixOutOfPortMap.keySet()) {
            Flows.mod(sw, floodlightProvider, null, netAddr.getPrefix(), netAddr.getPrefixLength(), dcPrefixOutOfPortMap.get(netAddr));
        }
    }

    public synchronized Map<NetworkAddressIPv4, Short> getProactiveOutOfPortNumber() {
        logger.debug("getOutOfPortNumberProactiveWithoutReference() begin");
        if (configData == null) {
            throw new IllegalArgumentException("DTM has not been configured yet");
        }

        if (cVectorMap.isEmpty()) {
            throw new IllegalArgumentException("Compensation vector empty");
        }

        for (VectorValue cVectorValue : compensationVector.getVectorValues()) {
            if (cVectorValue.getValue() >= 0) {
                String subnet = cVectorValue.getTunnelEndPrefix().getPrefix() + "/" + Integer.toString(cVectorValue.getTunnelEndPrefix().getPrefixLength());
                SubnetUtils utils = new SubnetUtils(subnet);
                for (ConfigDataEntry configDataEntry : configData.getEntries()) {
                    for (TunnelInfo tunnelInfo : configDataEntry.getTunnels()) {
                        if (utils.getInfo().isInRange(tunnelInfo.getTunnelID().getRemoteTunnelEndAddress().getPrefix())) {
                            dcPrefixOutOfPortMap.put(configDataEntry.getRemoteDcPrefix(), (short) tunnelInfo.getDaRouterOfPortNumber());
                        }
                    }
                }
            }
        }
        logger.debug("getOutOfPortNumberProactiveWithoutReference() end");

        return dcPrefixOutOfPortMap;
    }

    /**
     * Returns output port of switch for a new flow.
     *
     * @return output port number
     */
    public synchronized short getReactiveWithReferenceOutOfPortNumber() {
        logger.debug("getOutOfPortNumberReactiveWithReference() begin");
        if (configData == null) {
            throw new IllegalArgumentException("DTM has not been configured yet");
        }

        if (rVectorMap.isEmpty() || cVectorMap.isEmpty()) {
            throw new IllegalArgumentException("Vectors not set");
        }

        OFPacketIn pi = getOFPacketIn();
        String piDestAddr = intToStringIp(new OFMatch().loadFromPacket(pi.getPacketData(), pi.getInPort()).getNetworkDestination());

        logger.debug("Network dest = " + intToStringIp(new OFMatch().loadFromPacket(pi.getPacketData(), pi.getInPort()).getNetworkDestination()));

        short[] selectedDaRouterOfPortNumber = null;
        for (ConfigDataEntry entry : configData.getEntries()) {
            String subnet = entry.getRemoteDcPrefix().getPrefix() + "/" + Integer.toString(entry.getRemoteDcPrefix().getPrefixLength());
            SubnetUtils utils = new SubnetUtils(subnet);
            if (utils.getInfo().isInRange(piDestAddr)) {
                selectedDaRouterOfPortNumber = new short[]{
                    (short) entry.getTunnels().get(0).getDaRouterOfPortNumber(),
                    (short) entry.getTunnels().get(1).getDaRouterOfPortNumber()
                };
                logger.debug("IP " + piDestAddr + " in range " + subnet);
            }
        }

        if (selectedDaRouterOfPortNumber == null) {
            logger.debug("Destination host unreachable! Wrong IP address: " + piDestAddr);
            logger.debug("getOutOfPortNumberReactiveWithReference() end");
            return 0;
        }

        logger.debug("Selected DA router ports: " + Arrays.toString(selectedDaRouterOfPortNumber));

        double[] C = {
            daRouterCVectorMap.get(selectedDaRouterOfPortNumber[0]),
            daRouterCVectorMap.get(selectedDaRouterOfPortNumber[1]),};

        double[] R = {
            daRouterRVectorMap.get(selectedDaRouterOfPortNumber[0]),
            daRouterRVectorMap.get(selectedDaRouterOfPortNumber[1]),};

        if (daRouterCompenstatingToTunnelMap.get(selectedDaRouterOfPortNumber[0]) > 0) {
            compensatingToTunnel = 1;
        } else if (daRouterCompenstatingToTunnelMap.get(selectedDaRouterOfPortNumber[1]) > 0) {
            compensatingToTunnel = 2;
        } else {
            compensatingToTunnel = 0;
        }

        double[] normalizedR = {
            R[0] / (R[0] + R[1]),
            R[1] / (R[0] + R[1])
        };
        double[] invNormalizedR = {
            normalizedR[1],
            normalizedR[0]
        };
        double[] traffic = new double[LINK_COUNT];
        double trafficDCDC = 0;
        for (int i = 0; i < LINK_COUNT; i++) {
            traffic[i] = transmittedBytesMap.get(selectedDaRouterOfPortNumber[i]) - transmittedBytesStartMap.get(selectedDaRouterOfPortNumber[i]);
            trafficDCDC += traffic[i];
        }
        int tunnel;
        if (compensatingToTunnel != 0) { // compensate == true
            int ti = compensatingToTunnel - 1;
            if (traffic[ti] < C[ti] / invNormalizedR[ti]) {
                tunnel = compensatingToTunnel;
                logger.debug("Compensating to tunnel " + tunnel);
            } else {
                resetTransmittedBytesStart(selectedDaRouterOfPortNumber[0]);
                resetTransmittedBytesStart(selectedDaRouterOfPortNumber[1]);
                if (compensatingToTunnel == 1) {
                    tunnel = 2;
                } else {
                    tunnel = 1;
                }
                logger.debug("Turning off compensation, sending flow to tunnel " + tunnel);
                compensatingToTunnel = 0; // compensate = false
                daRouterCompenstatingToTunnelMap.put(selectedDaRouterOfPortNumber[0], (short) 0);
                daRouterCompenstatingToTunnelMap.put(selectedDaRouterOfPortNumber[1], (short) 0);
            }
        } else {
            if (traffic[0] / trafficDCDC <= normalizedR[0]) {
                tunnel = 1;
            } else {
                tunnel = 2;
            }
            logger.debug("Not compensating, sending flow to tunnel " + tunnel);
        }
        // change tunnel to DA Router port number
        if (tunnel == 1) {
            daRouterOfPortNumber = selectedDaRouterOfPortNumber[0];
        } else if (tunnel == 2) {
            daRouterOfPortNumber = selectedDaRouterOfPortNumber[1];
        } else {
            daRouterOfPortNumber = 0; //drop flow
        }
        logger.debug("Summary:");
        logger.debug("\t C: " + Arrays.toString(C));
        logger.debug("\t normalizedR: " + Arrays.toString(normalizedR));
        logger.debug("\t transmittedBytesStart: " + transmittedBytesStartMap.toString());
        logger.debug("\t transmittedBytes: " + transmittedBytesMap.toString());
        logger.debug("\t traffic: " + Arrays.toString(traffic) + ", trafficDAB: " + trafficDCDC);
        logger.debug("\t OFPortNumber: " + daRouterOfPortNumber);
        logger.debug("getOutOfPortNumberReactiveWithReference() end");
        return (short) daRouterOfPortNumber;
    }

    public synchronized short getReactiveWithoutReferenceOutOfPortNumber() {
        logger.debug("getOutOfPortNumberReactiveWithoutReference() begin");
        if (configData == null) {
            throw new IllegalArgumentException("DTM has not been configured yet");
        }

        if (cVectorMap.isEmpty()) {
            throw new IllegalArgumentException("Compensation vector empty");
        }

        OFPacketIn pi = getOFPacketIn();
        String piDestAddr = intToStringIp(new OFMatch().loadFromPacket(pi.getPacketData(), pi.getInPort()).getNetworkDestination());

        logger.debug("Network dest = " + intToStringIp(new OFMatch().loadFromPacket(pi.getPacketData(), pi.getInPort()).getNetworkDestination()));

        short[] selectedDaRouterOfPortNumber = null;
        for (ConfigDataEntry entry : configData.getEntries()) {
            String subnet = entry.getRemoteDcPrefix().getPrefix() + "/" + Integer.toString(entry.getRemoteDcPrefix().getPrefixLength());
            SubnetUtils utils = new SubnetUtils(subnet);
            if (utils.getInfo().isInRange(piDestAddr)) {
                selectedDaRouterOfPortNumber = new short[]{
                    (short) entry.getTunnels().get(0).getDaRouterOfPortNumber(),
                    (short) entry.getTunnels().get(1).getDaRouterOfPortNumber()
                };
                logger.debug("IP " + piDestAddr + " in range " + subnet);
            }
        }

        if (selectedDaRouterOfPortNumber == null) {
            logger.debug("Destination host unreachable! Wrong IP address: " + piDestAddr);
            logger.debug("getOutOfPortNumberReactiveWithoutReference");
            return 0;
        }

        logger.debug("Selected DA router ports: " + Arrays.toString(selectedDaRouterOfPortNumber));

        double[] C = {
            daRouterCVectorMap.get(selectedDaRouterOfPortNumber[0]),
            daRouterCVectorMap.get(selectedDaRouterOfPortNumber[1]),};

        if (C[0] >= 0) {
            daRouterOfPortNumber = selectedDaRouterOfPortNumber[0];
        } else if (C[1] >= 0) {
            daRouterOfPortNumber = selectedDaRouterOfPortNumber[1];
        } else {
            daRouterOfPortNumber = 0;
        }

        logger.debug("Summary:");
        logger.debug("\t OFPortNumber: " + daRouterOfPortNumber);
        logger.debug("getOutOfPortNumberReactiveWithoutReference() end");
        return (short) daRouterOfPortNumber;
    }

    private void validateConfigData(ConfigData configData) throws IllegalArgumentException {
        if (configData == null) {
            throw new IllegalArgumentException("Configuration data cannot be null");
        }

        if (configData.getEntries() == null || configData.getEntries().size() == 0) {
            throw new IllegalArgumentException("Configuration data entries cannot be null or empty");
        }

        for (ConfigDataEntry entry : configData.getEntries()) {
            if (entry.getRemoteDcPrefix() == null) {
                throw new IllegalArgumentException("RemoteDCPrefix cannot be null");
            }

            if (entry.getDaRouterOfDPID() == null) {
                throw new IllegalArgumentException("DaRouterOfDPID cannot be null");
            }

            if (entry.getTunnels() == null) {
                throw new IllegalArgumentException("Tunnels cannot be null");
            }

            if (entry.getTunnels().size() < LINK_COUNT) {
                throw new IllegalArgumentException("Number of tunnels below " + LINK_COUNT);
            }
        }

        if (configData.getOperationModeSDN() == null) {
            throw new IllegalArgumentException("Operation mode cannot be null");
        }

        if (configData.getLocalDCPortsConfig() == null || configData.getLocalDCPortsConfig().size() == 0) {
            throw new IllegalArgumentException("List of LocalDCPortsConfig cannot be empty or null");
        }

        for (LocalDCOfSwitchPorts localDCOfSwitchPorts : configData.getLocalDCPortsConfig()) {
            if (localDCOfSwitchPorts.getDaRouterOfDPID() == null) {
                throw new IllegalArgumentException("DA route OF DPID cannot be null");
            }
            if (localDCOfSwitchPorts.getLocalDCOfSwitchPortNumbers() == null || localDCOfSwitchPorts.getLocalDCOfSwitchPortNumbers().size() == 0) {
                throw new IllegalArgumentException("List of Local DC OF Switch Ports cannot be empty or null");
            }

        }
    }

    private void validateReferenceVector(RVector referenceVector) throws IllegalArgumentException {
        if (referenceVector.getVectorValues() == null) {
            throw new IllegalArgumentException("Reference vector values cannot be null");
        }

        if (referenceVector.getVectorValues().size() != 2) {
            throw new IllegalArgumentException(
                    "Reference vector must have exactly 2 values since this implementaion supports exactly 2 incoming inter-domain links.");
        }

        for (VectorValue vectorValue : referenceVector.getVectorValues()) {
            if (vectorValue.getValue() <= 0) {
                throw new IllegalArgumentException(vectorValue + " is not strictly positive");
            }
        }
    }

    private void validateCompensationVector(CVector compensationVector) {
        if (compensationVector == null) {
            throw new IllegalArgumentException("Compensation vector cannot be null");
        }
        if (compensationVector.getVectorValues() == null) {
            throw new IllegalArgumentException("Compensation vector values cannot be null");
        }
        if (compensationVector.getVectorValues().size() != 2) {
            throw new IllegalArgumentException(
                    "Compensation vector must have exactly 2 values since this implementation supports exactly 2 incoming inter-domain links.");
        }

        long sum = 0;
        for (VectorValue vectorValue : compensationVector.getVectorValues()) {
            sum += vectorValue.getValue();
        }
        if (sum != 0) {
            throw new IllegalArgumentException("Compensation vector does not sum to zero");
        }
    }

}
