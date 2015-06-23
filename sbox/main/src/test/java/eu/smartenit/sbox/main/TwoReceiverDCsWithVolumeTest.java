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
package eu.smartenit.sbox.main;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommunityTarget;
import org.snmp4j.Target;
import org.snmp4j.smi.OID;

import eu.smartenit.sbox.commons.SBoxProperties;
import eu.smartenit.sbox.commons.SBoxThreadHandler;
import eu.smartenit.sbox.commons.ThreadFactory;
import eu.smartenit.sbox.db.dao.ASDAO;
import eu.smartenit.sbox.db.dao.CostFunctionDAO;
import eu.smartenit.sbox.db.dao.DC2DCCommunicationDAO;
import eu.smartenit.sbox.db.dao.LinkDAO;
import eu.smartenit.sbox.db.dao.SystemControlParametersDAO;
import eu.smartenit.sbox.db.dao.TimeScheduleParametersDAO;
import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.ConfigData;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.LocalRVector;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.db.dto.SDNController;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.eca.EconomicAnalyzer;
import eu.smartenit.sbox.interfaces.intersbox.server.InterSBoxServer;
import eu.smartenit.sbox.interfaces.sboxsdn.SboxSdnClient;
import eu.smartenit.sbox.ntm.NetworkTrafficManager;
import eu.smartenit.sbox.ntm.NetworkTrafficManagerDTMMode;
import eu.smartenit.sbox.ntm.dtm.receiver.CVectorUpdateController;
import eu.smartenit.sbox.ntm.dtm.sender.SDNClientFactory;
import eu.smartenit.sbox.qoa.DTMQosAnalyzer;
import eu.smartenit.sbox.qoa.SNMPWrapper;
import eu.smartenit.sbox.qoa.SNMPWrapperFactory;

public class TwoReceiverDCsWithVolumeTest {
	private static final Logger logger = LoggerFactory.getLogger(TwoReceiverDCsWithVolumeTest.class);	
	
	private static final short SENDER_AS_NUMBER = 2;
	private static final short RECEIVER_AS1_NUMBER = 1;
	private static final short RECEIVER_AS2_NUMBER = 3;
	
	private SboxSdnClient mockedSDNClient = mock(SboxSdnClient.class);
	
	@Before
	public void setup() throws Exception {
		logger.info("Initializing thread service.");
		ThreadFactory threadFactory = new ThreadFactory();
		SBoxThreadHandler.threadService = Executors.newScheduledThreadPool(SBoxProperties.CORE_POOL_SIZE, threadFactory);
		
		logger.info("Preparing mock for DAOs");
		prepareMockForDAOAtECA();
		
		logger.info("Preparing mock for SboxSdnClient.");
		prepareMockForSDNClient();
		
		logger.info("Preparing mock for SNMPWrapper.");
		SNMPWrapper snmpWrapper = mock(SNMPWrapper.class);
		prepareMockForSNMPWrapper(snmpWrapper);
	}

	@After
	public void clean() {
		logger.info("Shutting down threads.");
		SBoxThreadHandler.shutdownNowThreads();
	}

