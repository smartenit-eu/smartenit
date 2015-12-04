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

import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import eu.smartenit.unada.db.dto.Content;
import eu.smartenit.unada.db.dto.UnadaInfo;
import eu.smartenit.unada.om.exceptions.OverlayException;
import eu.smartenit.unada.om.messages.BaseMessage;
import eu.smartenit.unada.om.messages.ContentInfoRequestMessage;
import eu.smartenit.unada.tpm.ASVector;
import eu.smartenit.unada.tpm.TopologyProximityMonitor;

public class OverlayManagerMessagingTest extends OverlayBaseTest{
	

	
	@Test
	public void basicMesagingTest() throws IOException, OverlayException, InterruptedException{
		
		BaseMessage mockedMessage = mock(BaseMessage.class, withSettings().serializable());
		UnadaInfo info = om1.getuNaDaInfo(); //very weird, when this getUnadaInfo() is inside the thenReturn() below I get a stubbing exception
		when(mockedMessage.getSender()).thenReturn(info);
		
		Assert.assertTrue(om1.sendMessage(om2.getuNaDaInfo(), mockedMessage));
	}
	
	@Test
	public void requestProviderTest() throws IOException, OverlayException, InterruptedException {
		TopologyProximityMonitor mockedTpm = mock(TopologyProximityMonitor.class);
		when(mockedTpm.sortClosest(anyList())).thenAnswer(new Answer<List<ASVector>>() {
			@Override
			public List<ASVector> answer(InvocationOnMock invocation) throws Throwable {
				List<ASVector> asv = new LinkedList<>();
				for(UnadaInfo info : (Set<UnadaInfo>) invocation.getArguments()[0] ){
					asv.add(new ASVector(info, new LinkedList<Integer>()));
				}
			    return asv;
			}
		});
		om1.setTpm(mockedTpm);
		om2.setTpm(mockedTpm);
		om1.advertiseContent(contentID1);
		LinkedHashSet<UnadaInfo> additionalProviders = new LinkedHashSet<UnadaInfo>();
		for(int i = 0 ; i < 10 ; i++){
			additionalProviders.add(getRandomUnadaInfo());
		}
		om1.addProviders(contentID1, additionalProviders);
		Set<UnadaInfo> providers = om2.queryProviders(contentID1, null);
		Assert.assertEquals(11, providers.size());
	}
	
	@Test
	public void requestContentInfoTest() throws IOException, OverlayException, InterruptedException {
		om1.advertiseContent(contentID1);
		om1.advertiseContent(contentID2);
		ContentInfoRequestMessage requ = new ContentInfoRequestMessage();
		om2.setContentRequestCounter(new CountDownLatch(1));
		om2.sendMessage(om1.getuNaDaInfo(), requ);
		om2.getContentRequestCounter().await(5, TimeUnit.SECONDS);
		for(List<Content> contents : om2.getContentLists().values()){
			Assert.assertEquals(2, contents.size());
		}
	}
}
