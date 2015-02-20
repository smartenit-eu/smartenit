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
package eu.smartenit.unada.sm;

import com.restfb.Connection;
import com.restfb.FacebookClient;
import com.restfb.types.User;
import eu.smartenit.unada.db.dao.impl.FeedItemDAO;
import eu.smartenit.unada.db.dao.impl.FriendDAO;
import eu.smartenit.unada.db.dao.impl.VideoInfoDAO;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test class for SocialMonitor
 */
public class SocialMonitorTest {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(SocialMonitorTest.class);

    /**
     * Dummy values for testing
     */
    private final static String FACEBOOK_ID = "1492826064306740";
    private final static String OAUTH_TOKEN = "CAACEdEose0cBAK1ZASLTZCXyruouG6Ogo70h1dZBvu1YOMlZAWmLf0Wil0N14jGI52KyZBAdxNMWyWjGz1aZCTu6JmZCZCkWxpPAWjwPS3rDlNKrkYTp8TuCG0vOXDT6IO4YVIiZBZCiwrXfEiKSIacn1qeI8MogrFt2EsYMceo1ZCSp4y0LijtlP5s8ET3EnttioB1h2L6aDLL1l9GjOy4GVue";

    /**
     * Tests the operation of SocialMonitor
     */
    @Test
    public void runSocialMonitor() {
        Owner owner = new Owner();
        owner.setFacebookID(FACEBOOK_ID);
        owner.setOauthToken(OAUTH_TOKEN);

        try {
            FeedItemDAO feedItemDAO = mock(FeedItemDAO.class);
            DAOFactory.setFeedItemDAO(feedItemDAO);
            when(feedItemDAO.findById(anyString())).thenReturn(null);
            doNothing().when(feedItemDAO).insert(any(FeedItem.class));

            VideoInfoDAO videoInfoDAO = mock(VideoInfoDAO.class);
            DAOFactory.setVideoInfoDAO(videoInfoDAO);
            when(videoInfoDAO.findById(anyLong())).thenReturn(null);
            doNothing().when(videoInfoDAO).insert(any(VideoInfo.class));

            FriendDAO friendDAO = mock(FriendDAO.class);
            DAOFactory.setFriendDAO(friendDAO);
            when(videoInfoDAO.findById(anyLong())).thenReturn(null);
            doNothing().when(friendDAO).insert(any(Friend.class));

            MonitorRunner runner = new MonitorRunner(owner);
            MonitorRunner runnerSpy = spy(runner);

            FeedItem fi1 = new FeedItem();
            fi1.setFeedType(FeedItem.FeedType.FEED);
            fi1.setContentID(1);

            FeedItem fi2 = new FeedItem();
            fi2.setFeedType(FeedItem.FeedType.POST_OF_FRIEND);
            fi2.setContentID(2);

            List<FeedItem> feedItems = new ArrayList<FeedItem>();
            feedItems.add(fi1);
            feedItems.add(fi2);

            doReturn(feedItems).when(runnerSpy).getFeedItems(owner);

            VideoInfo vi1 = new VideoInfo();
            VideoInfo vi2 = new VideoInfo();

            doReturn(vi1).when(runnerSpy).getVideoInfo(fi1.getContentID());
            doReturn(vi2).when(runnerSpy).getVideoInfo(fi2.getContentID());

            Friend f1 = new Friend();
            List<Friend> friendList = new ArrayList<Friend>();
            friendList.add(f1);

            doReturn(friendList).when(runnerSpy).getFriends(owner);
            doReturn(friendList).when(runnerSpy).getFriendsFromDB();

            UNaDaConfiguration uc = new UNaDaConfiguration();
            uc.setSocialInterval(10000);
            SocialMonitor sm = new SocialMonitor(uc);

            ScheduledFuture<?> sf = sm.startMonitoring(runnerSpy);

            Thread.sleep(10000);

            sm.stopMonitoring(sf);

        } catch (Exception e) {
            logger.error(e.getMessage(), e.getStackTrace());
            fail();
        }
    }

    /**
     * Tests friend list retrieval
     */
    @Test
    public void getFriendsTest() {
        try {
            Owner owner = new Owner();
            owner.setFacebookID("1");

            MonitorRunner runner = new MonitorRunner(owner);

            FacebookClient client = mock(FacebookClient.class);
            @SuppressWarnings("unchecked")
            Connection<User> connection = mock(Connection.class);

            User firstFriend = createUser("Firstname1", "Lastname1", "123");
            User secondFriend = createUser("Firstname2", "Lastname2", "456");

            List<User> data = new ArrayList<User>();
            data.add(firstFriend);
            data.add(secondFriend);

            when(connection.getData()).thenReturn(data);

            when(client.fetchConnection("me/friends", User.class)).thenReturn(connection);

            List<Friend> friendList = runner.getFriends(client, owner);

            assertEquals(friendList.get(0).getFacebookName(), "Firstname1" + "Lastname1");
            assertEquals(friendList.get(0).getFacebookID(), "123");

            assertEquals(friendList.get(1).getFacebookName(), "Firstname2" + "Lastname2");
            assertEquals(friendList.get(1).getFacebookID(), "456");
        } catch (Exception e) {
            fail();
        }

    }

    private User createUser(String firstName, String lastName, String id)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        User user = new User();
        Field name = user.getClass().getSuperclass().getDeclaredField("name");
        name.setAccessible(true);
        name.set(user, firstName + lastName);

        Field idField = user.getClass().getSuperclass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, id);

        return user;
    }

}
