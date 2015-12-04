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
package eu.smartenit.unada.commons.commands;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ARP class implementing the ARP command execution.
 * 
 * @author George Petropoulos
 * @version 2.0
 */
public class ARP {

    private ARP() {

    }

    private static ARP arpInstance = new ARP();
    private static final Logger logger = LoggerFactory.getLogger(ARP.class);
    private static String arpCommand = "/usr/sbin/arp ";

    /**
     * The method that returns the arp instance.
     * 
     * @return The arp instance.
     * 
     */
    public static ARP getArpInstance() {
        return arpInstance;
    }

    /**
     * The method that sets the arp instance.
     * 
     * @param arpInstance
     *            The arp instance.
     * 
     */
    public static void setArpInstance(ARP arpInstance) {
        ARP.arpInstance = arpInstance;
    }

    /**
     * The execution method. It executes the ARP command and reads the output
     * text. If successful, then returns the mapped mac address, otherwise it
     * returns null.
     * 
     * 
     * @param ipAddress
     *            The ip address for which ARP will be executed.
     * 
     * @return The MAC address
     * 
     */
    public String execute(String ipAddress) {
        logger.info("Executing ARP for ip address " + ipAddress);
        String macAddress = null;

        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(arpCommand + ipAddress);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));

            String line = "";
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
                if (lineNumber == 1) {
                    String[] lineSplits = line.split("\\s+");
                    return lineSplits[2];
                }
                lineNumber++;
            }

        } catch (Exception e) {
            logger.error("Error while executing arp command: " + e.getMessage());
        }

        return macAddress;
    }

}
