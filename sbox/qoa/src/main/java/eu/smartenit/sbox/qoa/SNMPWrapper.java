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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.snmp4j.CommunityTarget;
import org.snmp4j.MessageDispatcher;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

import eu.smartenit.sbox.commons.SBoxProperties;

/**
 * SNMP4j wrapper.
 * 
 * @author <a href="mailto:jgutkow@man.poznan.pl">Jakub Gutkowski</a> (<a href="http://psnc.pl">PSNC</a>)
 *
 */
public class SNMPWrapper {
	private final static int SNMP_VERSION = SnmpConstants.version2c;
	private Snmp snmpClient;
	
	public SNMPWrapper() {}
	
	public SNMPWrapper(Snmp snmpClient) {
		this.snmpClient = snmpClient;
	}
	
	/**
	 * Triggers snmpget command.
	 * 
	 * @param oid oid
	 * @param target {@link org.snmp4j.Target}
	 * @return response from SNMP server
	 * @throws IllegalStateException
	 */
	public String snmpGet(OID oid, Target target) {
		validateSnmpClient();
		
		final ResponseEvent responseEvent = sendSnmpGetRequest(oid, target);
		
		validateResponseEvent(responseEvent);
		validateResponseFromResponseEvent(responseEvent);
		if (responseEvent.getResponse().getErrorStatus() == PDU.noError) {
			return responseEvent.getResponse().getVariableBindings().toString();
		} else {
			throw new IllegalStateException(prepareErrorExceptionForGet(responseEvent.getResponse()));
		}
	}

	/**
	 * Triggers snmpwalk command.
	 * 
	 * @param oid oid
	 * @param target {@link org.snmp4j.Target}
	 * @return response from SNMP server
	 * @throws IllegalStateException
	 */
	public List<String> snmpWalk(OID oid, Target target) {
		validateSnmpClient();
		
		final List<TreeEvent> events = prepareTreeUtils().getSubtree(target, oid);
		if (events == null || events.size() == 0) {
			throw new IllegalStateException("Wrapper Error: Response is null");
		}
		
		List<String> response = new ArrayList<String>();
		for(TreeEvent event : events) {
			if (event.isError()) {
				throw new IllegalStateException(prepareErrorExceptionForWalk(oid, event));
			}
			
			final VariableBinding[] varBindings = event.getVariableBindings();
			if (varBindings == null || varBindings.length == 0) {
					throw new IllegalStateException("SNMPWrapper Error: Response is null");
				}
			for (VariableBinding varBinding : varBindings) {
					response.add(varBinding.toString());
			}
		}
		return response;
	}

	/**
	 * Starts SNMP client.
	 * 
	 * @throws IOException
	 */
	public void startClient() throws IOException {
		if(snmpClient == null) {
			MessageDispatcher md = new MessageDispatcherImpl();
			md.addMessageProcessingModel(new MPv2c());
			DefaultUdpTransportMapping tp = new DefaultUdpTransportMapping();
			snmpClient = new Snmp(md, tp);
		}
		snmpClient.listen();
	}

	/**
	 * Stops SNMP client.
	 * 
	 * @throws IOException
	 */
	public void stopClient() throws IOException {
		if(snmpClient != null) {
			snmpClient.close();
		}
		snmpClient = null;
	}
	
	/**
	 * Prepares object which implements {@link org.snmp4j.Target}
	 * 
	 * @param routerAddress router's IPv4 address
	 * @param snmpCommunity SNMP community
	 * @return {@link org.snmp4j.Target} object
	 */
	public Target prepareTarget(String routerAddress, String snmpCommunity) {
		final CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(snmpCommunity));
		if(!routerAddress.contains("/")) {
			routerAddress += "/161";
		}
		target.setAddress(new UdpAddress(routerAddress));
		target.setRetries(SBoxProperties.CONNECTION_RETRIES);
		target.setTimeout(SBoxProperties.CONNECTION_TIMEOUT);
		target.setVersion(SNMP_VERSION);
		return target;
	}
	
	/**
	 * Prepares {@link org.snmp4j.smi.OID} object.
	 * 
	 * @param oid oid as String
	 * @return {@link org.snmp4j.smi.OID} object
	 */
	public OID prepareOID(String oid) {
		return new OID(oid);
	}

	protected ResponseEvent sendSnmpGetRequest(OID oid, Target target) {
		final ResponseEvent responseEvent;
		try {
			responseEvent = snmpClient.send(prepareSnmpgetPDU(oid), target);
		} catch (Exception e) {
			throw new IllegalStateException("SNMPWrapper Error: \n", e);
		}
		return responseEvent;
	}
	
	protected void validateSnmpClient() {
		if (this.snmpClient == null) {
			throw new IllegalStateException("SNMPWrapper Error: SNMP client is null");
		}
	}
	
	protected String prepareErrorExceptionForGet(PDU responsePDU) {
		final StringBuilder error = new StringBuilder();
		error.append("SNMPWrapper Error: Request Failed\n");
		error.append("Error Status: ")
				.append(responsePDU.getErrorStatus())
				.append("\n");
		error.append("Error Index: ")
				.append(responsePDU.getErrorIndex())
				.append("\n");
		error.append("Error Status Text: ")
				.append(responsePDU.getErrorStatusText())
				.append("\n");
		return error.toString();
	}
	
	protected String prepareErrorExceptionForWalk(OID oid, TreeEvent event) {
		final StringBuilder error = new StringBuilder("SNMPWrapper Error:\n");
		error.append("[").append(oid).append(" = ")
		.append(event.getErrorMessage()).append("]");
		return error.toString();
	}
	
	
	protected PDU prepareSnmpgetPDU(OID oid) {
		final PDU pdu = new PDU();
		pdu.add(new VariableBinding(oid));
		pdu.setType(PDU.GET);
		return pdu;
	}
	
	protected TreeUtils prepareTreeUtils() {
		return new TreeUtils(snmpClient, new DefaultPDUFactory());
	}
	
	private void validateResponseFromResponseEvent(ResponseEvent responseEvent) {
		if (responseEvent.getResponse() == null) {
			throw new IllegalStateException("SNMPWrapper Error: Response is null");
		}
	}
	
	private void validateResponseEvent(ResponseEvent responseEvent) {
		if (responseEvent == null) {
			throw new IllegalStateException("SNMPWrapper Error: SNMP GET timed out");
		}
	}
}