	/**
	 * This method initializes NTM at sending domain with mocked SBox-SDN client
	 * which is later on used to verify interactions with the SDN controller.
	 * Inter-SBox server is launched. At the receiving domain NTM, Economic
	 * Analyzer and QoS Analyzer components are initialized. Mocked DAOs are
	 * used instead of the real database.
	 * 
	 * QoS Analyzer reads pre-configured counter values from mocked SNMPWrapper,
	 * calculates X and Z vectors and passes them to ECA and NTM. ECA calculates
	 * R vector after accounting period. NTM calculates C vector after each
	 * reporting period and updates remote NTM with new C and R (after
	 * accounting period) vectors. Values of those vectors are verified against
	 * known values.
	 * 
	 */
	@Test @Ignore
	public void shouldTestCompleteWorkflowWithGivenValuesAndTwoReceiverDCs() throws Exception {
		
		logger.info("--Testing full chain starting from QOS Analyzer on the traffic receiver side " +
				"up to NTM on the traffic sender side. --");

		logger.info("Initializing NTM at sending domain, #2, and inter-sbox server.");
		prepareMockForDAOAtNTM(SENDER_AS_NUMBER);
		NetworkTrafficManager remoteNTM = new NetworkTrafficManager();
		remoteNTM.initialize(NetworkTrafficManagerDTMMode.TRAFFIC_SENDER);
		
		SBoxProperties.INTER_SBOX_PORT++;
		new InterSBoxServer(SBoxProperties.INTER_SBOX_PORT, remoteNTM);
		Thread.sleep(6000);
		
		verify(mockedSDNClient, times(1)).configure(any(SDNController.class), any(ConfigData.class));
		
		logger.info("Initializing NTM at RECEIVER domain, #1.");
		prepareMockForDAOAtNTM(RECEIVER_AS1_NUMBER);
		NetworkTrafficManager localNTM = new NetworkTrafficManager();
		localNTM.initialize(NetworkTrafficManagerDTMMode.TRAFFIC_RECEIVER);
		CVectorUpdateController.deactivate();
		localNTM.getDtmTrafficManager().updateRVector(prepareRVectorWithInitialValues(RECEIVER_AS1_NUMBER));
		
		logger.info("Initializing Economic Analyzer at RECEIVER domain, #1.");
		EconomicAnalyzer eca = new EconomicAnalyzer(localNTM.getDtmTrafficManager());

		logger.info("Initializing QoS Analyzer at RECEIVER domain, #1.");
		prepareMockForDAOAtQOA(RECEIVER_AS1_NUMBER);
		DTMQosAnalyzer analyzer = new DTMQosAnalyzer(localNTM.getDtmTrafficManager(), eca);
		analyzer.initialize();

		Thread.sleep(1000);

		logger.info("Initializing NTM at RECEIVER domain, #3.");
		prepareMockForDAOAtNTM(RECEIVER_AS2_NUMBER);
		NetworkTrafficManager localNTM2 = new NetworkTrafficManager();
		localNTM2.initialize(NetworkTrafficManagerDTMMode.TRAFFIC_RECEIVER);
		CVectorUpdateController.deactivate();
		localNTM2.getDtmTrafficManager().updateRVector(prepareRVectorWithInitialValues(RECEIVER_AS2_NUMBER));
		
		logger.info("Initializing Economic Analyzer at RECEIVER domain, #3.");
		EconomicAnalyzer eca2 = new EconomicAnalyzer(localNTM2.getDtmTrafficManager());

		logger.info("Initializing QoS Analyzer at RECEIVER domain, #3.");
		prepareMockForDAOAtQOA(RECEIVER_AS2_NUMBER);
		DTMQosAnalyzer analyzer2 = new DTMQosAnalyzer(localNTM2.getDtmTrafficManager(), eca2);
		analyzer2.initialize();
		
		Thread.sleep(51*1000);

		ArgumentCaptor<RVector> rVectorArgument = ArgumentCaptor.forClass(RVector.class);
		ArgumentCaptor<CVector> cVectorArgument1 = ArgumentCaptor.forClass(CVector.class);
		ArgumentCaptor<CVector> cVectorArgument2 = ArgumentCaptor.forClass(CVector.class);
		
		verify(mockedSDNClient, times(24)).distribute(any(SDNController.class), cVectorArgument1.capture());
		verify(mockedSDNClient, times(8)).distribute(any(SDNController.class), cVectorArgument2.capture(), rVectorArgument.capture());
		
		verifyAllCVectorUpdates(cVectorArgument1);
		verifyAllCRVectorUpdates(cVectorArgument2, rVectorArgument);

		logger.info("-----------------------------------------------------");
	}

