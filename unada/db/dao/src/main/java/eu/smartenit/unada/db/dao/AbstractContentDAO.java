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
package eu.smartenit.unada.db.dao;

import java.util.Iterator;
import java.util.List;

import eu.smartenit.unada.db.dao.impl.ContentDAO;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import eu.smartenit.unada.db.dao.binders.BindContent;
import eu.smartenit.unada.db.dao.mappers.ContentMapper;
import eu.smartenit.unada.db.dto.Content;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

/**
 * The abstract ContentDAO class, including all SQL queries.
 * 
 * @authors George Petropoulos
 * @version 2.1
 * 
 */
public abstract class AbstractContentDAO {

	@SqlUpdate("CREATE TABLE IF NOT EXISTS content "
			+ "(contentid BIGINT PRIMARY KEY, " 
			+ "size BIGINT, "
			+ "url VARCHAR(255), " 
			+ "path VARCHAR(255), " 
			+ "quality VARCHAR(255), "
			+ "cachetype VARCHAR(255), " 
			+ "cachedate BIGINT, "
			+ "cachescore REAL, "
			+ "category VARCHAR(255), "
			+ "downloaded BOOLEAN, "
			+ "prefetched BOOLEAN, "
            + "vimeoprefetched BOOLEAN)")
	public abstract void createTable();

	@SqlUpdate("DROP TABLE IF EXISTS content")
	public abstract void deleteTable();

	@SqlUpdate("INSERT INTO content (contentid, size, url, path, "
			+ "quality, cachetype, cachedate, cachescore, "
			+ "category, downloaded, prefetched, vimeoprefetched) "
			+ "VALUES (:c.contentid, :c.size, :c.url, :c.path, "
			+ ":c.quality, :c.cachetype , :c.cachedate , :c.cachescore, "
			+ ":c.category, :c.downloaded, :c.prefetched, :c.vimeoprefetched)")
	public abstract void insert(@BindContent("c") Content cont);
	
	@SqlBatch("INSERT INTO content (contentid, size, url, path, "
			+ "quality, cachetype, cachedate, cachescore, "
			+ "category, downloaded, prefetched, vimeoprefetched) "
			+ "VALUES (:c.contentid, :c.size, :c.url, :c.path, "
			+ ":c.quality, :c.cachetype , :c.cachedate , :c.cachescore, "
			+ ":c.category, :c.downloaded, :c.prefetched, :c.vimeoprefetched)")
	@BatchChunkSize(1000)
	public abstract void insertAll(@BindContent("c") Iterator<Content> contents);

	@SqlUpdate("UPDATE content " 
			+ "SET size = :c.size, " 
			+ "url = :c.url, "
			+ "path = :c.path, " 
			+ "quality = :c.quality, "
			+ "cachetype = :c.cachetype, " 
			+ "cachedate = :c.cachedate, "
			+ "cachescore = :c.cachescore, "
			+ "category = :c.category, "
			+ "downloaded = :c.downloaded, "
			+ "prefetched = :c.prefetched, "
            + "vimeoprefetched = :c.vimeoprefetched "
			+ "WHERE contentid = :c.contentid")
	public abstract void update(@BindContent("c") Content cont);

	@SqlQuery("SELECT * FROM content")
	@Mapper(ContentMapper.class)
	public abstract List<Content> findAll();

	@SqlQuery("SELECT * FROM content WHERE contentid = :contentid")
	@Mapper(ContentMapper.class)
	public abstract Content findById(@Bind("contentid") long id);

	@SqlQuery("SELECT * FROM content WHERE path = :path")
	@Mapper(ContentMapper.class)
	public abstract Content findByPath(@Bind("path") String path);
	
	@SqlQuery("SELECT * FROM content WHERE cachetype = :cachetype")
	@Mapper(ContentMapper.class)
	public abstract List<Content> findByCacheType(@Bind("cachetype") String cachetype);
	
	@SqlQuery("SELECT * FROM content WHERE prefetched = 0 AND downloaded = 1")
	@Mapper(ContentMapper.class)
	public abstract List<Content> findAllNotPrefetched();
	
	@SqlQuery("SELECT contentid FROM content")
	public abstract List<Long> findAllIDs();

    @SqlQuery("SELECT c.contentid AS contentid, c.size AS size, " +
            "c.url AS url, c.path AS path, c.cachedate AS cachedate, " +
            "c.downloaded AS downloaded, c.prefetched AS prefetched, " +
            "c.vimeoprefetched AS prefetchedvimeo, co.timestamp AS timestamp " +
            "FROM content c " +
            "LEFT JOIN contentaccess co " +
            "ON co.contentid = c.contentid")
    @MapResultAsBean
    public abstract List<ContentDAO.ContentAccessJoinRow> findAllWithAccesses();

	@SqlUpdate("DELETE FROM content")
	public abstract void deleteAll();

	@SqlUpdate("DELETE FROM content WHERE contentid = :contentid")
	public abstract void delete(@Bind("contentid") long id);

    @SqlBatch("DELETE FROM content WHERE contentid = :c.contentid")
    @BatchChunkSize(1000)
    public abstract void deleteBatch(@BindContent("c") Iterator<Content> contents);

    @SqlUpdate("DELETE FROM content WHERE cachedate <= :threshold AND contentid NOT IN ("
            + "SELECT cont.contentid FROM content AS cont JOIN contentaccess AS contacc "
            + "ON cont.contentid = contacc.contentid "
            + "WHERE cont.contentid = :c.contentid "
            + "AND contacc.timestamp >= :threshold)")
    public abstract long deleteNotAccessedContent(@BindContent("c") Content content, @Bind("threshold") long threshold);
	
	@SqlQuery("SELECT sum(size) FROM content")
	public abstract long findTotalSize();
	
	@SqlQuery("SELECT * FROM content "
			+ "WHERE downloaded = 1 AND cachedate <= :cachethreshold")
	@Mapper(ContentMapper.class)
	public abstract List<Content> findAllOutDated(@Bind("cachethreshold") long cacheThreshold);

    @SqlQuery("SELECT * FROM content WHERE downloaded = 1")
    @Mapper(ContentMapper.class)
    public abstract List<Content> findAllDownloaded();

}
