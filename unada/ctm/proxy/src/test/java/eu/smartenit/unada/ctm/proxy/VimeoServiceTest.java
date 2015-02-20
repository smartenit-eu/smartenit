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
package eu.smartenit.unada.ctm.proxy;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import eu.smartenit.unada.commons.constants.UnadaConstants;
import eu.smartenit.unada.ctm.cache.util.CacheConstants;
import eu.smartenit.unada.ctm.proxy.video.ProxyVideoService;
import eu.smartenit.unada.ctm.proxy.video.impl.ProxyVimeoService;
import eu.smartenit.unada.db.dao.impl.ContentAccessDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.smartenit.unada.db.dao.impl.ContentDAO;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Content;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class VimeoServiceTest {
	
	public ContentDAO contentDAO = mock(ContentDAO.class);
    public ContentAccessDAO contentAccessDAO = mock(ContentAccessDAO.class);
	private ProxyVideoService service;

			
	@Before
	public void setup() {
		service = ProxyVimeoService.getProxyVimeoServiceInstance();

        CacheConstants.cachePath = "test/unada";
		
		when(contentDAO.findByPath("test/unada/46372/751/73389968.mp4")).thenReturn(null);
		
		Content c = new Content();
		c.setContentID(12345678);
		c.setDownloaded(false);
		when(contentDAO.findByPath("test/unada/33333/333/3333333.mp4")).thenReturn(c);
		
		c = new Content();
		c.setContentID(987654);
		c.setDownloaded(true);
		when(contentDAO.findByPath("test/unada/11111/333/3333333.mp4")).thenReturn(c);

        when(contentAccessDAO.findByContentID(987654)).thenReturn(null);
    	    	
    	DAOFactory.setContentDAO(contentDAO);
        DAOFactory.setContentAccessDAO(contentAccessDAO);
	}
	
	@Test
	public void testRewriteVideoRequestNotValidURL() {
		URI uri = service.rewriteVideoRequest("http://random.com/46372/751/73389968.mp4", 
				"/46372/751/73389968.mp4", "1.2.3.4");
		assertNull(uri);	
		verify(contentDAO, times(0)).findByPath("test/unada/46372/751/73389968.mp4");
	}
	
	@Test
	public void testRewriteVideoRequestValidURLNotCached() {
		URI uri = service.rewriteVideoRequest("https://avvimeo-a.akamaihd.net/46372/751/73389968.mp4",
				"/46372/751/73389968.mp4", "1.2.3.4");
		assertNull(uri);	
		verify(contentDAO, times(1)).findByPath("test/unada/46372/751/73389968.mp4");
	}
	
	@Test
	public void testRewriteVideoRequestValidURLDownloading() {
		URI uri = service.rewriteVideoRequest("https://avvimeo-a.akamaihd.net/33333/333/3333333.mp4",
				"/33333/333/3333333.mp4", "1.2.3.4");
		assertNull(uri);	
		verify(contentDAO, times(1)).findByPath("test/unada/33333/333/3333333.mp4");
		
	}
	
	@Test
	public void testRewriteVideoRequestCached() throws UnknownHostException {
		URI uri = service.rewriteVideoRequest("https://avvimeo-a.akamaihd.net/11111/333/3333333.mp4",
				"/11111/333/3333333.mp4", "1.2.3.4");
		assertNotNull(uri);	
		assertEquals(uri.toString(), "http://" + UnadaConstants.UNADA_IP_ADDRESS
                + "/unada/11111/333/3333333.mp4");
		verify(contentDAO, times(1)).findByPath("test/unada/11111/333/3333333.mp4");
	}

	
	@After
	public void teardown() {
		
	}
	

}
