package eu.smartenit.enduser.app.wifiService;

import java.util.List;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import eu.smartenit.enduser.app.ctm.Log;

public class WifiSwitcher implements IWifiSwitcher {
	private WifiManager mWifiManager;

	public WifiSwitcher() {
	}

	public WifiSwitcher(WifiManager wm) {
		mWifiManager = wm;
	}

	public void setWifiManager(WifiManager wm) {
		mWifiManager = wm;
	}

	/**
	 * @param connects
	 *            the ssid network
	 */
	public void connectToWifi(String ssid, WifiLocalDatabase wifiDatabase) {

		WifiConfiguration conf = new WifiConfiguration();
		conf.SSID = "\"" + ssid + "\"";
		conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

		mWifiManager.addNetwork(conf);
		Log.d("WifiSwitcher", ssid + " added");

		ScanResult r = null;
		{
			List<ScanResult> list = wifiDatabase.getAvailableNetwotrks();
			for (ScanResult i : list) {
				if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
					if (r == null)
						r = i;
					else if (i.level > r.level)
						r = i;
				}
			}
		}

		if (r != null) {
			List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
			for (WifiConfiguration i : list) {
				if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")
						&& r.BSSID == i.BSSID) {
					mWifiManager.disconnect();
					mWifiManager.enableNetwork(i.networkId, true);
					mWifiManager.reconnect();

					Log.d("WifiSwitcher", "conneting to:" + ssid);
					break;
				}
			}
		}

	}

	/**
	 * @param connects
	 *            to the ssid network with the key
	 */
	@Override
	public void connectToWifi(String ssid, String key) {

		WifiConfiguration conf = new WifiConfiguration();
		conf.SSID = "\"" + ssid + "\"";
		conf.preSharedKey = "\"" + key + "\"";
		// conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

		mWifiManager.addNetwork(conf);
		Log.d("WifiSwitcher", ssid + " added");

		List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
		for (WifiConfiguration i : list) {
			if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
				mWifiManager.disconnect();
				mWifiManager.enableNetwork(i.networkId, true);
				mWifiManager.reconnect();
				Log.d("WifiSwitcher", "conneting to:" + ssid);
				break;
			}
		}
	}
}
