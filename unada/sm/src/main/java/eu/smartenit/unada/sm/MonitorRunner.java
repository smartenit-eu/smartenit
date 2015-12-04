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
package eu.smartenit.unada.sm;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.types.Post;
import com.restfb.types.User;

import eu.smartenit.unada.commons.constants.UnadaConstants;
import eu.smartenit.unada.commons.logging.UnadaLogger;
import eu.smartenit.unada.db.dao.impl.FeedItemDAO;
import eu.smartenit.unada.db.dao.impl.FriendDAO;
import eu.smartenit.unada.db.dao.impl.TrustedUserDAO;
import eu.smartenit.unada.db.dao.impl.VideoInfoDAO;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.*;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * This class retrieves the relevant content from facebook and vimeo and stores the relevant parts in the database.
 */
class MonitorRunner implements Runnable {

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(MonitorRunner.class);

    /**
     * The owner object representing the user for which data is to be retrieved
     */
    private final Owner owner;

    /**
     * Creates a new MonitorRunner instance.
     *
     * @param owner The owner object representing the user for which data is to be retrieved.
     */
    MonitorRunner(Owner owner) {
        this.owner = owner;
    }

    /**
     * Returns the owner object representing the user for which data is retrieved.
     * @return Returns the owner object representing the user for which data is retrieved.
     */
    public Owner getOwner(){
        return owner;
    }

    /**
     * Method that invokes the MonitorRunner.
     */
    @Override
    public void run() {

        logger.debug("Retrieving friends from facebook...");
        List<Friend> friendList = getFriends(owner);
        logger.debug(friendList.size() + " friends found.");
        storeFriends(friendList);

        logger.debug("Exctracting trusted users from friends...");
        List<TrustedUser> trustedUserList = getTrustedUsers(friendList);
        logger.debug(trustedUserList.size() + " trusted users found.");
        storeTrustedUsers(trustedUserList);

        logger.debug("Retrieving feed items from facebook...");
        List<FeedItem> feedItemList = getFeedItems(owner);
        logger.debug(feedItemList.size() + " feed items received.");
        storeFeedItems(feedItemList);

        logger.debug("Exctracting relevant content IDs from feed items...");
        Set<Long> contentIDSet = createContentIDSet(feedItemList);
        logger.debug(contentIDSet.size() + " relevant content IDs found.");

        logger.debug("Retrieving video info from vimeo...");
        List<VideoInfo> videoInfoList = retrieveVideoInfo(contentIDSet);
        logger.debug(videoInfoList.size() + " video infos found.");
        storeVideoInfos(videoInfoList);
    }

    /**
     * Method that creates TrustedUsers from friend list
     *
     * @param friendList A List of Friends.
     * @return Returns TrustedUsers.
     */
    private List<TrustedUser> getTrustedUsers(List<Friend> friendList) {
        List<TrustedUser> trustedUserList = new ArrayList<TrustedUser>();
        for(Friend friend: friendList) {
            TrustedUser trustedUser = new TrustedUser();
            trustedUser.setFacebookID(friend.getFacebookID());
            trustedUserList.add(trustedUser);
        }
        return trustedUserList;
    }

    /**
     * Returns a List of Friends for a given owner
     * @param owner The owner object which represents the user for which data is to be downloaded
     * @return Returns a List of Friends for a given owner
     */
    List<Friend> getFriends(Owner owner) {
        FacebookClient facebookClient = new DefaultFacebookClient(owner.getOauthToken(), Version.VERSION_2_1);
        return getFriends(facebookClient, owner);
    }

    /**
     * Returns a List of Friends for a given owner
     *
     * @param facebookClient The client object used to retrieve data from facebook
     * @param owner The owner object which represents the user for which data is to be downloaded
     * @return Returns a List of Friends for a given owner
     */
    List<Friend> getFriends(FacebookClient facebookClient, Owner owner) {
        Connection<User> friends = facebookClient.fetchConnection("me/friends", User.class);
        List<Friend> friendList = new ArrayList<Friend>();
        logger.info("Found {} friends.", friends.getData().size());

        for(User facebookFriend: friends.getData()) {
            Friend friend = new Friend();
            friend.setFacebookID(facebookFriend.getId());
            friend.setFacebookName(facebookFriend.getName());
            friendList.add(friend);
        }

        return friendList;
    }

