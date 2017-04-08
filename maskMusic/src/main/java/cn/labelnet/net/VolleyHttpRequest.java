package cn.labelnet.net;

import java.util.Map;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

public class VolleyHttpRequest {

	public static void String_request(String url,
			VolleyHandler<String> volleyRequest) {
		Volley_StringRequest(Method.GET, url, null, volleyRequest);
	}

	public static void String_request(String url,
			final Map<String, String> map, VolleyHandler<String> volleyRequest) {
		Volley_StringRequest(Method.POST, url, map, volleyRequest);
	}

	private static void Volley_StringRequest(int method, String url,
			final Map<String, String> params,
			VolleyHandler<String> volleyRequest) {
		StringRequest stringrequest = new StringRequest(method, url,
				volleyRequest.reqLis, volleyRequest.reqErr) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return params;
			}
		};
		stringrequest.setTag("stringrequest");
		VolleyApplication.getQueue().add(stringrequest);
	}

	public static void JsonObject_Request(String url,
			VolleyHandler<JSONObject> volleyRequest) {
		Volley_JsonObjectRequest(Method.GET, url, null, volleyRequest);
	}

	public static void JsonObject_Request(String url, JSONObject jsonObject,
			VolleyHandler<JSONObject> volleyRequest) {
		Volley_JsonObjectRequest(Method.POST, url, jsonObject, volleyRequest);
	}

	private static void Volley_JsonObjectRequest(int method, String url,
			JSONObject jsonObject, VolleyHandler<JSONObject> volleyRequest) {
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method,
				url, jsonObject, volleyRequest.reqLis, volleyRequest.reqErr);
		jsonObjectRequest.setTag("jsonObjectRequest");
		VolleyApplication.getQueue().add(jsonObjectRequest);
	}

	public static void Image_request(String url,
			VolleyHandler<Bitmap> volleyRequest) {
		Volley_ImageRequest(url, 0, 0, volleyRequest);
	}

	public static void Image_request(String url, int maxWidth, int maxHeight,
			VolleyHandler<Bitmap> volleyRequest) {
		Volley_ImageRequest(url, maxWidth, maxHeight, volleyRequest);
	}

	private static void Volley_ImageRequest(String url, int maxWidth,
			int maxHeight, VolleyHandler<Bitmap> volleyRequest) {
		ImageRequest imageRequest = new ImageRequest(url, volleyRequest.reqLis,
				maxWidth, maxHeight, Config.RGB_565, volleyRequest.reqErr);
		imageRequest.setTag("imageRequest");
		VolleyApplication.getQueue().add(imageRequest);
	}

	public static void Image_Loader(String url, ImageListener imageListener,
			int maxWidth, int maxHidth) {
		Volley_ImageLoader(url, imageListener, maxWidth, maxHidth);
	}

	public static void Image_Loader(String url, ImageListener imageListener) {
		Volley_ImageLoader(url, imageListener, 0, 0);
	}



	private static void Volley_ImageLoader(String url,
			ImageListener imageListener, int maxWidth, int maxHidth) {
		ImageLoader imageLoader = new ImageLoader(VolleyApplication.getQueue(),
				new VolleyBitmapCache());
		imageLoader.get(url, imageListener, maxWidth, maxHidth);
	}

}
