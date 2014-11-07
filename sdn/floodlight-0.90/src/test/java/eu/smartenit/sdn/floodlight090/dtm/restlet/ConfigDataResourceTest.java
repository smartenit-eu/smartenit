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

import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.SimpleTunnelID;
import eu.smartenit.sdn.floodlight090.dtm.DTM;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

/**
 *
 * @author Piotr Wydrych
 * @version 1.0
 */
public class ConfigDataResourceTest {

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

    private static final String SERIALIZED_CONFIG_DATA
            = "{\n"
            + "  \"inCommunicationList\": null,\n"
            + "  \"outCommunicationList\": [\n"
            + "    {\n"
            + "      \"id\": null,\n"
            + "      \"trafficDirection\": \"outcomingTraffic\",\n"
            + "      \"remoteCloud\": null,\n"
            + "      \"localCloud\": null,\n"
            + "      \"qosParameters\": null,\n"
            + "      \"connectingTunnels\": [\n"
            + "        {\n"
            + "          \"@id\": \"Tunnel@1\",\n"
            + "          \"tunnelID\": {\n"
            + "            \"class\": \"eu.smartenit.sbox.db.dto.SimpleTunnelID\",\n"
            + "            \"tunnelName\": \"" + TUNNEL_NAMES[0] + "\",\n"
            + "            \"tunnelNumber\": " + TUNNEL_NUMBERS[0] + "\n"
            + "          },\n"
            + "          \"link\": {\n"
            + "            \"@id\": \"Link@1\",\n"
            + "            \"linkID\": {\n"
            + "              \"class\": \"eu.smartenit.sbox.db.dto.SimpleLinkID\",\n"
            + "              \"localLinkID\": \"" + LINK_IDS[0] + "\",\n"
            + "              \"localIspName\": \"" + ISP_IDS[0] + "\"\n"
            + "            },\n"
            + "            \"address\": null,\n"
            + "            \"physicalInterfaceName\": null,\n"
            + "            \"vlan\": 0,\n"
            + "            \"inboundInterfaceCounterOID\": null,\n"
            + "            \"outboundInterfaceCounterOID\": null,\n"
            + "            \"costFunction\": null,\n"
            + "            \"bgRouter\": null,\n"
            + "            \"traversingTunnels\": [\n"
            + "              \"Tunnel@1\"\n"
            + "            ]\n"
            + "          },\n"
            + "          \"sourceEndAddress\": null,\n"
            + "          \"destinationEndAddress\": null,\n"
            + "          \"physicalLocalInterfaceName\": null,\n"
            + "          \"inboundInterfaceCounterOID\": null,\n"
            + "          \"outboundInterfaceCounterOID\": null\n"
            + "        },\n"
            + "        {\n"
            + "          \"@id\": \"Tunnel@2\",\n"
            + "          \"tunnelID\": {\n"
            + "            \"class\": \"eu.smartenit.sbox.db.dto.SimpleTunnelID\",\n"
            + "            \"tunnelName\": \"" + TUNNEL_NAMES[1] + "\",\n"
            + "            \"tunnelNumber\": " + TUNNEL_NUMBERS[1] + "\n"
            + "          },\n"
            + "          \"link\": {\n"
            + "            \"@id\": \"Link@2\",\n"
            + "            \"linkID\": {\n"
            + "              \"class\": \"eu.smartenit.sbox.db.dto.SimpleLinkID\",\n"
            + "              \"localLinkID\": \"" + LINK_IDS[1] + "\",\n"
            + "              \"localIspName\": \"" + ISP_IDS[0] + "\"\n"
            + "            },\n"
            + "            \"address\": null,\n"
            + "            \"physicalInterfaceName\": null,\n"
            + "            \"vlan\": 0,\n"
            + "            \"inboundInterfaceCounterOID\": null,\n"
            + "            \"outboundInterfaceCounterOID\": null,\n"
            + "            \"costFunction\": null,\n"
            + "            \"bgRouter\": null,\n"
            + "            \"traversingTunnels\": [\n"
            + "              \"Tunnel@2\"\n"
            + "            ]\n"
            + "          },\n"
            + "          \"sourceEndAddress\": null,\n"
            + "          \"destinationEndAddress\": null,\n"
            + "          \"physicalLocalInterfaceName\": null,\n"
            + "          \"inboundInterfaceCounterOID\": null,\n"
            + "          \"outboundInterfaceCounterOID\": null\n"
            + "        }\n"
            + "      ]\n"
            + "    }\n"
            + "  ]\n"
            + "}";

    private static DTM dtm;
    private static ConfigDataResource resource;

    /**
     *
     */
    @BeforeClass
    public static void beforeClass() {
        dtm = DTM.getInstance();
        resource = new ConfigDataResource();
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testHandlePost() throws Exception {
        String response = resource.handlePost(SERIALIZED_CONFIG_DATA);
        JSONAssert.assertEquals(SERIALIZED_CONFIG_DATA, response, true);
        Assert.assertNull(dtm.getConfigData().getInCommunicationList());
        Assert.assertEquals(1, dtm.getConfigData().getOutCommunicationList().size());
        Assert.assertEquals(2, dtm.getConfigData().getOutCommunicationList().get(0).getConnectingTunnels().size());
        Assert.assertEquals(new SimpleTunnelID(TUNNEL_NAMES[0], TUNNEL_NUMBERS[0]), dtm.getConfigData().getOutCommunicationList().get(0).getConnectingTunnels().get(0).getTunnelID());
        Assert.assertEquals(new SimpleTunnelID(TUNNEL_NAMES[1], TUNNEL_NUMBERS[1]), dtm.getConfigData().getOutCommunicationList().get(0).getConnectingTunnels().get(1).getTunnelID());
        Assert.assertEquals(new SimpleLinkID(LINK_IDS[0], ISP_IDS[0]), dtm.getConfigData().getOutCommunicationList().get(0).getConnectingTunnels().get(0).getLink().getLinkID());
        Assert.assertEquals(new SimpleLinkID(LINK_IDS[1], ISP_IDS[0]), dtm.getConfigData().getOutCommunicationList().get(0).getConnectingTunnels().get(1).getLink().getLinkID());
    }

}
