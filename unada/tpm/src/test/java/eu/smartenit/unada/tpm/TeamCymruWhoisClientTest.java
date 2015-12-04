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

import static eu.smartenit.unada.tpm.TeamCymruWhoisClient.isSpecialPurposeAddress;
import java.net.Inet4Address;
import static java.net.InetAddress.getByName;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link TeamCymruWhoisClient}
 *
 * @author Piotr Wydrych
 * @version 2.0
 */
public class TeamCymruWhoisClientTest {

    TeamCymruWhoisClient client = new TeamCymruWhoisClient("whois.cymru.com", false);

    @Test
    public void testIsSpecialPurposeAddress() throws UnknownHostException {
        Assert.assertTrue(isSpecialPurposeAddress((Inet4Address) getByName("10.5.6.7")));
        Assert.assertTrue(isSpecialPurposeAddress((Inet4Address) getByName("172.31.1.1")));
        Assert.assertTrue(isSpecialPurposeAddress((Inet4Address) getByName("192.168.100.5")));
        Assert.assertTrue(isSpecialPurposeAddress((Inet4Address) getByName("240.15.45.5")));
        Assert.assertFalse(isSpecialPurposeAddress((Inet4Address) getByName("8.8.8.8")));
        Assert.assertFalse(isSpecialPurposeAddress((Inet4Address) getByName("149.156.96.9")));
        Assert.assertFalse(isSpecialPurposeAddress((Inet4Address) getByName("193.0.14.129")));
        Assert.assertFalse(isSpecialPurposeAddress((Inet4Address) getByName("199.7.83.42")));

    }

    @Test
    public void testIpToASN() throws Exception {
        Map<Inet4Address, Integer> testMap = new HashMap<>();
        testMap.put((Inet4Address) getByName("10.5.6.7"), 0);
        testMap.put((Inet4Address) getByName("172.31.1.1"), 0);
        testMap.put((Inet4Address) getByName("192.168.100.5"), 0);
        testMap.put((Inet4Address) getByName("240.15.45.5"), 0);
        testMap.put((Inet4Address) getByName("8.8.8.8"), 15169);
        testMap.put((Inet4Address) getByName("149.156.96.9"), 8267);
        testMap.put((Inet4Address) getByName("193.0.14.129"), 25152);
        testMap.put((Inet4Address) getByName("199.7.83.42"), 20144);
        Map<Inet4Address, Integer> outputMap = client.ipToASN(testMap.keySet());
        Assert.assertEquals(testMap.size(), outputMap.size());
        for (Map.Entry<Inet4Address, Integer> entry : testMap.entrySet()) {
            Assert.assertEquals(entry.getValue(), outputMap.get(entry.getKey()));
        }
    }

}
