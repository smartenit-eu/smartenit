package eu.smartenit.enduser.app.wifiService;

import java.util.List;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public interface IWifiScanner {
	void setWifiManager(WifiManager m);

	void startScan();

	void stopScan();

	List<ScanResult> getNetworks();

	List<ScanResult> getResults();

	boolean isRunning();

	void printNetworks();

	boolean isConnectedTo(String ssid);
}
