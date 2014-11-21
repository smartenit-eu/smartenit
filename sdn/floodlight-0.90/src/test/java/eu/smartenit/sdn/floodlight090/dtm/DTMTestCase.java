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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.RVector;

public class DTMTestCase {
    public final class State {

        // input
        public final Map<Short,Long> transmittedBytesMap;
        
        // output
        public final short daRouterOutPort;

        public State(Map<Short,Long> transmittedBytesMap, short daRouterOutPort){
            this.transmittedBytesMap = transmittedBytesMap;
            this.daRouterOutPort = daRouterOutPort;
        }
    }

    public final String name;
    // input
    public final List<RVector> referenceVectors;
    public final List<CVector> compensationVectors;
    public final Map<Short,Long> transmittedBytesStartMap;
    
    public final List<State> states = new ArrayList<>();

    public DTMTestCase(String name, long[] R, long[] C, Map<Short,Long> transmittedBytesStartMap) {
    	if (R.length != 2 && R.length != 4)
    		throw new IllegalArgumentException();
    	
    	this.name = name;
        
    	this.referenceVectors = new ArrayList<>();
        RVector rVector = new RVector();
        rVector.addVectorValueForTunnelEndPrefix(
           		new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[0], 24), R[0]);
        rVector.addVectorValueForTunnelEndPrefix(
           		new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[1], 24), R[1]);
        
        this.referenceVectors.add(rVector);
        
        if(R.length == 4) {
        	rVector = new RVector();
            rVector.addVectorValueForTunnelEndPrefix(
               		new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[2], 24), R[2]);
            rVector.addVectorValueForTunnelEndPrefix(
               		new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[3], 24), R[3]);
            
            this.referenceVectors.add(rVector);
        }
        
        this.compensationVectors = new ArrayList<>();
        CVector cVector = new CVector();
        cVector.addVectorValueForTunnelEndPrefix(
          		new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[0], 24), C[0]);
        cVector.addVectorValueForTunnelEndPrefix(
          		new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[1], 24), C[1]);
        this.compensationVectors.add(cVector);
        if(R.length == 4) {
        	cVector = new CVector();
        	cVector.addVectorValueForTunnelEndPrefix(
              		new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[2], 24), C[2]);
            cVector.addVectorValueForTunnelEndPrefix(
              		new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[3], 24), C[3]);
            this.compensationVectors.add(cVector);
        }
        
        this.transmittedBytesStartMap = transmittedBytesStartMap;
    }

    public DTMTestCase addState(Map<Short,Long> transmittedBytesMap, short daRouterOutPort) {
        states.add(new State(transmittedBytesMap, daRouterOutPort));
        return this;
    }
}