	private void verifyAllCVectorUpdates(ArgumentCaptor<CVector> cVectorArgument1) {
		NetworkAddressIPv4 tunnelEndPrefix1 = new NetworkAddressIPv4("10.1.1.0", 24);
		NetworkAddressIPv4 tunnelEndPrefix2 = new NetworkAddressIPv4("10.1.2.0", 24);
		NetworkAddressIPv4 tunnelEndPrefix3 = new NetworkAddressIPv4("10.3.1.0", 24);
		NetworkAddressIPv4 tunnelEndPrefix4 = new NetworkAddressIPv4("10.3.2.0", 24);
		
		assertEquals(141, cVectorArgument1.getAllValues().get(0).getVectorValueForTunnelEndPrefix(tunnelEndPrefix1));
		assertEquals(-141, cVectorArgument1.getAllValues().get(0).getVectorValueForTunnelEndPrefix(tunnelEndPrefix2));
		assertEquals(2111, cVectorArgument1.getAllValues().get(1).getVectorValueForTunnelEndPrefix(tunnelEndPrefix3));
		assertEquals(-2111, cVectorArgument1.getAllValues().get(1).getVectorValueForTunnelEndPrefix(tunnelEndPrefix4));
		
		assertEquals(495, cVectorArgument1.getAllValues().get(2).getVectorValueForTunnelEndPrefix(tunnelEndPrefix1));
		assertEquals(-495, cVectorArgument1.getAllValues().get(2).getVectorValueForTunnelEndPrefix(tunnelEndPrefix2));
		assertEquals(3166, cVectorArgument1.getAllValues().get(3).getVectorValueForTunnelEndPrefix(tunnelEndPrefix3));
		assertEquals(-3166, cVectorArgument1.getAllValues().get(3).getVectorValueForTunnelEndPrefix(tunnelEndPrefix4));
		
		assertEquals(41, cVectorArgument1.getAllValues().get(4).getVectorValueForTunnelEndPrefix(tunnelEndPrefix1));
		assertEquals(-41, cVectorArgument1.getAllValues().get(4).getVectorValueForTunnelEndPrefix(tunnelEndPrefix2));
		assertEquals(4805, cVectorArgument1.getAllValues().get(5).getVectorValueForTunnelEndPrefix(tunnelEndPrefix3));
		assertEquals(-4805, cVectorArgument1.getAllValues().get(5).getVectorValueForTunnelEndPrefix(tunnelEndPrefix4));
		
		assertEquals(43, cVectorArgument1.getAllValues().get(6).getVectorValueForTunnelEndPrefix(tunnelEndPrefix1));
		assertEquals(-43, cVectorArgument1.getAllValues().get(6).getVectorValueForTunnelEndPrefix(tunnelEndPrefix2));
		assertEquals(2729, cVectorArgument1.getAllValues().get(7).getVectorValueForTunnelEndPrefix(tunnelEndPrefix3));
		assertEquals(-2729, cVectorArgument1.getAllValues().get(7).getVectorValueForTunnelEndPrefix(tunnelEndPrefix4));
		
		assertEquals(420, cVectorArgument1.getAllValues().get(8).getVectorValueForTunnelEndPrefix(tunnelEndPrefix1));
		assertEquals(-420, cVectorArgument1.getAllValues().get(8).getVectorValueForTunnelEndPrefix(tunnelEndPrefix2));
		assertEquals(4643, cVectorArgument1.getAllValues().get(9).getVectorValueForTunnelEndPrefix(tunnelEndPrefix3));
		assertEquals(-4643, cVectorArgument1.getAllValues().get(9).getVectorValueForTunnelEndPrefix(tunnelEndPrefix4));
		
		assertEquals(388, cVectorArgument1.getAllValues().get(10).getVectorValueForTunnelEndPrefix(tunnelEndPrefix1));
		assertEquals(-388, cVectorArgument1.getAllValues().get(10).getVectorValueForTunnelEndPrefix(tunnelEndPrefix2));
		assertEquals(7194, cVectorArgument1.getAllValues().get(11).getVectorValueForTunnelEndPrefix(tunnelEndPrefix3));
		assertEquals(-7194, cVectorArgument1.getAllValues().get(11).getVectorValueForTunnelEndPrefix(tunnelEndPrefix4));
		
		assertEquals(-475, cVectorArgument1.getAllValues().get(12).getVectorValueForTunnelEndPrefix(tunnelEndPrefix1));
		assertEquals(475, cVectorArgument1.getAllValues().get(12).getVectorValueForTunnelEndPrefix(tunnelEndPrefix2));
		assertEquals(2857, cVectorArgument1.getAllValues().get(13).getVectorValueForTunnelEndPrefix(tunnelEndPrefix3));
		assertEquals(-2857, cVectorArgument1.getAllValues().get(13).getVectorValueForTunnelEndPrefix(tunnelEndPrefix4));
		
		assertEquals(-1280, cVectorArgument1.getAllValues().get(14).getVectorValueForTunnelEndPrefix(tunnelEndPrefix1));
		assertEquals(1280, cVectorArgument1.getAllValues().get(14).getVectorValueForTunnelEndPrefix(tunnelEndPrefix2));
		assertEquals(5755, cVectorArgument1.getAllValues().get(15).getVectorValueForTunnelEndPrefix(tunnelEndPrefix3));
		assertEquals(-5755, cVectorArgument1.getAllValues().get(15).getVectorValueForTunnelEndPrefix(tunnelEndPrefix4));
		
		assertEquals(-1458, cVectorArgument1.getAllValues().get(16).getVectorValueForTunnelEndPrefix(tunnelEndPrefix1));
		assertEquals(1458, cVectorArgument1.getAllValues().get(16).getVectorValueForTunnelEndPrefix(tunnelEndPrefix2));
		assertEquals(8641, cVectorArgument1.getAllValues().get(17).getVectorValueForTunnelEndPrefix(tunnelEndPrefix3));
		assertEquals(-8641, cVectorArgument1.getAllValues().get(17).getVectorValueForTunnelEndPrefix(tunnelEndPrefix4));
		
		assertEquals(-517, cVectorArgument1.getAllValues().get(18).getVectorValueForTunnelEndPrefix(tunnelEndPrefix1));
		assertEquals(517, cVectorArgument1.getAllValues().get(18).getVectorValueForTunnelEndPrefix(tunnelEndPrefix2));
		assertEquals(-1882, cVectorArgument1.getAllValues().get(19).getVectorValueForTunnelEndPrefix(tunnelEndPrefix3));
		assertEquals(1882, cVectorArgument1.getAllValues().get(19).getVectorValueForTunnelEndPrefix(tunnelEndPrefix4));
		
		assertEquals(452, cVectorArgument1.getAllValues().get(20).getVectorValueForTunnelEndPrefix(tunnelEndPrefix1));
		assertEquals(-452, cVectorArgument1.getAllValues().get(20).getVectorValueForTunnelEndPrefix(tunnelEndPrefix2));
		assertEquals(-3252, cVectorArgument1.getAllValues().get(21).getVectorValueForTunnelEndPrefix(tunnelEndPrefix3));
		assertEquals(3252, cVectorArgument1.getAllValues().get(21).getVectorValueForTunnelEndPrefix(tunnelEndPrefix4));
		
		assertEquals(-572, cVectorArgument1.getAllValues().get(22).getVectorValueForTunnelEndPrefix(tunnelEndPrefix1));
		assertEquals(572, cVectorArgument1.getAllValues().get(22).getVectorValueForTunnelEndPrefix(tunnelEndPrefix2));
		assertEquals(-6227, cVectorArgument1.getAllValues().get(23).getVectorValueForTunnelEndPrefix(tunnelEndPrefix3));
		assertEquals(6227, cVectorArgument1.getAllValues().get(23).getVectorValueForTunnelEndPrefix(tunnelEndPrefix4));
	}
	
