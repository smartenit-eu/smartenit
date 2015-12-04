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
package eu.smartenit.sbox.qoa.experiment;

import java.util.HashMap;
import java.util.Map;

import eu.smartenit.sbox.db.dto.LinkID;
import eu.smartenit.sbox.db.dto.TunnelID;
import eu.smartenit.sbox.qoa.CounterValues;

/**
 * Extends {@link CounterValues} to enable writing traffic details to file.
 * 
 * @author Lukasz Lopatowski
 * @version 1.2
 */
public class ExtendedCounterValues extends CounterValues {

	private Map<LinkID, ReceivedPackets> linkReceivedPackets = new HashMap<LinkID, ReceivedPackets>();
	private Map<TunnelID, ReceivedPackets> tunnelReceivedPackets = new HashMap<TunnelID, ReceivedPackets>();
	
	
	/**
	 * Method used to store amount of received packets for links.
	 * 
	 * @param linkID
	 * 		identifier of the monitored link
	 * @param packetsInfo
	 * 		{@link ReceivedPackets} object
	 */
	public void storeReceivedPackets(LinkID linkID, ReceivedPackets packetsInfo) {
		linkReceivedPackets.put(linkID, packetsInfo);
	}

	/**
	 * Method used to store amount of received packets for tunnels.
	 * 
	 * @param tunnelID
	 * 		identifier of the monitored tunnel
	 * @param packetsInfo
	 * 		{@link ReceivedPackets} object
	 */
	public void storeReceivedPackets(TunnelID tunnelID, ReceivedPackets packetsInfo) {
		tunnelReceivedPackets.put(tunnelID, packetsInfo);
	}

	/**
	 *  Method used to calculate difference between stored and provided values
	 *  	for all links and tunnels.
	 * 
	 * @param toBeSubtracted
	 * 		{@link ReceivedPackets} object
	 * @return
	 * 		{@link ExtendedCounterValues} object
	 */
	public ExtendedCounterValues calculateDifference(ExtendedCounterValues toBeSubtracted) {
		ExtendedCounterValues result = new ExtendedCounterValues();
		for(LinkID linkID : getAllLinkIds()) {
			result.storeCounterValue(linkID, getCounterValue(linkID) - toBeSubtracted.getCounterValue(linkID));
			result.storeReceivedPackets(linkID, getReceivedPackets(linkID).calculateDifference(toBeSubtracted.getReceivedPackets(linkID)));
		}
		for(TunnelID tunnelID : getAllTunnelsIds()) {
			result.storeCounterValue(tunnelID, getCounterValue(tunnelID) - toBeSubtracted.getCounterValue(tunnelID));
			result.storeReceivedPackets(tunnelID, getReceivedPackets(tunnelID).calculateDifference(toBeSubtracted.getReceivedPackets(tunnelID)));
		}
		
		return result;
	}
	
	@Override
	public void addLinksAndTunnels(CounterValues counterValues) {
		ExtendedCounterValues extendedCounterValues = (ExtendedCounterValues) counterValues;
		
		linkCounterValues.putAll(extendedCounterValues.getLinkCounterValues());
		tunnelCounterValues.putAll(extendedCounterValues.getTunnelCounterValues());
		linkReceivedPackets.putAll(extendedCounterValues.getLinkReceivedPackets());
		tunnelReceivedPackets.putAll(extendedCounterValues.getTunnelReceivedPackets());
	}
	
	/**
	 * Returns map with amount of packets for all links.
	 * 
	 * @return
	 * 		map
	 */
	public Map<LinkID, ReceivedPackets> getLinkReceivedPackets() {
		return this.linkReceivedPackets;
	}
	
	/**
	 * Returns map with amount of packets for all tunnels.
	 * 
	 * @return
	 * 		map
	 */
	public Map<TunnelID, ReceivedPackets> getTunnelReceivedPackets() {
		return this.tunnelReceivedPackets;
	}
	
	public ReceivedPackets getReceivedPackets(LinkID linkID) {
		return linkReceivedPackets.get(linkID);
	}
	
	public ReceivedPackets getReceivedPackets(TunnelID tunnelID) {
		return tunnelReceivedPackets.get(tunnelID);
	}
	
}
