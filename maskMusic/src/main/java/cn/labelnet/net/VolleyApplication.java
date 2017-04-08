package cn.labelnet.net;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import android.app.Application;

public class VolleyApplication extends Application {

	/**
	 */

	private static RequestQueue queue;

	@Override
	public void onCreate() {
		super.onCreate();
		queue = Volley.newRequestQueue(getApplicationContext());
	}

	public static RequestQueue getQueue() {
		return queue;
	}

}
