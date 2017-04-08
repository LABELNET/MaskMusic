package cn.labelnet.event;

import java.util.List;

import cn.labelnet.model.MusicModel;

public interface MainToFragmentRefrsh {

	/**
	 * 传条数，改变布局高度
	 * 
	 * @param size
	 */
	void changeFragmentHeight(int size);

	/**
	 * 得到音乐列表
	 * 
	 * @param models
	 */
	void getMusicModelList(List<MusicModel> models);

	/**
	 * ListView点击事件
	 * 
	 * @param postion
	 */
	void onListviewOnItemClickListener(int postion);
	
	/**
	 * 失败的回调
	 */

	void onFailListener();
	
	
	
}
