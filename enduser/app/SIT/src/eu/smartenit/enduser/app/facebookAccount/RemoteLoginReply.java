package eu.smartenit.enduser.app.facebookAccount;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import android.os.AsyncTask;
import eu.smartenit.enduser.app.ctm.Log;

public class RemoteLoginReply extends AsyncTask<String, Void, RemoteLoginReply> {
	private boolean outcome;
	private String privateSSID;
	private String password;
	private IRemoteLoginReplyCallback callback;

	public RemoteLoginReply(IRemoteLoginReplyCallback callback) {
		this.callback = callback;
	}

	public RemoteLoginReply(BufferedReader jsonReader) {
		try {
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = jsonReader.readLine()) != null) {
				sb.append(line + "\n");
			}
			JSONObject jsonObject = new JSONObject(sb.toString());
			this.outcome = jsonObject.getInt("outcome") == 1;
			JSONObject innerObject = jsonObject
					.getJSONObject("wifiConfiguration");
			// According to documentation it should be privateSSID but S Pi
			// returns it as ssid
			this.privateSSID = innerObject.getString("ssid");
			this.password = innerObject.getString("password");

		} catch (Exception e) {
			Log.s("", "Failed to parse result of UnaDa");
			Log.e("RemoteLoginReply", "Failed to parse result");
			// e.printStackTrace();
		}
	}

	public RemoteLoginReply(int nr, String ssid, String password) {
		this.privateSSID = ssid;
		this.password = password;
		this.outcome = nr == 1;
	}

	public boolean isOutcome() {
		return outcome;
	}

	public String getPrivateSSID() {
		return privateSSID;
	}

	public String getPassword() {
		return password;
	}

	@Override
	protected RemoteLoginReply doInBackground(String... params) {
		try {
			DefaultHttpClient defaultClient = new DefaultHttpClient();
			HttpGet httpGetRequest = new HttpGet(params[0]);

			HttpResponse httpResponse = defaultClient.execute(httpGetRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent(), "UTF-8"));

			RemoteLoginReply result = new RemoteLoginReply(reader);
			return result;

		} catch (Exception e) {
			Log.s("", "Failed to call UNada, try again ...");
			Log.e("RemoteLoginReply", "Failed to call remote UNada Login API");
			return null;
		}
	}

	protected void onPostExecute(RemoteLoginReply result) {
		callback.RemoteLoginReplyCallback(result);
	}
}
