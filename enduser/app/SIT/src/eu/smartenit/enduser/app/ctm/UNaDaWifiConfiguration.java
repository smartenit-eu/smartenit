package eu.smartenit.enduser.app.ctm;

public class UNaDaWifiConfiguration{
	private String SSID;
	private String password;

	public UNaDaWifiConfiguration(String SSID, String password) {
		this.SSID = SSID;
		this.password = password;
	}

	public String getSSID() {
		return SSID;
	}

	public String getPassword() {
		return password;
	}

}
