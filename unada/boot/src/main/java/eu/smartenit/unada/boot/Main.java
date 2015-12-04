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
package eu.smartenit.unada.boot;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Random;

import net.tomp2p.connection.Bindings;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.nat.FutureNAT;
import net.tomp2p.nat.PeerBuilderNAT;
import net.tomp2p.nat.PeerNAT;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.replication.IndirectReplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static Logger log = LoggerFactory.getLogger(Main.class);
	PeerDHT peer;
	
	Iterable<InetSocketAddress> nodes;
	
	public static void main(String[] args) throws Exception {
		
		Main start = new Main();
		log.info("Starting the bootstrap node");
		
		if(args.length < 1){
		start.start();
		} else {
			if(args[0].equalsIgnoreCase("-c")){
				start.create();
			}
		}

	}
	
	
	private void start() throws IOException, InterruptedException{
		setNodes();
		Random rnd = new Random();
		
		log.info("Creating DHT client Peer object.");
		Bindings bindings = new Bindings();
		bindings.listenAny();
				
		peer = new PeerBuilderDHT(new PeerBuilder(Number160.createHash(rnd.nextLong())).bindings(bindings).ports(4001).start()).start();
//		while(!bootstrap()){
//			log.warn("No bootstrap node could be reached.");
//			Thread.sleep(1000*60*5);
//		};
	}
	
	private void setNodes(){
		ArrayList<InetSocketAddress> addresses = new ArrayList<>();
		
		
//		addresses.add( new InetSocketAddress("emanicslab2.csg.uzh.ch", 4001));
//		addresses.add( new InetSocketAddress("emanicslab2.ps.tu-darmstadt.de", 4001));
//		addresses.add( new InetSocketAddress("emanicslab2.ewi.utwente.nl", 4001));
//		addresses.add(  new InetSocketAddress("emanicslab1.unige.ch", 4001));
//		addresses.add(  new InetSocketAddress("moscu.upc.es", 4001));
//		addresses.add(  new InetSocketAddress("emanicslab1.ps.tu-darmstadt.de", 4001));
//		addresses.add(  new InetSocketAddress("emanicslab1.man.poznan.pl", 4001));
//		addresses.add(  new InetSocketAddress("emanicslab1.informatik.unibw-muenchen.de", 4001));
//		addresses.add(  new InetSocketAddress("emanicslab1.csg.uzh.ch", 4001));
//		addresses.add(  new InetSocketAddress("muro.upc.es", 4001));
//		addresses.add(  new InetSocketAddress("emanicslab1.ewi.utwente.nl",4001));
		
		nodes = addresses;
	}
	
	private boolean bootstrap(){
		
		
		InetSocketAddress theAddress = null;
		FutureDiscover futureDiscover = null;
		for(InetSocketAddress addr : nodes){
			log.info("Discovering and bootstrapping to: {}", addr);
			//loop
			futureDiscover = peer.peer().discover()
					.expectManualForwarding()
					.inetAddress(addr.getAddress())
					.ports(addr.getPort()).start();
			
			futureDiscover.awaitUninterruptibly();
			if(futureDiscover.isSuccess()){
				theAddress = addr;
				break;
			}
		}
		
		if(theAddress == null){
			log.error("Could not discover with any of the provided nodes.");
			return false;
		}

		PeerNAT pn = new PeerBuilderNAT(peer.peer()).start();
		FutureNAT fn = pn.startSetupPortforwarding(futureDiscover);
		fn.awaitUninterruptibly();

		//add collection of peer addresses
		FutureBootstrap futureBootstrap = peer.peer().bootstrap()
				.inetAddress(theAddress.getAddress())
				.ports(theAddress.getPort()).start();
		futureBootstrap.awaitUninterruptibly();

		if(futureBootstrap.isFailed()){
			log.error("Could not bootstrap to any bootstrap node!");
			return false;
		}

		log.info("Finished bootsrapping, local address: {}", peer.peerAddress());
		return true;
	}
	
	private boolean create(){
		if ( peer != null ) {
			peer.shutdown().awaitUninterruptibly();
			peer=null;
		}
		Random rnd = new Random();
		try {
			peer = new PeerBuilderDHT(new PeerBuilder(Number160.createHash(rnd.nextLong())).ports(4001).start()).start();
			new IndirectReplication(peer).start();
		} catch (Exception e) {
			log.error("Failed to create overlay due to a socket error", e);
			return false;
		}
		return true;
	}

}
