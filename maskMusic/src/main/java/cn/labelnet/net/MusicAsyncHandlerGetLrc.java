package cn.labelnet.net;

import org.apache.http.Header;

import android.util.Log;
import cn.labelnet.util.StringUtil;

import com.loopj.android.http.AsyncHttpResponseHandler;

public class MusicAsyncHandlerGetLrc extends AsyncHttpResponseHandler{
	/**
	 * 只为 获取 单个 音乐的专辑图片URL服务
	 */

	/**
	 * 设置异步回调
	 */
	private MusicAsyncGetUrl musicasyncGetUrl;
	public void setMusicasyncGetUrl(MusicAsyncGetUrl musicasyncGetUrl) {
		this.musicasyncGetUrl = musicasyncGetUrl;
	}

	@Override
	public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
	}
	
	@Override
	public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
             		if(arg2!=null){
             			String msg=new String(arg2);
             			msg=StringUtil.getMusicSongLrc(msg);
             			//使用一个接口回调
             			musicasyncGetUrl.getSongImageURL(msg);
             		}
	}
	
}
