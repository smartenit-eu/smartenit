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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.smartenit.unada.ctm.cache.impl.CacheManagerImpl;
import eu.smartenit.unada.ctm.cache.impl.ContentManagerImpl;
import eu.smartenit.unada.db.dao.impl.CacheDAO;
import eu.smartenit.unada.db.dao.impl.ContentDAO;
import eu.smartenit.unada.db.dao.impl.SocialPredictionParametersDAO;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Content;
import eu.smartenit.unada.db.dto.SocialPredictionParameters;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

public class CacheManagerTest {
	
    List<Content> contents;
    
	private ContentDAO contentDAO = mock(ContentDAO.class);
	private CacheDAO cacheDAO = mock(CacheDAO.class);
	private SocialPredictionParametersDAO socialDAO = mock(SocialPredictionParametersDAO.class);
	private ContentManager contentManager = mock(ContentManagerImpl.class);
	private CacheManagerImpl cacheManager;
	private SocialPredictionParameters socialParams;
	
	@Before
	public void setup() throws Exception {
        contents = new ArrayList<Content>();
        Content c = new Content();
        c.setContentID(105239904);
        contents.add(c);
        
        c = new Content();
        c.setContentID(654321);
        contents.add(c);
        
        socialParams = new SocialPredictionParameters();
        socialParams.setLambda1(1);
        socialParams.setLambda2(1);
        socialParams.setLambda3(1);
        socialParams.setLambda4(1);
        socialParams.setLambda5(1);
        socialParams.setLambda6(1);
	}


    @Test
    public void testDeleteContents() throws IOException {
    	System.out.println("Should delete expired contents.");
    	
    	when(contentDAO.findAllOutDated(anyLong())).thenReturn(contents);
		DAOFactory.setContentDAO(contentDAO);
		
		when(cacheDAO.findLast()).thenReturn(null);
		DAOFactory.setCacheDAO(cacheDAO);
    	
        cacheManager = new CacheManagerImpl();
        cacheManager.setContentManager(contentManager);
        cacheManager.deleteExpired();
        
        verify(contentDAO, times(1)).findAllOutDated(anyLong());
        verify(contentManager, times(1)).deleteContents(anyListOf(Content.class));
    }
    
    @Test
    public void testUpdateCacheFetchOneIgnoreOne() {
    	System.out.println("Should prefetch the first and ignore the second.");
    	
        when(contentDAO.findTotalSize()).thenReturn(0L);
		DAOFactory.setContentDAO(contentDAO);
		
		when(cacheDAO.findLast()).thenReturn(null);
		DAOFactory.setCacheDAO(cacheDAO);
		
		when(socialDAO.findLast()).thenReturn(socialParams);
		DAOFactory.setSocialPredictionParametersDAO(socialDAO);
    	
    	List<Content> socialContentList = new ArrayList<Content>();
    	
    	//should be prefetched
    	Content c = new Content();
    	c.setContentID(32369539);
    	c.setCacheScore(0.6);
    	socialContentList.add(c);
    	
    	//should be ignored
    	c = new Content();
    	c.setContentID(105239904);
    	c.setCacheScore(0.4);
    	socialContentList.add(c);
    	
    	cacheManager = new CacheManagerImpl();
    	cacheManager.setContentManager(contentManager);
    	cacheManager.updateCache(socialContentList, null);
    	
    	verify(contentManager, times(1)).prefetchContent(any(Content.class));
        verify(contentManager, times(1)).deleteContents(anyListOf(Content.class), any(Long.class));
    }
    
    @Test
    public void testUpdateCacheFetchAll() {
    	System.out.println("Should prefetch all.");
    	
        when(contentDAO.findTotalSize()).thenReturn(0L);
		DAOFactory.setContentDAO(contentDAO);
		
		when(cacheDAO.findLast()).thenReturn(null);
		DAOFactory.setCacheDAO(cacheDAO);
		
		when(socialDAO.findLast()).thenReturn(socialParams);
		DAOFactory.setSocialPredictionParametersDAO(socialDAO);
    	
    	List<Content> socialContentList = new ArrayList<Content>();
    	
    	//should be prefetched
    	Content c = new Content();
    	c.setContentID(32369539);
    	c.setCacheScore(0.6);
    	socialContentList.add(c);
    	
    	//should be ignored
    	c = new Content();
    	c.setContentID(105239904);
    	c.setCacheScore(0.6);
    	socialContentList.add(c);
    	
    	cacheManager = new CacheManagerImpl();
    	cacheManager.setContentManager(contentManager);
    	cacheManager.updateCache(socialContentList, null);
    	
    	verify(contentManager, times(2)).prefetchContent(any(Content.class));
        verify(contentManager, times(1)).deleteContents(anyListOf(Content.class), any(Long.class));
    }
    
