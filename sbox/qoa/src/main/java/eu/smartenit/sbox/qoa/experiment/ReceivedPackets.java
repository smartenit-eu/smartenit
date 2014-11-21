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
package eu.smartenit.sbox.qoa.experiment;

/**
 * Used to store information about the number of three types of packets.
 * 
 * @author Lukasz Lopatowski
 * @version 1.2
 */
public class ReceivedPackets {

	private long unicast;
	private long multicast;
	private long broadcast;
	
	public ReceivedPackets(long unicast, long multicast, long broadcast) {
		this.unicast = unicast;
		this.multicast = multicast;
		this.broadcast = broadcast;
	}

	public long aggregate() {
		return unicast + multicast + broadcast;
	}

	public ReceivedPackets calculateDifference(ReceivedPackets toBeSubtracted) {
		ReceivedPackets result = new ReceivedPackets(
				unicast - toBeSubtracted.getUnicast(), 
				multicast - toBeSubtracted.getMulticast(), 
				broadcast - toBeSubtracted.getBroadcast());
		return result;
	}
	
	public long getUnicast() {
		return unicast;
	}

	public long getMulticast() {
		return multicast;
	}

	public long getBroadcast() {
		return broadcast;
	}
	
}
