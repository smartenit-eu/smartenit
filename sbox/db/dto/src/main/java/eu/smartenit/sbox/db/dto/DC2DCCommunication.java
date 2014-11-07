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
package eu.smartenit.sbox.db.dto;

import java.io.Serializable;
import java.util.List;

/**
 * The DC2DCCommunication class.
 * 
 * @author George Petropoulos
 * @version 1.0
 * 
 */
public final class DC2DCCommunication implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor.
	 */
	public DC2DCCommunication() {
		this.id = new DC2DCCommunicationID();
		this.remoteCloud = new CloudDC();
		this.localCloud = new CloudDC();
	}

	/**
	 * The constructor with arguments.
	 * 
	 * @param id
	 * @param trafficDirection
	 * @param remoteCloud
	 * @param localCloud
	 * @param qosParameters
	 * @param connectingTunnels
	 */
	public DC2DCCommunication(DC2DCCommunicationID id,
			Direction trafficDirection, CloudDC remoteCloud,
			CloudDC localCloud, QoSParameters qosParameters,
			List<Tunnel> connectingTunnels) {

		this.id = id;
		this.trafficDirection = trafficDirection;
		this.remoteCloud = remoteCloud;
		this.localCloud = localCloud;
		this.qosParameters = qosParameters;
		this.connectingTunnels = connectingTunnels;
	}

	private DC2DCCommunicationID id;

	private Direction trafficDirection;

	private CloudDC remoteCloud;

	private CloudDC localCloud;

	private QoSParameters qosParameters;

	private List<Tunnel> connectingTunnels;

	public DC2DCCommunicationID getId() {
		return id;
	}

	public void setId(DC2DCCommunicationID id) {
		this.id = id;
	}

	public Direction getTrafficDirection() {
		return trafficDirection;
	}

	public void setTrafficDirection(Direction trafficDirection) {
		this.trafficDirection = trafficDirection;
	}

	public CloudDC getRemoteCloud() {
		return remoteCloud;
	}

	public void setRemoteCloud(CloudDC remoteCloud) {
		this.remoteCloud = remoteCloud;
	}

	public CloudDC getLocalCloud() {
		return localCloud;
	}

	public void setLocalCloud(CloudDC localCloud) {
		this.localCloud = localCloud;
	}

	public QoSParameters getQosParameters() {
		return qosParameters;
	}

	public void setQosParameters(QoSParameters qosParameters) {
		this.qosParameters = qosParameters;
	}

	public List<Tunnel> getConnectingTunnels() {
		return connectingTunnels;
	}

	public void setConnectingTunnels(List<Tunnel> connectingTunnels) {
		this.connectingTunnels = connectingTunnels;
	}

	@Override
	public String toString() {
		return "DC2DCCommunication [id=" + id + ", trafficDirection="
				+ trafficDirection + ", remoteCloud=" + remoteCloud
				+ ", localCloud=" + localCloud + ", qosParameters="
				+ qosParameters + ", connectingTunnels=" + connectingTunnels
				+ "]";
	}

}
