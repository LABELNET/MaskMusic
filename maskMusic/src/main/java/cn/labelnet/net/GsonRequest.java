package cn.labelnet.net;

import java.io.UnsupportedEncodingException;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

public class GsonRequest<T> extends Request<T> {

	private Listener<T> glistener;

	private Gson gson;

	private Class<T> gClass;

	/**
	 * 
	 * @param method
	 * @param url
	 * @param listener
	 */
	public GsonRequest(int method, String url, Class<T> clazz,
			Listener<T> listener, ErrorListener errorlistener) {
		super(method, url, errorlistener);
		gson = new Gson();
		gClass = clazz;
		glistener = listener;
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));

			return Response.success(gson.fromJson(jsonString, gClass),
					HttpHeaderParser.parseCacheHeaders(response));

		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected void deliverResponse(T response) {
		glistener.onResponse(response);
	}

}
