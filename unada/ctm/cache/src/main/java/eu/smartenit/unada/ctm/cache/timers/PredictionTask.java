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
package eu.smartenit.unada.ctm.cache.timers;

import java.util.List;

import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.UNaDaConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.ctm.cache.CacheManager;
import eu.smartenit.unada.ctm.cache.impl.CacheManagerImpl;
import eu.smartenit.unada.ctm.cache.util.OverlayFactory;
import eu.smartenit.unada.ctm.cache.util.SocialFactory;
import eu.smartenit.unada.db.dto.Content;

/**
 * The PredictionTask class. It initializes the social and overlay 
 * prediction algorithms.
 * 
 * @author George Petropoulos
 * @version 2.1
 * 
 */
public class PredictionTask implements Runnable {

	private static final Logger logger = LoggerFactory
			.getLogger(PredictionTask.class);
	
	private CacheManager cacheManager;

    public PredictionTask() {

    }

	public void run() {
		predict();
	}

    private void predict() {
        logger.info("Running prediction tasks.");
        List<Content> overlayContentList = null;
        List<Content> socialContentList = null;

        //getting unada configuration from db to check whether predictions are enabled
        UNaDaConfiguration uNaDaConfiguration = DAOFactory.getuNaDaConfigurationDAO().findLast();

        // Get sorted overlay prediction list
        if (uNaDaConfiguration.isOverlayPredictionEnabled()) {
            logger.info("Executing overlay prediction.");
            overlayContentList = OverlayFactory.getOverlayManager().getPrediction();
        }

        // Get sorted social prediction list
        if (uNaDaConfiguration.isSocialPredictionEnabled()) {
            logger.info("Executing social prediction.");
            socialContentList = SocialFactory.getSocialAnalyzer().predicting();
        }

        cacheManager = new CacheManagerImpl();
        cacheManager.updateCache(socialContentList, overlayContentList);
        logger.info("Prediction task ended.");
    }

}
