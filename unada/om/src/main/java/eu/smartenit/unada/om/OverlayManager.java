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
package eu.smartenit.unada.om;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import net.tomp2p.connection.Bindings;
import net.tomp2p.rpc.SimpleBloomFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.commons.constants.UnadaConstants;
import eu.smartenit.unada.commons.logging.UnadaLogger;
import eu.smartenit.unada.commons.threads.UnadaThreadService;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Content;
import eu.smartenit.unada.db.dto.UNaDaConfiguration;
import eu.smartenit.unada.db.dto.UnadaInfo;
import eu.smartenit.unada.om.exceptions.DownloadException;
import eu.smartenit.unada.om.exceptions.OverlayException;
import eu.smartenit.unada.om.messages.BaseMessage;
import eu.smartenit.unada.om.messages.ContentInfoRequestMessage;
import eu.smartenit.unada.om.messages.ProviderRequestMessage;
import eu.smartenit.unada.om.messages.TracerouteReplyMessage;
import eu.smartenit.unada.om.messages.TracerouteRequestMessage;
import eu.smartenit.unada.tpm.TopologyProximityMonitor;
import eu.smartenit.unada.tpm.TopologyProximityMonitorImpl;


/**
 * This is the main class of the Overlay Manager component. To use the Overlay
 * Manager this class needs to be instantiated.
 * 
 * 
 * @author Andri Lareida
 *
 */
public class OverlayManager implements IOverlayManager {

	private static final Logger log = LoggerFactory.getLogger(OverlayManager.class);

	private UnadaInfo uNaDaInfo = new UnadaInfo();

	private TopologyProximityMonitor tpm = new TopologyProximityMonitorImpl(this);
	

	private NeighborDatabase neighborDatabase = new NeighborDatabase(tpm);
	private Map<Long, CountDownLatch> countDownLatches = new HashMap<>();

	private Map<NeighborStatistics, List<Content>> contentLists = new ConcurrentHashMap<NeighborStatistics, List<Content>>();
	private CountDownLatch contentRequestCounter = new CountDownLatch(0);

	private Map<Long, ContentDownloadHandler> downloadHandlers = new HashMap<>();
	
	private Overlay overlay;
	
//	InetSocketAddress addr2 = new InetSocketAddress("emanicslab2.man.poznan.pl", 4001);
//	InetSocketAddress addr3 = new InetSocketAddress("emanicslab2.ps.tu-darmstadt.de", 4001);
//	InetSocketAddress addr4 = new InetSocketAddress("emanicslab2.ewi.utwente.nl", 4001);
//	InetSocketAddress addr5 = new InetSocketAddress("emanicslab2.eecs.jacobs-university.de", 4001);
//	InetSocketAddress addr6 = new InetSocketAddress("moscu.upc.es", 4001);
//	InetSocketAddress addr7 = new InetSocketAddress("emanicslab1.ps.tu-darmstadt.de", 4001);
//	InetSocketAddress addr8 = new InetSocketAddress("emanicslab1.man.poznan.pl", 4001);
//	InetSocketAddress addr9 = new InetSocketAddress("emanicslab1.informatik.unibw-muenchen.de", 4001);
//	InetSocketAddress addr10 = new InetSocketAddress("emanicslab1.csg.uzh.ch", 4001);
//	InetSocketAddress addr11 = new InetSocketAddress("planck232ple.test.iminds.be", 4001);
//	InetSocketAddress addr12 = new InetSocketAddress("emanicslab1.ewi.utwente.nl",4001);

	public OverlayManager(String uNadaId) {
		getuNaDaInfo().setUnadaID(uNadaId);
		setOverlay(new Overlay(this));
		
		//this is added for study logging
		UnadaThreadService.getThreadService().scheduleAtFixedRate(new Runnable() {
					@Override
					public void run() {
						logNeighbors();;
					}
				}, 1, 1, TimeUnit.HOURS);
		
		// Overlay info mapping Unada ID with user ID md5 hash
		UnadaLogger.overall.debug("{}: Overlay Created ({}, {})", new Object[]{
				UnadaConstants.UNADA_OWNER_MD5,
				System.currentTimeMillis(),
				uNadaId}
				);
	}

	/* (non-Javadoc)
	 * @see eu.smartenit.unada.om.IOverlayManager#joinOverlay(java.net.InetAddress, int)
	 */
	public void joinOverlay(InetAddress bootstrapNode, int port) throws OverlayException {
		
		InetSocketAddress addr = new InetSocketAddress(bootstrapNode, port);
		
		getOverlay().joinOverlay(new Bindings(), addr);//, addr2, addr3, addr4, addr5, addr6, addr7, addr8, addr9, addr10, addr11, addr12);
	}
	
