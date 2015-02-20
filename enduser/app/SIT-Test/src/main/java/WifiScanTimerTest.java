package eu.smartenit.enduser.app.timers;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import eu.smartenit.enduser.app.wifiService.MockWifiScanner;
import android.content.Intent;
import android.os.IBinder;
import android.test.ServiceTestCase;
import android.test.mock.MockApplication;

public class WifiScanTimerTest extends ServiceTestCase<WifiScanTimer> {
	private IBinder service;
	private Intent serviceInent;

	public WifiScanTimerTest() {
		super(WifiScanTimer.class);
	}

	public WifiScanTimerTest(Class<WifiScanTimer> serviceClass) {
		super(serviceClass);
	}

	@Before
	public void setUp() throws Exception {
		serviceInent = new Intent();
		serviceInent.setClass(getContext(), WifiScanTimer.class);
		serviceInent.putExtra("IWifiScanner",
				"eu.smartenit.enduser.app.wifiService.MockWifiScanner");
		serviceInent.putExtra("IWifiSwitcher",
				"eu.smartenit.enduser.app.wifiService.MockWifiSwitcher");
		service = bindService(serviceInent);
		this.getService().setLoggedToFacebook(true, "123");
	}
	

	@Test
	public void test() {
		try {
//			Thread.sleep(2000);//Initial setup
			serviceInent.setAction("android.net.wifi.SCAN_RESULTS");
//			Thread.sleep(20000);//Search and wait to connect to HORST
//			MockWifiScanner.setIsConnectedTo(true);
//			Thread.sleep(80000);
		} catch (Exception ex) {

		}
		// fail("Not yet implemented");
	}
}
