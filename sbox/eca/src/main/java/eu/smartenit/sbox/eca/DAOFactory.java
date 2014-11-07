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
package eu.smartenit.sbox.eca;

import eu.smartenit.sbox.db.dao.CostFunctionDAO;
import eu.smartenit.sbox.db.dao.TimeScheduleParametersDAO;


/**
 * Static factory class used to obtain an instance of specific DAO class.
 * Following DAO classes are supported: {@link CostFunctionDAO}
 * 
 * @author D. Doenni, K. Bhargav, T. Bocek
 * @version 1.0
 * 
 */
public class DAOFactory {

	private static CostFunctionDAO costFunctionDAO = new CostFunctionDAO();
	private static TimeScheduleParametersDAO timeScheduleParametersDAO = new TimeScheduleParametersDAO();
	
	/**
	 * Returns locally stored instance of {@link CostFunctionDAO}. Specific DAO instance
	 * can be set using appropriate static setter method.
	 * 
	 * @return locally stored {@link CostFunctionDAO} instance
	 */
	public static CostFunctionDAO getCostFunctionDAOInstance() {
		return costFunctionDAO; 
	}
	
	/**
	 * Method used to set a given instance of {@link CostFunctionDAO} to be returned on
	 * each request.
	 * 
	 * @param dao
	 *            specific instance of {@link CostFunctionDAO}
	 */
	public static void setCostFunctionDAOInstance(CostFunctionDAO dao) {
		costFunctionDAO = dao;
	}
	
	/**
	 * Returns locally stored instance of {@link TimeScheduleParametersDAO}. Specific DAO instance
	 * can be set using appropriate static setter method.
	 * 
	 * @return locally stored {@link TimeScheduleParametersDAO} instance
	 */
	public static TimeScheduleParametersDAO getTimeScheduleParametersDAO() {
		return timeScheduleParametersDAO;
	}
	
	/**
	 * Method used to set a given instance of {@link TimeScheduleParametersDAO} to be returned on
	 * each request.
	 * 
	 * @param dao specific instance of {@link TimeScheduleParametersDAO}
	 */
	public static void setTimeScheduleParametersDAO(TimeScheduleParametersDAO dao) {
		timeScheduleParametersDAO = dao;
	}
	
}

