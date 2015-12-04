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
package eu.smartenit.sbox.interfaces.sboxsdn;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.ClientCookieEncoder;
import io.netty.handler.codec.http.DefaultCookie;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.ConfigData;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.db.dto.SDNController;
import eu.smartenit.sdn.interfaces.sboxsdn.RCVectors;
import eu.smartenit.sdn.interfaces.sboxsdn.Serialization;
import eu.smartenit.sdn.interfaces.sboxsdn.URLs;

/**
 * The client side of the SBox-SDN controller communication.
 * 
 * It implements methods for the exchange of the reference and compensation
 * vectors, as well SDN controller configuration parameters.
 * 
 * @author George Petropoulos
 * @version 1.0
 * 
 */
public class SboxSdnClient {

	private static final Logger logger = LoggerFactory
			.getLogger(SboxSdnClient.class);

	/**
	 * The constructor.
	 */
	public SboxSdnClient() {

	}

	/**
	 * The method that distributes the compensation and reference vectors to the
	 * SDN controller.
	 * 
	 * @param sdnController
	 *            The SDN Controller
	 * @param cVector
	 *            The compensation vector
	 * @param rVector
	 *            The reference vector
	 */
	public void distribute(SDNController sdnController, CVector cVector, RVector rVector) {
		logger.debug("Distributing cVector " + cVector + " and rVector " + rVector 
				+ " to SDN Controller " + sdnController.getManagementAddress().getPrefix());

		String content = "";
		RCVectors rcVectors = new RCVectors(rVector, cVector);
		
		try {
			content = Serialization.serialize(rcVectors);
		} catch (JsonProcessingException e1) {
			logger.warn("Received reference and compensation vectors cannot be serialized. "
					+ e1.getMessage());
		}

		try {
			URI uri = new URI("http://" + sdnController.getRestHost().getPrefix()  
					+ ":" + sdnController.getRestPort() 
					+ URLs.BASE_PATH + URLs.DTM_R_C_VECTORS_PATH);
			send(uri, content);
		} catch (InterruptedException e) {
			logger.warn("Exception while connecting to the rcVectors SDN controller REST API. " 
					+ e.getMessage());
		} catch (URISyntaxException e) {
			logger.warn("Exception while creating SDN controller rcVectors URI. " 
					+ e.getMessage());
		} catch (ConnectException e) {
			logger.warn("Exception while connecting to the rcVectors SDN controller REST API. "
					+ e.getMessage());
		}
		logger.debug("CVector and rVector were successfully sent to SDN Controller " 
				+ sdnController.getManagementAddress().getPrefix());
	}

	/**
	 * The method that distributes the compensation vector to the SDN
	 * controller.
	 * 
	 * @param sdnController
	 *            The SDN Controller
	 * @param cVector
	 *            The compensation vector
	 */
	public void distribute(SDNController sdnController, CVector cVector) {
		distribute(sdnController, cVector, null);
	}

	/**
	 * The method that provides the configuration parameters to the SDN
	 * controller.
	 * 
	 * @param sdnController
	 *            The SDN Controller
	 * @param configData
	 *            The configuration data
	 */
	public void configure(SDNController sdnController, ConfigData configData) {
		String content = "";
		logger.debug("Serializing configuration data " + configData.toString());
		try {
			content = Serialization.serialize(configData);
		} catch (JsonProcessingException e1) {
			logger.warn("Received configuration data cannot be serialized. " + e1.getMessage());
		}
		
		try {
			URI uri = new URI("http://" + sdnController.getRestHost().getPrefix() 
					+ ":" + sdnController.getRestPort() 
					+ URLs.BASE_PATH + URLs.DTM_CONFIG_DATA_PATH);
			send(uri, content);
		} catch (InterruptedException e) {
			logger.warn("Exception while connecting to the Config SDN controller REST API. " 
					+ e.getMessage());
		} catch (URISyntaxException e) {
			logger.warn("Exception while creating SDN controller Config URI. " 
					+ e.getMessage());
		} catch (ConnectException e) {
			logger.warn("Exception while connecting to the Config SDN controller REST API. "
					+ e.getMessage());
		}
		logger.debug("ConfigData were successfully sent to SDN Controller " 
				+ sdnController.getManagementAddress().getPrefix());
	}

	/**
	 * The method that sends the HTTP request to the SDN controller server.
	 * 
	 * @param uri
	 *            The URI of the SDN controller REST API
	 * @param content
	 *            The content to be sent, as plain text
	 * @throws InterruptedException
	 */
	private void send(URI uri, String content) throws InterruptedException, ConnectException {
		logger.debug("Sending the http request to the URI " + uri + ".");
		// Identify protocol, hot, and port parameters.
		String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
		String host = uri.getHost() == null ? "localhost" : uri.getHost();
		int port = uri.getPort();
		if (port == -1) {
			if ("http".equalsIgnoreCase(scheme)) {
				port = 8080;
			}
		}
		
		// Prepare the HTTP request.
		DefaultFullHttpRequest request = prepareHttpRequest(uri, content);

		// Configure the client.
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class)
					.handler(new SboxSdnClientInitializer());

			// Make the connection attempt.
			Channel channel = bootstrap.connect(host, port).sync().channel();

			// Send the HTTP request.
			channel.writeAndFlush(request);

			// Wait for the server to close the connection.
			channel.closeFuture().sync();
		} finally {
			// Shut down executor threads to exit.
			group.shutdownGracefully();
		}
		logger.debug("Http request was sent to the URI " + uri + ".");
	}

	/**
	 * The method that prepares the HTTP request header.
	 * 
	 * @param uri
	 *            The URI of the SDN controller REST API
	 * @param content
	 *            The content to be sent, as plain text
	 */
	public DefaultFullHttpRequest prepareHttpRequest(URI uri, String content) {
		logger.debug("Preparing the http request.");
		// Create the HTTP content bytes.
		/*
		ByteBuf buffer = ByteBufUtil.encodeString(ByteBufAllocator.DEFAULT,
				CharBuffer.wrap(content), CharsetUtil.UTF_8);
				*/

		// Prepare the HTTP request.
		// Set the HTTP protocol version, method, uri and content.
		/*
		DefaultFullHttpRequest request = new DefaultFullHttpRequest(
				HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getRawPath(), buffer);
				*/
		DefaultFullHttpRequest request = new DefaultFullHttpRequest(
				HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getRawPath(), 
				Unpooled.wrappedBuffer(content.getBytes()));

		// Set certain header parameters.
		request.headers().set(HttpHeaders.Names.HOST, uri.getHost());
		request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		request.headers().set(HttpHeaders.Names.ACCEPT, "application/json; q=0.9,*/*;q=0.8");
		request.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");
		request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, content.length());
		request.headers().set(HttpHeaders.Names.PRAGMA, HttpHeaders.Values.NO_CACHE);
		request.headers().set(HttpHeaders.Names.CACHE_CONTROL, HttpHeaders.Values.NO_CACHE);

		// Set some example cookies.
		request.headers().set(
				HttpHeaders.Names.COOKIE,
				ClientCookieEncoder.encode(new DefaultCookie(
						"smartenit-cookie", "smartenit")));
		logger.debug("Prepared the following HTTP request to be sent: \n" + request.toString() + "\n"
				+ request.content().toString(CharsetUtil.UTF_8));
		return request;
	}

}
