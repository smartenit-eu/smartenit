package eu.smartenit.enduser.app.ctm;

import org.junit.Test;

import eu.smartenit.enduser.app.ctm.UNaDaConfiguration;
import eu.smartenit.enduser.app.ctm.UNaDaWifiConfiguration;
import android.location.Location;
import android.test.AndroidTestCase;

public class UNaDaConfigurationTest extends AndroidTestCase {

	UNaDaConfiguration conf;

	public void setUp() {
		conf = getMockUnadaConfiguration();
	}

	@Test
	public void testSetUp() {
		assertNotNull("UNaDa configuration not initialized", conf);
	}

	@Test
	public void testGetIpAddress() {
		assertEquals("Wrong IP returned", "10.10.10.10", conf.getIpAddress());
	}

	@Test
	public void testGetMacAddress() {
		assertEquals("Wrong MAC returned", "11:22:33:44:55:66:77:88",
				conf.getMacAddress());
	}

	@Test
	public void testGetPort() {
		assertEquals("Wrong port returned", 3, conf.getPort());
	}

	@Test
	public void testGetLocation() {
		Location testLoc = new Location("mock provider");
		testLoc.setLatitude(15.6);
		testLoc.setLongitude(16.7);
		assertTrue("Wrong location stored",
				testLoc.distanceTo(conf.getLocation()) < 100);
	}

	@Test
	public void testGetPrivateWifi() {
		UNaDaWifiConfiguration privateConf = UNaDaWifiConfigurationTest
				.getMockPrivateWifi();
		assertEquals("Wrong Wifi SSID returned", privateConf.getSSID(), conf
				.getPrivateWifi().getSSID());
		assertEquals("Wrong Wifi password returned", privateConf.getPassword(),
				conf.getPrivateWifi().getPassword());
	}

	@Test
	public void testGetOpenWifi() {
		UNaDaWifiConfiguration openConf = UNaDaWifiConfigurationTest
				.getMockPublicWifi();
		assertEquals("Wrong Wifi SSID returned", openConf.getSSID(), conf
				.getOpenWifi().getSSID());
		assertEquals("Wrong Wifi password returned", openConf.getPassword(),
				conf.getOpenWifi().getPassword());
	}

	@Test
	public void testGetSocialPredictionInterval() {
		assertEquals("Wrong social prediction interval", 13,
				conf.getSocialPredictionInterval());
	}

	@Test
	public void testGetOverlayPredictionInterval() {
		assertEquals("Wrong overlay prediction interval", 67,
				conf.getOverlayPredictionInterval());
	}

	// MOCK DATA GENERATION //

	public static UNaDaConfiguration getMockUnadaConfiguration() {

		String gw = "10.10.10.10";
		String mac = "11:22:33:44:55:66:77:88";
		int socialPredictionInterval = 13;
		int overlayPredictionInterval = 67;
		return new UNaDaConfiguration(gw, mac, 3, getMockLocation(),
				UNaDaWifiConfigurationTest.getMockPrivateWifi(),
				UNaDaWifiConfigurationTest.getMockPublicWifi(),
				socialPredictionInterval, overlayPredictionInterval);
	}

	public static Location getMockLocation() {
		Location loc = new Location("mock provider");
		loc.setLatitude(15.6);
		loc.setLongitude(16.7);
		return loc;
	}

}
