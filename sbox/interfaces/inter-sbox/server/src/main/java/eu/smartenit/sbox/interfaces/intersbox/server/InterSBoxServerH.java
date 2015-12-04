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
package eu.smartenit.sbox.interfaces.intersbox.server;

import eu.smartenit.sbox.interfaces.intersbox.client.InterSBoxObject;


import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.codec.string.StringDecoder;

import java.net.InetSocketAddress;

import org.slf4j.Logger;                                                                                                                       
import org.slf4j.LoggerFactory;    

public class InterSBoxServerH extends ChannelInboundHandlerAdapter {

  private static final Logger logger = LoggerFactory.getLogger(InterSBoxServerH.class);
  private final InterSBoxServer server;

  public InterSBoxServerH(InterSBoxServer server)
  {
    this.server = server;
  }

  private ObjectMapper mapper;
  private ObjectMapper mymapper()
  { 
    if (mapper == null)
    {
      mapper = new ObjectMapper();
    }
    return mapper;
  }

  @Override
    public void channelActive( ChannelHandlerContext ctx) throws Exception
    {
    String host = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
    int port = ((InetSocketAddress)ctx.channel().remoteAddress()).getPort();
      logger.debug("Server connected from host:" + host + ":"  + port);
    }

   @Override
    public void channelRead( ChannelHandlerContext ctx, Object msg) throws Exception
    {
      // Echo back the received object to the client.
      String str = (String)msg;
    String host = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
    int port = ((InetSocketAddress)ctx.channel().remoteAddress()).getPort();
      logger.debug("Server received something : " + str + "host:" + host + ":"  + port);
      InterSBoxObject receivedObject=mymapper().readValue(str,InterSBoxObject.class);
      server.received(receivedObject);
      ctx.writeAndFlush(new String("ACK"));
    }

  @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
    {
    String host = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
    int port = ((InetSocketAddress)ctx.channel().remoteAddress()).getPort();
      logger.debug("Server readcomplete from host:" + host + ":"  + port);
      ctx.flush();
    }

  @Override
    public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
      logger.error( "Unexpected exception from downstream." + cause);
      ctx.close();
      throw ((Exception)cause);
    }
}

