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

/**
 * The VimeoPatterns class. It includes all Vimeo-required patterns.
 * 
 * @author George Petropoulos
 * @version 2.0
 * 
 */
public class VimeoPatterns {

    public static final String vimeoURL = "^(http|https)://(www.)?vimeo.com/(.+/)*\\p{Digit}{5,15}$";

    public static final String vimeoRequest = "^(http|https)://[a-z0-9_-]+\\.[a-z]+\\.[a-z]+/([^?]+)/\\p{Digit}{4,12}\\.mp4$";

    public static final String vimeoFolders = "(http|https)://[a-z0-9_-]+\\.[a-z]+\\.[a-z]+/([^?]+)/(.*\\.mp4).*";

}