	private void verifyAllCRVectorUpdates(ArgumentCaptor<CVector> cVectorArgument2, ArgumentCaptor<RVector> rVectorArgument) {
		NetworkAddressIPv4 tunnelEndPrefix1 = new NetworkAddressIPv4("10.1.1.0", 24);
		NetworkAddressIPv4 tunnelEndPrefix2 = new NetworkAddressIPv4("10.1.2.0", 24);
		NetworkAddressIPv4 tunnelEndPrefix3 = new NetworkAddressIPv4("10.3.1.0", 24);
		NetworkAddressIPv4 tunnelEndPrefix4 = new NetworkAddressIPv4("10.3.2.0", 24);
		
		assertEquals(0, cVectorArgument2.getAllValues().get(0).getVectorValueForTunnelEndPrefix(tunnelEndPrefix1));
		assertEquals(0, cVectorArgument2.getAllValues().get(0).getVectorValueForTunnelEndPrefix(tunnelEndPrefix2));
		assertEquals(15300, rVectorArgument.getAllValues().get(0).getVectorValueForTunnelEndPrefix(tunnelEndPrefix1));
		assertEquals(20800, rVectorArgument.getAllValues().get(0).getVectorValueForTunnelEndPrefix(tunnelEndPrefix2));

		assertEquals(0, cVectorArgument2.getAllValues().get(1).getVectorValueForTunnelEndPrefix(tunnelEndPrefix3));
		assertEquals(0, cVectorArgument2.getAllValues().get(1).getVectorValueForTunnelEndPrefix(tunnelEndPrefix4));
		assertEquals(19500, rVectorArgument.getAllValues().get(1).getVectorValueForTunnelEndPrefix(tunnelEndPrefix3));
		assertEquals(10000, rVectorArgument.getAllValues().get(1).getVectorValueForTunnelEndPrefix(tunnelEndPrefix4));
		
		assertEquals(0, cVectorArgument2.getAllValues().get(2).getVectorValueForTunnelEndPrefix(tunnelEndPrefix1));
		assertEquals(0, cVectorArgument2.getAllValues().get(2).getVectorValueForTunnelEndPrefix(tunnelEndPrefix2));
		assertEquals(14900, rVectorArgument.getAllValues().get(2).getVectorValueForTunnelEndPrefix(tunnelEndPrefix1));
		assertEquals(20700, rVectorArgument.getAllValues().get(2).getVectorValueForTunnelEndPrefix(tunnelEndPrefix2));
		
		assertEquals(0, cVectorArgument2.getAllValues().get(3).getVectorValueForTunnelEndPrefix(tunnelEndPrefix3));
		assertEquals(0, cVectorArgument2.getAllValues().get(3).getVectorValueForTunnelEndPrefix(tunnelEndPrefix4));
		assertEquals(20500, rVectorArgument.getAllValues().get(3).getVectorValueForTunnelEndPrefix(tunnelEndPrefix3));
		assertEquals(10050, rVectorArgument.getAllValues().get(3).getVectorValueForTunnelEndPrefix(tunnelEndPrefix4));
		
		assertEquals(0, cVectorArgument2.getAllValues().get(4).getVectorValueForTunnelEndPrefix(tunnelEndPrefix1));
		assertEquals(0, cVectorArgument2.getAllValues().get(4).getVectorValueForTunnelEndPrefix(tunnelEndPrefix2));
		assertEquals(19900, rVectorArgument.getAllValues().get(4).getVectorValueForTunnelEndPrefix(tunnelEndPrefix1));
		assertEquals(20100, rVectorArgument.getAllValues().get(4).getVectorValueForTunnelEndPrefix(tunnelEndPrefix2));
		
		assertEquals(0, cVectorArgument2.getAllValues().get(5).getVectorValueForTunnelEndPrefix(tunnelEndPrefix3));
		assertEquals(0, cVectorArgument2.getAllValues().get(5).getVectorValueForTunnelEndPrefix(tunnelEndPrefix4));
		assertEquals(12100, rVectorArgument.getAllValues().get(5).getVectorValueForTunnelEndPrefix(tunnelEndPrefix3));
		assertEquals(27900, rVectorArgument.getAllValues().get(5).getVectorValueForTunnelEndPrefix(tunnelEndPrefix4));
		
		assertEquals(0, cVectorArgument2.getAllValues().get(6).getVectorValueForTunnelEndPrefix(tunnelEndPrefix1));
		assertEquals(0, cVectorArgument2.getAllValues().get(6).getVectorValueForTunnelEndPrefix(tunnelEndPrefix2));
		assertEquals(17200, rVectorArgument.getAllValues().get(6).getVectorValueForTunnelEndPrefix(tunnelEndPrefix1));
		assertEquals(11800, rVectorArgument.getAllValues().get(6).getVectorValueForTunnelEndPrefix(tunnelEndPrefix2));
		
		assertEquals(0, cVectorArgument2.getAllValues().get(7).getVectorValueForTunnelEndPrefix(tunnelEndPrefix3));
		assertEquals(0, cVectorArgument2.getAllValues().get(7).getVectorValueForTunnelEndPrefix(tunnelEndPrefix4));
		assertEquals(17200, rVectorArgument.getAllValues().get(7).getVectorValueForTunnelEndPrefix(tunnelEndPrefix3));
		assertEquals(11800, rVectorArgument.getAllValues().get(7).getVectorValueForTunnelEndPrefix(tunnelEndPrefix4));
	}
	