    @Test
    public void testUpdateCacheIgnoreAll() {
    	System.out.println("Should ignore all.");
    	
        when(contentDAO.findTotalSize()).thenReturn(0L);
		DAOFactory.setContentDAO(contentDAO);
		
		when(cacheDAO.findLast()).thenReturn(null);
		DAOFactory.setCacheDAO(cacheDAO);
		
		when(socialDAO.findLast()).thenReturn(socialParams);
		DAOFactory.setSocialPredictionParametersDAO(socialDAO);
    	
    	List<Content> socialContentList = new ArrayList<Content>();
    	
    	//should be prefetched
    	Content c = new Content();
    	c.setContentID(32369539);
    	c.setCacheScore(0);
    	socialContentList.add(c);
    	
    	//should be ignored
    	c = new Content();
    	c.setContentID(105239904);
    	c.setCacheScore(0);
    	socialContentList.add(c);
    	
    	cacheManager = new CacheManagerImpl();
    	cacheManager.setContentManager(contentManager);
    	cacheManager.updateCache(socialContentList, null);
    	
    	verify(contentManager, times(0)).prefetchContent(any(Content.class));
        verify(contentManager, times(1)).deleteContents(anyListOf(Content.class), any(Long.class));
    }
    
    @Test
    public void testUpdateCacheFullCacheAboveThresholdIgnoreAll() {
    	System.out.println("Full cache, above threshold, should ignore all.");
    	
        when(contentDAO.findTotalSize()).thenReturn(8000000000L);
		DAOFactory.setContentDAO(contentDAO);
		
		when(cacheDAO.findLast()).thenReturn(null);
		DAOFactory.setCacheDAO(cacheDAO);
		
		when(socialDAO.findLast()).thenReturn(socialParams);
		DAOFactory.setSocialPredictionParametersDAO(socialDAO);
    	
    	List<Content> socialContentList = new ArrayList<Content>();
    	
    	//should be prefetched
    	Content c = new Content();
    	c.setContentID(32369539);
    	c.setCacheScore(0.6);
    	socialContentList.add(c);
    	
    	//should be ignored
    	c = new Content();
    	c.setContentID(105239904);
    	c.setCacheScore(0.6);
    	socialContentList.add(c);
    	
    	cacheManager = new CacheManagerImpl();
    	cacheManager.setContentManager(contentManager);
    	cacheManager.updateCache(socialContentList, null);
    	
    	verify(contentManager, times(0)).prefetchContent(any(Content.class));
    	verify(contentManager, times(1)).deleteContents(anyListOf(Content.class));
    }
    
    @Test
    public void testUpdateCacheFullCacheBelowThresholdIgnoreAll() {
    	System.out.println("Full cache, below threshold, should ignore all.");
    	
        when(contentDAO.findTotalSize()).thenReturn(8000000000L);
		DAOFactory.setContentDAO(contentDAO);
		
		when(cacheDAO.findLast()).thenReturn(null);
		DAOFactory.setCacheDAO(cacheDAO);
		
		when(socialDAO.findLast()).thenReturn(socialParams);
		DAOFactory.setSocialPredictionParametersDAO(socialDAO);
    	
    	List<Content> socialContentList = new ArrayList<Content>();
    	
    	//should be prefetched
    	Content c = new Content();
    	c.setContentID(32369539);
    	c.setCacheScore(0.1);
    	socialContentList.add(c);
    	
    	//should be ignored
    	c = new Content();
    	c.setContentID(105239904);
    	c.setCacheScore(0.1);
    	socialContentList.add(c);
    	
    	cacheManager = new CacheManagerImpl();
    	cacheManager.setContentManager(contentManager);
    	cacheManager.updateCache(socialContentList, null);
    	
    	verify(contentManager, times(0)).prefetchContent(any(Content.class));
        verify(contentManager, times(1)).deleteContents(anyListOf(Content.class));
    }

    @Test
    public void testUpdateCacheNearFullCacheAboveThresholdIgnoreSecond() {
        System.out.println("Near full cache, above threshold, should ignore second.");

        when(contentDAO.findTotalSize()).thenReturn(7890000000L);
        DAOFactory.setContentDAO(contentDAO);

        when(cacheDAO.findLast()).thenReturn(null);
        DAOFactory.setCacheDAO(cacheDAO);
        
        when(socialDAO.findLast()).thenReturn(socialParams);
		DAOFactory.setSocialPredictionParametersDAO(socialDAO);

        List<Content> socialContentList = new ArrayList<Content>();

        //should be prefetched
        Content c = new Content();
        c.setContentID(32369539);
        c.setCacheScore(0.6);
        socialContentList.add(c);

        //should be ignored
        c = new Content();
        c.setContentID(105239904);
        c.setCacheScore(0.6);
        socialContentList.add(c);

        cacheManager = new CacheManagerImpl();
        cacheManager.setContentManager(contentManager);
        cacheManager.updateCache(socialContentList, null);

        verify(contentManager, times(1)).prefetchContent(any(Content.class));
        verify(contentManager, times(1)).deleteContents(anyListOf(Content.class));
    }
    
