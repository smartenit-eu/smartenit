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

import eu.smartenit.unada.ctm.main.init.SocialFactory;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dao.util.Tables;
import eu.smartenit.unada.db.dto.Content;
import eu.smartenit.unada.db.dto.Owner;
import eu.smartenit.unada.db.dto.UNaDaConfiguration;
import eu.smartenit.unada.om.OverlayManager;
import eu.smartenit.unada.om.exceptions.OverlayException;
import eu.smartenit.unada.sa.SocialAnalyzer;
import eu.smartenit.unada.sm.SocialMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by geopet on 11/9/14.
 */
public class SocialAnalyzerIntegrationTest {

    Tables t;

    @Before
    public void setup() throws IOException {
        t = new Tables();
        t.createTables();
    }


    @Test @Ignore
    public void testSocialAnalyzer() throws InterruptedException, OverlayException {
        UNaDaConfiguration unadaConfiguration = new UNaDaConfiguration();
        unadaConfiguration.setSocialInterval(24*3600*1000);
        SocialMonitor socialMonitor = new SocialMonitor(unadaConfiguration);
        SocialFactory.setSocialMonitor(socialMonitor);
        Owner owner = new Owner();
        owner.setOauthToken("CAAVS6ZB5iEOQBAI2hpUH152umqZA6D5ZCHd1uA432xekyXi7qVHQ9hZChZBc5geoQJvocFQAc6jmKemZAuSCMXFNImzmZCVnlla5Dkh21uDM5tywKaZC3l3jloafJ3ROQBEoUu0K3U9iCtjbgynJ3PYor5iT9ZBTkrbb25fESZBiIvPNFYGU8RAyEC");
        SocialFactory.getSocialMonitor().startMonitoring(owner);
        Thread.sleep(10000);
        assertTrue(DAOFactory.getFeedItemDAO().findAll().size()>0);
        assertTrue(DAOFactory.getFriendDAO().findAll().size()>0);
        assertTrue(DAOFactory.getTrustedUserDAO().findAll().size()>0);
        assertTrue(DAOFactory.getVideoInfoDAO().findAll().size()>0);

        OverlayManager om = new OverlayManager("ada");
        om.createOverlay();
        SocialAnalyzer socialAnalyzer = new SocialAnalyzer(om);
        List<Content> contents = socialAnalyzer.predicting();
        assertEquals(contents.size(), 1);
        assertEquals(contents.get(0).getContentID(), 53803679);
        //assertEquals(contents.get(0).getCacheScore(), 0, 0.001);
    }

    @After
    public void teardown() {
        t.deleteTables();
    }
}
