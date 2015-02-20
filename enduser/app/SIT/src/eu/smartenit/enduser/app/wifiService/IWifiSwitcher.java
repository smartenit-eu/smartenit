package eu.smartenit.enduser.app.wifiService;

import android.net.wifi.WifiManager;

public interface IWifiSwitcher {
	void setWifiManager(WifiManager m);

	void connectToWifi(String ssid, WifiLocalDatabase wifiDatabase);

	void connectToWifi(String ssid, String key);
}