    /**
     * Returns a list of feed items from facebook for a given owner.
     * @param owner The owner object for which the feed items are to be retrieved.
     * @return Returns a list of feed items from facebook for a given owner.
     */
    public List<FeedItem> getFeedItems(Owner owner) {
        FacebookClient facebookClient = new DefaultFacebookClient(owner.getOauthToken(), Version.VERSION_2_1);
        return getFeedItems(facebookClient, owner);
    }

    /**
     * Returns a list of feed items from facebook for a given owner.
     *
     * @param facebookClient The client object used to retrieve data from facebook
     * @param owner The owner object for which the feed items are to be retrieved.
     * @return Returns a list of feed items from facebook for a given owner.
     */
    List<FeedItem> getFeedItems(FacebookClient facebookClient, Owner owner) {
        Connection<Post> feeds = facebookClient.fetchConnection("me/feed", Post.class);
        List<FeedItem> feedItemList = new ArrayList<FeedItem>();

        logger.info("Found {} feeds.", feeds.getData().size());

        for(int i = 0; i < feeds.getData().size(); i++) {
            Post facebookFeedItem = feeds.getData().get(i);
            //logger.info("Found {}", facebookFeedItem);
            FeedItem feedItem = createFeedItem(facebookFeedItem, FeedItem.FeedType.FEED);
            feedItemList.add(feedItem);
        }

        List<Friend> friendList = getFriendsFromDB();
        logger.info("Checking {} friends feeds.", friendList.size());
        for(int i = 0; i < friendList.size(); i++) {
            Connection<Post> feedsOfFriend = facebookClient.fetchConnection(friendList.get(i).getFacebookID() + "/feed", Post.class);
            logger.info("Found {} feeds of {}.", feeds.getData().size(), friendList.get(i).getFacebookID());
            for (int j = 0; j < feedsOfFriend.getData().size(); j++) {
                Post feedOfFriend = feedsOfFriend.getData().get(j);
                //if(String.valueOf(friendList.get(i).getFacebookID()).equals(feedOfFriend.getFrom().getId())) {
                FeedItem feedItem = createFeedItem(feedOfFriend, FeedItem.FeedType.FEED);
                feedItemList.add(feedItem);
                //}
            }
        }

        return feedItemList;
    }

    List<Friend> getFriendsFromDB() {
        FriendDAO friendDAO = DAOFactory.getFriendDAO();
        return friendDAO.findAll();
    }

    /**
     * Creates a FeedItem from a Post.
     * @param post The Post from which the data is to be extracted
     * @param feedType The feed item type
     * @return The created FeedItem object
     */
    private FeedItem createFeedItem(Post post, FeedItem.FeedType feedType) {
        FeedItem feedItem = new FeedItem();
        feedItem.setContentID(extractContentID(post));
        feedItem.setFeedType(feedType);
        feedItem.setFeedItemID(post.getId());
        feedItem.setTime(post.getCreatedTime());
        feedItem.setType(post.getType());
        feedItem.setUserID(post.getFrom().getId());
        return feedItem;
    }

    /**
     * Extracts the relevant feed items from the feed item list provided and returns it in a Set.
     *
     * @param feedItemList A List of FeedItems from which the relevant ids shall be extracted.
     * @return Returns the relevant feed items from the feed item list provided in a Set.
     */
    private Set<Long> createContentIDSet(List<FeedItem> feedItemList) {
        Set<Long> contentIDSet = new HashSet<Long>();
        for(FeedItem item: feedItemList) {
            if(item.getContentID() != -1) {
                contentIDSet.add(item.getContentID());
            }
        }
        return contentIDSet;
    }

    /**
     * Extracts the content id from a given Post object.
     *
     * @param post The post object from which the id is to be extracted
     * @return Returns the content id in a given Post object.
     */
    private long extractContentID(Post post) {
        long contentID = -1;
        if(post.getLink() != null) {
            contentID = extractContentID(post.getLink());
        }
        if(contentID == -1 && post.getDescription() != null) {
            contentID = extractContentID(post.getDescription());
        }
        return contentID;
    }

