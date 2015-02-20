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
package eu.smartenit.unada.ctm.cache;

import eu.smartenit.unada.ctm.cache.impl.OverlayDownloader;
import eu.smartenit.unada.ctm.cache.util.OverlayFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import eu.smartenit.unada.db.dao.impl.ContentDAO;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Content;
import eu.smartenit.unada.om.OverlayManager;

import java.io.File;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

public class OverlayDownloaderTest {
	
	public ContentDAO contentDAO = mock(ContentDAO.class);
	
	public OverlayManager overlayManager = mock(OverlayManager.class);
		
	Content c;
			
	@Before
	public void setup() throws Exception {
		when(contentDAO.findById(32369539)).thenReturn(null);
		
		c = new Content();
		c.setContentID(105239904);
		c.setPath("path/to/video.mp4");
		when(contentDAO.findById(105239904)).thenReturn(c);
		Mockito.doNothing().when(contentDAO).insert(any(Content.class));
		Mockito.doNothing().when(contentDAO).update(any(Content.class));
    	DAOFactory.setContentDAO(contentDAO);
    	
    	when(overlayManager.downloadContent(any(Content.class))).thenReturn(null);
    	OverlayFactory.setOverlayManager(overlayManager);
	}
	
	@Test 
	public void testDownloadVideoNotCached() throws Exception {
        System.out.println("Download not cached video.");
		Content c = new Content();
		c.setContentID(32369539);
		c.setPath("path/to/video2.mp4");
		OverlayDownloader overlayDownloader = new OverlayDownloader(c);
		overlayDownloader.downloadVideo();
		
	}
	
	
	@Test
	public void testDownloadVideoCached() throws Exception {
        System.out.println("Attempting to download cached video, nothing happens.");
		OverlayDownloader overlayDownloader = new OverlayDownloader(c);
		overlayDownloader.downloadVideo();
		
	}

	
	
	@After
	public void teardown() throws InterruptedException {
		Thread.sleep(3000);
        VimeoTestsUtil.deleteDirectory(new File("path"));
	}
	

}
