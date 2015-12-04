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
package eu.smartenit.unada.ctm.cache;

import com.github.axet.vget.info.VideoInfo;

import eu.smartenit.unada.ctm.cache.impl.VimeoInfoRetriever;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by geopet on 10/29/14.
 */
public class VimeoInfoRetrieverTest {

	@Test
	public void testRetrieveVideoInfo() throws MalformedURLException {
		long before = System.currentTimeMillis();
		VideoInfo videoInfo = new VimeoInfoRetriever()
				.retrieveVideoInfo("https://www.vimeo.com/32369539");
		System.out.println("Time needed for retrieving vimeo info "
				+ (System.currentTimeMillis() - before) + "ms");
		assertNotNull(videoInfo);
		assertEquals(videoInfo.getInfo().getLength(), 6573594.0, 0.1);
	}

    @Test
    public void testRetrieveVideoInfo2() throws MalformedURLException {
        long before = System.currentTimeMillis();
        VideoInfo videoInfo = new VimeoInfoRetriever()
                .retrieveVideoInfo("http://www.vimeo.com/76926148");
        System.out.println("Time needed for retrieving vimeo info "
                + (System.currentTimeMillis() - before) + "ms");
        assertNotNull(videoInfo);
    }

	@Test
	public void testRetrieveFalseVideoInfo() throws MalformedURLException {
		long before = System.currentTimeMillis();
		VideoInfo videoInfo = new VimeoInfoRetriever()
				.retrieveVideoInfo("https://www.vimeo.com/12334567890");
		System.out.println("Time needed for retrieving vimeo info "
				+ (System.currentTimeMillis() - before) + "ms");
		assertNull(videoInfo);
	}

	@After
	public void teardown() throws InterruptedException {
		Thread.sleep(5000);
	}
}
