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
package eu.smartenit.sdn.interfaces.sboxsdn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.CloudDC;
import eu.smartenit.sbox.db.dto.ConfigData;
import eu.smartenit.sbox.db.dto.ConfigDataEntry;
import eu.smartenit.sbox.db.dto.EndAddressPairTunnelID;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.NetworkAddressIPv4;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.db.dto.Tunnel;
import eu.smartenit.sbox.db.dto.TunnelInfo;

/**
 * Test serialization.
 *
 * @author Piotr Wydrych
 * @author Lukasz Lopatowski
 * @version 1.2
 */
public class SerializationTest {

    public static final int TUNNEL_END_PREFIX_LENGHT = 24;
	public static final String TUNNEL_END_PREFIX_1 = "10.1.1.0";
    public static final String TUNNEL_END_PREFIX_2 = "10.1.2.0";
	public static final String TUNNEL_END_PREFIX_3 = "10.2.1.0";
    public static final String TUNNEL_END_PREFIX_4 = "10.2.2.0";
    public static final String REMOTE_TUNNEL_END_ADDRESS_1 = "10.1.1.1";
    public static final String REMOTE_TUNNEL_END_ADDRESS_2 = "10.1.2.1";
    public static final String REMOTE_TUNNEL_END_ADDRESS_3 = "10.2.1.1";
    public static final String REMOTE_TUNNEL_END_ADDRESS_4 = "10.2.2.1";
    public static final String LOCAL_TUNNEL_END_ADDRESS_1 = "20.1.1.1";
    public static final String LOCAL_TUNNEL_END_ADDRESS_2 = "20.1.1.2";
    public static final String LOCAL_TUNNEL_END_ADDRESS_3 = "20.1.1.3";
    public static final String LOCAL_TUNNEL_END_ADDRESS_4 = "20.1.1.4";
    
    public static final int REMOTE_DC_PREFIX_LENGHT = 24;
	public static final String REMOTE_DC_PREFIX_1 = "10.10.1.0";
	public static final String REMOTE_DC_PREFIX_2 = "10.10.2.0";
	public static final String REMOTE_DC_PREFIX_3 = "10.10.3.0";
	
	public static final String DA_ROUTER_OF_DPID1 = "00:00:00:00:00:00:00:01";
	
	public static final String TUNNEL_NAME_11 = "TUNNEL11";
	public static final String TUNNEL_NAME_12 = "TUNNEL12";
	public static final String TUNNEL_NAME_21 = "TUNNEL21";
	public static final String TUNNEL_NAME_22 = "TUNNEL22";

    public static final int SOURCE_AS_NUMBER = 1;

	private static final String SERIALIZED_R_C_VECTORS
		= "{" +
			"\"referenceVector\":" +
				"{\"vectorValues\":" +
					"[{\"value\":1000000000," + 
					"\"tunnelEndPrefix\":{\"prefix\":\"" + TUNNEL_END_PREFIX_1 + "\",\"prefixLength\":" + TUNNEL_END_PREFIX_LENGHT + "}}," +
					"{\"value\":2000000000," + 
					"\"tunnelEndPrefix\":{\"prefix\":\"" + TUNNEL_END_PREFIX_2 + "\",\"prefixLength\":" + TUNNEL_END_PREFIX_LENGHT + "}}]," +
				"\"sourceAsNumber\":" + SOURCE_AS_NUMBER + "," +
				"\"thetaCollection\":null}," +
			"" +
			"\"compensationVector\":" +
				"{\"vectorValues\":" +
					"[{\"value\":1000000," + 
					"\"tunnelEndPrefix\":{\"prefix\":\"" + TUNNEL_END_PREFIX_1 + "\",\"prefixLength\":" + TUNNEL_END_PREFIX_LENGHT + "}}," +
					"{\"value\":-1000000," + 
					"\"tunnelEndPrefix\":{\"prefix\":\"" + TUNNEL_END_PREFIX_2 + "\",\"prefixLength\":" + TUNNEL_END_PREFIX_LENGHT + "}}]," +
				"\"sourceAsNumber\":" + SOURCE_AS_NUMBER + "}" +
		"}";
    
