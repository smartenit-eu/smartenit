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
package eu.smartenit.unada.ctm.cache.util;

import eu.smartenit.unada.sa.SocialAnalyzer;
import eu.smartenit.unada.sm.SocialMonitor;

/**
 * The SocialFactory class. It includes constructors for the Social Monitor and
 * Social Analyzer components.
 * 
 * @author George Petropoulos
 * @version 3.1
 * 
 */
public class SocialFactory {

    private static SocialAnalyzer socialAnalyzer;

    private static SocialMonitor socialMonitor;

    public static SocialAnalyzer getSocialAnalyzer() {
        return socialAnalyzer;
    }

    public static void setSocialAnalyzer(SocialAnalyzer socialAnalyzer) {
        SocialFactory.socialAnalyzer = socialAnalyzer;
    }

    public static SocialMonitor getSocialMonitor() {
        return socialMonitor;
    }

    public static void setSocialMonitor(SocialMonitor socialMonitor) {
        SocialFactory.socialMonitor = socialMonitor;
    }

}
