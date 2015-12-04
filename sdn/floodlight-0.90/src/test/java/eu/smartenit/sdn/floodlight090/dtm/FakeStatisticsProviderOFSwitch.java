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
package eu.smartenit.sdn.floodlight090.dtm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;

import org.jboss.netty.channel.Channel;
import org.openflow.protocol.OFFeaturesReply;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPhysicalPort;
import org.openflow.protocol.OFStatisticsRequest;
import org.openflow.protocol.statistics.OFDescriptionStatistics;
import org.openflow.protocol.statistics.OFPortStatisticsReply;
import org.openflow.protocol.statistics.OFPortStatisticsRequest;
import org.openflow.protocol.statistics.OFStatistics;
import org.openflow.protocol.statistics.OFStatisticsType;

public final class FakeStatisticsProviderOFSwitch implements IOFSwitch {

	private final Map<Short, Long> traffic = new HashMap<>();

	public synchronized void setTrafficForPort(short port, long transmittedBytes) {
		traffic.put(port, transmittedBytes);
	}

	@Override
	public synchronized Future<List<OFStatistics>> getStatistics(OFStatisticsRequest request) throws IOException {
		if (request == null) {
			throw new UnsupportedOperationException("Not supported.");
		}
		if (request.getStatisticType() == null
				|| !request.getStatisticType().equals(OFStatisticsType.PORT)) {
			throw new UnsupportedOperationException("Not supported.");
		}
		if (request.getStatistics() == null
				|| request.getStatistics().size() != 1) {
			throw new UnsupportedOperationException("Not supported.");
		}
		if (!request.getStatistics().get(0).getClass()
				.equals(OFPortStatisticsRequest.class)) {
			throw new UnsupportedOperationException("Not supported.");
		}
		final short port = ((OFPortStatisticsRequest) request.getStatistics()
				.get(0)).getPortNumber();
		return new Future<List<OFStatistics>>() {

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				return false;
			}

			@Override
			public boolean isCancelled() {
				return false;
			}

			@Override
			public boolean isDone() {
				return true;
			}

			@Override
			public List<OFStatistics> get() throws InterruptedException, ExecutionException {
				OFPortStatisticsReply reply = new OFPortStatisticsReply();
				reply.setTransmitBytes(traffic.get(port));
				List<OFStatistics> result = new ArrayList<>();
				result.add(reply);
				return result;
			}

			@Override
			public List<OFStatistics> get(long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException,
					TimeoutException {
				return get();
			}
		};
	}

	// <editor-fold defaultstate="collapsed" desc=" Not implemented methods ">
	@Override
	public void write(OFMessage m, FloodlightContext bc) throws IOException {
		//throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void write(List<OFMessage> msglist, FloodlightContext bc)
			throws IOException {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void disconnectOutputStream() {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public Channel getChannel() {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public int getBuffers() {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public int getActions() {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public int getCapabilities() {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public byte getTables() {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void setFeaturesReply(OFFeaturesReply featuresReply) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void setSwitchProperties(OFDescriptionStatistics description) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public Collection<OFPhysicalPort> getEnabledPorts() {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public Collection<Short> getEnabledPortNumbers() {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public OFPhysicalPort getPort(short portNumber) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public OFPhysicalPort getPort(String portName) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void setPort(OFPhysicalPort port) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void deletePort(short portNumber) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void deletePort(String portName) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public Collection<OFPhysicalPort> getPorts() {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public boolean portEnabled(short portName) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public boolean portEnabled(String portName) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public boolean portEnabled(OFPhysicalPort port) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public long getId() {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public String getStringId() {
		//throw new UnsupportedOperationException("Not supported.");
            return "";
	}

	@Override
	public Map<Object, Object> getAttributes() {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public Date getConnectedSince() {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public int getNextTransactionId() {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public Future<OFFeaturesReply> getFeaturesReplyFromSwitch()
			throws IOException {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void deliverOFFeaturesReply(OFMessage reply) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void cancelFeaturesReply(int transactionId) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public boolean isConnected() {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void setConnected(boolean connected) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public IFloodlightProviderService.Role getRole() {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public boolean isActive() {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void deliverStatisticsReply(OFMessage reply) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void cancelStatisticsReply(int transactionId) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void cancelAllStatisticsReplies() {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public boolean hasAttribute(String name) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public Object getAttribute(String name) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void setAttribute(String name, Object value) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public Object removeAttribute(String name) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void clearAllFlowMods() {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public boolean updateBroadcastCache(Long entry, Short port) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public Map<Short, Long> getPortBroadcastHits() {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void sendStatsQuery(OFStatisticsRequest request, int xid,
			IOFMessageListener caller) throws IOException {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void flush() {
//		throw new UnsupportedOperationException("Not supported.");
	}
	// </editor-fold>
}