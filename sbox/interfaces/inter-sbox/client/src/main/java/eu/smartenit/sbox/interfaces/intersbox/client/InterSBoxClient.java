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
package eu.smartenit.sbox.interfaces.intersbox.client;

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.RVector; 

import io.netty.util.CharsetUtil; 
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import org.slf4j.Logger;                                                                                                                       
import org.slf4j.LoggerFactory;    

public class InterSBoxClient {
  private static final Logger logger = LoggerFactory.getLogger(InterSBoxClient.class);

  /*** Constructor
  */
  public InterSBoxClient() 
  {
  }

  /** sends vectors to inter-sbox server of remote sbox
   * 
   *  @param host : remote sbox identifier
   *  @param port : port number of remote sbox receiver
   *  @param cVector : compensation vector
   *  @param rvPresent : rVector presence indicator
   *  @param rVector : reference Vector 
   */
  public void send(String host, int port, CVector cVector, boolean rvPresent, RVector rVector) throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    logger.debug("InterSBoxClient send");
    try {
      final CVector cVector_ = cVector;
      final RVector rVector_ = rVector;
      final boolean rvPresent_ = rvPresent;
      Bootstrap b = new Bootstrap();
      b.group(group)
        .channel(NioSocketChannel.class)
        .option(ChannelOption.TCP_NODELAY, true)
        .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(
              new StringDecoder(CharsetUtil.UTF_8),
              new StringEncoder(CharsetUtil.UTF_8),
              new InterSBoxClientH(cVector_, rvPresent_, rVector_));
            }
            });

      // Start the connection attempt.
      logger.debug("InterSBoxClient start connection attempt");
      Channel bf = b.connect(host, port).sync().channel();
      logger.debug("InterSBoxClient wait connection attempt");
      bf.closeFuture().sync();
      logger.debug("InterSBoxClient end connection attempt");
    } finally {
      group.shutdownGracefully();
    }
    logger.debug("InterSBoxClient send finish");
  }

  /** sends vectors to inter-sbox server of remote sbox
   * 
   *  @param host : remote sbox identifier
   *  @param port : port number of remote sbox receiver
   *  @param cVector : compensation vector
   */
  public void send(String host, int port, CVector cVector) throws Exception
  {
    send(host, port, cVector, false, null);
  }

  /** sends vectors to inter-sbox server of remote sbox
   * 
   *  @param host : remote sbox identifier
   *  @param port : port number of remote sbox receiver
   *  @param cVector : compensation vector
   *  @param rVector : reference Vector 
   */
  public void send(String host, int port, CVector cVector, RVector rVector) throws Exception
  {
    send(host, port, cVector, true, rVector);
  }

  /** sends vectors to inter-sbox server of remote sbox
   * 
   *  @param cVector : compensation vector
   *  @param rVector : reference Vector 
   *  @param host : remote sbox identifier
   */
  public void send(CVector cVector, RVector rVector, String dstIspId) throws Exception
  {
    String[] hostport = dstIspId.split(":");
    send(hostport[0], Integer.parseInt(hostport[1]), cVector, rVector);
  }

  /** sends vectors to inter-sbox server of remote sbox
   * 
   *  @param cVector : compensation vector
   *  @param host : remote sbox identifier
   */
  public void send(CVector cVector, String dstIspId) throws Exception
  {
    String[] hostport = dstIspId.split(":");
    send(hostport[0], Integer.parseInt(hostport[1]), cVector);
  }

}
