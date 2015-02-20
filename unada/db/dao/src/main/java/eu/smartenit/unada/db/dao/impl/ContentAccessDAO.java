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

import java.util.Properties;
import java.util.List;

import org.skife.jdbi.v2.DBI;

import eu.smartenit.unada.db.dao.AbstractContentAccessDAO;
import eu.smartenit.unada.db.dao.util.Constants;
import eu.smartenit.unada.db.dto.ContentAccess;

public class ContentAccessDAO {
	final AbstractContentAccessDAO dao;

	public ContentAccessDAO() {
		Properties connectionProperties = new Properties();
		DBI dbi = new DBI(Constants.DBI_URL, connectionProperties);
		dao = dbi.onDemand(AbstractContentAccessDAO.class);
	}

	public void createTable() {
		dao.createTable();
	}

	public void deleteTable() {
		dao.deleteTable();
	}
	
	public synchronized void insert(ContentAccess c) {
		dao.insert(c);
	}

	public synchronized List<ContentAccess> findAll() {
		return dao.findAll();
	}
	
	public synchronized List<ContentAccess> findByContentID(long contentID) {
		return dao.findByContentID(contentID);
	}
	
	public synchronized ContentAccess findLatestByContentIDFacebookID(long contentID, String facebookID) {
		return dao.findLatestByContentIDFacebookID(contentID, facebookID);
	}

    public synchronized ContentAccess findLatestByContentID(long contentID) {
        return dao.findLatestByContentID(contentID);
    }
	
	public synchronized void deleteAll() {
		dao.deleteAll();
	}
	
	public synchronized void deleteByContentID(long contentID) {
		dao.deleteByContentID(contentID);
	}

}