	/* (non-Javadoc)
	 * @see eu.smartenit.unada.om.IOverlayManager#createOverlay()
	 */
	public void createOverlay() throws OverlayException{
		Bindings bind = new Bindings();
		bind.listenAny();
		if(!tryBootstrapNodes(bind)){
			log.warn("No node could be reached, creating new overlay network");
			getOverlay().createOverlay(bind);
		}
	}

	/* (non-Javadoc)
	 * @see eu.smartenit.unada.om.IOverlayManager#createOverlay(java.net.InetAddress)
	 */
	@Override
	public void createOverlay(InetAddress address) throws OverlayException {
		getOverlay().createOverlay(new Bindings().addAddress(address));
	}

	/* (non-Javadoc)
	 * @see eu.smartenit.unada.om.IOverlayManager#createOverlay(java.lang.String)
	 */
	@Override
	public void createOverlay(String interfaceHint) throws OverlayException {
		getOverlay().createOverlay(new Bindings().addInterface(interfaceHint));
	}
	
	private boolean tryBootstrapNodes(Bindings bindings){
		try {
			getOverlay().joinOverlay(bindings);//, addr2, addr3, addr4, addr5, addr6, addr7, addr8, addr9, addr10, addr11, addr12);
			return true;
		}catch(OverlayException e){
			log.warn("No default bootstrap node could be reached. Message: {}", e.getMessage());
		}
		return false;
	}

	
	public void shutDown(){
		overlay.shutDown();
	}

	/**
	 * This method reads the Unadaconfiguration and puts the location into
	 * the Unada Info.
	 * 
	 * @param info
	 */
	public void resolveGeoLocation(UnadaInfo info) {
		UNaDaConfiguration conf = DAOFactory.getuNaDaConfigurationDAO().findAll().get(0);
		info.setLongitude(conf.getLocation().getLongitude());
		info.setLatitude(conf.getLocation().getLatitude());
	}

	/* (non-Javadoc)
	 * @see eu.smartenit.unada.om.IOverlayManager#advertiseContent(java.lang.String[])
	 */
	public void advertiseContent(long... contentID) {
		getOverlay().advertiseContent(contentID);
	}

	/**
	 * @param contentID
	 * @param filter
	 * @return
	 */
	public Set<UnadaInfo> queryProviders(long contentID, SimpleBloomFilter<String> filter) {
		if(filter == null)
			filter = new SimpleBloomFilter<>(1024,10);
		
		filter.add(this.getuNaDaInfo().getUnadaID());
		log.info("Querying providers for {}", contentID);
		//If there are no providers yet the DHT has to be queried.
		if(neighborDatabase.getProviderNumber(contentID) < 1){
			neighborDatabase.addProviders(contentID, overlay.queryDHT(contentID));
		}
		
		Set<UnadaInfo> tempProviders = neighborDatabase.getProviders(contentID);
		if(tempProviders.size() == 0){
			return tempProviders;
		}

		//Try to reach closest Unada to request more providers, if message cannot be sent the unadainfo is removed
		boolean success = false;
		int count = 0;

		synchronized (countDownLatches) {
			for(UnadaInfo info : tempProviders){
				if(info.equals(this.getuNaDaInfo()) || filter.contains(info.getUnadaID()))
					continue;
				success = sendMessage(info, new ProviderRequestMessage(contentID, neighborDatabase.getProviders(contentID), false));
				if(success){
					count++;
					if(count >= 2){
						break;
					}
				}else{
					log.debug("{} - Failed to send provider request to Unada, removing unada {}", getuNaDaInfo().getUnadaID(), info.getUnadaID());
					neighborDatabase.removeNeighbor(info.getUnadaID());
				}
			}
			countDownLatches.put(contentID, new CountDownLatch(count));
		}

		//TODO this timeout should be configurable
		log.debug("{} - Waiting for {} provider returns...", uNaDaInfo.getUnadaID(), count );
		try {
			countDownLatches.get(contentID).await(10, TimeUnit.SECONDS);
			log.debug("{} - Got {} provider returns, continueing.", uNaDaInfo.getUnadaID(), count );
		} catch (InterruptedException e) {
			log.warn("{} - Interrupted while waiting on countdown latch for providers, continuing normally.", uNaDaInfo.getUnadaID());
		}

		return neighborDatabase.getProviders(contentID, filter);
	}

