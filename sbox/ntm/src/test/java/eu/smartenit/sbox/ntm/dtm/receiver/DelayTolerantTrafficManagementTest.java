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
package eu.smartenit.sbox.ntm.dtm.receiver;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import eu.smartenit.sbox.commons.SBoxProperties;
import eu.smartenit.sbox.commons.SBoxThreadHandler;
import eu.smartenit.sbox.commons.ThreadFactory;
import eu.smartenit.sbox.db.dao.LinkDAO;
import eu.smartenit.sbox.db.dao.SystemControlParametersDAO;
import eu.smartenit.sbox.db.dao.TimeScheduleParametersDAO;
import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.ChargingRule;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.LinkID;
import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.db.dto.SBox;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.SystemControlParameters;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.interfaces.intersbox.client.InterSBoxClient;
import eu.smartenit.sbox.ntm.dtm.DAOFactory;

/**
 * Tests functionalities related with delay tolerant traffic management.
 * 
 * @author Lukasz Lopatowski
 * @version 3.1
 */
public class DelayTolerantTrafficManagementTest {
	
	private static final String LINK1_ID = "link1";
	private static final String LINK2_ID = "link2";
	private static final String ISP1_ID = "isp1";
	
	private int asNumber = 1;
	private XVector xVector = new XVector();
	private LocalRVector rVector = new LocalRVector();
	
	private RemoteSBoxContainer container = mock(RemoteSBoxContainer.class);
	private InterSBoxClient client = mock(InterSBoxClient.class);
	private NetConfWrapper netconf = mock(NetConfWrapper.class);
	
	private LinkDAO dao = mock(LinkDAO.class);
	private SystemControlParametersDAO scpDAO = mock(SystemControlParametersDAO.class);
	private TimeScheduleParametersDAO tspDAO = mock(TimeScheduleParametersDAO.class);

	private SimpleLinkID linkID1 = new SimpleLinkID(LINK1_ID, ISP1_ID);
	private SimpleLinkID linkID2 = new SimpleLinkID(LINK2_ID, ISP1_ID);
	
	@Before
	public void setup() {
		xVector.setSourceAsNumber(asNumber);
    	xVector.addVectorValueForLink(linkID1, 5000L);
    	xVector.addVectorValueForLink(linkID2, 5000L);
    	rVector.setSourceAsNumber(asNumber);
    	rVector.addVectorValueForLink(linkID1, 10000L);
    	rVector.addVectorValueForLink(linkID2, 20000L);
    	
    	InterSBoxClientFactory.disableUniqueClientCreationMode();
    	InterSBoxClientFactory.setClientInstance(client);
    	
    	SBox sbox1 = new SBox(new NetworkAddressIPv4("1.1.1.1", 32));
    	when(container.getRemoteSBoxes(asNumber)).thenReturn(Arrays.asList(sbox1));
    	
    	BGRouter bgRouterForLinkID1 = new BGRouter(new NetworkAddressIPv4("192.168.1.1", 32), "", null);
    	BGRouter bgRouterForLinkID2 = new BGRouter(new NetworkAddressIPv4("192.168.2.1", 32), "", null);
    	Link link1 = new Link(linkID1, null, "ethOnLink1", 0, null, null, null, bgRouterForLinkID1, null, new NetworkAddressIPv4("10.10.10.10", 24));
    	link1.setPolicerBandwidthLimitFactor(0.8);
    	link1.setFilterInterfaceName("eth1OnLink1");
    	link1.setAggregateLeakageFactor(0.05);
    	Link link2 = new Link(linkID2, null, "ethOnLink2", 0, null, null, null, bgRouterForLinkID2, null, new NetworkAddressIPv4("20.20.20.20", 24));
    	link2.setPolicerBandwidthLimitFactor(0.0);
    	link2.setFilterInterfaceName("eth1OnLink2");
    	link2.setAggregateLeakageFactor(0.10);
    	when(dao.findById(linkID1)).thenReturn(link1);
    	when(dao.findById(linkID2)).thenReturn(link2);

    	TimeScheduleParameters tsp = new TimeScheduleParameters();
    	tsp.setSamplingPeriod(300);
    	tsp.setReportPeriodDTM(30);
    	when(tspDAO.findLast()).thenReturn(tsp);
    	
    	DAOFactory.setLinkDAOInstance(dao);
    	DAOFactory.setTSPDAOInstance(tspDAO);
    	
    	when(netconf.deactivateHierarchicalFilter(any(String.class))).thenReturn(true);
    	when(netconf.activateHierarchicalFilter(any(String.class))).thenReturn(true);
    	NetConfWrapper.setInstance(netconf);
    	
    	SBoxThreadHandler.threadService = 
    			Executors.newScheduledThreadPool(SBoxProperties.CORE_POOL_SIZE, new ThreadFactory());
	}
	