    private static final String SERIALIZED_CONFIG_DATA
    	= "{" +
    		"\"entries\":" +
    			"[{\"remoteDcPrefix\":{\"prefix\":\"" + REMOTE_DC_PREFIX_1 + "\",\"prefixLength\":24}," +
    			"\"daRouterOfDPID\":\"" + DA_ROUTER_OF_DPID1 + "\"," +
    			"\"tunnels\":" +
    				"[{\"tunnelID\":" + 
    					"{\"tunnelName\":\"" + TUNNEL_NAME_11 + "\"," +
    					"\"localTunnelEndAddress\":{\"prefix\":\"" + LOCAL_TUNNEL_END_ADDRESS_1 + "\",\"prefixLength\":32}," + 
    					"\"remoteTunnelEndAddress\":{\"prefix\":\"" + REMOTE_TUNNEL_END_ADDRESS_1 + "\",\"prefixLength\":32}}," +
    					"\"daRouterOfPortNumber\":1}," +
    				"{\"tunnelID\":" + 
    					"{\"tunnelName\":\"" + TUNNEL_NAME_12 + "\"," + 
    					"\"localTunnelEndAddress\":{\"prefix\":\"" + LOCAL_TUNNEL_END_ADDRESS_2 +  "\",\"prefixLength\":32}," + 
    					"\"remoteTunnelEndAddress\":{\"prefix\":\"" + REMOTE_TUNNEL_END_ADDRESS_2 + "\",\"prefixLength\":32}}," + 
    					"\"daRouterOfPortNumber\":2}]}," +
    			"{\"remoteDcPrefix\":{\"prefix\":\"" + REMOTE_DC_PREFIX_2 + "\",\"prefixLength\":24}," +
    			"\"daRouterOfDPID\":\"" + DA_ROUTER_OF_DPID1 + "\"," +
    			"\"tunnels\":" +
    				"[{\"tunnelID\":" + 
    					"{\"tunnelName\":\"" + TUNNEL_NAME_11 + "\"," +
    					"\"localTunnelEndAddress\":{\"prefix\":\"" + LOCAL_TUNNEL_END_ADDRESS_1 + "\",\"prefixLength\":32}," + 
    					"\"remoteTunnelEndAddress\":{\"prefix\":\"" + REMOTE_TUNNEL_END_ADDRESS_1 + "\",\"prefixLength\":32}}," + 
    					"\"daRouterOfPortNumber\":3}," +
    				 "{\"tunnelID\":" + 
    					"{\"tunnelName\":\"" + TUNNEL_NAME_12 + "\"," + 
    					"\"localTunnelEndAddress\":{\"prefix\":\"" + LOCAL_TUNNEL_END_ADDRESS_2 + "\",\"prefixLength\":32}," + 
    					"\"remoteTunnelEndAddress\":{\"prefix\":\"" + REMOTE_TUNNEL_END_ADDRESS_2 + "\",\"prefixLength\":32}}," + 
    					"\"daRouterOfPortNumber\":4}]}," +
    			"{\"remoteDcPrefix\":{\"prefix\":\"" + REMOTE_DC_PREFIX_3 + "\",\"prefixLength\":24}," +
    			"\"daRouterOfDPID\":\"" + DA_ROUTER_OF_DPID1 + "\"," +
    			"\"tunnels\":" +
    				 "[{\"tunnelID\":" + 
    				 	"{\"tunnelName\":\"" + TUNNEL_NAME_21 + "\"," + 
    				 	"\"localTunnelEndAddress\":{\"prefix\":\"" + LOCAL_TUNNEL_END_ADDRESS_3 + "\",\"prefixLength\":32}," + 
    				 	"\"remoteTunnelEndAddress\":{\"prefix\":\"" + REMOTE_TUNNEL_END_ADDRESS_3 + "\",\"prefixLength\":32}}," + 
    				 	"\"daRouterOfPortNumber\":1}," +
    			     "{\"tunnelID\":" + 
    				 	"{\"tunnelName\":\"" + TUNNEL_NAME_22 + "\"," + 
    				 	"\"localTunnelEndAddress\":{\"prefix\":\"" + LOCAL_TUNNEL_END_ADDRESS_4 + "\",\"prefixLength\":32}," + 
    				 	"\"remoteTunnelEndAddress\":{\"prefix\":\"" + REMOTE_TUNNEL_END_ADDRESS_4 + "\",\"prefixLength\":32}}," + 
    				 	"\"daRouterOfPortNumber\":2}]}" +
    		"]}";

