package cn.labelnet.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import cn.labelnet.model.MusicModel;

public class StringUtil {

	private static JSONArray jsonArray = null;
	private static JSONObject jsonObject = null;
	private static MusicModel musicModel = null;
	private static List<MusicModel> mms = null;
	private static Gson gson = new Gson();

	/**
	 * 解析音乐JSON数据 : (1) 使用最优化的请况实现,防止内存泄漏; (2) 重复使用同一个变量;
	 * 
	 * @param json
	 * @return
	 */
	public synchronized static List<MusicModel> getMusicList(String json) {
		mms = new ArrayList<MusicModel>();
		try {
			jsonObject = new JSONObject(json);
			int showapi_res_code = jsonObject.getInt("showapi_res_code");
			if (showapi_res_code < 0) {
				return null;
			}
			jsonObject = jsonObject.getJSONObject("showapi_res_body");
			jsonObject = jsonObject.getJSONObject("pagebean");
			jsonArray = jsonObject.getJSONArray("songlist");
			for (int i = 0; i < jsonArray.length(); i++) {
				musicModel = gson.fromJson(jsonArray.get(i).toString(),
						MusicModel.class);
				mms.add(musicModel);
			}

		} catch (JSONException e) {
			return null;
		} finally {
			jsonArray = null;
			jsonObject = null;
		}
		return mms;
	}

	/**
	 * 解析出来 专辑 图片
	 * 
	 * @param msg
	 * @return
	 */
	public synchronized static String getMusicImageUrl(String msg) {

		String sName = null;
		try {

			jsonObject = new JSONObject(msg);
			int showapi_res_code = jsonObject.getInt("showapi_res_code");
			if (showapi_res_code < 0) {
				return null;
			}
			jsonObject = jsonObject.getJSONObject("showapi_res_body");
			jsonObject = jsonObject.getJSONObject("pagebean");
			jsonArray = jsonObject.getJSONArray("contentlist");

//			// 第一步 ：根据歌名查询，返回结果
//			for (int i = 0; i < jsonArray.length(); i++) {
//				jsonObject = jsonArray.getJSONObject(i);
//				if (singName.trim().equals(
//						jsonObject.getString("singername").trim())) {
//					// 歌手名字一样
//					sName = jsonObject.getString("albumpic_big");
//					break;
//				}
//			}
			// 根据歌手查询，返回结果
			if (sName == null) {
				if (jsonArray.length() > 0) {
					jsonObject = jsonArray.getJSONObject(0);
					sName = jsonObject.getString("albumpic_big");
				}
			}

		} catch (Exception e) {
			return null;
		} finally {
			jsonArray = null;
			jsonObject = null;
		}
		return sName;
	}

	/**
	 * 解析歌词
	 * @param msg
	 * @return
	 */
	public synchronized static String getMusicSongLrc(String msg) {
		
		String lrc=null;
		try{
			jsonObject = new JSONObject(msg);
			int showapi_res_code = jsonObject.getInt("showapi_res_code");
			if (showapi_res_code < 0) {
				return null;
			}
			jsonObject = jsonObject.getJSONObject("showapi_res_body");
			lrc=jsonObject.getString("lyric");
		}catch(Exception e){
			return null;
		}
		return lrc;
	}

}
