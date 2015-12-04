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
package eu.smartenit.sbox.qoa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.Link;

/**
 * Used to store information about routers and links on per AS number basis.
 * Provides specific query methods to retrieve information.
 * 
 * @author Jakub Gutkowski
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public class MonitoredLinksInventory {
	
	private Map<Integer, List<BGRouter>> asBGRouters = new HashMap<Integer, List<BGRouter>>();
	private Map<Integer, List<Link>> asMonitoredLinks = new HashMap<Integer, List<Link>>();
	
	/**
	 * Method to be called to populate inventory with monitored link information
	 * that is obtained from list of local {@link AS}s.
	 * 
	 * @param systems
	 *            list of {@link AS} instances carrying information on BG
	 *            routers and monitored inter-domain links
	 */
	public void populate(List<AS> systems) {
		for(AS as : systems) {
			asBGRouters.put(as.getAsNumber(), as.getBgRouters());
			List<Link> asLinks = new ArrayList<Link>();
			for(BGRouter bgRouter : as.getBgRouters())
				asLinks.addAll(bgRouter.getInterDomainLinks());			
			asMonitoredLinks.put(as.getAsNumber(), asLinks);
		}
	}
	
	/**
	 * Retrieves all inter-domain links for given local AS.
	 * 
	 * @param asNumber
	 *            number of the local AS
	 * @return list of {@link Link}s
	 */
	public List<Link> getLinks(int asNumber) {
		return asMonitoredLinks.get(asNumber);
	}
	
	/**
	 * Retrieves all links from given BG router.
	 * 
	 * @param bgRouter
	 *            {@link BGRouter} instance
	 * @return list of {@link Link}s
	 */
	public List<Link> getLinks(BGRouter bgRouter) {
		return bgRouter.getInterDomainLinks();
	}
	
	/**
	 * Retrieves all BG routers stored in the structure.
	 * 
	 * @return list of {@link BGRouter}s from all local ASs
	 */
	public List<BGRouter> getBGRouters() {
		List<BGRouter> bgRouters = new ArrayList<BGRouter>();
		for(Integer asNumber : asBGRouters.keySet())
			bgRouters.addAll(asBGRouters.get(asNumber));
		return bgRouters;
	}

	/**
	 * Retrieves all BG routers from given local AS.
	 * 
	 * @param asNumber
	 *            number of the local AS
	 * @return list of {@link BGRouter}s from given AS
	 */
	public List<BGRouter> getBGRoutersByAsNumber(int asNumber) {
		return asBGRouters.get(asNumber);
	}
	
	/**
	 * Returns number of the AS to which given BG router belongs.
	 * 
	 * @param bgRouter
	 *            {@link BGRouter} instance
	 * @return number of the AS
	 */
	public int getAsNumber(BGRouter bgRouter) {
		for(Integer asNumber : asBGRouters.keySet()) {
			if(asBGRouters.get(asNumber).contains(bgRouter)) 
				return asNumber;
		}
		return 0;
	}
	
	/**
	 * Returns numbers of all local ASs stored in the structure.
	 * 
	 * @return list of AS numbers
	 */
	public Set<Integer> getAllAsNumbers() {
		return asMonitoredLinks.keySet();
	}
	
}
