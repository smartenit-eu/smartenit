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
package eu.smartenit.unada.ctm.main;

import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Cache;
import eu.smartenit.unada.db.dto.Content;
import eu.smartenit.unada.om.OverlayManager;
import eu.smartenit.unada.om.exceptions.OverlayException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by geopet on 11/9/14.
 */
public class OverlayManagerIntegrationTest {

    @Before
    public void setup() {
        DAOFactory.getCacheDAO().createTable();
        DAOFactory.getContentDAO().createTable();

        Cache cache = new Cache();
        cache.setTimeToLive(1000);
        cache.setSize(8000000000L);
        cache.setSizeThreshold(100000);
        cache.setOverlayThreshold(0);
        cache.setSocialThreshold(0);
        DAOFactory.getCacheDAO().insert(cache);
    }

    @Test @Ignore
    public void testOverlayManagerPrediction() throws UnknownHostException, OverlayException,
            InterruptedException {

        OverlayManager om1 = new OverlayManager("1");
        om1.createOverlay(InetAddress.getByName("127.0.0.1"));

        Thread.sleep(3000);

        OverlayManager om2 = new OverlayManager("2");
        om2.setPort(4002);
        om2.joinOverlay(InetAddress.getByName("127.0.0.1"), 4001);
        om2.advertiseContent(1);

        Thread.sleep(3000);

        OverlayManager om3 = new OverlayManager("2");
        om3.setPort(4003);
        om3.joinOverlay(InetAddress.getByName("127.0.0.1"), 4001);
        om3.advertiseContent(1);

        Thread.sleep(3000);
        List<Content> overlayContents = om2.getPrediction();
        Thread.sleep(3000);
        assertTrue(overlayContents.size() > 0);
        Thread.sleep(3000);
        om1.shutDown();
        om2.shutDown();
    }

    @Test @Ignore
    public void testOverlayManagerPrefetching() throws IOException, OverlayException,
            InterruptedException {
        OverlayManager om = new OverlayManager("something");
        om.joinOverlay(InetAddress.getByName("192.168.40.1"), 4001);

        Thread.sleep(10000);

        //TODO add correct content information
        Content c = new Content();
        c.setContentID(76427539);
        c.setCacheScore(1);
        c.setPath("test/cache/70666/020/195586506.mp4");
        File f = new File("test/cache/70666/020/195586506.mp4");
        f.getParentFile().mkdirs();
        f.createNewFile();
        om.downloadContent(c);

        Thread.sleep(30000);
        assertTrue(new File("test/cache/65718/468/129300302.mp4").exists());

        om.shutDown();
    }

    @After
    public void teardown() {
        DAOFactory.getCacheDAO().deleteTable();
        DAOFactory.getContentDAO().deleteTable();
        IntegrationTestsUtil.deleteDirectory(new File("test/"));
    }
}
