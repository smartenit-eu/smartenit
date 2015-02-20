package eu.smartenit.enduser.app.timers;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import eu.smartenit.enduser.app.ctm.Log;
import eu.smartenit.enduser.app.energyreport.ReportStats;

/**
 * Background timer for periodically energy updates. Runs as service.
 * 
 * @author Robert Reinecke
 * 
 */
public class EnergyTimer extends Service {
	private final IBinder mBinder = new EnergyTimerBinder();
	private Timer periodicReport;
	private long reportInterval = 1000;
	private static boolean isRunning;
	private ReportStats task;

	private BroadcastReceiver mWifiChangeReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			NetworkInfo info = intent
					.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			if (info != null) {
				if (info.isConnected()) {
					ReportStats.setRegisterRequired(true);
				}
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("EnergyService", "started");
		startRunning();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		registerService();

		if (!isRunning) {
			startRunning();
		}
		return mBinder;
	}

	private void registerService() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		registerReceiver(mWifiChangeReceiver, intentFilter);
	}

	public class EnergyTimerBinder extends Binder {
		public EnergyTimer getService() {
			// Return this instance of WifiScanTimer so clients can
			// call public methods
			return EnergyTimer.this;
		}
	}

	@Override
	public void onDestroy() {
		stopRunning();
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(this.getClass().getName(), "UNBIND");
		return true;
	}

	public void startRunning() {
		Log.d("periodicEnergyData", "started");

		registerService();

		periodicReport = new Timer();
		TimerTask periodicScan = new TimerTask() {

			@Override
			public void run() {

				sendData();
			}
		};
		periodicReport.scheduleAtFixedRate(periodicScan, reportInterval,
				reportInterval);
		isRunning = true;
	}

	public void stopRunning() {
		periodicReport.cancel();
		isRunning = false;
		Log.d("periodicEnergyData", "stoped");
	}

	public boolean isRunning() {
		return isRunning;
	}

	protected void sendData() {
		String android_id = android.provider.Settings.Secure.getString(
				getBaseContext().getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);
		boolean f = false;
		task = new ReportStats(android_id);
		task.execute();
	}
}
