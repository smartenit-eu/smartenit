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
package eu.smartenit.unada.web.ui;

import eu.smartenit.unada.ctm.cache.timers.PredictionTask;
import eu.smartenit.unada.web.util.DemoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;

/**
 * The DemoBean class. It handles the demo.xhtml page.
 * 
 * @author George Petropoulos
 * @version 2.1
 * 
 */
@ManagedBean
@RequestScoped
public class DemoBean {

    private static final Logger logger = LoggerFactory
            .getLogger(DemoBean.class);

    private static final int MAX_EVENTS = 8;

    private static final int MAX_LINES = 80;

    public LinkedList<DemoEvent> eventList = new LinkedList<DemoEvent>();

    public LinkedList<DemoEvent> getEventList() {
        return eventList;
    }

    public void setEventList(LinkedList<DemoEvent> eventList) {
        this.eventList = eventList;
    }

    /**
     * The method that constructs the demo.xhtml page.
     * 
     */
    @PostConstruct
    public void init() {
        try {
            readLastLines(new File(System.getenv("HOME") + "/unada.log"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The method that reads the last X lines of the unada.log file.
     * 
     * @param file
     *            The log file to be read.
     * 
     */
    private void readLastLines(File file) throws FileNotFoundException,
            IOException {
        logger.debug("Reading last lines of unada.log.");
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        int lines = 0;
        StringBuilder builder = new StringBuilder();
        long length = file.length();
        length--;
        randomAccessFile.seek(length);
        for (long seek = length; seek >= 0; --seek) {
            randomAccessFile.seek(seek);
            char c = (char) randomAccessFile.read();
            builder.append(c);
            if (c == '\n') {
                builder = builder.reverse();
                filterLine(builder.toString());
                lines++;
                builder = null;
                builder = new StringBuilder();
                if (lines == MAX_LINES) {
                    break;
                }
            }
        }
        randomAccessFile.close();
    }

    /**
     * The method that filters each line and checks whether it contains any
     * major event, e.g. Proxying, WiFi offloading, Social Prediction, Overlay
     * Prediction, Vimeo prefetching, overlay prefetching.
     * 
     * @param s
     *            The line text to be checked and filtered.
     * 
     */
    private void filterLine(String s) {
        DemoEvent d;

        if (s.contains("CacheManagerImpl - ")
                && s.contains("Social prediction returned the ranked")) {
            String[] splits = s.split("CacheManagerImpl - ");
            String date = splits[0].trim().split("\\[")[0].trim();
            String text = splits[1];
            d = new DemoEvent();
            d.setTitle("Social Prediction");
            d.setText(text);
            d.setAlertclass("alert alert-info");
            // if (!eventList.contains(d))
            eventList.addLast(d);
        } else if (s.contains("VimeoDownloader - ")
                && (s.contains("Prefetching video") || s
                        .contains("completed successfully"))) {
            String[] splits = s.split("VimeoDownloader - ");
            String date = splits[0].trim().split("\\[")[0].trim();
            String text = splits[1];
            d = new DemoEvent();
            d.setTitle("Social Prefetching");
            d.setText(text);
            d.setAlertclass("alert alert-info");
            // if (!eventList.contains(d))
            eventList.addLast(d);
        } else if (s.contains("CacheManagerImpl - ")
                && s.contains("Overlay prediction returned the ranked")) {
            String[] splits = s.split("CacheManagerImpl - ");
            String date = splits[0].trim().split("\\[")[0].trim();
            String text = splits[1];
            d = new DemoEvent();
            d.setTitle("Overlay Prediction");
            d.setText(text);
            d.setAlertclass("alert alert-danger alert-error");
            // if (!eventList.contains(d))
            eventList.addLast(d);
        } else if (s.contains("OverlayDownloader - ")
                && (s.contains("Prefetching video") || s
                        .contains("completed successfully"))) {
            String[] splits = s.split("OverlayDownloader - ");
            String date = splits[0].trim().split("\\[")[0].trim();
            String text = splits[1];
            d = new DemoEvent();
            d.setTitle("Overlay Prefetching");
            d.setText(text);
            d.setAlertclass("alert alert-danger alert-error");
            // if (!eventList.contains(d))
            eventList.addLast(d);
        } else if (s.contains("ContentAccessLoggerImpl - ")
                && s.contains("is served from local cache.")) {
            String[] splits = s.split("ContentAccessLoggerImpl - ");
            String date = splits[0].trim().split("\\[")[0].trim();
            String text = splits[1];
            d = new DemoEvent();
            d.setTitle("Video Proxy");
            d.setText(text);
            d.setAlertclass("alert alert-success");
            // if (!eventList.contains(d))
            eventList.addLast(d);
        } else if (s.contains("LoginService - ")
                && s.contains("Login attempt by trusted user")) {
            String[] splits = s.split("LoginService - ");
            String date = splits[0].trim().split("\\[")[0].trim();
            String text = splits[1];
            d = new DemoEvent();
            d.setTitle("WiFi Offloading");
            d.setText(text);
            d.setAlertclass("alert alert-warning");
            // if (!eventList.contains(d))
            eventList.addLast(d);
        } else if (s.contains("MonitorRunner - ") && s.contains("Vimeo video")) {
            String[] splits = s.split("MonitorRunner - ");
            String date = splits[0].trim().split("\\[")[0].trim();
            String text = splits[1];
            d = new DemoEvent();
            d.setTitle("Social Monitoring");
            d.setText(text);
            d.setAlertclass("alert alert-info");
            // if (!eventList.contains(d))
            eventList.addLast(d);
        } else if (s.contains("DownloadRequestMessage - ")
                && (s.contains("Serving video") || s
                        .contains("completed successfully"))) {
            String[] splits = s.split("DownloadRequestMessage - ");
            String date = splits[0].trim().split("\\[")[0].trim();
            String text = splits[1];
            d = new DemoEvent();
            d.setTitle("Overlay Serving");
            d.setText(text);
            d.setAlertclass("alert alert-danger alert-error");
            // if (!eventList.contains(d))
            eventList.addLast(d);
        }

        // check if event list exceeded the limit, and delete the last item.
        if (eventList.size() > MAX_EVENTS) {
            eventList.removeLast();
        }
    }

    public void triggerPrediction() {
        PredictionTask prediction = new PredictionTask();
        prediction.run();
    }

}