	@Test
	public void shouldTriggerPolicerConfUpdates() throws Exception {
    	SystemControlParameters scp = new SystemControlParameters(ChargingRule.the95thPercentile, null, 0);
    	scp.setDelayTolerantTrafficManagement(true);
    	when(scpDAO.findLast()).thenReturn(scp);
    	DAOFactory.setSCPDAOInstance(scpDAO);
    	
		DTMTrafficManager manager = new DTMTrafficManager();
		manager.setSBoxContainer(container);
		manager.initialize();
		manager.updateXVector(xVector);
		manager.updateRVector(rVector);
		manager.updateRVector(rVector);
		
		ArgumentCaptor<Long> bandwidthLimit = ArgumentCaptor.forClass(Long.class);
		ArgumentCaptor<Long> bandwidthLimitPremium = ArgumentCaptor.forClass(Long.class);
		
		Thread.sleep(1000);
		verify(client, times(2)).send(any(String.class), anyInt(), any(CVector.class), any(RVector.class));
		verify(netconf, times(4)).updatePolicerConfig(anyString(), bandwidthLimit.capture(), bandwidthLimitPremium.capture());
		assertEquals(212L, bandwidthLimit.getAllValues().get(0).longValue());
		assertEquals(533L, bandwidthLimit.getAllValues().get(1).longValue());
		assertEquals(212L, bandwidthLimit.getAllValues().get(2).longValue());
		assertEquals(533L, bandwidthLimit.getAllValues().get(3).longValue());
	}
	
	@Test
	public void shouldNotTriggerPolicerConfUpdates() throws Exception {
    	SystemControlParameters scp = new SystemControlParameters(ChargingRule.the95thPercentile, null, 0);
    	scp.setDelayTolerantTrafficManagement(false);
    	when(scpDAO.findLast()).thenReturn(scp);
    	DAOFactory.setSCPDAOInstance(scpDAO);
    	
		DTMTrafficManager manager = new DTMTrafficManager();
		manager.setSBoxContainer(container);
		manager.initialize();
		manager.updateXVector(xVector);
		manager.updateRVector(rVector);
		manager.updateRVector(rVector);
		
		Thread.sleep(1000);
		verifyZeroInteractions(netconf);
	}

	@Test (expected = IllegalArgumentException.class)
	public void shouldThrowExceptionOnNullListOfLinks() {
		SystemControlParametersDAO scpDAO = mock(SystemControlParametersDAO.class);
		SystemControlParameters scp = new SystemControlParameters(ChargingRule.the95thPercentile, null, 0.1);
		scp.setDelayTolerantTrafficManagement(true);
		when(scpDAO.findLast()).thenReturn(scp);
		DAOFactory.setSCPDAOInstance(scpDAO);

		DTMTrafficManager manager = new DTMTrafficManager();
		manager.updateLinksWithRVectorAchieved(0, null);
	}
	
	@Test
	public void shouldDeactivateAndActivateFiltersOnGivenLinks() throws InterruptedException {
    	SystemControlParameters scp = new SystemControlParameters(ChargingRule.the95thPercentile, null, 0);
    	scp.setDelayTolerantTrafficManagement(true);
    	when(scpDAO.findLast()).thenReturn(scp);
    	DAOFactory.setSCPDAOInstance(scpDAO);
    	
		DTMTrafficManager manager = new DTMTrafficManager();
		manager.setSBoxContainer(container);
		manager.initialize();
		manager.updateLinksWithRVectorAchieved(asNumber, Arrays.asList((LinkID)linkID1));
		manager.updateLinksWithRVectorAchieved(asNumber, Arrays.asList((LinkID)linkID1));
		manager.updateLinksWithRVectorAchieved(asNumber, Arrays.asList((LinkID)linkID2));
		manager.updateRVector(rVector);
		
		ArgumentCaptor<String> deactivatedIfaceName = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> activatedIfaceName = ArgumentCaptor.forClass(String.class);
		
		Thread.sleep(1000);
		verify(netconf, times(2)).deactivateHierarchicalFilter(deactivatedIfaceName.capture());
		assertEquals("eth1OnLink1", deactivatedIfaceName.getAllValues().get(0));
		assertEquals("eth1OnLink2", deactivatedIfaceName.getAllValues().get(1));
		verify(netconf, times(2)).activateHierarchicalFilter(activatedIfaceName.capture());
	}
	
}
