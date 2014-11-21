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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.ConfigData;
import eu.smartenit.sbox.db.dto.ConfigDataEntry;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.db.dto.TunnelInfo;
import eu.smartenit.sbox.db.dto.VectorValue;

public class DTMConfigAndVectorsProcessingTest {

	private static final Logger logger = LoggerFactory.getLogger(DTMConfigAndVectorsProcessingTest.class);
	private static final String tunnelEndPrefix1 = "10.1.1.0";
	private static final String tunnelEndPrefix2 = "10.1.2.0";
	private static final String tunnelEndPrefix3 = "10.1.3.0";
	private static final String tunnelEndPrefix4 = "10.1.4.0";
	
	private DTM dtm;
	
	@Before
	public void setUp() {
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
		dtm.setConfigData(configData);

		Assert.assertEquals(configData, dtm.getConfigData());
	}

    /**
     * Test the {@link DTM#setReferenceVector(eu.smartenit.sbox.db.dto.RVector)}
     * method.
     */
	@Test
	public void testSetReferenceVector() {
		
		dtm.setConfigData(TestSetUpHelper.CONFIG_DATA_1DC);
		
		RVector referenceVector = new RVector();
		
		try { // Reference vector values cannot be null
			dtm.setReferenceVector(referenceVector);
			Assert.fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException ex) {
			logger.debug("Caught exception: " + ex.toString());
		}
		referenceVector.setVectorValues(null);
		try { // Vector Value list cannot be empty
			dtm.setReferenceVector(referenceVector);
			Assert.fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException ex) {
			logger.debug("Caught exception: " + ex.toString());
		}
		
		referenceVector.setVectorValues(Arrays.asList(
				new VectorValue(-1, new NetworkAddressIPv4(tunnelEndPrefix1, 24))));
		try { // -1 is not strictly positive
			dtm.setReferenceVector(referenceVector);
			Assert.fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException ex) {
			logger.debug("Caught exception: " + ex.toString());
		}
		
		referenceVector.setVectorValues(null);
		referenceVector.addVectorValueForTunnelEndPrefix(new NetworkAddressIPv4(tunnelEndPrefix1, 24), 0);
		try { // Reference vector does not contain value for tunnel2
			dtm.setReferenceVector(referenceVector);
			Assert.fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException ex) {
			logger.debug("Caught exception: " + ex.toString());
		}
		
		referenceVector.setVectorValues(null);
		referenceVector.addVectorValueForTunnelEndPrefix(new NetworkAddressIPv4(tunnelEndPrefix1, 24), 1);
		referenceVector.addVectorValueForTunnelEndPrefix(new NetworkAddressIPv4(tunnelEndPrefix2, 24), 1);
		
		dtm.setReferenceVector(referenceVector);
		Assert.assertEquals(referenceVector, dtm.getReferenceVector());
		
		dtm.setReferenceVector(null); // don't update R & don't throw exception
		Assert.assertEquals(referenceVector, dtm.getReferenceVector());
	}

	/**
     * Test the {@link DTM#setReferenceVector(eu.smartenit.sbox.db.dto.RVector)}
     * method.
     */
	@Test
	public void testSetMultipleReferenceVectors() {
		
		dtm.setConfigData(TestSetUpHelper.CONFIG_DATA_2DC);
		
		RVector referenceVector1 = new RVector();
		referenceVector1.setVectorValues(null);
		referenceVector1.addVectorValueForTunnelEndPrefix(new NetworkAddressIPv4(tunnelEndPrefix1, 24), 1);
		referenceVector1.addVectorValueForTunnelEndPrefix(new NetworkAddressIPv4(tunnelEndPrefix2, 24), 2);
		
		dtm.setReferenceVector(referenceVector1);
		
		RVector referenceVector2 = new RVector();
		referenceVector2.setVectorValues(null);
		referenceVector2.addVectorValueForTunnelEndPrefix(new NetworkAddressIPv4(tunnelEndPrefix3, 24), 3);
		referenceVector2.addVectorValueForTunnelEndPrefix(new NetworkAddressIPv4(tunnelEndPrefix4, 24), 4);
		
		dtm.setReferenceVector(referenceVector2);
		
		RVector referenceVector3 = new RVector();
		referenceVector3.setVectorValues(null);
		referenceVector3.addVectorValueForTunnelEndPrefix(new NetworkAddressIPv4(tunnelEndPrefix1, 24), 5);
		referenceVector3.addVectorValueForTunnelEndPrefix(new NetworkAddressIPv4(tunnelEndPrefix2, 24), 6);
		
		dtm.setReferenceVector(referenceVector3);
		
		assertEquals(4, dtm.rVectorMap.keySet().size());
		assertEquals(4, dtm.daRouterRVectorMap.keySet().size());

		assertEquals(5, (long)dtm.rVectorMap.get(new NetworkAddressIPv4(tunnelEndPrefix1, 24)));
		assertEquals(6, (long)dtm.daRouterRVectorMap.get((short)2));
	}
	
