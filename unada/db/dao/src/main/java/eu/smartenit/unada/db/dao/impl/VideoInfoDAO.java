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

import eu.smartenit.unada.db.dao.AbstractVideoInfoDAO;
import eu.smartenit.unada.db.dao.util.Constants;
import eu.smartenit.unada.db.dto.VideoInfo;

public class VideoInfoDAO {
	final AbstractVideoInfoDAO dao;

	public VideoInfoDAO() {
		Properties connectionProperties = new Properties();
		DBI dbi = new DBI(Constants.DBI_URL, connectionProperties);
		dao = dbi.onDemand(AbstractVideoInfoDAO.class);
	}

	public void createTable() {
		dao.createTable();
	}

	public void deleteTable() {
		dao.deleteTable();
	}
	
	public void insert(VideoInfo v) {
		dao.insert(v);
	}
	
	public synchronized void insertAll(Iterator<VideoInfo> vList) {
		dao.insertAll(vList);
	}
	
	public void update(VideoInfo v) {
		dao.update(v);
	}

	public List<VideoInfo> findAll() {
		return dao.findAll();
	}
	
	public VideoInfo findById(long contentID) {
		return dao.findById(contentID);
	}
	
	public void deleteAll() {
		dao.deleteAll();
	}

	public void delete(long contentID) {
		dao.delete(contentID);
	}

}
