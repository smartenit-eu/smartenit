package eu.smartenit.enduser.app.facebookAccount;

import android.content.Context;
import eu.smartenit.enduser.app.ctm.Log;
import eu.smartenit.enduser.app.wifiService.NetworkUtils;

/**
 * Login to Facebook, get token and provide facebookID.
 * 
 * @author haltug,robert
 * 
 */
public class LoginManager {

	private long facebookID;
	private String authToken;
	private String unadaUrl;
	private Context context;

	// Use default gateway as UnaDa URL
	public LoginManager(Context context, long facebookID, String authToken)
			throws Exception {
		this.facebookID = facebookID;
		this.authToken = authToken;
		this.context = context;
		this.unadaUrl = formUNaDaUrl(NetworkUtils.getDefaultGateway(context));
		Log.d("LoginManager", String.format("UNaDa URL: %s", unadaUrl));
	}

	// TODO remove and alter test case
	public LoginManager(long facebookID, String authToken, String unadaUrl,
			boolean test) {
		this.facebookID = facebookID;
		this.authToken = authToken;
		this.unadaUrl = unadaUrl;
	}

	public void login(long facebookID, IRemoteLoginReplyCallback callback) {
		(new RemoteLoginReply(callback)).execute(combineUrlWithFacebookID());
	}

	public long getFacebookID() {
		return facebookID;
	}

	public String getAuthToken() {
		return authToken;
	}

	private String combineUrlWithFacebookID() {
		String result = unadaUrl;
		if (unadaUrl.endsWith("/"))
			result += facebookID;
		else
			result += "/" + facebookID;
		return result;
	}

	private String formUNaDaUrl(String ip) {
		return String.format("http://%s:8080/unada/rest/login/", ip);
	}

	public String getUnadaUrl() {
		return unadaUrl;
	}

	public void setUnadaUrl(String unadaUrl) {
		this.unadaUrl = formUNaDaUrl(NetworkUtils.getDefaultGateway(context));
	}
}
