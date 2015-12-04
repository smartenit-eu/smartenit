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
package eu.smartenit.unada.tpm;

import eu.smartenit.unada.db.dto.UnadaInfo;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

/**
 * Test class for {@link TopologyProximityMonitorImpl}
 *
 * @author Piotr Wydrych
 * @version 2.0
 */
public class TopologyProximityMonitorImplTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TopologyProximityMonitorImplTest.class);

    private static final boolean IS_WINDOWS = System.getProperty("os.name").toUpperCase().startsWith("WINDOWS");
    private static final boolean IS_LINUX = System.getProperty("os.name").toUpperCase().startsWith("LINUX");

    private static final Inet4Address TARGET;

    static {
        try {
            TARGET = (Inet4Address) InetAddress.getByName("8.8.8.8");
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static final List<String> WINDOWS_TRACEROUTE_OUTPUT = Arrays.asList(new String[]{
        "",
        "Tracing route to 8.8.8.8 over a maximum of 30 hops",
        "",
        "  1     1 ms    <1 ms    <1 ms  149.156.203.249 ",
        "  2     2 ms     1 ms     1 ms  149.156.119.17 ",
        "  3    <1 ms    <1 ms    <1 ms  149.156.6.222 ",
        "  4    <1 ms    <1 ms    <1 ms  149.156.4.245 ",
        "  5     9 ms     9 ms     9 ms  212.191.224.69 ",
        "  6     9 ms     9 ms     9 ms  62.40.125.245 ",
        "  7    30 ms    30 ms    30 ms  62.40.98.130 ",
        "  8    29 ms    29 ms    29 ms  62.40.125.202 ",
        "  9    30 ms    30 ms    31 ms  209.85.241.110 ",
        " 10    30 ms    34 ms    30 ms  72.14.234.237 ",
        " 11    33 ms    33 ms    33 ms  209.85.241.226 ",
        " 12    36 ms    36 ms    36 ms  216.239.48.133 ",
        " 13    42 ms    36 ms    36 ms  209.85.255.49 ",
        " 14     *        *        *     Request timed out.",
        " 15    36 ms    36 ms    36 ms  8.8.8.8 ",
        "",
        "Trace complete."
    });
    private static final List<String> LINUX_TRACEROUTE_OUTPUT = Arrays.asList(new String[]{
        "traceroute to 8.8.8.8 (8.8.8.8), 30 hops max, 60 byte packets",
        " 1  149.156.203.249  4.025 ms",
        " 2  149.156.119.17  4.116 ms",
        " 3  149.156.6.222  3.675 ms",
        " 4  149.156.4.245  3.796 ms",
        " 5  212.191.224.69  9.667 ms",
        " 6  62.40.125.245  35.203 ms",
        " 7  62.40.98.130  30.821 ms",
        " 8  62.40.125.202  30.265 ms",
        " 9  209.85.241.110  31.014 ms",
        "10  72.14.234.237  31.412 ms",
        "11  209.85.241.226  38.833 ms",
        "12  216.239.48.133  37.234 ms",
        "13  209.85.255.49  41.889 ms",
        "14  *",
        "15  8.8.8.8  37.874 ms"
    });
    private static final List<Inet4Address> IP_HOPS;

    static {
        try {
            IP_HOPS = Arrays.asList(new Inet4Address[]{
                (Inet4Address) InetAddress.getByName("149.156.203.249"),
                (Inet4Address) InetAddress.getByName("149.156.119.17"),
                (Inet4Address) InetAddress.getByName("149.156.6.222"),
                (Inet4Address) InetAddress.getByName("149.156.4.245"),
                (Inet4Address) InetAddress.getByName("212.191.224.69"),
                (Inet4Address) InetAddress.getByName("62.40.125.245"),
                (Inet4Address) InetAddress.getByName("62.40.98.130"),
                (Inet4Address) InetAddress.getByName("62.40.125.202"),
                (Inet4Address) InetAddress.getByName("209.85.241.110"),
                (Inet4Address) InetAddress.getByName("72.14.234.237"),
                (Inet4Address) InetAddress.getByName("209.85.241.226"),
                (Inet4Address) InetAddress.getByName("216.239.48.133"),
                (Inet4Address) InetAddress.getByName("209.85.255.49"),
                null,
                (Inet4Address) InetAddress.getByName("8.8.8.8")
            });
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static final List<Integer> AS_VECTOR = Arrays.asList(new Integer[]{
        8267,
        8501,
        20965,
        15169
    });

    private final TPMMessageSender overlayManagerMock = Mockito.mock(TPMMessageSender.class);
    private final TopologyProximityMonitorImpl tpm = new TopologyProximityMonitorImpl(overlayManagerMock);

    @Test
    public void testSortClosest() throws UnknownHostException, IOException, InterruptedException, ExecutionException {
        final ASVector[] vectors = new ASVector[]{
            new ASVector(new UnadaInfo().setUnadaAddress("10.1.1.1"), Arrays.asList(new Integer[]{65000, 65001})),
            new ASVector(new UnadaInfo().setUnadaAddress("10.2.1.1"), Arrays.asList(new Integer[]{65000})),
            new ASVector(new UnadaInfo().setUnadaAddress("10.3.1.1"), Arrays.asList(new Integer[]{65000, 65002, 65003, 65004})),
            new ASVector(new UnadaInfo().setUnadaAddress("10.4.1.1"), Arrays.asList(new Integer[]{65000, 65002, 65003, 65004, 65005})),
            new ASVector(new UnadaInfo().setUnadaAddress("10.5.1.1"), Arrays.asList(new Integer[]{65000, 65002, 65003}))
        };

        final TopologyProximityMonitorImpl spyTpm = Mockito.spy(tpm);
        for (ASVector vector : vectors) {
            Mockito.doReturn(vector.getVector()).when(spyTpm).getASVector(vector.getRemoteAddress());
        }
        Future<List<ASVector>> requestor = Executors.newSingleThreadExecutor().submit(new Callable<List<ASVector>>() {
            @Override
            public List<ASVector> call() throws Exception {
                return spyTpm.sortClosest(Arrays.asList(
                        new UnadaInfo[]{
                            vectors[0].getRemoteAddress(),
                            vectors[0].getRemoteAddress(), // avoid duplicate requests
                            vectors[1].getRemoteAddress(),
                            vectors[2].getRemoteAddress(),
                            vectors[3].getRemoteAddress() // will not respond within timeout
                        }));
            }
        });

        // should occure within SORT_CLOSEST_TIMEOUT
        spyTpm.processRemoteTracerouteReply(vectors[0].getRemoteAddress(), null, vectors[0].getVector());
        spyTpm.processRemoteTracerouteReply(vectors[1].getRemoteAddress(), null, vectors[1].getVector());
        spyTpm.processRemoteTracerouteReply(vectors[2].getRemoteAddress(), null, vectors[2].getVector());
        spyTpm.processRemoteTracerouteReply(vectors[2].getRemoteAddress(), null, vectors[2].getVector()); // discard duplicate replies
        spyTpm.processRemoteTracerouteReply(vectors[4].getRemoteAddress(), null, vectors[4].getVector()); // discard replies from non-queried peers

        logger.info("Test case: not all peers respond within the timeout ({} ms), waiting", TopologyProximityMonitorImpl.SORT_CLOSEST_TIMEOUT);
        List<ASVector> sorted = requestor.get();

        Assert.assertEquals(Arrays.asList(new ASVector[]{vectors[1], vectors[0], vectors[2]}), sorted);

        spyTpm.processRemoteTracerouteReply(vectors[3].getRemoteAddress(), null, vectors[3].getVector()); // discard replies after timeout
    }

    @Test
    public void testGetASVector() throws UnknownHostException, IOException, InterruptedException {
        // do not execute traceroute
        List<String> executeTracerouteOutput = null;
        if (IS_WINDOWS) {
            executeTracerouteOutput = WINDOWS_TRACEROUTE_OUTPUT;
        }
        if (IS_LINUX) {
            executeTracerouteOutput = LINUX_TRACEROUTE_OUTPUT;
        }
        TopologyProximityMonitorImpl spyTpm = Mockito.spy(tpm);
        Mockito.doReturn(executeTracerouteOutput).when(spyTpm).executeTraceroute(TARGET);

        Assert.assertEquals(AS_VECTOR, spyTpm.getASVector(new UnadaInfo().setUnadaAddress(TARGET)));
    }

    @Test
    @Ignore // no traceroute on maven.man.poznan.pl
    public void testExecuteTraceroute() throws IOException, InterruptedException {
        List<String> output = tpm.executeTraceroute(TARGET);
        Assert.assertNotEquals(0, output.size());
        if (IS_WINDOWS) {
            Assert.assertEquals("", output.get(0));
            Assert.assertTrue(output.get(1).matches(".* 8\\.8\\.8\\.8 .*"));
        }
        if (IS_LINUX) {
            Assert.assertTrue(output.get(0).matches(".* 8\\.8\\.8\\.8 .*"));
        }
    }

    @Test
    public void testParseTraceroute() throws UnknownHostException {
        if (IS_WINDOWS) {
            testParseWindowsTraceroute();
        }
        if (IS_LINUX) {
            testParseLinuxTraceroute();
        }
    }

    @Test
    public void testParseWindowsTraceroute() throws UnknownHostException {
        Assert.assertEquals(IP_HOPS, tpm.parseWindowsTraceroute(WINDOWS_TRACEROUTE_OUTPUT));
    }

    @Test
    public void testParseLinuxTraceroute() throws UnknownHostException {
        Assert.assertEquals(IP_HOPS, tpm.parseLinuxTraceroute(LINUX_TRACEROUTE_OUTPUT));
    }

}
