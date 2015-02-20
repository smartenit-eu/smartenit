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
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.db.dto.Content;
import eu.smartenit.unada.db.dto.UnadaInfo;
import eu.smartenit.unada.om.exceptions.DownloadException;
import eu.smartenit.unada.om.messages.DownloadRequestMessage;

public class ContentDownloadHandler implements IFutureDownload{

	private static Logger log = LoggerFactory.getLogger(ContentDownloadHandler.class);
	private Content content;
	
	private int lastChunk = -1;
	private PriorityQueue<Chunk> chunkBacklog = new PriorityQueue<Chunk>();
	private DigestOutputStream writer = null;
	private long bytesDownloaded = 0;

	private Set<UnadaInfo> providers;
	private OverlayManager om = null;

	private AtomicBoolean started = new AtomicBoolean(false);
	private AtomicBoolean completed = new AtomicBoolean(false);
	private AtomicBoolean success = new AtomicBoolean(false);
	
	private CountDownLatch barrier = new CountDownLatch(1);
	
	private MessageDigest md;
	
	public ContentDownloadHandler(Content content) throws DownloadException {
		this.content = content;
		
		try {
			md = MessageDigest.getInstance("MD5");
			OutputStream os = Files.newOutputStream(Paths.get(this.content.getPath()));
			writer = new DigestOutputStream(os, md);
		} catch (IOException e) {
			throw new DownloadException("Error creating SeekableByteChannel for path "+content.getPath(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new DownloadException("Error: Algorithm for MD5 digest not found "+content.getPath(), e);
		}
	}
	
	public void startDownload(OverlayManager om) throws DownloadException{
		if(started.getAndSet(true)){
			throw new DownloadException("Download already started.");
		}
		this.om = om;
		this.providers = this.om.getCloseProviders(content.getContentID());
		sendRequest();
	}
	
	public void receiveContentInfo(Content content) throws DownloadException{
		if(content == null){
			log.warn("{} - Prvovider {} is not a provider, trying next provider.", om.getuNaDaInfo().getUnadaID(), providers.iterator().next().getUnadaID());
			providers.remove(0);
			sendRequest();			
		}
		this.content.setSize(content.getSize());
		
	}
	
	private void sendRequest() throws DownloadException{
		DownloadRequestMessage msg = new DownloadRequestMessage();
		msg.setContentID(content.getContentID());
		for(UnadaInfo dst : providers ){
			if(om.sendMessage(dst , msg)){
				return;
			}else{
				providers.remove(dst);
			}
		}
		throw new DownloadException("No providers reachable or none responding.");
	}
	
	public synchronized void receiveChunk(byte[] data, int chunkNo) {
		log.debug("Received chunk {} of file {}", chunkNo, content.getPath());
		Chunk c = new Chunk(chunkNo, data);
		if (lastChunk + 1 == chunkNo) {
			write(c);
			
		} else {
			chunkBacklog.add(c);
		}
		while(!chunkBacklog.isEmpty() && lastChunk + 1 == chunkBacklog.peek().getChunkNo()){
			write(chunkBacklog.remove());
		}
	}
	
	public void receiveDownloadComplete(byte[] checksum){
		log.debug("Download completed message received, writing remaining chunks.");
		while(!chunkBacklog.isEmpty()){
			Chunk c = chunkBacklog.remove();
			write(c);
		}
		byte[] localdigest =  md.digest();
		log.debug("Received digest: {} \n\t local digest: {}", checksum, localdigest);
		
		if(!Arrays.equals(localdigest, checksum)){
			log.warn("File hash did not match the received hash.");
			success.set(false);
		}else{
			success.set(true);
		}
		
		try {
			writer.close();
		} catch (IOException e) {
			log.error("Error while closing byte channel to file.", e);
		}
		
		log.debug("{} - Download complete. Reported size: {}, bytes written {}.", om.getuNaDaInfo().getUnadaID(), content.getSize(), bytesDownloaded);
		completed.set(true);
		log.debug("{} - Notifying waiting threads.", om.getuNaDaInfo().getUnadaID());
			barrier.countDown();
		log.debug("{} - Notified.", om.getuNaDaInfo().getUnadaID());
	}
	
	private synchronized void write(Chunk c){
		try {
			writer.write(c.getBuf());
			bytesDownloaded += c.getBuf().length;
			log.debug("Chunk {} written to file {}.\n Digest: {}", c.getChunkNo(), content.getPath(), MessageDigest.getInstance("MD5").digest(c.getBuf()));
			lastChunk = c.getChunkNo();
		} catch (IOException e) {
			log.error("Can not write to file {}", content.getPath(), e);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
			
	@Override
	public boolean isSuccess() {
		return success.get();
	}

	@Override
	public boolean isDone() {
		return completed.get();
	}

	@Override
	public  Content get() {
		log.debug("{} - Waiting for download to complete.", om.getuNaDaInfo().getUnadaID());
		while(!completed.get()){
			 try {
		            barrier.await();
		            log.debug("{} - Woken up.", om.getuNaDaInfo().getUnadaID());
		        } catch (InterruptedException e) {
		        	log.info("{} - Got interrupted before completion.", om.getuNaDaInfo().getUnadaID());}
		}
		return content;
	}

	@Override
	public Content get(long timeout, TimeUnit unit) throws TimeoutException{
		while(!completed.get()){
			 try {
		            if(!barrier.await(timeout, unit)){
		            	throw new TimeoutException();
		            }
		        } catch (InterruptedException e) {}
		}
		return content;
	}
}
