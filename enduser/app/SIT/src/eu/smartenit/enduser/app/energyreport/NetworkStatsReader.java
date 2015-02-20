package eu.smartenit.enduser.app.energyreport;

import android.net.TrafficStats;

public class NetworkStatsReader {

	private static long oldTxBytes = TrafficStats.getTotalTxBytes();
	private static long oldRxBytes = TrafficStats.getTotalRxBytes();

	private static long oldMobileTxBytes = TrafficStats.getMobileTxBytes();
	private static long oldMobileRxBytes = TrafficStats.getMobileRxBytes();

	public static interfaces[] read() {
		interfaces[] result = new interfaces[2];
		// get current values of counters
		long currentMobileTxBytes = TrafficStats.getMobileTxBytes();
		long currentMobileRxBytes = TrafficStats.getMobileRxBytes();
		long totalTxBytes = TrafficStats.getTotalTxBytes() - oldTxBytes;
		long totalRxBytes = TrafficStats.getTotalRxBytes() - oldRxBytes;

		// get WiFi data count, subtract total from mobile
		long currentNetworkSent = totalTxBytes
				- (currentMobileTxBytes - oldMobileTxBytes);
		long currentNetworkReceived = totalRxBytes
				- (currentMobileRxBytes - oldMobileRxBytes);

		result[0] = new interfaces();
		result[0].setPacketsrx(currentMobileRxBytes - oldMobileRxBytes);
		result[0].setPacketstx(currentMobileTxBytes - oldMobileTxBytes);
		result[0].setMac("ee:ee:ee:ee");

		result[1] = new interfaces();
		result[1].setPacketsrx(currentNetworkReceived);
		result[1].setPacketstx(currentNetworkSent);
		result[1].setMac("ff:ff:ff:ff");

		oldTxBytes = TrafficStats.getTotalTxBytes();
		oldRxBytes = TrafficStats.getTotalRxBytes();
		oldMobileTxBytes = currentMobileTxBytes;
		oldMobileRxBytes = currentMobileRxBytes;


		return result;
	}
}
