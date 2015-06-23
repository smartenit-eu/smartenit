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

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import net.juniper.netconf.CommitException;
import net.juniper.netconf.Device;
import net.juniper.netconf.LoadException;
import net.juniper.netconf.NetconfException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import eu.smartenit.sbox.db.dto.BGRouter;

/**
 * Implements router configuration operations by means of NetConf protocol.
 * 
 * @author Lukasz Lopatowski
 * @version 3.1
 */
public class NetConfWrapper {

	private static final Logger logger = LoggerFactory.getLogger(NetConfWrapper.class);
	
	// it is assumed that policers with given names are configured on routers
	private static final String POLICER_NAME_TEMPLATE = "POLICER_EF_<IFACE_NAME>";
	private static final String HIERARCHICAL_POLICER_NAME_TEMPLATE = "POLICER_TOLERANT_TRAFFIC_<IFACE_NAME>";
	private static final long DEFAULT_BURST_SIZE_LIMIT = 625000;
	
	private static final String MESSAGE_TEMPLATE = "" +
			"<rpc>" +
				"<load-configuration action=\"set\" format=\"text\">" +
					"<configuration-set>" +
						"<OPERATION-STRING>" +
					"</configuration-set>" +
				"</load-configuration>" +
			"</rpc>";
	
	private static final String IFACE_FILTER_CONFIG = "" +
			"<OPERATION-NAME> interfaces <INTERFACE-NAME> unit 0 family inet filter input";
	
	private static final String POLICER_CONFIG = "" +
			"set firewall policer <POLICER_NAME>" + 
				" if-exceeding bandwidth-limit <BANDWIDTH-LIMIT> burst-size-limit <BURST-SIZE-LIMIT>";
	
	private static final String HIERARCHICAL_POLICER_AGGREGATE_CONFIG = "" +
			"set firewall hierarchical-policer <HIERARCHICAL_POLICER_NAME>" +
 				" aggregate if-exceeding bandwidth-limit <BANDWIDTH-LIMIT> burst-size-limit <BURST-SIZE-LIMIT>";
	
	private static final String HIERARCHICAL_POLICER_PREMIUM_CONFIG = "" +
			"set firewall hierarchical-policer <HIERARCHICAL_POLICER_NAME>" +
				" premium if-exceeding bandwidth-limit <BANDWIDTH-LIMIT> burst-size-limit <BURST-SIZE-LIMIT>";
	
	private Device device;
	
	private static NetConfWrapper instance = null;
	
	/**
	 * Returns an instance of {@link NetConfWrapper} configured based on the
	 * provided {@link BGRouter} instance.
	 * 
	 * @param bgRouter
	 *            BG router to which the wrapper will connect
	 * @return instance of {@link NetConfWrapper}
	 */
	public static NetConfWrapper build(BGRouter bgRouter) {
		if (instance != null) 
			return instance;
		
		return new NetConfWrapper(
				bgRouter.getManagementAddress().getPrefix()
				, bgRouter.getNetconfUsername()
				, bgRouter.getNetconfPassword());
	}
	
	public static NetConfWrapper build(Device device) {
		if (instance != null) 
			return instance;
		return new NetConfWrapper(device);
	}
	
	public static void setInstance(NetConfWrapper wrapper) {
		instance = wrapper;
	}

	/**
	 * Creates a Device instance which will be used to sent configuration
	 * commands to the router.
	 * 
	 * @param ipAddress
	 *            IP address of the router to be configured
	 * @param username
	 *            Username configured on the router for SSH access
	 * @param password
	 *            Password configured on the router
	 */
	private NetConfWrapper(String ipAddress, String username, String password) {
		try {
			device = new Device(ipAddress, username, password, null);
		} catch (NetconfException | ParserConfigurationException e) {
			e.printStackTrace();
			device = null;
		}
	}
	
	private NetConfWrapper(Device device) {
		this.device = device;
	}

	/**
	 * Redirects the operation execution to another method providing default
	 * value of burst size limit.
	 * 
	 * @param interfaceName
	 *            name of the interface that is used on the router, e.g.
	 *            ge-2/0/4
	 * @param bandwidthLimit
	 *            bandwidth limit value in bits per second
	 * @return <code>true</code> if operation was executed without errors
	 */
	public boolean updatePolicerConfig(String interfaceName, long bandwidthLimit) {
		return updatePolicerConfig(interfaceName, bandwidthLimit, DEFAULT_BURST_SIZE_LIMIT);
	}
	
