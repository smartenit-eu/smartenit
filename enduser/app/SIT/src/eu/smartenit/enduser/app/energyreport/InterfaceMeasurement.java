package eu.smartenit.enduser.app.energyreport;

public class InterfaceMeasurement {
	private String mac;

	private long packetsrx;

	private long packetstx;

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public long getPacketsrx() {
		return packetsrx;
	}

	public void setPacketsrx(long packetsrx) {
		this.packetsrx = packetsrx;
	}

	public long getPacketstx() {
		return packetstx;
	}

	public void setPacketstx(long packetstx) {
		this.packetstx = packetstx;
	}
}