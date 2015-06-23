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

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.Tunnel;
import eu.smartenit.sbox.qoa.MonitoredLinksInventory;
import eu.smartenit.sbox.qoa.MonitoredTunnelsInventory;
import eu.smartenit.sbox.qoa.OIDValues;
import eu.smartenit.sbox.qoa.SNMPOIDCollector;

/**
 * Extends {@link SNMPOIDCollector} to enable writing traffic details to file.
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a
 *         href="http://psnc.pl">PSNC</a>)
 * @version 1.2
 * 
 */
public class ExtendedSNMPOIDCollector extends SNMPOIDCollector {
	private static final Logger logger = LoggerFactory.getLogger(ExtendedSNMPOIDCollector.class);

	/**
	 * The constructor with arguments.
	 * 
	 * @param links
	 *            links to be monitored
	 * @param tunnels
	 *            tunnels to be monitored
	 */
	public ExtendedSNMPOIDCollector(MonitoredLinksInventory links,
			MonitoredTunnelsInventory tunnels) {
		super(links, tunnels);
	}
	
	@Override
	protected void setOIDsForTunnels(DARouter daRouter) throws IOException {
		logger.info("Setting OIDs for tunnels ...");
		final List<String> interfaces = getIntrefacesForDARouter(daRouter);
		for (Tunnel tunnel : tunnels.getTunnels(daRouter)) {
			tunnel.setInboundInterfaceCounterOID(getInterfaceCounterOID(OIDValues.IF_HC_IN_OCTETS_OID.getValue(), tunnel.getPhysicalLocalInterfaceName(), interfaces));
			tunnel.setOutboundInterfaceCounterOID(getInterfaceCounterOID(OIDValues.IF_HC_OUT_OCTETS_OID.getValue(), tunnel.getPhysicalLocalInterfaceName(), interfaces));
			/* Extended to support packet counters*/
			tunnel.setInboundInterfaceCounterOID_UcastPkts(getInterfaceCounterOID(OIDValues.IF_HC_IN_UCAST_PKTS_OID.getValue(), tunnel.getPhysicalLocalInterfaceName(), interfaces));
			tunnel.setOutboundInterfaceCounterOID_UcastPkts(getInterfaceCounterOID(OIDValues.IF_HC_OUT_UCAST_PKTS_OID.getValue(), tunnel.getPhysicalLocalInterfaceName(), interfaces));
			tunnel.setInboundInterfaceCounterOID_MulticastPkts(getInterfaceCounterOID(OIDValues.IF_HC_IN_MULTICAST_PKTS_OID.getValue(), tunnel.getPhysicalLocalInterfaceName(), interfaces));
			tunnel.setOutboundInterfaceCounterOID_MulticastPkts(getInterfaceCounterOID(OIDValues.IF_HC_OUT_MULTICAST_PKTS_OID.getValue(), tunnel.getPhysicalLocalInterfaceName(), interfaces));
			tunnel.setInboundInterfaceCounterOID_BroadcastPkts(getInterfaceCounterOID(OIDValues.IF_HC_IN_BROADCAST_PKTS_OID.getValue(), tunnel.getPhysicalLocalInterfaceName(), interfaces));
			tunnel.setOutboundInterfaceCounterOID_BroadcastPkts(getInterfaceCounterOID(OIDValues.IF_HC_OUT_BROADCAST_PKTS_OID.getValue(), tunnel.getPhysicalLocalInterfaceName(), interfaces));
		}
	}
	
