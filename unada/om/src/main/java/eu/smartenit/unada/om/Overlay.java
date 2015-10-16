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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.tomp2p.connection.Bindings;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.FuturePut;
import net.tomp2p.dht.FutureRemove;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDirect;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.nat.FutureNAT;
import net.tomp2p.nat.PeerBuilderNAT;
import net.tomp2p.nat.PeerNAT;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.replication.IndirectReplication;
import net.tomp2p.rpc.ObjectDataReply;
import net.tomp2p.storage.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.commons.constants.UnadaConstants;
import eu.smartenit.unada.commons.threads.UnadaThreadService;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Content;
import eu.smartenit.unada.db.dto.UnadaInfo;
import eu.smartenit.unada.om.exceptions.OverlayException;
import eu.smartenit.unada.om.messages.BaseMessage;

public class Overlay {
	private static final Logger log = LoggerFactory.getLogger(Overlay.class);
	private static int UNADA_INFO_TTLs = 60*60*4;

	private PeerDHT peer;
	private int port = 8443;
	private UnadaInfo uNaDaInfo = new UnadaInfo();
	private ScheduledExecutorService executor = UnadaThreadService.getThreadService();
	private OverlayManager manager;
	
	private int overlayStatus = 3; // 0=OK, 1=Connecting, 2=Error, 3=initial

	public Overlay(OverlayManager manager) {
		this.manager = manager;
		this.uNaDaInfo = manager.getuNaDaInfo();
		this.setOverlayStatus(3);
	}

	public void joinOverlay(Bindings bindings, InetSocketAddress... bootstrapNodes) throws OverlayException {
		setOverlayStatus(1);
		log.info("Joining Overlay Network with bootstrap nodes: {}", bootstrapNodes, port);
		if ( peer != null ) {
			peer.shutdown().awaitUninterruptibly();
			peer = null;
		}

		connect(bindings);
		discoverAndBootstrap(bootstrapNodes);
		updateOverlay();
	}
	
	public void updateOverlay()	throws OverlayException {
		this.updateOverlay(peer);
	}

	public void updateOverlay(PeerDHT peer)	throws OverlayException {
		log.info("Updating UNaDa information inside DHT.");
		updateUnadaInfo(peer);
		manager.resolveGeoLocation(uNaDaInfo);
		FutureRemove remove = peer.remove(Number160.createHash(uNaDaInfo.getUnadaID())).all().start();
		try {
			remove.await();
		} catch (InterruptedException e1) {
			log.error("Failed to delete old contents.", e1);
		}
		
		FuturePut futurePut;
		
		try {
			
			log.debug("Updating Unada with address: {}, tcp port {}, udp port {}.", uNaDaInfo.getUnadaAddress(), uNaDaInfo.getTcpPort(), uNaDaInfo.getUdpPort());
			futurePut = peer.put(Number160.createHash(uNaDaInfo.getUnadaID())).data(new Data(uNaDaInfo).ttlSeconds(UNADA_INFO_TTLs + 30)).start();
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
			peer.shutdown().awaitUninterruptibly();
			peer=null;
		}
		try {
			connect(bindings);
		} catch (OverlayException e) {
			throw new OverlayException("Failed to create overlay due to a socket error", e);
		}
		log.info("Overlay created successfully.");
		updateUnadaInfo(peer);
		
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
			setOverlayStatus(2);
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
		deleteContents(contentID);
		for(long id : contentID){
			try {
				FuturePut put = peer.put(Number160.createHash(id)).data(Number160.createHash(uNaDaInfo.getUnadaID()), new Data(uNaDaInfo).ttlSeconds(UNADA_INFO_TTLs + 30)).start();
				put.awaitUninterruptibly();
				if(put.isSuccess()){
					log.debug("{} - Successfully advertised conten {} to DHT.",uNaDaInfo.getUnadaID() , id);
				}else{
					log.warn("{} - Failed to advertised conten {} to DHT.", uNaDaInfo.getUnadaID(), id);
				}
			} catch (IOException e) {
				log.error("{} - Failed while advertising conten {}.", uNaDaInfo.getUnadaID(), contentID, e);
			}
			manager.queryProviders(id, null);
		}
	}

	private void refreshData(){
		log.info("Doing DHT data maintenance.");
		for(Content content : DAOFactory.getContentDAO().findAllDownloaded()){
			advertiseContent(content.getContentID());
		}
		try {
			updateOverlay(peer);
		} catch (OverlayException e) {
			log.error("Failed to refresh UNaDa Info in DHT", e);
		}
	}

