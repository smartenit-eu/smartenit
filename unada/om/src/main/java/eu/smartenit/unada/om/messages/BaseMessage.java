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

import java.io.Serializable;

import eu.smartenit.unada.db.dto.UnadaInfo;
import eu.smartenit.unada.om.OverlayManager;

public abstract class BaseMessage implements Serializable {

	
	private static final long serialVersionUID = 1L;
	private UnadaInfo sender = null;

	public abstract void execute(OverlayManager om);

	public UnadaInfo getSender() {
		return sender;
	}

	public void setSender(UnadaInfo sender) {
		this.sender = sender;
	}
	
}
