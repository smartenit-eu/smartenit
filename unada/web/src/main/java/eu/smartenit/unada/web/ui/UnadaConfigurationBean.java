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
package eu.smartenit.unada.web.ui;

import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.UNaDaConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 * The UnadaConfigurationBean class. It handles the index.xhtml page.
 * 
 * @author George Petropoulos
 * @version 2.1
 * 
 */
@ManagedBean
@RequestScoped
public class UnadaConfigurationBean {

    private static final Logger logger = LoggerFactory
            .getLogger(UnadaConfigurationBean.class);

    public UNaDaConfiguration unadaConfiguration;

    public UNaDaConfiguration getUnadaConfiguration() {
        return unadaConfiguration;
    }

    public void setUnadaConfiguration(UNaDaConfiguration unadaConfiguration) {
        this.unadaConfiguration = unadaConfiguration;
    }

    /**
     * The init() method that initializes the UnadaConfigurationBean. It
     * retrieves stored uNaDa configuration parameters.
     * 
     */
    @PostConstruct
    public void init() {

        unadaConfiguration = DAOFactory.getuNaDaConfigurationDAO().findLast();
        if (unadaConfiguration == null) {
            unadaConfiguration = new UNaDaConfiguration();
        }
    }

    /**
     * The method that inserts new configuration parameters.
     * 
     * @return It returns to the current page.
     */
    public String updateUnadaConfiguration() {
        try {
            DAOFactory.getuNaDaConfigurationDAO().insert(unadaConfiguration);
            logger.debug("Inserted " + unadaConfiguration);
        } catch (Exception e) {
            logger.error("Error while updating unada's configuration to the database.");
        }
        return null;
    }

}
