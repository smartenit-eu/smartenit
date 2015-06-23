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
package eu.smartenit.unada.tpm;

import eu.smartenit.unada.commons.constants.UnadaConstants;
import eu.smartenit.unada.commons.logging.UnadaLogger;
import eu.smartenit.unada.db.dto.UnadaInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Topology Proximity Monitor for uNaDa.
 *
 * @author Piotr Wydrych
 * @version 2.0
 */
public class TopologyProximityMonitorImpl implements TopologyProximityMonitor {

    private static final Logger logger = LoggerFactory.getLogger(TopologyProximityMonitorImpl.class);

    private static final String OS = System.getProperty("os.name");
    private static final boolean IS_WINDOWS = OS.toUpperCase().startsWith("WINDOWS");
    private static final boolean IS_LINUX = OS.toUpperCase().startsWith("LINUX");

    private static final Pattern INET_4_PATTERN = Pattern.compile("(?:(?:(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d))\\.){3}(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d)"); // http://regexr.com/39it2
    private static final Pattern WIN_HOP_LINE = Pattern.compile("\\s*(\\d+)\\s+(\\*|<?\\d+ ms)\\s+(\\*|<?\\d+ ms)\\s+(\\*|<?\\d+ ms)\\s+(.*\\S)\\s*");
    private static final Pattern LIN_HOP_LINE = Pattern.compile("\\s*(\\d+)\\s+(?:(\\*)|(\\S+)\\s+\\d+\\.\\d+ ms)");

    private static final String WINDOWS_TRACEROUTE_COMMAND;
    private static final String LINUX_TRACEROUTE_COMMAND;
    private static final boolean COMPACT_NULL_HOPS;
    private static final String TEAM_CYMRU_WHOIS_HOST;
    private static final boolean RESOLVE_SPECIAL_PURPOSE_ADDRESSES;
    public static final int SORT_CLOSEST_TIMEOUT;

    static {
        try {
            logger.debug("Loading TPM properties");
            Configuration conf = new PropertiesConfiguration("tpm.properties");
            WINDOWS_TRACEROUTE_COMMAND = conf.getString("tpm.windowsTracerouteCommand");
            LINUX_TRACEROUTE_COMMAND = conf.getString("tpm.linuxTracerouteCommand");
            COMPACT_NULL_HOPS = conf.getBoolean("tpm.compactNullHops");
            TEAM_CYMRU_WHOIS_HOST = conf.getString("tpm.teamCymruWhoisHost");
            RESOLVE_SPECIAL_PURPOSE_ADDRESSES = conf.getBoolean("tpm.resolveSpecialPurposeAddresses");
            SORT_CLOSEST_TIMEOUT = conf.getInt("tpm.sortClosestTimeout");
        } catch (ConfigurationException ex) {
            throw new RuntimeException("Exception caught while loading TPM configuration file", ex);
        }
    }

    private final TPMMessageSender messageSender;

    private ConcurrentHashMap<UnadaInfo, List<Integer>> sortClosestMap;
    private final List<Integer> sortClosestMapNull = new ArrayList<>(); // null values are not allowed
    private CountDownLatch sortClosestLatch = null;
    private final Object sortClosestMajorSynchronizer = new Object();
    private final Object sortClosestMinorSynchronizer = new Object();

