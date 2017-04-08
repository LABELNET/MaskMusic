package cn.labelnet.event;

public interface LrcViewToMusicActivity {
	
     /**
      * LrcView的自定义事件，给
      */
	
	/**
	 * 
	 * 1.判断是否有歌词
	 * 2.在进行初始化成功后，2s之内没有加载到歌词就显示提示
	 * @param isLrc，是否有歌词
	 */
      void LrcViewIsLrc(boolean isLrc);
      
	
}
