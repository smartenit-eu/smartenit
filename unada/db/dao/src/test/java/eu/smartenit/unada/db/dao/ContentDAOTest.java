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
package eu.smartenit.unada.db.dao;

import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.smartenit.unada.db.dao.impl.ContentAccessDAO;
import eu.smartenit.unada.db.dao.impl.ContentDAO;
import eu.smartenit.unada.db.dao.impl.TrustedUserDAO;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Content;
import eu.smartenit.unada.db.dto.Content.ContentCategory;
import eu.smartenit.unada.db.dto.ContentAccess;
import eu.smartenit.unada.db.dto.TrustedUser;

public class ContentDAOTest {

	private ContentDAO cDao;

	private ContentAccessDAO caDao;

	private TrustedUserDAO tDao;

	@Before
	public void setUp() {

		cDao = new ContentDAO();
		cDao.createTable();

		caDao = new ContentAccessDAO();
		caDao.createTable();

		tDao = new TrustedUserDAO();
		tDao.createTable();
	}

	@Test 
	public void testFunctions() throws Exception {
		System.out.println("Testing all queries.");
		assertEquals(cDao.findAll().size(), 0);
		assertEquals(cDao.findAllIDs().size(), 0);
		assertEquals(caDao.findAll().size(), 0);
		assertNull(cDao.findById(1212));
		assertNull(cDao.findByPath("/a/b/video.mp4"));
		assertEquals(cDao.findByCacheType("social").size(), 0);
		assertEquals(caDao.findByContentID(1212).size(), 0);
		assertEquals(cDao.findAllNotPrefetched().size(), 0);
		assertEquals(cDao.findTotalSize(), 0);
		assertEquals(cDao.findAllOutDated(System.currentTimeMillis() - 1000000)
				.size(), 0);
        assertEquals(cDao.findAllWithAccesses().size(), 0);
		cDao.delete(123456);
		cDao.deleteBatch(new ArrayList<Content>().listIterator());

		Content c = new Content();
		c.setContentID(123456);
		c.setCacheType("social");
		c.setCacheScore(0.5);
		c.setCategory(ContentCategory.comedy);
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
		assertEquals(cDao.findAllIDs().get(0).longValue(), 123456);

		c = cDao.findById(123456);
		assertEquals(c.getCacheType(), "social");
		assertEquals(c.getCacheScore(), 0.5, 0.01);
		assertEquals(c.getQuality(), "480p");
		assertFalse(c.isDownloaded());
		assertTrue(c.isPrefetched());
        assertTrue(c.isPrefetchedVimeo());

		assertEquals(cDao.findAllNotPrefetched().size(), 0);
		assertEquals(cDao.findTotalSize(), 2332323);
		assertEquals(cDao.findAllOutDated(500000000)
				.size(), 0);

		c.setCacheDate(new Date(200000000));
		c.setDownloaded(true);
		c.setPrefetched(false);
		cDao.update(c);

		assertEquals(cDao.findAllOutDated(500000000)
				.size(), 1);
		assertEquals(cDao.findAllOutDated(500000000)
				.get(0).getContentID(), 123456);
		assertEquals(cDao.findAllOutDated(500000000)
				.get(0).getPath(), "/a/b/video.mp4");
		assertEquals(cDao.findAllNotPrefetched().size(), 1);

		assertEquals(cDao.findAll().size(), 1);
		assertEquals(cDao.findByCacheType("social").size(), 1);
		c = cDao.findByPath("/a/b/video.mp4");
		assertEquals(c.getCacheType(), "social");
		assertEquals(c.getCacheScore(), 0.5, 0.01);
		assertEquals(c.getQuality(), "480p");
		assertTrue(c.isDownloaded());
		assertEquals(c.getCacheDate().getTime(), 200000000);
        assertTrue(c.isPrefetchedVimeo());

        List<Content> contents = cDao.findAllWithAccesses();
        assertEquals(contents.size(), 1);
        assertEquals(contents.get(0).getAccessList().size(), 0);

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

		assertEquals(tDao.findAll().size(), 0);
		TrustedUser t = new TrustedUser();
		t.setFacebookID("888888");
		t.setMacAddress("00:00:00:00:00:ff");
		tDao.insert(t);
		assertEquals(tDao.findAll().size(), 1);

		ca = caDao.findLatestByContentIDFacebookID(123456, "3535353");
		assertNull(ca);

		ca = caDao.findLatestByContentIDFacebookID(123456, "888888");
		assertNotNull(ca);
		assertEquals(ca.getFacebookID(), "888888");
		assertEquals(ca.getTimeStamp().getTime(), 300000000);

        contents = cDao.findAllWithAccesses();
        assertEquals(contents.size(), 1);
        assertEquals(contents.get(0).getAccessList().size(), 2);


        Content c1 = new Content();
        c1.setContentID(123456);
        boolean f = cDao.deleteNotAccessedContent(c1, 100000001);
        assertFalse(f);
        assertEquals(cDao.findAll().size(), 1);

        f = cDao.deleteNotAccessedContent(c1, 300000001);
        assertTrue(f);
        assertEquals(cDao.findAll().size(), 0);

        c = new Content();
        c.setContentID(987654);
        c.setCacheType("social");
        c.setCacheScore(0.3);
        c.setCategory(ContentCategory.comedy);
        c.setDownloaded(true);
        c.setPrefetched(false);
        c.setPath("/e/f/video313.mp4");
        c.setQuality("480p");
        c.setSize(67757895);
        c.setUrl("www.vimeo.com/987654");
        c.setPrefetchedVimeo(true);
        c.setCacheDate(new Date(40000000));
        cDao.insert(c);

        assertEquals(cDao.findAll().size(), 1);

        f = cDao.deleteNotAccessedContent(c, 20000001);
        assertFalse(f);
        assertEquals(cDao.findAll().size(), 1);

        f = cDao.deleteNotAccessedContent(c, 40000001);
        assertTrue(f);
        assertEquals(cDao.findAll().size(), 0);

		cDao.deleteAll();
		assertEquals(cDao.findAll().size(), 0);
		assertEquals(caDao.findAll().size(), 0);
	}

