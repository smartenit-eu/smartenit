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

import static org.junit.Assert.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.util.CharsetUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;

import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.CloudDC;
import eu.smartenit.sbox.db.dto.ConfigData;
import eu.smartenit.sbox.db.dto.DARouter;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.DC2DCCommunicationID;
import eu.smartenit.sbox.db.dto.Direction;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.db.dto.SBox;
import eu.smartenit.sbox.db.dto.SDNController;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.ThetaCoefficient;
import eu.smartenit.sbox.db.dto.VectorValue;
import eu.smartenit.sdn.interfaces.sboxsdn.RCVectors;
import eu.smartenit.sdn.interfaces.sboxsdn.Serialization;
import eu.smartenit.sdn.interfaces.sboxsdn.URLs;

public class SboxSdnClientTest {
	
	@ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(9090);
  
	/**
	 * Creating http responses for matching http requests. 
	 */
    @BeforeClass
    public static void init() {
    	
    	//returning status 200 when receiving POST requests with correct headers
    	//for both rest apis
    	
        stubFor(post(urlEqualTo(URLs.BASE_PATH + URLs.DTM_R_C_VECTORS_PATH))
        		.withHeader("Accept", equalTo("application/json; q=0.9,*/*;q=0.8"))
        		.withHeader("Content-Type", equalTo( "application/json; charset=UTF-8"))
        		.willReturn(aResponse()
        				.withStatus(200)));
        
        stubFor(post(urlEqualTo(URLs.BASE_PATH + URLs.DTM_CONFIG_DATA_PATH))
        		.withHeader("Accept", equalTo("application/json; q=0.9,*/*;q=0.8"))
        		.withHeader("Content-Type", equalTo( "application/json; charset=UTF-8"))
        		.willReturn(aResponse()
        				.withStatus(200)));

    }
	
    /**
	 * Checking whether http request for c vector is correct. 
	 * 
     * @throws JsonProcessingException 
     * @throws URISyntaxException 
	 */
	@Test
	public void testPrepareHttpRequestCVector() 
			throws JsonProcessingException, URISyntaxException {
		
		CVector cVector = new CVector();
		cVector.setSourceAsNumber(1);
		List<VectorValue> vectorValues = new ArrayList<VectorValue>();
		vectorValues.add(new VectorValue(1000000, new SimpleLinkID("link1", "etc")));
		vectorValues.add(new VectorValue(2000000, new SimpleLinkID("link2", "etc")));
		cVector.setVectorValues(vectorValues);
		
		RCVectors rcVectors = new RCVectors(null, cVector);
		String content = Serialization.serialize(rcVectors);
		
		SboxSdnClient ss = new SboxSdnClient();
		SDNController sdn = new SDNController();
		sdn.setRestHost(new NetworkAddressIPv4("98.33.33.33", 0));
		sdn.setRestPort(9090);
		
		URI uri = new URI("http://" + sdn.getRestHost().getPrefix() 
				+ ":" + sdn.getRestPort() 
				+ URLs.BASE_PATH + URLs.DTM_R_C_VECTORS_PATH);
		
		DefaultFullHttpRequest request = ss.prepareHttpRequest(uri, content);
		assertEquals(request.getUri(), "/smartenit/dtm/r-c-vectors");
		assertEquals(request.headers().get("Accept"), "application/json; q=0.9,*/*;q=0.8");
		assertEquals(request.headers().get("Content-Type"), "application/json; charset=UTF-8");
		assertEquals(request.headers().get("Host"), "98.33.33.33");
		assertEquals(request.headers().get("Pragma"), "no-cache");
		assertEquals(request.headers().get("Cache-Control"), "no-cache");
		assertEquals(request.content().toString(CharsetUtil.UTF_8), content);
		
	}
	