    /**
     * Extracts the content ID from a given String
     *
     * @param string The string from which the content ID is to be extracted.
     * @return The content ID extracted from a given String.
     */
    private long extractContentID(String string) {
        if(string.contains("vimeo.com")) {
            String[] splitURL = string.split("/");
            String contentID = splitURL[splitURL.length - 1];
            contentID = contentID.split("\\?")[0];
            return Long.parseLong(contentID);
        }
        //-1 is for not a vimeo video
        return -1;
    }

    /**
     * Returns video meta information for a given content ID
     *
     * @param contentID The content ID for which video information is to be retrieved
     * @return Returns video meta information for a given content ID
     */
    VideoInfo getVideoInfo(long contentID) {
        JSONObject response = vimeoRequest(contentID);
        VideoInfo videoInfo = new VideoInfo();

        videoInfo.setContentID(contentID);
        List<String> namesList = getNamesList(response);
        if(namesList.contains("upload_date")) {
            videoInfo.setPublishDate(toDate(response.get("upload_date")));
        } else {
            videoInfo.setPublishDate(null);
        }
        if(namesList.contains("stats_number_of_plays")) {
            videoInfo.setViewsNumber(toLong(response.get("stats_number_of_plays")));
        } else {
            videoInfo.setViewsNumber(-1);
        }

        String title = null;
        if(namesList.contains("title")) {
            title = (String) response.get("title");
        }

        logger.info("Vimeo video \"{}\" was posted in Facebook News Feed.", title != null ? title : contentID);

        return videoInfo;
    }

    /**
     * Extracts the element names of the JSONObject and places them in a List
     *
     * @param response The response object from which the element names are to be extracted
     * @return The element names of the JSONObject in a List
     */
    private List<String> getNamesList(JSONObject response) {
        JSONArray names = response.names();
        List<String> namesList = new ArrayList<String>();
        if (response != null) {
            for (int i = 0; i < names.length(); i++) {
                namesList.add(names.getString(i));
            }
        }
        return namesList;
    }

    /**
     * Convenience method that casts an Object to an Integer.
     *
     * @param stats_number_of_plays The object to be casted.
     * @return A long value representing the casted object.
     */
    private long toLong(Object stats_number_of_plays) {

        return (Integer) stats_number_of_plays;
    }

    /**
     * Returns the upload date of a given object.
     *
     * @param upload_date The upload date object.
     * @return The upload date of the given object.
     */
    private Date toDate(Object upload_date) {
        String sourceDate = (String) upload_date;
        sourceDate = sourceDate.replaceAll("-", ":");
        sourceDate = sourceDate.replaceAll(" ", ":");
        String[] splitDate = sourceDate.split(":");

        int year = Integer.parseInt(splitDate[0]);
        int month = Integer.parseInt(splitDate[1]);
        int day = Integer.parseInt(splitDate[2]);
        int hour = Integer.parseInt(splitDate[3]);
        int minute = Integer.parseInt(splitDate[4]);
        int second = Integer.parseInt(splitDate[5]);

        LocalDateTime dateTime = new LocalDateTime(year, month, day, hour, minute, second);
        return dateTime.toDate();
    }