    public TopologyProximityMonitorImpl(TPMMessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public void processRemoteTracerouteRequest(UnadaInfo src, UnadaInfo dst) throws UnknownHostException, IOException, InterruptedException {
        logger.debug("Processing remote traceroute request from {}", src.getInetAddress().getHostAddress());
        messageSender.sendRemoteTracerouteReply(src, getASVector(src));
    }

    @Override
    public void processRemoteTracerouteReply(UnadaInfo src, UnadaInfo dst, List<Integer> vector) {
        logger.debug("Processing remote traceroute reply from {}", src.getInetAddress().getHostAddress());
        synchronized (sortClosestMinorSynchronizer) {
            if (sortClosestMap == null || !sortClosestMap.containsKey(src)) {
                logger.info("Discarding unexpected remote traceroute reply");
            } else {
                if (sortClosestMap.put(src, vector) == sortClosestMapNull) {
                    sortClosestLatch.countDown();
                } else {
                    logger.info("Discarding duplicated remote traceroute reply");
                }
            }
        }
    }

    @Override
    public List<ASVector> sortClosest(Collection<UnadaInfo> addresses) throws InterruptedException {
        if (sortClosestLatch != null) {
            logger.warn("Another sortClosest(Collection<InetAddress>) in progress - waiting");
        }
        synchronized (sortClosestMajorSynchronizer) {
            try {
                addresses = new HashSet<>(addresses); // uniqueness
                synchronized (sortClosestMinorSynchronizer) {
                    sortClosestLatch = new CountDownLatch(addresses.size());
                    sortClosestMap = new ConcurrentHashMap<>();
                    for (UnadaInfo info : addresses) {
                        sortClosestMap.put(info, sortClosestMapNull); // null values are not allowed
                    }
                }
                logger.debug("Sending remote traceroute requests to {} peers", addresses.size());
                for (UnadaInfo info : addresses) {
                    messageSender.sendRemoteTracerouteRequest(info);
                }
                if (!sortClosestLatch.await(SORT_CLOSEST_TIMEOUT, TimeUnit.MILLISECONDS)) {
                    logger.warn("Not all peers replied within the timeout");
                }
                synchronized (sortClosestMinorSynchronizer) {
                    List<ASVector> result = new ArrayList<>();
                    for (Map.Entry<UnadaInfo, List<Integer>> entry : sortClosestMap.entrySet()) {
                        if (entry.getValue() == sortClosestMapNull) {
                            logger.warn("Couldn't get remote traceroute for {}, omitting", entry.getKey());
                        } else {
                            result.add(new ASVector(entry.getKey(), entry.getValue()));
                        }
                    }
                    Collections.sort(result);
                    return result;
                }
            } finally {
                synchronized (sortClosestMinorSynchronizer) {
                    sortClosestMap = null;
                    sortClosestLatch = null;
                }
            }
        }
    }

    @Override
    public List<Integer> getASVector(UnadaInfo info) throws UnknownHostException, IOException, InterruptedException {
        logger.debug("Getting AS vector for {}", info.getInetAddress().getHostAddress());
        List<Inet4Address> hops = parseTraceroute(executeTraceroute((Inet4Address) info.getInetAddress()));

        Map<Inet4Address, Integer> ipToASN = new TeamCymruWhoisClient(TEAM_CYMRU_WHOIS_HOST, RESOLVE_SPECIAL_PURPOSE_ADDRESSES).ipToASN(hops); // new TeamCymruWhoisClient instance for each request

        List<Integer> asvector = new ArrayList<>();
        Integer lastAS = null;
        int nulls = 0;
        for (Inet4Address hop : hops) {
            Integer as = ipToASN.get(hop);
            if (as != null && as.equals(lastAS)) {
                // within the same AS
                nulls = 0;
            } else if (as != null) {
                // unkown intermediate ASes
                for (int i = 0; i < nulls; i++) {
                    asvector.add(null);
                }

                // another AS
                asvector.add(as);
                lastAS = as;
            } else {
                nulls = COMPACT_NULL_HOPS ? 1 : nulls + 1; // same or another AS?
            }
        }
        // unkown ASes
        for (int i = 0; i < nulls; i++) {
            asvector.add(null);
        }
        
        String ases = asvector == null ? "[]" : asvector.toString();
        UnadaLogger.overall.debug("{}: Traceroute ({}, {})", 
        		new Object[]{UnadaConstants.UNADA_OWNER_MD5, 
        		info.getInetAddress().getHostAddress(), ases});
        return asvector;
    }

    /**
     * OS-independent traceroute executor to specified address.
     *
     * @param address destination host.
     * @return Traceroute output.
     * @throws IOException If an I/O error occurs
     * @throws InterruptedException if the current thread is
     * {@linkplain Thread#interrupt() interrupted} by another thread while it is
     * waiting, then the wait is ended and an {@link InterruptedException} is
     * thrown.
     */
    public List<String> executeTraceroute(Inet4Address address) throws IOException, InterruptedException {
        logger.debug("Executing traceroute");
        String command;
        if (IS_WINDOWS) {
            command = String.format(WINDOWS_TRACEROUTE_COMMAND, address.getHostAddress());
        } else if (IS_LINUX) {
            command = String.format(LINUX_TRACEROUTE_COMMAND, address.getHostAddress());
        } else {
            throw new UnsupportedOperationException("OS is neither Windows nor Linux (os.name = \"" + OS + "\")");
        }
        Process proc = Runtime.getRuntime().exec(command, new String[]{"LC_ALL=C"});
        BufferedReader stdout = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        BufferedReader stderr = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

        String line;
        String error = "";
        while ((line = stderr.readLine()) != null) {
            error = error + line + "\n";
        }
        if (!error.isEmpty()) {
            throw new RuntimeException("Error while executing \"" + command + "\": " + error);
        }

        proc.waitFor();

        List<String> result = new ArrayList<>();
        while ((line = stdout.readLine()) != null) {
            result.add(line);
        }
        logger.debug("Traceroute finished");
        return result;
    }

    /**
     * OS-independed traceroute parser.
     *
     * @param output traceroute output.
     * @return list of IP addresses. May contain nulls if hops were not
     * discovered.
     * @throws UnknownHostException should not be thrown.
     */
    public List<Inet4Address> parseTraceroute(List<String> output) throws UnknownHostException {
        if (IS_WINDOWS) {
            return parseWindowsTraceroute(output);
        } else if (IS_LINUX) {
            return parseLinuxTraceroute(output);
        } else {
            throw new UnsupportedOperationException("OS is neither Windows nor Linux (os.name = \"" + OS + "\")");
        }
    }

    /**
     * Windows traceroute parser.
     *
     * @param output traceroute output.
     * @return list of IP addresses. May contain nulls if hops were not
     * discovered.
     * @throws UnknownHostException should not be thrown.
     */
    public List<Inet4Address> parseWindowsTraceroute(List<String> output) throws UnknownHostException {
        //
        //Tracing route to 8.8.8.8 over a maximum of 30 hops
        //
        //  1     1 ms    <1 ms    <1 ms  149.156.203.249 
        //  2     2 ms     1 ms     1 ms  149.156.119.17 
        //  3    <1 ms    <1 ms    <1 ms  149.156.6.222 
        //  4    <1 ms    <1 ms    <1 ms  149.156.4.245 
        //  5     9 ms     9 ms     9 ms  212.191.224.69 
        //  6     9 ms     9 ms     9 ms  62.40.125.245 
        //  7    30 ms    30 ms    30 ms  62.40.98.130 
        //  8    29 ms    29 ms    29 ms  62.40.125.202 
        //  9    30 ms    30 ms    31 ms  209.85.241.110 
        // 10    30 ms    34 ms    30 ms  72.14.234.237 
        // 11    33 ms    33 ms    33 ms  209.85.241.226 
        // 12    36 ms    36 ms    36 ms  216.239.48.133 
        // 13    42 ms    36 ms    36 ms  209.85.255.49 
        // 14     *        *        *     Request timed out.
        // 15    36 ms    36 ms    36 ms  8.8.8.8 
        //
        //Trace complete.
        //
        logger.debug("Parsing Windows traceroute output");
        if (logger.isTraceEnabled()) {
            logger.trace("Traceroute output:\n" + StringUtils.join(output, "\n"));
        }
        int i = 0;
        if (!output.get(i).isEmpty()) {
            throwParseError(output, i, "expected empty line");
        }
        if (output.get(++i).isEmpty()) {
            throwParseError(output, i, "expected non-empty line");
        }
        for (; !output.get(i).isEmpty(); ++i) {
        }
        if (!output.get(i).isEmpty()) {
            throwParseError(output, i, "expected empty line");
        }
        if (output.get(++i).isEmpty()) {
            throwParseError(output, i, "expected non-empty line");
        }
        List<Inet4Address> result = new ArrayList<>();
        for (int j = 1; !output.get(i).isEmpty(); ++i, ++j) {
            Matcher m = WIN_HOP_LINE.matcher(output.get(i));
            if (!m.matches() || !m.group(1).equals(Integer.toString(j))) {
                throwParseError(output, i, null);
            }
            if ((m.group(2) + m.group(3) + m.group(4)).equals("***")) {
                result.add(null);
            } else if (INET_4_PATTERN.matcher(m.group(5)).matches()) {
                result.add((Inet4Address) InetAddress.getByName(m.group(5)));
            } else {
                throwParseError(output, i, null);
            }
        }
        if (output.get(++i).isEmpty()) {
            throwParseError(output, i, "expected non-empty line");
        }
        return result;
    }

    /**
     * Linux traceroute parser.
     *
     * @param output traceroute output.
     * @return list of IP addresses. May contain nulls if hops were not
     * discovered.
     * @throws UnknownHostException should not be thrown.
     */
    public List<Inet4Address> parseLinuxTraceroute(List<String> output) throws UnknownHostException {
        //traceroute to 8.8.8.8 (8.8.8.8), 30 hops max, 60 byte packets
        // 1  10.22.0.1  1.279 ms
        // 2  149.156.96.10  3.146 ms
        // 3  149.156.6.222  2.710 ms
        // 4  149.156.0.218  2.977 ms
        // 5  212.191.224.69  11.179 ms
        // 6  62.40.125.245  11.429 ms
        // 7  62.40.98.130  32.515 ms
        // 8  62.40.125.202  34.245 ms
        // 9  209.85.241.110  33.893 ms
        //10  209.85.251.246  34.139 ms
        //11  209.85.240.142  37.292 ms
        //12  216.239.48.143  65.936 ms
        //13  216.239.49.36  40.477 ms
        //14  *
        //15  8.8.8.8  41.475 ms
        logger.debug("Parsing Linux traceroute output");
        if (logger.isTraceEnabled()) {
            logger.trace("Traceroute output:\n" + StringUtils.join(output, "\n"));
        }
        int i = 0;
        if (output.get(i).isEmpty()) {
            throwParseError(output, i, "expected non-empty line");
        }
        List<Inet4Address> result = new ArrayList<>();
        for (++i; i < output.size(); ++i) {
            Matcher m = LIN_HOP_LINE.matcher(output.get(i));
            if (!m.matches() || !m.group(1).equals(Integer.toString(i))) {
                throwParseError(output, i, null);
            }
            if ("*".equals(m.group(2))) {
                result.add(null);
            } else if (INET_4_PATTERN.matcher(m.group(3)).matches()) {
                result.add((Inet4Address) InetAddress.getByName(m.group(3)));
            } else {
                throwParseError(output, i, null);
            }
        }
        return result;
    }

    private void throwParseError(List<String> output, int index, String reason) {
        throw new IllegalArgumentException("Parse error in line " + (index + 1) + ": \"" + output.get(index) + "\"" + reason == null ? "" : (", " + reason));
    }
}
