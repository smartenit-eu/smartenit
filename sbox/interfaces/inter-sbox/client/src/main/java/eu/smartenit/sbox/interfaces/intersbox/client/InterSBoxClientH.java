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
package eu.smartenit.sbox.interfaces.intersbox.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.RVector; 

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterSBoxClientH extends ChannelInboundHandlerAdapter
{
  private static final Logger logger = LoggerFactory.getLogger(InterSBoxClientH.class);

  private final InterSBoxObject preparedMessage;

  private ObjectMapper mapper=null;
  private ObjectMapper mymapper()
  {
    if (mapper==null) 
    {
      mapper = new ObjectMapper();
    }
    return mapper;
  }

  public InterSBoxClientH(CVector cVector, boolean rvPresent, RVector rVector)
  {
    logger.debug("Client Prepares");

    if (mapper==null) mapper = new ObjectMapper();

    preparedMessage = new InterSBoxObject();
    preparedMessage.cVector = cVector;
    preparedMessage.rVector = rVector;
    preparedMessage.rvPresent = rvPresent;
  }

  @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
      final String mystring = mymapper().writeValueAsString(preparedMessage);
      String host = ((InetSocketAddress)ctx.channel().localAddress()).getAddress().getHostAddress();
      int port = ((InetSocketAddress)ctx.channel().localAddress()).getPort();
      logger.debug("Client Send : " + mystring + " host:port=" + host + ":" + port);
      ctx.writeAndFlush(mystring);
    }

  @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
      logger.debug("Client Read : " + (String)msg);
      ctx.close();
    }

  @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
    {
      ctx.flush();
    }

  @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
      logger.error( "Unexpected exception from downstream." + cause);
      ctx.close();
      throw ((Exception)cause);
    }
}