	private void discoverAndBootstrap(InetSocketAddress... bootstrapNodes) throws OverlayException {
		InetSocketAddress theAddress = null;
		FutureDiscover futureDiscover = null;
		PeerNAT pn = null;
		for(InetSocketAddress addr : bootstrapNodes){
			log.info("Discovering and bootstrapping to: {}", addr);
			//loop
			PeerAddress paOrig = peer.peerBean().serverPeerAddress();
			futureDiscover = peer.peer().discover()
					.inetAddress(addr.getAddress())
					.ports(addr.getPort()).start();
			futureDiscover.awaitUninterruptibly();

			if(futureDiscover.isFailed()){
				log.info("NAT or Firewall present, trying UPNP.");
				pn = new PeerBuilderNAT(peer.peer()).start();
				FutureNAT fn = pn.startSetupPortforwarding(futureDiscover);
				fn.awaitUninterruptibly();

				if(fn.isFailed()){
					log.info("UPNP Failed, checking direct port forwarding.");
					peer.peerBean().serverPeerAddress(paOrig);
					futureDiscover = peer.peer().discover()
							.expectManualForwarding()
							.inetAddress(addr.getAddress())
							.ports(addr.getPort()).start();
					futureDiscover.awaitUninterruptibly();
					
				}
				if(fn.isSuccess() || futureDiscover.isSuccess()){
					theAddress = addr;
					break;
				}else{
					log.info("Discovering failed trying next node.");
				}
			}else {
				theAddress = addr;
				break;
			}
		}

		if(theAddress == null){
			setOverlayStatus(2);
			throw new OverlayException("Could not discover to any bootstrap node!");
		}
		//add collection of peer addresses
		FutureBootstrap futureBootstrap = peer.peer().bootstrap()
				.inetAddress(theAddress.getAddress())
				.ports(theAddress.getPort()).start();
		futureBootstrap.awaitUninterruptibly();

		if(futureBootstrap.isFailed()){
			setOverlayStatus(2);
			throw new OverlayException("Could not bootstrap to any bootstrap node!");
		}
		
		log.info("Finished bootsrapping, local address: {}", peer.peerAddress());
		updateUnadaInfo(peer);
		setOverlayStatus(0);
	}
	
	private void updateUnadaInfo(PeerDHT peer){
		uNaDaInfo.setUnadaAddress(peer.peerAddress().inetAddress());
		uNaDaInfo.setTcpPort(peer.peerAddress().tcpPort());
		uNaDaInfo.setUdpPort(peer.peerAddress().udpPort());
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
			} catch (Exception e) {
				log.error("{} - Error reading UnadaInfo generic exception", uNaDaInfo.getUnadaID(), e);
			}
		}
		return providers;
	}

	public boolean sendMessage(UnadaInfo destination, BaseMessage message) {
		message.setSender(uNaDaInfo);
		try {
			PeerAddress address = new PeerAddress(Number160.createHash(destination.getUnadaID()), destination.getUnadaAddress(), destination.getTcpPort(), destination.getUdpPort());
			FutureDirect future = peer.peer().sendDirect(address).object(message).start();
			future.await(5000);
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
		} catch (InterruptedException e) {
			log.error("{} - Sending Message failed, interrupted.",uNaDaInfo.getUnadaID(), e);
		}
		return false;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getOverlayStatus() {
		return overlayStatus;
	}

	public synchronized void setOverlayStatus(int overlayStatus) {
		log.debug("updating overlay status with: " + overlayStatus);
		UnadaConstants.OVERLAY_STATUS = overlayStatus;
		this.overlayStatus = overlayStatus;
		writeStatusToFile(overlayStatus);
		
		log.debug("updated overlay status");
	}

	private void writeStatusToFile(int overlayStatus) {
		Path file = Paths.get(System.getenv("HOME"),"overlay_status");
		log.debug("writing status to file: {}" , file.toString());
		
		try {
			log.debug("file already existed: {}", Files.deleteIfExists(file));
			
			try (OutputStream out = Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out))){
				
					if(overlayStatus == 0){
						log.debug("writing {}" , 1);
						writer.write("1");
					}else{
						log.debug("writing {}" , 0);
						writer.write("0");
					}
					log.debug("closing writer");
					writer.close();
					
				} catch (Exception e) {
					log.warn("Could nor write overlay status.");
				}
		} catch (IOException e1) {
			log.warn("Failed to remove file");
		}
	}

	public void deleteContents(long... contents){
		for(long contentID : contents){
			FutureRemove remove = peer.remove(Number160.createHash(contentID)).contentKey(Number160.createHash(uNaDaInfo.getUnadaID())).start();
			try {
				remove.await();
			} catch (InterruptedException e1) {
				log.error("Failed to delete old content entries from DHT.", e1);
			}
		}
		
	}
	
	public void shutDown(){
		setOverlayStatus(2);
		if(peer != null){
			log.info("Shutting down Overlay Manager.");
			peer.shutdown();
		}else{
			log.warn("Peer already shut down!");
		}
	}

}
