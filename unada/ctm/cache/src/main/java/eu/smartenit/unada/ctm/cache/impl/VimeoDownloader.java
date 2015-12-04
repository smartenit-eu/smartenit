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

import com.github.axet.vget.VGet;
import com.github.axet.vget.info.VideoInfo;
import com.github.axet.vget.vhs.VimeoInfo;

import eu.smartenit.unada.commons.constants.CacheConstants;
import eu.smartenit.unada.commons.constants.UnadaConstants;
import eu.smartenit.unada.commons.logging.UnadaLogger;
import eu.smartenit.unada.commons.threads.UnadaThreadService;
import eu.smartenit.unada.ctm.cache.VideoDownloader;
import eu.smartenit.unada.ctm.cache.util.OverlayFactory;
import eu.smartenit.unada.ctm.cache.util.VimeoPatterns;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The VimeoDownloader runnable class. It includes methods that retrieve content
 * information from Vimeo, and download the content, if not present in the
 * cache.
 * 
 * @author George Petropoulos
 * @version 3.1
 * 
 */
public class VimeoDownloader implements VideoDownloader, Runnable {

    private static final Logger logger = LoggerFactory
            .getLogger(VimeoDownloader.class);
    private String contentURL;
    private long contentID;
    private boolean prefetched;

    /**
     * The constructor.
     * 
     */
    public VimeoDownloader(String contentURL, boolean prefetched) {
        this.contentURL = contentURL;
        this.prefetched = prefetched;
    }

    /**
     * The method that executes the VimeoDownloader thread.
     * 
     */
    public void run() {
        try {
            downloadVideo();
        } catch (Exception e) {
            logger.error("Error while downloading video from " + contentURL
                    + " and url " + contentURL + " : " + e.getMessage());
        }
    }

    /**
     * The method that downloads the video from Vimeo. It checks whether the
     * content is cached, otherwise it downloads it and stores it in the local
     * cache.
     * 
     * @throws Exception
     */
    public void downloadVideo() throws Exception {

        contentID = getVideoID(contentURL);
        logger.info("Attempting to download content " + contentID
                + " from Vimeo.");

        // check if file already exists.
        logger.info("Checking if content " + contentID + " exists in cache.");
        Content content = null;
        try {
            content = DAOFactory.getContentDAO().findById(contentID);
        } catch (Exception e) {
            logger.error("Exception while accessing database for content id "
                    + contentID + ": " + e.getMessage());
            return;
        }
        if (content != null) {
            logger.info("Content " + contentID
                    + " is either downloading or already cached.");
            return;
        } else {
            logger.info("Content " + contentID + " is not cached.");
            VideoInfo info;
            Future<VideoInfo> future = UnadaThreadService.getThreadService()
                    .submit(new Callable<VideoInfo>() {

                        public VideoInfo call() throws Exception {
                            return new VimeoInfoRetriever()
                                    .retrieveVideoInfo(contentURL);
                        }
                    });
            info = future.get(10, TimeUnit.SECONDS);

            VGet v = new VGet(info, new File(""));
            Path target = getVideoStorage(v);
            try {
                if (target == null) {
                    throw new Exception(
                            "Streaming URL not according to pattern.");
                }
                Files.createDirectories(target.getParent());
                v.setTarget(target.toFile());

                // save content information to database
                content = new Content();
                content.setContentID(contentID);
                content.setCacheScore(0);
                content.setDownloaded(false);
                content.setPrefetched(prefetched);
                content.setPath(target.toString());
                content.setQuality(VimeoInfo.VimeoQuality.pLow.toString());
                content.setSize(v.getVideo().getInfo().getLength());
                content.setUrl(contentURL);
                content.setPrefetchedVimeo(true);

                logger.info("Inserting content " + content);
                DAOFactory.getContentDAO().insert(content);

                // download content
                logger.info("Prefetching video " + content.getContentID()
                        + " with size " + (float) content.getSize() / 1000000
                        + "MB from Vimeo.");
                long timeBefore = System.currentTimeMillis();
                v.download();
                long timeAfter = System.currentTimeMillis();

                // check if size is zero, then update with real size
                File f = new File(content.getPath());
                content.setSize(f.length());

                // update content with status and cache date
                content.setDownloaded(true);
                content.setCacheDate(new Date(System.currentTimeMillis()));
                logger.debug("Updating content " + content);
                logger.info("Prefetching of video {} from Vimeo was "
                        + "completed successfully after {} seconds.",
                        content.getContentID(), (timeAfter - timeBefore) / 1000);
                DAOFactory.getContentDAO().update(content);

                UnadaLogger.overall.info(
                        "{}: Video prefetching ({}, {}, {}, social, {}, {})",
                        new Object[] { UnadaConstants.UNADA_OWNER_MD5,
                                timeAfter, content.getContentID(),
                                content.getSize(),
                                prefetched ? "prefetched" : "watched",
                                timeAfter - timeBefore });

            } catch (Exception e) {
                logger.error("Exception while downloading video " + contentURL
                        + ": " + e.getMessage());
            }

            UnadaThreadService.getThreadService().execute(new Runnable() {
                @Override
                public void run() {
                    OverlayFactory.getOverlayManager().advertiseContent(
                            contentID);
                }
            });
        }

    }

    /**
     * The method that extracts the path where the video will be downloaded.
     * 
     * @param v
     *            The video information, as extracted by Vimeo.
     * 
     * @return The path where the video will be downloaded.
     * 
     */
    public Path getVideoStorage(VGet v) {
        Pattern p = Pattern.compile(VimeoPatterns.vimeoFolders);
        Matcher m = p.matcher(v.getVideo().getInfo().getSource().toString());
        if (m.find()) {
            return Paths.get(CacheConstants.cachePath, m.group(2), m.group(3));
        }
        return null;
    }

    /**
     * The method that retrieves the content ID from the request url.
     * 
     * @param url
     *            The request URL.
     * 
     * @return The content identifier.
     * 
     */
    public long getVideoID(String url) {
        long videoID;
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        String[] urlSplits = url.split("/");
        String videoString = urlSplits[urlSplits.length - 1];
        if (videoString.contains(".")) {
            String[] videoStrings = videoString.split(".");
            videoString = videoStrings[0];
        }
        videoID = Long.valueOf(videoString);
        return videoID;
    }
}