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
package eu.smartenit.sbox.qoa.experiment;

/**
 * Data container class used used to store information about the number of three types of packets.
 * 
 * @author Lukasz Lopatowski
 * @version 1.2
 */
public class ReceivedPackets {

	private long unicast;
	private long multicast;
	private long broadcast;
	
	
	/**
	 * The constructor with arguments.
	 * 
	 * @param unicast
	 * 		unicast packets
	 * @param multicast
	 * 		multicast packets
	 * @param broadcast
	 * 		broadcast packets
	 */
	public ReceivedPackets(long unicast, long multicast, long broadcast) {
		this.unicast = unicast;
		this.multicast = multicast;
		this.broadcast = broadcast;
	}

	
	/**
	 * Method used to calculate packets sum.
	 * 
	 * @return
	 * 		packets sum
	 */
	public long aggregate() {
		return unicast + multicast + broadcast;
	}

	/**
	 *  Method used to calculate difference between stored and provided values.
	 * 
	 * @param toBeSubtracted
	 * 		{@link ReceivedPackets} object
	 * @return
	 * 		difference between stored and provided values
	 */
	public ReceivedPackets calculateDifference(ReceivedPackets toBeSubtracted) {
		ReceivedPackets result = new ReceivedPackets(
				unicast - toBeSubtracted.getUnicast(), 
				multicast - toBeSubtracted.getMulticast(), 
				broadcast - toBeSubtracted.getBroadcast());
		return result;
	}
	
	/**
	 *  Returns amount of unicast packets. 
	 * 
	 * @return
	 * 		amount of unicast packets
	 */
	public long getUnicast() {
		return unicast;
	}

	/**
	 *  Returns amount of multicast packets. 
	 * 
	 * @return
	 * 		amount of multicast packets
	 */
	public long getMulticast() {
		return multicast;
	}

	/**
	 *  Returns amount of broadcast packets. 
	 * 
	 * @return
	 * 		amount of broadcast packets
	 */
	public long getBroadcast() {
		return broadcast;
	}
	
}
