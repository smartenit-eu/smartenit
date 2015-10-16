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

import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.*;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * The Periodic6hTask class. It runs some periodic tasks for RBH trials.
 * 
 * @author George Petropoulos
 * @version 3.1
 * 
 */
public class Periodic24hTask implements Runnable {

    private static final Logger logger = LoggerFactory
            .getLogger(Periodic24hTask.class);

    /**
     * The method that executes the thread.
     * 
     */
    public void run() {
        logger.info("Running the 24h periodic task.");
        updateSocialPredictionParameters();
        logger.info("Done.");
    }

    public void updateSocialPredictionParameters() {
        List<SocialScores> socialScores = DAOFactory.getSocialScoresDAO()
                .findAll();

        if (socialScores != null && socialScores.size() >= 6) {
            logger.debug("Executing linear regression.");
            OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
            double[] y = new double[socialScores.size()];
            double[][] x = new double[socialScores.size()][];

            for (int i = 0; i < socialScores.size(); i++) {
                // calculating x
                x[i] = new double[] { socialScores.get(i).getAlpha(),
                        socialScores.get(i).getDelta(),
                        socialScores.get(i).getEta(),
                        socialScores.get(i).getPhi(),
                        socialScores.get(i).getGamma() };
                logger.debug("x = {}", Arrays.toString(x[i]));

                // calculating y
                Content content = DAOFactory.getContentDAO().findById(
                        socialScores.get(i).getContentID());
                List<ContentAccess> accesses = DAOFactory.getContentAccessDAO()
                        .findByContentID(content.getContentID());
                double views = 0;
                if (accesses != null) {
                    views = accesses.size();
                }

                long intervals = (System.currentTimeMillis() - content
                        .getCacheDate().getTime()) / (24 * 60 * 60000);
                double p = 0;
                if (intervals == 0) {
                    p = Math.min(0.99, views);
                } else {
                    p = Math.min(0.99, (double) views / (double) intervals);
                }

                if (p < 0.0000001) {
                    p = 0.0000001;
                }
                logger.debug("Views {}, intervals {}, p {}, content {}", views,
                        intervals, p, content.getContentID());
                y[i] = Math.log(p / (1 - p));
            }
            logger.debug("y {}", Arrays.toString(y));
            regression.newSampleData(y, x);
            double[] beta;
            try {
                beta = regression.estimateRegressionParameters();
                logger.debug("beta = {}", Arrays.toString(beta));
            } catch (Exception e) {
                logger.error("Error calculating new social prediction params.");
                return;
            }
            SocialPredictionParameters social = new SocialPredictionParameters();
            social.setLambda6(beta[0]);
            social.setLambda1(beta[1]);
            social.setLambda2(beta[2]);
            social.setLambda3(beta[3]);
            social.setLambda4(beta[4]);
            social.setLambda5(beta[5]);
            logger.info("Updated social prediction parameters = {} ", social);
            try {
                DAOFactory.getSocialPredictionParametersDAO().insert(social);
            } catch (Exception e) {
                logger.error("Error inserting new social prediction params.");
            }
        }
    }

}
