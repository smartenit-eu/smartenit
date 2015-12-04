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
 * The UNaDaConfiguration class.
 * 
 * @authors George Petropoulos
 * @version 2.1
 * 
 */
public class UNaDaConfiguration implements Serializable{

	private static final long serialVersionUID = 8421375010731810581L;
	
	public UNaDaConfiguration() {
		this.location = new Location();
		this.privateWifi = new WifiConfiguration();
		this.openWifi = new WifiConfiguration();
        this.socialPredictionEnabled = true;
        this.overlayPredictionEnabled = true;
        this.bootstrapNode = true;
        this.chunkSize = 102400;
	}
	
	private String ipAddress;
	private String macAddress;
	private int port;
	private Location location;
	private WifiConfiguration privateWifi;
	private WifiConfiguration openWifi;
	private long overlayInterval;
	private long socialInterval;
	private long predictionInterval;
	private boolean bootstrapNode;
    private boolean overlayPredictionEnabled;
    private boolean socialPredictionEnabled;
    private long chunkSize;
	
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public WifiConfiguration getPrivateWifi() {
		return privateWifi;
	}
	public void setPrivateWifi(WifiConfiguration privateWifi) {
		this.privateWifi = privateWifi;
	}
	public WifiConfiguration getOpenWifi() {
		return openWifi;
	}
	public void setOpenWifi(WifiConfiguration openWifi) {
		this.openWifi = openWifi;
	}
	public long getOverlayInterval() {
		return overlayInterval;
	}
	public void setOverlayInterval(long overlayInterval) {
		this.overlayInterval = overlayInterval;
	}
	public long getSocialInterval() {
		return socialInterval;
	}
	public void setSocialInterval(long socialInterval) {
		this.socialInterval = socialInterval;
	}
	public long getPredictionInterval() {
		return predictionInterval;
	}
	public void setPredictionInterval(long predictionInterval) {
		this.predictionInterval = predictionInterval;
	}
	public boolean isBootstrapNode() {
		return bootstrapNode;
	}
	public void setBootstrapNode(boolean bootstrapNode) {
		this.bootstrapNode = bootstrapNode;
	}

    public boolean isOverlayPredictionEnabled() {
        return overlayPredictionEnabled;
    }

    public void setOverlayPredictionEnabled(boolean overlayPredictionEnabled) {
        this.overlayPredictionEnabled = overlayPredictionEnabled;
    }

    public boolean isSocialPredictionEnabled() {
        return socialPredictionEnabled;
    }

    public void setSocialPredictionEnabled(boolean socialPredictionEnabled) {
        this.socialPredictionEnabled = socialPredictionEnabled;
    }

    public long getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(long chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Override
    public String toString() {
        return "UNaDaConfiguration{" +
                "ipAddress='" + ipAddress + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", port=" + port +
                ", location=" + location +
                ", privateWifi=" + privateWifi +
                ", openWifi=" + openWifi +
                ", overlayInterval=" + overlayInterval +
                ", socialInterval=" + socialInterval +
                ", predictionInterval=" + predictionInterval +
                ", bootstrapNode=" + bootstrapNode +
                ", overlayPredictionEnabled=" + overlayPredictionEnabled +
                ", socialPredictionEnabled=" + socialPredictionEnabled +
                ", chunkSize=" + chunkSize +
                '}';
    }
}
