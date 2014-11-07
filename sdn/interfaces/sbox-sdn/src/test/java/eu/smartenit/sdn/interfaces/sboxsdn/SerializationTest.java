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

import eu.smartenit.sbox.db.dto.AS;
import eu.smartenit.sbox.db.dto.BGRouter;
import eu.smartenit.sbox.db.dto.CVector;
import eu.smartenit.sbox.db.dto.CloudDC;
import eu.smartenit.sbox.db.dto.ConfigData;
import eu.smartenit.sbox.db.dto.DC2DCCommunication;
import eu.smartenit.sbox.db.dto.Direction;
import eu.smartenit.sbox.db.dto.Link;
import eu.smartenit.sbox.db.dto.RVector;
import eu.smartenit.sbox.db.dto.SimpleLinkID;
import eu.smartenit.sbox.db.dto.SimpleTunnelID;
import eu.smartenit.sbox.db.dto.Tunnel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

/**
 * Test serializaion.
 *
 * @author Piotr Wydrych
 * @version 1.0
 */
public class SerializationTest {

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

    private static final String SERIALIZED_R_C_VECTORS
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

    private static final String SERIALIZED_CONFIG_DATA
            = "{\n"
            + "  \"inCommunicationList\": null,\n"
            + "  \"outCommunicationList\": [\n"
            + "    {\n"
            + "      \"id\": {\n"
            + "        \"communicationNumber\": 0,\n"
            + "        \"communicationSymbol\": null,\n"
            + "        \"localAsNumber\": 0,\n"
            + "        \"localCloudDCName\": null,\n"
            + "        \"remoteAsNumber\": 0,\n"
            + "        \"remoteCloudDCName\": null\n"
            + "      },\n"
            + "      \"trafficDirection\": \"outcomingTraffic\",\n"
            + "      \"remoteCloud\": {\n"
            + "        \"@id\": \"CloudDC@1\",\n"
            + "        \"cloudDcName\": null,\n"
            + "        \"as\": {\n"
            + "          \"@id\": \"AS@1\",\n"
            + "          \"asNumber\": 0,\n"
            + "          \"local\": false,\n"
            + "          \"bgRouters\": null,\n"
            + "          \"sbox\": {\n"
            + "            \"managementAddress\": {\n"
            + "              \"prefix\": null,\n"
            + "              \"prefixLength\": 0\n"
            + "            }\n"
            + "          },\n"
            + "          \"localClouds\": null\n"
            + "        },\n"
            + "        \"daRouter\": {\n"
            + "          \"managementAddress\": {\n"
            + "            \"prefix\": null,\n"
            + "            \"prefixLength\": 0\n"
            + "          },\n"
            + "          \"snmpCommunity\": null\n"
            + "        },\n"
            + "        \"sdnController\": {\n"
            + "          \"managementAddress\": {\n"
            + "            \"prefix\": null,\n"
            + "            \"prefixLength\": 0\n"
            + "          },\n"
            + "          \"daRouters\": null,\n"
            + "          \"restHost\": {\n"
            + "            \"prefix\": null,\n"
            + "            \"prefixLength\": 0\n"
            + "          },\n"
            + "          \"restPort\": 0,\n"
            + "          \"openflowHost\": {\n"
            + "            \"prefix\": null,\n"
            + "            \"prefixLength\": 0\n"
            + "          },\n"
            + "          \"openflowPort\": 0\n"
            + "        },\n"
            + "        \"dcNetworks\": []\n"
            + "      },\n"
            + "      \"localCloud\": {\n"
            + "        \"@id\": \"CloudDC@2\",\n"
            + "        \"cloudDcName\": null,\n"
            + "        \"as\": {\n"
            + "          \"@id\": \"AS@2\",\n"
            + "          \"asNumber\": 0,\n"
            + "          \"local\": false,\n"
            + "          \"bgRouters\": null,\n"
            + "          \"sbox\": {\n"
            + "            \"managementAddress\": {\n"
            + "              \"prefix\": null,\n"
            + "              \"prefixLength\": 0\n"
            + "            }\n"
            + "          },\n"
            + "          \"localClouds\": null\n"
            + "        },\n"
            + "        \"daRouter\": {\n"
            + "          \"managementAddress\": {\n"
            + "            \"prefix\": null,\n"
            + "            \"prefixLength\": 0\n"
            + "          },\n"
            + "          \"snmpCommunity\": null\n"
            + "        },\n"
            + "        \"sdnController\": {\n"
            + "          \"managementAddress\": {\n"
            + "            \"prefix\": null,\n"
            + "            \"prefixLength\": 0\n"
            + "          },\n"
            + "          \"daRouters\": null,\n"
            + "          \"restHost\": {\n"
            + "            \"prefix\": null,\n"
            + "            \"prefixLength\": 0\n"
            + "          },\n"
            + "          \"restPort\": 0,\n"
            + "          \"openflowHost\": {\n"
            + "            \"prefix\": null,\n"
            + "            \"prefixLength\": 0\n"
            + "          },\n"
            + "          \"openflowPort\": 0\n"
            + "        },\n"
            + "        \"dcNetworks\": []\n"
            + "      },\n"
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
            + "            \"address\": {\n"
            + "              \"prefix\": null,\n"
            + "              \"prefixLength\": 0\n"
            + "            },\n"
            + "            \"physicalInterfaceName\": null,\n"
            + "            \"vlan\": 0,\n"
            + "            \"inboundInterfaceCounterOID\": null,\n"
            + "            \"outboundInterfaceCounterOID\": null,\n"
            + "            \"costFunction\": {\n"
            + "              \"type\": null,\n"
            + "              \"subtype\": null,\n"
            + "              \"inputUnit\": null,\n"
            + "              \"outputUnit\": null,\n"
            + "              \"segments\": []\n"
            + "            },\n"
            + "            \"bgRouter\": {\n"
            + "              \"@id\": \"BGRouter@1\",\n"
            + "              \"managementAddress\": {\n"
            + "                \"prefix\": null,\n"
            + "                \"prefixLength\": 0\n"
            + "              },\n"
            + "              \"snmpCommunity\": null,\n"
            + "              \"interDomainLinks\": null\n"
            + "            },\n"
            + "            \"traversingTunnels\": [\n"
            + "              \"Tunnel@1\"\n"
            + "            ]\n"
            + "          },\n"
            + "          \"sourceEndAddress\": {\n"
            + "            \"prefix\": null,\n"
            + "            \"prefixLength\": 0\n"
            + "          },\n"
            + "          \"destinationEndAddress\": {\n"
            + "            \"prefix\": null,\n"
            + "            \"prefixLength\": 0\n"
            + "          },\n"
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
            + "            \"address\": {\n"
            + "              \"prefix\": null,\n"
            + "              \"prefixLength\": 0\n"
            + "            },\n"
            + "            \"physicalInterfaceName\": null,\n"
            + "            \"vlan\": 0,\n"
            + "            \"inboundInterfaceCounterOID\": null,\n"
            + "            \"outboundInterfaceCounterOID\": null,\n"
            + "            \"costFunction\": {\n"
            + "              \"type\": null,\n"
            + "              \"subtype\": null,\n"
            + "              \"inputUnit\": null,\n"
            + "              \"outputUnit\": null,\n"
            + "              \"segments\": []\n"
            + "            },\n"
            + "            \"bgRouter\": {\n"
            + "              \"@id\": \"BGRouter@2\",\n"
            + "              \"managementAddress\": {\n"
            + "                \"prefix\": null,\n"
            + "                \"prefixLength\": 0\n"
            + "              },\n"
            + "              \"snmpCommunity\": null,\n"
            + "              \"interDomainLinks\": null\n"
            + "            },\n"
            + "            \"traversingTunnels\": [\n"
            + "              \"Tunnel@2\"\n"
            + "            ]\n"
            + "          },\n"
            + "          \"sourceEndAddress\": {\n"
            + "            \"prefix\": null,\n"
            + "            \"prefixLength\": 0\n"
            + "          },\n"
            + "          \"destinationEndAddress\": {\n"
            + "            \"prefix\": null,\n"
            + "            \"prefixLength\": 0\n"
            + "          },\n"
            + "          \"physicalLocalInterfaceName\": null,\n"
            + "          \"inboundInterfaceCounterOID\": null,\n"
            + "          \"outboundInterfaceCounterOID\": null\n"
            + "        }\n"
            + "      ]\n"
            + "    }\n"
            + "  ]\n"
            + "}";

