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

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.SocialPredictionParameters;

/**
 * The SocialBean class. It handles the social.xhtml page.
 * 
 * @author George Petropoulos
 * @version 2.0
 * 
 */
@ManagedBean
@RequestScoped
public class SocialBean {

    private static final Logger logger = LoggerFactory
            .getLogger(SocialBean.class);

    private SocialPredictionParameters socialPredictionParameters;

    public SocialPredictionParameters getSocialPredictionParameters() {
        return socialPredictionParameters;
    }

    public void setSocialPredictionParameters(
            SocialPredictionParameters socialPredictionParameters) {
        this.socialPredictionParameters = socialPredictionParameters;
    }

    /**
     * The init() method that initializes the SocialBean. It retrieves the
     * stored social prediction algorithms parameters.
     * 
     */
    @PostConstruct
    public void init() {
        socialPredictionParameters = DAOFactory
                .getSocialPredictionParametersDAO().findLast();
        if (socialPredictionParameters == null) {
            socialPredictionParameters = new SocialPredictionParameters();
        }
    }

    /**
     * The method that updates the social prediction algorithms parameters.
     * 
     * @return It returns to the current page.
     * 
     */
    public String updateSocialParameters() {
        try {
            DAOFactory.getSocialPredictionParametersDAO().insert(
                    socialPredictionParameters);
        } catch (Exception e) {
            logger.error("Error while updating unada's social prediction parameters "
                    + "to the database.");
        }
        return "/social.xhtml?faces-redirect=true";
    }

}