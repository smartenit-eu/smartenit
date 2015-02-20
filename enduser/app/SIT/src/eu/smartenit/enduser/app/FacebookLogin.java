package eu.smartenit.enduser.app;

import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import eu.smartenit.enduser.app.ctm.Log;
import android.view.Menu;
import eu.smartenit.enduser.app.R;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import eu.smartenit.enduser.app.facebookAccount.LoginManager;
import eu.smartenit.enduser.app.timers.WifiScanTimer;

public class FacebookLogin extends Activity {

	LoginManager loginManager;
	Context contextT;
	WifiScanTimer mWifiScanTimer;
	boolean loggedToFacebook = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.contextT = getApplicationContext();
		setContentView(R.layout.activity_facebook_login);

		// start Facebook Login
		Session.openActiveSession(this, true, new Session.StatusCallback() {

			// callback when session changes state
			@Override
			public void call(final Session session, SessionState state,
					Exception exception) {
				if (session.isOpened()) {
					// make request to the /me API
					Log.d("FacebookLogin", "Send login request to facebook");
					Request.newMeRequest(session,
							new Request.GraphUserCallback() {
								// callback after Graph API response with user
								// object
								@Override
								public void onCompleted(GraphUser user,
										Response response) {
									Log.d("FacebookLogin",
											"Facebook login request reply received");
									// Login success
									if (user != null) {
										// Save Facebook ID
										SharedPreferences preferences = PreferenceManager
												.getDefaultSharedPreferences(contextT);

										SharedPreferences.Editor editor = preferences
												.edit();
										editor.putString("FacebookID",
												user.getId());

										editor.putString("FacebookName",
												user.getName());

										editor.commit();
										// Intent serviceController = new
										// Intent(
										// contextT,
										// ServiceController.class);

										Bitmap bitmap = null;
										URL imgUrl = null;
										try {
											imgUrl = new URL(
													"https://graph.facebook.com/"
															+ user.getId()
															+ "/picture?type=large");

											InputStream in = (InputStream) imgUrl
													.getContent();
											bitmap = BitmapFactory
													.decodeStream(in);
										} catch (Exception e) {
											e.printStackTrace();
										}

										mWifiScanTimer.setLoggedToFacebook(
												true, user.getId(),
												user.getName(), imgUrl);

									} else
										Log.d("FacebookLogin",
												"Facebook login failed");
								}
							}).executeAsync();
				}
			}
		});
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
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);

	}

	@Override
	public void onBackPressed() {
		this.finish();
		Intent serviceController = new Intent(this.getApplicationContext(),
				ServiceController.class);
		startActivityForResult(serviceController, 0);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unbindService(mConnection);
		overridePendingTransition(0, 0);
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			WifiScanTimer.WifiScanTimerBinder b = (WifiScanTimer.WifiScanTimerBinder) binder;
			mWifiScanTimer = b.getService();
			closeLogin();
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
	}

	private void closeLogin() {
		this.finish();
		Intent serviceController = new Intent(this.getApplicationContext(),
				ServiceController.class);
		startActivityForResult(serviceController, 0);
	}

	public static boolean facebookLogout(Context context) {
		try {
			Session session = Session.getActiveSession();
			if (session != null) {
				if (!session.isClosed()) {
					session.closeAndClearTokenInformation();
					clearPreference(context);
				}
			} else {
				session = new Session(context);
				Session.setActiveSession(session);

				session.closeAndClearTokenInformation();
				clearPreference(context);
			}
			return true;
		} catch (Exception ex) {
			Log.e("FacebookLogin", ex.getMessage());
			return false;
		}
	}

	private static void clearPreference(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		SharedPreferences.Editor editor = preferences.edit();
		editor.remove("FacebookID");
		editor.commit();
	}

}
