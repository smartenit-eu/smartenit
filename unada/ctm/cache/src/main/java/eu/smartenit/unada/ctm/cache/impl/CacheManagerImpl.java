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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import com.github.axet.vget.info.VideoInfo;

import eu.smartenit.unada.commons.constants.UnadaConstants;
import eu.smartenit.unada.commons.logging.UnadaLogger;
import eu.smartenit.unada.commons.threads.UnadaThreadService;

import eu.smartenit.unada.db.dto.SocialPredictionParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.ctm.cache.CacheManager;
import eu.smartenit.unada.ctm.cache.ContentManager;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Cache;
import eu.smartenit.unada.db.dto.Content;

/**
 * The CacheManagerImpl class. It includes methods to update the cache, based on
 * the received sorted lists of content from the social and overlay prediction.
 * 
 * @author George Petropoulos
 * @version 2.1
 * 
 */
public class CacheManagerImpl implements CacheManager {

    private static final Logger logger = LoggerFactory
            .getLogger(CacheManagerImpl.class);

    private ContentManager contentManager = new ContentManagerImpl();

    public CacheManagerImpl() {

    }

    public ContentManager getContentManager() {
        return contentManager;
    }

    public void setContentManager(ContentManager contentManager) {
        this.contentManager = contentManager;
    }

    /**
     * The method that updates the cache based on the received sorted lists of
     * content from the social and overlay prediction.
     * 
     * @param socialContentList
     *            The sorted list of contents from the social prediction.
     * @param overlayContentList
     *            The sorted list of contents from the overlay prediction.
     * 
     */
    public void updateCache(List<Content> socialContentList,
            List<Content> overlayContentList) {

        Cache cache = DAOFactory.getCacheDAO().findLast();

        // if cache is empty, just fill some random parameters.
        if (cache == null) {
            cache = new Cache();
            cache.setSize(8000000000L); // 8GB
            cache.setSizeThreshold(100000000L); // 100MB
            cache.setOverlayThreshold(0.5);
            cache.setSocialThreshold(0.5);
            cache.setTimeToLive(7 * 24 * 3600000); // 1 week
        }

        int socialListSize = 0;
        int overlayListSize = 0;

        if (socialContentList == null) {
            socialContentList = new ArrayList<Content>();
        }
        logger.info("Social prediction returned the ranked content list: "
                + getContentIdsAsString(socialContentList));
        socialListSize = socialContentList.size();

        if (overlayContentList == null) {
            overlayContentList = new ArrayList<Content>();
        }
        logger.info("Overlay prediction returned the ranked content list: "
                + getContentIdsAsString(overlayContentList));
        overlayListSize = overlayContentList.size();

        updateSocialContentListWithSizes(socialContentList);

        List<Content> ignoredContentList = new ArrayList<Content>();
        List<Long> toBePrefetched = new ArrayList<Long>();
        long currentCacheSize = DAOFactory.getContentDAO().findTotalSize();

        int max = (socialListSize > overlayListSize) ? socialListSize
                : overlayListSize;
        int index = 0;
        long currentTime = System.currentTimeMillis();

        SocialPredictionParameters sp = DAOFactory
                .getSocialPredictionParametersDAO().findLast();
        if (sp != null) {
            UnadaLogger.overall
                    .info("{}: Social prediction parameters @{}: ({}, {}, {}, {}, {}, {})",
                            new Object[] { UnadaConstants.UNADA_OWNER_MD5,
                                    currentTime, sp.getLambda1(),
                                    sp.getLambda2(), sp.getLambda3(),
                                    sp.getLambda4(), sp.getLambda5(),
                                    sp.getLambda6() });
        }

        while (index < max) {

            Content c;
            if (index < socialListSize) {
                c = socialContentList.get(index);

                UnadaLogger.overall.info(
                        "{}: Social prediction @ {}: ({}, {})",
                        new Object[] { UnadaConstants.UNADA_OWNER_MD5,
                                currentTime, c.getContentID(),
                                c.getCacheScore() });

                logger.debug("Checking content " + c.getContentID()
                        + " with score " + c.getCacheScore());
                // check if content already in to-be-prefetched list
                logger.debug("Prefetched list " + toBePrefetched);
                if (!toBePrefetched.contains((Long) c.getContentID())) {
                    // if content exists in overlay
                    int overlayIndex = contentExistsInList(overlayContentList,
                            c.getContentID());
                    if (overlayIndex != -1) {
                        logger.debug("Social content " + c.getContentID()
                                + " also belongs to overlay list.");
                        // if overlay score is higher than threshold, then
                        // prefer
                        // overlay
                        if (overlayContentList.get(overlayIndex)
                                .getCacheScore() >= cache.getOverlayThreshold()) {
                            logger.debug("Overlay score is good, will be preferred from there.");
                            // do nothing else here.
                        } else {
                            // check social score, ignore if not and download if
                            // ok
                            if (c.getCacheScore() < cache.getSocialThreshold()
                                    || currentCacheSize + c.getSize() > cache
                                            .getSize()
                                            - cache.getSizeThreshold()) {
                                logger.debug("SL: Adding content "
                                        + c.getContentID()
                                        + " to ignored list.");
                                ignoredContentList.add(c);
                            } else {
                                logger.debug("SL: Attempting to prefetch content "
                                        + c.getContentID());
                                getContentManager().prefetchContent(c);
                                toBePrefetched.add(c.getContentID());
                                currentCacheSize = currentCacheSize
                                        + c.getSize();
                            }
                        }
                    } else {
                        // check social score, ignore if not and download if ok
                        if (c.getCacheScore() < cache.getSocialThreshold()
                                || currentCacheSize + c.getSize() > cache
                                        .getSize() - cache.getSizeThreshold()) {
                            logger.debug("SL: Adding content "
                                    + c.getContentID() + " to ignored list.");
                            ignoredContentList.add(c);
                        } else {
                            logger.debug("SL: Attempting to prefetch content "
                                    + c.getContentID());
                            getContentManager().prefetchContent(c);
                            toBePrefetched.add(c.getContentID());
                            currentCacheSize = currentCacheSize + c.getSize();
                        }
                    }
                }
            }

            if (index < overlayListSize) {
                c = overlayContentList.get(index);

                UnadaLogger.overall.info(
                        "{}: Overlay prediction @ {}: ({}, {})",
                        new Object[] { UnadaConstants.UNADA_OWNER_MD5,
                                currentTime, c.getContentID(),
                                c.getCacheScore() });

                logger.debug("Checking content " + c.getContentID()
                        + " with score " + c.getCacheScore());
                // check if already to be prefetched.
                logger.debug("Prefetched list " + toBePrefetched);
                if (!toBePrefetched.contains((Long) c.getContentID())) {
                    // check overlay score, ignore if not and download if ok
                    if (c.getCacheScore() < cache.getOverlayThreshold()
                            || currentCacheSize + c.getSize() > cache.getSize()
                                    - cache.getSizeThreshold()) {
                        logger.debug("OL: Adding content " + c.getContentID()
                                + " to ignored list.");
                        ignoredContentList.add(c);
                    } else {
                        logger.debug("OL: Attempting to prefetch content "
                                + c.getContentID());
                        getContentManager().prefetchContent(c);
                        toBePrefetched.add(c.getContentID());
                        currentCacheSize = currentCacheSize + c.getSize();
                    }
                }
            }
            index++;
        }

        logger.info("Updated cache size " + (float) currentCacheSize / 1000000
                + "MB.");

        // Finally delete the ignored ones
        // Check if cache is very close to threshold: delete all ignored,
        // otherwise delete least accessed.
        if (currentCacheSize > (float) cache.getSize() - 1.5
                * (float) cache.getSizeThreshold()) {
            logger.info("Cache size is very close to threshold, will now delete all ignored.");
            getContentManager().deleteContents(ignoredContentList);
        } else {
            logger.info("Cache size is OK, will now delete least accessed.");
            getContentManager().deleteContents(ignoredContentList,
                    cache.getTimeToLive());
        }
    }

