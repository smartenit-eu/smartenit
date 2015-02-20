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

import eu.smartenit.unada.ctm.cache.util.OverlayFactory;
import eu.smartenit.unada.ctm.main.timers.PredictionTask;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dao.util.Tables;
import eu.smartenit.unada.db.dto.Cache;
import eu.smartenit.unada.db.dto.Owner;
import eu.smartenit.unada.om.OverlayManager;
import eu.smartenit.unada.om.exceptions.OverlayException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by geopet on 11/9/14.
 */
public class PredictionIntegrationTest {

    Tables t;

    @Before
    public void setup() throws Exception {
        t = new Tables();
        t.createTables();

        Cache cache = new Cache();
        cache.setTimeToLive(1000);
        cache.setSize(8000000000L);
        cache.setSizeThreshold(100000);
        cache.setOverlayThreshold(0);
        cache.setSocialThreshold(0);
        DAOFactory.getCacheDAO().insert(cache);

        //TODO update owner facebook id
        Owner owner = new Owner();
        owner.setFacebookID("");
        owner.setOauthToken("CAAVS6ZB5iEOQBAI2hpUH152umqZA6D5ZCHd1uA432xekyXi7qVHQ9hZChZBc5geoQJvocFQAc6jmKemZAuSCMXFNImzmZCVnlla5Dkh21uDM5tywKaZC3l3jloafJ3ROQBEoUu0K3U9iCtjbgynJ3PYor5iT9ZBTkrbb25fESZBiIvPNFYGU8RAyEC");
        DAOFactory.getOwnerDAO().insert(owner);

    }

    @Test @Ignore
    public void testPredictionTask() throws UnknownHostException, OverlayException,
            InterruptedException {
        assertEquals(DAOFactory.getContentDAO().findAll().size(), 0);
        OverlayManager om = new OverlayManager("something");
        OverlayFactory.setOverlayManager(om);
        om.joinOverlay(InetAddress.getByName("192.168.40.1"), 4001);

        Thread.sleep(10000);

        new PredictionTask().run();
        Thread.sleep(30000);

        assertTrue(DAOFactory.getContentDAO().findAll().size() > 0);
        om.shutDown();
    }


    @After
    public void teardown() {
        t.deleteTables();
        IntegrationTestsUtil.deleteDirectory(new File("test/"));
    }
}