    /**
     * Test serialization of {@link RCVectors}.
     *
     * @throws Exception
     */
    @Test
    public void testSerializeRCVectors() throws Exception {
        String text;

        NetworkAddressIPv4 tunnelEndPrefix1 = new NetworkAddressIPv4(TUNNEL_END_PREFIX_1, TUNNEL_END_PREFIX_LENGHT);
        NetworkAddressIPv4 tunnelEndPrefix2 = new NetworkAddressIPv4(TUNNEL_END_PREFIX_2, TUNNEL_END_PREFIX_LENGHT);
        
        RCVectors vectors = new RCVectors(new RVector(), new CVector());
        vectors.getReferenceVector().addVectorValueForTunnelEndPrefix(tunnelEndPrefix1, 1000000000);
        vectors.getReferenceVector().addVectorValueForTunnelEndPrefix(tunnelEndPrefix2, 2000000000);
        vectors.getReferenceVector().setSourceAsNumber(SOURCE_AS_NUMBER);
        vectors.getReferenceVector().setThetaCollection(null);
        vectors.getCompensationVector().addVectorValueForTunnelEndPrefix(tunnelEndPrefix1, 1000000);
        vectors.getCompensationVector().addVectorValueForTunnelEndPrefix(tunnelEndPrefix2, -1000000);
        vectors.getCompensationVector().setSourceAsNumber(SOURCE_AS_NUMBER);
        text = Serialization.serialize(vectors);
        
        JSONAssert.assertEquals(SERIALIZED_R_C_VECTORS, text, true);
    }
    
    /**
     * Test serialization of {@link ConfigData}.
     *
     * @throws Exception
     */
    @Test
    public void testSerializeConfigData() throws Exception {
        String text;
        ConfigData configData = new ConfigData();
        
        ConfigDataEntry entry1 = new ConfigDataEntry();
        entry1.setRemoteDcPrefix(new NetworkAddressIPv4(REMOTE_DC_PREFIX_1, REMOTE_DC_PREFIX_LENGHT));
        entry1.setDaRouterOfDPID(DA_ROUTER_OF_DPID1);
        TunnelInfo tunnel11 = new TunnelInfo(
        		new EndAddressPairTunnelID(
        				TUNNEL_NAME_11, 
        				new NetworkAddressIPv4(LOCAL_TUNNEL_END_ADDRESS_1, 32), 
        				new NetworkAddressIPv4(REMOTE_TUNNEL_END_ADDRESS_1, 32)),
        				1);
        TunnelInfo tunnel12 = new TunnelInfo(
        		new EndAddressPairTunnelID(
        				TUNNEL_NAME_12, 
        				new NetworkAddressIPv4(LOCAL_TUNNEL_END_ADDRESS_2, 32), 
        				new NetworkAddressIPv4(REMOTE_TUNNEL_END_ADDRESS_2, 32)), 
        				2);
        entry1.setTunnels(Arrays.asList(tunnel11, tunnel12));
        
        ConfigDataEntry entry2 = new ConfigDataEntry();
        entry2.setRemoteDcPrefix(new NetworkAddressIPv4(REMOTE_DC_PREFIX_2, REMOTE_DC_PREFIX_LENGHT));
        entry2.setDaRouterOfDPID(DA_ROUTER_OF_DPID1);
        TunnelInfo tunnel21 = new TunnelInfo(
        		new EndAddressPairTunnelID(
        				TUNNEL_NAME_11, 
        				new NetworkAddressIPv4(LOCAL_TUNNEL_END_ADDRESS_1, 32), 
        				new NetworkAddressIPv4(REMOTE_TUNNEL_END_ADDRESS_1, 32)),
        				3);
        TunnelInfo tunnel22 = new TunnelInfo(
        		new EndAddressPairTunnelID(
        				TUNNEL_NAME_12, 
        				new NetworkAddressIPv4(LOCAL_TUNNEL_END_ADDRESS_2, 32), 
        				new NetworkAddressIPv4(REMOTE_TUNNEL_END_ADDRESS_2, 32)), 
        				4);
        entry2.setTunnels(Arrays.asList(tunnel21, tunnel22));
        
        ConfigDataEntry entry3 = new ConfigDataEntry();
        entry3.setRemoteDcPrefix(new NetworkAddressIPv4(REMOTE_DC_PREFIX_3, REMOTE_DC_PREFIX_LENGHT));
        entry3.setDaRouterOfDPID(DA_ROUTER_OF_DPID1);
        TunnelInfo tunnel31 = new TunnelInfo(
        		new EndAddressPairTunnelID(
        				TUNNEL_NAME_21, 
        				new NetworkAddressIPv4(LOCAL_TUNNEL_END_ADDRESS_3, 32), 
        				new NetworkAddressIPv4(REMOTE_TUNNEL_END_ADDRESS_3, 32)),
        				1);
        TunnelInfo tunnel32 = new TunnelInfo(
        		new EndAddressPairTunnelID(
        				TUNNEL_NAME_22, 
        				new NetworkAddressIPv4(LOCAL_TUNNEL_END_ADDRESS_4, 32), 
        				new NetworkAddressIPv4(REMOTE_TUNNEL_END_ADDRESS_4, 32)), 
        				2);
        entry3.setTunnels(Arrays.asList(tunnel31, tunnel32));
        
        configData.setEntries(Arrays.asList(entry1, entry2, entry3));
        
        text = Serialization.serialize(configData);
        
        JSONAssert.assertEquals(SERIALIZED_CONFIG_DATA, text, true);
    }

