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
package eu.smartenit.unada.sa;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import eu.smartenit.unada.db.dao.impl.*;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.*;
import eu.smartenit.unada.om.IOverlayManager;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Class which holds the SocialAnalyzer algorithm
 */
public final class SocialAnalyzer {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(SocialAnalyzer.class);

    /**
     * OverlayManager instance for retrieving Unada information
     */
    private final IOverlayManager overlayManager;

    /**
     * Creates a SocialAnalyzer instance
     *
     * @param overlayManager An OverlayManager instance for retrieving Unada information
     */
    public SocialAnalyzer(IOverlayManager overlayManager) {
        this.overlayManager = overlayManager;
    }

    /**
     * Invokes the prediction algorithm and returns a List of Content items, sorted by their score (ascending)
     *
     * @return Returns a List of Content items, sorted by their score (ascending)
     */
    public List<Content> predicting() {

        List<Content> sortedContent = new ArrayList<Content>();

        List<FeedItem> feedItemList = getFeedItems();
        Set<Long> contentIDSet = createContentIDSet(feedItemList);
        Map<Long, FeedItem> feedItemMap = crateFeedItemMap(feedItemList);

        for(long contentID: contentIDSet) {
            logger.info("Calculating prediction score for content ID: " + contentID);
            VideoInfo videoInfo = getVideoInfo(contentID);
            if(videoInfo == null) {
                logger.warn("Ignoring contentID " + contentID + " (VideoInfo) was null");
                continue;
            }

            //age
            logger.debug("Calculating age for content ID " + contentID);
            double alpha = calculateAge(videoInfo.getPublishDate(), feedItemMap.get(contentID).getTime());
            if(alpha == -1) {
                logger.warn("Age for content ID " + contentID + " could not be calculated. Setting age component to 0.");
            }
            logger.debug("Alpha: " + alpha);

            //distance
            logger.debug("Calculating distance for content ID " + contentID);
            double delta = getDistance(contentID);
            if(delta == -1) {
                logger.warn("Distance for content ID " + contentID + " could not be calculated. Setting distance component to 0.");
            }
            logger.debug("Delta: " + delta);

            //history
            logger.debug("Obtaining number of accesses to the database for content ID " + contentID);
            long eta = getNumberOfAccesses(contentID);
            logger.debug("Eta: " + eta);

            //popularity
            logger.debug("Obtaining number of views from database for content ID " + contentID);
            long phi = videoInfo.getViewsNumber();
            if(phi == -1) {
                logger.warn("Popularity for content ID " + contentID + " could not be calculated. Setting popularity component to 0.");
            }
            logger.debug("Phi: " + phi);

            //social
            logger.debug("Obtaining friends from database for current user");
            List<Friend> friendList = getFriends();
            if(logger.isDebugEnabled()) {
                for(Friend friend: friendList) {
                    logger.debug("Friend: " + friend.getFacebookID() + " " + friend.getFacebookName());
                }
            }
            logger.debug("Number of friends: " + friendList.size());

            logger.debug("Calculating social score for content ID " + contentID);
            double gamma = getSocial(friendList, contentID);
            logger.debug("Gamma: " + gamma);

            logger.debug("Calculating final score for content ID " + contentID);
            double score = calculateScore(alpha, delta, eta, phi, gamma);

            SocialScores s = new SocialScores(contentID, calcFAge(alpha), calcFDistance(delta),
                    calcFHistory(eta), calcFPouplarity(phi), calcFSocial(gamma));
            try {
                DAOFactory.getSocialScoresDAO().insert(s);
                logger.debug("Inserted score {}", s);
            } catch (Exception e) {
                logger.error("Error inserting social score {}", s);
            }

            Content content = new Content();
            content.setContentID(contentID);
            content.setCacheScore(score);
            sortedContent.add(content);
            logger.info("Done.");
        }

        logger.info("Sorting content by prediction score...");
        Collections.sort(sortedContent, new Comparator<Content>() {
            @Override
            public int compare(Content o1, Content o2) {
                return o1.getCacheScore() < o2.getCacheScore() ? 1 : -1;
            }
        });
        logger.info("Done.");
        return sortedContent;
    }

