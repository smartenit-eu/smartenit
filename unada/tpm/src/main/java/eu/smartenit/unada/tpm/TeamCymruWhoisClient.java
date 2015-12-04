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
package eu.smartenit.unada.tpm;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class of Team Cymru IP to ASN Mapping service client.
 *
 * @author Piotr Wydrych
 * @version 2.0
 */
public class TeamCymruWhoisClient {

    private static final Logger logger = LoggerFactory.getLogger(TeamCymruWhoisClient.class);

    // hard-coded intentionally - it is not ewxpected to change
    private static final byte TEAM_CYMRU_WHOIS_PORT = 43;

    // http://www.iana.org/assignments/iana-ipv4-special-registry/
    private static final int[][] SPECIAL_PURPOSE_BLOCKS = {
        {0x00000000, 0xFF000000}, // 0.0.0.0/8
        {0x0A000000, 0xFF000000}, // 10.0.0.0/8
        {0x64400000, 0xFFC00000}, // 100.64.0.0/10
        {0x7F000000, 0xFF000000}, // 127.0.0.0/8
        {0xA9FE0000, 0xFFFF0000}, // 169.254.0.0/16
        {0xAC100000, 0xFFF00000}, // 172.16.0.0/12
        {0xC0000000, 0xFFFFFF00}, // 192.0.0.0/24
        {0xC0000200, 0xFFFFFF00}, // 192.0.2.0/24
        {0xC0586300, 0xFFFFFF00}, // 192.88.99.0/24
        {0xC0A80000, 0xFFFF0000}, // 192.168.0.0/16
        {0xC6120000, 0xFFFE0000}, // 198.18.0.0/15
        {0xC6336400, 0xFFFFFF00}, // 198.51.100.0/24
        {0xCB007100, 0xFFFFFF00}, // 203.0.113.0/24
        {0xF0000000, 0xF0000000}, // multicast & future use
    };

    private final String teamCymruWhoisHost;
    private final boolean resolveSpecialPurposeAddresses;

    public TeamCymruWhoisClient(String teamCymruWhoisHost, boolean resolveSpecialPurposeAddresses) {
        this.teamCymruWhoisHost = teamCymruWhoisHost;
        this.resolveSpecialPurposeAddresses = resolveSpecialPurposeAddresses;
    }

    /**
     * Checks if specified address is a special-purpose address and, thus, may
     * not be assigned an AS number.
     *
     * @param address address to be checked.
     * @see <a
     * href="http://www.iana.org/assignments/iana-ipv4-special-registry/">IANA
     * IPv4 Special-Purpose Address Registry</a>
     * @return true if address is special-purpose.
     */
    public static boolean isSpecialPurposeAddress(Inet4Address address) {
        int addr = ByteBuffer.wrap(address.getAddress()).getInt();
        for (int[] block : SPECIAL_PURPOSE_BLOCKS) {
            if ((addr & block[1]) == block[0]) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Address %s belongs to special %d.%d.%d.%d/%d block",
                            address.getHostAddress(),
                            (block[0] >> 24) & 0xFF, (block[0] >> 16) & 0xFF, (block[0] >> 8) & 0xFF, block[0] & 0xFF,
                            Integer.bitCount(block[1])));
                }
                return true;
            }
        }
        logger.debug(String.format("Address %s is globally routable", address.getHostAddress()));
        return false;
    }

    /**
     * Connects to Team Cymru IP to ASN Mapping service and resolves AS numbers
     * for IP addresses.
     *
     * @param ipAddresses IP addresses to lookup.
     * @return IP-address-to-AS-number map.
     * {@link #isSpecialPurposeAddress(java.net.Inet4Address) Special purpose addresses}
     * are assigned ASN 0 (zero).
     * @throws InterruptedException if {@link ChannelFuture#sync()} throws
     * InterruptedException.
     * @see <a href="http://www.team-cymru.org/Services/ip-to-asn.html#whois">IP
     * to ASN Mapping - Team Cymru</a>
     */
    public Map<Inet4Address, Integer> ipToASN(Collection<Inet4Address> ipAddresses) throws InterruptedException {
        logger.debug("Discovering AS numbers for {}", ipAddresses.toString());
        ipAddresses = new LinkedHashSet<>(ipAddresses); // copy
        ipAddresses.remove(null);
        ConcurrentHashMap<Inet4Address, Integer> asMap = new ConcurrentHashMap<>(ipAddresses.size());
        for (Iterator<Inet4Address> it = ipAddresses.iterator(); it.hasNext();) {
            Inet4Address inet4Address = it.next();
            if (!resolveSpecialPurposeAddresses && isSpecialPurposeAddress(inet4Address)) {
                it.remove();
                asMap.put(inet4Address, 0);
            }
        }
        if (ipAddresses.isEmpty()) {
            return new HashMap<>(asMap);
        }
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new TeamCymruWhoisClientInitializer(new TeamCymruWhoisClientHandler(asMap)));

            Channel channel = bootstrap.connect(teamCymruWhoisHost, TEAM_CYMRU_WHOIS_PORT).sync().channel();
            logger.debug("channel = " + channel);

            // http://www.team-cymru.org/Services/ip-to-asn.html#whois
            StringBuilder request = new StringBuilder();
            request.append("begin\r\n");
            request.append("noasname\r\n");
            request.append("noheader\r\n");
            for (Inet4Address address : ipAddresses) {
                request.append(address.getHostAddress()).append("\r\n");
            }
            request.append("end\r\n");

            logger.debug("Sending request: " + request);
            channel.writeAndFlush(request.toString());

            channel.closeFuture().sync();
        } finally {
            // Shut down executor threads to exit.
            group.shutdownGracefully();
        }
//        ipAddresses.removeAll(asMap.keySet());
//        if (!ipAddresses.isEmpty()) {
//            logger.error("Could not find ASN for " + ipAddresses.toString());
//       }
        logger.debug("Request completed.");
        return new HashMap<>(asMap);
    }
}
