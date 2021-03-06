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

import eu.smartenit.unada.commons.constants.CacheConstants;
import eu.smartenit.unada.commons.constants.UnadaConstants;
import eu.smartenit.unada.commons.logging.UnadaLogger;
import eu.smartenit.unada.commons.threads.UnadaThreadService;
import eu.smartenit.unada.ctm.cache.VideoDownloader;
import eu.smartenit.unada.ctm.cache.util.FileUtils;
import eu.smartenit.unada.ctm.cache.util.OverlayFactory;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Content;
import eu.smartenit.unada.om.IFutureDownload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;

/**
 * The OverlayDownloader runnable class. It includes methods that retrieve
 * content information from the overlay, and download the content, if not
 * present in the cache.
 * 
 * @author George Petropoulos
 * @version 2.1
 * 
 */
public class OverlayDownloader implements VideoDownloader, Runnable {

    private static final Logger logger = LoggerFactory
            .getLogger(OverlayDownloader.class);

    private Content content;

    /**
     * The constructor.
     * 
     */
    public OverlayDownloader(Content content) {
        this.content = content;
    }

    /**
     * The method that executes the OverlayDownloader thread.
     * 
     */
    public void run() {
        try {
            downloadVideo();
        } catch (Exception e) {
            logger.error("Error while downloading video "
                    + content.getContentID() + " from the overlay.");
        }
    }

    /**
     * The method that downloads the video from the overlay. It checks whether
     * the content is cached, otherwise it downloads it and stores it in the
     * local cache.
     * 
     * @throws Exception
     */
    public void downloadVideo() throws Exception {

        logger.info("Attempting to download content " + content.getContentID()
                + " from the overlay.");

        // check if file already exists.
        logger.info("Checking if content " + content.getContentID()
                + " exists in cache.");
        Content c = null;
        try {
            c = DAOFactory.getContentDAO().findById(content.getContentID());
        } catch (Exception e) {
            logger.error("Exception while accessing database for content id "
                    + content.getContentID() + ": " + e.getMessage());
            return;
        }
        if (c != null) {
            logger.info("Content " + content.getContentID()
                    + " is either downloading or already cached.");
            return;
        } else {

            try {
                logger.debug("Content " + content.getContentID()
                        + " is not cached.");
                String remotePath = content.getPath();
                logger.debug("Content path " + content.getPath());
                if (remotePath != null && !remotePath.isEmpty()) {
                    String p = remotePath.split("unada")[1];
                    content.setPath(CacheConstants.cachePath + p);

                    File f = new File(content.getPath());
                    if (!f.getParentFile().exists()) {
                        f.getParentFile().mkdirs();
                    }
                    f.createNewFile();
                } else {
                    logger.debug("Path will be resolved during overlay download.");
                }

                // save content information to database
                content.setCacheScore(0);
                content.setDownloaded(false);
                content.setPrefetched(true);
                content.setQuality("480p");
                content.setUrl("http://www.vimeo.com/m/"
                        + content.getContentID());
                content.setPrefetchedVimeo(false);
                content.setCacheDate(new Date(System.currentTimeMillis()));

                logger.info("Inserting content " + content);
                DAOFactory.getContentDAO().insert(content);

                // download content
                logger.info("Prefetching video " + content.getContentID()
                        + " with size " + (float) content.getSize() / 1000000
                        + "MB from the overlay.");
                long timeBefore = System.currentTimeMillis();
                IFutureDownload future = OverlayFactory.getOverlayManager()
                        .downloadContent(content);
                future.get();
                long timeAfter = System.currentTimeMillis();

                if (!future.isSuccess()) {
                    logger.warn("Download from overlay failed! Will prefetch it from Vimeo.");
                    // delete existing record
                    FileUtils.deleteFile(content.getPath());
                    DAOFactory.getContentDAO().delete(content.getContentID());

                    // run vimeo downloader to fetch it from Vimeo
                    UnadaThreadService.getThreadService().execute(
                            new VimeoDownloader(content.getUrl(), true));
                    return;
                }

                // update content with size, status and cache date
                content.setDownloaded(true);
                content.setCacheDate(new Date(System.currentTimeMillis()));
                logger.debug("Updating content " + content);
                logger.info("Prefetching of video {} from the overlay was "
                        + "completed successfully after {} seconds.",
                        content.getContentID(), (timeAfter - timeBefore) / 1000);
                DAOFactory.getContentDAO().update(content);

                UnadaLogger.overall.info(
                        "{}: Video prefetching ({}, {}, {}, overlay, {})",
                        new Object[] { UnadaConstants.UNADA_OWNER_MD5,
                                timeAfter, content.getContentID(),
                                content.getSize(), timeAfter - timeBefore });
                UnadaThreadService.getThreadService().execute(new Runnable() {
                    @Override
                    public void run() {
                        OverlayFactory.getOverlayManager().advertiseContent(
                                content.getContentID());
                    }
                });

            } catch (Exception e) {
                logger.error("Exception while downloading video {}",
                        content.getContentID(), e);
                logger.warn("Download from overlay failed! Will prefetch it from Vimeo.");
                // delete existing record
                FileUtils.deleteFile(content.getPath());
                DAOFactory.getContentDAO().delete(content.getContentID());

                // run vimeo downloader to fetch it from Vimeo
                UnadaThreadService.getThreadService().execute(
                        new VimeoDownloader(content.getUrl(), true));
                return;
            }
        }
    }
}
