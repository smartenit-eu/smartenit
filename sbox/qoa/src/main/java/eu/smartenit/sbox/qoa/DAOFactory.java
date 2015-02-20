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
package eu.smartenit.sbox.qoa;

import eu.smartenit.sbox.db.dao.ASDAO;
import eu.smartenit.sbox.db.dao.DC2DCCommunicationDAO;
import eu.smartenit.sbox.db.dao.SystemControlParametersDAO;
import eu.smartenit.sbox.db.dao.TimeScheduleParametersDAO;

/**
 * Static factory class used to obtain an instance of specific DAO class.
 * Following DAO classes are supported: {@link DC2DCCommunicationDAO},
 * {@link ASDAO} and {@link TimeScheduleParametersDAO}
 * 
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a
 *         href="http://psnc.pl">PSNC</a>)
 * @author <a href="mailto:llopat@man.poznan.pl">Lukasz Lopatowski</a> (<a
 *         href="http://psnc.pl">PSNC</a>)
 * @version 1.0
 * 
 */
public class DAOFactory {
	private static ASDAO asDAO = new ASDAO();
	private static DC2DCCommunicationDAO dc2dcCommunicationDAO = new DC2DCCommunicationDAO();
	private static TimeScheduleParametersDAO timeScheduleParametersDAO = new TimeScheduleParametersDAO();
	private static SystemControlParametersDAO systemControlParametersDAO = new SystemControlParametersDAO();
	
	/**
	 * Method returns an instance of the {@link ASDAO}
	 * 
	 * @return instance of {@link ASDAO}
	 */
	public static ASDAO getASDAOInstance() {
		return asDAO; 
	}
	
	/**
	 * Method returns an instance of the {@link DC2DCCommunicationDAO}
	 * 
	 * @return instance of {@link DC2DCCommunicationDAO}
	 */
	public static DC2DCCommunicationDAO getDCDC2DCCommunicationDAOInstance() {
		return dc2dcCommunicationDAO;
	}
	
	/**
	 * Method returns an instance of the {@link TimeScheduleParametersDAO}
	 * 
	 * @return instance of {@link TimeScheduleParametersDAO}
	 */
	public static TimeScheduleParametersDAO getTimeScheduleParametersDAOInstance() {
		return timeScheduleParametersDAO;
	}
	
	/**
	 * Method sets the locally stored {@link ASDAO} instance to the one
	 * provided. Method is mainly meant to be used in unit testing.
	 * 
	 * @param dao
	 *            specific instance of the {@link ASDAO}
	 */
	public static void setASDAOInstance(ASDAO dao) {
		asDAO = dao;
	}
	
	/**
	 * Method sets the locally stored {@link DC2DCCommunicationDAO} instance to
	 * the one provided. Method is mainly meant to be used in unit testing.
	 * 
	 * @param dao
	 *            specific instance of the {@link DC2DCCommunicationDAO}
	 */
	public static void setDC2DCCommunicationDAO(DC2DCCommunicationDAO dao) {
		dc2dcCommunicationDAO = dao;
	}
	
	/**
	 * Method sets the locally stored {@link TimeScheduleParametersDAO} instance
	 * to the one provided. Method is mainly meant to be used in unit testing.
	 * 
	 * @param dao
	 *            specific instance of the {@link TimeScheduleParametersDAO}
	 */
	public static void setTimeScheduleParametersDAO(TimeScheduleParametersDAO dao) {
		timeScheduleParametersDAO = dao;
	}
	
	/**
	 * Method used to set a given instance of {@link SystemControlParametersDAO}
	 * to be returned on each request.
	 * 
	 * @param dao
	 *            specific instance of {@link SystemControlParametersDAO}
	 */
	public static void setSCPDAOInstance(SystemControlParametersDAO dao) {
		systemControlParametersDAO = dao;
	}
	
	/**
	 * Returns locally stored instance of {@link SystemControlParametersDAO}.
	 * Specific DAO instance can be set using appropriate static setter method.
	 * 
	 * @return locally stored {@link SystemControlParametersDAO} instance
	 */
	public static SystemControlParametersDAO getSCPDAOInstance() {
		return systemControlParametersDAO;
	}
}
