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
package eu.smartenit.unada.ctm.proxy.video.impl;

import java.net.*;
import java.util.regex.Pattern;

import eu.smartenit.unada.commons.constants.UnadaConstants;
import eu.smartenit.unada.commons.threads.UnadaThreadService;
import eu.smartenit.unada.ctm.cache.impl.ContentAccessLoggerImpl;
import eu.smartenit.unada.ctm.cache.impl.VimeoDownloader;
import eu.smartenit.unada.ctm.cache.util.CacheConstants;
import eu.smartenit.unada.ctm.cache.util.VimeoPatterns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.ctm.proxy.video.ProxyVideoService;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Content;

/**
 * The ProxyVimeoService class. It implements the ProxyVideoService interface methods.
 * It includes methods for proxying Vimeo requests and rewriting URLs.
 * 
 * @author George Petropoulos
 * @version 2.1
 * 
 */
public class ProxyVimeoService implements ProxyVideoService {

	private static ProxyVimeoService proxyVimeoServiceInstance = new ProxyVimeoService();
	private static final Logger logger = LoggerFactory
			.getLogger(ProxyVimeoService.class);
	private Pattern vimeoURL;
	private Pattern vimeoRequest;

	public ProxyVimeoService() {
		vimeoURL = Pattern.compile(VimeoPatterns.vimeoURL);
		vimeoRequest = Pattern.compile(VimeoPatterns.vimeoRequest);
	}

    public static ProxyVimeoService getProxyVimeoServiceInstance() {
        return proxyVimeoServiceInstance;
    }

    public static void setProxyVimeoServiceInstance(ProxyVimeoService proxyVimeoServiceInstance) {
        ProxyVimeoService.proxyVimeoServiceInstance = proxyVimeoServiceInstance;
    }

    /**
	 * The method that proxies all requests. If it matches the Vimeo pattern, then
	 * it starts the vimeo downloader thread.
	 * 
	 * @param url The request to be proxied.
	 * 
	 */
	public void proxyVideoRequest(String url) {

		// check if vimeo url matches vimeo url pattern.
		if (vimeoURL.matcher(url).matches()) {
			logger.info("Proxying vimeo request " + url);
			VimeoDownloader downloader = new VimeoDownloader(url, false);

            UnadaThreadService.getThreadService().execute(downloader);
		}
	}

	/**
	 * The method that rewrites a specific request. If it matches the Vimeo pattern, 
	 * and the content is already downloaded and cached, then it serves it from
	 * the local server.
	 * 
	 * @param url The request URL.
	 * @param uri The request URI.
	 * @param ipAddress The ip address of the user accessing the content.
	 * 
	 * @return The rewritten URI.
	 * 
	 */
	public URI rewriteVideoRequest(String url, String uri, String ipAddress) {
		URI rewrittenURI = null;

		// check if vimeo request matches the vimeo request pattern.
		if (vimeoRequest.matcher(url).matches()) {
			logger.info("Rewriting vimeo request " + url);
			String path = CacheConstants.cachePath + uri;
			Content content = DAOFactory.getContentDAO().findByPath(path);

            //check if content exists in cache and is downloaded.
			if (content != null && content.isDownloaded()) {
				logger.info("Content " + content.getContentID() + " is cached, " +
                        "and will be served from local HTTP server.");
				try {
                    String localIPAddress = UnadaConstants.UNADA_IP_ADDRESS;
                    NetworkInterface nic = NetworkInterface.getByName("wlan0");
                    if (nic != null && nic.isUp()) {
                        for (InterfaceAddress addr : nic.getInterfaceAddresses()) {
                            if (!addr.getAddress().getHostAddress().contains(":")) {
                                localIPAddress = addr.getAddress().getHostAddress();
                            }
                        }
                    }
					rewrittenURI = new URI("http://" + localIPAddress + "/unada" + uri);
					ContentAccessLoggerImpl contentAccessLogger =
                            new ContentAccessLoggerImpl(content.getContentID(), ipAddress);
                    contentAccessLogger.updateAccessLog();
                    logger.debug("Rewritten URI = " + rewrittenURI);
					return rewrittenURI;
				} catch (URISyntaxException e) {
					logger.error("Error while rewriting URI: " + rewrittenURI
							+ ": " + e.getMessage());
				} catch (SocketException e) {
                    logger.error("Error while getting the IP address of wlan0 interface: " + e.getMessage());
                }
            }
		}
		return rewrittenURI;
	}

}
