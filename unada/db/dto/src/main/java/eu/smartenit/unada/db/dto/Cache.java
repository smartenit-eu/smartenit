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
package eu.smartenit.unada.db.dto;

import java.io.Serializable;

/**
 * The Cache class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public class Cache implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8188712472628923396L;

	public Cache() {

	}

	private long sizeThreshold;
	private long size;
	private long timeToLive;
	private double socialThreshold;
	private double overlayThreshold;

	public long getSizeThreshold() {
		return sizeThreshold;
	}

	public void setSizeThreshold(long sizeThreshold) {
		this.sizeThreshold = sizeThreshold;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(long timeToLive) {
		this.timeToLive = timeToLive;
	}

	public double getSocialThreshold() {
		return socialThreshold;
	}

	public void setSocialThreshold(double socialThreshold) {
		this.socialThreshold = socialThreshold;
	}

	public double getOverlayThreshold() {
		return overlayThreshold;
	}

	public void setOverlayThreshold(double overlayThreshold) {
		this.overlayThreshold = overlayThreshold;
	}

	@Override
	public String toString() {
		return "Cache [sizeThreshold=" + sizeThreshold + ", size=" + size
				+ ", timeToLive=" + timeToLive + ", socialThreshold="
				+ socialThreshold + ", overlayThreshold=" + overlayThreshold
				+ "]";
	}

}
