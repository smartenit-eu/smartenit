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

import java.util.Arrays;

import net.floodlightcontroller.packet.Data;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPacket;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.UDP;

import org.openflow.protocol.OFPacketIn;

import eu.smartenit.sbox.db.dto.ConfigData;
import eu.smartenit.sbox.db.dto.ConfigDataEntry;
import eu.smartenit.sbox.db.dto.EndAddressPairTunnelID;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.TunnelInfo;

public class TestSetUpHelper {
   
	public static final String[] TUNNEL_NAMES = {
        "tunnel11",
        "tunnel12",
        "tunnel21",
        "tunnel22"
    };
    public static final String[] REMOTE_TUNNEL_END_ADDRESSES = {
        "10.1.1.1",
        "10.1.2.1",
        "10.1.3.1",
        "10.1.4.1"
    };
    public static final String[] REMOTE_TUNNEL_END_ADDRESS_PREFIXES = {
        "10.1.1.0",
        "10.1.2.0",
        "10.1.3.0",
        "10.1.4.0"
    };
    public static final String[] LOCAL_TUNNEL_END_ADDRESSES = {
        "20.1.1.1",
        "20.1.1.2",
        "20.1.1.3",
        "20.1.1.4"
    };
    public static final short[] DA_ROUTER_PORT_NUMBERS = {
        1,
        2,
        3,
        4
    };
    public static final String[] REMOTE_DC_PREFIXES = {
        "10.10.1.0",
        "10.10.2.0"
    };
    public static final int[] REMOTE_DC_PREFIX_LENGTHS = {
        24,
        24
    };
    public static final String[] DESTINATION_IPS = {
        "10.10.1.100",
        "10.10.2.100"
    };
    public static final String DA_ROUTER_OF_DPID1 = "00:00:00:00:00:00:00:01";
    
    public static final ConfigData CONFIG_DATA_1DC;
    public static final ConfigData CONFIG_DATA_2DC;
    
	static {
		CONFIG_DATA_1DC = new ConfigData();
        ConfigDataEntry entry1 = new ConfigDataEntry();
        entry1.setRemoteDcPrefix(new NetworkAddressIPv4(REMOTE_DC_PREFIXES[0], REMOTE_DC_PREFIX_LENGTHS[0]));
        entry1.setDaRouterOfDPID(DA_ROUTER_OF_DPID1);
        TunnelInfo tunnel11 = new TunnelInfo(
        		new EndAddressPairTunnelID(
        				TUNNEL_NAMES[0], 
        				new NetworkAddressIPv4(LOCAL_TUNNEL_END_ADDRESSES[0], 32), 
        				new NetworkAddressIPv4(REMOTE_TUNNEL_END_ADDRESSES[0], 32)),
        				1);
        TunnelInfo tunnel12 = new TunnelInfo(
        		new EndAddressPairTunnelID(
        				TUNNEL_NAMES[1], 
        				new NetworkAddressIPv4(LOCAL_TUNNEL_END_ADDRESSES[1], 32), 
        				new NetworkAddressIPv4(REMOTE_TUNNEL_END_ADDRESSES[1], 32)), 
        				2);
        entry1.setTunnels(Arrays.asList(tunnel11, tunnel12));

        CONFIG_DATA_1DC.setEntries(Arrays.asList(entry1));
	}
	
	static {
		CONFIG_DATA_2DC = new ConfigData();
        ConfigDataEntry entry1 = new ConfigDataEntry();
        entry1.setRemoteDcPrefix(new NetworkAddressIPv4(REMOTE_DC_PREFIXES[0], REMOTE_DC_PREFIX_LENGTHS[0]));
        entry1.setDaRouterOfDPID(DA_ROUTER_OF_DPID1);
        TunnelInfo tunnel11 = new TunnelInfo(
        		new EndAddressPairTunnelID(
        				TUNNEL_NAMES[0], 
        				new NetworkAddressIPv4(LOCAL_TUNNEL_END_ADDRESSES[0], 32), 
        				new NetworkAddressIPv4(REMOTE_TUNNEL_END_ADDRESSES[0], 32)),
        				1);
        TunnelInfo tunnel12 = new TunnelInfo(
        		new EndAddressPairTunnelID(
        				TUNNEL_NAMES[1], 
        				new NetworkAddressIPv4(LOCAL_TUNNEL_END_ADDRESSES[1], 32), 
        				new NetworkAddressIPv4(REMOTE_TUNNEL_END_ADDRESSES[1], 32)), 
        				2);
        entry1.setTunnels(Arrays.asList(tunnel11, tunnel12));
        
        ConfigDataEntry entry2 = new ConfigDataEntry();
        entry2.setRemoteDcPrefix(new NetworkAddressIPv4(REMOTE_DC_PREFIXES[1], REMOTE_DC_PREFIX_LENGTHS[1]));
        entry2.setDaRouterOfDPID(DA_ROUTER_OF_DPID1);
        TunnelInfo tunnel13 = new TunnelInfo(
        		new EndAddressPairTunnelID(
        				TUNNEL_NAMES[2], 
        				new NetworkAddressIPv4(LOCAL_TUNNEL_END_ADDRESSES[2], 32), 
        				new NetworkAddressIPv4(REMOTE_TUNNEL_END_ADDRESSES[2], 32)),
        				3);
        TunnelInfo tunnel14 = new TunnelInfo(
        		new EndAddressPairTunnelID(
        				TUNNEL_NAMES[3], 
        				new NetworkAddressIPv4(LOCAL_TUNNEL_END_ADDRESSES[3], 32), 
        				new NetworkAddressIPv4(REMOTE_TUNNEL_END_ADDRESSES[3], 32)), 
        				4);
        entry2.setTunnels(Arrays.asList(tunnel13, tunnel14));
        
        CONFIG_DATA_2DC.setEntries(Arrays.asList(entry1, entry2));
	}
	
	public static OFPacketIn prepareOFPacketIn(String destAddress) {
	    IPacket iPacket = new Ethernet()
	            .setDestinationMACAddress("00:11:22:33:44:55")
	            .setSourceMACAddress("00:44:33:22:11:00")
	            .setEtherType(Ethernet.TYPE_IPv4)
	            .setPayload(
	                    new IPv4()
	                    .setTtl((byte) 128)
	                    .setSourceAddress("192.168.1.1")
	                    .setDestinationAddress(destAddress)
	                    .setPayload(new UDP()
	                            .setSourcePort((short) 5000)
	                            .setDestinationPort((short) 5001)
	                            .setPayload(new Data(new byte[]{0x01}))));

	    byte[] iPacketSerialized = iPacket.serialize();
	    
	    return new OFPacketIn()
	            .setBufferId(-1)
	            .setInPort((short) 1)
	            .setPacketData(iPacketSerialized)
	            .setReason(OFPacketIn.OFPacketInReason.NO_MATCH)
	            .setTotalLength((short) iPacketSerialized.length);
	}
	
}