    /**
     * Test serialization of {@link RCVectors} and {@link ConfigData}.
     *
     * @throws Exception
     */
    @Test
    public void testSerialize() throws Exception {
        String text;

        RCVectors vectors = new RCVectors(new RVector(), new CVector());
        vectors.getReferenceVector().addVectorValueForLink(new SimpleLinkID(LINK_IDS[0], ISP_IDS[0]), 1000000000);
        vectors.getReferenceVector().addVectorValueForLink(new SimpleLinkID(LINK_IDS[1], ISP_IDS[0]), 2000000000);
        vectors.getReferenceVector().setSourceAsNumber(SOURCE_AS_NUMBER);
        vectors.getReferenceVector().setThetaCollection(null);
        vectors.getCompensationVector().addVectorValueForLink(new SimpleLinkID(LINK_IDS[0], ISP_IDS[0]), 1000000);
        vectors.getCompensationVector().addVectorValueForLink(new SimpleLinkID(LINK_IDS[1], ISP_IDS[0]), -1000000);
        vectors.getCompensationVector().setSourceAsNumber(SOURCE_AS_NUMBER);
        text = Serialization.serialize(vectors);
        JSONAssert.assertEquals(SERIALIZED_R_C_VECTORS, text, true);

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
        text = Serialization.serialize(configData);
        JSONAssert.assertEquals(SERIALIZED_CONFIG_DATA, text, true);
    }

