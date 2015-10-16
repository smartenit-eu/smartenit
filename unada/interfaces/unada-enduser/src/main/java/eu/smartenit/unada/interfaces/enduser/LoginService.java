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
package eu.smartenit.unada.interfaces.enduser;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.RemoteLoginReply;
import eu.smartenit.unada.db.dto.TrustedUser;
import eu.smartenit.unada.db.dto.UNaDaConfiguration;
import eu.smartenit.unada.commons.commands.ARP;

import java.util.Date;

/**
 * The login REST API of uNaDa towards the end-user device. It listens to URL:
 * http://<unada-ip-address>:<unada-port>/unada/rest/login/<facebookID> for
 * end-user application login requests.
 * 
 * @author George Petropoulos
 * @version 2.0
 */
@Path("/login")
public class LoginService {

    private static final Logger logger = LoggerFactory
            .getLogger(LoginService.class);

    /**
     * The login method. It checks whether the user is a trusted one. If yes,
     * then it executes an ARP request and updates its entry with his MAC
     * address and finally returns the private SSID parameters (SSID name and
     * password). Otherwise, it returns a negative reply.
     * 
     * 
     * @param facebookID
     *            The user's facebook identifier
     * @param request
     *            The http servlet request
     * 
     * @return The RemoteLoginReply object
     * 
     */
    @GET
    @Path("/{facebookID}")
    @Produces(MediaType.APPLICATION_JSON)
    public RemoteLoginReply login(@PathParam("facebookID") String facebookID,
            @Context HttpServletRequest request) {

        RemoteLoginReply reply = new RemoteLoginReply();
        reply.setType("remoteLoginReply");

        TrustedUser trustedUser = DAOFactory.getTrustedUserDAO().findById(
                facebookID);
        if (trustedUser != null) {
            String ipAddress = request.getRemoteAddr();
            logger.info("Login attempt by trusted user with id " + facebookID
                    + " and ip address " + ipAddress);
            reply.setOutcome(1);
            UNaDaConfiguration unadaConfiguration = DAOFactory
                    .getuNaDaConfigurationDAO().findLast();
            if (unadaConfiguration != null) {
                reply.setWifiConfiguration(unadaConfiguration.getPrivateWifi());
            }

            String macAddress = ARP.getArpInstance().execute(ipAddress);
            trustedUser.setMacAddress(macAddress);
            trustedUser.setLastAccess(new Date(System.currentTimeMillis()));
            DAOFactory.getTrustedUserDAO().update(trustedUser);
        } else {
            logger.info("Login attempt by non-trusted user with id "
                    + facebookID);
            reply.setOutcome(0);
        }

        return reply;
    }

}