	@Override
	protected void setOIDsForLinks(BGRouter bgRouter) throws IOException {
		logger.info("Setting OIDs for links ...");
		final List<String> interfaces = getIntrefacesForBGRouter(bgRouter);
		for (Link link : links.getLinks(bgRouter)) {
			link.setInboundInterfaceCounterOID(getInterfaceCounterOID(OIDValues.IF_HC_IN_OCTETS_OID.getValue(), link.getPhysicalInterfaceName(), interfaces));
			link.setOutboundInterfaceCounterOID(getInterfaceCounterOID(OIDValues.IF_HC_OUT_OCTETS_OID.getValue(), link.getPhysicalInterfaceName(), interfaces));
			/* Extended to support packet counters*/
			link.setInboundInterfaceCounterOID_UcastPkts(getInterfaceCounterOID(OIDValues.IF_HC_IN_UCAST_PKTS_OID.getValue(), link.getPhysicalInterfaceName(), interfaces));
			link.setOutboundInterfaceCounterOID_UcastPkts(getInterfaceCounterOID(OIDValues.IF_HC_OUT_UCAST_PKTS_OID.getValue(), link.getPhysicalInterfaceName(), interfaces));
			link.setInboundInterfaceCounterOID_MulticastPkts(getInterfaceCounterOID(OIDValues.IF_HC_IN_MULTICAST_PKTS_OID.getValue(), link.getPhysicalInterfaceName(), interfaces));
			link.setOutboundInterfaceCounterOID_MulticastPkts(getInterfaceCounterOID(OIDValues.IF_HC_OUT_MULTICAST_PKTS_OID.getValue(), link.getPhysicalInterfaceName(), interfaces));
			link.setInboundInterfaceCounterOID_BroadcastPkts(getInterfaceCounterOID(OIDValues.IF_HC_IN_BROADCAST_PKTS_OID.getValue(), link.getPhysicalInterfaceName(), interfaces));
			link.setOutboundInterfaceCounterOID_BroadcastPkts(getInterfaceCounterOID(OIDValues.IF_HC_OUT_BROADCAST_PKTS_OID.getValue(), link.getPhysicalInterfaceName(), interfaces));
		}	
	}
	
	@Override
	protected void setOIDsForTunnels(BGRouter bgRouter) throws IOException {
		logger.info("Setting OIDs for tunnels ...");
		final List<String> interfaces = getIntrefacesForBGRouter(bgRouter);
		for (Tunnel tunnel : tunnels.getTunnels(bgRouter)) {
			tunnel.setInboundInterfaceCounterOID(getInterfaceCounterOID(OIDValues.IF_HC_IN_OCTETS_OID.getValue(), tunnel.getPhysicalLocalInterfaceName(), interfaces));
			tunnel.setOutboundInterfaceCounterOID(getInterfaceCounterOID(OIDValues.IF_HC_OUT_OCTETS_OID.getValue(), tunnel.getPhysicalLocalInterfaceName(), interfaces));
			/* Extended to support packet counters*/
			tunnel.setInboundInterfaceCounterOID_UcastPkts(getInterfaceCounterOID(OIDValues.IF_HC_IN_UCAST_PKTS_OID.getValue(), tunnel.getPhysicalLocalInterfaceName(), interfaces));
			tunnel.setOutboundInterfaceCounterOID_UcastPkts(getInterfaceCounterOID(OIDValues.IF_HC_OUT_UCAST_PKTS_OID.getValue(), tunnel.getPhysicalLocalInterfaceName(), interfaces));
			tunnel.setInboundInterfaceCounterOID_MulticastPkts(getInterfaceCounterOID(OIDValues.IF_HC_IN_MULTICAST_PKTS_OID.getValue(), tunnel.getPhysicalLocalInterfaceName(), interfaces));
			tunnel.setOutboundInterfaceCounterOID_MulticastPkts(getInterfaceCounterOID(OIDValues.IF_HC_OUT_MULTICAST_PKTS_OID.getValue(), tunnel.getPhysicalLocalInterfaceName(), interfaces));
			tunnel.setInboundInterfaceCounterOID_BroadcastPkts(getInterfaceCounterOID(OIDValues.IF_HC_IN_BROADCAST_PKTS_OID.getValue(), tunnel.getPhysicalLocalInterfaceName(), interfaces));
			tunnel.setOutboundInterfaceCounterOID_BroadcastPkts(getInterfaceCounterOID(OIDValues.IF_HC_OUT_BROADCAST_PKTS_OID.getValue(), tunnel.getPhysicalLocalInterfaceName(), interfaces));
		}
	}
}
