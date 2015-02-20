package eu.smartenit.enduser.app.ctm;


import org.junit.BeforeClass;
import org.junit.Test;

import eu.smartenit.enduser.app.ctm.UNaDaWifiConfiguration;
import android.test.AndroidTestCase;

public class UNaDaWifiConfigurationTest extends AndroidTestCase {
	
	UNaDaWifiConfiguration config;

	@BeforeClass
	public void setUp() {

		config = getMockPrivateWifi();
	}

	@Test
	public void testWifiConfiguration() {
		assertNotNull("Initialization of WiFi configuration failed", config);
	}

	@Test
	public void testGetSSID() {
		assertTrue("Wrong SSID returned", config.getSSID().equals("privateSsid"));
	}

	@Test
	public void testGetPassword() {
		assertTrue("Wrong password returned", config.getPassword().equals("secure!!!"));
	}
	
	public static UNaDaWifiConfiguration getMockPrivateWifi () {
		String privateSsid = "privateSsid";
		String privatePw = "secure!!!";
		return new UNaDaWifiConfiguration(privateSsid, privatePw);
	}
	
	public static UNaDaWifiConfiguration getMockPublicWifi() {
		String privateSsid = "HORST";
		String privatePw = "";
		return new UNaDaWifiConfiguration(privateSsid, privatePw);
	}
	
}
