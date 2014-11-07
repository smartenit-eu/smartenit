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
package eu.smartenit.sbox.interfaces.sboxsdn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

/**
 * The handler class for the SBox-SDN controller communication. 
 * 
 * It prints the server response for the sent HTTP request.
 *
 * @author George Petropoulos
 * @version 1.0
 * 
 */
public class SboxSdnClientHandler extends SimpleChannelInboundHandler<HttpObject> {
	
	private static final Logger logger = LoggerFactory.getLogger(SboxSdnClientHandler.class);

	/**
	 * The methods that reads server's response. 
	 * 
	 * It prints the server response for the sent HTTP request.
	 *
	 * @param ctx The channel handler context
	 * @param msg The received HTTP response
	 * 
	 */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
    	if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;

            logger.debug("----Header----");
            logger.debug(response.toString());
            logger.debug("----End of header----");
            if (HttpHeaders.isTransferEncodingChunked(response)) {
                logger.debug("----Chunked content----");
            } else {
                logger.debug("----Content----");
            }
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;

            logger.debug(content.content().toString(CharsetUtil.UTF_8));
            if (content instanceof LastHttpContent) {
                logger.debug("----End of content----");
                ctx.close();
            }
        }
    }
    

    /**
	 * The method handling exceptions while connecting to server.
	 *
	 * @param ctx The channel handler context
	 * @param cause The exception cause
	 * 
	 */
    @Override
    public void exceptionCaught(
            ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn(cause.getMessage());
        ctx.close();
    }
    
}
