package eu.smartenit.enduser.app.ctm;

import android.location.Location;

public class UNaDaConfiguration {
	private String ipAddress;
	private String macAddress;
	private int port;
	private Location location;
	private UNaDaWifiConfiguration privateWifi;
	private UNaDaWifiConfiguration openWifi;
	private long socialPredictionInterval;
	private long overlayPredictionInterval;
	private boolean isTrusted;

	public String getIpAddress() {
		return ipAddress;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public int getPort() {
		return port;
	}

	public Location getLocation() {
		return location;
	}

	public UNaDaWifiConfiguration getPrivateWifi() {
		return privateWifi;
	}

	public UNaDaWifiConfiguration getOpenWifi() {
		return openWifi;
	}

	public long getSocialPredictionInterval() {
		return socialPredictionInterval;
	}

	public long getOverlayPredictionInterval() {
		return overlayPredictionInterval;
	}

	public UNaDaConfiguration(String ipAddress, String macAddress, int port,
			Location location, UNaDaWifiConfiguration privateWifi,
			UNaDaWifiConfiguration openWifi, long socialPredictionInterval,
			long overlayPredictionInterval) {
		this.ipAddress = ipAddress;
		this.macAddress = macAddress;
		this.port = port;
		this.location = location;
		this.privateWifi = privateWifi;
		this.openWifi = openWifi;
		this.socialPredictionInterval = socialPredictionInterval;
		this.overlayPredictionInterval = overlayPredictionInterval;
	}

}
