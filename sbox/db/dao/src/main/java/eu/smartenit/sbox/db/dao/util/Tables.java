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
package eu.smartenit.sbox.db.dao.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dao.ASDAO;
import eu.smartenit.sbox.db.dao.BGRouterDAO;
import eu.smartenit.sbox.db.dao.CloudDCDAO;
import eu.smartenit.sbox.db.dao.CostFunctionDAO;
import eu.smartenit.sbox.db.dao.DARouterDAO;
import eu.smartenit.sbox.db.dao.DC2DCCommunicationDAO;
import eu.smartenit.sbox.db.dao.ISPDAO;
import eu.smartenit.sbox.db.dao.LinkDAO;
import eu.smartenit.sbox.db.dao.PrefixDAO;
import eu.smartenit.sbox.db.dao.SDNControllerDAO;
import eu.smartenit.sbox.db.dao.SegmentDAO;
import eu.smartenit.sbox.db.dao.SystemControlParametersDAO;
import eu.smartenit.sbox.db.dao.TimeScheduleParametersDAO;
import eu.smartenit.sbox.db.dao.TunnelDAO;

/**
 * The Tables class.
 *
 * @authors George Petropoulos
 * @version 1.0
 * 
 */
public class Tables {
	
	private static final Logger logger = LoggerFactory.getLogger(Tables.class);
	
	/**
	 * The constructor.
	 */
	public Tables () {
		
	}
	
	/**
	 * The method that creates all tables.
	 */
	public void createAll () {
		
		logger.info("Creating all sbox tables.");
		
		ASDAO asdao = new ASDAO();
		asdao.createTable();
		
		BGRouterDAO bgdao = new BGRouterDAO();
		bgdao.createTable();
		
		CloudDCDAO cldao = new CloudDCDAO();
		cldao.createTable();
		
		CostFunctionDAO codao = new CostFunctionDAO();
		codao.createTable();
		
		DARouterDAO dadao = new DARouterDAO();
		dadao.createTable();
		
		DC2DCCommunicationDAO dcdao = new DC2DCCommunicationDAO();
		dcdao.createTable();
		
		ISPDAO idao = new ISPDAO();
		idao.createTable();
		
		LinkDAO ldao = new LinkDAO();
		ldao.createTable();
		
		PrefixDAO pdao = new PrefixDAO();
		pdao.createTable();
		
		SDNControllerDAO sdao = new SDNControllerDAO();
		sdao.createTable();
		
		SegmentDAO segdao = new SegmentDAO();
		segdao.createTable();
		
		SystemControlParametersDAO scdao = new SystemControlParametersDAO();
		scdao.createTable();
		
		TimeScheduleParametersDAO tidao = new TimeScheduleParametersDAO();
		tidao.createTable();
		
		TunnelDAO tudao = new TunnelDAO();
		tudao.createTable();
	}
	
	/**
	 * The method that deletes all tables.
	 */
	public void deleteAll () {
		
		logger.info("Deleting all sbox tables.");
		
		ASDAO asdao = new ASDAO();
		asdao.deleteTable();
		
		BGRouterDAO bgdao = new BGRouterDAO();
		bgdao.deleteTable();
		
		CloudDCDAO cldao = new CloudDCDAO();
		cldao.deleteTable();
		
		CostFunctionDAO codao = new CostFunctionDAO();
		codao.deleteTable();
		
		DARouterDAO dadao = new DARouterDAO();
		dadao.deleteTable();
		
		LinkDAO ldao = new LinkDAO();
		ldao.deleteTable();
		
		DC2DCCommunicationDAO dcdao = new DC2DCCommunicationDAO();
		dcdao.deleteTable();
		
		ISPDAO idao = new ISPDAO();
		idao.deleteTable();
		
		PrefixDAO pdao = new PrefixDAO();
		pdao.deleteTable();
		
		SDNControllerDAO sdao = new SDNControllerDAO();
		sdao.deleteTable();
		
		SegmentDAO segdao = new SegmentDAO();
		segdao.deleteTable();
		
		SystemControlParametersDAO scdao = new SystemControlParametersDAO();
		scdao.deleteTable();
		
		TimeScheduleParametersDAO tidao = new TimeScheduleParametersDAO();
		tidao.deleteTable();
		
		TunnelDAO tudao = new TunnelDAO();
		tudao.deleteTable();
	}

}
