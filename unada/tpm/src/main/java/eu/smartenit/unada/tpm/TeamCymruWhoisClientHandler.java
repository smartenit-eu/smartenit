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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Inbound handler capable to handle and parse rensponse from Team Cymru IP to
 * ASN Mapping service.
 *
 * @author Piotr Wydrych
 * @version 2.0
 */
public class TeamCymruWhoisClientHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = LoggerFactory.getLogger(TeamCymruWhoisClientHandler.class);
    private static final Pattern responseLine = Pattern.compile("\\s*(\\d+)\\s*\\|\\s*(\\S+)\\s*");

    private final ConcurrentHashMap<Inet4Address, Integer> asMap;

    /**
     * Handler construtor.
     *
     * @param asMap map used to store parsing result.
     */
    public TeamCymruWhoisClientHandler(ConcurrentHashMap<Inet4Address, Integer> asMap) {
        this.asMap = asMap;
    }

    /**
     * Handles response from IP to ASN Mapping service.
     *
     * @param ctx the {@link ChannelHandlerContext} which this
     * {@link SimpleChannelInboundHandler} belongs to
     * @param msg the message to handle
     * @throws Exception is thrown if an error occurred
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        logger.debug("Reading from channel " + ctx.pipeline().channel() + ": \"" + msg + "\"");
        if (msg.startsWith("Bulk mode;")) {
            return;
        }
        Matcher m = responseLine.matcher(msg);
        if (m.matches()) {
            asMap.put(
                    (Inet4Address) InetAddress.getByName(m.group(2)),
                    Integer.parseInt(m.group(1)));
        } else {
            throw new UnsupportedOperationException("Could not parse \"" + msg + "\"");
        }
    }

}
