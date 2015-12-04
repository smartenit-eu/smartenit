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
 * The VideoInfo class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public class VideoInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4268223521751870215L;


	public VideoInfo() {
		this.publishDate = new Date();
	}

	public VideoInfo(long contentID, Date publishDate, long viewsNumber) {
		super();
		this.contentID = contentID;
		this.publishDate = publishDate;
		this.viewsNumber = viewsNumber;
	}

	private long contentID;
	private Date publishDate;
	private long viewsNumber;

	
	public long getContentID() {
		return contentID;
	}

	public void setContentID(long contentID) {
		this.contentID = contentID;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public long getViewsNumber() {
		return viewsNumber;
	}

	public void setViewsNumber(long viewsNumber) {
		this.viewsNumber = viewsNumber;
	}

	@Override
	public String toString() {
		return "VideoInfo [contentID=" + contentID + ", publishDate="
				+ publishDate + ", viewsNumber=" + viewsNumber + "]";
	}

}
