package cn.labelnet.net;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.show.api.ShowApiRequest;

/**
 * 音乐请求工具类 ShowAPi 提供的方法
 * 
 */
public class MusicRequest extends NetRequestImp {

	/**
	 *  音乐列表信息
	 */
	private final String HOT_MUSIC_URL = "http://route.showapi.com/213-4";
	private final String SHOW_API_APPID = "12932";
	private final String SHOW_API_SECRET = "0a96acfd83a24f44ad34e0d1e42fb8d7";
	
	/**
	 * 音乐id , 查询信息
	 */
	private final String SELECT_SONG_BY_KEYWORD_URL="http://route.showapi.com/213-1";
	
	/**
	 * 歌词地址
	 */
	private final String GET_SONG_LRC_URL="http://route.showapi.com/213-2";
	
	
	
	ShowApiRequest apiRequest = null;
	// 提供异步回调方法
	private AsyncHttpResponseHandler musicAsyncHandler;

	public void setMusicAsyncHandler(AsyncHttpResponseHandler handler) {
		this.musicAsyncHandler = handler;
	}

	/**
	 * 热门榜 : 数据请求
	 */
	@Override
	public void requestStringData(int topid) {
		new ShowApiRequest(HOT_MUSIC_URL, SHOW_API_APPID, SHOW_API_SECRET)
				.setResponseHandler(musicAsyncHandler)
				.addTextPara("topid", topid + "").post();
	}
	
	/**
	 *  关键字搜索
	 */
	@Override
	public void requestStringData(String keyword) {
		new ShowApiRequest(SELECT_SONG_BY_KEYWORD_URL, SHOW_API_APPID, SHOW_API_SECRET)
        .setResponseHandler(musicAsyncHandler)
        .addTextPara("keyword", keyword)
        .addTextPara("page", "1")
        .post();
	}
	
	/**
	 * 获取歌词
	 */
	@Override
	public void requestStringLrcData(String songId) {
	    new ShowApiRequest(GET_SONG_LRC_URL ,SHOW_API_APPID,SHOW_API_SECRET)
        .setResponseHandler(musicAsyncHandler)
        .addTextPara("musicid",songId+"")
        .post();
	}
	
}