	/**
	 * Updates hierarchical policer configuration on an interface with given
	 * values of bandwidth limit and burst size limit.
	 * 
	 * @param interfaceName
	 *            name of the interface that is used on the router, e.g.
	 *            ge-2/0/4
	 * @param bandwidthLimit
	 *            bandwidth limit value in bits per second
	 * @param burstSizeLimit
	 *            burst size limit value in bits per second
	 * @return <code>true</code> if operation was executed without errors
	 */
	public boolean updatePolicerConfig(String interfaceName, long bandwidthLimit, long burstSizeLimit) {
		if (checkIfDeviceIsNull()) 
			return false;
		
		String firstOperation = POLICER_CONFIG
				.replaceAll("<POLICER_NAME>", POLICER_NAME_TEMPLATE.replaceAll("<IFACE_NAME>", interfaceName.toUpperCase()))
				.replaceAll("<BANDWIDTH-LIMIT>", Long.toString(bandwidthLimit))
				.replaceAll("<BURST-SIZE-LIMIT>", Long.toString(burstSizeLimit));
		String secondOperation = HIERARCHICAL_POLICER_AGGREGATE_CONFIG
				.replaceAll("<HIERARCHICAL_POLICER_NAME>", HIERARCHICAL_POLICER_NAME_TEMPLATE.replaceAll("<IFACE_NAME>", interfaceName.toUpperCase()))
				.replaceAll("<BANDWIDTH-LIMIT>", Long.toString(bandwidthLimit))
				.replaceAll("<BURST-SIZE-LIMIT>", Long.toString(burstSizeLimit));
		String thirdOperation = HIERARCHICAL_POLICER_PREMIUM_CONFIG
				.replaceAll("<HIERARCHICAL_POLICER_NAME>", HIERARCHICAL_POLICER_NAME_TEMPLATE.replaceAll("<IFACE_NAME>", interfaceName.toUpperCase()))
				.replaceAll("<BANDWIDTH-LIMIT>", Long.toString(bandwidthLimit))
				.replaceAll("<BURST-SIZE-LIMIT>", Long.toString(burstSizeLimit));
		
		return connectAndExecuteOperation(
				MESSAGE_TEMPLATE.replaceAll("<OPERATION-STRING>", firstOperation)
				, MESSAGE_TEMPLATE.replaceAll("<OPERATION-STRING>", secondOperation)
				, MESSAGE_TEMPLATE.replaceAll("<OPERATION-STRING>", thirdOperation));
	}
	
	/**
	 * Activates hierarchical filter on given router interface.
	 * 
	 * @param interfaceName
	 *            name of the interface that is used on the router, e.g.
	 *            ge-2/0/4
	 * @return <code>true</code> if operation was executed without errors
	 */
	public boolean activateHierarchicalFilter(String interfaceName) {
		if (checkIfDeviceIsNull()) 
			return false;
		
		String operation = IFACE_FILTER_CONFIG
			.replaceAll("<OPERATION-NAME>", OperationName.ACTIVATE.value)
			.replaceAll("<INTERFACE-NAME>", interfaceName);
		String message = MESSAGE_TEMPLATE.replaceAll("<OPERATION-STRING>", operation);
		return connectAndExecuteOperation(message);
	}

	/**
	 * Deactivates hierarchical filter on given router interface.
	 * 
	 * @param interfaceName
	 *            name of the interface that is used on the router, e.g.
	 *            ge-2/0/4
	 * @return <code>true</code> if operation was executed without errors
	 */
	public boolean deactivateHierarchicalFilter(String interfaceName) {
		if (checkIfDeviceIsNull()) 
			return false;
		
		String operation = IFACE_FILTER_CONFIG
				.replaceAll("<OPERATION-NAME>", OperationName.DEACTIVATE.value)
				.replaceAll("<INTERFACE-NAME>", interfaceName);
		String message = MESSAGE_TEMPLATE.replaceAll("<OPERATION-STRING>", operation);
		return connectAndExecuteOperation(message);
	}

	private boolean connectAndExecuteOperation(String... messages) {
		try {
			device.connect();
			for (String message : messages)
				device.executeRPC(message);
			device.commit();
			return true;
			
		} catch(LoadException | CommitException | NetconfException e) {
			System.out.println(e.getMessage());
			return false;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return false;
		} catch (SAXException e) {
			System.out.println(e.getMessage());
			return false;
		} finally {
			device.close();
		}
	}
	
	private boolean checkIfDeviceIsNull() {
		if (device == null) {
			logger.warn("Problem occured during creation of instance of net.juniper.netconf.Device.");
			return true;
		}
		return false;
	}
	
	public enum OperationName {
		ACTIVATE("activate"),
		DEACTIVATE("deactivate");
		
		private final String value;
		
		OperationName(String value) {
			this.value = value;
		}
		
		public String value() {
			return value;
		}
	}
	
}