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
package eu.smartenit.sbox.qoa.experiment;

import java.util.List;

import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.Tunnel;
import eu.smartenit.sbox.qoa.CounterCollectorThread;
import eu.smartenit.sbox.qoa.CounterValues;

/**
 * Extends {@link CounterCollectorThread} to enable writing traffic details to file.
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a
 *         href="http://psnc.pl">PSNC</a>)
 * @version 1.2
 * 
 */
public class ExtendedCounterCollectorThread extends CounterCollectorThread {

	/**
	 * The constructor with arguments to be used when monitoring BG router.
	 * 
	 * @param links
	 *            list of links of a given BG router that will be monitored
	 * @param tunnels
	 *            list of tunnels of a given BG router that will be monitored
	 * @param bgRouter
	 *            target {@link BGRouter}
	 */
	public ExtendedCounterCollectorThread(List<Link> links,
			List<Tunnel> tunnels, BGRouter bgRouter) {
		super(links, tunnels, bgRouter);
	}

	/**
	 * The constructor with arguments to be used when monitoring DA router.
	 * 
	 * @param tunnels
	 *            list of tunnels of a given DA router that will be monitored
	 * @param daRouter
	 *            target {@link DARouter}
	 */
	public ExtendedCounterCollectorThread(List<Tunnel> tunnelsByDARouter,
			DARouter daRouter) {
		super(tunnelsByDARouter, daRouter);
	}

	@Override
	protected CounterValues collectCounterValuesForBGRouter() throws Exception {
		final ExtendedCounterValues counterValues = new ExtendedCounterValues();
		if(links != null) {
			for (Link link : links) {
				counterValues.storeCounterValue(link.getLinkID(), getInboundLinkCounterValueFromBGRouter(link));
				final ReceivedPackets receivedPackets = new ReceivedPackets(
					getCounterValueFromBGRouter(link.getInboundInterfaceCounterOID_UcastPkts()),
					getCounterValueFromBGRouter(link.getInboundInterfaceCounterOID_MulticastPkts()),
					getCounterValueFromBGRouter(link.getInboundInterfaceCounterOID_BroadcastPkts()));
				counterValues.storeReceivedPackets(link.getLinkID(), receivedPackets);
			}
		}
		if(tunnels != null) {
			for (Tunnel tunnel : tunnels) {
				counterValues.storeCounterValue(tunnel.getTunnelID(), getInboundTunnelCounterValueFromBGRouter(tunnel));
				final ReceivedPackets receivedPackets = new ReceivedPackets(
					getCounterValueFromBGRouter(tunnel.getInboundInterfaceCounterOID_UcastPkts()),
					getCounterValueFromBGRouter(tunnel.getInboundInterfaceCounterOID_MulticastPkts()),
					getCounterValueFromBGRouter(tunnel.getInboundInterfaceCounterOID_BroadcastPkts()));
				counterValues.storeReceivedPackets(tunnel.getTunnelID(), receivedPackets);
			}
		}
		return counterValues;
	}
	
	@Override
	protected CounterValues collectCounterValuesForDARouter() throws Exception {
		final ExtendedCounterValues counterValues = new ExtendedCounterValues();
		if(tunnels != null) {
			for (Tunnel tunnel : tunnels) {
				counterValues.storeCounterValue(tunnel.getTunnelID(), getInboundTunnelCounterValueFromDARouter(tunnel));
				final ReceivedPackets receivedPackets = new ReceivedPackets(
						getCounterValueFromDARouter(tunnel.getInboundInterfaceCounterOID_UcastPkts()),
						getCounterValueFromDARouter(tunnel.getInboundInterfaceCounterOID_MulticastPkts()),
						getCounterValueFromDARouter(tunnel.getInboundInterfaceCounterOID_BroadcastPkts()));
					counterValues.storeReceivedPackets(tunnel.getTunnelID(), receivedPackets);
			}
		}
		return counterValues;
	}
	
	private long getCounterValueFromBGRouter(String oid) throws Exception {
		long value = -1;
		value = parseCounterValue(
				snmpWrapper.snmpGet(snmpWrapper.prepareOID(oid), 
				snmpWrapper.prepareTarget(bgRouter.getManagementAddress().getPrefix(), bgRouter.getSnmpCommunity())));
		return value;
	}
	
	private long getCounterValueFromDARouter(String oid) {
		long value = -1;
		value = parseCounterValue(
				snmpWrapper.snmpGet(snmpWrapper.prepareOID(oid), 
				snmpWrapper.prepareTarget(daRouter.getManagementAddress().getPrefix(), daRouter.getSnmpCommunity())));
		return value;
	}
}
