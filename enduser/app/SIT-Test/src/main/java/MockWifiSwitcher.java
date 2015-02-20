package eu.smartenit.enduser.app.wifiService;

import android.net.wifi.WifiManager;
import android.util.Log;

public class MockWifiSwitcher implements IWifiSwitcher {

	@Override
	public void setWifiManager(WifiManager m) {
	}

	@Override
	public void connectToWifi(String ssid) {
		Log.d("WifiSwitcher", "conneting to: " + ssid);
	}

	@Override
	public void connectToWifi(String ssid, String key) {
		Log.d("WifiSwitcher", "conneting to: " + ssid + ", Key:" + key);

	}

}
