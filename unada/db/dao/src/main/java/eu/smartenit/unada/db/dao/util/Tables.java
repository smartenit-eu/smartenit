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
package eu.smartenit.unada.db.dao.util;

public class Tables {
	
	public void createTables() {
		
		DAOFactory.getCacheDAO().createTable();
		
		DAOFactory.getContentDAO().createTable();
		
		DAOFactory.getContentAccessDAO().createTable();

        DAOFactory.getFeedItemDAO().createTable();

        DAOFactory.getFriendDAO().createTable();
		
		DAOFactory.getOwnerDAO().createTable();
		
		DAOFactory.getSocialPredictionParametersDAO().createTable();
		
		DAOFactory.getTrustedUserDAO().createTable();
		
		DAOFactory.getuNaDaConfigurationDAO().createTable();
		
		DAOFactory.getVideoInfoDAO().createTable();

        DAOFactory.getSocialScoresDAO().createTable();
	}

    public void deleteTables() {

        DAOFactory.getCacheDAO().deleteTable();

        DAOFactory.getContentDAO().deleteTable();

        DAOFactory.getContentAccessDAO().deleteTable();

        DAOFactory.getFeedItemDAO().deleteTable();

        DAOFactory.getFriendDAO().deleteTable();

        DAOFactory.getOwnerDAO().deleteTable();

        DAOFactory.getSocialPredictionParametersDAO().deleteTable();

        DAOFactory.getTrustedUserDAO().deleteTable();

        DAOFactory.getuNaDaConfigurationDAO().deleteTable();

        DAOFactory.getVideoInfoDAO().deleteTable();

        DAOFactory.getSocialScoresDAO().deleteTable();
    }

}