    /**
     * Test deserialization of {@link RCVectors}.
     *
     * @throws Exception
     */
    @Test
    public void testDeserializeRCVectors() throws Exception {
        RCVectors vectors = Serialization.deserialize(SERIALIZED_R_C_VECTORS, RCVectors.class);
        
        NetworkAddressIPv4 tunnelEndPrefix1 = new NetworkAddressIPv4(TUNNEL_END_PREFIX_1, TUNNEL_END_PREFIX_LENGHT);
        NetworkAddressIPv4 tunnelEndPrefix2 = new NetworkAddressIPv4(TUNNEL_END_PREFIX_2, TUNNEL_END_PREFIX_LENGHT);
        
        Assert.assertEquals(1000000000, vectors.getReferenceVector().getVectorValueForTunnelEndPrefix(tunnelEndPrefix1));
        Assert.assertEquals(2000000000, vectors.getReferenceVector().getVectorValueForTunnelEndPrefix(tunnelEndPrefix2));
        Assert.assertEquals(SOURCE_AS_NUMBER, vectors.getReferenceVector().getSourceAsNumber());
        Assert.assertNull(vectors.getReferenceVector().getThetaCollection());
        Assert.assertEquals(1000000, vectors.getCompensationVector().getVectorValueForTunnelEndPrefix(tunnelEndPrefix1));
        Assert.assertEquals(-1000000, vectors.getCompensationVector().getVectorValueForTunnelEndPrefix(tunnelEndPrefix2));
        Assert.assertEquals(SOURCE_AS_NUMBER, vectors.getCompensationVector().getSourceAsNumber());
    }