    /**
     * Retrieves FeedItems from the database
     *
     * @return A List containing FeedItems from the database.
     */
    private List<FeedItem> getFeedItems() {
        FeedItemDAO feedItemDAO = DAOFactory.getFeedItemDAO();
        List<FeedItem> feedItemsFromDB = feedItemDAO.findAll();
        List<FeedItem> feedItems = new ArrayList<FeedItem>();
        for(FeedItem feedItem: feedItemsFromDB) {
            if(FeedItem.FeedType.FEED.equals(feedItem.getFeedType())) {
                feedItems.add(feedItem);
            }
        }
        return feedItems;
    }

    /**
     * Creates a Set of relevant content IDs from a given feed item List.
     *
     * @param feedItemList The List from which the content IDs are to be retrieved.
     * @return Returns a Set of relevant content IDs from a given feed item List.
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
     * Creates a Map for looking up FeedItems by content ID.
     * @param feedItemList The List of FeedItems for which the Map shall be created.
     * @return Returns a Map for looking up FeedItems by content ID.
     */
    private Map<Long, FeedItem> crateFeedItemMap(List<FeedItem> feedItemList) {
        Map<Long, FeedItem> map = new HashMap<Long, FeedItem>();
        for(FeedItem feedItem: feedItemList) {
            if(feedItem.getContentID() != -1) {
                map.put(feedItem.getContentID(), feedItem);
            }
        }
        return map;
    }

    /**
     * Retrieves video information from the database
     *
     * @param contentID The content ID of the file for which video information is to be retrieved.
     * @return Returns video information from the database
     */
    private VideoInfo getVideoInfo(long contentID) {
        VideoInfoDAO videoInfoDAO = DAOFactory.getVideoInfoDAO();
        return videoInfoDAO.findById(contentID);
    }

    /**
     * Calculates the age of a video in weeks.
     *
     * @param publishDate Point in time at which the video was published.
     * @param feedDate Point in time at which the video was posted on the users wall.
     * @return The age of the video in weeks or -1 if it could not be calculated.
     */
    private double calculateAge(Date publishDate, Date feedDate) {
        if(publishDate == null || feedDate == null) {
            return -1;
        } else {
            LocalDateTime feedPublished = new LocalDateTime(feedDate);
            LocalDateTime videoPublished = new LocalDateTime(publishDate);

            double ageInMilliseconds = feedPublished.toDateTime().getMillis() - videoPublished.toDateTime().getMillis();
            return ageInMilliseconds / 1000 / 60 / 60 / 24 / 7;
        }
    }

    /**
     * Calculates the distance of some content identified by content ID from the current Unada.
     * @param contentID The content ID for which video information is to be retrieved.
     * @return Returns the distance of some content identified by content ID from the current Unada.
     */
    private double getDistance(long contentID) {
        UnadaInfo localInfo = overlayManager.getuNaDaInfo();
        double localLatitude = localInfo.getLatitude();
        double localLongitude = localInfo.getLongitude();


        Set<UnadaInfo> closeProviders = overlayManager.getCloseProviders(contentID);
        //If no unada could be found, return -1, i.e. no valid value
        if(closeProviders.isEmpty()) {
            return -1;
        }

        UnadaInfoDistance uid = null;

        for(UnadaInfo unadaInfo: closeProviders) {
            double distance = calculateDistance(localLatitude, localLongitude, unadaInfo.getLatitude(), unadaInfo.getLongitude());
            if(uid == null || distance < uid.getDistance()) {
                uid = new UnadaInfoDistance(unadaInfo, distance);
            }
        }

        if(uid != null) {
            return uid.getDistance();
        } else {
            return -1;
        }
    }

    /**
     * Calculates the distance between two geographical points
     *
     * @param localLatitude Latitude of local unada
     * @param localLongitude Longitude of local unada
     * @param remoteLatitude Latitude of remote unada
     * @param remoteLongitude Longitude of remote unada
     *
     * @return The distance between two geographical points.
     */
    private double calculateDistance(double localLatitude, double localLongitude, double remoteLatitude, double remoteLongitude) {
        LatLng localPoint = new LatLng(localLatitude, localLongitude);
        LatLng remotePoint = new LatLng(remoteLatitude, remoteLongitude);
        return LatLngTool.distance(localPoint, remotePoint, LengthUnit.KILOMETER);
    }

