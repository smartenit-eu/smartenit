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

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import eu.smartenit.unada.commons.constants.CacheConstants;
import eu.smartenit.unada.ctm.cache.impl.VimeoDownloader;
import eu.smartenit.unada.ctm.cache.impl.VimeoInfoRetriever;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.axet.vget.VGet;
import com.github.axet.vget.info.VideoInfo;

import eu.smartenit.unada.db.dao.impl.ContentDAO;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Content;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

public class VimeoDownloaderTest {
	
	public ContentDAO contentDAO = mock(ContentDAO.class);

	private File file32369539mobile;
			
	@Before
	public void setup() throws Exception {
		when(contentDAO.findById(32369539)).thenReturn(null);
		when(contentDAO.findById(105239904)).thenReturn(new Content());
		Mockito.doNothing().when(contentDAO).insert(any(Content.class));
		Mockito.doNothing().when(contentDAO).update(any(Content.class));
    	DAOFactory.setContentDAO(contentDAO);

        CacheConstants.cachePath = "test/unada";
    	file32369539mobile = new File("test/unada/03110/750/73389900.mp4");
	}
	
	@Test 
	public void testDownloadVideoMobileNotCached() throws Exception {
        System.out.println("Download not cached mobile video.");
		assertFalse(file32369539mobile.exists());
		VimeoDownloader vimeoDownloader = new VimeoDownloader("https://www.vimeo.com/32369539", false);
		vimeoDownloader.downloadVideo();
		assertTrue(file32369539mobile.exists());
	}
	
	@Test
	public void testDownloadVideoCached() throws Exception {
        System.out.println("Download cached video, nothing happens.");
		VimeoDownloader vimeoDownloader = new VimeoDownloader("https://www.vimeo.com/105239904", false);
		vimeoDownloader.downloadVideo();
	}

	@Test
	public void testGetVideoStorage() throws MalformedURLException {
        System.out.println("Test get video storage.");
		VimeoDownloader vimeoDownloader = new VimeoDownloader("https://www.vimeo.com/32369539", false);

		VideoInfo videoInfo = new VimeoInfoRetriever().retrieveVideoInfo("https://www.vimeo.com/32369539");
		VGet v = new VGet(videoInfo, new File(""));

		Path p = vimeoDownloader.getVideoStorage(v);
		assertNotNull(p);
		assertEquals(Paths.get(CacheConstants.cachePath, "03110","750","73389900.mp4"), p);
	}


    @Test
    public void testGetVideoID() throws Exception {
        System.out.println("Test get video id.");
        VimeoDownloader vimeoDownloader = new VimeoDownloader("", false);

        long videoID = vimeoDownloader.getVideoID("https://vimeo.com/32369539");
        assertEquals(videoID, 32369539);

        videoID = vimeoDownloader.getVideoID("https://vimeo.com/32369539/");
        assertEquals(videoID, 32369539);

        videoID = vimeoDownloader.getVideoID("https://vimeo.com/dada/fsafs/dadada/32369539/");
        assertEquals(videoID, 32369539);
    }
	
	@After
	public void teardown() throws InterruptedException {
		Thread.sleep(3000);
		VimeoTestsUtil.deleteDirectory(new File("test/"));
	}
	

}
