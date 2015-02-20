package eu.smartenit.enduser.app.wifiService;

import java.util.List;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import eu.smartenit.enduser.app.ctm.Log;

public class WifiScanner implements IWifiScanner {
	private WifiManager mWifiManager;
	private List<ScanResult> scanResults;
	private boolean isRunning = false;

	public WifiScanner() {
	}

	public WifiScanner(WifiManager wm) {
		mWifiManager = wm;
	}

	public void setWifiManager(WifiManager wm) {
		mWifiManager = wm;
	}

	/**
	 * starts the scan
	 */
	public void startScan() {
		Log.s("", "Start scannig for SSID");
		mWifiManager.startScan();
		isRunning = true;
	}

	/**
	 * stops the scan
	 */
	public void stopScan() {
		isRunning = false;
		printNetworks();
		Log.s("", "Wifi scanning stopped.");
	}

	/**
	 * @return list of scanned wifinetworks
	 */
	public List<ScanResult> getNetworks() {
		return scanResults;
	}

	/**
	 * stores the scan results
	 * 
	 * @return
	 */
	public List<ScanResult> getResults() {
		// TODO: Store data
		scanResults = mWifiManager.getScanResults();
		return scanResults;
	}

	/**
	 * @return true if scantimer is running
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * prints the scan results in the LogCat
	 */
	public void printNetworks() {
		int n = 0;
		StringBuilder strb = new StringBuilder();
		for (ScanResult i : scanResults) {
			strb.append(n);
			strb.append(" :");
			strb.append(i.SSID);
			strb.append("|| ");
			n++;
		}
		Log.d("WifiScan", "scaned networks:" + strb);
		Log.s("", scanResults.size() + " network(s) found.");
	}

	public boolean isConnectedTo(String ssid) {
		ssid = "\"" + ssid + "\"";
		if (mWifiManager.getConnectionInfo() != null) {
			return mWifiManager.getConnectionInfo().getSSID().equals(ssid);
		} else {
			return false;
		}
	}
}