	/**
	 * Checking whether http request for c and r vectors is correct. 
	 * 
     * @throws JsonProcessingException 
     * @throws URISyntaxException 
	 */
	@Test
	public void testPrepareHttpRequestRCVectors() 
			throws JsonProcessingException, URISyntaxException {
		
		CVector cVector = new CVector();
		cVector.setSourceAsNumber(1);
		List<VectorValue> vectorValues = new ArrayList<VectorValue>();
		vectorValues.add(new VectorValue(1000000, new SimpleLinkID("link1", "etc")));
		vectorValues.add(new VectorValue(2000000, new SimpleLinkID("link2", "etc")));
		cVector.setVectorValues(vectorValues);
		
		RVector rVector = new RVector();
		vectorValues = new ArrayList<VectorValue>();
		vectorValues.add(new VectorValue(2, new SimpleLinkID("link3", "etc")));
		vectorValues.add(new VectorValue(3, new SimpleLinkID("link4", "etc")));
		rVector.setVectorValues(vectorValues);
		rVector.setSourceAsNumber(3);
		List<ThetaCoefficient> thetas = new ArrayList<ThetaCoefficient>();
		thetas.add(new ThetaCoefficient(0.53253, new DC2DCCommunicationID(552542, "+", 2,
				"fb", 4, "drop")));
		rVector.setThetaCollection(thetas);
		
		RCVectors rcVectors = new RCVectors(rVector, cVector);
		String content = Serialization.serialize(rcVectors);
		
		SboxSdnClient ss = new SboxSdnClient();
		SDNController sdn = new SDNController();
		sdn.setRestHost(new NetworkAddressIPv4("98.33.33.33", 0));
		sdn.setRestPort(9090);
		
		URI uri = new URI("http://" + sdn.getRestHost().getPrefix() 
				+ ":" + sdn.getRestPort() 
				+ URLs.BASE_PATH + URLs.DTM_R_C_VECTORS_PATH);
		
		DefaultFullHttpRequest request = ss.prepareHttpRequest(uri, content);
		assertEquals(request.getUri(), "/smartenit/dtm/r-c-vectors");
		assertEquals(request.headers().get("Accept"), "application/json; q=0.9,*/*;q=0.8");
		assertEquals(request.headers().get("Content-Type"), "application/json; charset=UTF-8");
		assertEquals(request.headers().get("Host"), "98.33.33.33");
		assertEquals(request.headers().get("Pragma"), "no-cache");
		assertEquals(request.headers().get("Cache-Control"), "no-cache");
		assertEquals(request.content().toString(CharsetUtil.UTF_8), content);
		
	}
	
	
	/**
	 * Checking whether http request for config data is correct. 
	 * 
     * @throws JsonProcessingException 
     * @throws URISyntaxException 
	 */
	@Test
	public void testPrepareHttpRequestConfigData() 
			throws JsonProcessingException, URISyntaxException {
		
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
		sdn.setRestHost(new NetworkAddressIPv4("98.33.33.33", 0));
		sdn.setRestPort(9090);
		
		String content = Serialization.serialize(configData);
		
		URI uri = new URI("http://" + sdn.getRestHost().getPrefix() 
				+ ":" + sdn.getRestPort() 
				+ URLs.BASE_PATH + URLs.DTM_CONFIG_DATA_PATH);
		
		DefaultFullHttpRequest request = ss.prepareHttpRequest(uri, content);
		assertEquals(request.getUri(), "/smartenit/dtm/config-data");
		assertEquals(request.headers().get("Accept"), "application/json; q=0.9,*/*;q=0.8");
		assertEquals(request.headers().get("Content-Type"), "application/json; charset=UTF-8");
		assertEquals(request.headers().get("Host"), "98.33.33.33");
		assertEquals(request.headers().get("Pragma"), "no-cache");
		assertEquals(request.headers().get("Cache-Control"), "no-cache");
		assertEquals(request.content().toString(CharsetUtil.UTF_8), content);
		
	}
	
	
	/**
	 * Mocking sdn controller and checking whether received request 
	 * includes serialized c vector.
	 * 
	 * @throws JsonProcessingException 
	 */
	@Test
	public void testDistributeCVector() throws JsonProcessingException {
		CVector cVector = new CVector();
		cVector.setSourceAsNumber(1);
		List<VectorValue> vectorValues = new ArrayList<VectorValue>();
		vectorValues.add(new VectorValue(1000000, new SimpleLinkID("link1", "etc")));
		vectorValues.add(new VectorValue(2000000, new SimpleLinkID("link2", "etc")));
		cVector.setVectorValues(vectorValues);
		
		SboxSdnClient ss = new SboxSdnClient();
		SDNController sdn = new SDNController();
		sdn.setRestHost(new NetworkAddressIPv4("localhost", 0));
		sdn.setRestPort(9090);
		
		ss.distribute(sdn, cVector);
		
		RCVectors rcVectors = new RCVectors(null, cVector);
		String content = Serialization.serialize(rcVectors);
		
		verify(postRequestedFor(urlMatching("/smartenit/dtm/r-c-vectors"))
				.withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
				.withHeader("Accept", equalTo("application/json; q=0.9,*/*;q=0.8"))
				.withRequestBody(equalTo(content))
				);
	}
	