    /**
     * Issues a request to vimeo and converts the result to a suitable JSONObject.
     *
     * @param contentID The content ID for which metadata is to be retrieved.
     * @return The JSONObject created from the response of the request.
     */
    private JSONObject vimeoRequest(long contentID) {
        String responseAsString = null;
        CloseableHttpResponse response = null;
        ByteArrayOutputStream out = null;
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            String url = createURL(contentID);
            logger.debug("Sending request to " + url);
            HttpGet httpGet = new HttpGet(url);
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            out = new ByteArrayOutputStream();
            entity.writeTo(out);
            responseAsString = out.toString("UTF-8");
            out.close();

        } catch (IOException e) {
            logger.error(e.getMessage(), e.getStackTrace());
        } finally {
            if(response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e.getStackTrace());
                }
            }
            if(out != null) {
                try {
                    out.close();
                } catch(IOException e) {
                    logger.error(e.getMessage(), e.getStackTrace());
                }
            }

        }
        if(responseAsString == null) {
            return toJSON("{}");
        } else {
            responseAsString = responseAsString.substring(1, responseAsString.length() - 1);
            logger.debug("Converting response to JSON...");
            JSONObject JSONResponse = toJSON(responseAsString);
            logger.debug("Done.");
            return JSONResponse;
        }
    }

    /**
     * Converts a String to a JSONObject.
     *
     * @param responseAsString The string to be converted to a JSONObject.
     * @return The JSONObject created from a String.
     */
    private JSONObject toJSON(String responseAsString) {
        return new JSONObject(responseAsString);
    }

    /**
     * Creates the vimeo URL from a given content ID that is used to retrieve metainformation.
     *
     * @param contentID The content ID for which meatadata is to be retrieved.
     * @return Returns the URL for which metadata is to be retrieved.
     */
    private String createURL(long contentID) {
        return "http://vimeo.com/api/v2/video/" + contentID + ".json";
    }

    /**
     * Stores posts of friends in the database.
     *
     * @param friendPostList The posts of friends to be stored
     */
    private void storeFriendPosts(List<FeedItem> friendPostList) {
        logger.info("Storing friend posts in database...");
        FeedItemDAO feedItemDAO = DAOFactory.getFeedItemDAO();
        try {
            feedItemDAO.insertAll(friendPostList.iterator());
        } catch (Exception e) {
            logger.error("Error while inserting friend posts");
            logger.error(e.getMessage(), e.getStackTrace());
        }
        logger.info("Done.");
    }

    /**
     * Stores friends in the database.
     *
     * @param friendList List containint the friends to be stored.
     */
    private void storeFriends(List<Friend> friendList) {
        logger.info("Storing friends in database...");
        FriendDAO friendDAO = DAOFactory.getFriendDAO();
        try {
            friendDAO.insertAll(friendList.iterator());
        } catch (Exception e) {
            logger.error("Error while inserting friends");
            logger.error(e.getMessage(), e);
        }
        logger.info("Done.");
    }

    /**
     * Creates trusted users from List and stores them in the database
     *
     * @param trustedUserList The friend list to be stored as trusted users.
     */
    private void storeTrustedUsers(List<TrustedUser> trustedUserList) {
        logger.info("Storing trusted users in database...");
        TrustedUserDAO trustedUserDAO = DAOFactory.getTrustedUserDAO();

        try {
            trustedUserDAO.insertAll(trustedUserList.iterator());
        } catch (Exception e) {
            logger.error("Error while inserting trusted users");
            logger.error(e.getMessage(), e);
        }
        logger.info("Done.");
    }

    /**
     * Stores video meta information in the database.
     *
     * @param videoInfoList List containing the video metadata.
     */
    private void storeVideoInfos(List<VideoInfo> videoInfoList) {
        logger.info("Storing video info in database...");
        VideoInfoDAO videoInfoDAO = DAOFactory.getVideoInfoDAO();
        videoInfoDAO.insertAll(videoInfoList.iterator());
        logger.info("Done.");
    }

    /**
     * Returns video metadata for a given Set of contentIDs
     *
     * @param contentIDSet Set of content IDs for which metadata is to be stored.
     * @return Returns video metadata for a given Set of contentIDs
     */
    private List<VideoInfo> retrieveVideoInfo(Set<Long> contentIDSet) {
        List<VideoInfo> videoInfoList = new ArrayList<VideoInfo>();
        for (long contentID : contentIDSet) {
            VideoInfo videoInfo = getVideoInfo(contentID);
            videoInfoList.add(videoInfo);
        }
        return videoInfoList;
    }

    /**
     * Stores feed items in the database.
     *
     * @param feedItemList List of feed items to be stored in the database.
     */
    private void storeFeedItems(List<FeedItem> feedItemList) {
        logger.info("Storing " + feedItemList.size() + " feed items in database...");
        FeedItemDAO feedItemDAO = DAOFactory.getFeedItemDAO();
        try {
            feedItemDAO.insertAll(feedItemList.iterator());
        } catch (Exception e) {
            logger.error("Error while inserting feed items");
            logger.error(e.getMessage(), e);
        }
        logger.info("Done.");
    }
}