package cn.labelnet.event;


/**
 * 接口实现意图 ：LrcView实现此接口，后在MusciActivity中，使用其接口，将调用LrcView中实现的playToEnd()方法，
 * 进行歌词初始化操作
 */
public interface LrcPlayToEnd {


	/**
	 *  播放到最后，回调初始化 歌词显示
	 */
	void playToEnd();
	
	/**
	 * 暂停后，初始化节面时，将歌词设置到当前时间位置
	 */
	void playToPause(long mNextTime);

}
