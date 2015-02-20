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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The Content class.
 * 
 * @authors George Petropoulos
 * @version 2.1
 * 
 */
public class Content implements Serializable, Comparable<Content> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2482985205766366002L;

	public Content() {
		this.cacheDate = new Date();
		this.videoInfo = new VideoInfo();
		this.accessList = new ArrayList<ContentAccess>();
		this.category = ContentCategory.comedy;
	}
	
	private long contentID;
	private long size;
	private String url;
	private String path;
	private String quality;
	private List<ContentAccess> accessList;
	private String cacheType;
	private Date cacheDate;
	private double cacheScore;
	private int hopCount = Integer.MAX_VALUE;
	private ContentCategory category;
	private VideoInfo videoInfo;
	private boolean downloaded;
	private boolean prefetched;
    private boolean prefetchedVimeo;
	
	public enum ContentCategory {
		sports, comedy
	}

	public long getContentID() {
		return contentID;
	}

	public void setContentID(long contentID) {
		this.contentID = contentID;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public List<ContentAccess> getAccessList() {
		return accessList;
	}

	public void setAccessList(List<ContentAccess> accessList) {
		this.accessList = accessList;
	}

	public String getCacheType() {
		return cacheType;
	}

	public void setCacheType(String cacheType) {
		this.cacheType = cacheType;
	}

	public Date getCacheDate() {
		return cacheDate;
	}

	public void setCacheDate(Date cacheDate) {
		this.cacheDate = cacheDate;
	}

	public double getCacheScore() {
		return cacheScore;
	}

	public void setCacheScore(double cacheScore) {
		this.cacheScore = cacheScore;
	}
	
	public int getHopCount() {
		return hopCount;
	}

	public void setHopCount(int hopCount) {
		this.hopCount = hopCount;
	}
	
	public boolean updateHopCount(int hops){
		boolean shorter = this.getHopCount() > hops;
		if(shorter){
			this.setHopCount(hops);
		}
		return shorter;
	}

	public ContentCategory getCategory() {
		return category;
	}

	public void setCategory(ContentCategory category) {
		this.category = category;
	}

	public VideoInfo getVideoInfo() {
		return videoInfo;
	}

	public void setVideoInfo(VideoInfo videoInfo) {
		this.videoInfo = videoInfo;
	}

	public boolean isDownloaded() {
		return downloaded;
	}

	public void setDownloaded(boolean downloaded) {
		this.downloaded = downloaded;
	}

	public boolean isPrefetched() {
		return prefetched;
	}

	public void setPrefetched(boolean prefetched) {
		this.prefetched = prefetched;
	}

    public boolean isPrefetchedVimeo() {
        return prefetchedVimeo;
    }

    public void setPrefetchedVimeo(boolean prefetchedVimeo) {
        this.prefetchedVimeo = prefetchedVimeo;
    }

    @Override
    public String toString() {
        return "Content{" +
                "contentID=" + contentID +
                ", size=" + size +
                ", url='" + url + '\'' +
                ", path='" + path + '\'' +
                ", quality='" + quality + '\'' +
                ", accessList=" + accessList +
                ", cacheType='" + cacheType + '\'' +
                ", cacheDate=" + cacheDate +
                ", cacheScore=" + cacheScore +
                ", hopCount=" + hopCount +
                ", category=" + category +
                ", videoInfo=" + videoInfo +
                ", downloaded=" + downloaded +
                ", prefetched=" + prefetched +
                ", prefetchedVimeo=" + prefetchedVimeo +
                '}';
    }

    public int compareTo(Content o) {
		if(this.cacheScore < o.getCacheScore())
			return -1;
		if(this.cacheScore > o.getCacheScore())
			return 1;
		
		return 0;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Content content = (Content) o;

        if (Double.compare(content.cacheScore, cacheScore) != 0) return false;
        if (contentID != content.contentID) return false;
        if (downloaded != content.downloaded) return false;
        if (prefetched != content.prefetched) return false;
        if (prefetchedVimeo != content.prefetchedVimeo) return false;
        if (size != content.size) return false;
        if (cacheDate != null ? !cacheDate.equals(content.cacheDate) : content.cacheDate != null) return false;
        if (path != null ? !path.equals(content.path) : content.path != null) return false;
        if (url != null ? !url.equals(content.url) : content.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (contentID ^ (contentID >>> 32));
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (quality != null ? quality.hashCode() : 0);
        result = 31 * result + (cacheType != null ? cacheType.hashCode() : 0);
        result = 31 * result + (cacheDate != null ? cacheDate.hashCode() : 0);
        temp = Double.doubleToLongBits(cacheScore);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + hopCount;
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (videoInfo != null ? videoInfo.hashCode() : 0);
        result = 31 * result + (downloaded ? 1 : 0);
        result = 31 * result + (prefetched ? 1 : 0);
        result = 31 * result + (prefetchedVimeo ? 1 : 0);
        return result;
    }
}
