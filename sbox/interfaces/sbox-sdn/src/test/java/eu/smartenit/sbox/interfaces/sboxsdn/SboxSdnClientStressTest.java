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
package eu.smartenit.sbox.interfaces.sboxsdn;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;

import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.CloudDC;
import eu.smartenit.sbox.db.dto.ConfigData;
import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.DC2DCCommunicationID;
import eu.smartenit.sbox.db.dto.Direction;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.SBox;
import eu.smartenit.sbox.db.dto.SDNController;
import eu.smartenit.sdn.interfaces.sboxsdn.URLs;

public class SboxSdnClientStressTest {
	
	@ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(9090);
  
	/**
	 * Creating http responses for matching http requests. 
	 */
    @BeforeClass
    public static void init() {
    	
    	//returning status 200 when receiving POST requests with correct headers
        
        stubFor(post(urlEqualTo(URLs.BASE_PATH + URLs.DTM_CONFIG_DATA_PATH))
        		.withHeader("Accept", equalTo("application/json; q=0.9,*/*;q=0.8"))
        		.withHeader("Content-Type", equalTo( "application/json; charset=UTF-8"))
        		.willReturn(aResponse()
        				.withStatus(200)));

    }
	

	/**
	 * Mocking sdn controller and checking whether received request 
	 * includes serialized r and c vectors.
	 * 
	 * @throws JsonProcessingException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testConfigure() throws JsonProcessingException, InterruptedException {
		ConfigData configData = new ConfigData();
		List<DC2DCCommunication> inCommunicationList = new ArrayList<DC2DCCommunication>();
		DC2DCCommunication dc = new DC2DCCommunication();
		dc.setId(new DC2DCCommunicationID(552542, "+", 2, "fb", 4, "drop"));
		dc.setLocalCloud(new CloudDC("fb", 
				new AS(2, true, null, null, new SBox(new NetworkAddressIPv4("2.2.2.2", 0)), null), 
				new DARouter(new NetworkAddressIPv4("2.2.2.4", 0), "snmp3"),
				null, null));
		dc.setRemoteCloud(new CloudDC("drop", 
				new AS(4, true, null, null, new SBox(new NetworkAddressIPv4("4.4.4.4", 0)), null), 
				null, null, null));
		dc.setTrafficDirection(Direction.incomingTraffic);
		inCommunicationList.add(dc);
		configData.setInCommunicationList(inCommunicationList);
		configData.setOutCommunicationList(null);
		
		SboxSdnClient ss = new SboxSdnClient();
		SDNController sdn = new SDNController();
		sdn.setRestHost(new NetworkAddressIPv4("localhost", 0));
		sdn.setRestPort(9090);
		
		for (int i=0; i<500; i++){
			ss.configure(sdn, configData);
			Thread.sleep(50);
		}
		
		verify(500, postRequestedFor(urlMatching("/smartenit/dtm/config-data")));
	}
	

}
