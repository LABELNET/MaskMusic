package cn.labelnet.net;

import java.util.List;

import cn.labelnet.model.MusicModel;

public interface MusicAsync {

	//成功
	void onSuccess(List<MusicModel> mms);
	
	//失败
	void onFail(String msg);
	
	
}
