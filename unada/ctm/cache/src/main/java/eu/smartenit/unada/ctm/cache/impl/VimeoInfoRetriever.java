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
import com.github.axet.vget.vhs.VimeoParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The VimeoInfoRetriever class. It includes a method which retrieves a Vimeo's
 * video information from the Vimeo.
 * 
 * @author George Petropoulos
 * @version 2.0
 * 
 */
public class VimeoInfoRetriever {

    private static final Logger logger = LoggerFactory
            .getLogger(VimeoInfoRetriever.class);

    public VimeoInfoRetriever() {

    }

    /**
     * The method that retrieves the content information from Vimeo.
     * 
     * @param url
     *            The Vimeo URL.
     * 
     * @return The video information.
     * 
     */
    public VideoInfo retrieveVideoInfo(String url) {
        VideoInfo info = null;
        logger.info("Retrieving information for content " + url);
        URL web = null;
        try {
            web = new URL(url);
        } catch (MalformedURLException e) {
            logger.debug("Malformed URL " + url
                    + ", now aborting Vimeo info retrieval.");
            return null;
        }
        VimeoParser user = (VimeoParser) VGet.parser(web);

        info = user.info(web);

        VGet v = new VGet(info, new File(""));

        AtomicBoolean stop = new AtomicBoolean(false);
        Runnable notify = new Runnable() {
            public void run() {
            }
        };
        try {
            v.extract(user, stop, notify);
            logger.debug("Download URL: " + info.getInfo().getSource());
        } catch (Exception e) {
            logger.error("Exception while retrieving information for " + url
                    + ": " + e.getMessage());
            return null;
        }
        return info;
    }
}
