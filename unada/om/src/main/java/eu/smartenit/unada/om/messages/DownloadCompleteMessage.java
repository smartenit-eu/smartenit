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

import eu.smartenit.unada.om.OverlayManager;

public class DownloadCompleteMessage extends BaseMessage{

	private static final long serialVersionUID = -2218735876786997196L;
	private long ContentID;
	private byte[] md5Digest;
	
	

	@Override
	public void execute(OverlayManager om) {
		om.getDownloadHandlers().get(getContentID()).receiveDownloadComplete(getMd5Digest());
	}

	public long getContentID() {
		return ContentID;
	}

	public DownloadCompleteMessage setContentID(long contentID) {
		ContentID = contentID;
		return this;
	}
	
	public byte[] getMd5Digest() {
		return md5Digest;
	}

	public DownloadCompleteMessage setMd5Digest(byte[] md5Digest) {
		this.md5Digest = md5Digest;
		return this;
	}
	

}
