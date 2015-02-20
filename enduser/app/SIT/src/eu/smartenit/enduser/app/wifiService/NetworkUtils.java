package eu.smartenit.enduser.app.wifiService;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

public class NetworkUtils {

	public static String getDefaultGateway(Context context) {
		try {
			WifiManager networkd = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			DhcpInfo details = networkd.getDhcpInfo();

			return intToIp(details.gateway);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	public static String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + ((i >> 24) & 0xFF);
	}

}