    /**
     * Counts the number of times some contend identified by content ID was accessed.
     * @param contentID The content ID for which access number is to be retrieved.
     * @return Counts the number of times some contend identified by content ID was accessed.
     */
    private long getNumberOfAccesses(long contentID) {
        ContentAccessDAO contentAccessDAO =  DAOFactory.getContentAccessDAO();
        List<ContentAccess> contentAccessList = contentAccessDAO.findByContentID(contentID);

        long numberOfAccesses = 0;
        for(ContentAccess contentAccessFromDB: contentAccessList) {
            if(contentID == contentAccessFromDB.getContentID()) {
                numberOfAccesses++;
            }
        }

        return numberOfAccesses;
    }

    /**
     * Returns the friends of the current user.
     * @return Returns the friends of the current user.
     */
    private List<Friend> getFriends() {
        FriendDAO friendDAO = DAOFactory.getFriendDAO();
        return friendDAO.findAll();
    }

    /**
     * Calculates the social parameter.
     *
     * @param friendList List of friends.
     * @param contentID The content ID for which the parameter shall be calculated.
     * @return The social parameter for a given content ID.
     */
    private double getSocial(List<Friend> friendList, long contentID) {
        Set<String> friendsHoldingFeed = new HashSet<String>();
        FeedItemDAO feedItemDAO = DAOFactory.getFeedItemDAO();
        List<FeedItem> feedItems = feedItemDAO.findAll();

        Set<String> friendIds = new HashSet<>();
        for(Friend friend: friendList) {
            friendIds.add(friend.getFacebookID());
        }
        for(FeedItem feedItem: feedItems) {
            if(friendIds.contains(feedItem.getUserID()) && feedItem.getContentID() == contentID) {
                friendsHoldingFeed.add(feedItem.getUserID());
            }
        }

        return 100 * friendsHoldingFeed.size() / friendList.size();
    }

    /**
     * Calculates the prediction score for a given set of parameters.
     *
     * @param alpha The age in weeks
     * @param delta The distance in km
     * @param eta The number of historical accesses
     * @param phi The popularity of a video
     * @param gamma The social parameter
     * @return The prediction score for a given set of parameters.
     */
    public double calculateScore(double alpha, double delta, double eta, double phi, double gamma) {

        double fAge = calcFAge(alpha);
        double fDistance = calcFDistance(delta);
        double fHistory = calcFHistory(eta);
        double fPopularity = calcFPouplarity(phi);
        double fSocial = calcFSocial(gamma);

        double score = calculateFinalScore(fAge, fDistance, fHistory, fPopularity, fSocial);
        double p = transformToProbability(score);
        logger.debug("score, probability = {}, {}", score, p);
        //boolean predicting = pRetweet >= 0.5;

        return p;
    }

    /**
     * Caluclates the age component.
     *
     * @param alpha The age in weeks.
     * @return Returns the age component or 0 if it could not be calculated.
     */
    private double calcFAge(double alpha) {
        if(alpha == -1) {
            return 0;
        }
        return 4.898 * Math.pow(10, -4) * Math.pow(Math.E, -2.951 * alpha) - 2.961 * Math.pow(10, -7) * alpha + 1.111 * Math.pow(10, -4);
    }

    /**
     * Calculates the distance component.
     *
     * @param delta The distance in km.
     * @return Returns the distance component or 0 if it could not be calculated.
     */
    private double calcFDistance(double delta) {
        if(delta == -1) {
            return 0;
        }
        return 2.309 * Math.pow(10, -3) * Math.pow(Math.E, -0.01 * delta) - 2.45 * Math.pow(10, -9)*delta + 7.92*Math.pow(10, -5);
    }

    /**
     * Calculates the history component.
     *
     * @param eta The number of accesses
     * @return Returns the history component.
     */
    private double calcFHistory(double eta) {
        if(eta < 500) {
            return 0.1377 * Math.log(0.2952 + 0.5 * eta) + 0.1683;
        } else {
            return 0.99;
        }
    }

    /**
     * Calculates the popularity component.
     * @param phi The video popularity
     * @return Returns the popularity component or 0 if it could not be calculated.
     */
    private double calcFPouplarity(double phi) {
        if(phi == -1) {
            return 0;
        }
        return 3.98 * Math.pow(10, -2) * Math.pow(Math.E, -6.66 * Math.pow(10, -8) * phi) + 2.4 * Math.pow(10, -6);
    }