    /**
     * The method that deletes the expired contents from the local cache and
     * database.
     * 
     */
    public void deleteExpired() {
        Cache cache = DAOFactory.getCacheDAO().findLast();

        // if cache is empty, just fill some random parameters.
        if (cache == null) {
            cache = new Cache();
            cache.setTimeToLive(7 * 24 * 3600000); // 1 week
        }

        // Find expired contents and delete them.
        List<Content> expiredContents = DAOFactory.getContentDAO()
                .findAllOutDated(
                        System.currentTimeMillis() - cache.getTimeToLive());
        logger.debug("Deleting {} expired cached contents from the database "
                + "and the local cache. Time to live = {}",
                expiredContents.size(), cache.getTimeToLive());
        getContentManager().deleteContents(expiredContents);
    }

    private int contentExistsInList(List<Content> list, long contentID) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getContentID() == contentID) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void updateSocialContentListWithSizes(List<Content> contentList) {
        List<Future<VideoInfo>> futureList = new ArrayList<Future<VideoInfo>>();
        for (int i = 0; i < contentList.size(); i++) {
            final long contentID = contentList.get(i).getContentID();
            Future<VideoInfo> f = UnadaThreadService.getThreadService().submit(
                    new Callable<VideoInfo>() {

                        public VideoInfo call() throws Exception {
                            return new VimeoInfoRetriever()
                                    .retrieveVideoInfo("http://www.vimeo.com/"
                                            + contentID);
                        }
                    });
            futureList.add(f);
        }

        for (int i = 0; i < futureList.size(); i++) {
            VideoInfo info = null;
            try {
                info = futureList.get(i).get(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("Interruption while retrieving info: "
                        + e.getMessage());
            } catch (ExecutionException e) {
                logger.error("Error while retrieving info: " + e.getMessage());
            } catch (TimeoutException e) {
                logger.error("Timeout while retrieving info: " + e.getMessage());
            }

            if (info != null) {
                contentList.get(i).setSize(info.getInfo().getLength());
                logger.debug("Content " + contentList.get(i).getContentID()
                        + ", size = " + info.getInfo().getLength());
            } else {
                contentList.get(i).setSize(0);
            }
        }
    }

    private String getContentIdsAsString(List<Content> contents) {
        String s = "[";
        for (Content c : contents) {
            s += String.valueOf(c.getContentID());
            if (contents.indexOf(c) != contents.size() - 1) {
                s += ", ";
            }
        }
        return s.concat("]");
    }

}