	private void prepareMockForDAOAtNTM(short asNumber) {
		ASDAO ntmMockedASDAO = mock(ASDAO.class);
		DC2DCCommunicationDAO ntmMockedComDAO = mock(DC2DCCommunicationDAO.class);
		if (asNumber == RECEIVER_AS1_NUMBER) {
			when(ntmMockedASDAO.findAllAsesCloudsBgRoutersLinks()).thenReturn(DBStructuresBuilderForMockedDAO.receiverSystems1);
			when(ntmMockedComDAO.findAllDC2DCCommunicationsCloudsTunnels()).thenReturn(DBStructuresBuilderForMockedDAO.receiverCommunications1);
		} else if (asNumber == RECEIVER_AS2_NUMBER) {
			when(ntmMockedASDAO.findAllAsesCloudsBgRoutersLinks()).thenReturn(DBStructuresBuilderForMockedDAO.receiverSystems2);
			when(ntmMockedComDAO.findAllDC2DCCommunicationsCloudsTunnels()).thenReturn(DBStructuresBuilderForMockedDAO.receiverCommunications2);
		} else 
			when(ntmMockedComDAO.findAllDC2DCCommunicationsCloudsTunnels()).thenReturn(DBStructuresBuilderForMockedDAO.senderCommunications2DCs);
		
		eu.smartenit.sbox.ntm.dtm.DAOFactory.setASDAOInstance(ntmMockedASDAO);
		eu.smartenit.sbox.ntm.dtm.DAOFactory.setDC2DCComDAOInstance(ntmMockedComDAO);
		
		LinkDAO ntmMockedLinkDAO = mock(LinkDAO.class);
    	when(ntmMockedLinkDAO.findById(new SimpleLinkID("111", "isp1"))).thenReturn(
    			new Link(new SimpleLinkID("111", "isp1"), null, null, 0, null, null, null, null, null, new NetworkAddressIPv4("10.1.1.0", 24)));
    	when(ntmMockedLinkDAO.findById(new SimpleLinkID("121", "isp1"))).thenReturn(
    			new Link(new SimpleLinkID("121", "isp1"), null, null, 0, null, null, null, null, null, new NetworkAddressIPv4("10.1.2.0", 24)));
    	when(ntmMockedLinkDAO.findById(new SimpleLinkID("311", "isp3"))).thenReturn(
    			new Link(new SimpleLinkID("311", "isp3"), null, null, 0, null, null, null, null, null, new NetworkAddressIPv4("10.3.1.0", 24)));
    	when(ntmMockedLinkDAO.findById(new SimpleLinkID("321", "isp3"))).thenReturn(
    			new Link(new SimpleLinkID("321", "isp3"), null, null, 0, null, null, null, null, null, new NetworkAddressIPv4("10.3.2.0", 24)));
		eu.smartenit.sbox.ntm.dtm.DAOFactory.setLinkDAOInstance(ntmMockedLinkDAO);
		
		SystemControlParametersDAO ntmMockedSCPDAO = mock(SystemControlParametersDAO.class);
		when(ntmMockedSCPDAO.findLast()).thenReturn(DBStructuresBuilderForMockedDAO.scpWithVolumeRule);
		eu.smartenit.sbox.ntm.dtm.DAOFactory.setSCPDAOInstance(ntmMockedSCPDAO);

		TimeScheduleParametersDAO ntmMockedTimeDAO = mock(TimeScheduleParametersDAO.class);
		when(ntmMockedTimeDAO.findLast()).thenReturn(DBStructuresBuilderForMockedDAO.tspForVolumeRule);
		eu.smartenit.sbox.ntm.dtm.DAOFactory.setTSPDAOInstance(ntmMockedTimeDAO);
	}
	
	private void prepareMockForDAOAtQOA(short asNumber) {
		ASDAO qoaMockedASDAO = mock(ASDAO.class);
		DC2DCCommunicationDAO qoaMockedComDAO = mock(DC2DCCommunicationDAO.class);
		if (asNumber == RECEIVER_AS1_NUMBER) { 
			when(qoaMockedASDAO.findAllAsesCloudsBgRoutersLinks()).thenReturn(DBStructuresBuilderForMockedDAO.receiverSystems1);		
			when(qoaMockedComDAO.findAllDC2DCCommunicationsCloudsTunnels()).thenReturn(DBStructuresBuilderForMockedDAO.receiverCommunications1);
		} else {
			when(qoaMockedASDAO.findAllAsesCloudsBgRoutersLinks()).thenReturn(DBStructuresBuilderForMockedDAO.receiverSystems2);		
			when(qoaMockedComDAO.findAllDC2DCCommunicationsCloudsTunnels()).thenReturn(DBStructuresBuilderForMockedDAO.receiverCommunications2);
		}
		
		TimeScheduleParametersDAO qoaMockedTimeDAO = mock(TimeScheduleParametersDAO.class);
		when(qoaMockedTimeDAO.findLast()).thenReturn(DBStructuresBuilderForMockedDAO.tspForVolumeRule);
		SystemControlParametersDAO qoaMockedSCPDAO = mock(SystemControlParametersDAO.class);
		when(qoaMockedSCPDAO.findLast()).thenReturn(DBStructuresBuilderForMockedDAO.scpWithVolumeRule);
		eu.smartenit.sbox.qoa.DAOFactory.setASDAOInstance(qoaMockedASDAO);
		eu.smartenit.sbox.qoa.DAOFactory.setDC2DCCommunicationDAO(qoaMockedComDAO);
		eu.smartenit.sbox.qoa.DAOFactory.setTimeScheduleParametersDAO(qoaMockedTimeDAO);
		eu.smartenit.sbox.qoa.DAOFactory.setSCPDAOInstance(qoaMockedSCPDAO);
	}
	