    /**
     * Calculates the social component
     *
     * @param gamma The social parameter
     * @return Returns the social component.
     */
    private double calcFSocial(double gamma) {
        return 1.892 * Math.pow(10, -3) * Math.pow(Math.E, 5.641 * Math.pow(10, -2) * gamma) + 1.511 * Math.pow(10, -2);
    }

    /**
     * Transforms a prediction score to a probability
     *
     * @param score The popularity score
     * @return A probability for a given prediction score.
     */
    private double transformToProbability(double score) {
        return 1 / (1 + Math.pow(Math.E, score));
    }

    /**
     * Calculates the final score from a set of precalculated parameter values.
     *
     * @param fAge The age component
     * @param fDistance The popularity component
     * @param fHistory The history component
     * @param fPopularity The popularity component
     * @param fSocial The social component
     * @return The final score from a set of precalculated parameter values.
     */
    private double calculateFinalScore(
            double fAge,
            double fDistance,
            double fHistory,
            double fPopularity,
            double fSocial) {

        SocialPredictionParametersDAO sppDAO = DAOFactory.getSocialPredictionParametersDAO();
        SocialPredictionParameters parameters = sppDAO.findLast();

        /**
         * Calculation parameters
         */
        double lambda1;
        double lambda2;
        double lambda3;
        double lambda4;
        double lambda5;
        double lambda6;

        //If the values is not set in a database, set default values
        if(parameters == null) {
            logger.warn("Lambda i parameters seem not the be configured. Assuming default values.");
            lambda1 = -3646.8405;
            lambda2 = -348.238;
            lambda3 = -174.6666;
            lambda4 = 32.539;
            lambda5 = -1.0468;
            lambda6 = 1.8463;
            logger.info("Done.");
        } else {
            logger.info("Configuring lambda i...");
            lambda1 = parameters.getLambda1();
            lambda2 = parameters.getLambda2();
            lambda3 = parameters.getLambda3();
            lambda4 = parameters.getLambda4();
            lambda5 = parameters.getLambda5();
            lambda6 = parameters.getLambda6();
            logger.info("Done.");
        }

        return 	  lambda1 * fAge
                + lambda2 * fDistance
                + lambda3 * fHistory
                + lambda4 * fPopularity
                + lambda5 * fSocial
                + lambda6;
    }

    /**
     * Helper class used for sorting Content items by prediction score.
     */
    private class ContentItem implements Comparable<ContentItem> {
        /**
         * The content ID of the Content.
         */
        private final long contentID;

        /**
         * The prediction score.
         */
        private final double score;

        /**
         * Creates a new ContentItem instance.
         * @param contentID content ID
         * @param score prediction score
         */
        private ContentItem(long contentID, double score) {
            this.contentID = contentID;
            this.score = score;
        }

        /**
         * Returns the content ID
         * @return Returns the content ID
         */
        public long getContentID() {
            return contentID;
        }

        /**
         * Returns the prediction score.
         * @return Returns the prediction score.
         */
        public double getScore() {
            return score;
        }

        @Override
        public int compareTo(ContentItem o) {
            if(this.getScore() < o.getScore()) {
                return -1;
            } else if (this.getScore() > o.getScore()) {
                return 1;
            }
            return 0;
        }
    }

    /**
     * Helper class for sorting unadas by distance.
     */
    private class UnadaInfoDistance {
        /**
         * The unada to be considered
         */
        private final UnadaInfo unadaInfo;

        /**
         * The distance in km.
         */
        private final double distance;

        /**
         * Creates a new UnadaInfoDistance instance.
         *
         * @param unadaInfo The unada to be considered
         * @param distance The distance in km.
         */
        private UnadaInfoDistance(UnadaInfo unadaInfo, double distance) {
            this.unadaInfo = unadaInfo;
            this.distance = distance;
        }

        /**
         * Returns the unada in question.
         * @return Returns the unada in question.
         */
        public UnadaInfo getUnadaInfo() {
            return unadaInfo;
        }

        /**
         * Returns the distance in km.
         * @return Returns the distance in km.
         */
        public double getDistance() {
            return distance;
        }
    }

}
