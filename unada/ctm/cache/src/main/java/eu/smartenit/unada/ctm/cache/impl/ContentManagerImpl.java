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
package eu.smartenit.unada.ctm.cache.impl;

import eu.smartenit.unada.ctm.cache.util.OverlayFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.commons.constants.UnadaConstants;
import eu.smartenit.unada.commons.logging.UnadaLogger;
import eu.smartenit.unada.commons.threads.UnadaThreadService;
import eu.smartenit.unada.ctm.cache.ContentManager;
import eu.smartenit.unada.ctm.cache.util.FileUtils;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Content;

import java.util.List;

/**
 * The ContentManagerImpl class. It includes methods to delete contents from the
 * database and the local cache, prefetches content, either from Vimeo or other
 * uNaDas and inserts them to the local cache.
 * 
 * @author George Petropoulos
 * @version 2.1
 * 
 */
public class ContentManagerImpl implements ContentManager {

    private static final Logger logger = LoggerFactory
            .getLogger(ContentManagerImpl.class);

    // max allowed hop count to download from overlay
    private static final int MAX_HOPS = 1;

    private VimeoDownloader vimeoDownloader;

    private OverlayDownloader overlayDownloader;

    public VimeoDownloader getVimeoDownloader() {
        return vimeoDownloader;
    }

    public void setVimeoDownloader(VimeoDownloader vimeoDownloader) {
        this.vimeoDownloader = vimeoDownloader;
    }

    public OverlayDownloader getOverlayDownloader() {
        return overlayDownloader;
    }

    public void setOverlayDownloader(OverlayDownloader overlayDownloader) {
        this.overlayDownloader = overlayDownloader;
    }

    public ContentManagerImpl() {

    }

    /**
     * The method that deletes a content from both the local cache and the
     * database.
     * 
     * @param content
     *            The content to be deleted.
     * 
     */
    public void deleteContent(Content content) {
        if (content != null) {
            logger.info("Deleting content " + content.getContentID()
                    + " from the database and local cache " + content.getPath());
            // String contentRootDirectory =
            // FileUtils.getRootDir(content.getPath());
            // String remotePath = content.getPath();
            // String p = remotePath.split("unada")[1];
            // content.setPath(CacheConstants.cachePath + p);
            FileUtils.deleteFile(DAOFactory.getContentDAO()
                    .findById(content.getContentID()).getPath());
            DAOFactory.getContentDAO().delete(content.getContentID());
        }
    }

    /**
     * The method that deletes a list of contents from both the local cache and
     * the database.
     * 
     * @param contents
     *            The list of contents to be deleted.
     * 
     */
    public void deleteContents(List<Content> contents) {
        if (contents != null && contents.size() != 0) {
            logger.info("Deleting content list from the database and local cache.");
            for (Content c : contents) {
                // String contentRootDirectory =
                // FileUtils.getRootDir(c.getPath());
                // String remotePath = c.getPath();
                // String p = remotePath.split("unada")[1];
                // c.setPath(CacheConstants.cachePath + p);
                FileUtils.deleteFile(DAOFactory.getContentDAO()
                        .findById(c.getContentID()).getPath());

                UnadaLogger.overall.info(
                        "{}: Cache delete ({}, {})",
                        new Object[] {
                                UnadaConstants.UNADA_OWNER_MD5,
                                c.getContentID(),
                                System.currentTimeMillis()
                                        - c.getCacheDate().getTime() });
            }
            DAOFactory.getContentDAO().deleteBatch(contents.listIterator());
        }
    }

    /**
     * The method that deletes a list of contents from both the local cache and
     * the database, that were not accessed since the given time threshold.
     * 
     * @param contents
     *            The list of contents to be deleted.
     * @param threshold
     *            The time threshold.
     * 
     */
    public void deleteContents(List<Content> contents, long threshold) {
        if (contents != null && contents.size() != 0) {
            logger.info("Deleting least accessed content list for threshold of "
                    + threshold + "ms.");
            for (Content c : contents) {
                boolean deleted = DAOFactory.getContentDAO()
                        .deleteNotAccessedContent(c,
                                System.currentTimeMillis() - threshold);
                if (deleted) {
                    logger.info("Content is deleted from database, will be deleted from physical cache.");
                    // String contentRootDirectory =
                    // FileUtils.getRootDir(c.getPath());
                    // String remotePath = c.getPath();
                    // String p = remotePath.split("unada")[1];
                    // c.setPath(CacheConstants.cachePath + p);
                    FileUtils.deleteFile(DAOFactory.getContentDAO()
                            .findById(c.getContentID()).getPath());

                    UnadaLogger.overall.info(
                            "{}: Cache delete ({}, {})",
                            new Object[] {
                                    UnadaConstants.UNADA_OWNER_MD5,
                                    c.getContentID(),
                                    System.currentTimeMillis()
                                            - c.getCacheDate().getTime() });
                }
            }
        }
    }

    /**
     * The method that prefetches a content, and inserts it to the local cache
     * and the database.
     * 
     * @param content
     *            The content to be prefetched.
     * 
     */
    public void prefetchContent(Content content) {
        if (content != null) {
            logger.info("Determining download source for content "
                    + content.getContentID());

            if (OverlayFactory.getOverlayManager().getHopCount(content) <= MAX_HOPS) { // change
                                                                                       // this
                                                                                       // to
                                                                                       // ask
                                                                                       // OM
                                                                                       // for
                                                                                       // hop
                                                                                       // count
                logger.info("Content is closer from the overlay, hence will be fetched from there.");
                OverlayDownloader overlayDownloader = new OverlayDownloader(
                        content);
                setOverlayDownloader(overlayDownloader);
                UnadaThreadService.getThreadService().execute(
                        getOverlayDownloader());
            } else {
                logger.info("Content is closer from the Vimeo servers, hence will be fetched from there.");
                VimeoDownloader vimeoDownloader = new VimeoDownloader(
                        "http://www.vimeo.com/m/" + content.getContentID(),
                        true);
                setVimeoDownloader(vimeoDownloader);
                UnadaThreadService.getThreadService().execute(
                        getVimeoDownloader());
            }
        }
    }

    /**
     * The method that prefetches a list of contents, and inserts them to the
     * local cache and the database.
     * 
     * @param contents
     *            The list of contents to be prefetched.
     * 
     */
    public void prefetchContents(List<Content> contents) {

        for (Content c : contents) {
            prefetchContent(c);
        }

    }

}