	private void prepareMockForDAOAtECA() {
		CostFunctionDAO ecaMockedCostDAO = mock(CostFunctionDAO.class);
		when(ecaMockedCostDAO.findByLinkId(Mockito.argThat(new IsLinkIdStringEqual("111"))))
			.thenReturn(DBStructuresBuilderForMockedDAO.receiverCostFunction1);
		when(ecaMockedCostDAO.findByLinkId(Mockito.argThat(new IsLinkIdStringEqual("121"))))
			.thenReturn(DBStructuresBuilderForMockedDAO.receiverCostFunction2);
		when(ecaMockedCostDAO.findByLinkId(Mockito.argThat(new IsLinkIdStringEqual("311"))))
			.thenReturn(DBStructuresBuilderForMockedDAO.receiverCostFunction3);
		when(ecaMockedCostDAO.findByLinkId(Mockito.argThat(new IsLinkIdStringEqual("321"))))
			.thenReturn(DBStructuresBuilderForMockedDAO.receiverCostFunction4);
		TimeScheduleParametersDAO ecaMockedTimeDAO = mock(TimeScheduleParametersDAO.class);
		when(ecaMockedTimeDAO.findLast()).thenReturn(DBStructuresBuilderForMockedDAO.tspForVolumeRule);
		SystemControlParametersDAO ecaMockedSCPDAO = mock(SystemControlParametersDAO.class);
		when(ecaMockedSCPDAO.findLast()).thenReturn(DBStructuresBuilderForMockedDAO.scpWithVolumeRule);
		eu.smartenit.sbox.eca.DAOFactory.setCostFunctionDAOInstance(ecaMockedCostDAO);
		eu.smartenit.sbox.eca.DAOFactory.setTSPDAOInstance(ecaMockedTimeDAO);
		eu.smartenit.sbox.eca.DAOFactory.setSCPDAOInstance(ecaMockedSCPDAO);
	}
	
	private void prepareMockForSDNClient() {
		SDNClientFactory.disableUniqueClientCreationMode();
		SDNClientFactory.setClientInstance(mockedSDNClient);
	}
	
