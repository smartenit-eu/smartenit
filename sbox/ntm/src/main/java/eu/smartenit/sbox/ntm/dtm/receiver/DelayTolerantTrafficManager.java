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
package eu.smartenit.sbox.ntm.dtm.receiver;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.LinkID;
import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.LocalVectorValue;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.ntm.dtm.DAOFactory;

/**
 * Uses {@link NetConfWrapper} to execute operations related with delay tolerant
 * traffic management requested by the {@link DTMTrafficManager}.
 * 
 * @author Lukasz Lopatowski
 * @version 3.1
 */
public class DelayTolerantTrafficManager {

	private static final Logger logger = LoggerFactory.getLogger(DelayTolerantTrafficManager.class);
	
	private List<SimpleLinkID> linksWithDeactivatedFilters = new ArrayList<>();
	
	/**
	 * Sets bandwidth limits for hierarchical policer to be used during next
	 * accounting period. Target router and interface details with new bandwidth
	 * limit values are retrieved from the given reference vector.
	 * 
	 * @param rVector
	 *            reference vector value
	 */
	public void updatePolicerLimitsOnLinks(LocalRVector rVector) {
		for(LocalVectorValue vectorValue : rVector.getVectorValues()) {
			Link link = DAOFactory.getLinkDAOInstance().findById((SimpleLinkID)vectorValue.getLinkID());
			BGRouter bgRouter = link.getBgRouter();
			logger.debug("Updating policer bandwidth limits on interface {} @router {} for R vector value {}", 
					link.getPhysicalInterfaceName(),
					bgRouter.getManagementAddress().getPrefix(),
					vectorValue.getValue());
			boolean success = NetConfWrapper.build(bgRouter)
					.updatePolicerConfig(
							link.getPhysicalInterfaceName(), 
							calculateBandwidthLimit(vectorValue.getValue(), link.getPolicerBandwidthLimitFactor()));
			if (!success)
				logger.warn("Configuration action was not executed correctly.");
		}
	}

	/**
	 * Recalculates R vector component value to bandwidth in bits per second.
	 * 
	 * @param value
	 *            in bytes per sampling period (i.e. 300 seconds)
	 * @param factor
	 *            policer bandwidth limit factor
	 * @return recalculated value in bits per second
	 */
	private long calculateBandwidthLimit(long value, double factor) {
		if (factor == 0) factor = 1;
		return (long)((value * 8 / DAOFactory.getTSPDAOInstance().findLast().getSamplingPeriod()) * factor);
	}

	/**
	 * Updates locally stored list of links for which filters should be
	 * deactivated and triggers proper operations on routers using
	 * {@link NetConfWrapper}.
	 * 
	 * @param links
	 *            list of inter-domain links
	 */
	public void deactivateFiltersOnGivenLinks(List<LinkID> links) {
		for(LinkID linkID : links) {
			SimpleLinkID simpleLinkID = (SimpleLinkID)linkID;
			if (!linksWithDeactivatedFilters.contains(simpleLinkID)) {
				Link link = DAOFactory.getLinkDAOInstance().findById(simpleLinkID);
				logger.debug("Deacvtivating filter on link {}", simpleLinkID);
				boolean success = NetConfWrapper
						.build(link.getBgRouter())
						.deactivateHierarchicalFilter(link.getPhysicalInterfaceName());
				if (!success)
					logger.warn("Configuration action was not executed correctly.");
				else
					linksWithDeactivatedFilters.add(simpleLinkID);
			}
		}
	}
	
	/**
	 * Activates filters on all links on which they have been deactivated.
	 * 
	 */
	public void activateFiltersOnAllLinks() {
		for (SimpleLinkID simpleLinkID : linksWithDeactivatedFilters) {
			Link link = DAOFactory.getLinkDAOInstance().findById(simpleLinkID);
			logger.debug("Activating filter on link {}", simpleLinkID);
			boolean success = NetConfWrapper
					.build(link.getBgRouter())
					.activateHierarchicalFilter(link.getPhysicalInterfaceName());
			if (!success)
				logger.warn("Configuration action was not executed correctly.");
		}
		linksWithDeactivatedFilters = new ArrayList<>();
	}
	
}
