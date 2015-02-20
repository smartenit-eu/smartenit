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

import eu.smartenit.unada.ctm.cache.ContentManager;
import eu.smartenit.unada.ctm.cache.impl.CacheManagerImpl;
import eu.smartenit.unada.ctm.cache.impl.ContentManagerImpl;
import eu.smartenit.unada.ctm.cache.util.CacheConstants;
import eu.smartenit.unada.db.dao.impl.CacheDAO;
import eu.smartenit.unada.db.dao.impl.ContentDAO;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Cache;
import eu.smartenit.unada.db.dto.Content;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class CacheManagerIntegrationTest {

	private CacheManagerImpl cacheManager;
	
	@Before
	public void setup() throws Exception {
        DAOFactory.getCacheDAO().createTable();
        DAOFactory.getContentDAO().createTable();

        CacheConstants.cachePath = "test/unada";

        Cache cache = new Cache();
        cache.setTimeToLive(1000);
        cache.setSize(8000000000L);
        cache.setSizeThreshold(100000);
        cache.setOverlayThreshold(0.5);
        cache.setSocialThreshold(0.5);
        DAOFactory.getCacheDAO().insert(cache);
	}


    @Test @Ignore
    public void testFetchAll() throws IOException, InterruptedException {
    	System.out.println("Should delete expired contents.");

		List<Content> contents = new ArrayList<>();
        Content c = new Content();
        c.setContentID(76230623);
        c.setCacheScore(1);
        contents.add(c);

        c = new Content();
        c.setContentID(105239904);
        c.setCacheScore(1);
        contents.add(c);

        c = new Content();
        c.setContentID(32369539);
        c.setCacheScore(1);
        contents.add(c);

        cacheManager = new CacheManagerImpl();
        cacheManager.updateCache(contents, null);

        Thread.sleep(30000);

        assertEquals(DAOFactory.getContentDAO().findAll().size(), 3);
        assertTrue(new File("test/unada/66025/911/194935670.mp4").exists());
        assertTrue(new File("test/unada/03110/750/73389900.mp4").exists());
        assertTrue(new File("test/unada/50974/591/292562150.mp4").exists());
    }

    @Test @Ignore
    public void testRemoveExpired() {
        cacheManager = new CacheManagerImpl();
        cacheManager.deleteExpired();

        assertEquals(DAOFactory.getContentDAO().findAll().size(), 0);
        assertFalse(new File("test/unada/66025/911/194935670.mp4").exists());
        assertFalse(new File("test/unada/03110/750/73389900.mp4").exists());
        assertFalse(new File("test/unada/50974/591/292562150.mp4").exists());
    }

    
    @After
    public void teardown() throws InterruptedException {

        DAOFactory.getCacheDAO().deleteTable();
        DAOFactory.getContentDAO().deleteTable();

        IntegrationTestsUtil.deleteDirectory(new File("test/"));
    	Thread.sleep(3000);
    }

}
