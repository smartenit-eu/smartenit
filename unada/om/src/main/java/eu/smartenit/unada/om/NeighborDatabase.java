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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.tomp2p.rpc.SimpleBloomFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.util.Maps;

import eu.smartenit.unada.db.dto.UnadaInfo;
import eu.smartenit.unada.tpm.ASVector;
import eu.smartenit.unada.tpm.TopologyProximityMonitor;

public class NeighborDatabase {
	
	
	private static final Logger log = LoggerFactory.getLogger(NeighborDatabase.class);
	
	
	private Map<String, UnadaInfo> neighbors = new HashMap<>();
	private Map<Long, Set<String>> providers = new HashMap<>();
	
	private TopologyProximityMonitor tpm;
	
	public NeighborDatabase(TopologyProximityMonitor tpm) {
		this.tpm = tpm;
	}
	
	public synchronized void addUnadaInfo(UnadaInfo... infos){
		Set<UnadaInfo> set = new HashSet<>();
		for(UnadaInfo info : infos){
			set.add(info);
		}
		this.addUnadaInfo(set);
	}
	
	public synchronized void addUnadaInfo(Set<UnadaInfo> infos){
		log.debug("Checking and adding {} new unada infos.", infos.size());
		Set<UnadaInfo> infoSet = new HashSet<>();
		int counter = 0;
		for(UnadaInfo info : infos){
			//Check if the Unada is new (not known yet)
			if(!neighbors.containsKey(info.getUnadaID())){
				//Check if traceroute info is already set in unadainfo
				if(info.getHopCount() == Integer.MAX_VALUE){
					//if not set we have to resolve later
					infoSet.add(info);
				} else{
					neighbors.put(info.getUnadaID(), info);
					counter++;
				}
			}
		}
		log.debug("{} new unadas added directly. {} Unadas' hop counts need to be resolved.", counter, infoSet.size());
		resolveAndAdd(infoSet);
	}

	private void resolveAndAdd(Set<UnadaInfo> infoSet) {
		int counter = 0;
		try {
			for(ASVector vector : tpm.sortClosest(infoSet)){
				UnadaInfo info = vector.getRemoteAddress();
				
				if(vector.getVector() == null || vector.getVector().contains(null)){
					log.debug("AS vector for {}, is null", info.getUnadaID());
					info.setHopCount(1);
				}else {
					log.debug("AS vector for {}, {} hops", info.getUnadaID(),vector.getVector().size());
					info.setHopCount(vector.getVector().size()-1);
				}
				neighbors.put(info.getUnadaID(), info);
				counter++;
			}
		} catch (InterruptedException e) {
			log.error("Error while resolving AS vectors!", e);
		}
		log.debug("{} new Unadas resolved and added to neighbor list", counter);
	}
	
	public synchronized void addProviders(long contentID, UnadaInfo... infos){
		Set<UnadaInfo> set = new HashSet<>();
		for(UnadaInfo info : infos){
			set.add(info);
		}
		this.addProviders(contentID, set);
	}
	
	public synchronized void addProviders(long contentID, Set<UnadaInfo> infos){
		log.debug("Received {} providers for content id: {}", infos.size(), contentID);
		this.addUnadaInfo(infos);
		if(!providers.containsKey(contentID)){
			providers.put(contentID, new HashSet<String>());
		}
		Set<String> providersForContent = providers.get(contentID);
		int counter = 0;
		for(UnadaInfo info : infos){
			counter += (providersForContent.add(info.getUnadaID())? 1 : 0);
		}
		log.debug("Added {} new providers for content {}, total providers are now {}", counter, contentID, providersForContent.size());
		
	}
	
	public synchronized Set<UnadaInfo> getProviders(long contentID){
		Set<UnadaInfo> sortedProviders = new TreeSet<>();
		for(String id : providers.get(contentID)){
			UnadaInfo inf = neighbors.get(id);
			if(inf == null){
				sortedProviders.remove(id);
				} else { 
				sortedProviders.add(inf);
			}
		}
		log.debug("Returned {} sorted providers for content {}", sortedProviders.size(), contentID);
		return sortedProviders;
	}
	
	public synchronized Set<UnadaInfo> getProviders(long contentID, SimpleBloomFilter<String> filter){
		Set<UnadaInfo> sortedProviders = new TreeSet<>();
		if(!providers.containsKey(contentID))
			return sortedProviders;
		for(String id : providers.get(contentID)){
			if(filter.contains(id)){
				continue;
			}else{
				sortedProviders.add(neighbors.get(id));
			}
		}
		log.debug("Returned {} sorted providers for content {}", sortedProviders.size(), contentID);
		return sortedProviders;
	}
	
	public LinkedHashMap<String, UnadaInfo> getNeighbors(){
		return new LinkedHashMap<>(neighbors);
	}
	
	public synchronized int getProviderNumber(long contentID){
		if(providers.containsKey(contentID)){
			return providers.get(contentID).size();		
		}else{
			return 0;
		}
	}

	public synchronized void removeNeighbor(String id){
		log.debug("Removing Unada {} from neighbors and providers.", id);
		neighbors.remove(id);
		for(Set<String> infos : providers.values()){
			infos.remove(id);
		}
	}
	
	public void setTpm(TopologyProximityMonitor tpm){
		this.tpm = tpm;
	}
	
	public UnadaInfo getUnadaInfo(String id){
		return neighbors.get(id);
	}

}
