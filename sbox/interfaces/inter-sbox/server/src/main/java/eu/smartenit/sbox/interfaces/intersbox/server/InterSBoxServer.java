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
package eu.smartenit.sbox.interfaces.intersbox.server;

import java.util.Enumeration;
import java.net.InetSocketAddress;

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.ntm.NetworkTrafficManager;
import eu.smartenit.sbox.ntm.dtm.DTMRemoteVectorsReceiver;
import eu.smartenit.sbox.interfaces.intersbox.client.InterSBoxObject;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import org.slf4j.Logger;                                                                                                                       
import org.slf4j.LoggerFactory;    



public class InterSBoxServer implements Runnable
{

  private Thread t;
  private int port;

  private static final Logger logger = LoggerFactory.getLogger(InterSBoxServer.class);

  /**
   *  starts the server
   */
  public void run()
  {
    logger.debug("InterSBoxServer run");
    try
    {
      run0();
    }
    catch(Exception e)
    {
      logger.error("Exception raised in InterSBoxServer thread : " + e.toString());
    }
    logger.debug("InterSBoxServer end run");
  }


  /**
   *  starts the server
   */
  public void run0() throws Exception  {
    EventLoopGroup bossGroup = new NioEventLoopGroup(4);
    EventLoopGroup workerGroup = new NioEventLoopGroup(4);
    try {
      final InterSBoxServerH sHandler = new InterSBoxServerH(this);
      final InterSBoxServer s0 = this;
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(
              new StringDecoder(CharsetUtil.UTF_8),                                                                                                    
              new StringEncoder(CharsetUtil.UTF_8),                    
              new InterSBoxServerH(s0) );
            // sHandler);
            }
        });

      // Bind and start to accept incoming connections.
      b.bind(new InetSocketAddress("0.0.0.0", port)).sync().channel().closeFuture().sync();
    } finally {
      logger.debug("InterSBoxServer run finally begin");
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
      logger.debug("InterSBoxServer run finally end");
    }
  }

  private NetworkTrafficManager ntm;
  private  DTMRemoteVectorsReceiver receiver;

  /**
   *  Server Constructor
   *  
   *  @param port : listening port
   *  @param ntm0 : local network traffic manager
   *  @param receiver0 : DTM remote vectors receiver
   */
  public InterSBoxServer(int port, NetworkTrafficManager ntm0, DTMRemoteVectorsReceiver receiver0) 
  {
    logger.debug("Construct InterSBoxServer");
    this.port = port;
    if (receiver0 == null)
    {
      if (ntm0 == null)
      {
        ntm0 = new NetworkTrafficManager();
        ntm0.initialize();
      }
      ntm = ntm0;
      if (ntm==null)
        receiver = null;
      else
        receiver = ntm.getDtmVectorsReceiver();
    }
    else
      receiver = receiver0;

    t = new Thread(this);
    t.start();
  }

  /**
   *  Server Constructor
   *  
   *  @param port : listening port
   */
  public InterSBoxServer(int port) {
    this(port, null, null);
  }

  /**
   *  Server Constructor
   *  
   *  @param port : listening port
   *  @param ntm0 : local network traffic manager
   */
  public InterSBoxServer(int port, NetworkTrafficManager ntm0) {
    this(port, ntm0, null);
  }

  /**
   *  Server Constructor
   *  
   *  @param port : listening port
   *  @param receiver0 : DTM remote vectors receiver
   */
  public InterSBoxServer(int port, DTMRemoteVectorsReceiver receiver0) {
    this(port, null, receiver0);
  }

  public void join() throws Exception
  {
    t.join();
  }

  public void join(long millis) throws Exception
  {
    t.join(millis);
  }

  /**
   *  forwards cVector and rVector to associated receiver
   *  
   *  @param cVector : compensation vector
   *  @param rVector : reference vector
   */
  public void receive(CVector cVector, RVector rVector)
  {
    logger.debug("received c+rvector InterSBoxServer");
    if (receiver!=null)
      receiver.receive(cVector, rVector);
  }

  /**
   *  forwards cVector and rVector to associated receiver
   *  
   *  @param cVector : compensation vector
   */
  public void receive(CVector cVector)
  {
    logger.debug("received cvector InterSBoxServer");
    if (receiver!=null)
      receiver.receive(cVector);
  }

  public void received(InterSBoxObject obj)
  {
    logger.debug("Received InterSBoxServer");
    if (obj.rvPresent)
    {
      receive(obj.cVector, obj.rVector);
    }
    else
    {
      receive(obj.cVector);
    }
  }
}

