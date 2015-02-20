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

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.smartenit.unada.ctm.main.init.SocialFactory;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Owner;
import eu.smartenit.unada.db.dto.UNaDaConfiguration;
import eu.smartenit.unada.sm.SocialMonitor;

public class SocialMonitorIntegrationTest {
	
	
	@Before
	public void setup() throws IOException {
		DAOFactory.getFeedItemDAO().createTable();
		DAOFactory.getFriendDAO().createTable();
		DAOFactory.getTrustedUserDAO().createTable();
		DAOFactory.getVideoInfoDAO().createTable();
	}

	
	@Test @Ignore
	public void testSocialMonitor() throws InterruptedException {
		UNaDaConfiguration unadaConfiguration = new UNaDaConfiguration();
		unadaConfiguration.setSocialInterval(24*3600*1000);
		SocialMonitor socialMonitor = new SocialMonitor(unadaConfiguration);
        SocialFactory.setSocialMonitor(socialMonitor);
        Owner owner = new Owner();
        owner.setOauthToken("CAAVS6ZB5iEOQBAI2hpUH152umqZA6D5ZCHd1uA432xekyXi7qVHQ9hZChZBc5geoQJvocFQAc6jmKemZAuSCMXFNImzmZCVnlla5Dkh21uDM5tywKaZC3l3jloafJ3ROQBEoUu0K3U9iCtjbgynJ3PYor5iT9ZBTkrbb25fESZBiIvPNFYGU8RAyEC");
        SocialFactory.getSocialMonitor().startMonitoring(owner);
        Thread.sleep(30000);
        assertTrue(DAOFactory.getFeedItemDAO().findAll().size()>0);
        assertTrue(DAOFactory.getFriendDAO().findAll().size()>0);
        assertTrue(DAOFactory.getTrustedUserDAO().findAll().size()>0);
        assertTrue(DAOFactory.getVideoInfoDAO().findAll().size()>0);
	}
	
	@After
	public void teardown() {
		DAOFactory.getFeedItemDAO().deleteTable();
		DAOFactory.getFriendDAO().deleteTable();
		DAOFactory.getTrustedUserDAO().deleteTable();
		DAOFactory.getVideoInfoDAO().deleteTable();
	}

}