    @Test
    public void testUpdateCacheFetchAllSocialOverlay() {
    	System.out.println("Should prefetch all from both overlay and social.");
    	
        when(contentDAO.findTotalSize()).thenReturn(0L);
		DAOFactory.setContentDAO(contentDAO);
		
		when(cacheDAO.findLast()).thenReturn(null);
		DAOFactory.setCacheDAO(cacheDAO);
		
		when(socialDAO.findLast()).thenReturn(socialParams);
		DAOFactory.setSocialPredictionParametersDAO(socialDAO);
    	
    	List<Content> socialContentList = new ArrayList<Content>();
    	
    	//should be prefetched
    	Content c = new Content();
    	c.setContentID(32369539);
    	c.setCacheScore(0.6);
    	socialContentList.add(c);
    	
    	//should be prefetched
    	c = new Content();
    	c.setContentID(105239904);
    	c.setCacheScore(0.6);
    	socialContentList.add(c);
    	
    	List<Content> overlayContentList = new ArrayList<Content>();
    	
    	//should be prefetched
    	c = new Content();
    	c.setContentID(23414);
    	c.setCacheScore(0.6);
    	overlayContentList.add(c);

        //should be prefetched
        c = new Content();
        c.setContentID(755753);
        c.setCacheScore(0.6);
        overlayContentList.add(c);
    	
    	cacheManager = new CacheManagerImpl();
    	cacheManager.setContentManager(contentManager);
    	cacheManager.updateCache(socialContentList, overlayContentList);
    	
    	verify(contentManager, times(4)).prefetchContent(any(Content.class));
        verify(contentManager, times(1)).deleteContents(anyListOf(Content.class), any(Long.class));
    }
    
    @Test
    public void testUpdateCacheContentInBothListsLowScore() {
    	System.out.println("Should prefetch all, content exists in both lists, "
    			+ "but is low scored in overlay.");
    	
        when(contentDAO.findTotalSize()).thenReturn(0L);
		DAOFactory.setContentDAO(contentDAO);
		
		when(cacheDAO.findLast()).thenReturn(null);
		DAOFactory.setCacheDAO(cacheDAO);
		
		when(socialDAO.findLast()).thenReturn(socialParams);
		DAOFactory.setSocialPredictionParametersDAO(socialDAO);
    	
    	List<Content> socialContentList = new ArrayList<Content>();
    	
    	//should be prefetched
    	Content c = new Content();
    	c.setContentID(105239904);
    	c.setCacheScore(0.6);
    	socialContentList.add(c);
    	
    	//should be prefetched
    	c = new Content();
    	c.setContentID(32369539);
    	c.setCacheScore(0.6);
    	socialContentList.add(c);
    	
    	List<Content> overlayContentList = new ArrayList<Content>();
    	
    	//should not be prefetched
    	c = new Content();
    	c.setContentID(105239904);
    	c.setCacheScore(0.4);
    	overlayContentList.add(c);
    	
    	cacheManager = new CacheManagerImpl();
    	cacheManager.setContentManager(contentManager);
    	cacheManager.updateCache(socialContentList, overlayContentList);
    	
    	verify(contentManager, times(2)).prefetchContent(any(Content.class));
        verify(contentManager, times(1)).deleteContents(anyListOf(Content.class), any(Long.class));
    }
    
    @Test
    public void testUpdateCacheContentInBothListsGoodScore() {
    	System.out.println("Should prefetch all, content exists in both lists, "
    			+ "and has good scored in overlay.");
    	
        when(contentDAO.findTotalSize()).thenReturn(0L);
		DAOFactory.setContentDAO(contentDAO);
		
		when(cacheDAO.findLast()).thenReturn(null);
		DAOFactory.setCacheDAO(cacheDAO);
		
		when(socialDAO.findLast()).thenReturn(socialParams);
		DAOFactory.setSocialPredictionParametersDAO(socialDAO);
    	
    	List<Content> socialContentList = new ArrayList<Content>();
    	
    	//should not be prefetched
    	Content c = new Content();
    	c.setContentID(105239904);
    	c.setCacheScore(0.6);
    	socialContentList.add(c);
    	
    	//should be prefetched
    	c = new Content();
    	c.setContentID(32369539);
    	c.setCacheScore(0.6);
    	socialContentList.add(c);
    	
    	List<Content> overlayContentList = new ArrayList<Content>();
    	
    	//should be prefetched
    	c = new Content();
    	c.setContentID(105239904);
    	c.setCacheScore(0.7);
    	overlayContentList.add(c);
    	
    	cacheManager = new CacheManagerImpl();
    	cacheManager.setContentManager(contentManager);
    	cacheManager.updateCache(socialContentList, overlayContentList);
    	
    	verify(contentManager, times(2)).prefetchContent(any(Content.class));
        verify(contentManager, times(1)).deleteContents(anyListOf(Content.class), any(Long.class));
    }
    
    @After
    public void teardown() throws InterruptedException {
    	Thread.sleep(3000);
    }

}
