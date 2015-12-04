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
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * The UnadaInfo class.
 * 
 * @authors George Petropoulos
 * @version 2.0
 * 
 */
public class UnadaInfo implements Serializable, Comparable<UnadaInfo>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3212752756081453932L;

	public UnadaInfo() {
		
	}
	
	private String type;
    private String unadaAddress;
    
    private int tcpPort;
    private int udpPort;
    
    private double longitude;
    private double latitude;
    private String unadaID;
    private int hopCount = Integer.MAX_VALUE;
    private long timestamp = System.currentTimeMillis();
    
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUnadaAddress() {
		return unadaAddress;
	}
	public InetAddress getInetAddress(){
		try {
			return InetAddress.getByName(unadaAddress);
		} catch (UnknownHostException e) {
			return null;
		}
	}
	public UnadaInfo setUnadaAddress(String unadaAddress) {
		this.unadaAddress = unadaAddress;
		return this;
	}
	
	public UnadaInfo setUnadaAddress(InetAddress unadaAddress) {
		this.unadaAddress = unadaAddress.getHostAddress();
		return this;
	}

	public void setUnadaPorts(int unadaPort) {
		this.tcpPort = unadaPort;
		this.udpPort = unadaPort;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public String getUnadaID() {
		return unadaID;
	}
	public void setUnadaID(String unadaID) {
		this.unadaID = unadaID;
	}
	public Integer getHopCount() {
		return hopCount;
	}
	public void setHopCount(int hopCount) {
		this.hopCount = hopCount;
	}

	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int hashCode() {
		// use the same fields as in equals()
		int hash = 7;
		hash = 73 * hash + (this.unadaAddress != null ? this.unadaAddress.hashCode() : 0);
//		hash = 73 * hash + this.tcpPort;
//		hash = 73 * hash + this.udpPort;
		hash = 73 * hash + (this.unadaID != null ? this.unadaID.hashCode() : 0);
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof UnadaInfo){
			UnadaInfo info = (UnadaInfo) obj;
			return unadaID.equals(info.getUnadaID())
					&& unadaAddress.equals(info.getUnadaAddress());
//					&& tcpPort == info.getTcpPort()
//					&& udpPort == info.getUdpPort();
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		return "UnadaInfo [type=" + type + ", unadaAddress=" + unadaAddress
				+ ", tcpPort=" + tcpPort + ", udpPort=" + udpPort + ", longitude=" + longitude
				+ ", latitude=" + latitude + ", unadaID=" + unadaID + ", hopCount=" + hopCount + "]";
	}
	
	@Override
	public int compareTo(UnadaInfo o) {
		int comp = this.getHopCount().compareTo(o.getHopCount());
		if(comp == 0){
			if(this.equals(o)){
				return 0;
			}else{
				return -1;
			}
		}
		
		return comp;
	}
	public int getTcpPort() {
		return tcpPort;
	}
	public void setTcpPort(int tcpPort) {
		this.tcpPort = tcpPort;
	}
	public int getUdpPort() {
		return udpPort;
	}
	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

}
