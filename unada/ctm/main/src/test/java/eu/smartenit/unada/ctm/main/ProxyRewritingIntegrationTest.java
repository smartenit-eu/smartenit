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
package eu.smartenit.unada.ctm.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.net.URI;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import eu.smartenit.unada.ctm.cache.util.CacheConstants;
import eu.smartenit.unada.ctm.proxy.video.ProxyVideoService;
import eu.smartenit.unada.ctm.proxy.video.impl.ProxyVimeoService;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Content;

public class ProxyRewritingIntegrationTest {

	private static ProxyVideoService service;

			
	@BeforeClass
	public static void setup() throws Exception {
		DAOFactory.getContentDAO().createTable();
		service = ProxyVimeoService.getProxyVimeoServiceInstance();

        CacheConstants.cachePath = "test/unada";
				
		Content c = new Content();
		c.setContentID(12345678);
		c.setDownloaded(false);
		c.setPath("test/unada/33333/333/3333333.mp4");
		DAOFactory.getContentDAO().insert(c);
		
		
		c = new Content();
		c.setContentID(987654);
		c.setDownloaded(true);
		c.setPath("test/unada/11111/333/3333333.mp4");
		DAOFactory.getContentDAO().insert(c);
    	    	
	}
	
	@Test @Ignore
	public void testRewriteVideoRequestNotValidURL() {
		URI uri = service.rewriteVideoRequest("http://random.com/46372/751/73389968.mp4", 
				"/46372/751/73389968.mp4", "1.2.3.4");
		assertNull(uri);	
	}
	
	@Test @Ignore
	public void testRewriteVideoRequestValidURLNotCached() {
		URI uri = service.rewriteVideoRequest("http://pdl.vimeocdn.com/46372/751/73389968.mp4", 
				"/46372/751/73389968.mp4", "1.2.3.4");
		assertNull(uri);	
	}
	
	@Test @Ignore
	public void testRewriteVideoRequestValidURLDownloading() {
		URI uri = service.rewriteVideoRequest("http://pdl.vimeocdn.com/33333/333/3333333.mp4", 
				"/33333/333/3333333.mp4", "1.2.3.4");
		assertNull(uri);	
		
	}
	
	@Test @Ignore
	public void testRewriteVideoRequestCached() {
		URI uri = service.rewriteVideoRequest("http://pdl.vimeocdn.com/11111/333/3333333.mp4", 
				"/11111/333/3333333.mp4", "1.2.3.4");
		assertNotNull(uri);	
		assertEquals(uri.toString(), "http://192.168.40.1/unada" + "/11111/333/3333333.mp4");
	}

	
	@AfterClass
	public static void teardown() {
		DAOFactory.getContentDAO().deleteTable();
        IntegrationTestsUtil.deleteDirectory(new File("test/"));
	}
	
}
