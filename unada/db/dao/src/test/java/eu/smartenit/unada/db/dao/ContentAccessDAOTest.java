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
package eu.smartenit.unada.db.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import eu.smartenit.unada.db.dao.impl.ContentDAO;
import eu.smartenit.unada.db.dto.Content;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.smartenit.unada.db.dao.impl.ContentAccessDAO;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.ContentAccess;

public class ContentAccessDAOTest {

    private ContentDAO cDao;

	private ContentAccessDAO caDao;

	@Before
	public void setUp() {

        cDao = new ContentDAO();
        cDao.createTable();

		caDao = new ContentAccessDAO();
		caDao.createTable();
	}

	@Test 
	public void testFunctions() throws Exception {
		System.out.println("Testing all queries.");
		assertEquals(caDao.findAll().size(), 0);
		assertEquals(caDao.findByContentID(1212).size(), 0);
        assertNull(caDao.findLatestByContentID(1212));
        assertNull(caDao.findLatestByContentIDFacebookID(1212, "2222"));

        Content c = new Content();
        c.setContentID(123456);
        c.setCacheType("social");
        c.setCacheScore(0.5);
        c.setCategory(Content.ContentCategory.comedy);
        c.setDownloaded(false);
        c.setPrefetched(true);
        c.setPath("/a/b/video.mp4");
        c.setQuality("480p");
        c.setSize(2332323);
        c.setUrl("www.vimeo.com/123456");
        c.setPrefetchedVimeo(true);
        c.setCacheDate(new Date(100000000));
        cDao.insert(c);

        assertEquals(cDao.findAll().size(), 1);
        assertEquals(cDao.findAllIDs().size(), 1);

		ContentAccess ca = new ContentAccess();
		ca.setContentID(123456);
		ca.setFacebookID("888888");
		ca.setTimeStamp(new Date(200000000));
		caDao.insert(ca);

		ca = new ContentAccess();
		ca.setContentID(123456);
		ca.setFacebookID("888888");
		ca.setTimeStamp(new Date(300000000));
		caDao.insert(ca);

		assertEquals(caDao.findAll().size(), 2);
		List<ContentAccess> caList = caDao.findByContentID(123456);
		assertNotNull(caList);
		assertEquals(caList.size(), 2);
		assertEquals(caList.get(0).getTimeStamp().getTime(), 200000000);

		ca = caDao.findLatestByContentIDFacebookID(123456, "3535353");
		assertNull(ca);

		ca = caDao.findLatestByContentIDFacebookID(123456, "888888");
		assertNotNull(ca);
		assertEquals(ca.getFacebookID(), "888888");
		assertEquals(ca.getTimeStamp().getTime(), 300000000);

        ca = caDao.findLatestByContentID(123456);
        assertNotNull(ca);
        assertEquals(ca.getFacebookID(), "888888");
        assertEquals(ca.getTimeStamp().getTime(), 300000000);
		
		caDao.deleteByContentID(123456);
		assertEquals(caDao.findAll().size(), 0);
		
        caDao.deleteAll();
        assertEquals(caDao.findAll().size(), 0);
	}


	@Test @Ignore
	public void testThreads() throws Exception {
		System.out.println("Testing threads.");
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		
		System.out.println("Checking 5000 inserts threads..");
		//Testing insert threads
		for (int i = 0; i < 5000; i++) {
			final int j = i;
			executorService.execute(new Runnable() {
				public void run() {
					ContentAccess ca = new ContentAccess();
					ca.setContentID(j);
					ca.setFacebookID("888888");
					ca.setTimeStamp(new Date(j));
					try {
						DAOFactory.getContentAccessDAO().insert(ca);
					} catch (Exception e) {
						System.out.println("Error in " + j + "th insert thread." + e.getMessage());
					}
				}
			});
		}
		Thread.sleep(10000);
		assertEquals(DAOFactory.getContentAccessDAO().findAll().size(), 5000);
		
		System.out.println("Checking 5000 query threads..");
		//Testing query threads
		List<Future<ContentAccess> > futures = new ArrayList<Future<ContentAccess> >();
		for (int i = 0; i < 5000; i++) {
			final int j = i;
			Future<ContentAccess> f = executorService.submit(new Callable<ContentAccess>() {
				@Override
				public ContentAccess call() throws Exception {
					return DAOFactory.getContentAccessDAO().findLatestByContentIDFacebookID(j, "888888");
				}
			});
			futures.add(f);
		}
		Thread.sleep(2000);
		for (int k=0; k<futures.size(); k++) {
			assertEquals(((ContentAccess) futures.get(k).get(100, TimeUnit.MILLISECONDS))
					.getTimeStamp().getTime(), k);
		}
		executorService.shutdown();
	}

	@After
	public void tearDown() {
        caDao.deleteTable();
        cDao.deleteTable();
	}

}
