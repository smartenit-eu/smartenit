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
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.Direction;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.SimpleTunnelID;
import eu.smartenit.sbox.db.dto.Tunnel;
import eu.smartenit.sbox.db.dto.TunnelID;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import org.jboss.netty.channel.Channel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openflow.protocol.OFFeaturesReply;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPhysicalPort;
import org.openflow.protocol.OFStatisticsRequest;
import org.openflow.protocol.statistics.OFDescriptionStatistics;
import org.openflow.protocol.statistics.OFPortStatisticsReply;
import org.openflow.protocol.statistics.OFPortStatisticsRequest;
import org.openflow.protocol.statistics.OFStatistics;
import org.openflow.protocol.statistics.OFStatisticsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main DTM class test.
 *
 * @author Piotr Wydrych
 */
public class DTMTest {

    private static final Logger logger = LoggerFactory.getLogger(DTMTest.class);

    public static final String[] TUNNEL_NAMES = {
        "tunnel1",
        "tunnel2"
    };
    public static final int[] TUNNEL_NUMBERS = {
        1,
        2
    };
    public static final String[] LINK_IDS = {
        "link1",
        "link2"
    };
    public static final String[] ISP_IDS = {
        "isp1"
    };

    private static final class FakeStatisticsProviderOFSwitch implements IOFSwitch {

        private final Map<Short, Long> traffic = new HashMap<>();

        public synchronized void setTrafficForPort(short port, long transmittedBytes) {
            traffic.put(port, transmittedBytes);
        }

        @Override
        public synchronized Future<List<OFStatistics>> getStatistics(OFStatisticsRequest request) throws IOException {
            if (request == null) {
                throw new UnsupportedOperationException("Not supported.");
            }
            if (request.getStatisticType() == null || !request.getStatisticType().equals(OFStatisticsType.PORT)) {
                throw new UnsupportedOperationException("Not supported.");
            }
            if (request.getStatistics() == null || request.getStatistics().size() != 1) {
                throw new UnsupportedOperationException("Not supported.");
            }
            if (!request.getStatistics().get(0).getClass().equals(OFPortStatisticsRequest.class)) {
                throw new UnsupportedOperationException("Not supported.");
            }
            final short port = ((OFPortStatisticsRequest) request.getStatistics().get(0)).getPortNumber();
            return new Future<List<OFStatistics>>() {

                @Override
                public boolean cancel(boolean mayInterruptIfRunning) {
                    return false;
                }

                @Override
                public boolean isCancelled() {
                    return false;
                }

                @Override
                public boolean isDone() {
                    return true;
                }

                @Override
                public List<OFStatistics> get() throws InterruptedException, ExecutionException {
                    OFPortStatisticsReply reply = new OFPortStatisticsReply();
                    reply.setTransmitBytes(traffic.get(port));
                    List<OFStatistics> result = new ArrayList<>();
                    result.add(reply);
                    return result;
                }

                @Override
                public List<OFStatistics> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                    return get();
                }
            };
        }

