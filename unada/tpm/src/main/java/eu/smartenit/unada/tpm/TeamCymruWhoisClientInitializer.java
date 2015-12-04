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

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Channel initialized for {@link TeamCymruWhoisClientHandler}.
 *
 * @author Piotr Wydrych
 * @version 2.0
 */
public class TeamCymruWhoisClientInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger logger = LoggerFactory.getLogger(TeamCymruWhoisClientInitializer.class);

    private final TeamCymruWhoisClientHandler handler;

    /**
     * Channel initialized constructor.
     *
     * @param handler response hanler.
     */
    public TeamCymruWhoisClientInitializer(TeamCymruWhoisClientHandler handler) {
        this.handler = handler;
    }

    /**
     * This method will be called once the {@link Channel} was registered. After
     * the method returns this instance will be removed from the
     * {@link ChannelPipeline} of the {@link Channel}.
     *
     * @param ch the {@link Channel} which was registered.
     * @throws Exception is thrown if an error occurs. In that case the
     * {@link Channel} will be closed.
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        logger.debug("Initializing channel " + ch);
        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        ch.pipeline().addLast(new StringDecoder(StandardCharsets.US_ASCII));
        ch.pipeline().addLast(new StringEncoder(StandardCharsets.US_ASCII));
        ch.pipeline().addLast(handler);
    }

}
