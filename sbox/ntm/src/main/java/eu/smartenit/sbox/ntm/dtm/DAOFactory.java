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
package eu.smartenit.sbox.ntm.dtm;

import eu.smartenit.sbox.db.dao.ASDAO;
import eu.smartenit.sbox.db.dao.DC2DCCommunicationDAO;
import eu.smartenit.sbox.db.dao.LinkDAO;
import eu.smartenit.sbox.db.dao.SystemControlParametersDAO;
import eu.smartenit.sbox.db.dao.TimeScheduleParametersDAO;

/**
 * Static factory class used to obtain an instance of specific DAO class.
 * Following DAO classes are supported: {@link DC2DCCommunicationDAO},
 * {@link ASDAO}, {@link LinkDAO}, {@link SystemControlParametersDAO} and
 * {@link TimeScheduleParametersDAO}.
 * 
 * @author Lukasz Lopatowski
 * @version 3.0
 * 
 */
public class DAOFactory {

	private static ASDAO asDAO = new ASDAO();
	private static DC2DCCommunicationDAO dc2dcCommunicationDAO = new DC2DCCommunicationDAO();
	private static LinkDAO linkDAO = new LinkDAO();
	private static SystemControlParametersDAO scpDAO = new SystemControlParametersDAO();
	private static TimeScheduleParametersDAO tspDAO = new TimeScheduleParametersDAO();
	
	/**
	 * Returns locally stored instance of {@link ASDAO}. Specific DAO instance
	 * can be set using appropriate static setter method.
	 * 
	 * @return locally stored {@link ASDAO} instance
	 */
	public static ASDAO getASDAOInstance() {
		return asDAO; 
	}
	
	/**
	 * Returns locally stored instance of {@link DC2DCCommunicationDAO}.
	 * Specific DAO instance can be set using appropriate static setter method.
	 * 
	 * @return locally stored {@link DC2DCCommunicationDAO} instance
	 */
	public static DC2DCCommunicationDAO getDCDC2DCComDAOInstance() {
		return dc2dcCommunicationDAO;
	}

	/**
	 * Returns locally stored instance of {@link LinkDAO}. Specific DAO instance
	 * can be set using appropriate static setter method.
	 * 
	 * @return locally stored {@link LinkDAO} instance
	 */
	public static LinkDAO getLinkDAOInstance() {
		return linkDAO;
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
	 * Method used to set a given instance of {@link ASDAO} to be returned on
	 * each request.
	 * 
	 * @param dao
	 *            specific instance of {@link ASDAO}
	 */
	public static void setASDAOInstance(ASDAO dao) {
		asDAO = dao;
	}
	
	/**
	 * Method used to set a given instance of {@link DC2DCCommunicationDAO} to
	 * be returned on each request.
	 * 
	 * @param dao
	 *            specific instance of {@link DC2DCCommunicationDAO}
	 */
	public static void setDC2DCComDAOInstance(DC2DCCommunicationDAO dao) {
		dc2dcCommunicationDAO = dao;
	}

	/**
	 * Method used to set a given instance of {@link LinkDAO} to be returned on
	 * each request.
	 * 
	 * @param dao
	 *            specific instance of {@link LinkDAO}
	 */
	public static void setLinkDAOInstance(LinkDAO dao) {
		linkDAO = dao;
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
