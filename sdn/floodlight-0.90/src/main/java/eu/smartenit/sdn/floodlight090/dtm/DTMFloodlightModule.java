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

import eu.smartenit.sdn.floodlight090.dtm.restlet.DTMRestletRoutable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.restserver.IRestApiService;
import org.openflow.protocol.OFType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Floodlight module responsible for adding
 * {@link DTMMessageListener message listener}, {@link DTMSwitchListener switch listener},
 * and {@link DTMRestletRoutable RESTlet routable}.
 *
 * @author Grzegorz Rzym
 * @author Piotr Wydrych
 * @version 1.2
 */
public class DTMFloodlightModule implements IFloodlightModule {

    private static final Logger logger = LoggerFactory.getLogger(DTMFloodlightModule.class);

    private IFloodlightProviderService floodlightProvider;
    private IRestApiService restApi;

    /**
     * Do nothing and return null. This module does not implement any services.
     *
     * @return null
     */
    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        logger.debug("getModuleServices() one-liner");
        return null;
    }

    /**
     * Do nothing and return null. This module does not implement any services.
     *
     * @return null
     */
    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
        logger.debug("getServiceImpls() one-liner");
        return null;
    }

    /**
     * Get a list of Modules that this module depends on, i.e.,
     * {@link IFloodlightProviderService} and {@link IRestApiService}. The
     * module system will ensure that each these dependencies is resolved before
     * the subsequent calls to init().
     *
     * @return The Collection of IFloodlightServices that this module depends
     * on.
     */
    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        logger.debug("getModuleDependencies() begin");
        Collection<Class<? extends IFloodlightService>> dependencies = new ArrayList<>();
        dependencies.add(IFloodlightProviderService.class);
        dependencies.add(IRestApiService.class);
        logger.debug("getModuleDependencies() end");
        return dependencies;
    }

    /**
     * Initialize DTM module.
     *
     * @param context Floodlight module context
     * @throws FloodlightModuleException never
     */
    @Override
    public void init(FloodlightModuleContext context) throws FloodlightModuleException {
        logger.debug("init(FloodlightModuleContext) begin");
        logger.debug("Initializing DTM floodlight module");
        floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
        restApi = context.getServiceImpl(IRestApiService.class);
        logger.debug("init(FloodlightModuleContext) end");
    }

    /**
     * Start DTM module.
     *
     * @param context Floodlight module context
     */
    @Override
    public void startUp(FloodlightModuleContext context) {
        logger.debug("startUp(FloodlightModuleContext) begin");
        logger.debug("Starting DTM floodlight module");
        floodlightProvider.addOFMessageListener(OFType.PACKET_IN, new DTMMessageListener(floodlightProvider));
        floodlightProvider.addOFSwitchListener(new DTMSwitchListener());
        restApi.addRestletRoutable(new DTMRestletRoutable());
        DTM.getInstance().setFloodlightProvider(floodlightProvider);
        logger.debug("startUp(FloodlightModuleContext) end");
    }

}
