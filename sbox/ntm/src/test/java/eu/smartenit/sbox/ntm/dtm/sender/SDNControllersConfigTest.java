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
package eu.smartenit.sbox.ntm.dtm.sender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import eu.smartenit.sbox.db.dao.DC2DCCommunicationDAO;
import eu.smartenit.sbox.db.dao.SystemControlParametersDAO;
import eu.smartenit.sbox.db.dto.ChargingRule;
import eu.smartenit.sbox.db.dto.ConfigData;
import eu.smartenit.sbox.db.dto.ConfigDataEntry;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.OperationModeSDN;
import eu.smartenit.sbox.db.dto.SDNController;
import eu.smartenit.sbox.db.dto.SystemControlParameters;
import eu.smartenit.sbox.db.dto.TunnelInfo;
import eu.smartenit.sbox.interfaces.sboxsdn.SboxSdnClient;
import eu.smartenit.sbox.ntm.dtm.DAOFactory;

/**
 * Tests SDN Controllers configuration process launched during
 * {@link DTMRemoteVectorsReceiver} initialization.
 * 
 * @author Lukasz Lopatowski
 * @version 3.0
 * 
 */
public class SDNControllersConfigTest {

	private static SboxSdnClient sdnClient = mock(SboxSdnClient.class);
	private static DC2DCCommunicationDAO dao = mock(DC2DCCommunicationDAO.class);
	private static SystemControlParametersDAO scpDAO = mock(SystemControlParametersDAO.class);
	
	@BeforeClass
	public static void setup() {
    	SDNClientFactory.disableUniqueClientCreationMode();
    	SDNClientFactory.setClientInstance(sdnClient);
    	
    	when(dao.findAllDC2DCCommunicationsCloudsTunnels()).thenReturn(DBStructuresBuilder.communicationsOnTrafficSender);
    	DAOFactory.setDC2DCComDAOInstance(dao);
    	
    	SystemControlParameters scp = new SystemControlParameters(ChargingRule.volume, OperationModeSDN.reactiveWithReferenceVector, 0.1);
    	when(scpDAO.findLast()).thenReturn(scp);
    	DAOFactory.setSCPDAOInstance(scpDAO);
	}
	
	@Test
	public void shouldInitControllers() {
		DTMRemoteVectorsReceiver receiver = new DTMRemoteVectorsReceiver();
		receiver.initialize();
		
		verify(sdnClient, times(2)).configure(any(SDNController.class), any(ConfigData.class));
		reset(sdnClient);
	}
	
	@Test
	public void shouldConstructProperConfigData() {
		DTMRemoteVectorsReceiver receiver = new DTMRemoteVectorsReceiver();
		receiver.initialize();
		
		ArgumentCaptor<SDNController> sdnCntrArg = ArgumentCaptor.forClass(SDNController.class); 
		ArgumentCaptor<ConfigData> cDataArg = ArgumentCaptor.forClass(ConfigData.class);
		
		verify(sdnClient, times(2)).configure(sdnCntrArg.capture(), cDataArg.capture());
		verifyConfigData(sdnCntrArg.getAllValues().get(0), cDataArg.getAllValues().get(0));
		verifyConfigData(sdnCntrArg.getAllValues().get(1), cDataArg.getAllValues().get(1));
		reset(sdnClient);
	}

	private void verifyConfigData(SDNController sdnController, ConfigData configData) {
		if (sdnController.getManagementAddress().equals(new NetworkAddressIPv4(DBStructuresBuilder.sdnController1Address, 32))) {
			verifyConfigDataForSDNCntr1(configData);
		} else
			verifyConfigDataForSDNCntr2(configData);
	}

