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
package eu.smartenit.unada.interfaces.enduser;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.smartenit.unada.commons.commands.ARP;
import eu.smartenit.unada.db.dao.impl.TrustedUserDAO;
import eu.smartenit.unada.db.dao.impl.UNaDaConfigurationDAO;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.RemoteLoginReply;
import eu.smartenit.unada.db.dto.TrustedUser;
import eu.smartenit.unada.db.dto.UNaDaConfiguration;
import eu.smartenit.unada.db.dto.WifiConfiguration;

/**
 * The LoginServiceTest class implementing tests for the login REST API.
 * 
 * @author George Petropoulos
 * @version 2.0
 */
public class LoginServiceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(LoginService.class);
    }

    private TrustedUserDAO trustedUserDAO = mock(TrustedUserDAO.class);

    private HttpServletRequest request = mock(HttpServletRequest.class);

    private UNaDaConfigurationDAO unadaDAO = mock(UNaDaConfigurationDAO.class);

    private ARP arp = mock(ARP.class);

    @Before
    public void setup() {
        TrustedUser trusted = new TrustedUser();
        trusted.setFacebookID("4567890");
        trusted.setMacAddress("aa:ff::0e");

        UNaDaConfiguration unadaConfiguration = new UNaDaConfiguration();
        unadaConfiguration.setIpAddress("192.168.40.1");
        WifiConfiguration privateWifi = new WifiConfiguration();
        privateWifi.setSSID("private-rb-horst-ssid");
        privateWifi.setPassword("1234567890");
        unadaConfiguration.setPrivateWifi(privateWifi);

        when(trustedUserDAO.findById("123456")).thenReturn(null);
        when(trustedUserDAO.findById("4567890")).thenReturn(trusted);
        when(request.getRemoteAddr()).thenReturn("192.168.40.10");
        when(unadaDAO.findLast()).thenReturn(unadaConfiguration);
        when(arp.execute("192.168.40.10")).thenReturn("aa:ff::0e");
        DAOFactory.setTrustedUserDAO(trustedUserDAO);
        DAOFactory.setuNaDaConfigurationDAO(unadaDAO);
    }

    @Test
    public void testNonExistingTrustedUser() {
        RemoteLoginReply negativeReply = target("login/123456")
                .register(request).request(MediaType.APPLICATION_JSON_TYPE)
                .get(RemoteLoginReply.class);
        assertNotNull(negativeReply);
        assertEquals(negativeReply.getOutcome(), 0);
        assertNull(negativeReply.getWifiConfiguration());
    }

    // ignored due to some HttpServletRequest issue
    @Test
    @Ignore
    public void testExistingTrustedUser() {
        RemoteLoginReply negativeReply = target("login/4567890").request(
                MediaType.APPLICATION_JSON_TYPE).get(RemoteLoginReply.class);
        assertNotNull(negativeReply);
        assertEquals(negativeReply.getOutcome(), 1);
        assertNotNull(negativeReply.getWifiConfiguration());
    }

}