    /**
     * Test deserialization of {@link RCVectors} and {@link ConfigData}.
     *
     * @throws Exception
     */
    @Test
    public void testDeserialize() throws Exception {
        RCVectors vectors = Serialization.deserialize(SERIALIZED_R_C_VECTORS, RCVectors.class);
        Assert.assertEquals(1000000000, vectors.getReferenceVector().getVectorValueForLink(new SimpleLinkID(LINK_IDS[0], ISP_IDS[0])));
        Assert.assertEquals(2000000000, vectors.getReferenceVector().getVectorValueForLink(new SimpleLinkID(LINK_IDS[1], ISP_IDS[0])));
        Assert.assertEquals(SOURCE_AS_NUMBER, vectors.getReferenceVector().getSourceAsNumber());
        Assert.assertNull(vectors.getReferenceVector().getThetaCollection());
        Assert.assertEquals(1000000, vectors.getCompensationVector().getVectorValueForLink(new SimpleLinkID(LINK_IDS[0], ISP_IDS[0])));
        Assert.assertEquals(-1000000, vectors.getCompensationVector().getVectorValueForLink(new SimpleLinkID(LINK_IDS[1], ISP_IDS[0])));
        Assert.assertEquals(SOURCE_AS_NUMBER, vectors.getCompensationVector().getSourceAsNumber());

        ConfigData configData = Serialization.deserialize(SERIALIZED_CONFIG_DATA, ConfigData.class);
        Assert.assertNull(configData.getInCommunicationList());
        Assert.assertEquals(1, configData.getOutCommunicationList().size());
        Assert.assertEquals(Direction.outcomingTraffic, configData.getOutCommunicationList().get(0).getTrafficDirection());
        Assert.assertEquals(2, configData.getOutCommunicationList().get(0).getConnectingTunnels().size());
        Assert.assertEquals(new SimpleTunnelID(TUNNEL_NAMES[0], TUNNEL_NUMBERS[0]), configData.getOutCommunicationList().get(0).getConnectingTunnels().get(0).getTunnelID());
        Assert.assertEquals(new SimpleTunnelID(TUNNEL_NAMES[1], TUNNEL_NUMBERS[1]), configData.getOutCommunicationList().get(0).getConnectingTunnels().get(1).getTunnelID());
        Assert.assertEquals(new SimpleLinkID(LINK_IDS[0], ISP_IDS[0]), configData.getOutCommunicationList().get(0).getConnectingTunnels().get(0).getLink().getLinkID());
        Assert.assertEquals(new SimpleLinkID(LINK_IDS[1], ISP_IDS[0]), configData.getOutCommunicationList().get(0).getConnectingTunnels().get(1).getLink().getLinkID());
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
