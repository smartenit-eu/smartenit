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
package eu.smartenit.unada.web.ui;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.Version;
import com.restfb.types.User;

import eu.smartenit.unada.commons.commands.ARP;
import eu.smartenit.unada.commons.constants.UnadaConstants;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Owner;
import eu.smartenit.unada.db.dto.TrustedUser;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * The UnadaSessionBean class. It handles the facebook login and access token
 * retrieval.
 * 
 * @author George Petropoulos
 * @version 3.1
 * 
 */
@ManagedBean
@SessionScoped
public class UnadaSessionBean {

    private static final Logger logger = LoggerFactory
            .getLogger(UnadaSessionBean.class);

    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The init() method that initializes the ConfigurationBean. It checks for
     * valid sessions, retrieves user's token and checks whether he is the
     * machine owner and also retrieves stored uNaDa configuration parameters.
     * 
     */
    @PostConstruct
    public void init() {
        HttpServletRequest req = (HttpServletRequest) FacesContext
                .getCurrentInstance().getExternalContext().getRequest();

        boolean validSession = req.getRequestedSessionId() != null
                && !req.isRequestedSessionIdValid();

        boolean noFacebook = false;
        try {
            noFacebook = (Boolean) FacesContext.getCurrentInstance()
                    .getExternalContext().getApplicationMap().get("noFacebook");
            if (noFacebook) {
                name = "Admin";
                return;
            }
        } catch (Exception e) {
            // do nothing
        }

        // check if session has expired
        if (!validSession) {
            String error = req.getParameter("error_reason");
            if (error != null) {
                redirectToLoginPage();
            }

            String code = req.getParameter("code");
            if (code != null) {
                String token = null;
                try {
                    token = retrieveToken(code);
                } catch (Exception e) {
                    logger.error("Error while retrieving token: "
                            + e.getMessage());
                    redirectToLoginPage();
                }
                if (token != null) {
                    Owner owner = DAOFactory.getOwnerDAO().findLast();
                    Owner currentUser = getOwner(token);

                    if (owner == null) {
                        logger.info("Currently there is no owner for this uNaDa.");
                        try {
                            // insert owner of this unada
                            getExtendedToken(currentUser);
                            DAOFactory.getOwnerDAO().insert(currentUser);

                            // add owner as trusted user and update MAC address
                            TrustedUser trustedUser = new TrustedUser();
                            trustedUser.setFacebookID(currentUser
                                    .getFacebookID());
                            String ipAddress = req.getRemoteAddr();
                            trustedUser.setMacAddress(ARP.getArpInstance()
                                    .execute(ipAddress));
                            DAOFactory.getTrustedUserDAO().insert(trustedUser);

                        } catch (Exception e) {
                            logger.error("Error while inserting new owner and trusted user: "
                                    + e.getMessage());
                        }
                    } else {
                        logger.info("Current owner id = "
                                + owner.getFacebookID());
                        if (owner.getFacebookID().equals(
                                currentUser.getFacebookID())) {
                            logger.info("Existing owner successfully logins to the uNaDa. "
                                    + "Updating his token.");
                            getExtendedToken(currentUser);
                            DAOFactory.getOwnerDAO().update(currentUser);
                            logger.debug("Updated token = "
                                    + currentUser.getOauthToken());

                        } else {
                            redirectToLoginPage();
                        }
                    }
                } else {
                    redirectToLoginPage();
                }
            } else {
                redirectToLoginPage();
            }
        }
    }

    /**
     * The method that gets an authentication token and retrieves the user's
     * data.
     * 
     * @param token
     *            The authentication token
     * @return The owner object, including user's data.
     */
    private Owner getOwner(String token) {
        Owner owner = new Owner();
        FacebookClient facebookClient = new DefaultFacebookClient(token);

        User user = facebookClient.fetchObject("me", User.class);
        name = user.getName();
        logger.debug("User " + name + " tries to login. ");
        logger.debug("Short lived token = " + token);

        owner.setFacebookID(user.getId());
        owner.setOauthToken(token);
        return owner;
    }

    private void getExtendedToken(Owner owner) {
        FacebookClient facebookClient = new DefaultFacebookClient(
                owner.getOauthToken(), Version.VERSION_2_1);
        AccessToken extendedAccessToken = facebookClient
                .obtainExtendedAccessToken(UnadaConstants.APP_ID,
                        UnadaConstants.APP_SECRET, owner.getOauthToken());
        String extendedToken = extendedAccessToken.getAccessToken();

        owner.setFacebookID(owner.getFacebookID());
        owner.setOauthToken(extendedToken);

        logger.debug("Long lived token = " + extendedToken);
    }

    /**
     * The method that retrieves user's authentication token from the Facebook
     * API.
     * 
     * @param code
     *            The HTTP response code.
     * @return The user's authentication token. Null if there is an error.
     */
    private String retrieveToken(String code) throws Exception {
        String token = null;
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(
                "https://graph.facebook.com/oauth/access_token");

        try {

            String[][] parameters = {
                    { "client_id", UnadaConstants.APP_ID },
                    { "client_secret", UnadaConstants.APP_SECRET },
                    { "redirect_uri",
                            UnadaConstants.REDIRECT_URI + "/unada/index.xhtml" },
                    { "code", code } };

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

            for (int i = 0; i < parameters.length; i++) {
                nameValuePairs.add(new BasicNameValuePair(parameters[i][0],
                        parameters[i][1]));
            }

            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse resp = client.execute(post);
            BufferedReader rd = new BufferedReader(new InputStreamReader(resp
                    .getEntity().getContent()));

            String message = "";
            String lineData;
            while ((lineData = rd.readLine()) != null) {
                message += lineData;
            }

            // Add more safety traps
            String[] params = message.split("&");
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    if (params[i].contains("access_token")) {
                        String[] B = params[i].split("=");
                        if (B != null) {
                            token = B[1];
                        }
                        break;
                    }
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Exception while parsing token: " + e.getMessage());
            return null;
        }
        return token;
    }

    /**
     * The method that invalidates the session and redirects to login page.
     * 
     */
    private void redirectToLoginPage() {
        try {
            FacesContext.getCurrentInstance().getExternalContext()
                    .invalidateSession();
            ((HttpServletResponse) FacesContext.getCurrentInstance()
                    .getExternalContext().getResponse())
                    .sendRedirect("login.xhtml");
        } catch (Exception e) {
            logger.error("Exception while redirecting to login page: "
                    + e.getMessage());
        }
    }

    /**
     * The method that invalidates the user's session and logs him out the uNaDa
     * web application.
     * 
     * @return It returns to login page.
     */
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext()
                .invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }
}
