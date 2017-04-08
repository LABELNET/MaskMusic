package cn.labelnet.net;

public class NetRequestImp implements NetRequest {

	/**
	 * 实现NetRequest接口,给想要实现方法的提供想要的方法
	 * 
	 */

	@Override
	public String requestStringData() {
		return null;
	}

	@Override
	public void requestStringData(int topid) {
		// 这里给MusicRequest 提供此方法,在MusicRequest 中 重写这个方法即可
	}
	
	@Override
	public void requestStringData(String keyword) {
	}

	@Override
	public void requestStringLrcData(String songId) {
		
	}
	
}