	/* (non-Javadoc)
	 * @see eu.smartenit.unada.om.IOverlayManager#getPrediction()
	 */
	public synchronized List<Content> getPrediction() {
		log.info("Calculating overlay prediction.");
		LinkedHashMap<String, UnadaInfo> neighbors = neighborDatabase.getNeighbors();
		log.info("Predicting with {} neighbors.", neighbors.size());
		
		contentRequestCounter = new CountDownLatch(neighbors.size());
		for(UnadaInfo info : neighbors.values()){
			if(!sendMessage(info, new ContentInfoRequestMessage())){
				contentRequestCounter.countDown();
				log.warn("Failed to send Content info request to {}", info.getUnadaID());
			}else{
				log.debug("Sent Content info request to {}", info.getUnadaID());
			}
		}

		//Wait until either all neighbors have replied or 10 seconds have passed
		try {
			contentRequestCounter.await(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.error("Thread got interrupted while waiting for countdown latch.");
		}

		//create a filter to recognize content that is already in the local cache
		List<Content> content = getNonPrefetchedContent();
		SimpleBloomFilter<Long> filter = new SimpleBloomFilter<Long>(100*1024, content.size());
		for(Content c : content){
			filter.add(c.getContentID());
		}

		Map<Long, RankedContent> rankedContents = new HashMap<Long, RankedContent>();
		//count similarity per neighbor
		for(Entry<NeighborStatistics, List<Content>> entry : contentLists.entrySet()){
			entry.getKey().setTotalContent(entry.getValue().size());
			String unadaID = entry.getKey().getUnadaInfo().getUnadaID();
			int hopCount = Integer.MAX_VALUE;
			if(neighbors.containsKey(unadaID)){
                hopCount = neighbors.get(unadaID).getHopCount();
                log.debug("Hop count from Unada {} is found to be {}", unadaID, hopCount);
			}
            log.info("Unada ID " + unadaID + " has hop count = " + hopCount);
			
			int counter = 0; 
			for(Content c : entry.getValue()){
				if(rankedContents.containsKey(c.getContentID())){
					boolean updated = rankedContents.get(c.getContentID()).getContent().updateHopCount(hopCount);
					log.debug("Updated hopcount of known content {} to {} hops is {}", c.getContentID(), hopCount, updated);
				}else{
					boolean updated = c.updateHopCount(hopCount);
					log.debug("Updated hopcount of new content {} to {} hops is {}", c.getContentID(), hopCount, updated);
					rankedContents.put(c.getContentID(), new RankedContent(c));
				}

				if(filter.contains(c.getContentID())){
					counter++;
				}
			}
			entry.getKey().setCommonContent(counter);
		}
		log.debug("Similarity of neighbors {}", contentLists);

		//count score per content
		for(Entry<NeighborStatistics, List<Content>> entry : contentLists.entrySet()){
			for(Content c : entry.getValue()){
				//The number of common content is added to the score of a content
				rankedContents.get(c.getContentID()).incrementScore(entry.getKey().getCommonContent());;
			}
		}

		List<Content> rankedList = new LinkedList<Content>();
		for (RankedContent rc : rankedContents.values()){
			rankedList.add(rc.getContent());
			rc.getContent().setCacheScore(rc.getScore());
		}
		Collections.sort(rankedList, Collections.reverseOrder());
		log.debug("Prediction result: {}", rankedList);
		contentLists.clear();
		return rankedList;
	}

	/* (non-Javadoc)
	 * @see eu.smartenit.unada.om.IOverlayManager#downloadContent(java.lang.String)
	 */
	public IFutureDownload downloadContent(Content content) throws DownloadException {
		log.info("Downloading content {}", content.getContentID());
		ContentDownloadHandler handler = new ContentDownloadHandler(content);
		downloadHandlers.put(content.getContentID(), handler);
		handler.startDownload(this);
		return handler;
	}

	/* (non-Javadoc)
	 * @see eu.smartenit.unada.om.IOverlayManager#sendMessage(eu.smartenit.unada.db.dto.UnadaInfo, eu.smartenit.unada.om.messages.BaseMessage)
	 */
	public boolean sendMessage(UnadaInfo destination, BaseMessage message) {
		return overlay.sendMessage(destination, message);
	}
	
	@Override
	public int getOverlayStatus(){
		return overlay.getOverlayStatus();
	}

	public void sendRemoteTracerouteRequest(UnadaInfo dst) {
		TracerouteRequestMessage msg = new TracerouteRequestMessage();
		this.sendMessage(dst, msg);
	}

	public void sendRemoteTracerouteReply(UnadaInfo dst, List<Integer> vector) {
		TracerouteReplyMessage msg = new TracerouteReplyMessage();
		msg.setVector(vector);
		this.sendMessage(dst, msg);
	}

	//Call back methods
	public void addContentInfo(UnadaInfo info, List<Content> content){
		log.debug("Adding content info from {} for {} contents.", info.getUnadaID(), content.size());
		NeighborStatistics stats = new NeighborStatistics();
		stats.setUnadaInfo(neighborDatabase.getUnadaInfo(info.getUnadaID()));
		contentLists.put(stats, content);
		contentRequestCounter.countDown();
		log.debug("contentRequestCounter counted down.");
	}

	public synchronized void addProviders(long contentID, Set<UnadaInfo> newProviders){
		newProviders.remove(this.getuNaDaInfo());
		log.debug("{} - Received {} providers for content {}.", uNaDaInfo.getUnadaID(), newProviders.size(), contentID);
		neighborDatabase.addProviders(contentID, newProviders);
		countDownLatch(contentID);
	}

	private void countDownLatch(long contentID){
		synchronized (countDownLatches) {

			CountDownLatch latch = countDownLatches.get(contentID);
			if(latch != null){
				latch.countDown();
				log.debug("{} - Counted latch for {} down.", uNaDaInfo.getUnadaID(), contentID);
			}
		}
	}

	public void addProviderFromMessage(long contentID, UnadaInfo info){
		if(this.getuNaDaInfo().equals(info))
			return;
		neighborDatabase.addProviders(contentID, info);
		log.info("Provider {} added for content id {}", info, contentID);
	}
	
	@Override
	public void deleteContents(long... contents) {
		overlay.deleteContents();
		
	}

	//Internal Methods

	// Get and set methods

	/* (non-Javadoc)
	 * @see eu.smartenit.unada.om.IOverlayManager#getCloseProviders(long)
	 */
	public Set<UnadaInfo> getCloseProviders(long contentID){
		return this.getCloseProviders(contentID, null);
	}
	
	public Set<UnadaInfo> getCloseProviders(long contentID, SimpleBloomFilter<String> filter){
		log.debug("{} - Close providers were asked for.", uNaDaInfo.getUnadaID());
		if(filter == null)
			filter = new SimpleBloomFilter<>(1024,10);
		Set<UnadaInfo> list = neighborDatabase.getProviders(contentID, filter);
		if(list == null || list.size() < 5){
			log.debug("{} - Not enough providers for {}, query for more.", uNaDaInfo.getUnadaID(), contentID);
			list = this.queryProviders(contentID, filter);
		}
		
		return list;
	}
	
	@Override
	public int getHopCount(Content content) {
		Set<UnadaInfo> providers = getCloseProviders(content.getContentID());
		for(UnadaInfo info : providers){
			return info.getHopCount();
		}
		return Integer.MAX_VALUE;
	}
	
	void logNeighbors(){
		StringBuffer neighbors = new StringBuffer();
		neighbors.append("[");
		for(UnadaInfo info : neighborDatabase.getNeighbors().values()){
			neighbors.append("{");
			neighbors.append(info.getUnadaID()).append(", ");
			neighbors.append(info.getUnadaAddress());
			neighbors.append("}");
		}
		neighbors.append("]");
		
		//5. Overlay neighbors (ip, userID, fascebookID) in fixed time intervals e.g. every hour
		UnadaLogger.overall.debug("{}: Overlay Neighbors ({}, {})", new Object[]{
				UnadaConstants.UNADA_OWNER_MD5, 
				System.currentTimeMillis(),
				neighbors.toString()});
	}

	/* (non-Javadoc)
	 * @see eu.smartenit.unada.om.IOverlayManager#getuNaDaInfo()
	 */
	public UnadaInfo getuNaDaInfo() {
		return uNaDaInfo;
	}

	public void setuNaDaInfo(UnadaInfo uNaDaInfo) {
		this.uNaDaInfo = uNaDaInfo;
	}

	public TopologyProximityMonitor getTpm() {
		return tpm;
	}

	public void setTpm(TopologyProximityMonitor tpm) {
		this.tpm = tpm;
		neighborDatabase.setTpm(tpm);
	}

	public void setPort(int port) {
		getOverlay().setPort(port);
	}

	public Map<Long, ContentDownloadHandler> getDownloadHandlers() {
		return downloadHandlers;
	}

	public List<Content> getNonPrefetchedContent() {
		return DAOFactory.getContentDAO().findAllNotPrefetched();
	}

	public Map<NeighborStatistics, List<Content>> getContentLists() {
		return contentLists;
	}

	public void setContentLists(Map<NeighborStatistics, List<Content>> contentLists) {
		this.contentLists = contentLists;
	}

	public CountDownLatch getContentRequestCounter() {
		return contentRequestCounter;
	}

	public void setContentRequestCounter(CountDownLatch countDownLatch) {
		this.contentRequestCounter = countDownLatch;
	}
	
	void addUnadaInfoForTest(UnadaInfo info){
		info.setHopCount(1);
		neighborDatabase.addUnadaInfo(info);
	}

	public Overlay getOverlay() {
		return overlay;
	}

	public void setOverlay(Overlay overlay) {
		this.overlay = overlay;
	}

	@Override
	public void clearNeighborDB() {
		neighborDatabase = new NeighborDatabase(tpm);
	}

}