	private void verifyConfigDataForSDNCntr1(ConfigData configData) {
		assertEquals(9, configData.getEntries().size());

		assertEquals(OperationModeSDN.reactiveWithReferenceVector, configData.getOperationModeSDN());
		assertEquals(2, configData.getLocalDCPortsConfig().size());
		
		ConfigDataEntry entry = getEntryFromConfigData(
				new NetworkAddressIPv4(DBStructuresBuilder.remoteCloudDCNetwork11, 28), DBStructuresBuilder.localDARDPID1, configData);
		assertNotNull(entry);
		assertEquals(2, entry.getTunnels().size());
		assertNotNull(getTunnelInfoFromList("100-1 to 1 (1)", 1, entry.getTunnels()));
		assertNotNull(getTunnelInfoFromList("100-1 to 1 (2)", 2, entry.getTunnels()));
	
		entry = getEntryFromConfigData(
				new NetworkAddressIPv4(DBStructuresBuilder.remoteCloudDCNetwork12, 28), DBStructuresBuilder.localDARDPID1, configData);
		assertNotNull(entry);
		assertEquals(2, entry.getTunnels().size());
		assertNotNull(getTunnelInfoFromList("100-1 to 1 (1)", 1, entry.getTunnels()));
		assertNotNull(getTunnelInfoFromList("100-1 to 1 (2)", 2, entry.getTunnels()));
	
		entry = getEntryFromConfigData(
				new NetworkAddressIPv4(DBStructuresBuilder.remoteCloudDCNetwork11, 28), DBStructuresBuilder.localDARDPID2, configData);
		assertNotNull(entry);
		assertEquals(2, entry.getTunnels().size());
		assertNotNull(getTunnelInfoFromList("100-2 to 1 (1)", 1, entry.getTunnels()));
		assertNotNull(getTunnelInfoFromList("100-2 to 1 (2)", 2, entry.getTunnels()));

		entry = getEntryFromConfigData(
				new NetworkAddressIPv4(DBStructuresBuilder.remoteCloudDCNetwork12, 28), DBStructuresBuilder.localDARDPID2, configData);
		assertNotNull(entry);
		assertEquals(2, entry.getTunnels().size());
		assertNotNull(getTunnelInfoFromList("100-2 to 1 (1)", 1, entry.getTunnels()));
		assertNotNull(getTunnelInfoFromList("100-2 to 1 (2)", 2, entry.getTunnels()));
		
		entry = getEntryFromConfigData(
				new NetworkAddressIPv4(DBStructuresBuilder.remoteCloudDCNetwork21, 28), DBStructuresBuilder.localDARDPID2, configData);
		assertNotNull(entry);
		assertEquals(2, entry.getTunnels().size());
		assertNotNull(getTunnelInfoFromList("100-2 to 1 (1)", 1, entry.getTunnels()));
		assertNotNull(getTunnelInfoFromList("100-2 to 1 (2)", 2, entry.getTunnels()));
		
		entry = getEntryFromConfigData(
				new NetworkAddressIPv4(DBStructuresBuilder.remoteCloudDCNetwork31, 28), DBStructuresBuilder.localDARDPID1, configData);
		assertNotNull(entry);
		assertEquals(2, entry.getTunnels().size());
		assertNotNull(getTunnelInfoFromList("100-1 to 2 (1)", 3, entry.getTunnels()));
		assertNotNull(getTunnelInfoFromList("100-1 to 2 (2)", 4, entry.getTunnels()));
		
		entry = getEntryFromConfigData(
				new NetworkAddressIPv4(DBStructuresBuilder.remoteCloudDCNetwork32, 28), DBStructuresBuilder.localDARDPID1, configData);
		assertNotNull(entry);
		assertEquals(2, entry.getTunnels().size());
		assertNotNull(getTunnelInfoFromList("100-1 to 2 (1)", 3, entry.getTunnels()));
		assertNotNull(getTunnelInfoFromList("100-1 to 2 (2)", 4, entry.getTunnels()));
		
		entry = getEntryFromConfigData(
				new NetworkAddressIPv4(DBStructuresBuilder.remoteCloudDCNetwork31, 28), DBStructuresBuilder.localDARDPID2, configData);
		assertNotNull(entry);
		assertEquals(2, entry.getTunnels().size());
		assertNotNull(getTunnelInfoFromList("100-2 to 2 (1)", 3, entry.getTunnels()));
		assertNotNull(getTunnelInfoFromList("100-2 to 2 (2)", 4, entry.getTunnels()));

		entry = getEntryFromConfigData(
				new NetworkAddressIPv4(DBStructuresBuilder.remoteCloudDCNetwork32, 28), DBStructuresBuilder.localDARDPID2, configData);
		assertNotNull(entry);
		assertEquals(2, entry.getTunnels().size());
		assertNotNull(getTunnelInfoFromList("100-2 to 2 (1)", 3, entry.getTunnels()));
		assertNotNull(getTunnelInfoFromList("100-2 to 2 (2)", 4, entry.getTunnels()));
	}
	
	private void verifyConfigDataForSDNCntr2(ConfigData configData) {
		assertEquals(2, configData.getEntries().size());

		assertEquals(OperationModeSDN.reactiveWithReferenceVector, configData.getOperationModeSDN());
		assertEquals(1, configData.getLocalDCPortsConfig().size());
		assertEquals("00:00:00:00:00:00:00:03", configData.getLocalDCPortsConfig().get(0).getDaRouterOfDPID());
		assertEquals(2, configData.getLocalDCPortsConfig().get(0).getLocalDCOfSwitchPortNumbers().size());
		
		ConfigDataEntry entry = getEntryFromConfigData(
				new NetworkAddressIPv4(DBStructuresBuilder.remoteCloudDCNetwork31, 28), DBStructuresBuilder.localDARDPID3, configData);
		assertNotNull(entry);
		assertEquals(2, entry.getTunnels().size());
		assertNotNull(getTunnelInfoFromList("101-1 to 2 (1)", 1, entry.getTunnels()));
		assertNotNull(getTunnelInfoFromList("101-1 to 2 (2)", 2, entry.getTunnels()));
		
		entry = getEntryFromConfigData(
				new NetworkAddressIPv4(DBStructuresBuilder.remoteCloudDCNetwork32, 28), DBStructuresBuilder.localDARDPID3, configData);
		assertEquals(2, entry.getTunnels().size());
		assertNotNull(entry);
		assertNotNull(getTunnelInfoFromList("101-1 to 2 (1)", 1, entry.getTunnels()));
		assertNotNull(getTunnelInfoFromList("101-1 to 2 (2)", 2, entry.getTunnels()));
	}
	
	private ConfigDataEntry getEntryFromConfigData(NetworkAddressIPv4 dcNetwork, String dpid, ConfigData data) {
		for(ConfigDataEntry entry : data.getEntries()) {
			if (entry.getRemoteDcPrefix().equals(dcNetwork) 
					&& entry.getDaRouterOfDPID().equals(dpid))
				return entry;
		}
		return null;
	}
	
	private TunnelInfo getTunnelInfoFromList(String tunnelName, int daRouterOfPortNumber, List<TunnelInfo> tunnels) {
		for(TunnelInfo info : tunnels)
			if(info.getTunnelID().getTunnelName().equals(tunnelName)
					&& info.getDaRouterOfPortNumber() == daRouterOfPortNumber)
				return info;
		return null;
	}
	
}
