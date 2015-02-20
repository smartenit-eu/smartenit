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

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.tomp2p.connection.Bindings;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.FuturePut;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDirect;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.replication.IndirectReplication;
import net.tomp2p.rpc.ObjectDataReply;
import net.tomp2p.storage.Data;
import eu.smartenit.unada.commons.threads.UnadaThreadService;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Content;
import eu.smartenit.unada.db.dto.UnadaInfo;
import eu.smartenit.unada.om.exceptions.OverlayException;
import eu.smartenit.unada.om.messages.BaseMessage;

public class Overlay {
	private static final Logger log = LoggerFactory.getLogger(Overlay.class);
	private static int UNADA_INFO_TTLs = 100;
	
	private PeerDHT peer;
	private int port = 8443;
	private UnadaInfo uNaDaInfo = new UnadaInfo();
	private ScheduledExecutorService executor = UnadaThreadService.getThreadService();
	private OverlayManager manager;
	
	public Overlay(OverlayManager manager) {
		this.manager = manager;
		this.uNaDaInfo = manager.getuNaDaInfo();
	}
	
	public void joinOverlay(InetAddress bootstrapNode, int port) throws OverlayException {
		log.info("Joining Overlay Network with bootstrap node: {}:{}", bootstrapNode, port);
		if ( peer == null ) {
			connect(new Bindings());
			discoverAndBootstrap(bootstrapNode, port);

		}
		updateOverlay(peer.peerAddress().inetAddress(), peer.peerAddress().tcpPort());
	}
	
	public void updateOverlay(InetAddress newAddress, int port)	throws OverlayException {
		log.info("Updating UNaDa information inside DHT.");
		uNaDaInfo.setUnadaAddress(newAddress.getHostAddress());
		uNaDaInfo.setUnadaPort(port);
		manager.resolveGeoLocation(uNaDaInfo);
		FuturePut futurePut;
		try {
			futurePut = peer.put(Number160.createHash(uNaDaInfo.getUnadaID())).data(new Data(uNaDaInfo).ttlSeconds(UNADA_INFO_TTLs)).start();
			futurePut.awaitUninterruptibly();
			if(!futurePut.isSuccess()){
				throw new OverlayException("Put for UNaDa info failed.");
			}
		} catch (IOException e) {
			throw new OverlayException("Unexpected Exception:", e);
		}
	}
	