        // <editor-fold defaultstate="collapsed" desc=" Not implemented methods ">
        @Override
        public void write(OFMessage m, FloodlightContext bc) throws IOException {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void write(List<OFMessage> msglist, FloodlightContext bc) throws IOException {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void disconnectOutputStream() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Channel getChannel() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public int getBuffers() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public int getActions() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public int getCapabilities() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public byte getTables() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setFeaturesReply(OFFeaturesReply featuresReply) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setSwitchProperties(OFDescriptionStatistics description) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Collection<OFPhysicalPort> getEnabledPorts() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Collection<Short> getEnabledPortNumbers() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public OFPhysicalPort getPort(short portNumber) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public OFPhysicalPort getPort(String portName) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setPort(OFPhysicalPort port) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void deletePort(short portNumber) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void deletePort(String portName) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Collection<OFPhysicalPort> getPorts() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean portEnabled(short portName) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean portEnabled(String portName) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean portEnabled(OFPhysicalPort port) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public long getId() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getStringId() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Map<Object, Object> getAttributes() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Date getConnectedSince() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public int getNextTransactionId() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Future<OFFeaturesReply> getFeaturesReplyFromSwitch() throws IOException {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void deliverOFFeaturesReply(OFMessage reply) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void cancelFeaturesReply(int transactionId) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean isConnected() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setConnected(boolean connected) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public IFloodlightProviderService.Role getRole() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean isActive() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void deliverStatisticsReply(OFMessage reply) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void cancelStatisticsReply(int transactionId) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void cancelAllStatisticsReplies() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean hasAttribute(String name) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Object getAttribute(String name) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setAttribute(String name, Object value) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Object removeAttribute(String name) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void clearAllFlowMods() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean updateBroadcastCache(Long entry, Short port) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Map<Short, Long> getPortBroadcastHits() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void sendStatsQuery(OFStatisticsRequest request, int xid, IOFMessageListener caller) throws IOException {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void flush() {
            throw new UnsupportedOperationException("Not supported.");
        }
        // </editor-fold>
    }

    private static final class DTMTestCase {

        private final class State {

            // input
            public final long[] transmittedBytes;

            // output
            public final TunnelID tunnel;

            public State(long[] transmittedBytes, short tunnel) {
                this.transmittedBytes = transmittedBytes;
                this.tunnel = new SimpleTunnelID(TUNNEL_NAMES[tunnel - 1], TUNNEL_NUMBERS[tunnel - 1]);
            }
        }

        public final String name;
        // input
        public final RVector referenceVector;
        public final CVector compensationVector;
        public final long[] transmittedBytesStart;

        public final List<State> states = new ArrayList<>();

        public DTMTestCase(long[] R, long[] C, long[] transmittedBytesStart) {
            this(null, R, C, transmittedBytesStart);
        }

        public DTMTestCase(String name, long[] R, long[] C, long[] transmittedBytesStart) {
            this.name = name;
            this.referenceVector = new RVector();
            for (int i = 0; i < R.length; i++) {
                this.referenceVector.addVectorValueForLink(new SimpleLinkID(LINK_IDS[i], ISP_IDS[0]), R[i]);
            }
            this.compensationVector = new CVector();
            for (int i = 0; i < C.length; i++) {
                this.compensationVector.addVectorValueForLink(new SimpleLinkID(LINK_IDS[i], ISP_IDS[0]), C[i]);
            }
            this.transmittedBytesStart = transmittedBytesStart;
        }

        public DTMTestCase addState(long[] transmittedBytes, short tunnel) {
            states.add(new State(transmittedBytes, tunnel));
            return this;
        }
    }

    private static final DTMTestCase[] DTM_TEST_CASES = {
        new DTMTestCase("Trivial-1",
        new long[]{1000000L, 1000000L}, // normalizedR: [0.5, 0.5]
        new long[]{0L, 0L},
        new long[]{0L, 0L}
        )
        .addState(new long[]{1000L, 0L}, (short) 2) // traffic: [1000, 0], trafficDAB: 1000. Not compensating, sending flow to tunnel 2
        ,
        new DTMTestCase("Trivial-2",
        new long[]{1000000L, 1000000L}, // normalizedR: [0.5, 0.5]
        new long[]{0L, 0L},
        new long[]{0L, 0L}
        )
        .addState(new long[]{0L, 1000L}, (short) 1) // traffic: [0, 1000], trafficDAB: 1000. Not compensating, sending flow to tunnel 1
        ,
        new DTMTestCase("ZD-1",
        new long[]{20000000000L, 10000000000L}, // normalizedR: [0.67, 0.33]
        new long[]{20000000L, -20000000L},
        new long[]{4400000000L, 5900000000L}
        )
        .addState(new long[]{4417000000L, 5900000000L}, (short) 1) // traffic: [17000000, 0], trafficDAB: 17000000. Compensating to tunnel 1
        .addState(new long[]{4460000064L, 5900000000L}, (short) 2) // traffic: [60000064, 0], trafficDAB: 60000064. Turning off compensation, sending flow to tunnel 2
        .addState(new long[]{4460000064L, 5900001560L}, (short) 1) // traffic: [0, 1560], trafficDAB: 1560. Not compensating, sending flow to tunnel 1
        .addState(new long[]{4460001264L, 5900001560L}, (short) 1) // traffic: [1200, 1560], trafficDAB: 2760. Not compensating, sending flow to tunnel 1
        .addState(new long[]{4460007896L, 5900003560L}, (short) 2) // traffic: [7832, 3560], trafficDAB: 11392. Not compensating, sending flow to tunnel 2
        .addState(new long[]{4460007896L, 5900004560L}, (short) 1) // traffic: [7832, 4560], trafficDAB: 12392. Not compensating, sending flow to tunnel 1
        .addState(new long[]{4460008096L, 5900004560L}, (short) 1) // traffic: [8032, 4560], trafficDAB: 12592. Not compensating, sending flow to tunnel 1
        .addState(new long[]{4460009996L, 5900004560L}, (short) 2) // traffic: [9932, 4560], trafficDAB: 14492. Not compensating, sending flow to tunnel 2
        .addState(new long[]{4460009996L, 5900004660L}, (short) 2) // traffic: [9932, 4660], trafficDAB: 14592. Not compensating, sending flow to tunnel 2
        .addState(new long[]{4460009996L, 5900004860L}, (short) 2) // traffic: [9932, 4860], trafficDAB: 14792. Not compensating, sending flow to tunnel 2
        .addState(new long[]{4460009996L, 5900005260L}, (short) 1) // traffic: [9932, 5260], trafficDAB: 15192. Not compensating, sending flow to tunnel 1
        .addState(new long[]{4460013996L, 5900005260L}, (short) 2) // traffic: [13932, 5260], trafficDAB: 19192. Not compensating, sending flow to tunnel 2
        ,
        new DTMTestCase("ZD-2",
        new long[]{20000000000L, 10000000000L}, // normalizedR: [0.67, 0.33]
        new long[]{-20000000L, 20000000L},
        new long[]{4400000000L, 5900000000L}
        )
        .addState(new long[]{4400000000L, 5912000000L}, (short) 2) // traffic: [0, 12000000], trafficDAB: 12000000. Compensating to tunnel 2
        .addState(new long[]{4400000000L, 5930000064L}, (short) 1) // traffic: [0, 30000064], trafficDAB: 30000064. Turning off compensation, sending flow to tunnel 1
        .addState(new long[]{4400000233L, 5930000064L}, (short) 2) // traffic: [233, 0], trafficDAB: 233. Not compensating, sending flow to tunnel 2
        .addState(new long[]{4400000233L, 5930000364L}, (short) 1) // traffic: [233, 300], trafficDAB: 533. Not compensating, sending flow to tunnel 1
        .addState(new long[]{4400001033L, 5930000364L}, (short) 2) // traffic: [1033, 300], trafficDAB: 1333. Not compensating, sending flow to tunnel 2
        .addState(new long[]{4400001033L, 5930000964L}, (short) 1) // traffic: [1033, 900], trafficDAB: 1933. Not compensating, sending flow to tunnel 1
        .addState(new long[]{4400001233L, 5930000964L}, (short) 1) // traffic: [1233, 900], trafficDAB: 2133. Not compensating, sending flow to tunnel 1
        .addState(new long[]{4400001733L, 5930000964L}, (short) 1) // traffic: [1733, 900], trafficDAB: 2633. Not compensating, sending flow to tunnel 1
        .addState(new long[]{4400002733L, 5930000964L}, (short) 2) // traffic: [2733, 900], trafficDAB: 3633. Not compensating, sending flow to tunnel 2
        .addState(new long[]{4400002733L, 5930001964L}, (short) 1) // traffic: [2733, 1900], trafficDAB: 4633. Not compensating, sending flow to tunnel 1
        .addState(new long[]{4400003755L, 5930001964L}, (short) 1) // traffic: [3755, 1900], trafficDAB: 5655. Not compensating, sending flow to tunnel 1
        .addState(new long[]{4400004555L, 5930001964L}, (short) 2) // traffic: [4555, 1900], trafficDAB: 6455. Not compensating, sending flow to tunnel 2
        .addState(new long[]{4400004555L, 5930002164L}, (short) 2) // traffic: [4555, 2100], trafficDAB: 6655. Not compensating, sending flow to tunnel 2
        ,
        new DTMTestCase("ZD-3",
        new long[]{25000000000L, 25000000000L}, // normalizedR: [0.50, 0.50]
        new long[]{10000000L, -10000000L},
        new long[]{6800000000L, 7800000000L}
        )
        .addState(new long[]{6800034560L, 7800000000L}, (short) 1) // traffic: [34560, 0], trafficDAB: 34560. Compensating to tunnel 1
        .addState(new long[]{6800006788L, 7800000000L}, (short) 1) // traffic: [6788, 0], trafficDAB: 6788. Compensating to tunnel 1
        .addState(new long[]{6805606788L, 7800000000L}, (short) 1) // traffic: [5606788, 0], trafficDAB: 5606788. Compensating to tunnel 1
        .addState(new long[]{6819606788L, 7800000000L}, (short) 1) // traffic: [19606788, 0], trafficDAB: 19606788. Compensating to tunnel 1
        .addState(new long[]{6820000123L, 7800000000L}, (short) 2) // traffic: [20000123, 0], trafficDAB: 20000123. Turning off compensation, sending flow to tunnel 2
        .addState(new long[]{6820000123L, 7800003459L}, (short) 1) // traffic: [0, 3459], trafficDAB: 3459. Not compensating, sending flow to tunnel 1
        .addState(new long[]{6820020123L, 7800003459L}, (short) 2) // traffic: [20000, 3459], trafficDAB: 23459. Not compensating, sending flow to tunnel 2
        .addState(new long[]{6820020123L, 7800023459L}, (short) 1) // traffic: [20000, 23459], trafficDAB: 43459. Not compensating, sending flow to tunnel 1
        .addState(new long[]{6820023123L, 7800023459L}, (short) 1) // traffic: [23000, 23459], trafficDAB: 46459. Not compensating, sending flow to tunnel 1
        .addState(new long[]{6820031123L, 7800023459L}, (short) 2) // traffic: [31000, 23459], trafficDAB: 54459. Not compensating, sending flow to tunnel 2
        .addState(new long[]{6820031123L, 7800043550L}, (short) 1) // traffic: [31000, 43550], trafficDAB: 74550. Not compensating, sending flow to tunnel 1
        .addState(new long[]{6820071123L, 7800043550L}, (short) 2) // traffic: [71000, 43550], trafficDAB: 114550. Not compensating, sending flow to tunnel 2
        .addState(new long[]{6820071123L, 7800073470L}, (short) 1) // traffic: [71000, 73470], trafficDAB: 144470. Not compensating, sending flow to tunnel 1
        .addState(new long[]{6820141117L, 7800073470L}, (short) 2) // traffic: [140994, 73470], trafficDAB: 214464. Not compensating, sending flow to tunnel 2
    };

    private static DTM dtm;

    /**
     *
     */
    @BeforeClass
    public static void beforeClass() {
        dtm = DTM.getInstance();
    }

    /**
     *
     */
    @Before
    public void before() {
        dtm.setSwitch(null);

        ConfigData configData = new ConfigData();
        DC2DCCommunication outCommunication = new DC2DCCommunication();
        outCommunication.setTrafficDirection(Direction.outcomingTraffic);
        Tunnel tunnel1 = new Tunnel();
        tunnel1.setTunnelID(new SimpleTunnelID(TUNNEL_NAMES[0], TUNNEL_NUMBERS[0]));
        Link link1 = new Link();
        link1.setLinkID(new SimpleLinkID(LINK_IDS[0], ISP_IDS[0]));
        link1.setTraversingTunnels(Arrays.asList(new Tunnel[]{tunnel1}));
        tunnel1.setLink(link1);
        Tunnel tunnel2 = new Tunnel();
        tunnel2.setTunnelID(new SimpleTunnelID(TUNNEL_NAMES[1], TUNNEL_NUMBERS[1]));
        Link link2 = new Link();
        link2.setLinkID(new SimpleLinkID(LINK_IDS[1], ISP_IDS[0]));
        link2.setTraversingTunnels(Arrays.asList(new Tunnel[]{tunnel2}));
        tunnel2.setLink(link2);
        outCommunication.setConnectingTunnels(Arrays.asList(new Tunnel[]{tunnel1, tunnel2}));
        configData.setOutCommunicationList(Arrays.asList(new DC2DCCommunication[]{outCommunication}));
        dtm.setConfigData(configData);
    }

    /**
     * Test if {@link DTM#getInstance()} returns the same value in subsequent
     * calls.
     */
    @Test
    public void testGetInstance() {
        Assert.assertSame(dtm, DTM.getInstance());
    }

    @Test
    public void testSetConfigData() {
        ConfigData configData = new ConfigData();
        try { // Configuration data cannot be null
            dtm.setConfigData(null);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }
        try { // Out communication list cannot be null
            dtm.setConfigData(configData);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }
        configData.setOutCommunicationList(Arrays.asList(new DC2DCCommunication[]{}));
        try { // Out communication list has to contain exactly one entry
            dtm.setConfigData(configData);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }
        configData.setOutCommunicationList(Arrays.asList(new DC2DCCommunication[]{null, null}));
        try { // Out communication list has to contain exactly one entry
            dtm.setConfigData(configData);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }
        configData.setOutCommunicationList(Arrays.asList(new DC2DCCommunication[]{null}));
        try { // Out communication cannot be null
            dtm.setConfigData(configData);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }
        DC2DCCommunication outCommunication = new DC2DCCommunication();
        configData.setOutCommunicationList(Arrays.asList(new DC2DCCommunication[]{outCommunication}));
        try { // Connecting tunnels for out communication cannot be null
            dtm.setConfigData(configData);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }
        outCommunication.setTrafficDirection(Direction.outcomingTraffic);
        outCommunication.setConnectingTunnels(Arrays.asList(new Tunnel[]{}));
        try { // DTM v. 1.0 supports only exactly 2 unique tunnels on unique links
            dtm.setConfigData(configData);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }
        outCommunication.setConnectingTunnels(Arrays.asList(new Tunnel[]{null, null, null}));
        try { // DTM v. 1.0 supports only exactly 2 unique tunnels on unique links
            dtm.setConfigData(configData);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }
        Tunnel tunnel1 = new Tunnel();
        tunnel1.setTunnelID(new SimpleTunnelID(TUNNEL_NAMES[0], TUNNEL_NUMBERS[0]));
        Link link1 = new Link();
        link1.setLinkID(new SimpleLinkID(LINK_IDS[0], ISP_IDS[0]));
        link1.setTraversingTunnels(Arrays.asList(new Tunnel[]{tunnel1}));
        tunnel1.setLink(link1);
        Tunnel tunnel2 = new Tunnel();
        tunnel2.setTunnelID(new SimpleTunnelID(TUNNEL_NAMES[1], TUNNEL_NUMBERS[1]));
        Link link2 = new Link();
        link2.setLinkID(new SimpleLinkID(LINK_IDS[1], ISP_IDS[0]));
        link2.setTraversingTunnels(Arrays.asList(new Tunnel[]{tunnel2}));
        tunnel2.setLink(link2);
        outCommunication.setConnectingTunnels(Arrays.asList(new Tunnel[]{tunnel1, tunnel1}));
        try { // Tunnel IDs are not unique
            dtm.setConfigData(configData);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }
        outCommunication.setConnectingTunnels(Arrays.asList(new Tunnel[]{tunnel1, tunnel2}));
        tunnel2.setLink(link1);
        try { // Link IDs are not unique
            dtm.setConfigData(configData);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }
        tunnel2.setLink(link2);
        configData.setOutCommunicationList(Arrays.asList(new DC2DCCommunication[]{outCommunication}));
        dtm.setConfigData(configData);

        Assert.assertEquals(configData, dtm.getConfigData());
    }

    /**
     * Test the {@link DTM#setReferenceVector(eu.smartenit.sbox.db.dto.RVector)}
     * method.
     */
    @Test
    public void testSetReferenceVector() {
        RVector referenceVector = new RVector();
        try { // Reference vector values cannot be null
            dtm.setReferenceVector(referenceVector);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }
        referenceVector.addVectorValueForLink(new SimpleLinkID(LINK_IDS[0], ISP_IDS[0]), 1);
        try { // DTM v. 1.0 supports only exactly 2 links
            dtm.setReferenceVector(referenceVector);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }
        referenceVector.addVectorValueForLink(new SimpleLinkID(LINK_IDS[1], ISP_IDS[0]), -1);
        try { // -1 is not strictly positive
            dtm.setReferenceVector(referenceVector);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }
        referenceVector.addVectorValueForLink(new SimpleLinkID("", ISP_IDS[0]), 1);
        try { // DTM v. 1.0 supports only exactly 2 links
            dtm.setReferenceVector(referenceVector);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }
        referenceVector.setVectorValues(null);
        referenceVector.addVectorValueForLink(new SimpleLinkID(LINK_IDS[0], ISP_IDS[0]), 0);
        referenceVector.addVectorValueForLink(new SimpleLinkID("", ISP_IDS[0]), 0);
        try { // Reference vector does not contain value for link2
            dtm.setReferenceVector(referenceVector);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }
        referenceVector.setVectorValues(null);
        referenceVector.addVectorValueForLink(new SimpleLinkID(LINK_IDS[0], ISP_IDS[0]), 1);
        referenceVector.addVectorValueForLink(new SimpleLinkID(LINK_IDS[1], ISP_IDS[0]), 1);
        dtm.setReferenceVector(referenceVector);
        Assert.assertEquals(referenceVector, dtm.getReferenceVector());
        dtm.setReferenceVector(null); // don't update R & don't throw exception
        Assert.assertEquals(referenceVector, dtm.getReferenceVector());
    }

    /**
     * Test the
     * {@link DTM#setCompensationVector(eu.smartenit.sbox.db.dto.CVector)}
     * method.
     */
    @Test
    public void testSetCompensationVector() {
        try { // Compensation vector cannot be null
            dtm.setCompensationVector(null);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }
        CVector compensationVector = new CVector();
        try { // Compensation vector values cannot be null
            dtm.setCompensationVector(compensationVector);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }
        compensationVector.addVectorValueForLink(new SimpleLinkID(LINK_IDS[0], ISP_IDS[0]), 0);
        try { // DTM v. 1.0 supports only exactly 2 links
            dtm.setCompensationVector(compensationVector);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }
        compensationVector.addVectorValueForLink(new SimpleLinkID(LINK_IDS[1], ISP_IDS[0]), 1);
        try { // Compensation vector does not sum to zero
            dtm.setCompensationVector(compensationVector);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }
        compensationVector.addVectorValueForLink(new SimpleLinkID("", ISP_IDS[0]), 0);
        try { // DTM v. 1.0 supports only exactly 2 links
            dtm.setCompensationVector(compensationVector);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }
        compensationVector.setVectorValues(null);
        compensationVector.addVectorValueForLink(new SimpleLinkID(LINK_IDS[0], ISP_IDS[0]), 0);
        compensationVector.addVectorValueForLink(new SimpleLinkID("", ISP_IDS[0]), 0);
        try { // Compensation vector does not contain value for link2
            dtm.setCompensationVector(compensationVector);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }
    }

    /**
     * Test the {@link DTM#setSwitch(net.floodlightcontroller.core.IOFSwitch)}
     * method.
     */
    @Test
    public void testSetSwitch() {
        dtm.setSwitch(new FakeStatisticsProviderOFSwitch());
        try {
            dtm.setSwitch(new FakeStatisticsProviderOFSwitch());
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test algorithm ({@link DTM#getTunnel()} method).
     *
     * @throws java.util.concurrent.TimeoutException
     */
    @Test
    public void testGetTunnel() throws TimeoutException {
        FakeStatisticsProviderOFSwitch sw = new FakeStatisticsProviderOFSwitch();
        DTM.getInstance().setSwitch(sw);
        for (DTMTestCase testCase : DTM_TEST_CASES) {
            for (int p = 0; p < testCase.transmittedBytesStart.length; p++) {
                sw.setTrafficForPort((short) (p + 1), testCase.transmittedBytesStart[p]);
            }
            waitForStatisticsSetting(testCase.transmittedBytesStart);
            dtm.setReferenceVector(testCase.referenceVector);
            dtm.setCompensationVector(testCase.compensationVector);
            for (int i = 0; i < testCase.states.size(); i++) {
                DTMTestCase.State state = testCase.states.get(i);
                for (int p = 0; p < state.transmittedBytes.length; p++) {
                    sw.setTrafficForPort((short) (p + 1), state.transmittedBytes[p]);
                }
                waitForStatisticsSetting(state.transmittedBytes);
                Assert.assertEquals(testCase.name + "-" + (i + 1), state.tunnel, dtm.getTunnel());
            }
        }
    }

    private void waitForStatisticsSetting(long[] transmittedBytes) throws TimeoutException {
        final int SLEEP = 10; // in ms
        final long MAX_TOTAL_SLEEP = (DTM.PORT_STATISTICS_POLLING_INTERVAL + 1000) * 1000000;

        final long timeout = System.nanoTime() + MAX_TOTAL_SLEEP;
        while (true) {
            if (Arrays.equals(transmittedBytes, dtm.getTransmittedBytes())) {
                return;
            }
            if (System.nanoTime() > timeout) {
                throw new TimeoutException();
            }
            try {
                Thread.sleep(SLEEP);
            } catch (InterruptedException ex) {
            }
        }
    }
}
