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
package eu.smartenit.unada.om.messages;

import java.util.BitSet;
import java.util.Set;

import net.tomp2p.rpc.SimpleBloomFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.db.dto.UnadaInfo;
import eu.smartenit.unada.om.OverlayManager;

public class ProviderRequestMessage extends BaseMessage{
	
	private static final int BLOOMFILTER_SIZE = 1024;
	private static final long serialVersionUID = -4774301746910891484L;
	
	private static final Logger log = LoggerFactory.getLogger(ProviderRequestMessage.class);
	private long contentID;
	private boolean hasContent = true;
	private byte[] filter;
	private int expectedElements;
	
	public ProviderRequestMessage(long contentID, Set<UnadaInfo> set, boolean hasContent) {
		super();
		this.contentID = contentID;
		expectedElements = set.size();
		this.hasContent = hasContent;
		SimpleBloomFilter<String> bloom = new SimpleBloomFilter<>(BLOOMFILTER_SIZE, expectedElements);
		for(UnadaInfo info : set){
			bloom.add(info.getUnadaID());
		}
		filter = bloom.getBitSet().toByteArray();
	}
	
	@Override
	public void execute(OverlayManager om) {
		log.debug("{} - Responding to provider request from {} for content {}", om.getuNaDaInfo().getUnadaID(), getSender().getUnadaAddress(), contentID);
		ProviderReplyMessage response = new ProviderReplyMessage();
		response.setContentID(contentID);
		SimpleBloomFilter<String> bloomFilter = new SimpleBloomFilter<String>(BLOOMFILTER_SIZE, expectedElements, BitSet.valueOf(filter));
		bloomFilter.add(this.getSender().getUnadaID());
		Set<UnadaInfo> providers = om.getCloseProviders(contentID, bloomFilter);
		providers.remove(this.getSender());
		response.getProviders().addAll(providers);
		
		log.debug("{} - Returning {} providers to {}", om.getuNaDaInfo().getUnadaID(), response.getProviders().size(), getSender().getUnadaAddress());
		om.sendMessage(getSender(), response);
		if(hasContent){
			om.addProviderFromMessage(contentID, this.getSender());
		}
	}

}

