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
package eu.smartenit.sbox.eca;

import eu.smartenit.sbox.db.dao.CostFunctionDAO;
import eu.smartenit.sbox.db.dao.SystemControlParametersDAO;
import eu.smartenit.sbox.db.dao.TimeScheduleParametersDAO;


/**
 * Static factory class used to obtain an instance of specific DAO class.
 * Following DAO classes are supported: {@link CostFunctionDAO}, {link
 * {@link TimeScheduleParametersDAO} and {@link SystemControlParametersDAO}.
 * 
 * @author D. Doenni, K. Bhargav, T. Bocek
 * @version 1.0
 * 
 */
public class DAOFactory {

	private static CostFunctionDAO costFunctionDAO = new CostFunctionDAO();
	private static SystemControlParametersDAO scpDAO = new SystemControlParametersDAO();
	private static TimeScheduleParametersDAO tspDAO = new TimeScheduleParametersDAO();
	
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
	 * Returns locally stored instance of {@link SystemControlParametersDAO}.
	 * Specific DAO instance can be set using appropriate static setter method.
	 * 
	 * @return locally stored {@link SystemControlParametersDAO} instance
	 */
	public static SystemControlParametersDAO getSCPDAOInstance() {
		return scpDAO;
	}
	
	/**
	 * Returns locally stored instance of {@link TimeScheduleParametersDAO}.
	 * Specific DAO instance can be set using appropriate static setter method.
	 * 
	 * @return locally stored {@link TimeScheduleParametersDAO} instance
	 */
	public static TimeScheduleParametersDAO getTSPDAOInstance() {
		return tspDAO;
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
	 * Method used to set a given instance of {@link SystemControlParametersDAO}
	 * to be returned on each request.
	 * 
	 * @param dao
	 *            specific instance of {@link SystemControlParametersDAO}
	 */
	public static void setSCPDAOInstance(SystemControlParametersDAO dao) {
		scpDAO = dao;
	}

	/**
	 * Method used to set a given instance of {@link TimeScheduleParametersDAO}
	 * to be returned on each request.
	 * 
	 * @param dao
	 *            specific instance of {@link TimeScheduleParametersDAO}
	 */
	public static void setTSPDAOInstance(TimeScheduleParametersDAO dao) {
		tspDAO = dao;
	}
	
}

