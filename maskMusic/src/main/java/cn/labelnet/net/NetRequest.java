package cn.labelnet.net;

public interface NetRequest {
	
	/**
	 * 网络请求接口 
	 */
	
	//请求字符串
	String requestStringData();
	
	void requestStringData(int topid);
	
	void requestStringData(String keyword);
	
	void requestStringLrcData(String songId);
	
}