	private void prepareMockForSNMPWrapper(SNMPWrapper snmpWrapper) {
		when(snmpWrapper.snmpWalk(any(OID.class), any(Target.class))).thenReturn(prepareSnmpwalkResponse());
		Mockito.doCallRealMethod().when(snmpWrapper).prepareOID(any(String.class));
		Mockito.doCallRealMethod().when(snmpWrapper).prepareTarget(any(String.class), any(String.class));
		
		/* localLinkID -> 111 */
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEqual("1.3.6.1.2.1.31.1.1.1.6.0")), Mockito.argThat(new IsAddressEqual("1.2.1.1"))))
			.thenReturn("1.3.6.1.2.1.31.1.1.1.6.0 = 0]",
					"1.3.6.1.2.1.31.1.1.1.6.0 = 4900]", "1.3.6.1.2.1.31.1.1.1.6.0 = 9150]", "1.3.6.1.2.1.31.1.1.1.6.0 = 15000]",
					"1.3.6.1.2.1.31.1.1.1.6.0 = 20000]", "1.3.6.1.2.1.31.1.1.1.6.0 = 24200]", "1.3.6.1.2.1.31.1.1.1.6.0 = 29700]",
					"1.3.6.1.2.1.31.1.1.1.6.0 = 35700]", "1.3.6.1.2.1.31.1.1.1.6.0 = 42700]", "1.3.6.1.2.1.31.1.1.1.6.0 = 47900]",
					"1.3.6.1.2.1.31.1.1.1.6.0 = 51900]", "1.3.6.1.2.1.31.1.1.1.6.0 = 56900]", "1.3.6.1.2.1.31.1.1.1.6.0 = 62900]");
		
		/* localLinkID -> 121 */
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEqual("1.3.6.1.2.1.31.1.1.1.6.1")), Mockito.argThat(new IsAddressEqual("1.2.1.2"))))
			.thenReturn("1.3.6.1.2.1.31.1.1.1.6.1 = 0]",
					"1.3.6.1.2.1.31.1.1.1.6.1 = 7200]", "1.3.6.1.2.1.31.1.1.1.6.1 = 14000]", "1.3.6.1.2.1.31.1.1.1.6.1 = 21100]",
					"1.3.6.1.2.1.31.1.1.1.6.1 = 28000]", "1.3.6.1.2.1.31.1.1.1.6.1 = 34600]", "1.3.6.1.2.1.31.1.1.1.6.1 = 42000]",
					"1.3.6.1.2.1.31.1.1.1.6.1 = 49200]", "1.3.6.1.2.1.31.1.1.1.6.1 = 57000]", "1.3.6.1.2.1.31.1.1.1.6.1 = 63800]",
					"1.3.6.1.2.1.31.1.1.1.6.1 = 66800]", "1.3.6.1.2.1.31.1.1.1.6.1 = 73800]", "1.3.6.1.2.1.31.1.1.1.6.1 = 77800]");
		
		/* tunnelNumber -> 1111 */
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEqual("1.3.6.1.2.1.31.1.1.1.6.0")), Mockito.argThat(new IsAddressEqual("1.1.1.1"))))
			.thenReturn("1.3.6.1.2.1.31.1.1.1.6.0 = 0]",
					"1.3.6.1.2.1.31.1.1.1.6.0 = 900]", "1.3.6.1.2.1.31.1.1.1.6.0 = 1900]", "1.3.6.1.2.1.31.1.1.1.6.0 = 2900]",
					"1.3.6.1.2.1.31.1.1.1.6.0 = 3790]", "1.3.6.1.2.1.31.1.1.1.6.0 = 4700]", "1.3.6.1.2.1.31.1.1.1.6.0 = 5600]",
					"1.3.6.1.2.1.31.1.1.1.6.0 = 7600]", "1.3.6.1.2.1.31.1.1.1.6.0 = 10800]", "1.3.6.1.2.1.31.1.1.1.6.0 = 11700]",
					"1.3.6.1.2.1.31.1.1.1.6.0 = 12550]", "1.3.6.1.2.1.31.1.1.1.6.0 = 13300]", "1.3.6.1.2.1.31.1.1.1.6.0 = 14200]");
		
		/* tunnelNumber -> 1211 */
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEqual("1.3.6.1.2.1.31.1.1.1.6.1")), Mockito.argThat(new IsAddressEqual("1.1.1.1"))))
			.thenReturn("1.3.6.1.2.1.31.1.1.1.6.1 = 0]",
					"1.3.6.1.2.1.31.1.1.1.6.1 = 90]", "1.3.6.1.2.1.31.1.1.1.6.1 = 205]", "1.3.6.1.2.1.31.1.1.1.6.1 = 300]",
					"1.3.6.1.2.1.31.1.1.1.6.1 = 380]", "1.3.6.1.2.1.31.1.1.1.6.1 = 460]", "1.3.6.1.2.1.31.1.1.1.6.1 = 500]",
					"1.3.6.1.2.1.31.1.1.1.6.1 = 1050]", "1.3.6.1.2.1.31.1.1.1.6.1 = 1600]", "1.3.6.1.2.1.31.1.1.1.6.1 = 2200]",
					"1.3.6.1.2.1.31.1.1.1.6.1 = 2950]", "1.3.6.1.2.1.31.1.1.1.6.1 = 3600]", "1.3.6.1.2.1.31.1.1.1.6.1 = 4400]");
		
		/* localLinkID -> 311 */
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEqual("1.3.6.1.2.1.31.1.1.1.6.0")), Mockito.argThat(new IsAddressEqual("3.2.1.1"))))
			.thenReturn("1.3.6.1.2.1.31.1.1.1.6.0 = 0]",
					"1.3.6.1.2.1.31.1.1.1.6.0 = 8000]", "1.3.6.1.2.1.31.1.1.1.6.0 = 12000]", "1.3.6.1.2.1.31.1.1.1.6.0 = 16500]",
					"1.3.6.1.2.1.31.1.1.1.6.0 = 22000]", "1.3.6.1.2.1.31.1.1.1.6.0 = 26300]", "1.3.6.1.2.1.31.1.1.1.6.0 = 29500]",
					"1.3.6.1.2.1.31.1.1.1.6.0 = 35500]", "1.3.6.1.2.1.31.1.1.1.6.0 = 42500]", "1.3.6.1.2.1.31.1.1.1.6.0 = 47700]",
					"1.3.6.1.2.1.31.1.1.1.6.0 = 51700]", "1.3.6.1.2.1.31.1.1.1.6.0 = 56700]", "1.3.6.1.2.1.31.1.1.1.6.0 = 62700]");
		
		/* localLinkID -> 321 */
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEqual("1.3.6.1.2.1.31.1.1.1.6.1")), Mockito.argThat(new IsAddressEqual("3.2.1.2"))))
			.thenReturn("1.3.6.1.2.1.31.1.1.1.6.1 = 0]",
					"1.3.6.1.2.1.31.1.1.1.6.1 = 6000]", "1.3.6.1.2.1.31.1.1.1.6.1 = 9000]", "1.3.6.1.2.1.31.1.1.1.6.1 = 13000]",
					"1.3.6.1.2.1.31.1.1.1.6.1 = 19950]", "1.3.6.1.2.1.31.1.1.1.6.1 = 25050]", "1.3.6.1.2.1.31.1.1.1.6.1 = 30550]",
					"1.3.6.1.2.1.31.1.1.1.6.1 = 37750]", "1.3.6.1.2.1.31.1.1.1.6.1 = 45500]", "1.3.6.1.2.1.31.1.1.1.6.1 = 52350]",
					"1.3.6.1.2.1.31.1.1.1.6.1 = 55350]", "1.3.6.1.2.1.31.1.1.1.6.1 = 62350]", "1.3.6.1.2.1.31.1.1.1.6.1 = 66350]");
		
		/* tunnelNumber -> 3111 */
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEqual("1.3.6.1.2.1.31.1.1.1.6.0")), Mockito.argThat(new IsAddressEqual("3.1.1.1"))))
			.thenReturn("1.3.6.1.2.1.31.1.1.1.6.0 = 0]",
					"1.3.6.1.2.1.31.1.1.1.6.0 = 5000]", "1.3.6.1.2.1.31.1.1.1.6.0 = 6000]", "1.3.6.1.2.1.31.1.1.1.6.0 = 7000]",
					"1.3.6.1.2.1.31.1.1.1.6.0 = 8850]", "1.3.6.1.2.1.31.1.1.1.6.0 = 10300]", "1.3.6.1.2.1.31.1.1.1.6.0 = 13500]",
					"1.3.6.1.2.1.31.1.1.1.6.0 = 15500]", "1.3.6.1.2.1.31.1.1.1.6.0 = 17700]", "1.3.6.1.2.1.31.1.1.1.6.0 = 19600]",
					"1.3.6.1.2.1.31.1.1.1.6.0 = 20450]", "1.3.6.1.2.1.31.1.1.1.6.0 = 21200]", "1.3.6.1.2.1.31.1.1.1.6.0 = 22100]");
		
		/* tunnelNumber -> 3211 */
		when(snmpWrapper.snmpGet(Mockito.argThat(new IsOidEqual("1.3.6.1.2.1.31.1.1.1.6.1")), Mockito.argThat(new IsAddressEqual("3.1.1.1"))))
			.thenReturn("1.3.6.1.2.1.31.1.1.1.6.1 = 0]",
					"1.3.6.1.2.1.31.1.1.1.6.1 = 3000]", "1.3.6.1.2.1.31.1.1.1.6.1 = 5000]", "1.3.6.1.2.1.31.1.1.1.6.1 = 9000]",
					"1.3.6.1.2.1.31.1.1.1.6.1 = 12550]", "1.3.6.1.2.1.31.1.1.1.6.1 = 13500]", "1.3.6.1.2.1.31.1.1.1.6.1 = 16500]",
					"1.3.6.1.2.1.31.1.1.1.6.1 = 17050]", "1.3.6.1.2.1.31.1.1.1.6.1 = 17600]", "1.3.6.1.2.1.31.1.1.1.6.1 = 18200]",
					"1.3.6.1.2.1.31.1.1.1.6.1 = 18950]", "1.3.6.1.2.1.31.1.1.1.6.1 = 19600]", "1.3.6.1.2.1.31.1.1.1.6.1 = 20400]");
		
		SNMPWrapperFactory.disableUniqueSnmpWrappperCreationMode();
		SNMPWrapperFactory.setSNMPWrapperInstance(snmpWrapper);
	}
	
	private LocalRVector prepareRVectorWithInitialValues(short asNumber) {
		LocalRVector rVector = new LocalRVector();
		if (asNumber == RECEIVER_AS1_NUMBER) { 
			rVector.setSourceAsNumber(10);
			rVector.addVectorValueForLink(new SimpleLinkID("111", "isp1"), 15000L);
			rVector.addVectorValueForLink(new SimpleLinkID("121", "isp1"), 21000L);
		} else {
	    	rVector.setSourceAsNumber(30);
	    	rVector.addVectorValueForLink(new SimpleLinkID("311", "isp3"), 26000L);
	    	rVector.addVectorValueForLink(new SimpleLinkID("321", "isp3"), 10000L);
		}
    	return rVector;
	}
	
	private List<String> prepareSnmpwalkResponse() {
		return Arrays.asList("1.3.6.1.2.1.31.1.1.1.1.0 = eth0",  
			"1.3.6.1.2.1.31.1.1.1.1.1 = eth1",
			"1.3.6.1.2.1.31.1.1.1.1.2 = eth2",
			"1.3.6.1.2.1.31.1.1.1.1.3 = eth3", 
			"1.3.6.1.2.1.31.1.1.1.1.4 = eth4",
			"1.3.6.1.2.1.31.1.1.1.1.5 = eth5",
			"1.3.6.1.2.1.31.1.1.1.1.6 = eth6",
			"1.3.6.1.2.1.31.1.1.1.1.7 = eth7");
	}
	
	class IsOidEqual extends ArgumentMatcher<OID> {
		private String oid;

		public IsOidEqual(String oid) {
			this.oid = oid;
		}

		@Override
		public boolean matches(Object argument) {
			if(argument instanceof OID) {
				if(((OID) argument).toDottedString().equals(oid)) {
					return true;
				}
			}
			return false;
		}
	}
	
	class IsAddressEqual extends ArgumentMatcher<CommunityTarget> {
		private String address;
		
		public IsAddressEqual(String address) {
			this.address = address;
		}

		@Override
		public boolean matches(Object argument) {
			if(argument instanceof CommunityTarget) {
				if(((CommunityTarget) argument).getAddress().toString().contains(address)) {
					return true;
				}
			}
			return false;
		}
	}
	
	class IsLinkIdStringEqual extends ArgumentMatcher<SimpleLinkID> {
		private String linkID;
		
		public IsLinkIdStringEqual(String linkID) {
			this.linkID = linkID;
		}
		
		@Override
		public boolean matches(Object argument) {
			if (argument instanceof SimpleLinkID) {
				if(((SimpleLinkID)argument).getLocalLinkID().equals(linkID))
					return true;
			}
			return false;
		}
	}
}
