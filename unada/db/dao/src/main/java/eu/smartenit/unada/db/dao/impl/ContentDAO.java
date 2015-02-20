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
package eu.smartenit.unada.db.dao.impl;

import java.util.*;

import eu.smartenit.unada.db.dto.ContentAccess;
import org.skife.jdbi.v2.DBI;

import eu.smartenit.unada.db.dao.AbstractContentDAO;
import eu.smartenit.unada.db.dao.util.Constants;
import eu.smartenit.unada.db.dto.Content;

public class ContentDAO {
	final AbstractContentDAO dao;

	/**
	 * The constructor.
	 */
	public ContentDAO() {
		Properties connectionProperties = new Properties();
		DBI dbi = new DBI(Constants.DBI_URL, connectionProperties);
		dao = dbi.onDemand(AbstractContentDAO.class);
	}

	public void createTable() {
		dao.createTable();
	}

	public void deleteTable() {
		dao.deleteTable();
	}

	public synchronized void insert(Content c) throws Exception {
		dao.insert(c);
	}

	public synchronized void insertAll(Iterator<Content> contents) {
		dao.insertAll(contents);
	}

	public synchronized List<Content> findAll() {
		return dao.findAll();
	}

	public synchronized void update(Content c) {
		dao.update(c);
	}

	public synchronized void delete(long id) {
		dao.delete(id);
	}

	public synchronized void deleteAll() {
		dao.deleteAll();
	}

	public synchronized Content findById(long id) {
		return dao.findById(id);
	}

	public synchronized Content findByPath(String path) {
		return dao.findByPath(path);
	}
	
	public synchronized List<Content> findByCacheType(String cachetype) {
		return dao.findByCacheType(cachetype);
	}
	
	public synchronized List<Content> findAllNotPrefetched() {
		return dao.findAllNotPrefetched();
	}

    public synchronized List<Content> findAllWithAccesses() {
        List<ContentAccessJoinRow> contentAccessJoinRows = dao.findAllWithAccesses();
        return joinRowsToContentList(contentAccessJoinRows);
    }
	
	public synchronized long findTotalSize() {
		return dao.findTotalSize();
	}
	
	public synchronized List<Content> findAllOutDated(long cacheThreshold) {
		return dao.findAllOutDated(cacheThreshold);
	}
	
	public synchronized List<Long> findAllIDs() {
		return dao.findAllIDs();
	}

    public synchronized void deleteBatch(Iterator<Content> contents) {
        dao.deleteBatch(contents);
    }

    public synchronized boolean deleteNotAccessedContent(Content content, long threshold) {
        return dao.deleteNotAccessedContent(content, threshold) > 0;
    }

    private List<Content> joinRowsToContentList(List<ContentAccessJoinRow> joinRows) {
        List<Content> contentList = new ArrayList<Content>();
        for (ContentAccessJoinRow join : joinRows) {
            Content c = new Content();
            c.setContentID(join.getContentid());
            c.setCacheDate(new Date(join.getCachedate()));
            c.setPrefetchedVimeo(join.isPrefetchedvimeo());
            c.setPrefetched(join.isPrefetched());
            c.setDownloaded(join.isDownloaded());
            c.setPath(join.getPath());
            c.setSize(join.getSize());
            c.setUrl(join.getUrl());

            ContentAccess ca = null;
            if (join.getTimestamp() != 0) {
                ca = new ContentAccess();
                ca.setTimeStamp(new Date(join.getTimestamp()));
            }

            if (contentList.indexOf(c) != -1) {
                if (ca != null)
                    contentList.get(contentList.indexOf(c)).getAccessList().add(ca);
            }
            else {
                if (ca != null)
                    c.getAccessList().add(ca);
                contentList.add(c);
            }
        }
        return contentList;
    }

    public static class ContentAccessJoinRow {

        public ContentAccessJoinRow () {

        }

        public long contentid;
        public long size;
        public String url;
        public String path;
        public long cachedate;
        public boolean downloaded;
        public boolean prefetched;
        public boolean prefetchedvimeo;
        public long timestamp;

        public long getContentid() {
            return contentid;
        }

        public void setContentid(long contentid) {
            this.contentid = contentid;
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

        public long getCachedate() {
            return cachedate;
        }

        public void setCachedate(long cachedate) {
            this.cachedate = cachedate;
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

        public boolean isPrefetchedvimeo() {
            return prefetchedvimeo;
        }

        public void setPrefetchedvimeo(boolean prefetchedvimeo) {
            this.prefetchedvimeo = prefetchedvimeo;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ContentAccessJoinRow that = (ContentAccessJoinRow) o;

            if (cachedate != that.cachedate) return false;
            if (contentid != that.contentid) return false;
            if (downloaded != that.downloaded) return false;
            if (prefetched != that.prefetched) return false;
            if (prefetchedvimeo != that.prefetchedvimeo) return false;
            if (size != that.size) return false;
            if (timestamp != that.timestamp) return false;
            if (path != null ? !path.equals(that.path) : that.path != null) return false;
            if (url != null ? !url.equals(that.url) : that.url != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = (int) (contentid ^ (contentid >>> 32));
            result = 31 * result + (int) (size ^ (size >>> 32));
            result = 31 * result + (url != null ? url.hashCode() : 0);
            result = 31 * result + (path != null ? path.hashCode() : 0);
            result = 31 * result + (int) (cachedate ^ (cachedate >>> 32));
            result = 31 * result + (downloaded ? 1 : 0);
            result = 31 * result + (prefetched ? 1 : 0);
            result = 31 * result + (prefetchedvimeo ? 1 : 0);
            result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
            return result;
        }
    }
}
