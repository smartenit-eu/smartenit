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
package eu.smartenit.sbox.ntm.dtm;

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
import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.db.dto.SBox;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.VectorValue;
import eu.smartenit.sbox.db.dto.XVector;
import eu.smartenit.sbox.interfaces.intersbox.client.InterSBoxClient;

/**
 * Includes detailed test methods for workflow triggered by
 * {@link DTMTrafficManager} after receiving updated traffic vectors from QoS
 * Analyzer and Economic Analyzer component.
 * 
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public class XRVectorsUpdateDetailedTest {

	private int asNumber1 = 1;
	private RVector rVector;
	private XVector xVector1, xVector2;
	
	private RemoteSBoxContainer container = mock(RemoteSBoxContainer.class);
	private InterSBoxClient client = mock(InterSBoxClient.class);
	
	@Before
	public void setup() {
    	SBox sbox1 = new SBox(new NetworkAddressIPv4("1.1.1.1", 32));
    	when(container.getRemoteSBoxes(asNumber1)).thenReturn(Arrays.asList(sbox1));
    	
    	InterSBoxClientFactory.disableUniqueClientCreationMode();
    	InterSBoxClientFactory.setClientInstance(client);
    	
    	SBoxThreadHandler.threadService = 
    			Executors.newScheduledThreadPool(SBoxProperties.CORE_POOL_SIZE, new ThreadFactory());
    	
		VectorValue xValue1 = new VectorValue(12, new SimpleLinkID("id1", "isp1"));
		VectorValue xValue2 = new VectorValue(18, new SimpleLinkID("id2", "isp1"));
		xVector1 = new XVector(Arrays.asList(xValue1, xValue2) , asNumber1);

		VectorValue xValue3 = new VectorValue(14, new SimpleLinkID("id1", "isp1"));
		VectorValue xValue4 = new VectorValue(10, new SimpleLinkID("id2", "isp1"));
		xVector2 = new XVector(Arrays.asList(xValue3, xValue4) , asNumber1);
		
		VectorValue rValue1 = new VectorValue(50, new SimpleLinkID("id1", "isp1"));
		VectorValue rValue2 = new VectorValue(80, new SimpleLinkID("id2", "isp1"));
		rVector = new RVector(Arrays.asList(rValue1, rValue2), asNumber1, null);
	}
	
	@Test
	public void shouldDistributeCRVectorsAfterAccountingPeriodEnd() throws Exception {
		DTMTrafficManager manager = new DTMTrafficManager();
		manager.setSBoxContainer(container);
		manager.initialize();
		manager.updateXVector(xVector1);
		manager.updateRVector(rVector);
		
		Thread.sleep(1000);
		ArgumentCaptor<CVector> cVectorArgument = ArgumentCaptor.forClass(CVector.class);
		ArgumentCaptor<RVector> rVectorArgument = ArgumentCaptor.forClass(RVector.class);
		
		verify(client, times(1)).send(any(String.class), anyInt(), cVectorArgument.capture(), rVectorArgument.capture());
		reset(client);
		SimpleLinkID linkID1 = new SimpleLinkID("id1", "isp1");
		SimpleLinkID linkID2 = new SimpleLinkID("id2", "isp1");
		assertEquals(rVector.getVectorValueForLink(linkID1), rVectorArgument.getValue().getVectorValueForLink(linkID1));
		assertEquals(rVector.getVectorValueForLink(linkID2), rVectorArgument.getValue().getVectorValueForLink(linkID2));
		assertEquals(0, cVectorArgument.getValue().getVectorValueForLink(linkID1));
		assertEquals(0, cVectorArgument.getValue().getVectorValueForLink(linkID2));
	}
	
	@Test
	public void shouldDistributeCVectorBeforeAccountingPeriodEndSingleXVector() throws Exception {
		DTMTrafficManager manager = new DTMTrafficManager();
		manager.setSBoxContainer(container);
		manager.initialize();
		manager.updateRVector(rVector);
		manager.updateXVector(xVector2);
		
		Thread.sleep(1000);
		ArgumentCaptor<CVector> cVectorArgument = ArgumentCaptor.forClass(CVector.class);
		
		verify(client, times(1)).send(any(String.class), anyInt(), cVectorArgument.capture());
		reset(client);
		SimpleLinkID linkID1 = new SimpleLinkID("id1", "isp1");
		SimpleLinkID linkID2 = new SimpleLinkID("id2", "isp1");
		assertEquals(-4, cVectorArgument.getValue().getVectorValueForLink(linkID1));
		assertEquals(4, cVectorArgument.getValue().getVectorValueForLink(linkID2));
	}
	
	@Test
	public void shouldDistributeCVectorBeforeAccountingPeriodEnd() throws Exception {
		DTMTrafficManager manager = new DTMTrafficManager();
		manager.setSBoxContainer(container);
		manager.initialize();
		manager.updateRVector(rVector);
		manager.updateXVector(xVector1);
		manager.updateXVector(xVector2);
		
		Thread.sleep(1000);
		ArgumentCaptor<CVector> cVectorArgument = ArgumentCaptor.forClass(CVector.class);
		
		verify(client, times(2)).send(any(String.class), anyInt(), cVectorArgument.capture());
		reset(client);
		SimpleLinkID linkID1 = new SimpleLinkID("id1", "isp1");
		SimpleLinkID linkID2 = new SimpleLinkID("id2", "isp1");
		assertEquals(-5, cVectorArgument.getValue().getVectorValueForLink(linkID1));
		assertEquals(5, cVectorArgument.getValue().getVectorValueForLink(linkID2));
	}
	
}
