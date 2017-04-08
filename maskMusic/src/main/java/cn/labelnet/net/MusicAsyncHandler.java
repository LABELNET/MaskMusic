package cn.labelnet.net;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import cn.labelnet.model.MusicModel;
import cn.labelnet.util.StringUtil;

import com.google.gson.JsonArray;
import com.loopj.android.http.AsyncHttpResponseHandler;

public  class MusicAsyncHandler extends AsyncHttpResponseHandler {

	private MusicAsync mAsync;
	private List<MusicModel> mms = null;


	public void setMAsync(MusicAsync mAsync) {
		this.mAsync = mAsync;
		mms = new ArrayList<MusicModel>();
	}

	@Override
	public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		// 失败
		mAsync.onFail(arg3.getMessage() + " : " );
	}

	@Override
	public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		// 成功
		String msg = "no msg";
		if (arg2.length > 0) {
			msg = new String(arg2);
			mms = StringUtil.getMusicList(msg);
			if(mms!=null){
				mAsync.onSuccess(mms);
			}else{
				msg="解析为null";
				mAsync.onFail(msg);
			}
		
		} else {
			mAsync.onFail(msg);
		}

	}

}
