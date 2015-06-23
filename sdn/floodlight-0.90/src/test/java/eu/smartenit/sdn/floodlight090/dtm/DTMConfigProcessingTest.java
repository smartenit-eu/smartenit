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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.test.MockFloodlightProvider;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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

public class DTMConfigProcessingTest {

    private static final Logger logger = LoggerFactory.getLogger(DTMConfigProcessingTest.class);
    private static final String tunnelEndPrefix1 = "10.1.1.0";
    private static final String tunnelEndPrefix2 = "10.1.2.0";
    private static final String tunnelEndPrefix3 = "10.1.3.0";
    private static final String tunnelEndPrefix4 = "10.1.4.0";

    private DTM dtm;

    @Before
    public void setUp() {
        DTM.resetInstance();
        dtm = DTM.getInstance();
    }

    /**
     * Test if {@link DTM#getInstance()} returns the same value in subsequent
     * calls.
     */
    @Test
    public void testGetInstance() {
        Assert.assertSame(dtm, DTM.getInstance());
    }
    
    /**
     * Tests the {@link ConfigData} setter
     */
    @Test
    public void testSetConfigData() {
        ConfigData configData = new ConfigData();
        try { // Configuration data cannot be null
            dtm.setConfigData(null);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }

        try { // ConfigData entries list cannot be null
            dtm.setConfigData(configData);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }

        configData.setEntries(new ArrayList<ConfigDataEntry>());
        try { // ConfigDataEntry cannot be empty
            dtm.setConfigData(configData);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }

        configData.setEntries(Arrays.asList(new ConfigDataEntry(null, null, null)));
        try { // Remote DC prefix cannot be null
            dtm.setConfigData(configData);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }

        configData.setEntries(Arrays.asList(new ConfigDataEntry(new NetworkAddressIPv4(), null, null)));
        try { // DA Router DPID cannot be null
            dtm.setConfigData(configData);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }

        configData.setEntries(Arrays.asList(new ConfigDataEntry(new NetworkAddressIPv4(), "", null)));
        try { // Connecting tunnels for out communication cannot be null
            dtm.setConfigData(configData);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }

        configData.setEntries(Arrays.asList(new ConfigDataEntry(new NetworkAddressIPv4(), "", new ArrayList<TunnelInfo>())));
        try { // Number of tunnels below 2
            dtm.setConfigData(configData);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }

        TunnelInfo tunnel1 = new TunnelInfo();
        TunnelInfo tunnel2 = new TunnelInfo();
        configData.setEntries(Arrays.asList(new ConfigDataEntry(new NetworkAddressIPv4(), "", Arrays.asList(tunnel1, tunnel2))));
        try { // lack of operation mode
            dtm.setConfigData(configData);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }

        configData.setOperationModeSDN(OperationModeSDN.proactiveWithReferenceVector);
        try { // lack of LocalDCOfSwitchPorts
            dtm.setConfigData(configData);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }

        LocalDCOfSwitchPorts localDCOfSwitchPort1 = new LocalDCOfSwitchPorts();
        localDCOfSwitchPort1.setDaRouterOfDPID("00:00:00:15:5d:72:a2:03");
        configData.setLocalDCPortsConfig(Arrays.asList(localDCOfSwitchPort1));
        try { // lack of port numbers in LocalDCOfSwitchPorts
            dtm.setConfigData(configData);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            logger.debug("Caught exception: " + ex.toString());
        }

        localDCOfSwitchPort1.setLocalDCOfSwitchPortNumbers(Arrays.asList(10));
        configData.setLocalDCPortsConfig(Arrays.asList(localDCOfSwitchPort1));

        dtm.setFloodlightProvider(new MockFloodlightProvider());
        IOFSwitch switchMock = EasyMock.createNiceMock(IOFSwitch.class);
        expect(switchMock.getStringId()).andReturn("mock");
        replay(switchMock);
        dtm.setSwitch(null);
        dtm.setSwitch(switchMock);
        dtm.setConfigData(configData);
        Assert.assertEquals(configData, dtm.getConfigData());
    }

    /**
     * Test the {@link DTM#setSwitch(net.floodlightcontroller.core.IOFSwitch)}
     * method.
     */
    @Test
    public void testSetSwitch() {
        dtm.setSwitch(null);
        dtm.setSwitch(new FakeStatisticsProviderOFSwitch());
        try {
            dtm.setSwitch(new FakeStatisticsProviderOFSwitch());
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
        }
    }

}
