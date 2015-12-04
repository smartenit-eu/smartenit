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
package eu.smartenit.unada.ctm.cache;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import eu.smartenit.unada.ctm.cache.impl.ContentManagerImpl;
import eu.smartenit.unada.db.dao.impl.ContentDAO;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Content;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyLong;

public class ContentManagerTest {

	private ContentDAO contentDAO = mock(ContentDAO.class);
	private ContentManagerImpl contentManager;
	
	@Before
	public void setup() throws IOException {
		Mockito.doNothing().when(contentDAO).delete(123456);
        Mockito.doNothing().when(contentDAO).deleteBatch(anyListOf(Content.class).listIterator());
		DAOFactory.setContentDAO(contentDAO);
	}

	@Test
	public void testDeleteNullContent() {
		System.out.println("Should not delete null content.");
		contentManager = new ContentManagerImpl();
		contentManager.deleteContent(null);
		verify(contentDAO, times(0)).delete(anyLong());
	}

    @Test
	public void testDeleteContent() {	
    	System.out.println("Should delete content.");
		Content c = new Content();
		c.setContentID(123456);
		c.setPath("/unada/path/to/file");
		
		contentManager = new ContentManagerImpl();
		contentManager.deleteContent(c);
		
		verify(contentDAO, times(1)).delete(123456);
	}
    
    @Test
    public void testDeleteNullContents() {
    	System.out.println("Should not delete null contents.");
        contentManager = new ContentManagerImpl();
        contentManager.deleteContents(null);
        verify(contentDAO, times(0)).deleteBatch(anyListOf(Content.class).listIterator());
    }
    
    @Test
    public void testDeleteEmptyListContents() {
    	System.out.println("Should not delete empty list contents.");
        contentManager = new ContentManagerImpl();
        contentManager.deleteContents(new ArrayList<Content>());
        verify(contentDAO, times(0)).deleteBatch(anyListOf(Content.class).listIterator());
    }

    @Test
    public void testDeleteContents() {
    	System.out.println("Should delete not empty contents.");
    	
        List<Content> contents = new ArrayList<Content>();
        Content c = new Content();
        c.setContentID(123456);
        c.setPath("/unada/path/to/file");
        contents.add(c);
        
        c = new Content();
        c.setContentID(654321);
        c.setPath("/unada/path/to/file2");
        contents.add(c);
        
        contentManager = new ContentManagerImpl();
        contentManager.deleteContents(contents);

        verify(contentDAO, times(1)).deleteBatch(anyListOf(Content.class).listIterator());
    }
    
    @Test
    public void testPrefetchNullContent() {
    	System.out.println("Should not prefetch null content.");
    	contentManager = new ContentManagerImpl();
    	
    	contentManager.prefetchContent(null);
    }
    
    @Test @Ignore
    public void testPrefetchSocialContent() throws Exception {
    	System.out.println("Should prefetch social content.");
    	
    	Content c = new Content();
    	c.setContentID(105239904);
    	
    	contentManager = new ContentManagerImpl();
    	contentManager.prefetchContent(c); 	
    }
    
    @Test @Ignore
    public void testPrefetchOverlayContent() throws Exception {
    	System.out.println("Should prefetch overlay content.");
    	
    	Content c = new Content();
    	c.setContentID(105239904);
    	c.setPath("/unada/path/to/video");
    	
    	contentManager = new ContentManagerImpl();
    	contentManager.prefetchContent(c);
    }
    
    @Test @Ignore
    public void testPrefetchContents() throws Exception {
    	System.out.println("Should prefetch contents list.");
    	
    	List<Content> contents = new ArrayList<Content>();
    	
    	Content c = new Content();
    	c.setContentID(32369539);
    	contents.add(c);
    	
    	c = new Content();
    	c.setContentID(105239904);
    	c.setPath("/unada/path/to/video");
    	contents.add(c);
    	
    	contentManager = new ContentManagerImpl();    	
    	contentManager.prefetchContents(contents);
    }
    
    @After
    public void teardown() throws InterruptedException {
    	Thread.sleep(3000);
        VimeoTestsUtil.deleteDirectory(new File("/unada"));
    }
    

}
