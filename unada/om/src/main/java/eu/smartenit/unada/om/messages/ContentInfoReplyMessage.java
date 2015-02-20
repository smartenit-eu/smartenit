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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.db.dto.Content;
import eu.smartenit.unada.om.OverlayManager;

public class ContentInfoReplyMessage extends BaseMessage {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5381203416139998637L;
	private	List<Content> content = null;
	private Logger log = LoggerFactory.getLogger(ContentInfoRequestMessage.class);
		
	@Override
	public void execute(OverlayManager om) {
		log.debug("Executing ContentInfoReplyMessage from {} with {} contents", this.getSender().getUnadaID(), content.size());
		om.addContentInfo(this.getSender(), content);
	}

	public List<Content> getContent() {
		return content;
	}

	public void setContent(List<Content> content) {
		this.content = content;
	}

}
