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
public class DTMFlowDistributorTest {

    public static final OFPacketIn PACKET_1 = TestSetUpHelper.prepareOFPacketIn(TestSetUpHelper.DESTINATION_IPS[0]);

    private static final DTMTestCase[] DTM_TEST_CASES = {
    	prepareDTMTestCase1(),
    	prepareDTMTestCase2(),
    	prepareDTMTestCase3(),
    	prepareDTMTestCase4(),
    	prepareDTMTestCase5()
    };

    private static DTM dtm;

    @BeforeClass
    public static void beforeClass() {
        dtm = DTM.getInstance();
    }

	@Before
    public void before() {
        dtm.setSwitch(null);
        dtm.setConfigData(TestSetUpHelper.CONFIG_DATA_1DC);
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
            dtm.setCompensationVector(testCase.compensationVectors.get(0));
            for (int i = 0; i < testCase.states.size(); i++) {
                DTMTestCase.State state = testCase.states.get(i);
                for (Entry<Short, Long> entry : state.transmittedBytesMap.entrySet()) {
                    sw.setTrafficForPort(entry.getKey().shortValue(), entry.getValue());
                }
                waitForStatisticsSetting(state.transmittedBytesMap);
                
                dtm.setOFPacketIn(PACKET_1);
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
    	
    	Map<Short, Long> bytesMapState1 = new HashMap<>();
    	bytesMapState1.put((short)1, 1000L);
    	bytesMapState1.put((short)2, 0L);
    	
    	return new DTMTestCase("Trivial-1",
		        new long[]{1000000L, 1000000L}, // normalizedR: [0.5, 0.5]
		        new long[]{0L, 0L},
		        bytesMapStart)
		        .addState(bytesMapState1, (short) 2); // traffic: [1000, 0], trafficDAB: 1000. Not compensating, sending flow to tunnel 2
	}
    
	private static DTMTestCase prepareDTMTestCase2() {
		Map<Short, Long> bytesMapStart = new HashMap<>(); // new long[]{0L, 0L}
		bytesMapStart.put((short)1, 0L);
		bytesMapStart.put((short)2, 0L);
		
    	Map<Short, Long> bytesMapState1 = new HashMap<>();
    	bytesMapState1.put((short)1, 0L);
    	bytesMapState1.put((short)2, 1000L);
    	
    	return new DTMTestCase("Trivial-2",
				new long[]{1000000L, 1000000L}, // normalizedR: [0.5, 0.5]
				new long[]{0L, 0L},
				bytesMapStart)
        		.addState(bytesMapState1, (short) 1); // traffic: [0, 1000], trafficDAB: 1000. Not compensating, sending flow to tunnel 1
	}
	
	private static DTMTestCase prepareDTMTestCase3() {
		Map<Short, Long> bytesMapStart = new HashMap<>(); // new long[]{4400000000L, 5900000000L}
		bytesMapStart.put((short)1, 4400000000L);
		bytesMapStart.put((short)2, 5900000000L);
		
		return new DTMTestCase("ZD-1",
		        new long[]{20000000000L, 10000000000L}, // normalizedR: [0.67, 0.33]
		        new long[]{20000000L, -20000000L},
		        bytesMapStart
		        )
		        .addState(mapFrom(4417000000L, 5900000000L), (short) 1) // traffic: [17000000, 0], trafficDAB: 17000000. Compensating to tunnel 1
		        .addState(mapFrom(4460000064L, 5900000000L), (short) 2) // traffic: [60000064, 0], trafficDAB: 60000064. Turning off compensation, sending flow to tunnel 2
		        .addState(mapFrom(4460000064L, 5900001560L), (short) 1) // traffic: [0, 1560], trafficDAB: 1560. Not compensating, sending flow to tunnel 1
		        .addState(mapFrom(4460001264L, 5900001560L), (short) 1) // traffic: [1200, 1560], trafficDAB: 2760. Not compensating, sending flow to tunnel 1
		        .addState(mapFrom(4460007896L, 5900003560L), (short) 2) // traffic: [7832, 3560], trafficDAB: 11392. Not compensating, sending flow to tunnel 2
		        .addState(mapFrom(4460007896L, 5900004560L), (short) 1) // traffic: [7832, 4560], trafficDAB: 12392. Not compensating, sending flow to tunnel 1
		        .addState(mapFrom(4460008096L, 5900004560L), (short) 1) // traffic: [8032, 4560], trafficDAB: 12592. Not compensating, sending flow to tunnel 1
		        .addState(mapFrom(4460009996L, 5900004560L), (short) 2) // traffic: [9932, 4560], trafficDAB: 14492. Not compensating, sending flow to tunnel 2
		        .addState(mapFrom(4460009996L, 5900004660L), (short) 2) // traffic: [9932, 4660], trafficDAB: 14592. Not compensating, sending flow to tunnel 2
		        .addState(mapFrom(4460009996L, 5900004860L), (short) 2) // traffic: [9932, 4860], trafficDAB: 14792. Not compensating, sending flow to tunnel 2
		        .addState(mapFrom(4460009996L, 5900005260L), (short) 1) // traffic: [9932, 5260], trafficDAB: 15192. Not compensating, sending flow to tunnel 1
		        .addState(mapFrom(4460013996L, 5900005260L), (short) 2); // traffic: [13932, 5260], trafficDAB: 19192. Not compensating, sending flow to tunnel 2
	}
	
	private static DTMTestCase prepareDTMTestCase4() {
		Map<Short, Long> bytesMapStart = new HashMap<>(); // new long[]{4400000000L, 5900000000L}
		bytesMapStart.put((short)1, 4400000000L);
		bytesMapStart.put((short)2, 5900000000L); 
		
		return new DTMTestCase("ZD-2",
				new long[]{20000000000L, 10000000000L}, // normalizedR: [0.67, 0.33]
				new long[]{-20000000L, 20000000L},
				bytesMapStart
				)
        		.addState(mapFrom(4400000000L, 5912000000L), (short) 2) // traffic: [0, 12000000], trafficDAB: 12000000. Compensating to tunnel 2
        		.addState(mapFrom(4400000000L, 5930000064L), (short) 1) // traffic: [0, 30000064], trafficDAB: 30000064. Turning off compensation, sending flow to tunnel 1
        		.addState(mapFrom(4400000233L, 5930000064L), (short) 2) // traffic: [233, 0], trafficDAB: 233. Not compensating, sending flow to tunnel 2
        		.addState(mapFrom(4400000233L, 5930000364L), (short) 1) // traffic: [233, 300], trafficDAB: 533. Not compensating, sending flow to tunnel 1
        		.addState(mapFrom(4400001033L, 5930000364L), (short) 2) // traffic: [1033, 300], trafficDAB: 1333. Not compensating, sending flow to tunnel 2
        		.addState(mapFrom(4400001033L, 5930000964L), (short) 1) // traffic: [1033, 900], trafficDAB: 1933. Not compensating, sending flow to tunnel 1
        		.addState(mapFrom(4400001233L, 5930000964L), (short) 1) // traffic: [1233, 900], trafficDAB: 2133. Not compensating, sending flow to tunnel 1
        		.addState(mapFrom(4400001733L, 5930000964L), (short) 1) // traffic: [1733, 900], trafficDAB: 2633. Not compensating, sending flow to tunnel 1
        		.addState(mapFrom(4400002733L, 5930000964L), (short) 2) // traffic: [2733, 900], trafficDAB: 3633. Not compensating, sending flow to tunnel 2
        		.addState(mapFrom(4400002733L, 5930001964L), (short) 1) // traffic: [2733, 1900], trafficDAB: 4633. Not compensating, sending flow to tunnel 1
        		.addState(mapFrom(4400003755L, 5930001964L), (short) 1) // traffic: [3755, 1900], trafficDAB: 5655. Not compensating, sending flow to tunnel 1
        		.addState(mapFrom(4400004555L, 5930001964L), (short) 2) // traffic: [4555, 1900], trafficDAB: 6455. Not compensating, sending flow to tunnel 2
        		.addState(mapFrom(4400004555L, 5930002164L), (short) 2); // traffic: [4555, 2100], trafficDAB: 6655. Not compensating, sending flow to tunnel 2
	}

	private static DTMTestCase prepareDTMTestCase5() {
		Map<Short, Long> bytesMapStart = new HashMap<>(); // new long[]{6800000000L, 7800000000L}
		bytesMapStart.put((short)1, 6800000000L);
		bytesMapStart.put((short)2, 7800000000L); 
		
		return new DTMTestCase("ZD-3",
		        new long[]{25000000000L, 25000000000L}, // normalizedR: [0.50, 0.50]
		        new long[]{10000000L, -10000000L},
		        bytesMapStart
		        )
		        .addState(mapFrom(6800034560L, 7800000000L), (short) 1) // traffic: [34560, 0], trafficDAB: 34560. Compensating to tunnel 1
		        .addState(mapFrom(6800006788L, 7800000000L), (short) 1) // traffic: [6788, 0], trafficDAB: 6788. Compensating to tunnel 1
		        .addState(mapFrom(6805606788L, 7800000000L), (short) 1) // traffic: [5606788, 0], trafficDAB: 5606788. Compensating to tunnel 1
		        .addState(mapFrom(6819606788L, 7800000000L), (short) 1) // traffic: [19606788, 0], trafficDAB: 19606788. Compensating to tunnel 1
		        .addState(mapFrom(6820000123L, 7800000000L), (short) 2) // traffic: [20000123, 0], trafficDAB: 20000123. Turning off compensation, sending flow to tunnel 2
		        .addState(mapFrom(6820000123L, 7800003459L), (short) 1) // traffic: [0, 3459], trafficDAB: 3459. Not compensating, sending flow to tunnel 1
		        .addState(mapFrom(6820020123L, 7800003459L), (short) 2) // traffic: [20000, 3459], trafficDAB: 23459. Not compensating, sending flow to tunnel 2
		        .addState(mapFrom(6820020123L, 7800023459L), (short) 1) // traffic: [20000, 23459], trafficDAB: 43459. Not compensating, sending flow to tunnel 1
		        .addState(mapFrom(6820023123L, 7800023459L), (short) 1) // traffic: [23000, 23459], trafficDAB: 46459. Not compensating, sending flow to tunnel 1
		        .addState(mapFrom(6820031123L, 7800023459L), (short) 2) // traffic: [31000, 23459], trafficDAB: 54459. Not compensating, sending flow to tunnel 2
		        .addState(mapFrom(6820031123L, 7800043550L), (short) 1) // traffic: [31000, 43550], trafficDAB: 74550. Not compensating, sending flow to tunnel 1
		        .addState(mapFrom(6820071123L, 7800043550L), (short) 2) // traffic: [71000, 43550], trafficDAB: 114550. Not compensating, sending flow to tunnel 2
		        .addState(mapFrom(6820071123L, 7800073470L), (short) 1) // traffic: [71000, 73470], trafficDAB: 144470. Not compensating, sending flow to tunnel 1
		        .addState(mapFrom(6820141117L, 7800073470L), (short) 2); // traffic: [140994, 73470], trafficDAB: 214464. Not compensating, sending flow to tunnel 2
	}
	
	private static Map<Short, Long> mapFrom(long value1, long value2) {
		Map<Short, Long> bytesMap = new HashMap<>();
		bytesMap.put((short)1, value1);
		bytesMap.put((short)2, value2);
		return bytesMap;
	}
	
}
