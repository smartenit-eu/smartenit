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
package eu.smartenit.unada.db.dao.util;

import eu.smartenit.unada.db.dao.impl.*;


public class DAOFactory {
	
	private static CacheDAO cacheDAO = new CacheDAO();

	private static ContentAccessDAO contentAccessDAO = new ContentAccessDAO();

	private static ContentDAO contentDAO = new ContentDAO();

    private static FeedItemDAO feedItemDAO = new FeedItemDAO();
    
    private static FriendDAO friendDAO = new FriendDAO();

    private static OwnerDAO ownerDAO = new OwnerDAO();
    
    private static SocialPredictionParametersDAO socialPredictionParametersDAO = new SocialPredictionParametersDAO();
    
    private static SocialScoresDAO socialScoresDAO = new SocialScoresDAO();

    private static TrustedUserDAO trustedUserDAO = new TrustedUserDAO();
    
    private static UNaDaConfigurationDAO uNaDaConfigurationDAO = new UNaDaConfigurationDAO();
    
    private static VideoInfoDAO videoInfoDAO = new VideoInfoDAO();

	public static CacheDAO getCacheDAO() {
		return cacheDAO;
	}

	public static void setCacheDAO(CacheDAO cacheDAO) {
		DAOFactory.cacheDAO = cacheDAO;
	}

	public static ContentAccessDAO getContentAccessDAO() {
		return contentAccessDAO;
	}

	public static void setContentAccessDAO(ContentAccessDAO contentAccessDAO) {
		DAOFactory.contentAccessDAO = contentAccessDAO;
	}

	public static ContentDAO getContentDAO() {
		return contentDAO;
	}

	public static void setContentDAO(ContentDAO contentDAO) {
		DAOFactory.contentDAO = contentDAO;
	}

	public static FeedItemDAO getFeedItemDAO() {
		return feedItemDAO;
	}

	public static void setFeedItemDAO(FeedItemDAO feedItemDAO) {
		DAOFactory.feedItemDAO = feedItemDAO;
	}

	public static FriendDAO getFriendDAO() {
		return friendDAO;
	}

	public static void setFriendDAO(FriendDAO friendDAO) {
		DAOFactory.friendDAO = friendDAO;
	}

	public static OwnerDAO getOwnerDAO() {
		return ownerDAO;
	}

	public static void setOwnerDAO(OwnerDAO ownerDAO) {
		DAOFactory.ownerDAO = ownerDAO;
	}

	public static SocialPredictionParametersDAO getSocialPredictionParametersDAO() {
		return socialPredictionParametersDAO;
	}

	public static void setSocialPredictionParametersDAO(
			SocialPredictionParametersDAO socialPredictionParametersDAO) {
		DAOFactory.socialPredictionParametersDAO = socialPredictionParametersDAO;
	}

	public static TrustedUserDAO getTrustedUserDAO() {
		return trustedUserDAO;
	}

	public static void setTrustedUserDAO(TrustedUserDAO trustedUserDAO) {
		DAOFactory.trustedUserDAO = trustedUserDAO;
	}

	public static UNaDaConfigurationDAO getuNaDaConfigurationDAO() {
		return uNaDaConfigurationDAO;
	}

	public static void setuNaDaConfigurationDAO(
			UNaDaConfigurationDAO uNaDaConfigurationDAO) {
		DAOFactory.uNaDaConfigurationDAO = uNaDaConfigurationDAO;
	}

	public static VideoInfoDAO getVideoInfoDAO() {
		return videoInfoDAO;
	}

	public static void setVideoInfoDAO(VideoInfoDAO videoInfoDAO) {
		DAOFactory.videoInfoDAO = videoInfoDAO;
	}

    public static SocialScoresDAO getSocialScoresDAO() {
        return socialScoresDAO;
    }

    public static void setSocialScoresDAO(SocialScoresDAO socialScoresDAO) {
        DAOFactory.socialScoresDAO = socialScoresDAO;
    }
}
