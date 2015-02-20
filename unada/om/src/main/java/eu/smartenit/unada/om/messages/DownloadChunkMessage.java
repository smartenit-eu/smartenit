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
package eu.smartenit.unada.om.messages;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.smartenit.unada.om.ContentDownloadHandler;
import eu.smartenit.unada.om.OverlayManager;

public class DownloadChunkMessage extends BaseMessage {

	private static final long serialVersionUID = -2056453621381272200L;
	private static Logger log = LoggerFactory.getLogger(DownloadChunkMessage.class);
	private long contentID;
	private int chunkNo;
	
	private byte[] data;
	
	@Override
	public void execute(OverlayManager om) {
		log.debug("{} - Received Chunk message, chunk no.: {}", om.getuNaDaInfo().getUnadaID(), chunkNo);
		try {
			log.debug("Chunk {} digest: {} ", chunkNo, MessageDigest.getInstance("MD5").digest(data));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ContentDownloadHandler handler = om.getDownloadHandlers().get(contentID);
		if(handler == null){
			//TODO error!!!! cancel the download
			return;
		}
		handler.receiveChunk(getData(), chunkNo);
	}

	public long getContentID() {
		return contentID;
	}

	public DownloadChunkMessage setContentID(long contentID) {
		this.contentID = contentID;
		return this;
	}

	public int getChunkNo() {
		return chunkNo;
	}

	public DownloadChunkMessage setChunkNo(int chunkNo) {
		this.chunkNo = chunkNo;
		return this;
	}

	public byte[] getData() {
		return data;
	}

	public DownloadChunkMessage setData(byte[] data) {
		this.data = data;
		return this;
	}

}
