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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import net.juniper.netconf.Device;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.xml.sax.SAXException;

import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;

/**
 * Set of test methods for all device configuration operations that are
 * supported by the {@link NetConfWrapper}. Some tests need to be ignored since
 * they try to connect to real router.
 * 
 * @author Lukasz Lopatowski
 * @version 3.1
 */
public class NetConfWrapperTest {

	private static final String ROUTER_IP_ADDRESS = "10.134.0.8";
	private static final String ROUTER_USER_NAME = "user";
	private static final String ROUTER_USER_PASSWORD = "password";
	private static final String INTERFACE_NAME = "ge-2/0/4";

	private static BGRouter bgRouter;
	private Device device = mock(Device.class);
	
	@Before
	public void setup() {
		bgRouter = new BGRouter(new NetworkAddressIPv4(ROUTER_IP_ADDRESS, 32), null, null);
		bgRouter.setNetconfUsername(ROUTER_USER_NAME);
		bgRouter.setNetconfPassword(ROUTER_USER_PASSWORD);
		
		NetConfWrapper.setInstance(null);
	}
	
	private static final String ACTIVATE_FILTER_MESSAGE = "" +
			"<rpc>" +
				"<load-configuration action=\"set\" format=\"text\">" +
					"<configuration-set>" +
						"activate interfaces ge-2/0/4 unit 0 family inet filter input" +
					"</configuration-set>" +
				"</load-configuration>" +
			"</rpc>";
	@Test
	public void shouldActivateFilter() throws IOException, SAXException {
		NetConfWrapper.build(device).activateHierarchicalFilter(INTERFACE_NAME);
		verify(device, times(1)).connect();
		ArgumentCaptor<String> message = ArgumentCaptor.forClass(String.class);
		verify(device, times(1)).executeRPC(message.capture());
		verify(device, times(1)).commit();
		verify(device, times(1)).close();
		assertEquals(ACTIVATE_FILTER_MESSAGE, message.getValue());
	}
	
	private static final String DEACTIVATE_FILTER_MESSAGE = "" +
			"<rpc>" +
				"<load-configuration action=\"set\" format=\"text\">" +
					"<configuration-set>" +
						"deactivate interfaces ge-2/0/4 unit 0 family inet filter input" +
					"</configuration-set>" +
				"</load-configuration>" +
			"</rpc>";
	@Test
	public void shouldDeactivateFilter() throws IOException, SAXException {
		NetConfWrapper.build(device).deactivateHierarchicalFilter(INTERFACE_NAME);
		verify(device, times(1)).connect();
		ArgumentCaptor<String> message = ArgumentCaptor.forClass(String.class);
		verify(device, times(1)).executeRPC(message.capture());
		verify(device, times(1)).commit();
		verify(device, times(1)).close();
		assertEquals(DEACTIVATE_FILTER_MESSAGE, message.getValue());
	}
	
	private static final String SET_BANDWIDTH_LIMIT_ON_POLICER_MESSAGE = "" +
			"<rpc>" +
				"<load-configuration action=\"set\" format=\"text\">" +
					"<configuration-set>" +
						"set firewall policer POLICER_EF_GE-2/0/4" + 
						" if-exceeding bandwidth-limit 100000 burst-size-limit 625000" +
					"</configuration-set>" +
				"</load-configuration>" +
			"</rpc>";
	
	private static final String SET_BANDWIDTH_LIMIT_ON_HIERARCHICAL_POLICER_AGGREGATE_MESSAGE = "" +
			"<rpc>" +
				"<load-configuration action=\"set\" format=\"text\">" +
					"<configuration-set>" +
						"set firewall hierarchical-policer POLICER_TOLERANT_TRAFFIC_GE-2/0/4" +
	 					" aggregate if-exceeding bandwidth-limit 100000 burst-size-limit 625000" +
					"</configuration-set>" +
				"</load-configuration>" +
			"</rpc>";
	
	private static final String SET_BANDWIDTH_LIMIT_ON_HIERARCHICAL_POLICER_PREMIUM_MESSAGE = "" +
			"<rpc>" +
				"<load-configuration action=\"set\" format=\"text\">" +
					"<configuration-set>" +
						"set firewall hierarchical-policer POLICER_TOLERANT_TRAFFIC_GE-2/0/4" +
	 					" premium if-exceeding bandwidth-limit 100000 burst-size-limit 625000" +
					"</configuration-set>" +
				"</load-configuration>" +
			"</rpc>";
	
	@Test
	public void shouldSetBandwidthLimit() throws SAXException, IOException {
		NetConfWrapper.build(device).updatePolicerConfig(INTERFACE_NAME, 100000);
		verify(device, times(1)).connect();
		ArgumentCaptor<String> message = ArgumentCaptor.forClass(String.class);
		verify(device, times(3)).executeRPC(message.capture());
		verify(device, times(1)).commit();
		verify(device, times(1)).close();
		assertEquals(SET_BANDWIDTH_LIMIT_ON_POLICER_MESSAGE, message.getAllValues().get(0));
		assertEquals(SET_BANDWIDTH_LIMIT_ON_HIERARCHICAL_POLICER_AGGREGATE_MESSAGE, message.getAllValues().get(1));
		assertEquals(SET_BANDWIDTH_LIMIT_ON_HIERARCHICAL_POLICER_PREMIUM_MESSAGE, message.getAllValues().get(2));
	}

	@Test
	public void shouldReturnFalseOnDeviceNull() {
		Device device = null;
		assertFalse(NetConfWrapper.build(device).activateHierarchicalFilter(INTERFACE_NAME));
		assertFalse(NetConfWrapper.build(device).deactivateHierarchicalFilter(INTERFACE_NAME));
		assertFalse(NetConfWrapper.build(device).updatePolicerConfig(INTERFACE_NAME, 100000));
	}
	
	@After
	public void clearMock() {
		reset(device);
	}
	
	@Ignore
	@Test
	public void shouldActivateFilterOnRealDevice() {
		assertTrue(NetConfWrapper.build(bgRouter).activateHierarchicalFilter(INTERFACE_NAME));
	}
	
	@Ignore
	@Test
	public void shouldDeactivateFilterOnRealDevice() {
		assertTrue(NetConfWrapper.build(bgRouter).deactivateHierarchicalFilter(INTERFACE_NAME));
	}

	@Ignore
	@Test
	public void shouldSetBandwidthLimitOnRealDevice() {
		assertTrue(NetConfWrapper.build(bgRouter).updatePolicerConfig(INTERFACE_NAME, 250000));
	}
	
	@Ignore
	@Test
	public void shouldSetBandwidthLimitAndBurstSizeOnRealDevice() {
		assertTrue(NetConfWrapper.build(bgRouter).updatePolicerConfig(INTERFACE_NAME, 120000, 125000));
	}
	
}
