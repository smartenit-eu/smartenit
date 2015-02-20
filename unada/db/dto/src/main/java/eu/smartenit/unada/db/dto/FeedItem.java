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
package eu.smartenit.unada.db.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * The FeedItem class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public class FeedItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7909394847496020239L;

	public FeedItem() {
		this.time = new Date();
		this.feedType = FeedType.FEED;
	}
	
	private String feedItemID;
	private long contentID;
	private String userID;
	private String type;
	private Date time;
	private FeedType feedType;

	public String getFeedItemID() {
		return feedItemID;
	}
	public void setFeedItemID(String feedItemID) {
		this.feedItemID = feedItemID;
	}
	public long getContentID() {
		return contentID;
	}
	public void setContentID(long contentID) {
		this.contentID = contentID;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public FeedType getFeedType() {
		return feedType;
	}
	public void setFeedType(FeedType feedType) {
		this.feedType = feedType;
	}
	
	@Override
	public String toString() {
		return "FeedItem [feedItemID=" + feedItemID + ", contentID="
				+ contentID + ", userID=" + userID + ", type=" + type
				+ ", time=" + time + ", feedType=" + feedType + "]";
	}
	
	public enum FeedType {FEED, 
		POST_OF_FRIEND }
	
	

}
