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
package eu.smartenit.unada.web.ui;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.FeedItem;
import eu.smartenit.unada.db.dto.Friend;
import eu.smartenit.unada.db.dto.TrustedUser;
import eu.smartenit.unada.db.dto.VideoInfo;

/**
 * The AdministrationBean class. It handles the admin.xhtml page.
 * 
 * @author George Petropoulos
 * @version 2.1
 * 
 */
@ManagedBean
@RequestScoped
public class AdministrationBean {
			
	private List<TrustedUser> trustedList;

    private List<Friend> friendsList;

    private List<VideoInfo> videoInfoList;

    private List<FeedItem> feedItemList;
	
	public List<TrustedUser> getTrustedList() {
		return trustedList;
	}

	public void setTrustedList(List<TrustedUser> trustedList) {
		this.trustedList = trustedList;
	}

    public List<Friend> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(List<Friend> friendsList) {
        this.friendsList = friendsList;
    }

    public List<VideoInfo> getVideoInfoList() {
        return videoInfoList;
    }

    public void setVideoInfoList(List<VideoInfo> videoInfoList) {
        this.videoInfoList = videoInfoList;
    }

    public List<FeedItem> getFeedItemList() {
        return feedItemList;
    }

    public void setFeedItemList(List<FeedItem> feedItemList) {
        this.feedItemList = feedItemList;
    }

    /**
	 * The init() method that initializes the AdministrationBean.
	 * It retrieves the stored trusted users.
	 * 
	 */
	@PostConstruct
	public void init() {
		trustedList = DAOFactory.getTrustedUserDAO().findAll();
        friendsList = DAOFactory.getFriendDAO().findAll();
        videoInfoList = DAOFactory.getVideoInfoDAO().findAll();
        feedItemList = DAOFactory.getFeedItemDAO().findAll();
	}
	
	/**
	 * The method that deletes a trusted user from the database.
	 * 
	 * @param t The trusted user to be deleted.
	 * 
	 */
	public void deleteTrustedUser(TrustedUser t) {
		DAOFactory.getTrustedUserDAO().delete(t.getFacebookID());
		trustedList = DAOFactory.getTrustedUserDAO().findAll();
	}

    /**
     * The method that deletes a video info from the database.
     *
     * @param v The video info to be deleted.
     *
     */
    public void deleteVideoInfo(VideoInfo v) {
        DAOFactory.getVideoInfoDAO().delete(v.getContentID());
        videoInfoList = DAOFactory.getVideoInfoDAO().findAll();
    }

    /**
     * The method that deletes all video infos from the database.
     *
     */
    public void deleteVideoInfos() {
        DAOFactory.getVideoInfoDAO().deleteAll();
        videoInfoList = DAOFactory.getVideoInfoDAO().findAll();
    }

    /**
     * The method that deletes all feed items from the database.
     *
     */
    public void deleteFeedItems() {
        DAOFactory.getFeedItemDAO().deleteAll();
        feedItemList = DAOFactory.getFeedItemDAO().findAll();
    }

    /**
     * The method that deletes all friends from the database.
     *
     */
    public void deleteFriends() {
        DAOFactory.getFriendDAO().deleteAll();
        friendsList = DAOFactory.getFriendDAO().findAll();
    }
	
}