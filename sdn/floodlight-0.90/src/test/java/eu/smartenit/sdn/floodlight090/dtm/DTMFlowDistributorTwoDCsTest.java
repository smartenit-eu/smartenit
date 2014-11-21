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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openflow.protocol.OFPacketIn;

/**
 * Main DTM class test.
 *
 * @author Grzegorz Rzym
 * @version 1.2
 */
public class DTMFlowDistributorTwoDCsTest {

    public static final OFPacketIn PACKET_1 = TestSetUpHelper.prepareOFPacketIn(TestSetUpHelper.DESTINATION_IPS[0]);
    public static final OFPacketIn PACKET_2 = TestSetUpHelper.prepareOFPacketIn(TestSetUpHelper.DESTINATION_IPS[1]);
    public static final OFPacketIn PACKET_3_NO_ENTRY = TestSetUpHelper.prepareOFPacketIn("10.10.3.100");

    private static final DTMTestCase[] DTM_TEST_CASES = {
    	prepareDTMTestCase1(),
    	prepareDTMTestCase2(),
    	prepareDTMTestCase3()
    };

    private static DTM dtm;

    @BeforeClass
    public static void beforeClass() {
        dtm = DTM.getInstance();
    }

	@Before
    public void before() {
        dtm.setSwitch(null);
        dtm.setConfigData(TestSetUpHelper.CONFIG_DATA_2DC);
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
            for (Entry<Short, Long> entry : testCase.transmittedBytesStartMap.entrySet()) {
                sw.setTrafficForPort(entry.getKey().shortValue(), entry.getValue());
            }
            waitForStatisticsSetting(testCase.transmittedBytesStartMap);
            
            dtm.setReferenceVector(testCase.referenceVectors.get(0));
            dtm.setReferenceVector(testCase.referenceVectors.get(1));
            dtm.setCompensationVector(testCase.compensationVectors.get(0));
            dtm.setCompensationVector(testCase.compensationVectors.get(1));
            
            for (int i = 0; i < testCase.states.size(); i++) {
                DTMTestCase.State state = testCase.states.get(i);
                for (Entry<Short, Long> entry : state.transmittedBytesMap.entrySet()) {
                    sw.setTrafficForPort(entry.getKey().shortValue(), entry.getValue());
                }
                waitForStatisticsSetting(state.transmittedBytesMap);
                
                if(i%2 == 0)
                	dtm.setOFPacketIn(PACKET_1);
                else
                	dtm.setOFPacketIn(PACKET_2);
                Assert.assertEquals(testCase.name + "-" + (i + 1), state.daRouterOutPort, dtm.getOutOfPortNumber());
            }
        }
    }

    private void waitForStatisticsSetting(Map<Short, Long> transmittedBytesOnPorts) throws TimeoutException {
        final int SLEEP = 10; // in ms
        final long MAX_TOTAL_SLEEP = (DTM.PORT_STATISTICS_POLLING_INTERVAL + 1000) * 1000000;

        final long timeout = System.nanoTime() + MAX_TOTAL_SLEEP;
        while (true) {
            if (checkTransmittedBytesOnPorts(transmittedBytesOnPorts, dtm.getTransmittedBytesOnAllPorts())) {
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
    
    private boolean checkTransmittedBytesOnPorts(Map<Short, Long> expected, Map<Short, Long> actual) {
    	for(Short port : expected.keySet()) {
    		if (actual.get(port) == null)
    			return false;
    		if (!expected.get(port).equals(actual.get(port)))
    			return false;
    	}
    	return true;
    }
    
    private static DTMTestCase prepareDTMTestCase1() {
    	Map<Short, Long> bytesMapStart = new HashMap<>(); // new long[]{0L, 0L}
    	bytesMapStart.put((short)1, 0L);
    	bytesMapStart.put((short)2, 0L);
    	bytesMapStart.put((short)3, 0L);
    	bytesMapStart.put((short)4, 0L);
    	
    	return new DTMTestCase("Trivial-1",
		        new long[]{1000000L, 1000000L, 1000000L, 1000000L}, // normalizedR: [0.5, 0.5]
		        new long[]{0L, 0L, 0L, 0L},
		        bytesMapStart)
		        .addState(mapFrom(1000L, 0L, 0L, 0L), (short) 2) // traffic: [1000, 0], trafficDAB: 1000. Not compensating, sending flow to tunnel 2
    			.addState(mapFrom(1000L, 0L, 1000L, 0L), (short) 4);
	}
    
	private static DTMTestCase prepareDTMTestCase2() {
		Map<Short, Long> bytesMapStart = new HashMap<>(); // new long[]{0L, 0L}
		bytesMapStart.put((short)1, 0L);
		bytesMapStart.put((short)2, 0L);
		bytesMapStart.put((short)3, 0L);
		bytesMapStart.put((short)4, 0L);
		
    	return new DTMTestCase("Trivial-2",
				new long[]{1000000L, 1000000L, 1000000L, 1000000L}, // normalizedR: [0.5, 0.5]
				new long[]{0L, 0L, 0L, 0L},
				bytesMapStart)
        		.addState(mapFrom(0L, 1000L, 0L, 0L), (short) 1) // traffic: [0, 1000], trafficDAB: 1000. Not compensating, sending flow to tunnel 1
        		.addState(mapFrom(0L, 1000L, 0L, 1000L), (short) 3); 
    	}
	
	private static DTMTestCase prepareDTMTestCase3() {
		Map<Short, Long> bytesMapStart = new HashMap<>();
		bytesMapStart.put((short)1, 4400000000L);
		bytesMapStart.put((short)2, 5900000000L);
		bytesMapStart.put((short)3, 6800000000L);
		bytesMapStart.put((short)4, 7800000000L);
		
		return new DTMTestCase("ZD-1",
		        new long[]{20000000000L, 10000000000L, 25000000000L, 25000000000L}, // normalizedR: [0.67, 0.33][0.50, 0.50]
		        new long[]{20000000L, -20000000L, 10000000L, -10000000L},
		        bytesMapStart
		        )
		        .addState(mapFrom(4417000000L, 5900000000L, 6800000000L, 7800000000L), (short) 1)
		        .addState(mapFrom(4417000000L, 5900000000L, 6800034560L, 7800000000L), (short) 3)
		        .addState(mapFrom(4460000064L, 5900000000L, 6800034560L, 7800000000L), (short) 2)
		        .addState(mapFrom(4460000064L, 5900000000L, 6800006788L, 7800000000L), (short) 3)
		        .addState(mapFrom(4460000064L, 5900001560L, 6800006788L, 7800000000L), (short) 1)
		        .addState(mapFrom(4460000064L, 5900001560L, 6805606788L, 7800000000L), (short) 3)
		        .addState(mapFrom(4460001264L, 5900001560L, 6805606788L, 7800000000L), (short) 1)
		        .addState(mapFrom(4460001264L, 5900001560L, 6819606788L, 7800000000L), (short) 3)
		        .addState(mapFrom(4460007896L, 5900003560L, 6819606788L, 7800000000L), (short) 2)
		        .addState(mapFrom(4460007896L, 5900003560L, 6820000123L, 7800000000L), (short) 4)
		        .addState(mapFrom(4460007896L, 5900004560L, 6820000123L, 7800000000L), (short) 1)
		        .addState(mapFrom(4460007896L, 5900004560L, 6820000123L, 7800003459L), (short) 3)
		        .addState(mapFrom(4460008096L, 5900004560L, 6820000123L, 7800003459L), (short) 1)
		        .addState(mapFrom(4460008096L, 5900004560L, 6820020123L, 7800003459L), (short) 4)
		        .addState(mapFrom(4460009996L, 5900004560L, 6820020123L, 7800003459L), (short) 2)
		        .addState(mapFrom(4460009996L, 5900004560L, 6820020123L, 7800023459L), (short) 3)
		        .addState(mapFrom(4460009996L, 5900004660L, 6820020123L, 7800023459L), (short) 2)
		        .addState(mapFrom(4460009996L, 5900004660L, 6820023123L, 7800023459L), (short) 3)
		        .addState(mapFrom(4460009996L, 5900004860L, 6820023123L, 7800023459L), (short) 2)
		        .addState(mapFrom(4460009996L, 5900004860L, 6820031123L, 7800023459L), (short) 4)
		        .addState(mapFrom(4460009996L, 5900005260L, 6820031123L, 7800023459L), (short) 1)
		        .addState(mapFrom(4460009996L, 5900005260L, 6820031123L, 7800043550L), (short) 3)
		        .addState(mapFrom(4460013996L, 5900005260L, 6820031123L, 7800043550L), (short) 2)
		        .addState(mapFrom(4460013996L, 5900005260L, 6820071123L, 7800043550L), (short) 4);
	}
	
	private static Map<Short, Long> mapFrom(long value1, long value2, long value3, long value4) {
		Map<Short, Long> bytesMap = new HashMap<>();
		bytesMap.put((short)1, value1);
		bytesMap.put((short)2, value2);
		bytesMap.put((short)3, value3);
		bytesMap.put((short)4, value4);
		return bytesMap;
	}

}
