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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.commons.constants.UnadaConstants;

/**
 * The LoginBean class. It handles the login.xhtml page.
 * 
 * @author George Petropoulos
 * @version 2.0
 * 
 */
@ManagedBean
@RequestScoped
public class LoginBean {

    private static final Logger logger = LoggerFactory
            .getLogger(LoginBean.class);

    private String overlayStatus;

    public String getOverlayStatus() {
        switch (UnadaConstants.OVERLAY_STATUS) {
        case 0:
            overlayStatus = "OK";
            break;
        case 1:
            overlayStatus = "CONNECTING";
            break;
        case 2:
            overlayStatus = "ERROR";
            break;
        case 3:
            overlayStatus = "INITIALING";
            break;
        }
        return overlayStatus;
    }

    public void setOverlayStatus(String overlayStatus) {
        this.overlayStatus = overlayStatus;
    }

    /**
     * The login listener method, that handles the Facebook login. It redirects
     * to the Facebook API for further authentication.
     * 
     * @param event
     *            The action event received from the login page.
     */
    public void login(ActionEvent event) {

        final String furl = "https://www.facebook.com/dialog/oauth?"
                + "client_id=" + UnadaConstants.APP_ID + "&" + "redirect_uri="
                + UnadaConstants.REDIRECT_URI + "/unada/index.xhtml&"
                + "scope=" + UnadaConstants.PERMISSIONS + "&"
                + "response_type=code";

        HttpServletResponse response = (HttpServletResponse) FacesContext
                .getCurrentInstance().getExternalContext().getResponse();

        try {
            FacesContext.getCurrentInstance().getExternalContext()
                    .getApplicationMap().put("noFacebook", false);
            response.sendRedirect(furl);
        } catch (Exception e) {
            logger.error("Error while loggin into Facebook API: "
                    + e.getMessage());
        }
    }

    public String loginNoFacebook() {
        FacesContext.getCurrentInstance().getExternalContext()
                .getApplicationMap().put("noFacebook", true);
        return "index";
    }
}