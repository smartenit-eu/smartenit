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

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Cache;
import eu.smartenit.unada.db.dto.Content;

/**
 * The CacheBean class. It handles the cache.xhtml page.
 * 
 * @author George Petropoulos
 * @version 2.1
 * 
 */
@ManagedBean
@RequestScoped
public class CacheBean {
	
	private static final Logger logger = LoggerFactory.getLogger(CacheBean.class);
	
	private Cache cache;
	private List<Content> contentList;

	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public List<Content> getContentList() {
		return contentList;
	}

	public void setContentList(List<Content> contentList) {
		this.contentList = contentList;
	}

	/**
	 * The init() method that initializes the CacheBean.
	 * It retrieves the stored caches and contents.
	 * 
	 */
	@PostConstruct
	public void init() {
		
		cache = DAOFactory.getCacheDAO().findLast();
		if (cache == null) {
			cache = new Cache();
		}
		contentList = DAOFactory.getContentDAO().findAllWithAccesses();
	}
	
	
	/**
	 * The method that updates cache parameters to the database.
	 * 
	 * @return The cache.xhtml page.
	 * 
	 */
	public String updateCache() {
		logger.debug("Updating cache " + cache);
		DAOFactory.getCacheDAO().insert(cache);
		return null;
	}

    /**
     * The method that deletes a cached content from the database and the cache.
     *
     * @param c The content to be deleted from cache.
     *
     */
    public void delete(Content c) {
        logger.info("Deleting content " + c.getContentID());
        deleteFile(c.getPath());
        DAOFactory.getContentDAO().delete(c.getContentID());
        contentList = DAOFactory.getContentDAO().findAll();
    }

    /**
     * The method that deletes a file recursively.
     *
     * @param file The file to be deleted.
     *
     * @return The outcome of the deletion.
     */
    public boolean deleteFile(String file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        Path dir = Paths.get(file);
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file,
                                                 BasicFileAttributes attrs) throws IOException {

                    logger.debug("Deleting file: " + file);
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir,
                                                          IOException exc) throws IOException {

                    logger.debug("Deleting dir: " + dir);
                    if (exc == null) {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    } else {
                        throw exc;
                    }
                }

            });
        } catch (IOException e) {
            logger.error("Exception while deleting file " + file
                    + ": " + e.getMessage());
            return false;
        }
        return true;
    }
}