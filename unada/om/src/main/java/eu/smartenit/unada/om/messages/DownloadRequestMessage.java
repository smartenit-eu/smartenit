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
package eu.smartenit.unada.om.messages;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.commons.constants.UnadaConstants;
import eu.smartenit.unada.commons.logging.UnadaLogger;
import eu.smartenit.unada.db.dao.util.DAOFactory;
import eu.smartenit.unada.db.dto.Content;
import eu.smartenit.unada.om.OverlayManager;

public class DownloadRequestMessage extends BaseMessage {

	private static final long serialVersionUID = 4484799080704999650L;
	private static Logger log = LoggerFactory.getLogger(DownloadRequestMessage.class);

	private long contentID;
	private int bufferCapacity = (int) DAOFactory.getuNaDaConfigurationDAO().findLast().getChunkSize();
	private int retries = 3;
	
	
	@Override
	public void execute(OverlayManager om) {
		long startTime = System.currentTimeMillis();
		
		MessageDigest md;
		log.info("Serving video {} to overlay neighbor.", contentID);
		Content content = DAOFactory.getContentDAO().findById(contentID);
		DownloadReplyMessage reply = new DownloadReplyMessage();
		try{
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			log.error("No MD5 algorithm for message digest found.");
			om.sendMessage(this.getSender(), reply);
			return;
		}

		reply.setContent(content);
		if(!om.sendMessage(this.getSender(), reply)){
			log.error("Requesting UNaDa not reachable");
			return;
		}
		Path contentPath = Paths.get(content.getPath());

		try(
				InputStream is = Files.newInputStream(contentPath, StandardOpenOption.READ);
				DigestInputStream channel = new DigestInputStream(is, md);
				){
			byte[] buffer = new byte[bufferCapacity];
			int chunkNo = 0;
			int read = Integer.MAX_VALUE;
			while ( (read = channel.read(buffer)) > 0){
				DownloadChunkMessage msg = new DownloadChunkMessage();
				msg.setChunkNo(chunkNo);
				msg.setContentID(contentID);
				byte[] data = Arrays.copyOfRange(buffer, 0, read);
				log.debug("Chunk no {} digest: {}", chunkNo, MessageDigest.getInstance("MD5").digest(data));
				msg.setData(data);
				int attempt = 0;
				while(!om.sendMessage(this.getSender(), msg)){
					attempt++;
					try {Thread.sleep(300);} catch (InterruptedException e) {}
					if(attempt >= retries ){
						log.error("Failed to send message {} times, giving up!.");
						return;
					}
				}
				chunkNo++;
			}

			log.info("Serving video {} to overlay neighbor was completed successfully after {} seconds.",
					contentID, (System.currentTimeMillis() - startTime)/1000);
			//Study log #3 Video Serving Events (time, videoID, size, source, download time) 
			UnadaLogger.overall.debug("{}: Video Serving Event ({}, {}, {}, {}, {})", new Object[]{
					UnadaConstants.UNADA_OWNER_MD5, 
					System.currentTimeMillis(), 
					content.getContentID(), 
					content.getSize(), 
					(content.isPrefetchedVimeo() ? "vimeo" : "overlay"), 
					(System.currentTimeMillis() - startTime)});
			
			om.sendMessage(this.getSender(), new DownloadCompleteMessage().setContentID(contentID).setMd5Digest(md.digest()));
		} catch (IOException e) {
			log.error("A problem occurred with reading the file: {}", contentPath, e);
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public long getContentID() {
		return contentID;
	}

	public void setContentID(long contentID) {
		this.contentID = contentID;
	}

}
