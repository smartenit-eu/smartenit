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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.db.dto.Content;
import eu.smartenit.unada.om.OverlayManager;
import eu.smartenit.unada.om.exceptions.DownloadException;

public class DownloadReplyMessage extends BaseMessage{

	private static final long serialVersionUID = -926456023097023730L;
	private static Logger log = LoggerFactory.getLogger(DownloadReplyMessage.class);
	private Content content = null;
	
	@Override
	public void execute(OverlayManager om)  {
		log.debug("{} - Executing Content Reply Message.", om.getuNaDaInfo().getUnadaID());
		try {
			om.getDownloadHandlers().get(getContent().getContentID()).receiveContentInfo(getContent());
		} catch (DownloadException e) {
			log.error("Failed to download a file because no providers are available.", e);
			om.getDownloadHandlers().get(getContent().getContentID()).cancelDownload();
		}
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

}
