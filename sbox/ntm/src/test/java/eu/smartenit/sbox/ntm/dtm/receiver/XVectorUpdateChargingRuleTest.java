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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.ChargingRule;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.LinkID;
import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.LocalVectorValue;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SBox;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.SystemControlParameters;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.interfaces.intersbox.client.InterSBoxClient;
import eu.smartenit.sbox.ntm.dtm.DAOFactory;

/**
 * Includes test methods for workflow triggered by {@link DTMTrafficManager}
 * after receiving updated link traffic vector from QoS Analyzer component (
 * for both values of {@link ChargingRule})
 * 
 * @author Lukasz Lopatowski
 * @version 3.1
 * 
 */
public class XVectorUpdateChargingRuleTest {

	private int asNumber1 = 1;
	private LocalRVector rVector;
	private XVector xVector1, xVector2, xVector3, xVector4;
	
	private RemoteSBoxContainer container = mock(RemoteSBoxContainer.class);
	private InterSBoxClient client = mock(InterSBoxClient.class);
	private LinkDAO dao = mock(LinkDAO.class);
	private SystemControlParametersDAO scpDAO = mock(SystemControlParametersDAO.class);
	private TimeScheduleParametersDAO tspDAO = mock(TimeScheduleParametersDAO.class);
	
	SimpleLinkID linkID1 = new SimpleLinkID("id1", "isp1");
	SimpleLinkID linkID2 = new SimpleLinkID("id2", "isp1");

	@Before
	public void setup() {
    	SBox sbox1 = new SBox(new NetworkAddressIPv4("1.1.1.1", 32));
    	when(container.getRemoteSBoxes(asNumber1)).thenReturn(Arrays.asList(sbox1));
    	
    	InterSBoxClientFactory.disableUniqueClientCreationMode();
    	InterSBoxClientFactory.setClientInstance(client);
    	
    	SBoxThreadHandler.threadService = 
    			Executors.newScheduledThreadPool(SBoxProperties.CORE_POOL_SIZE, new ThreadFactory());
    	
		LocalVectorValue xValue1 = new LocalVectorValue(100, linkID1);
		LocalVectorValue xValue2 = new LocalVectorValue(100, linkID2);
		xVector1 = new XVector(Arrays.asList(xValue1, xValue2), asNumber1);

		LocalVectorValue xValue3 = new LocalVectorValue(200, linkID1);
		LocalVectorValue xValue4 = new LocalVectorValue(50, linkID2);
		xVector2 = new XVector(Arrays.asList(xValue3, xValue4), asNumber1);

		LocalVectorValue xValue5 = new LocalVectorValue(60, linkID1);
		LocalVectorValue xValue6 = new LocalVectorValue(120, linkID2);
		xVector3 = new XVector(Arrays.asList(xValue5, xValue6), asNumber1);
		
		LocalVectorValue xValue7 = new LocalVectorValue(180, linkID1);
		LocalVectorValue xValue8 = new LocalVectorValue(120, linkID2);
		xVector4 = new XVector(Arrays.asList(xValue7, xValue8), asNumber1);
		
		LocalVectorValue rValue1 = new LocalVectorValue(500, linkID1);
		LocalVectorValue rValue2 = new LocalVectorValue(800, linkID2);
		rVector = new LocalRVector(Arrays.asList(rValue1, rValue2), asNumber1);
		
		when(dao.findById(linkID1)).thenReturn(
				new Link(linkID1, null, null, 0, null, null, null, null, null, new NetworkAddressIPv4("1.1.1.1", 24)));
		when(dao.findById(linkID2)).thenReturn(
				new Link(linkID2, null, null, 0, null, null, null, null, null, new NetworkAddressIPv4("2.2.2.2", 24)));
		
		DAOFactory.setLinkDAOInstance(dao);
	}
	
