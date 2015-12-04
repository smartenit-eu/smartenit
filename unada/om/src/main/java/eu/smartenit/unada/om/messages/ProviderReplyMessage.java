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
package eu.smartenit.unada.om.messages;

import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.db.dto.UnadaInfo;
import eu.smartenit.unada.om.OverlayManager;

public class ProviderReplyMessage extends BaseMessage {
	

	private static final long serialVersionUID = 4947707576465056826L;
	private static final Logger log = LoggerFactory.getLogger(ProviderReplyMessage.class);
	private long contentID = 0L;
	private Set<UnadaInfo> providers = new LinkedHashSet<UnadaInfo>();

	@Override
	public void execute(OverlayManager om) {
		for(UnadaInfo info : this.getProviders()){
			info.setHopCount(Integer.MAX_VALUE);
		}
		
		om.addProviders(contentID, providers);
	}

	public long getContentID() {
		return contentID;
	}

	public void setContentID(long contentID) {
		this.contentID = contentID;
	}

	public Set<UnadaInfo> getProviders() {
		return providers;
	}

	public void setProviders(LinkedHashSet<UnadaInfo> providers) {
		this.providers = providers;
	}
	
	

}