	/**
	 * Mocking sdn controller and checking whether received request 
	 * includes serialized r and c vectors.
	 * 
	 * @throws JsonProcessingException 
	 */
	@Test
	public void testDistributeCRVectors() throws JsonProcessingException {
		CVector cVector = new CVector();
		cVector.setSourceAsNumber(1);
		List<VectorValue> vectorValues = new ArrayList<VectorValue>();
		vectorValues.add(new VectorValue(1000000, new SimpleLinkID("link1", "etc")));
		vectorValues.add(new VectorValue(2000000, new SimpleLinkID("link2", "etc")));
		cVector.setVectorValues(vectorValues);
		
		RVector rVector = new RVector();
		vectorValues = new ArrayList<VectorValue>();
		vectorValues.add(new VectorValue(2, new SimpleLinkID("link3", "etc")));
		vectorValues.add(new VectorValue(3, new SimpleLinkID("link4", "etc")));
		rVector.setVectorValues(vectorValues);
		rVector.setSourceAsNumber(3);
		List<ThetaCoefficient> thetas = new ArrayList<ThetaCoefficient>();
		thetas.add(new ThetaCoefficient(0.53253, new DC2DCCommunicationID(552542, "+", 2,
				"fb", 4, "drop")));
		rVector.setThetaCollection(thetas);
		
		SboxSdnClient ss = new SboxSdnClient();
		SDNController sdn = new SDNController();
		sdn.setRestHost(new NetworkAddressIPv4("localhost", 0));
		sdn.setRestPort(9090);
		
		ss.distribute(sdn, cVector, rVector);
		
		RCVectors rcVectors = new RCVectors(rVector, cVector);
		String content = Serialization.serialize(rcVectors);
		
		verify(postRequestedFor(urlMatching("/smartenit/dtm/r-c-vectors"))
				.withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
				.withHeader("Accept", equalTo("application/json; q=0.9,*/*;q=0.8"))
				.withRequestBody(equalTo(content))
				);
	}
	
	/**
	 * Mocking sdn controller and checking whether received request 
	 * includes serialized r and c vectors.
	 * 
	 * @throws JsonProcessingException 
	 */
	@Test
	public void testConfigure() throws JsonProcessingException {
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
		
		ss.configure(sdn, configData);
		
		String content = Serialization.serialize(configData);
		
		verify(postRequestedFor(urlMatching("/smartenit/dtm/config-data"))
				.withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
				.withHeader("Accept", equalTo("application/json; q=0.9,*/*;q=0.8"))
				.withRequestBody(equalTo(content))
				);
	}
	

}
