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
package eu.smartenit.unada.sa;

import eu.smartenit.unada.db.dao.impl.*;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.*;
import eu.smartenit.unada.om.IOverlayManager;
import eu.smartenit.unada.om.OverlayManager;

import org.joda.time.LocalDateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for SocialAnalyzer
 */
public class SocialAnalyzerTest {

    @Test
    public void predictionTest() {

        //Creating social prediction parameter mock objects
        SocialPredictionParameters spp = new SocialPredictionParameters();
        spp.setLambda1(-3646.8405);
        spp.setLambda2(-348.238);
        spp.setLambda3(-174.6666);
        spp.setLambda4(32.539);
        spp.setLambda5(-1.0468);
        spp.setLambda6(1.8463);

        SocialPredictionParametersDAO sppDAO = mock(SocialPredictionParametersDAO.class);
        when(sppDAO.findLast()).thenReturn(spp);


        DAOFactory.setSocialPredictionParametersDAO(sppDAO);

        //Creating feed item mock objects
        FeedItemDAO feedItemDAO = mock(FeedItemDAO.class);

        Date now = new LocalDateTime(2014, 5, 13, 17, 30).toDate();

        FeedItem feedItem = new FeedItem();
        feedItem.setContentID(17);
        feedItem.setFeedType(FeedItem.FeedType.FEED);
        feedItem.setTime(now);

        FeedItem feedItem2 = new FeedItem();
        feedItem2.setContentID(18);
        feedItem2.setFeedType(FeedItem.FeedType.FEED);
        feedItem2.setTime(now);

        //Creating  video info mock objects
        VideoInfoDAO videoInfoDAO = mock(VideoInfoDAO.class);

        LocalDateTime ldtv1 = new LocalDateTime(2014, 5, 2, 17, 0);
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setContentID(17);
        videoInfo.setViewsNumber(16);
        videoInfo.setPublishDate(ldtv1.toDate());

        LocalDateTime ldtv2 = new LocalDateTime(2014, 5, 3, 16, 30);
        VideoInfo videoInfo2 = new VideoInfo();
        videoInfo2.setContentID(18);
        videoInfo2.setViewsNumber(15);
        videoInfo2.setPublishDate(ldtv2.toDate());

        when(videoInfoDAO.findById(17)).thenReturn(videoInfo);
        when(videoInfoDAO.findById(18)).thenReturn(videoInfo2);

        DAOFactory.setVideoInfoDAO(videoInfoDAO);

        //Creating unada mocks
        IOverlayManager om = mock(OverlayManager.class);

        UnadaInfo localUnadaInfo = new UnadaInfo();
        localUnadaInfo.setLatitude(47.368227);
        localUnadaInfo.setLongitude(8.539724);

        when(om.getuNaDaInfo()).thenReturn(localUnadaInfo);

        UnadaInfo firstContent1 = new UnadaInfo();
        firstContent1.setUnadaID("id1");
        firstContent1.setUnadaAddress("127.0.0.1");
        firstContent1.setUnadaPorts(3456);
        firstContent1.setLatitude(47.737188);
        firstContent1.setLongitude(7.353973);

        UnadaInfo firstContent2 = new UnadaInfo();
        firstContent2.setUnadaID("id2");
        firstContent2.setUnadaAddress("127.0.0.2");
        firstContent2.setUnadaPorts(3567);
        firstContent2.setLatitude(47.563409);
        firstContent2.setLongitude(7.607346);

        LinkedHashSet<UnadaInfo> firstUnadaList = new LinkedHashSet<UnadaInfo>();
        firstUnadaList.add(firstContent1);
        firstUnadaList.add(firstContent2);
        when(om.getCloseProviders(17)).thenReturn(firstUnadaList);

        UnadaInfo secondContent1 = new UnadaInfo();
        secondContent1.setUnadaID("id3");
        secondContent1.setUnadaAddress("127.0.0.3");
        secondContent1.setUnadaPorts(5555);
        secondContent1.setLatitude(46.616903);
        secondContent1.setLongitude(2.120361);

        UnadaInfo secondContent2 = new UnadaInfo();
        secondContent1.setUnadaID("id4");
        secondContent1.setUnadaAddress("127.0.0.4");
        secondContent1.setUnadaPorts(4444);
        secondContent1.setLatitude(39.498742);
        secondContent2.setLongitude(-3.625488);

        LinkedHashSet<UnadaInfo> secondUnadaList = new LinkedHashSet<UnadaInfo>();
        secondUnadaList.add(secondContent1);
        secondUnadaList.add(secondContent2);

        when(om.getCloseProviders(18)).thenReturn(secondUnadaList);

        ContentAccessDAO contentAccessDAO = mock(ContentAccessDAO.class);

        ContentAccess contentAccess = new ContentAccess();
        contentAccess.setContentID(17);
        ContentAccess contentAccess2 = new ContentAccess();
        contentAccess2.setContentID(17);
        ContentAccess contentAccess3 = new ContentAccess();
        contentAccess3.setContentID(17);

        List<ContentAccess> contentAccessList1 = new ArrayList<ContentAccess>();
        contentAccessList1.add(contentAccess);
        contentAccessList1.add(contentAccess2);
        contentAccessList1.add(contentAccess3);

        ContentAccess contentAccess4 = new ContentAccess();
        contentAccess4.setContentID(18);
        ContentAccess contentAccess5 = new ContentAccess();
        contentAccess5.setContentID(18);
        ContentAccess contentAccess6 = new ContentAccess();
        contentAccess6.setContentID(18);

        List<ContentAccess> contentAccessList2 = new ArrayList<ContentAccess>();
        contentAccessList2.add(contentAccess4);
        contentAccessList2.add(contentAccess5);
        contentAccessList2.add(contentAccess6);

        when(contentAccessDAO.findByContentID(17)).thenReturn(contentAccessList1);
        when(contentAccessDAO.findByContentID(18)).thenReturn(contentAccessList2);

        DAOFactory.setContentAccessDAO(contentAccessDAO);

        FriendDAO friendDAO = mock(FriendDAO.class);

        Friend friend = new Friend();
        friend.setFacebookID("123");
        List<Friend> friends = new ArrayList<Friend>();
        friends.add(friend);

        when(friendDAO.findAll()).thenReturn(friends);
        DAOFactory.setFriendDAO(friendDAO);


        FeedItem feedItem3 = new FeedItem();
        feedItem3.setContentID(17);
        feedItem3.setFeedType(FeedItem.FeedType.POST_OF_FRIEND);
        feedItem3.setTime(now);

        FeedItem feedItem4 = new FeedItem();
        feedItem4.setContentID(18);
        feedItem4.setFeedType(FeedItem.FeedType.POST_OF_FRIEND);
        feedItem4.setTime(now);

        List<FeedItem> feeds = new ArrayList<FeedItem>();
        feeds.add(feedItem);
        feeds.add(feedItem2);
        feeds.add(feedItem3);
        feeds.add(feedItem4);

        when(feedItemDAO.findAll()).thenReturn(feeds);

        DAOFactory.setFeedItemDAO(feedItemDAO);

        SocialAnalyzer analyzer = new SocialAnalyzer(om);
        List<Content> sortedContentList = analyzer.predicting();

        assertEquals(sortedContentList.get(0).getContentID(), 18);
        assertEquals(sortedContentList.get(1).getContentID(), 17);

    }

    public void runWithoutOverlayManager() {
        SocialPredictionParameters spp = new SocialPredictionParameters();
        spp.setLambda1(-3646.8405);
        spp.setLambda2(-348.238);
        spp.setLambda3(-174.6666);
        spp.setLambda4(32.539);
        spp.setLambda5(-1.0468);
        spp.setLambda6(1.8463);

        SocialPredictionParametersDAO sppDAO = mock(SocialPredictionParametersDAO.class);
        when(sppDAO.findLast()).thenReturn(spp);


        DAOFactory.setSocialPredictionParametersDAO(sppDAO);


        IOverlayManager om = mock(OverlayManager.class);

        UnadaInfo localUnadaInfo = new UnadaInfo();
        localUnadaInfo.setLatitude(47.368227);
        localUnadaInfo.setLongitude(8.539724);

        when(om.getuNaDaInfo()).thenReturn(localUnadaInfo);

        LinkedHashSet<UnadaInfo> list = new LinkedHashSet<UnadaInfo>();
        when(om.getCloseProviders(anyLong())).thenReturn(list);

        SocialAnalyzer sa = new SocialAnalyzer(om);
        sa.predicting();
    }

}
