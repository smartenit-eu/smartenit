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
package eu.smartenit.unada.commons.logging;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

/**
 * The UnadaLogger class. It creates some additional logs for RBH trials.
 * 
 * @author George Petropoulos
 * @version 3.1
 * 
 */
public class UnadaLogger {

    public static Logger social;

    public static Logger overall;

    static {
        social = createLogger("social", System.getenv("HOME")
                + "/social.log.tmp", System.getenv("HOME")
                + "/log/social.log.%d{yyyy-MM-dd}.%i");
        overall = createLogger("overall", System.getenv("HOME")
                + "/overall.log.tmp", System.getenv("HOME")
                + "/log/overall.log.%d{yyyy-MM-dd}.%i");
    }

    /**
     * The method that creates a logger with a given name and at a given path.
     * 
     * @param name
     *            The logger name.
     * @param tmpfile
     *            , String logArchive The logger path.
     * 
     * @return The created logger.
     * 
     *         <rollingPolicy
     *         class="ch.qos.logback.core.rolling.FixedWindowRollingPolicy">
     *         <fileNamePattern>${HOME}/unada.%i.log.zip</fileNamePattern>
     *         <minIndex>1</minIndex> <maxIndex>2</maxIndex> </rollingPolicy>
     * 
     *         <triggeringPolicy
     *         class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
     *         <maxFileSize>10MB</maxFileSize> </triggeringPolicy>
     * 
     */
    public static Logger createLogger(String name, String tmpfile,
            String logArchive) {

        System.out.println("Creating logger with name " + name + "tempfile: "
                + tmpfile + " and archive: " + logArchive);
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        PatternLayoutEncoder ple = new PatternLayoutEncoder();

        ple.setPattern("%msg%n");
        ple.setContext(lc);
        ple.start();

        FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
        // SizeAndTimeBasedFNATP<ILoggingEvent> triggeringPolicy=new
        // SizeAndTimeBasedFNATP<ILoggingEvent>();
        // RollingFileAppender<ILoggingEvent> fileAppender = new
        // RollingFileAppender<ILoggingEvent>();
        // TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new
        // TimeBasedRollingPolicy<>();

        // triggeringPolicy.setMaxFileSize("1MB");
        // triggeringPolicy.setContext(lc);
        // triggeringPolicy.setTimeBasedRollingPolicy(rollingPolicy);

        // rollingPolicy.setFileNamePattern(logArchive);
        // rollingPolicy.setContext(lc);
        // rollingPolicy.setParent(fileAppender);
        // rollingPolicy.setTimeBasedFileNamingAndTriggeringPolicy(triggeringPolicy);

        fileAppender.setFile(tmpfile);
        fileAppender.setEncoder(ple);
        fileAppender.setContext(lc);
        // fileAppender.setTriggeringPolicy(rollingPolicy);
        fileAppender.setAppend(true);

        // triggeringPolicy.start();
        // rollingPolicy.start();
        fileAppender.start();

        Logger logger = (Logger) LoggerFactory.getLogger(name);
        logger.addAppender(fileAppender);
        logger.setLevel(Level.INFO);

        logger.info("Logger created");

        return logger;
    }

}