        /**
         * Test deserialization of {@link ConfigData}.
         *
         * @throws Exception
         */
        @Test
        public void testDeserializeConfigData() throws Exception {    
        ConfigData configData = Serialization.deserialize(SERIALIZED_CONFIG_DATA, ConfigData.class);
		ConfigDataEntry entry = getEntryFromConfigData(
				new NetworkAddressIPv4(REMOTE_DC_PREFIX_1, REMOTE_DC_PREFIX_LENGHT), DA_ROUTER_OF_DPID1, configData);
        
        Assert.assertNotNull(entry);
        Assert.assertEquals(2, entry.getTunnels().size());
        Assert.assertNotNull(getTunnelInfoFromList(TUNNEL_NAME_11, 1, entry.getTunnels()));
        Assert.assertNotNull(getTunnelInfoFromList(TUNNEL_NAME_12, 2, entry.getTunnels()));
		
		entry = getEntryFromConfigData(
				new NetworkAddressIPv4(REMOTE_DC_PREFIX_2, REMOTE_DC_PREFIX_LENGHT), DA_ROUTER_OF_DPID1, configData);
		Assert.assertEquals(2, entry.getTunnels().size());
		Assert.assertNotNull(entry);
        Assert.assertNotNull(getTunnelInfoFromList(TUNNEL_NAME_11, 3, entry.getTunnels()));
        Assert.assertNotNull(getTunnelInfoFromList(TUNNEL_NAME_12, 4, entry.getTunnels()));
    
		entry = getEntryFromConfigData(
				new NetworkAddressIPv4(REMOTE_DC_PREFIX_3, REMOTE_DC_PREFIX_LENGHT), DA_ROUTER_OF_DPID1, configData);
		Assert.assertEquals(2, entry.getTunnels().size());
		Assert.assertNotNull(entry);
        Assert.assertNotNull(getTunnelInfoFromList(TUNNEL_NAME_21, 1, entry.getTunnels()));
        Assert.assertNotNull(getTunnelInfoFromList(TUNNEL_NAME_22, 2, entry.getTunnels()));
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
	
    /**
     * Test if bi-directional reference between {@link Link} and
     * {@link BGRouter} are correctly (de-)serialized.
     *
     * @throws Exception
     */
    @Test
    public void testLinkBGRouterBiDirReferenceSerialization() throws Exception {
        Link link1 = new Link();
        Link link2 = new Link();
        BGRouter router = new BGRouter();

        link1.setBgRouter(router);
        link2.setBgRouter(router);

        List<Link> interDomainLinks = new ArrayList<>();
        interDomainLinks.add(link1);
        interDomainLinks.add(link2);
        router.setInterDomainLinks(interDomainLinks);

        String serializedRouter = Serialization.serialize(router);

        BGRouter deserializedRouter = Serialization.deserialize(serializedRouter, BGRouter.class);

        Assert.assertEquals(2, deserializedRouter.getInterDomainLinks().size());
        for (Link link : deserializedRouter.getInterDomainLinks()) {
            Assert.assertSame(deserializedRouter, link.getBgRouter());
        }
    }

    /**
     * Test if bi-directional reference between {@link Tunnel} and {@link Link}
     * are correctly (de-)serialized.
     *
     * @throws Exception
     */
    @Test
    public void testTunnelLinkBiDirReferenceSerialization() throws Exception {
        Tunnel tunnel1 = new Tunnel();
        Tunnel tunnel2 = new Tunnel();
        Link link = new Link();

        tunnel1.setLink(link);
        tunnel2.setLink(link);

        List<Tunnel> traversingTunnels = new ArrayList<>();
        traversingTunnels.add(tunnel1);
        traversingTunnels.add(tunnel2);
        link.setTraversingTunnels(traversingTunnels);

        String serializedLink = Serialization.serialize(link);

        Link deserializedLink = Serialization.deserialize(serializedLink, Link.class);

        Assert.assertEquals(2, deserializedLink.getTraversingTunnels().size());
        for (Tunnel tunnel : deserializedLink.getTraversingTunnels()) {
            Assert.assertSame(deserializedLink, tunnel.getLink());
        }
    }

    /**
     * Test if bi-directional reference between {@link CloudDC} and {@link AS}
     * are correctly (de-)serialized.
     *
     * @throws Exception
     */
    @Test
    public void testCloudDCASBiDirReferenceSerialization() throws Exception {
        CloudDC cloudDC1 = new CloudDC();
        CloudDC cloudDC2 = new CloudDC();
        AS as = new AS();

        cloudDC1.setAs(as);
        cloudDC2.setAs(as);

        List<CloudDC> localClouds = new ArrayList<>();
        localClouds.add(cloudDC1);
        localClouds.add(cloudDC2);
        as.setLocalClouds(localClouds);

        String seializedAS = Serialization.serialize(as);

        AS deserializedAS = Serialization.deserialize(seializedAS, AS.class);

        Assert.assertEquals(2, deserializedAS.getLocalClouds().size());
        for (CloudDC cloudDC : deserializedAS.getLocalClouds()) {
            Assert.assertSame(deserializedAS, cloudDC.getAs());
        }
    }
}
