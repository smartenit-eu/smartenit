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

import eu.smartenit.unada.om.OverlayManager;

public class TracerouteReplyMessage extends BaseMessage {
	
	private static final long serialVersionUID = 59969285782411847L;
	private List<Integer> vector;

	@Override
	public void execute(OverlayManager om) {
		om.getTpm().processRemoteTracerouteReply(super.getSender(), om.getuNaDaInfo(), vector);
	}

	public List<Integer> getVector() {
		return vector;
	}

	public void setVector(List<Integer> vector) {
		this.vector = vector;
	}

}
