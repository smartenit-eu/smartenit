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
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.core.JsonProcessingException;

import eu.smartenit.sbox.commons.SBoxProperties;
import eu.smartenit.sbox.commons.SBoxThreadHandler;
import eu.smartenit.sbox.commons.ThreadFactory;
import eu.smartenit.sbox.db.dao.LinkDAO;
import eu.smartenit.sbox.db.dao.SystemControlParametersDAO;
import eu.smartenit.sbox.db.dao.TimeScheduleParametersDAO;
import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.ChargingRule;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.db.dto.SBox;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.SystemControlParameters;
import eu.smartenit.sbox.db.dto.TimeScheduleParameters;
import eu.smartenit.sbox.interfaces.intersbox.client.InterSBoxClient;
import eu.smartenit.sbox.ntm.dtm.DAOFactory;

/**
 * Tests loading of default reference vector from file specified in SBox
 * configuration file.
 * 
 * @author Lukasz Lopatowski
 * @version 3.0
 */
public class LoadingDefaultRVectorDuringInitTest {
	private static final String LINK1_ID = "link1";
	private static final String LINK2_ID = "link2";
	private static final String ISP1_ID = "isp1";
	
	private int asNumber1 = 1;
	private RemoteSBoxContainer container = mock(RemoteSBoxContainer.class);
	private InterSBoxClient client = mock(InterSBoxClient.class);
	private LinkDAO dao = mock(LinkDAO.class);
	private SystemControlParametersDAO scpDAO = mock(SystemControlParametersDAO.class);
	private TimeScheduleParametersDAO tspDAO = mock(TimeScheduleParametersDAO.class);
	
	@Before
	public void setup() throws JsonProcessingException {
		SimpleLinkID linkID1 = new SimpleLinkID(LINK1_ID, ISP1_ID);
		SimpleLinkID linkID2 = new SimpleLinkID(LINK2_ID, ISP1_ID);
		
    	SBox sbox1 = new SBox(new NetworkAddressIPv4("1.1.1.1", 32));
    	when(container.getRemoteSBoxes(asNumber1)).thenReturn(Arrays.asList(sbox1));
    	
    	InterSBoxClientFactory.disableUniqueClientCreationMode();
    	InterSBoxClientFactory.setClientInstance(client);
    	
       	when(dao.findById(linkID1)).thenReturn(
    			new Link(linkID1, null, null, 0, null, null, null, null, null, new NetworkAddressIPv4("10.10.10.10", 24)));
    	when(dao.findById(linkID2)).thenReturn(
    			new Link(linkID2, null, null, 0, null, null, null, null, null, new NetworkAddressIPv4("20.20.20.20", 24)));
    	
    	SystemControlParameters scp = new SystemControlParameters(ChargingRule.volume, null, 0.1);
    	when(scpDAO.findLast()).thenReturn(scp);
    	TimeScheduleParameters tsp = new TimeScheduleParameters();
    	tsp.setCompensationPeriod(12);
    	tsp.setReportPeriodDTM(3);
    	when(tspDAO.findLast()).thenReturn(tsp);
    	
    	DAOFactory.setSCPDAOInstance(scpDAO);
    	DAOFactory.setTSPDAOInstance(tspDAO);
    	DAOFactory.setLinkDAOInstance(dao);
    	
    	SBoxThreadHandler.threadService = 
    			Executors.newScheduledThreadPool(SBoxProperties.CORE_POOL_SIZE, new ThreadFactory());
	}
	
	@Test
	public void shouldLoadCorrectRVectorAndUpdateRemoteSBox() throws Exception {
		SBoxProperties.DEFAULT_REF_VECTOR_FILE = "src/test/resources/test-rvector.json";
		DTMTrafficManager dtm = new DTMTrafficManager();
		dtm.setSBoxContainer(container);
		dtm.initialize();
		
		Thread.sleep(1000);
		ArgumentCaptor<CVector> cVectorArgument = ArgumentCaptor.forClass(CVector.class);
		ArgumentCaptor<RVector> rVectorArgument = ArgumentCaptor.forClass(RVector.class);
		verify(client, times(1)).send(any(String.class), anyInt(), cVectorArgument.capture(), rVectorArgument.capture());
		reset(client);
		
		assertEquals(0, cVectorArgument.getValue().getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("10.10.10.10", 24)));
		assertEquals(0, cVectorArgument.getValue().getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("20.20.20.20", 24)));
		assertEquals(10000000, rVectorArgument.getValue().getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("10.10.10.10", 24)));
		assertEquals(20000000, rVectorArgument.getValue().getVectorValueForTunnelEndPrefix(new NetworkAddressIPv4("20.20.20.20", 24)));
	}
	
	@Test
	public void shouldNotUpdateRemoteSBoxWhenRVectorFileNotExists() throws Exception {
		SBoxProperties.DEFAULT_REF_VECTOR_FILE = "src/test/resources/test-error-rvector.json";
		DTMTrafficManager dtm = new DTMTrafficManager();
		dtm.setSBoxContainer(container);
		dtm.initialize();
		
		Thread.sleep(1000);
		verifyZeroInteractions(client);
	}
	
	@After
	public void setDefaultRefVectorFilePathToNotExistingFile() {
		SBoxProperties.DEFAULT_REF_VECTOR_FILE = "src/test/resources/test-error-rvector.json";
	}

}
