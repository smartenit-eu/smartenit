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
package eu.smartenit.unada.om;

import java.net.InetAddress;
import java.util.List;
import java.util.Set;

import eu.smartenit.unada.db.dto.Content;
import eu.smartenit.unada.db.dto.UnadaInfo;
import eu.smartenit.unada.om.exceptions.DownloadException;
import eu.smartenit.unada.om.exceptions.OverlayException;
import eu.smartenit.unada.om.messages.BaseMessage;
import eu.smartenit.unada.tpm.TPMMessageSender;

public interface IOverlayManager extends TPMMessageSender {

	/**
	 * Bootstraps to an existing DHT node running on the specified address and
	 * port.
	 * 
	 * @param bootstrapNode
	 * @param port
	 * @throws OverlayException
	 */
	public abstract void joinOverlay(InetAddress bootstrapNode, int port)
			throws OverlayException;
	
	
	/**
	 * Creates a new network (DHT) where other peers can join. An attempt to
	 * find a public IP address among the network interfaces will be made. If
	 * one is found it binds to the first public IP address found otherwise the
	 * first address found is used.
	 * 
	 * @throws OverlayException
	 */
	public abstract void createOverlay() throws OverlayException;
	
	/**
	 * Creates a new network (DHT) where other peers can join. It tries to bind to the specified address. 
	 * 
	 * @param address
	 * @throws OverlayException
	 */
	public abstract void createOverlay(InetAddress address) throws OverlayException;
	
	/**
	 * Creates a new network (DHT) where other peers can join. It tries to bind to the specified interface.
	 * 
	 * @param interfaceHint
	 * @throws OverlayException
	 */
	public abstract void createOverlay(String interfaceHint) throws OverlayException;

	/**
	 * Updates the DHt with the information that the specified content can be
	 * found on this peer.
	 * 
	 * @param contentID
	 */
	public abstract void advertiseContent(long... contentID);
	
	/**
	 * Method delivers a message of type {@link BaseMessage} to the specified
	 * destination. It returns true if the message was delilvered succesfully or
	 * false if not.
	 * 
	 * @param destination
	 * @param message
	 * @return
	 */
	public abstract boolean sendMessage(UnadaInfo destination, BaseMessage message);
	
	/**
	 * This method will run the overlay prediction and return a sorted list
	 * with the most relevant content in the beginning of the list.
	 * 
	 * @return
	 */
	public abstract List<Content> getPrediction();
	
	/**
	 * This method downloads the specified content into the 
	 * content path defined in the path argument.
	 * 
	 * @param contentID
	 * @param path
	 * @return 
	 * @throws DownloadException 
	 */
	public abstract IFutureDownload downloadContent(Content content) throws DownloadException;
	
	/**
	 * Returns a sorted list where close providers are in the beginning of the list.
	 * 
	 * @param contentID
	 * @return
	 */
	public Set<UnadaInfo> getCloseProviders(long contentID);
	
	
	/**
	 * Finds sources in the overlay and returns hop count.
	 * @param content the content object to be resolved
	 * @return
	 */
	public int getHopCount(Content content);
	
	/**
	 * This method deletes all the contents this node has stored in the DHT.
	 */
	public void deleteContents(long... contents);
	
	/**
	 * This method deletes all the contents this node has stored in the DHT.
	 */
	public void clearNeighborDB();
	
	/**
	 * Returns the local UnadaInfo
	 * @return
	 */
	public UnadaInfo getuNaDaInfo();

	/**
	 * Shuts down the peer connection to the DHT.
	 */
	public abstract void shutDown();


	/**
	 * Returns a status code of the overlay.
	 * 
	 * @return  0=OK, 1=Connecting, 2=Error, 3=initial
	 */
	public abstract int getOverlayStatus();
}