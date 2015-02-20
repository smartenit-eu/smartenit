
package eu.smartenit.enduser.app.wifiService;

import java.util.List;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;

/**
 * Local database for storing credentials of known UNaDa
 * @author haltug
 *
 */
public class WifiLocalDatabase {
	private List<ScanResult> mResults;
	private List<WifiConfiguration> mConnections;
	
	public WifiLocalDatabase(List<WifiConfiguration> list,List<ScanResult> results){
		mConnections = list;	
		mResults = results;
	}
	
	public void setNetworks(List<WifiConfiguration> list){
		mConnections = list;	
	}
	
	public void setNetworksResults(List<ScanResult> list){
		mResults = list;	
	}
	
	public boolean networkAvailable(String ssid){
		 for (ScanResult i : mResults) {
		        if (i.SSID != null && i.SSID.equals(ssid)) {
		            return true;
		        }
		    }
		return false;
	}
	
	public List<WifiConfiguration> getConnections(){
		return mConnections;
	}
	
	public List<ScanResult> getAvailableNetwotrks(){
		return mResults;
	} 
}
