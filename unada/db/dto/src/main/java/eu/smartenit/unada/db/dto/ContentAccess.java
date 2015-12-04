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
package eu.smartenit.unada.db.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * The ContentAccess class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public class ContentAccess implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4028349134881457581L;

	public ContentAccess() {
		this.timeStamp = new Date();
	}
	
	private long contentID;
	private Date timeStamp;
	private String facebookID;
	
	public long getContentID() {
		return contentID;
	}
	public void setContentID(long contentID) {
		this.contentID = contentID;
	}
	public Date getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getFacebookID() {
		return facebookID;
	}
	public void setFacebookID(String facebookID) {
		this.facebookID = facebookID;
	}
	
	@Override
	public String toString() {
		return "ContentAccess [contentID=" + contentID + ", timeStamp="
				+ timeStamp + ", facebookID=" + facebookID + "]";
	}
	
	

}
