package cn.labelnet.net;

import com.android.volley.Response;
import com.android.volley.VolleyError;


/**
 * @author yuan
 *
 */

public abstract class VolleyHandler<T> {

	
	public Response.Listener<T> reqLis;
	public Response.ErrorListener reqErr;

	public VolleyHandler() {
		reqLis = new reqListener();
		reqErr = new reqErrorListener();

	}

	public abstract void reqSuccess(T response);

	public abstract void reqError(String error);

	public class reqListener implements Response.Listener<T> {

		@Override
		public void onResponse(T response) {
			reqSuccess(response);
		}
	}

	public class reqErrorListener implements Response.ErrorListener {

		@Override
		public void onErrorResponse(VolleyError error) {
			reqError(error.getMessage());
		}

	}

}