	@Test 
	public void testBatchFunction() {
		System.out.println("Testing batch functions.");
		List<Content> contents = new ArrayList<Content>();
		Content c;
		for (int i = 0; i < 1000; i++) {
			c = new Content();
			c.setContentID(i);
			c.setCacheType("social");
			c.setCategory(ContentCategory.comedy);
			c.setCacheScore(new Random().nextDouble());
			c.setPath("/a/b/video.mp4");
			c.setQuality("480p");
			c.setSize(10000);
            c.setDownloaded(true);
			contents.add(c);
		}
		cDao.insertAll(contents.listIterator());
		assertEquals(cDao.findAll().size(), 1000);

		assertEquals(cDao.findAllNotPrefetched().size(), 1000);
		assertEquals(cDao.findTotalSize(), 10000000);
		assertEquals(cDao.findAllIDs().size(), 1000);

		long before = System.currentTimeMillis();
		cDao.deleteBatch(contents.listIterator());
		System.out.println("Delete batch required "
				+ (System.currentTimeMillis() - before) + "ms");
		assertEquals(cDao.findAll().size(), 0);

		contents = new ArrayList<Content>();
		for (int i = 0; i < 1000; i++) {
			c = new Content();
			c.setContentID(i);
			c.setCacheType("social");
			c.setCategory(ContentCategory.comedy);
			c.setCacheScore(new Random().nextDouble());
			c.setPath("/a/b/video.mp4");
			c.setQuality("480p");
			c.setSize(10000);
			contents.add(c);
		}
		cDao.insertAll(contents.listIterator());
		assertEquals(cDao.findAll().size(), 1000);

		cDao.deleteAll();
		assertEquals(cDao.findAll().size(), 0);
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
					Content c = new Content();
					c.setContentID(j);
					c.setCacheType("social");
					c.setCacheScore(new Random().nextDouble());
					c.setPath("/a/b/" + j + ".mp4");
					c.setQuality("480p");
					c.setSize(10000);
					try {
						DAOFactory.getContentDAO().insert(c);
					} catch (Exception e) {
						System.out.println("Error in " + j + "th insert thread." + e.getMessage());
					}
				}
			});
		}
		Thread.sleep(10000);
		assertEquals(DAOFactory.getContentDAO().findAll().size(), 5000);
		
		System.out.println("Checking 5000 query threads..");
		//Testing query threads
		List<Future<Content> > futures = new ArrayList<Future<Content> >();
		for (int i = 0; i < 5000; i++) {
			final int j = i;
			Future<Content> f = executorService.submit(new Callable<Content>() {
				@Override
				public Content call() throws Exception {
					return DAOFactory.getContentDAO().findByPath("/a/b/" + j + ".mp4");
				}
			});
			futures.add(f);
		}
		Thread.sleep(2000);
		for (Future<Content> f : futures) {
			assertEquals(((Content)f.get(100, TimeUnit.MILLISECONDS)).getSize(), 10000);
		}
		executorService.shutdown();
	}

	@After
	public void tearDown() {
        caDao.deleteTable();
		cDao.deleteTable();
		tDao.deleteTable();
	}

}
