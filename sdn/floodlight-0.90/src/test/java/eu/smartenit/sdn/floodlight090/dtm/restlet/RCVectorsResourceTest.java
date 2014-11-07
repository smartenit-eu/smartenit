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
package eu.smartenit.sdn.floodlight090.dtm.restlet;

import eu.smartenit.sbox.db.dto.ConfigData;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.Direction;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.SimpleTunnelID;
import eu.smartenit.sbox.db.dto.Tunnel;
import eu.smartenit.sdn.floodlight090.dtm.DTM;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

/**
 *
 * @author Piotr Wydrych
 * @version 1.0
 */
public class RCVectorsResourceTest {

    public static final String[] TUNNEL_NAMES = {
        "tunnel1",
        "tunnel2"
    };
    public static final int[] TUNNEL_NUMBERS = {
        1,
        2
    };
    public static final String[] LINK_IDS = {
        "link1",
        "link2"
    };
    public static final String[] ISP_IDS = {
        "isp1"
    };
    public static final int SOURCE_AS_NUMBER = 1;

    private static final String SERIALIZED_R_C_VECTORS_1
            = "{\n"
            + "  \"referenceVector\": {\n"
            + "    \"vectorValues\": [\n"
            + "      {\n"
            + "        \"value\": 1000000000,\n"
            + "        \"linkID\": {\n"
            + "          \"class\": \"eu.smartenit.sbox.db.dto.SimpleLinkID\",\n"
            + "          \"localLinkID\": \"" + LINK_IDS[0] + "\",\n"
            + "          \"localIspName\": \"" + ISP_IDS[0] + "\"\n"
            + "        }\n"
            + "      },\n"
            + "      {\n"
            + "        \"value\": 2000000000,\n"
            + "        \"linkID\": {\n"
            + "          \"class\": \"eu.smartenit.sbox.db.dto.SimpleLinkID\",\n"
            + "          \"localLinkID\": \"" + LINK_IDS[1] + "\",\n"
            + "          \"localIspName\": \"" + ISP_IDS[0] + "\"\n"
            + "        }\n"
            + "      }\n"
            + "    ],\n"
            + "    \"sourceAsNumber\": " + SOURCE_AS_NUMBER + ",\n"
            + "    \"thetaCollection\": null\n"
            + "  },\n"
            + "  \"compensationVector\": {\n"
            + "    \"vectorValues\": [\n"
            + "      {\n"
            + "        \"value\": 1000000,\n"
            + "        \"linkID\": {\n"
            + "          \"class\": \"eu.smartenit.sbox.db.dto.SimpleLinkID\",\n"
            + "          \"localLinkID\": \"" + LINK_IDS[0] + "\",\n"
            + "          \"localIspName\": \"" + ISP_IDS[0] + "\"\n"
            + "        }\n"
            + "      },\n"
            + "      {\n"
            + "        \"value\": -1000000,\n"
            + "        \"linkID\": {\n"
            + "          \"class\": \"eu.smartenit.sbox.db.dto.SimpleLinkID\",\n"
            + "          \"localLinkID\": \"" + LINK_IDS[1] + "\",\n"
            + "          \"localIspName\": \"" + ISP_IDS[0] + "\"\n"
            + "        }\n"
            + "      }\n"
            + "    ],\n"
            + "    \"sourceAsNumber\": " + SOURCE_AS_NUMBER + "\n"
            + "  }\n"
            + "} ";
    private static final String SERIALIZED_R_C_VECTORS_2
            = "{\n"
            + "  \"referenceVector\": {\n"
            + "    \"vectorValues\": [\n"
            + "      {\n"
            + "        \"value\": 1000000000,\n"
            + "        \"linkID\": {\n"
            + "          \"class\": \"eu.smartenit.sbox.db.dto.SimpleLinkID\",\n"
            + "          \"localLinkID\": \"" + LINK_IDS[0] + "\",\n"
            + "          \"localIspName\": \"" + ISP_IDS[0] + "\"\n"
            + "        }\n"
            + "      },\n"
            + "      {\n"
            + "        \"value\": 2000000000,\n"
            + "        \"linkID\": {\n"
            + "          \"class\": \"eu.smartenit.sbox.db.dto.SimpleLinkID\",\n"
            + "          \"localLinkID\": \"" + LINK_IDS[1] + "\",\n"
            + "          \"localIspName\": \"" + ISP_IDS[0] + "\"\n"
            + "        }\n"
            + "      }\n"
            + "    ],\n"
            + "    \"sourceAsNumber\": " + SOURCE_AS_NUMBER + ",\n"
            + "    \"thetaCollection\": null\n"
            + "  },\n"
            + "  \"compensationVector\": {\n"
            + "    \"vectorValues\": [\n"
            + "      {\n"
            + "        \"value\": 2000000,\n"
            + "        \"linkID\": {\n"
            + "          \"class\": \"eu.smartenit.sbox.db.dto.SimpleLinkID\",\n"
            + "          \"localLinkID\": \"" + LINK_IDS[0] + "\",\n"
            + "          \"localIspName\": \"" + ISP_IDS[0] + "\"\n"
            + "        }\n"
            + "      },\n"
            + "      {\n"
            + "        \"value\": -2000000,\n"
            + "        \"linkID\": {\n"
            + "          \"class\": \"eu.smartenit.sbox.db.dto.SimpleLinkID\",\n"
            + "          \"localLinkID\": \"" + LINK_IDS[1] + "\",\n"
            + "          \"localIspName\": \"" + ISP_IDS[0] + "\"\n"
            + "        }\n"
            + "      }\n"
            + "    ],\n"
            + "    \"sourceAsNumber\": " + SOURCE_AS_NUMBER + "\n"
            + "  }\n"
            + "} ";
    private static final String SERIALIZED_C_VECTOR_2
            = "{\n"
            + "  \"referenceVector\": null,\n"
            + "  \"compensationVector\": {\n"
            + "    \"vectorValues\": [\n"
            + "      {\n"
            + "        \"value\": 2000000,\n"
            + "        \"linkID\": {\n"
            + "          \"class\": \"eu.smartenit.sbox.db.dto.SimpleLinkID\",\n"
            + "          \"localLinkID\": \"" + LINK_IDS[0] + "\",\n"
            + "          \"localIspName\": \"" + ISP_IDS[0] + "\"\n"
            + "        }\n"
            + "      },\n"
            + "      {\n"
            + "        \"value\": -2000000,\n"
            + "        \"linkID\": {\n"
            + "          \"class\": \"eu.smartenit.sbox.db.dto.SimpleLinkID\",\n"
            + "          \"localLinkID\": \"" + LINK_IDS[1] + "\",\n"
            + "          \"localIspName\": \"" + ISP_IDS[0] + "\"\n"
            + "        }\n"
            + "      }\n"
            + "    ],\n"
            + "    \"sourceAsNumber\": " + SOURCE_AS_NUMBER + "\n"
            + "  }\n"
            + "} ";

