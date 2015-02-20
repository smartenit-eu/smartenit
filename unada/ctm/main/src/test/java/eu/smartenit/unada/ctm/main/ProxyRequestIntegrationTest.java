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

import static org.junit.Assert.*;

import java.io.File;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import eu.smartenit.unada.ctm.cache.util.CacheConstants;
import eu.smartenit.unada.ctm.proxy.video.ProxyVideoService;
import eu.smartenit.unada.ctm.proxy.video.impl.ProxyVimeoService;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Content;

public class ProxyRequestIntegrationTest {

	private static ProxyVideoService service;
			
	@BeforeClass
	public static void setup() throws Exception {
		DAOFactory.getContentDAO().createTable();
		service = ProxyVimeoService.getProxyVimeoServiceInstance();

        CacheConstants.cachePath = "test/unada";
				
		Content c = new Content();
		c.setContentID(105239904);
		c.setDownloaded(true);
		c.setPath("test/unada/whatever.mp4");
		DAOFactory.getContentDAO().insert(c);
        IntegrationTestsUtil.deleteDirectory(new File("test/"));
    	    	
	}
	
	@Test @Ignore
	public void testProxyInvalidVimeoURL() {
		//service.proxyVideoRequest("http://dada.vimeo.com/m/105239904");
	}
	
	@Test @Ignore
	public void testProxyCachedContent() {
		//service.proxyVideoRequest("http://vimeo.com/m/105239904");
	}
	
	@Test @Ignore
	public void testProxyNonCachedContent() throws InterruptedException {
		//service.proxyVideoRequest("http://vimeo.com/m/32369539");
		Thread.sleep(30000);
		
		Content c = DAOFactory.getContentDAO().findById(32369539);
		assertNotNull(c);
		assertTrue(c.isDownloaded());
		
		assertTrue(new File("test/unada/03110/750/73389900.mp4").exists());
	}

	
	@AfterClass
	public static void teardown() {
		DAOFactory.getContentDAO().deleteTable();
		IntegrationTestsUtil.deleteDirectory(new File("test/"));
	}
	
}
