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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.smartenit.unada.db.dao.impl.FeedItemDAO;
import eu.smartenit.unada.db.dto.FeedItem;
import eu.smartenit.unada.db.dto.FeedItem.FeedType;

public class FeedItemDAOTest {
	
	private FeedItemDAO fDao;
	
	@Before
	public void setUp() {
		fDao = new FeedItemDAO();
		fDao.createTable();
	}
	
	@Test
	public void testFunctions() throws Exception {
		assertEquals(fDao.findAll().size(), 0);
		assertNull(fDao.findById("1234_1234"));
        assertEquals(fDao.findAllByContentID(123456).size(), 0);
		
		FeedItem f = new FeedItem();
		f.setFeedItemID("4444444_1234");
		f.setContentID(123456);
		f.setTime(new Date(100000000));
		f.setType("share");
		f.setUserID("4444444");
		fDao.insert(f);
		
		assertEquals(fDao.findAll().size(), 1);
        assertEquals(fDao.findAllByContentID(123456).size(), 1);
		f = fDao.findById("4444444_1234");
		assertNotNull(f);
		assertEquals(f.getType(), "share");
		assertEquals(f.getUserID(), "4444444");
		assertEquals(f.getFeedType(), FeedType.FEED);
		
		f = new FeedItem();
		f.setFeedItemID("4444444_9999");
		f.setContentID(1234567);
		f.setTime(new Date(100000000));
		f.setType("share");
		f.setUserID("4444444");
		f.setFeedType(FeedType.FEED);
		fDao.insert(f);
		
		assertEquals(fDao.findAll().size(), 2);
        assertEquals(fDao.findAllByContentID(1234567).size(), 1);
		f = fDao.findById("4444444_9999");
		assertNotNull(f);
		assertEquals(f.getContentID(), 1234567);
		assertEquals(f.getType(), "share");
		assertEquals(f.getUserID(), "4444444");
		assertEquals(f.getFeedType(), FeedType.FEED);
		
		f.setType("like");
		f.setFeedType(FeedType.POST_OF_FRIEND);
		fDao.update(f);

        assertEquals(fDao.findAllByContentID(1234567).size(), 1);
		assertEquals(fDao.findAll().size(), 2);
		f = fDao.findById("4444444_9999");
		assertNotNull(f);
		assertEquals(f.getType(), "like");
		assertEquals(f.getUserID(), "4444444");
		assertEquals(f.getFeedType(), FeedType.POST_OF_FRIEND);
		
		
		f.setType("repost");
		f.setContentID(555555);
		fDao.insert(f);
		assertEquals(fDao.findAll().size(), 2);
		f = fDao.findById("4444444_9999");
		assertNotNull(f);
		assertEquals(f.getContentID(), 555555);
		assertEquals(f.getType(), "repost");
		assertEquals(f.getUserID(), "4444444");
		
		fDao.deleteAll();
		assertEquals(fDao.findAll().size(), 0);
        assertEquals(fDao.findAllByContentID(1234567).size(), 0);
		
		List<FeedItem> feedItems = new ArrayList<FeedItem>();
		for (int i=0; i<1000; i++) {
			f = new FeedItem();
			f.setFeedItemID("4444444_" + i);
			f.setContentID(1);
			f.setTime(new Date(100000000));
			f.setType("share");
			f.setUserID("4444444");
			feedItems.add(f);
		}
		fDao.insertAll(feedItems.listIterator());
        assertEquals(fDao.findAllByContentID(1).size(), 1000);
		assertEquals(fDao.findAll().size(), 1000);
		
		fDao.deleteAll();
		assertEquals(fDao.findAll().size(), 0);
		
		feedItems = new ArrayList<FeedItem>();
		for (int i=0; i<1000; i++) {
			f = new FeedItem();
			//inserting the same feed item
			f.setFeedItemID("1");
			f.setContentID(i);
			f.setTime(new Date(100000000));
			f.setType("share");
			f.setUserID("4444444");
			feedItems.add(f);
		}
		fDao.insertAll(feedItems.listIterator());
		assertEquals(fDao.findAll().size(), 1);
		assertEquals(fDao.findAll().get(0).getContentID(), 999);
		assertEquals(fDao.findAll().get(0).getType(), "share");
		assertEquals(fDao.findAll().get(0).getFeedItemID(), "1");
		
		fDao.deleteAll();
		assertEquals(fDao.findAll().size(), 0);
	}
	
	@After
	public void tearDown() {
		fDao.deleteTable();
	}

}
