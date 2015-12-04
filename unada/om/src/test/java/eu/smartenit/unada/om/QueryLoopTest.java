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

import java.util.BitSet;

import net.tomp2p.rpc.SimpleBloomFilter;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import eu.smartenit.unada.db.dto.UnadaInfo;

/**
 * This is just a manual debugging test,should be ignored in building.
 * 
 * @author Andri
 *
 */
public class QueryLoopTest extends OverlayBaseTest {

	@Test@Ignore
	public void loopTest() throws Exception {

		om1.advertiseContent(contentID1);
		
		om2.advertiseContent(contentID1);
		
		Thread.sleep(2000);
//		om1.getPrediction();
		
		
		Thread.sleep(600000);
	}
	
	@Test@Ignore
	public void bloomFilterTest() throws Exception {
		
		SimpleBloomFilter<UnadaInfo> filter1 = new SimpleBloomFilter<>(1024, 10);
		filter1.add(om1.getuNaDaInfo());
		Assert.assertTrue(filter1.contains(om1.getuNaDaInfo()));
		
		byte[] serialized1 = filter1.getBitSet().toByteArray();
		
		SimpleBloomFilter<UnadaInfo> filter2 = new SimpleBloomFilter<>(1024, 10, BitSet.valueOf(serialized1));
		
		Assert.assertTrue(filter2.contains(om1.getuNaDaInfo()));
	}

}
