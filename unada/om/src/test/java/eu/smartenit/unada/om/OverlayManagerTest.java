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
package eu.smartenit.unada.om;


import java.io.IOException;
import java.net.UnknownHostException;

import net.tomp2p.dht.FutureGet;
import net.tomp2p.peers.Number160;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import eu.smartenit.unada.db.dto.UnadaInfo;
import eu.smartenit.unada.om.exceptions.OverlayException;

public class OverlayManagerTest extends OverlayBaseTest{

	
	@Test
	public void testJoinOverlay() throws IOException, ClassNotFoundException, OverlayException{
		FutureGet futureGet = network[23].get(Number160.createHash(om1ID)).start();
		futureGet.awaitUninterruptibly();
		Assert.assertTrue(futureGet.isSuccess());
		
		UnadaInfo info = (UnadaInfo) futureGet.data().object();
		Assert.assertNotNull(info.getUnadaAddress());
	}
	
	@Test
	public void testUpdateOverlay() throws ClassNotFoundException, IOException, OverlayException {
				
		om1.getOverlay().updateOverlay();
		
		FutureGet futureGet = network[23].get(Number160.createHash(om1ID)).start();
		futureGet.awaitUninterruptibly();
		Assert.assertTrue(futureGet.isSuccess());
		
		UnadaInfo info = (UnadaInfo) futureGet.data().object();
		Assert.assertNotNull(info.getUnadaAddress());
		Assert.assertEquals(om1.getuNaDaInfo().getUnadaAddress(), info.getUnadaAddress());
		Assert.assertEquals(om1.getuNaDaInfo().getTcpPort(), info.getTcpPort());
	}
		
	@Test@Ignore
	public void resolveGeoLocation() throws UnknownHostException {
		OverlayManager om = new OverlayManager("randomid");
		UnadaInfo info = new UnadaInfo();
		info.setUnadaAddress("130.60.156.188");
		info.setUnadaPorts(4001);
		om.resolveGeoLocation(info);
		Assert.assertEquals(47.3667, info.getLatitude(), 0);
		Assert.assertEquals(8.55, info.getLongitude(), 0);
	}
	

}
