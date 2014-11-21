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

import java.util.List;
import java.util.concurrent.Callable;

import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.Tunnel;

/**
 * Thread class responsible for collecting counter values from specific
 * interfaces of a given router.
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a
 *         href="http://psnc.pl">PSNC</a>)
 * @version 1.0
 * 
 */
public class CounterCollectorThread implements Callable<CounterValues> {
	protected List<Link> links;
	protected BGRouter bgRouter;
	protected List<Tunnel> tunnels;
	protected DARouter daRouter;
	protected SNMPWrapper snmpWrapper;
	
	public CounterCollectorThread() {
		this.snmpWrapper = SNMPWrapperFactory.getInstance();
	}
	
	/**
	 * The constructor with arguments to be used when monitoring BG router.
	 * 
	 * @param links
	 *            list of links of a given BG router that will be monitored
	 * @param bgRouter
	 *            target {@link BGRouter}
	 */
	public CounterCollectorThread(List<Link> links, BGRouter bgRouter) {
		this();
		this.links = links;
		this.bgRouter = bgRouter;
	}
	
	/**
	 * The constructor with arguments to be used when monitoring DA router.
	 * 
	 * @param tunnels
	 *            list of tunnels of a given DA router that will be monitored
	 * @param daRouter
	 *            target {@link DARouter}
	 */
	public CounterCollectorThread(List<Tunnel> tunnels, DARouter daRouter) {
		this();
		this.tunnels = tunnels;
		this.daRouter = daRouter;
	}

	/**
	 * Thread's main method responsible for fetching data from counters and
	 * updating {@link TrafficCollectorTask}.
	 */
	public CounterValues call() throws Exception {
		return collectCounterValues();
	}
	
	protected CounterValues collectCounterValues() throws Exception {
		if(!(validateBgRouter() || validateDaRouter())) {
			throw new IllegalArgumentException("Both BGRouter and DARouter are null");
		}
		
		validateSnmpWrapper();
		try {
			snmpWrapper.startClient();
			if(validateBgRouter()) {
				return collectCounterValuesForBGRouter();
			} else {
				return collectCounterValuesForDARouter();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			snmpWrapper.stopClient();
		}
	}
	
	protected CounterValues collectCounterValuesForBGRouter() throws Exception {
		final CounterValues counterValues = new CounterValues();
		for (Link link : links) {
			counterValues.storeCounterValue(link.getLinkID(), getInboundLinkCounterValueFromBGRouter(link));
		}
		return counterValues;
	}
	
	protected CounterValues collectCounterValuesForDARouter() throws Exception {
		final CounterValues counterValues = new CounterValues();
		for (Tunnel tunnel : tunnels) {
			counterValues.storeCounterValue(tunnel.getTunnelID(), getInboundTunnelCounterValueFromDARouter(tunnel));
		}
		return counterValues;
	}

	protected boolean validateBgRouter() {
		if (bgRouter == null || links == null) {
			return false;
		}
		return true;
	}
	
	protected boolean validateDaRouter() {
		if (daRouter == null || tunnels == null) {
			return false;
		}
		return true;
	}
	
	private void validateSnmpWrapper() {
		if (snmpWrapper == null) {
			throw new IllegalArgumentException("snmWrapper is null");
		}
	}

	protected long getInboundLinkCounterValueFromBGRouter(Link link) throws Exception {
		long value = -1;
		value = parseCounterValue(
				snmpWrapper.snmpGet(snmpWrapper.prepareOID(link.getInboundInterfaceCounterOID()), 
				snmpWrapper.prepareTarget(bgRouter.getManagementAddress().getPrefix(), bgRouter.getSnmpCommunity())));
		return value;
	}
	
	protected long getInboundTunnelCounterValueFromDARouter(Tunnel tunnel) {
		long value = -1;
		value = parseCounterValue(
				snmpWrapper.snmpGet(snmpWrapper.prepareOID(tunnel.getInboundInterfaceCounterOID()), 
				snmpWrapper.prepareTarget(daRouter.getManagementAddress().getPrefix(), daRouter.getSnmpCommunity())));
		return value;
	}

	protected long parseCounterValue(String snmpGet) throws NumberFormatException {
		return Long.valueOf(snmpGet.substring(snmpGet.indexOf("=") + 2, snmpGet.length() - 1));
	}
}
