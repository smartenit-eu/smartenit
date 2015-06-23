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

import eu.smartenit.sbox.db.dto.OperationModeSDN;
import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;

import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adds a new flow when a {@link OFPacketIn packet in message} is received from
 * a switch.
 *
 * @author Grzegorz Rzym
 * @author Piotr Wydrych
 * @version 3.0
 */
public class DTMMessageListener implements IOFMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(DTMMessageListener.class);

    private final IFloodlightProviderService floodlightProvider;

    /**
     * Constructor of message listener.
     *
     * @param floodlightProvider Floodlight controller
     */
    public DTMMessageListener(IFloodlightProviderService floodlightProvider) {
        logger.debug("DTMMessageListener(IFloodlightProviderService) begin");
        this.floodlightProvider = floodlightProvider;
        logger.debug("DTMMessageListener(IFloodlightProviderService) end");
    }

    /**
     * Process new packet and add a flow for it.
     *
     * @param sw switch
     * @param msg incoming packet
     * @param cntx Floodlight context
     * @return {@link Command#STOP} - the packet should not be processed by next
     * listeners
     * @see DTM#getOfPortNumber()
     * @see Flows#add(net.floodlightcontroller.core.IOFSwitch,
     * net.floodlightcontroller.core.IFloodlightProviderService,
     * net.floodlightcontroller.core.FloodlightContext, short,
     * org.openflow.protocol.OFPacketIn)
     */
    @Override
    public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
        logger.debug("receive(IOFSwitch,OFMessage,FloodlightContext) begin");
        
        if (DTM.getInstance().getCompensationVector()==null){
            logger.debug("Compensation vector is not set yet");
            return Command.STOP;
        }
        
        DTM.getInstance().setOFPacketIn((OFPacketIn) msg);
        
        if (DTM.getInstance().getConfigData() == null) {
        	logger.warn("DTM is not yet configured with proper ConfigData. Will not process this message from switch ...");
        	return Command.STOP;
        }
        
        if (DTM.getInstance().getConfigData().getOperationModeSDN() == OperationModeSDN.reactiveWithReferenceVector) {
            if (DTM.getInstance().getReactiveWithReferenceOutOfPortNumber() != 0) {
                Flows.add(sw, floodlightProvider, cntx, DTM.getInstance().getReactiveWithReferenceOutOfPortNumber(), (OFPacketIn) msg);
            } else {
                logger.debug("receive(IOFSwitch,OFMessage,FloodlightContext) DTM not configured");
            }
        } else if (DTM.getInstance().getConfigData().getOperationModeSDN() == OperationModeSDN.reactiveWithoutReferenceVector) {
            Flows.add(sw, floodlightProvider, cntx, DTM.getInstance().getReactiveWithoutReferenceOutOfPortNumber(), (OFPacketIn) msg);
        } else if (DTM.getInstance().getConfigData().getOperationModeSDN() == OperationModeSDN.proactiveWithReferenceVector) {
            DTM.getInstance().sendStaticFlowRule(DTM.getInstance().getProactiveOutOfPortNumber());
            logger.debug("ERR - should not be here!");
        } else if (DTM.getInstance().getConfigData().getOperationModeSDN() == OperationModeSDN.proactiveWithoutReferenceVector) {
            DTM.getInstance().sendStaticFlowRule(DTM.getInstance().getProactiveOutOfPortNumber());
            logger.debug("ERR - should not be here");
        } else if (DTM.getInstance().getConfigData().getOperationModeSDN() == null) {
            logger.warn("receive(IOFSwitch,OFMessage,FloodlightContext) DTM not configured");
        }
        
        logger.debug("receive(IOFSwitch,OFMessage,FloodlightContext) end");
        return Command.STOP; // do not process the packet by next listeners        
    }

    /**
     * Return the listener name.
     *
     * @return name (simple name of the class)
     */
    @Override
    public String getName() {
        logger.debug("getName() one-liner");
        return getClass().getSimpleName();
    }

    /**
     * Return false - this listener does not have prerequisites.
     *
     * @param type the message type to which this applies
     * @param name the name of the module
     * @return false
     */
    @Override
    public boolean isCallbackOrderingPrereq(OFType type, String name) {
        logger.debug("isCallbackOrderingPrereq(OFType,String) one-liner");
        return false;
    }

    /**
     * Return false - this listener does not have post-requisites.
     *
     * @param type the message type to which this applies
     * @param name the name of the module
     * @return false
     */
    @Override
    public boolean isCallbackOrderingPostreq(OFType type, String name) {
        logger.debug("isCallbackOrderingPostreq(OFType,String) one-liner");
        return false;
    }
}
