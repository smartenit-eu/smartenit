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

import eu.smartenit.unada.db.dao.impl.VideoInfoDAO;
import eu.smartenit.unada.db.dto.VideoInfo;

public class VideoInfoDAOTest {
	
	private VideoInfoDAO vDao;
			
	@Before
	public void setUp() {
		vDao = new VideoInfoDAO();
		vDao.createTable();
	}
	
	@Test
	public void testFunctions() throws Exception {
		assertEquals(vDao.findAll().size(), 0);
		assertNull(vDao.findById(123456));
		
		VideoInfo v = new VideoInfo();
		v.setContentID(123456);
		v.setPublishDate(new Date(1000000000));
		v.setViewsNumber(232);
		vDao.insert(v);
				
		assertEquals(vDao.findAll().size(), 1);
		v = vDao.findById(123456);
		assertEquals(v.getPublishDate().getTime(), 1000000000);
		
		v.setViewsNumber(1000);
		vDao.update(v);
		
		assertEquals(vDao.findAll().size(), 1);
		v = vDao.findById(123456);
		assertEquals(v.getViewsNumber(), 1000);

		v.setPublishDate(new Date(2000000000));
		v.setViewsNumber(20000);
		vDao.insert(v);

		assertEquals(vDao.findAll().size(), 1);
		v = vDao.findById(123456);
		assertEquals(v.getViewsNumber(), 20000);
		assertEquals(v.getPublishDate().getTime(), 2000000000);
		
		vDao.delete(123456);
		assertEquals(vDao.findAll().size(), 0);
		
		List<VideoInfo> vList = new ArrayList<VideoInfo>();
		for (int i=0; i<1000; i++) {
			v = new VideoInfo();
			v.setContentID(i);
			v.setPublishDate(new Date(System.currentTimeMillis()));
			v.setViewsNumber(i);
			vList.add(v);
		}
		vDao.insertAll(vList.listIterator());
		assertEquals(vDao.findAll().size(), 1000);
		
		vDao.deleteAll();
		assertEquals(vDao.findAll().size(), 0);
		
		vList = new ArrayList<VideoInfo>();
		for (int i=0; i<1000; i++) {
			v = new VideoInfo();
			
			//insert contents with the same id.
			v.setContentID(123456);
			v.setPublishDate(new Date(System.currentTimeMillis()));
			v.setViewsNumber(i);
			vList.add(v);
		}
		
		vDao.insertAll(vList.listIterator());
		assertEquals(vDao.findAll().size(), 1);
		assertEquals(vDao.findAll().get(0).getViewsNumber(), 999);
		
		vDao.deleteAll();
		assertEquals(vDao.findAll().size(), 0);
	}
	
	@After
	public void tearDown() {
		vDao.deleteTable();
	}

}
