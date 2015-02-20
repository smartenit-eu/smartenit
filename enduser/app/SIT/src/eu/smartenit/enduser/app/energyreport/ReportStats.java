package eu.smartenit.enduser.app.energyreport;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;

import eu.smartenit.enduser.app.ctm.Log;
import android.app.admin.DeviceAdminInfo;
import android.os.AsyncTask;

//TODO refactor to use one json and http class
//TODO use gson

public class ReportStats extends AsyncTask<String, Void, ReportStats> {
	private static volatile boolean registerRequired = true;
	private String androidId;
	// private String metricserver = "http://130.83.117.49:9001/%s/%s.json";
	//private String metricserver = "http://192.168.5.31:9000/%s/%s.json";
	private String metricserver = "http://10.200.232.255:9000/%s/%s.json";
	

	public ReportStats(String androidId) {
		this.androidId = androidId;
	}

	public static void setRegisterRequired(boolean value) {
		registerRequired = value;
	}

	public static boolean getRegisterRequired() {
		return registerRequired;
	}

	private String deviceInfo() {
		return new Gson().toJson(new DeviceInfo(androidId));
	}

	private String flowData() {
		PhoneData pd = new PhoneData(androidId);
		
		Date now = new Date();

		Measurements[] m = new Measurements[1];
		m[0] = new Measurements();
		m[0].setInterfaces(NetworkStatsReader.read());
		m[0].setNetworktraffic("100");
		m[0].setTimestamp(now.getTime()/1000);
		m[0].setTotalcpucycle("5");
		m[0].setUsercpucycle("2");
		pd.setMeasurements(m);

		return new Gson().toJson(pd);
	}

	protected String getServerUrl(String folder) {
		return String.format(metricserver, folder, androidId);
	}

	protected String postHttp(String Url, String value) throws Exception {

		// TODO check if already registered device
		DefaultHttpClient defaultClient = new DefaultHttpClient();
		HttpPost httpPostRequest = new HttpPost(Url);

		StringEntity input = new StringEntity(value);
		input.setContentType("application/json");
		httpPostRequest.setEntity(input);

		HttpResponse httpResponse = defaultClient.execute(httpPostRequest);

		// TODO log http return
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				httpResponse.getEntity().getContent(), "UTF-8"));

		String result = "", sCurrentLine;
		while ((sCurrentLine = reader.readLine()) != null)
			result += sCurrentLine;

		defaultClient.getConnectionManager().shutdown();

		return result;
	}

	protected ReportStats doInBackground(String... params) {
		try {
			if (registerRequired) {
				Log.d("ReportStats",
						postHttp(getServerUrl("info"), deviceInfo()));

				registerRequired = false;
			}
			Log.d("ReportStats",
					postHttp(getServerUrl("phone_data"), flowData()));
			return this;
		} catch (Exception e) {
			Log.e("ReportStats", e.getMessage());
			return null;
		}
	}

	protected void onPostExecute(ReportStats result) {

	}
}
