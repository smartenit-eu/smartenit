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

import java.io.IOException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.om.OverlayManager;

public class TracerouteRequestMessage extends BaseMessage {
	
	private static final long serialVersionUID = 201253402386901791L;
	private static Logger log = LoggerFactory.getLogger(TracerouteRequestMessage.class);
	
	@Override
	public void execute(OverlayManager om) {
		try {
			om.getTpm().processRemoteTracerouteRequest(this.getSender(), om.getuNaDaInfo());
		} catch (UnknownHostException e) {
			log.error("Could not process traceroute.", e);
		} catch (IOException e) {
			log.error("I/O Exception while processing tracreoute request", e);
		} catch (InterruptedException e) {
			log.error("Thread interrupted while processing traceroute request.", e);
		}
	}

}
