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
package eu.smartenit.sbox.db.dao;

import java.util.List;
import java.util.Properties;

import eu.smartenit.sbox.db.dao.gen.AbstractASDAO;
import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.CloudDC;
import eu.smartenit.sbox.db.dto.Link;

import org.skife.jdbi.v2.DBI;
import org.sqlite.SQLiteConfig;

/**
 * The ASDAO class.
 *
 * @authors Antonis Makris, George Petropoulos
 * @version 1.0
 * 
 */
public class ASDAO {
	final AbstractASDAO dao;

	/**
	 * The constructor.
	 */
	public ASDAO() {
		Properties connectionProperties = new Properties();
		SQLiteConfig config = new SQLiteConfig();
		config.enforceForeignKeys(true);
		connectionProperties = config.toProperties(); 
		DBI dbi = new DBI(DbConstants.DBI_URL, connectionProperties);
		dao = dbi.onDemand(AbstractASDAO.class);
	}

	/**
	 * The method that creates the AS table.
	 */
	public void createTable() {
		dao.createTable();
	}

	/**
	 * The method that deletes the AS table.
	 */
	public void deleteTable() {
		dao.deleteTable();
	}

	/**
	 * The method that inserts an AS into the AS table.
	 * 
	 * @param as The AS to be inserted.
	 */
	public void insert(AS as) throws Exception {
		dao.insert(as);
	}

	/**
	 * The method that finds all the stored ASes from the AS table.
	 * 
	 * @return The list of stored ASes.
	 */
	public List<AS> findAll() {
		return dao.findAll();
	}

	/**
	 * The method that finds all the stored local ASes from the AS table.
	 * 
	 * @return The list of stored local ASes.
	 */
	public List<AS> findLocalAs() {
		return dao.findLocalAs();
	}

	/**
	 * The method that finds all the stored remote ASes from the AS table.
	 * 
	 * @return The list of stored remote ASes.
	 */
	public List<AS> findRemoteAs() {
		return dao.findRemoteAs();
	}

	/**
	 * The method that deletes all the stored ASes from the AS table.
	 * 
	 */
	public void deleteAll() {
		dao.deleteAll();
	}

	/**
	 * The method that finds the last stored AS from the AS table.
	 * 
	 * @return The last stored AS.
	 */
	public AS findLast() {
		AS t = null;
		List<AS> list = findAll();
		if (list.size() != 0 && list != null)
			t = list.get(list.size() - 1);
		return t;
	}

	/**
	 * The method that updates a stored AS into the AS table.
	 * 
	 * @param as The AS to be updated.
	 */
	public void update(AS as) {
		dao.update(as);
	}

	/**
	 * The method that finds a stored AS by its AS number.
	 * 
	 * @param asNo The AS number.
	 */
	public AS findByAsNumber(int asNo) {
		return dao.findById(asNo);
	}
	
	/**
	 * The method that deletes a stored AS by its AS number.
	 * 
	 * @param asNo The AS number.
	 */
	public void deleteById(int asNo) {
		dao.deleteById(asNo);
	}
	
	/**
	 * The method that finds all stored ASes, along with their attached BG Routers and 
	 * inter-domain links, with their traversing tunnels.
	 * 
	 * @return The list of ASes.
	 */
	public List<AS> findAllAsesCloudsBgRoutersLinks () {
		List<AS> ases = dao.findAll();
		BGRouterDAO bgdao = new BGRouterDAO();
		LinkDAO ldao = new LinkDAO();
		CloudDCDAO cdao = new CloudDCDAO();
		for (AS as : ases) {
			//finding local clouds
			List<CloudDC> clouds = cdao.findByASNumber(as.getAsNumber());
			as.setLocalClouds(clouds);
			
			//finding bg routers 
			List<BGRouter> bgRouters = bgdao.findByASNumber(as.getAsNumber());
			for (BGRouter bg : bgRouters) {
				//finding their interdomain links
				List<Link> interDomainLinks = 
						ldao.findByBGRouterAddress(bg.getManagementAddress().getPrefix());
				//assigning their bg router
				for (Link l : interDomainLinks) {
					l.setBgRouter(bg);
				}
				bg.setInterDomainLinks(interDomainLinks);
			}
			as.setBgRouters(bgRouters);
		}
		return ases;
	}
	
	/**
	 * The method that finds an AS by its AS number, along with its attached BG Routers and 
	 * inter-domain links, with their traversing tunnels.
	 * 
	 * @return The AS.
	 */
	public AS findASCloudsBgRoutersLinksByASNumber(int asNumber) {
		AS as = dao.findById(asNumber);
		BGRouterDAO bgdao = new BGRouterDAO();
		LinkDAO ldao = new LinkDAO();
		CloudDCDAO cdao = new CloudDCDAO();
		// finding local clouds
		List<CloudDC> clouds = cdao.findByASNumber(asNumber);
		as.setLocalClouds(clouds);

		// finding bg routers
		List<BGRouter> bgRouters = bgdao.findByASNumber(asNumber);
		for (BGRouter bg : bgRouters) {
			// finding their interdomain links
			List<Link> interDomainLinks = ldao.findByBGRouterAddress(bg
					.getManagementAddress().getPrefix());
			// assigning their bg router
			for (Link l : interDomainLinks) {
				l.setBgRouter(bg);
			}
			bg.setInterDomainLinks(interDomainLinks);
		}
		as.setBgRouters(bgRouters);
		return as;
	}

}
