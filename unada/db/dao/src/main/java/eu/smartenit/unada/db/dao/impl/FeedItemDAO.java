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

import java.util.Iterator;
import java.util.Properties;
import java.util.List;

import org.skife.jdbi.v2.DBI;

import eu.smartenit.unada.db.dao.AbstractFeedItemDAO;
import eu.smartenit.unada.db.dao.util.Constants;
import eu.smartenit.unada.db.dto.FeedItem;

/**
 * The FeedItemDAO class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public class FeedItemDAO {
	final AbstractFeedItemDAO dao;

	/**
	 * The constructor.
	 */
	public FeedItemDAO() {
		Properties connectionProperties = new Properties();
		DBI dbi = new DBI(Constants.DBI_URL, connectionProperties);
		dao = dbi.onDemand(AbstractFeedItemDAO.class);
	}

	/**
	 * The method that creates the FeedItem table.
	 */
	public void createTable() {
		dao.createTable();
	}

	/**
	 * The method that deletes the FeedItem table.
	 */
	public void deleteTable() {
		dao.deleteTable();
	}

	/**
	 * The method that inserts a FeedItem entry.
	 */
	public void insert(FeedItem f) throws Exception {
		dao.insert(f);
	}
	
	/**
	 * The method that inserts a batch of FeedItem entries.
	 */
	public synchronized void insertAll(Iterator<FeedItem> feedItems) throws Exception {
		dao.insertAll(feedItems);
	}
	
	/**
	 * The method that finds a FeedItem entry by its identifier.
	 */
	public FeedItem findById(String feedItemID) {
		return dao.findById(feedItemID);

	}

	/**
	 * The method that finds all FeedItem entries.
	 */
	public List<FeedItem> findAll() {
		return dao.findAll();
	}

    /**
     * The method that finds all FeedItem entries which have the content id.
     *
     * @param contentID The content identifier.
     *
     */
    public List<FeedItem> findAllByContentID(long contentID) {
        return dao.findAllByContentID(contentID);
    }
	

	/**
	 * The method that updates a FeedItem entry.
	 */
	public void update(FeedItem f) {
		dao.update(f);
	}

	/**
	 * The method that deletes a FeedItem entry by its identifier.
	 */
	public void delete(String feedItemID) {
		dao.delete(feedItemID);
	}

	/**
	 * The method that deletes all FeedItem entries.
	 */
	public void deleteAll() {
		dao.deleteAll();
	}
}
