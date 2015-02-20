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
package eu.smartenit.unada.ctm.proxy.init;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.smartenit.unada.ctm.proxy.video.impl.ProxyVimeoService;
import org.eclipse.jetty.proxy.ProxyServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The UNaDaProxyServlet class. 
 * It's the servlet that proxies all requests and processes only the Vimeo-related ones, 
 * and rewrites streaming URLs in order to serve them from the local uNaDa server.
 * 
 * @author George Petropoulos
 * @version 2.0
 * 
 */
public class UNaDaProxyServlet extends ProxyServlet {
	
	private static final Logger logger = LoggerFactory.getLogger(UNaDaProxyServlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The method that initializes the UNaDaProxyServlet.
	 * 
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		logger.debug("Proxy Servlet initialized.");
		super.init(config);
	}

	/**
	 * The method that proxies all requests and processes only the Vimeo-related ones.
	 * 
	 * @throws ServletException
	 * @throws IOException
	 * 
	 */
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		//logger.debug("Proxy Servlet proxying request " + req.getRequestURL());
		new ProxyVimeoService().proxyVideoRequest(req.getRequestURL().toString());
		super.service(req, res);
	}

	/**
	 * The method that rewrites a Vimeo streaming request, in cases the content is already cached.
	 * 
	 * @param request The HttpServletRequest
	 * 
	 * @return URI The rewritten URI.
	 * 
	 */
	@Override
	protected URI rewriteURI(HttpServletRequest request) {
		//logger.debug("Proxy Servlet rewriting URL " + request.getRequestURL());
		
		URI uri = new ProxyVimeoService()
				.rewriteVideoRequest(request.getRequestURL().toString(), 
				request.getRequestURI().toString(), request.getRemoteAddr());
		if (uri != null) {
			return uri;
		}
		return super.rewriteURI(request);
	}

}
