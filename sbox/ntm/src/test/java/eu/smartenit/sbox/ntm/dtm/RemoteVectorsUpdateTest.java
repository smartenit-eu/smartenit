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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import eu.smartenit.sbox.db.dao.DC2DCCommunicationDAO;
import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.ConfigData;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.db.dto.SDNController;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.interfaces.sboxsdn.SboxSdnClient;

/**
 * Includes test methods for workflow triggered by
 * {@link DTMRemoteVectorsReceiver} class after receiving new compensation and
 * reference vectors from remote SBoxes.
 * 
 * @author Lukasz Lopatowski
 * @version 1.0
 * 
 */
public class RemoteVectorsUpdateTest {
	
	private static final String LINK1_ID = "link1";
	private static final String ISP1_ID = "isp1";
	
	private CVector cVector = new CVector();
	private RVector rVector = new RVector();
	
	private SboxSdnClient sdnClient = mock(SboxSdnClient.class);
	private DC2DCCommunicationDAO dao = mock(DC2DCCommunicationDAO.class);
	
	@Before
	public void setup() {
		cVector.setSourceAsNumber(1);
		cVector.addVectorValueForLink(new SimpleLinkID(LINK1_ID, ISP1_ID), 500L);
		rVector.setSourceAsNumber(1);
    	rVector.addVectorValueForLink(new SimpleLinkID(LINK1_ID, ISP1_ID), 1000L);
    	
    	SDNClientFactory.disableUniqueClientCreationMode();
    	SDNClientFactory.setClientInstance(sdnClient);
    	
    	when(dao.findAllDC2DCCommunicationsCloudsTunnels()).thenReturn(DBStructuresBuilder.communicationsOnTrafficSender);
    	DAOFactory.setDC2DCCommunicationDAO(dao);
	}
	
	@Test
	public void shouldInitControllers() {
		DTMRemoteVectorsReceiver receiver = new DTMRemoteVectorsReceiver();
		receiver.initialize();
		
		verify(sdnClient, times(2)).configure(any(SDNController.class), any(ConfigData.class));
		reset(sdnClient);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowExceptionOnCVectorNull() {
		cVector = null;
		
		DTMRemoteVectorsReceiver receiver = new DTMRemoteVectorsReceiver();
		receiver.receive(cVector);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowExceptionOnRCVectorsNull() {
		rVector = null;
		
		DTMRemoteVectorsReceiver receiver = new DTMRemoteVectorsReceiver();
		receiver.receive(cVector, rVector);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowExceptionOnInvalidCVectorASNumberArgument() {
		cVector.setSourceAsNumber(0);
		
		DTMRemoteVectorsReceiver receiver = new DTMRemoteVectorsReceiver();
		receiver.receive(cVector);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowExceptionOnInvalidCRVectorASNumberArgument() {
		rVector.setSourceAsNumber(0);
		
		DTMRemoteVectorsReceiver receiver = new DTMRemoteVectorsReceiver();
		receiver.receive(cVector, rVector);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowExceptionOnInvalidCVectorValuesListArgument() {
		cVector.setVectorValues(null);
		
		DTMRemoteVectorsReceiver receiver = new DTMRemoteVectorsReceiver();
		receiver.receive(cVector, rVector);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowExceptionOnInvalidCRVectorValuesListArgument() {
		rVector.setVectorValues(null);
		
		DTMRemoteVectorsReceiver receiver = new DTMRemoteVectorsReceiver();
		receiver.receive(cVector, rVector);
	}
	
	@Test
	public void shouldProcessAndNotUpdateWhenNoSDNControllers() {
		when(dao.findAllDC2DCCommunicationsCloudsTunnels()).thenReturn(new ArrayList<DC2DCCommunication>());
		DTMRemoteVectorsReceiver receiver = new DTMRemoteVectorsReceiver();
		receiver.initialize();
		receiver.receive(cVector, rVector);
		receiver.receive(cVector);

		verify(sdnClient, times(0)).configure(any(SDNController.class), any(ConfigData.class));
		verify(sdnClient, times(0)).distribute(any(SDNController.class), any(CVector.class), any(RVector.class));
		verify(sdnClient, times(0)).distribute(any(SDNController.class), any(CVector.class));
		reset(sdnClient);
	}
	
	@Test
	public void shouldProcessAndUpdateAfterRemoteCVectorReceived() {
		DTMRemoteVectorsReceiver receiver = new DTMRemoteVectorsReceiver();
		receiver.initialize();
		
		receiver.receive(cVector);

		verify(sdnClient, times(2)).distribute(any(SDNController.class), any(CVector.class));
		reset(sdnClient);
	}
	
	@Test
	public void shouldProcessAndUpdateAfterRemoteCAndRVectorsReceived() {
		DTMRemoteVectorsReceiver receiver = new DTMRemoteVectorsReceiver();
		receiver.initialize();
		
		receiver.receive(cVector, rVector);
		receiver.receive(cVector);

		verify(sdnClient, times(2)).distribute(any(SDNController.class), any(CVector.class), any(RVector.class));
		verify(sdnClient, times(2)).distribute(any(SDNController.class), any(CVector.class));
		reset(sdnClient);
	}
	
}
