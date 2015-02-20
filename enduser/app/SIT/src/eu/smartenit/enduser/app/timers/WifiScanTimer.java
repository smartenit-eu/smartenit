package eu.smartenit.enduser.app.timers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import eu.smartenit.enduser.app.ServiceController;
import eu.smartenit.enduser.app.ctm.Log;
import eu.smartenit.enduser.app.facebookAccount.IRemoteLoginReplyCallback;
import eu.smartenit.enduser.app.facebookAccount.LoginManager;
import eu.smartenit.enduser.app.facebookAccount.RemoteLoginReply;
import eu.smartenit.enduser.app.wifiService.WifiLocalDatabase;
import eu.smartenit.enduser.app.wifiService.IWifiScanner;
import eu.smartenit.enduser.app.wifiService.IWifiSwitcher;

/**
 * Background timer for periodically scan operation. Runs as service.
 * 
 * @author haltug
 * 
 */
public class WifiScanTimer extends Service implements IRemoteLoginReplyCallback {
	// private WifiScanTimer mWifiScanTimer;
	private final IBinder mBinder = new WifiScanTimerBinder();
	private Timer periodicWifiScan;
	private static boolean isRunning;
	private boolean wifiScanReceiverIsOn = false;
	private int scanInterval = 6000; // in ms
	private String horstSsid = "HORST";
	private String secureSsid = "RBH-secured";
	private String key = "";
	private static boolean horstFound = false;
	private static boolean loggedToFacebook = false;
	private static String facebookID;
	private static String facebookUsername;
	private static Bitmap facebookProfilePicture;

	private static IWifiScanner mWifiScanner;
	private static IWifiSwitcher mWifiSwitcher;
	private static WifiLocalDatabase mWifiDatabase;
	private static WifiManager mWifiManager;
	private static ConnectivityManager mConnectivityManager;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("WifiScanService", "started");
		// mWifiScanTimer = this;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		try {
			mWifiScanner = (IWifiScanner) Class.forName(
					intent.getExtras().getString("IWifiScanner")).newInstance();
			mWifiSwitcher = (IWifiSwitcher) Class.forName(
					intent.getExtras().getString("IWifiSwitcher"))
					.newInstance();
			mWifiScanner.setWifiManager(mWifiManager);
			mWifiSwitcher.setWifiManager(mWifiManager);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mWifiDatabase = new WifiLocalDatabase(
				mWifiManager.getConfiguredNetworks(), mWifiScanner.getResults());
		registerWifiScanReceiver();

		if (!isRunning) {
			startRunning();
		}

