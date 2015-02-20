package eu.smartenit.enduser.app.wifiService;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

public class MockWifiScanner implements IWifiScanner {
	private List<ScanResult> scanResults;
	private boolean isRunning = false;
	private static boolean isConneted = false;

	@Override
	public void setWifiManager(WifiManager m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startScan() {
		isRunning = true;
	}

	@Override
	public void stopScan() {
		isRunning = false;
	}

	@Override
	public List<ScanResult> getNetworks() {
		return scanResults;
	}

	@Override
	public List<ScanResult> getResults() {
		scanResults = new ArrayList<ScanResult>();
		scanResults.add(CreateScanResult("HORST"));
		scanResults.add(CreateScanResult("RHB-secured"));
		return scanResults;
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	@Override
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

	}

	private static ScanResult CreateScanResult(String SSID) {
		try {
			Constructor<ScanResult> q = (Constructor<ScanResult>) ScanResult.class
					.getConstructors()[1];
			ScanResult sr = q.newInstance(null, "", "", 0, 0, 0);
			sr.SSID = SSID;

			return sr;
		} catch (Exception e) {
			Log.e("", e.getMessage());
		}
		return null;
	}

	public boolean isConnectedTo(String ssid) {
		return isConneted;
	}

	public static void setIsConnectedTo(boolean val) {
		isConneted = val;
	}

}
