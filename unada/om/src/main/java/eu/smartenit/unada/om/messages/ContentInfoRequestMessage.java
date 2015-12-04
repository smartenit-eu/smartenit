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

import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.om.OverlayManager;

public class ContentInfoRequestMessage extends BaseMessage {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5804367086545010047L;
	
	private Logger log = LoggerFactory.getLogger(ContentInfoRequestMessage.class);
	

	@Override
	public void execute(OverlayManager om) {
		log.debug("Executing ContentInfoRequestMessage from {}", this.getSender().getUnadaID());
		ContentInfoReplyMessage msg = new ContentInfoReplyMessage();
		msg.setContent(DAOFactory.getContentDAO().findAllNotPrefetched());
		om.sendMessage(this.getSender(), msg);
		
	}

}
