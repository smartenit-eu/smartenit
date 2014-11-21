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
package eu.smartenit.sbox.ntm.dtm;

import eu.smartenit.sbox.db.dao.ASDAO;
import eu.smartenit.sbox.db.dao.DC2DCCommunicationDAO;
import eu.smartenit.sbox.db.dao.LinkDAO;

/**
 * Static factory class used to obtain an instance of specific DAO class.
 * Following DAO classes are supported: {@link DC2DCCommunicationDAO}, 
 * {@link ASDAO} and {@link LinkDAO}.
 * 
 * @author Lukasz Lopatowski
 * @version 1.2
 * 
 */
public class DAOFactory {

	private static ASDAO asDAO = new ASDAO();
	private static DC2DCCommunicationDAO dc2dcCommunicationDAO = new DC2DCCommunicationDAO();
	private static LinkDAO linkDAO = new LinkDAO();
	
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
	public static DC2DCCommunicationDAO getDCDC2DCCommunicationDAOInstance() {
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
	public static void setDC2DCCommunicationDAO(DC2DCCommunicationDAO dao) {
		dc2dcCommunicationDAO = dao;
	}

	/**
	 * Method used to set a given instance of {@link LinkDAO} to be returned on
	 * each request.
	 * 
	 * @param dao
	 *            specific instance of {@link LinkDAO}
	 */
	public static void setLinkDAO(LinkDAO dao) {
		linkDAO = dao;
	}

}
