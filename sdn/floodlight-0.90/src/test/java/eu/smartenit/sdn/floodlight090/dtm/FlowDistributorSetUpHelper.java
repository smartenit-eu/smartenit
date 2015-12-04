/**
 * Copyright (C) 2015 The SmartenIT consortium (http://www.smartenit.eu)
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartenit.sdn.floodlight090.dtm;

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;

/**
 *
 * @author Grzegorz Rzym
 */
public class FlowDistributorSetUpHelper {

    public static final CVector C_VECTOR_11;
    public static final CVector C_VECTOR_12;
    public static final CVector C_VECTOR_21;
    public static final CVector C_VECTOR_22;
    public static final CVector C_VECTOR_31;
    public static final CVector C_VECTOR_32;
    public static final CVector C_VECTOR_41;
    public static final CVector C_VECTOR_42;

    static {
        C_VECTOR_11 = new CVector();
        C_VECTOR_11.addVectorValueForTunnelEndPrefix(
                new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[0], 24), 100L);
        C_VECTOR_11.addVectorValueForTunnelEndPrefix(
                new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[1], 24), -100L);
    }

    static {
        C_VECTOR_12 = new CVector();
        C_VECTOR_12.addVectorValueForTunnelEndPrefix(
                new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[2], 24), 10L);
        C_VECTOR_12.addVectorValueForTunnelEndPrefix(
                new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[3], 24), -10L);
    }

    static {
        C_VECTOR_21 = new CVector();
        C_VECTOR_21.addVectorValueForTunnelEndPrefix(
                new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[0], 24), -200L);
        C_VECTOR_21.addVectorValueForTunnelEndPrefix(
                new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[1], 24), 200L);
    }

    static {
        C_VECTOR_22 = new CVector();
        C_VECTOR_22.addVectorValueForTunnelEndPrefix(
                new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[2], 24), -4000L);
        C_VECTOR_22.addVectorValueForTunnelEndPrefix(
                new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[3], 24), 4000L);
    }

    static {
        C_VECTOR_31 = new CVector();
        C_VECTOR_31.addVectorValueForTunnelEndPrefix(
                new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[0], 24), 10L);
        C_VECTOR_31.addVectorValueForTunnelEndPrefix(
                new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[1], 24), -10L);
    }

    static {
        C_VECTOR_32 = new CVector();
        C_VECTOR_32.addVectorValueForTunnelEndPrefix(
                new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[2], 24), -200L);
        C_VECTOR_32.addVectorValueForTunnelEndPrefix(
                new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[3], 24), 200L);
    }

    static {
        C_VECTOR_41 = new CVector();
        C_VECTOR_41.addVectorValueForTunnelEndPrefix(
                new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[0], 24), -100L);
        C_VECTOR_41.addVectorValueForTunnelEndPrefix(
                new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[1], 24), 100L);
    }

    static {
        C_VECTOR_42 = new CVector();
        C_VECTOR_42.addVectorValueForTunnelEndPrefix(
                new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[2], 24), 400L);
        C_VECTOR_42.addVectorValueForTunnelEndPrefix(
                new NetworkAddressIPv4(TestSetUpHelper.REMOTE_TUNNEL_END_ADDRESS_PREFIXES[3], 24), -400L);
    }
}
