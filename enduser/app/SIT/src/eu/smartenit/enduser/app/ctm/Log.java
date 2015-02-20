package eu.smartenit.enduser.app.ctm;

import android.content.Context;
import android.content.Intent;

public class Log {

	private static Context context;

	public static void setContext(Context context) {
		Log.context = context;
	}

	public static void d(String tag, String msg) {
		android.util.Log.d(tag, msg);
	}

	public static void i(String tag, String msg) {
		android.util.Log.i(tag, msg);
	}

	public static void e(String tag, String msg) {
		android.util.Log.e(tag, msg);
	}

	public static void s(String tag, String msg) {
		android.util.Log.e(tag, msg);
		if (context != null) {
			Intent intent = new Intent();
			intent.setAction("com.tudarmstadt.rbhost.LOG");
			intent.putExtra("tag", tag);
			intent.putExtra("msg", msg);
			context.sendBroadcast(intent);
		}
	}
}
