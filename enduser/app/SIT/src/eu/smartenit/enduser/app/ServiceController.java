package eu.smartenit.enduser.app;

import java.io.File;

import android.R.string;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import eu.smartenit.enduser.app.R;
import eu.smartenit.enduser.app.ctm.Log;
import eu.smartenit.enduser.app.timers.EnergyTimer;
import eu.smartenit.enduser.app.timers.WifiScanTimer;

public class ServiceController extends Activity {

	private WifiScanTimer mWifiScanTimer;
	private Intent energyTimer;
	private ComponentName energyTimerComponent;
	private ServiceController context;
	private Button mLoginButton;
	private TextView mUsernameTextView;
	private TextView mstatusTextView;
	private ImageView mProfilePictureImageView;

	private BroadcastReceiver mFacebookStatusReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {

			if (intent.getAction().equals(
					"com.tudarmstadt.rbhost.FacebookStatus")) {
				try {
					if (mProfilePictureImageView != null) {

						Bitmap bm = (Bitmap) intent
								.getParcelableExtra("profilePicture");
						if (bm != null)
							mProfilePictureImageView.setImageBitmap(bm);

						disableLoginButton();
					}
				} catch (Exception ex) {
				}
			}
		}
	};

	private BroadcastReceiver mLogReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {

			if (intent.getAction().equals("com.tudarmstadt.rbhost.LOG")) {
				if (mstatusTextView != null) {
					String tmp = mstatusTextView.getText().toString();
					if (tmp.split("\r\n").length > 10) {
						int q = tmp.indexOf("\r\n");
						tmp = tmp.substring(q + 2);
					}
					String tag = intent.getExtras().getString("tag");
					String msg = intent.getExtras().getString("msg");
					if (tag == null || tag.isEmpty())
						tmp += msg + "\r\n";
					else
						tmp += tag + ":" + msg + "\r\n";
					mstatusTextView.setText(tmp);
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_controller);
		context = this;
		mLoginButton = (Button) this.findViewById(R.id.button1);
		mUsernameTextView = (TextView) this.findViewById(R.id.usernameTextView);
		mstatusTextView = (TextView) this.findViewById(R.id.statusTextView);
		mProfilePictureImageView = (ImageView) this
				.findViewById(R.id.profilePictureImageView);
		Log.setContext(context);

		energyTimer = new Intent(this, EnergyTimer.class);
		energyTimerComponent = startService(energyTimer);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.service_controller, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			WifiScanTimer.WifiScanTimerBinder b = (WifiScanTimer.WifiScanTimerBinder) binder;
			mWifiScanTimer = b.getService();

			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(context);
			String facebookID = preferences.getString("FacebookID", "");
			String facebookName = preferences.getString("FacebookName", "");
			

			if (!facebookID.isEmpty()) {
				mWifiScanTimer.setLoggedToFacebook(true, facebookID,
						facebookName, null);
				mWifiScanTimer.Start(facebookID);
			}
			disableLoginButton();
		}

		public void onServiceDisconnected(ComponentName className) {
			mWifiScanTimer = null;
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		Intent serviceInent = new Intent(getBaseContext(), WifiScanTimer.class);
		serviceInent.putExtra("IWifiScanner",
				"eu.smartenit.enduser.app.wifiService.WifiScanner");
		serviceInent.putExtra("IWifiSwitcher",
				"eu.smartenit.enduser.app.wifiService.WifiSwitcher");
		bindService(serviceInent, mConnection, BIND_AUTO_CREATE);
		disableLoginButton();
		{
			IntentFilter i = new IntentFilter();
			i.addAction("com.tudarmstadt.rbhost.LOG");
			this.registerReceiver(this.mLogReceiver, i);
		}
		{
			IntentFilter i = new IntentFilter();
			i.addAction("com.tudarmstadt.rbhost.FacebookStatus");
			this.registerReceiver(this.mFacebookStatusReceiver, i);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		// unbindService(mConnection);
		overridePendingTransition(0, 0);
		this.unregisterReceiver(this.mLogReceiver);
	}

	public void facebookLoginClick(View v) {
		if (mWifiScanTimer.isLoggedToFacebook() == true) {
			mWifiScanTimer.stopRunning();
			mWifiScanTimer.setLoggedToFacebook(false, "", "", null);
			FacebookLogin.facebookLogout(context);
		} else {
			if (mWifiScanTimer.getConnectivityManager().getActiveNetworkInfo() != null) {
				int networkType = mWifiScanTimer.getConnectivityManager()
						.getActiveNetworkInfo().getType();
				if (networkType >= 0 && networkType < 6) {
					facebookLogin();
					return;
				} else {
					Toast.makeText(this,
							R.string.toast_service_controller_bad_network,
							Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(this,
						R.string.toast_service_controller_no_network,
						Toast.LENGTH_LONG).show();
			}
		}

		disableLoginButton();
	}

	private void facebookLogin() {
		this.finish();
		Intent facebookLogin = new Intent(this.getApplicationContext(),
				FacebookLogin.class);
		facebookLogin.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivityForResult(facebookLogin, 0);
	}

	@Override
	public void onDestroy() {
		unbindService(mConnection);
		stopService(energyTimer);
		super.onDestroy();
	}

	private void disableLoginButton() {
		if (mWifiScanTimer == null
				|| mWifiScanTimer.isLoggedToFacebook() == false) {
			mProfilePictureImageView.setImageBitmap(null);
			// mProfilePictureImageView.setImageResource(0);
			mUsernameTextView.setText("");
			mLoginButton.setText("Log in with Facebook");
		} else {
			// Toast.makeText(this, R.string.searching_for_horst,
			// Toast.LENGTH_SHORT).show();
			// mLoginButton.setClickable(false);
			// mLoginButton.setTextColor(17170432);

			mUsernameTextView.setText("Logged in as: "
					+ mWifiScanTimer.getFacebookUsername());
			Bitmap profilePicture = mWifiScanTimer.getFacebookProfilePicture();
			if (profilePicture != null)
				mProfilePictureImageView.setImageBitmap(profilePicture);

			mLoginButton.setText("Log out of Facebook");
		}
	}

}