    private static DTM dtm;
    private static RCVectorsResource resource;

    /**
     *
     */
    @BeforeClass
    public static void beforeClass() {
        dtm = DTM.getInstance();
        resource = new RCVectorsResource();
    }
    
    @Before
    public void before() {
        ConfigData configData = new ConfigData();
        DC2DCCommunication outCommunication = new DC2DCCommunication();
        outCommunication.setTrafficDirection(Direction.outcomingTraffic);
        Tunnel tunnel1 = new Tunnel();
        tunnel1.setTunnelID(new SimpleTunnelID(TUNNEL_NAMES[0], TUNNEL_NUMBERS[0]));
        Link link1 = new Link();
        link1.setLinkID(new SimpleLinkID(LINK_IDS[0], ISP_IDS[0]));
        link1.setTraversingTunnels(Arrays.asList(new Tunnel[]{tunnel1}));
        tunnel1.setLink(link1);
        Tunnel tunnel2 = new Tunnel();
        tunnel2.setTunnelID(new SimpleTunnelID(TUNNEL_NAMES[1], TUNNEL_NUMBERS[1]));
        Link link2 = new Link();
        link2.setLinkID(new SimpleLinkID(LINK_IDS[1], ISP_IDS[0]));
        link2.setTraversingTunnels(Arrays.asList(new Tunnel[]{tunnel2}));
        tunnel2.setLink(link2);
        outCommunication.setConnectingTunnels(Arrays.asList(new Tunnel[]{tunnel1, tunnel2}));
        configData.setOutCommunicationList(Arrays.asList(new DC2DCCommunication[]{outCommunication}));
        dtm.setConfigData(configData);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testHandlePost() throws Exception {
        String response = resource.handlePost(SERIALIZED_R_C_VECTORS_1);
        JSONAssert.assertEquals(SERIALIZED_R_C_VECTORS_1, response, true);
        Assert.assertEquals(1000000000, dtm.getReferenceVector().getVectorValueForLink(new SimpleLinkID(LINK_IDS[0], ISP_IDS[0])));
        Assert.assertEquals(2000000000, dtm.getReferenceVector().getVectorValueForLink(new SimpleLinkID(LINK_IDS[1], ISP_IDS[0])));
        Assert.assertEquals(SOURCE_AS_NUMBER, dtm.getReferenceVector().getSourceAsNumber());
        Assert.assertNull(dtm.getReferenceVector().getThetaCollection());
        Assert.assertEquals(1000000, dtm.getCompensationVector().getVectorValueForLink(new SimpleLinkID(LINK_IDS[0], ISP_IDS[0])));
        Assert.assertEquals(-1000000, dtm.getCompensationVector().getVectorValueForLink(new SimpleLinkID(LINK_IDS[1], ISP_IDS[0])));
        Assert.assertEquals(SOURCE_AS_NUMBER, dtm.getCompensationVector().getSourceAsNumber());

        response = resource.handlePost(SERIALIZED_C_VECTOR_2);
        JSONAssert.assertEquals(SERIALIZED_R_C_VECTORS_2, response, true);
        Assert.assertEquals(1000000000, dtm.getReferenceVector().getVectorValueForLink(new SimpleLinkID(LINK_IDS[0], ISP_IDS[0])));
        Assert.assertEquals(2000000000, dtm.getReferenceVector().getVectorValueForLink(new SimpleLinkID(LINK_IDS[1], ISP_IDS[0])));
        Assert.assertEquals(SOURCE_AS_NUMBER, dtm.getReferenceVector().getSourceAsNumber());
        Assert.assertNull(dtm.getReferenceVector().getThetaCollection());
        Assert.assertEquals(2000000, dtm.getCompensationVector().getVectorValueForLink(new SimpleLinkID(LINK_IDS[0], ISP_IDS[0])));
        Assert.assertEquals(-2000000, dtm.getCompensationVector().getVectorValueForLink(new SimpleLinkID(LINK_IDS[1], ISP_IDS[0])));
        Assert.assertEquals(SOURCE_AS_NUMBER, dtm.getCompensationVector().getSourceAsNumber());
    }

}