	@Test
	public void shouldDistributeCVectorChargingRule95thNoController() throws Exception {
    	SystemControlParameters scp = new SystemControlParameters(ChargingRule.the95thPercentile, null, 0);
    	when(scpDAO.findLast()).thenReturn(scp);
    	TimeScheduleParameters tsp = new TimeScheduleParameters();
    	tsp.setSamplingPeriod(9);
    	tsp.setReportPeriodDTM(3);
    	when(tspDAO.findLast()).thenReturn(tsp);
    	
    	DAOFactory.setSCPDAOInstance(scpDAO);
    	DAOFactory.setTSPDAOInstance(tspDAO);
		
		DTMTrafficManager manager = new DTMTrafficManager();
		manager.setSBoxContainer(container);
		manager.initialize();
		CVectorUpdateController.deactivate();
		manager.updateRVector(rVector);
		manager.updateXVector(xVector1);
		Thread.sleep(100);
		manager.updateXVector(xVector2);
		Thread.sleep(100);
		manager.updateXVector(xVector3);
		Thread.sleep(100);
		manager.updateXVector(xVector4);
		
		Thread.sleep(1000);
		ArgumentCaptor<CVector> cVectorArgument = ArgumentCaptor.forClass(CVector.class);
		
		verify(client, times(4)).send(any(String.class), anyInt(), cVectorArgument.capture());
		reset(client);
		assertEquals(-23, cVectorArgument.getAllValues().get(0).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("1.1.1.1", 24)));
		assertEquals(23, cVectorArgument.getAllValues().get(0).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("2.2.2.2", 24)));
		assertEquals(-126, cVectorArgument.getAllValues().get(1).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("1.1.1.1", 24)));
		assertEquals(126, cVectorArgument.getAllValues().get(1).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("2.2.2.2", 24)));
		assertEquals(0, cVectorArgument.getAllValues().get(2).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("1.1.1.1", 24)));
		assertEquals(0, cVectorArgument.getAllValues().get(2).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("2.2.2.2", 24)));
		assertEquals(-64, cVectorArgument.getAllValues().get(3).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("1.1.1.1", 24)));
		assertEquals(64, cVectorArgument.getAllValues().get(3).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("2.2.2.2", 24)));
	}
	
	@Test
	public void shouldDistributeCVectorChargingRule95thWithController() throws Exception {
		xVector1 = new XVector(Arrays.asList(new LocalVectorValue(1000, linkID1), new LocalVectorValue(1000, linkID2)), asNumber1);
		xVector2 = new XVector(Arrays.asList(new LocalVectorValue(500, linkID1), new LocalVectorValue(1000, linkID2)), asNumber1);
		xVector3 = new XVector(Arrays.asList(new LocalVectorValue(500, linkID1), new LocalVectorValue(1000, linkID2)), asNumber1);
		xVector4 = new XVector(Arrays.asList(new LocalVectorValue(100, linkID1), new LocalVectorValue(5000, linkID2)), asNumber1);
		
		rVector = new LocalRVector(Arrays.asList(new LocalVectorValue(3000, linkID1), new LocalVectorValue(5000, linkID2)), asNumber1);
		
		SystemControlParameters scp = new SystemControlParameters(ChargingRule.the95thPercentile, null, 0.2);
    	when(scpDAO.findLast()).thenReturn(scp);
    	TimeScheduleParameters tsp = new TimeScheduleParameters();
    	tsp.setSamplingPeriod(18);
    	tsp.setReportPeriodDTM(3);
    	tsp.setCompensationPeriod(90);
    	when(tspDAO.findLast()).thenReturn(tsp);
    	
    	DAOFactory.setSCPDAOInstance(scpDAO);
    	DAOFactory.setTSPDAOInstance(tspDAO);
		
		DTMTrafficManager manager = new DTMTrafficManager();
		manager.setSBoxContainer(container);
		manager.initialize();
		manager.updateRVector(rVector);
		manager.updateXVector(xVector1);
		Thread.sleep(100);
		manager.updateXVector(xVector2);
		Thread.sleep(100);
		manager.updateXVector(xVector3);
		Thread.sleep(100);
		manager.updateXVector(xVector4);
		
		CVectorUpdateController controller = CVectorUpdateController.getInstance();
		controller.updateLinksWithRVectorAchieved(asNumber1, Arrays.asList((LinkID)linkID2));
		Thread.sleep(100);
		manager.updateXVector(xVector1);
		Thread.sleep(100);
		manager.updateXVector(xVector1);
		Thread.sleep(100);
		manager.updateXVector(xVector1);
		controller.updateLinksWithRVectorAchieved(asNumber1, Arrays.asList((LinkID)linkID1));
		Thread.sleep(100);
		manager.updateXVector(xVector1);
		Thread.sleep(100);
		manager.updateXVector(xVector1);
		
		Thread.sleep(1000);
		ArgumentCaptor<CVector> cVectorArgument = ArgumentCaptor.forClass(CVector.class);
		verify(client, times(5)).send(any(String.class), anyInt(), cVectorArgument.capture());
		reset(client);
		assertEquals(-250, cVectorArgument.getAllValues().get(0).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("1.1.1.1", 24)));
		assertEquals(250, cVectorArgument.getAllValues().get(0).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("2.2.2.2", 24)));
		assertEquals(1687, cVectorArgument.getAllValues().get(1).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("1.1.1.1", 24)));
		assertEquals(-1687, cVectorArgument.getAllValues().get(1).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("2.2.2.2", 24)));
		assertEquals(-800000, cVectorArgument.getAllValues().get(2).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("1.1.1.1", 24)));
		assertEquals(800000, cVectorArgument.getAllValues().get(2).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("2.2.2.2", 24)));
		assertEquals(800000, cVectorArgument.getAllValues().get(3).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("1.1.1.1", 24)));
		assertEquals(-800000, cVectorArgument.getAllValues().get(3).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("2.2.2.2", 24)));
		assertEquals(-800000, cVectorArgument.getAllValues().get(4).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("1.1.1.1", 24)));
		assertEquals(800000, cVectorArgument.getAllValues().get(4).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("2.2.2.2", 24)));
	}
	
	@Test
	public void shouldDistributeCVectorChargingRuleVolume() throws Exception {
    	SystemControlParameters scp = new SystemControlParameters(ChargingRule.volume, null, 0);
    	when(scpDAO.findLast()).thenReturn(scp);
    	TimeScheduleParameters tsp = new TimeScheduleParameters();
    	when(tspDAO.findLast()).thenReturn(tsp);
    	
    	DAOFactory.setSCPDAOInstance(scpDAO);
    	DAOFactory.setTSPDAOInstance(tspDAO);
    	
		DTMTrafficManager manager = new DTMTrafficManager();
		manager.setSBoxContainer(container);
		manager.initialize();
		CVectorUpdateController.deactivate();
		manager.updateRVector(rVector);
		manager.updateXVector(xVector1);
		Thread.sleep(100);
		manager.updateXVector(xVector2);
		Thread.sleep(100);
		manager.updateXVector(xVector3);
		Thread.sleep(100);
		manager.updateXVector(xVector4);
		
		Thread.sleep(1000);
		ArgumentCaptor<CVector> cVectorArgument = ArgumentCaptor.forClass(CVector.class);
		
		verify(client, times(4)).send(any(String.class), anyInt(), cVectorArgument.capture());
		reset(client);
		assertEquals(-23, cVectorArgument.getAllValues().get(0).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("1.1.1.1", 24)));
		assertEquals(23, cVectorArgument.getAllValues().get(0).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("2.2.2.2", 24)));
		assertEquals(-126, cVectorArgument.getAllValues().get(1).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("1.1.1.1", 24)));
		assertEquals(126, cVectorArgument.getAllValues().get(1).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("2.2.2.2", 24)));
		assertEquals(-117, cVectorArgument.getAllValues().get(2).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("1.1.1.1", 24)));
		assertEquals(117, cVectorArgument.getAllValues().get(2).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("2.2.2.2", 24)));
		assertEquals(-182, cVectorArgument.getAllValues().get(3).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("1.1.1.1", 24)));
		assertEquals(182, cVectorArgument.getAllValues().get(3).getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("2.2.2.2", 24)));
	}
	
}