	/**
	 * Test the
	 * {@link DTM#setCompensationVector(eu.smartenit.sbox.db.dto.CVector)}
	 * method.
	 */
	@Test
	public void testSetCompensationVector() {
		
		dtm.setConfigData(TestSetUpHelper.CONFIG_DATA_1DC);
		
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
		
		compensationVector.setVectorValues(null);
		compensationVector.addVectorValueForTunnelEndPrefix(new NetworkAddressIPv4(tunnelEndPrefix1, 24), 0);
		try { // Compensation vector does not contain value for tunnel2
			dtm.setCompensationVector(compensationVector);
			Assert.fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException ex) {
			logger.debug("Caught exception: " + ex.toString());
		}

		compensationVector.setVectorValues(null);
		compensationVector.addVectorValueForTunnelEndPrefix(new NetworkAddressIPv4(tunnelEndPrefix1, 24), 0);
		compensationVector.addVectorValueForTunnelEndPrefix(new NetworkAddressIPv4(tunnelEndPrefix2, 24), 1);
		try { // Compensation vector does not sum to zero
			dtm.setCompensationVector(compensationVector);
			Assert.fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException ex) {
			logger.debug("Caught exception: " + ex.toString());
		}
		
		compensationVector.setVectorValues(null);
		compensationVector.addVectorValueForTunnelEndPrefix(new NetworkAddressIPv4(tunnelEndPrefix1, 24), -1);
		compensationVector.addVectorValueForTunnelEndPrefix(new NetworkAddressIPv4(tunnelEndPrefix2, 24), 1);
		dtm.setCompensationVector(compensationVector);
		Assert.assertEquals(compensationVector, dtm.getCompensationVector());
	}

	/**
	 * Test the
	 * {@link DTM#setCompensationVector(eu.smartenit.sbox.db.dto.CVector)}
	 * method.
	 */
	@Test
	public void testSetMultipleCompensationVector() {

		dtm.transmittedBytesMap.put((short)1, 100L);
		dtm.transmittedBytesMap.put((short)2, 200L);
		dtm.transmittedBytesMap.put((short)3, 300L);
		dtm.transmittedBytesMap.put((short)4, 400L);
		
		dtm.setConfigData(TestSetUpHelper.CONFIG_DATA_2DC);
		
		CVector compensationVector1 = new CVector();
		compensationVector1.setVectorValues(null);
		compensationVector1.addVectorValueForTunnelEndPrefix(new NetworkAddressIPv4(tunnelEndPrefix1, 24), 1);
		compensationVector1.addVectorValueForTunnelEndPrefix(new NetworkAddressIPv4(tunnelEndPrefix2, 24), -1);
		
		dtm.setCompensationVector(compensationVector1);
		
		CVector compensationVector2 = new CVector();
		compensationVector2.setVectorValues(null);
		compensationVector2.addVectorValueForTunnelEndPrefix(new NetworkAddressIPv4(tunnelEndPrefix3, 24), 5);
		compensationVector2.addVectorValueForTunnelEndPrefix(new NetworkAddressIPv4(tunnelEndPrefix4, 24), -5);
		
		dtm.setCompensationVector(compensationVector2);
		
		dtm.transmittedBytesMap.put((short)1, 1100L);
		dtm.transmittedBytesMap.put((short)2, 1200L);
		dtm.transmittedBytesMap.put((short)3, 1300L);
		dtm.transmittedBytesMap.put((short)4, 1400L);
		
		CVector compensationVector3 = new CVector();
		compensationVector3.setVectorValues(null);
		compensationVector3.addVectorValueForTunnelEndPrefix(new NetworkAddressIPv4(tunnelEndPrefix1, 24), 3);
		compensationVector3.addVectorValueForTunnelEndPrefix(new NetworkAddressIPv4(tunnelEndPrefix2, 24), -3);
		
		dtm.setCompensationVector(compensationVector3);
		
		assertEquals(4, dtm.cVectorMap.keySet().size());
		assertEquals(4, dtm.daRouterCVectorMap.keySet().size());
		assertEquals(4, dtm.daRouterCompenstatingToTunnelMap.keySet().size());
		
		assertEquals(3, (long)dtm.cVectorMap.get(new NetworkAddressIPv4(tunnelEndPrefix1, 24)));
		assertEquals(-3, (long)dtm.daRouterCVectorMap.get((short)2));
		
		assertEquals(1100, (long)dtm.transmittedBytesStartMap.get((short)1));
		assertEquals(1200, (long)dtm.transmittedBytesStartMap.get((short)2));
		assertEquals(300, (long)dtm.transmittedBytesStartMap.get((short)3));
		assertEquals(400, (long)dtm.transmittedBytesStartMap.get((short)4));
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
