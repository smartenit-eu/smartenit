/**
 * Copyright (C) 2015 The SmartenIT consortium (http://www.smartenit.eu)
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

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for new {@link IOFSwitch switches} and configures
 * {@link DTM main DTM class}.
 *
 * @author Grzegorz Rzym
 * @author Piotr Wydrych
 * @version 1.0
 */
public class DTMSwitchListener implements IOFSwitchListener {

    private static final Logger logger = LoggerFactory.getLogger(DTMSwitchListener.class);

    /**
     * Fired when a switch is connected to the controller, and has sent a
     * features reply. Propagates the information to {@link DTM}.
     *
     * @param sw switch
     * @see DTM#setSwitch(net.floodlightcontroller.core.IOFSwitch)
     */
    @Override
    public void addedSwitch(IOFSwitch sw) {
        logger.debug("addedSwitch(IOFSwitch) begin");
        DTM.getInstance().setSwitch(sw);
        logger.debug("addedSwitch(IOFSwitch) end");
    }

    /**
     * Fired when a switch is disconnected from the controller. Propages the
     * information to {@link DTM}.
     *
     * @param sw switch
     * @see DTM#setSwitch(net.floodlightcontroller.core.IOFSwitch)
     */
    @Override
    public void removedSwitch(IOFSwitch sw) {
        logger.debug("removedSwitch(IOFSwitch) begin");
        DTM.getInstance().setSwitch(null);
        logger.debug("removedSwitch(IOFSwitch) end");
    }

    /**
     * Fired when ports on a switch change (any change to the collection of
     * OFPhysicalPorts and/or to a particular port). Does nothing.
     *
     * @param switchId switch id
     */
    @Override
    public void switchPortChanged(Long switchId) {
        logger.debug("switchPortChanged(Long) begin/end");
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

}