		return mBinder;
	}

	public class WifiScanTimerBinder extends Binder {
		public WifiScanTimer getService() {
			// Return this instance of WifiScanTimer so clients can
			// call public methods
			return WifiScanTimer.this;
		}
	}

	@Override
	public void onDestroy() {
		unregisterWifiScanReceiver();
		// stopRunning();
		super.onDestroy();
	}

	/**
	 * Starts the scantimer and executes scans
	 */
	public void startRunning() {
		Log.d("periodicScan", "started");
		periodicWifiScan = new Timer();
		TimerTask periodicScan = new TimerTask() {

			@Override
			public void run() {
				if (!wifiScanReceiverIsOn) {
					registerWifiScanReceiver();
				}
				runScan();
				// Log.d("periodicScan", "executed");
			}
		};
		periodicWifiScan.scheduleAtFixedRate(periodicScan, scanInterval,
				scanInterval);
		isRunning = true;
	}

	/**
	 * Stops the scantimer
	 */
	public void stopRunning() {
		if (periodicWifiScan != null)
			periodicWifiScan.cancel();
		unregisterWifiScanReceiver();
		isRunning = false;
		Log.d("periodicScan", "stoped");
	}

	/**
	 * 
	 * @return if scantimer is running
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * Executes the scan
	 */
	protected void runScan() {
		if (!isConnectedToRbh() && !isConnectedToHorst()
				&& !mWifiScanner.isRunning() && isLoggedToFacebook()) {
			mWifiScanner.startScan();
		}

		// When connected to HORST, call the API to get secure SSID
		if (isConnectedToHorst() && isLoggedToFacebook()) {
			stopRunning();
			mWifiScanner.stopScan();
			callUNaDa(facebookID, "");
		}

		if (isConnectedToRbh())
			stopRunning();
	}

	private void callUNaDa(String id, String token) {
		try {
			Log.d("WifiScanTimer", "Send login request to UNaDa");
			LoginManager loginManager = new LoginManager(
					getApplicationContext(), Long.parseLong(id), token);
			loginManager.login(123, this);
		} catch (Exception e) {
			Log.e("WifiScanTimer", e.getMessage());
		}
	}

	public void RemoteLoginReplyCallback(RemoteLoginReply remoteLoginReply) {
		// Got the result from UNaDa
		if (remoteLoginReply != null
				&& remoteLoginReply.getPrivateSSID() != null) {
			WifiManager mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

			Log.d("WifiScanTimer", "Connect to private SSID");
			// Connect to private SSID
			mWifiSwitcher.connectToWifi(remoteLoginReply.getPrivateSSID(),
					remoteLoginReply.getPassword());

			stopRunning();
			Log.s("", "Succesfully logged in in UnaDa ...");
			Log.s("", "Connecting to secure network ...");
			// TODO: We are about to connect to RHB Secure, show something on
			// the screen to inform user.
		} else {
			// setLoggedToFacebook(false, null);
			startRunning();
		}
	}

	/**
	 * Initializes the receiver for the WifiScanner
	 */
	private BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			Log.d("WifiScanReceiver", intent.getAction());
			if (intent.getAction().equals(
					WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				mWifiDatabase.setNetworksResults(mWifiScanner.getResults());

				// mWifiScanner.printNetworks();
				if (!isConnectedToRbh()
						&& mWifiDatabase.networkAvailable(horstSsid)
						&& isLoggedToFacebook()) {
					horstFound = true;
					mWifiSwitcher.connectToWifi(horstSsid, mWifiDatabase);
					unregisterWifiScanReceiver();
					// Continue to work since it will take some times to connect
					// to HORST
					// stopRunning();

					// sendBroadcast("HORST_FOUND");
				}

			}

			if (intent.getAction().equals(
					"com.tudarmstadt.rbhost.FacebookLogin.LOGGED_TO_FACEBOOK")) {
				Log.d("BroadcastFbLog", "logged");
				// TODO: Not sure it why is it so.
				// setLoggedToFacebook(true);
			}
		}
	};

	/**
	 * registers the receiver
	 */
	private void registerWifiScanReceiver() {
		IntentFilter i = new IntentFilter();
		i.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		if (true) { // for later settings
			// Log.d("WifiScanReceiver", "registered");
			this.registerReceiver(this.mWifiScanReceiver, i);
			wifiScanReceiverIsOn = true;
		} else {
			unregisterWifiScanReceiver();
		}
	}

	/**
	 * unregisters the receiver
	 */
	private void unregisterWifiScanReceiver() {
		if (wifiScanReceiverIsOn) {
			// Log.d("WifiScanReceiver", "unregistered");
			this.unregisterReceiver(this.mWifiScanReceiver);
			wifiScanReceiverIsOn = false;
		}
	}

	/**
	 * @return returns the WifiLocalDatabase
	 */
	public WifiLocalDatabase getWifiLocalDatabase() {
		return mWifiDatabase;
	}

	public void setRbh(String ssid, String key) {
		this.secureSsid = ssid;
		this.key = key;
	}

	public boolean horstFound() {
		return horstFound;
	}

	public boolean isConnectedToRbh() {
		return mWifiScanner.isConnectedTo(secureSsid);
	}

	public boolean isConnectedToHorst() {
		return mWifiScanner.isConnectedTo(horstSsid);
	}

	public void connectToRbh() {
		mWifiSwitcher.connectToWifi(secureSsid, key);
	}

	public ConnectivityManager getConnectivityManager() {
		return mConnectivityManager;
	}

	private void sendBroadcast(String action) {
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(getClass().getName() + "." + action);
		// Log.d("boradcastAction", broadcastIntent.getAction());
		this.sendBroadcast(broadcastIntent);
	}

	public void setLoggedToFacebook(boolean state, String faceBookID,
			String facebookUsername, final URL profilePicture) {
		loggedToFacebook = state;
		WifiScanTimer.facebookID = faceBookID;
		WifiScanTimer.facebookUsername = facebookUsername;

		if (facebookUsername != null && profilePicture == null) {
			File imgFile = getOutputMediaFile();
			if (imgFile.exists()) {
				Bitmap bitmap = BitmapFactory.decodeFile(imgFile
						.getAbsolutePath());
				facebookProfilePicture = bitmap;
				showFacebookProfilePicture(bitmap);
			}
		}

		if (profilePicture != null)
			new Thread(new Runnable() {
				public void run() {
					Bitmap bitmap = null;
					try {
						InputStream in = (InputStream) profilePicture
								.getContent();
						facebookProfilePicture = BitmapFactory.decodeStream(in);

						storeImage(facebookProfilePicture);

						showFacebookProfilePicture(bitmap);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();

		// https://graph.facebook.com/1374731126152474/picture?type=large
		// facebookProfilePicture = profilePicture;

		if (state) {
			Log.s("Facebook", "Welcome " + facebookUsername + "!");
		} else {
			Log.s("Facebook", "Logged out!");
		}
	}

	private void showFacebookProfilePicture(Bitmap bitmap) {
		Intent intent = new Intent();
		intent.setAction("com.tudarmstadt.rbhost.FacebookStatus");
		intent.putExtra("profilePicture", bitmap);
		getApplicationContext().sendBroadcast(intent);
	}

	public boolean isLoggedToFacebook() {
		return loggedToFacebook;
	}

	public void Start(String facebookID) {

	}

	public static String getFacebookUsername() {
		return facebookUsername;
	}

	public static Bitmap getFacebookProfilePicture() {
		return facebookProfilePicture;
	}

	private void storeImage(Bitmap image) {
		File pictureFile = getOutputMediaFile();
		if (pictureFile == null) {
			Log.d("storeImage",
					"Error creating media file, check storage permissions: ");// e.getMessage());
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			image.compress(Bitmap.CompressFormat.PNG, 90, fos);
			fos.close();
		} catch (Exception e) {
			Log.d("storeImage", "Error accessing file: " + e.getMessage());
		}
	}

	private File getOutputMediaFile() {
		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory() + "/Android/data/"
						+ getApplicationContext().getPackageName() + "/Files");
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}
		File mediaFile;
		String mImageName = "profile.png";
		mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ mImageName);
		return mediaFile;
	}
}