	public void createOverlay(Bindings bindings)throws OverlayException{
		log.info("Creating new DHT overlay with bindings: {}.", bindings.toString());
		if ( peer != null ) {
			peer.shutdown();
			peer=null;
		}
		try {
			connect(bindings);
		} catch (OverlayException e) {
			throw new OverlayException("Failed to create overlay due to a socket error", e);
		}
		uNaDaInfo.setUnadaAddress(peer.peerAddress().inetAddress());
		uNaDaInfo.setUnadaPort(peer.peerAddress().udpPort());
	}
	
	
	/**
	 * Internal method used to bootstrap the peer to the DHT. The bootstrapNode can be
	 * any node that is already connected to the DHT.
	 * 
	 * @param bootstrapNode
	 * @param port
	 * @throws IOException
	 */
	private void connect(Bindings bindings) throws OverlayException {
		log.info("Creating DHT client Peer object.");
		try{
			peer = new PeerBuilderDHT(new PeerBuilder(Number160.createHash(uNaDaInfo.getUnadaID())).bindings(bindings).ports(port).start()).start();
		}catch (IOException e){
			throw new OverlayException("Error connecting to the DHT.", e);
		}
		new IndirectReplication(peer).start();
		peer.peer().objectDataReply(new ObjectDataReply() {
			public Object reply(PeerAddress sender, Object request) throws Exception {
				if (request instanceof BaseMessage) {
					final BaseMessage msg = (BaseMessage) request;
					executor.execute(new Runnable() {
						public void run() {
							msg.execute(manager);
						}
					});
					return "OK";
				} else {
					return "ERROR";
				}
			}
		});
		executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				refreshData();
			}
		}, UNADA_INFO_TTLs, UNADA_INFO_TTLs, TimeUnit.SECONDS);
	}
	
	public void advertiseContent(long... contentID) {
		log.info("Advertising contents into DHT: ", contentID);
		for(long id : contentID){
			try {
				FuturePut put = peer.add(Number160.createHash(id)).data(new Data(uNaDaInfo).ttlSeconds(UNADA_INFO_TTLs)).start();
				put.awaitUninterruptibly();
				if(put.isSuccess()){
					log.debug("{} - Successfully advertised conten {} to DHT.",uNaDaInfo.getUnadaID() , id);
				}else{
					log.warn("{} - Failed to advertised conten {} to DHT.", uNaDaInfo.getUnadaID(), id);
				}
				//manager.getCloseProviders(id);
			} catch (IOException e) {
				log.error("{} - Failed while advertising conten {}.", uNaDaInfo.getUnadaID(), contentID, e);
			}
			manager.queryProviders(id, null);
		}
	}
	
	private void refreshData(){
		log.info("Doing DHT data maintenance.");
		for(Content content : DAOFactory.getContentDAO().findAll()){
			advertiseContent(content.getContentID());
		}
		try {
			updateOverlay(uNaDaInfo.getInetAddress(), uNaDaInfo.getUnadaPort());
		} catch (OverlayException e) {
			log.error("Failed to refresh UNaDa Info in DHT", e);
		}
	}
	
	private void discoverAndBootstrap(InetAddress bootstrapNode, int port) {
		log.info("Discovering and bootstrapping to: {}:{}", bootstrapNode, port);
		FutureDiscover futureDiscover = peer.peer().discover()
				.inetAddress(bootstrapNode)
				.ports(port).start();
		futureDiscover.awaitUninterruptibly();

		FutureBootstrap futureBootstrap = peer.peer().bootstrap()
				.inetAddress(bootstrapNode)
				.ports(port).start();
		futureBootstrap.awaitUninterruptibly();

		uNaDaInfo.setUnadaAddress(peer.peerAddress().inetAddress());
		uNaDaInfo.setUnadaPort(peer.peerAddress().udpPort());

		log.info("Finished bootsrapping, local address: {}", peer.peerAddress());
	}
	
	public List<InetAddress> getPublicAddresses() throws SocketException{
		List<InetAddress> addrList = new ArrayList<InetAddress>();
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while(interfaces.hasMoreElements()){
			NetworkInterface ifc = interfaces.nextElement(); 
			try {
				if(ifc.isUp()) {
					Enumeration<InetAddress> addresses = ifc.getInetAddresses();
					while(addresses.hasMoreElements()){
						InetAddress addr = addresses.nextElement();
						if(!(addr.isSiteLocalAddress()||addr.isLoopbackAddress())){
							addrList.add(addr);
						}
					}
				}
			} catch (SocketException e) {
				log.error("Error determining if interface {} is up.", ifc, e);
			}
		}
		return addrList;
	}

	public Set<UnadaInfo> queryDHT(long contentID) {
		LinkedHashSet<UnadaInfo> providers = new LinkedHashSet<UnadaInfo>();
		FutureGet get = peer.get(Number160.createHash(contentID)).all().start();
		get.awaitUninterruptibly();
		if(get.isSuccess()){
			log.debug("{} - Succesfully loaded providers from DHT for content {}",uNaDaInfo.getUnadaID(), contentID);
		}else{
			log.warn("{} - Failed to load providers from DHT for content {}. Reason: {}", uNaDaInfo.getUnadaID(), contentID, get.failedReason());
			return providers;
		}
		for(Data d : get.dataMap().values()){
			try {
				if (d.object() instanceof UnadaInfo) {
					if(uNaDaInfo.equals((UnadaInfo)d.object())){
						continue;
					}
					providers.add((UnadaInfo) d.object());
				}
			} catch (ClassNotFoundException e) {
				log.error("{} - Error reading UnadaInfo wrong object.", uNaDaInfo.getUnadaID(), e);
			} catch (IOException e) {
				log.error("{} - Error reading UnadaInfo I/O exception", uNaDaInfo.getUnadaID(), e);
			}
		}
		return providers;
	}
	
	public boolean sendMessage(UnadaInfo destination, BaseMessage message) {
		message.setSender(uNaDaInfo);
		try {
			PeerAddress address = new PeerAddress(Number160.createHash(destination.getUnadaID()), destination.getUnadaAddress(), destination.getUnadaPort(), destination.getUnadaPort());
			FutureDirect future = peer.peer().sendDirect(address).object(message).start();
			future.awaitUninterruptibly();
			if(future.isSuccess() && future.object().equals("OK")){
				log.debug("{} - {} Message sent succesfully", uNaDaInfo.getUnadaID(), message.getClass());
				return true;
			}else{
				log.warn("{} - {} Message sending failed", uNaDaInfo.getUnadaID(), message.getClass());
				return false;
			}

		} catch (ClassNotFoundException e) {
			log.error("{} - Message object type not recognized.",uNaDaInfo.getUnadaID(), e);
		} catch (IOException e) {
			log.error("{} - Input Output erroe while sending message.",uNaDaInfo.getUnadaID(), e);
		}
		return false;
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public void shutDown(){
		if(peer != null){
			log.info("Shutting down Overlay Manager.");
			peer.shutdown();
		}else{
			log.warn("Peer already shut down!");
		}
	}
	
}
