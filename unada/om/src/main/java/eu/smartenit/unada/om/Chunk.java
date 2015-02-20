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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Chunk implements Comparable<Chunk>{
	
	private static Logger log = LoggerFactory.getLogger(Chunk.class);
	private byte[] data;
	private int chunkNo = 0;
	
	public Chunk(int chunkNo, byte[] buf){
		this.setChunkNo(chunkNo);
		this.setBuf(buf);
		try {
			log.debug("Chunk object created {} digest: {} ", chunkNo, MessageDigest.getInstance("MD5").digest(data));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int compareTo(Chunk o) {
		if(this.getChunkNo() > o.getChunkNo())
			return 1;
		if(this.getChunkNo() > o.getChunkNo())
			return -1;
		return 0;
	}
	
	public int getChunkNo() {
		return chunkNo;
	}

	public void setChunkNo(int chunkNo) {
		this.chunkNo = chunkNo;
	}

	public byte[] getBuf() {
		return data;
	}

	public void setBuf(byte[] buf) {
		this.data = buf;
	}
}
