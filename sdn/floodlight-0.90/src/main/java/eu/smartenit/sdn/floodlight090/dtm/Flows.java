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
package eu.smartenit.sdn.floodlight090.dtm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFType;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages flows. Currently, only adding flows is supported.
 *
 * @author Grzegorz Rzym
 * @author Piotr Wydrych
 * @version 1.0
 */
public class Flows {

    private static final Logger logger = LoggerFactory.getLogger(Flows.class);

    private Flows() {
        logger.debug("Flows() begin/end");
    }

    /**
     * Adds flow for given switch, outgoing port, and incoming packet.
     * Constructs {@link OFMatch} from the incoming packet and instructs switch
     * to send all packets from the flow to be sent to given outgoing port. The
     * received packet is sent do switch for forwarding.
     *
     * @param sw swithc
     * @param floodlightProvider Floodlight controller
     * @param cntx Floodlight context
     * @param outPort outgoing port
     * @param pi incoming packet
     */
    public static void add(IOFSwitch sw, IFloodlightProviderService floodlightProvider, FloodlightContext cntx, short outPort, OFPacketIn pi) {
        logger.debug("add(IOFSwitch,IFloodlightProviderService,FloodlightContext,short OFPacketIn) begin");

        OFFlowMod flowMod = (OFFlowMod) floodlightProvider.getOFMessageFactory().getMessage(OFType.FLOW_MOD);

        // Parse the received packet
        OFMatch match = new OFMatch();
        match.loadFromPacket(pi.getPacketData(), pi.getInPort());

        match.setWildcards(0);
        flowMod.setMatch(match);

        flowMod.setCommand(OFFlowMod.OFPFC_ADD);
        flowMod.setIdleTimeout((short) 5);
        flowMod.setHardTimeout((short) 0);
        flowMod.setPriority((short) 50);
        flowMod.setBufferId(OFPacketOut.BUFFER_ID_NONE);
        flowMod.setFlags((short) 1);

        List<OFAction> actions = new ArrayList<>();
        actions.add(new OFActionOutput().setPort(outPort));
        flowMod.setActions(actions);
        flowMod.setLengthU(OFFlowMod.MINIMUM_LENGTH + OFActionOutput.MINIMUM_LENGTH);

        OFPacketOut po = (OFPacketOut) floodlightProvider.getOFMessageFactory().getMessage(OFType.PACKET_OUT);

        po.setActions(actions);
        po.setActionsLength((short) OFActionOutput.MINIMUM_LENGTH);

        short poLength = (short) (po.getActionsLength() + OFPacketOut.MINIMUM_LENGTH);

        po.setBufferId(pi.getBufferId());
        po.setInPort(pi.getInPort());
        if (pi.getBufferId() == OFPacketOut.BUFFER_ID_NONE) {
            byte[] packetData = pi.getPacketData();
            poLength += packetData.length;
            po.setPacketData(packetData);
        }
        po.setLength(poLength);

        List<OFMessage> msglist = new ArrayList<>();
        msglist.add(flowMod);
        msglist.add(po);
        try {
            sw.write(msglist, cntx);
            sw.flush();
            logger.debug(String.format("Flow rule (out port %d) added to switch %s", outPort, sw.getStringId()));
        } catch (IOException ex) {
            logger.error(String.format("Error while adding flow rule (out port %d) to switch %s", outPort, sw.getStringId()), ex);
        }
        logger.debug("add(IOFSwitch,IFloodlightProviderService,FloodlightContext,short OFPacketIn) end");
    }

}
