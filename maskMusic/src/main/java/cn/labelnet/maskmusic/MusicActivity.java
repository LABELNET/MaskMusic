/**
 * 
 */
package cn.labelnet.maskmusic;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import cn.labelnet.event.LrcPlayToEnd;
import cn.labelnet.event.LrcViewToMusicActivity;
import cn.labelnet.model.MusicModel;
import cn.labelnet.net.MusicAsyncGetUrl;
import cn.labelnet.net.MusicAsyncHandlerGetUrl;
import cn.labelnet.net.MusicRequest;
import cn.labelnet.net.VolleyHttpPath;
import cn.labelnet.net.VolleyHttpRequest;
import cn.labelnet.ui.CountDownTimer;
import cn.labelnet.ui.LrcView;
import cn.labelnet.util.TimeUtil;
import android.R.dimen;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 音乐播放器Activity 实现 ： (1)播放控制（通过BordcastReceiver） (2)进度条实现控制 (3)歌词实现，滚动控制
 *
 */
public class MusicActivity extends Activity implements OnClickListener,
		MusicAsyncGetUrl, LrcViewToMusicActivity {

	// UI
	private ImageView iv_music_back, iv_music_download, iv_music_songpic,
			music_play, music_next, music_prev;
	private TextView tv_music_songname, tv_music_singer, tv_music_heart,
			tv_time_sheng, tv_time_all, tv_song_lrc;
	private ProgressBar progressbar_music, music_show_gone;

	// 网络
	private MusicAsyncHandlerGetUrl musicAsyncHandlerGetUrl;
	private MusicRequest musicRequest;
	private String musicName = null;
	// net 图片请求
	private ImageListener imageListener = null;

	// 控制
	private int i = 0;

	// MusicActivity
	private final String MUSIC_ACTICITY_INTNET = "music";
	private Intent mIntent = null;

	// 广播
	private BroadCastMusicReceiver broadMusicReceiver;
	private final String MUSIC_SERVICE_RECEIVER_ACTION = "service.to.musicactivity";
	private final String MUSIC_SERVICE_TO_ACTIVITY_MODEL = "model";
	private final String MUSIC_SERVICE_TO_ACTIVITY_ISPLAY = "isplay";
	private final String MUSIC_SERVICE_TO_ACTIVITY_NOWTIME = "nowtime";
	// 响应码 : 41001 没数据 , 41002 : 有数据
	private final String MUSIC_SERVICE_TOACTIVITY_CODE = "mpcode";

	// Service
	private final String MUSIC_ACTIVITY_SERVICE_ACTION = "activity.to.musicservice";
	private final String MUSIC_ACTIVITY_SERVICE_KEY = "musictype";
	private Intent serviceIntent = null;

	// 数据
	private MusicModel mm = null;
	private boolean isplay = false;

	// 倒计时
	private CountDownTime countDown = null;
	private final int COUNT_DOWN_INTERVAL = 1000;
	private int currentTime = 0;

	// 进度条
	private double allSecond = 0;

	// 歌词
	private LrcView lrc;
	private LrcPlayToEnd lrcplaytoend;

	@Override
	protected void onStart() {
		initData();
		try {
			serviceIntent.putExtra(MUSIC_ACTIVITY_SERVICE_KEY, 40001);
			sendBroadcast(serviceIntent);
		} catch (Exception e) {
			showToast("new Intent 异常 : " + e.getMessage());
		}
		super.onStart();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music);
		initView();
		initBoradCast();
	}

	// 初始化 广播 和 注册 广播
	private void initBoradCast() {
		broadMusicReceiver = new BroadCastMusicReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MUSIC_SERVICE_RECEIVER_ACTION);
		registerReceiver(broadMusicReceiver, filter);
	}

	// 初始化 数据serviceIntent
	private void initData() {
		// 实例化 Service 中的话 Intent
		serviceIntent = new Intent();
		serviceIntent.setAction(MUSIC_ACTIVITY_SERVICE_ACTION);
	}

	// 初始化 界面布局
	private void initView() {
		iv_music_back = (ImageView) findViewById(R.id.iv_music_back);
		iv_music_download = (ImageView) findViewById(R.id.iv_music_download);
		iv_music_songpic = (ImageView) findViewById(R.id.iv_music_songpic);
		music_play = (ImageView) findViewById(R.id.music_play);
		music_next = (ImageView) findViewById(R.id.music_next);
		music_prev = (ImageView) findViewById(R.id.music_prev);
		iv_music_back.setOnClickListener(this);
		iv_music_download.setOnClickListener(this);
		iv_music_songpic.setOnClickListener(this);
		music_play.setOnClickListener(this);
		music_next.setOnClickListener(this);
		music_prev.setOnClickListener(this);
		tv_music_songname = (TextView) findViewById(R.id.tv_music_songname);
		tv_music_singer = (TextView) findViewById(R.id.tv_music_singer);
		tv_music_heart = (TextView) findViewById(R.id.tv_music_heart);
		tv_time_sheng = (TextView) findViewById(R.id.tv_time_sheng);
		tv_time_all = (TextView) findViewById(R.id.tv_time_all);
		tv_song_lrc = (TextView) findViewById(R.id.tv_song_lrc);
		progressbar_music = (ProgressBar) findViewById(R.id.progressbar_music);
		music_show_gone = (ProgressBar) findViewById(R.id.music_show_gone);
		tv_music_songname.setText(i + "");
		lrc = (LrcView) findViewById(R.id.lrc);
		lrc.setLrcViewToMusicActivity(this);
		lrcplaytoend = lrc;

		// 初始化网络请求
		musicAsyncHandlerGetUrl = new MusicAsyncHandlerGetUrl();
		musicAsyncHandlerGetUrl.setMusicasyncGetUrl(this);
		musicRequest = new MusicRequest();
		
		// 初始化专辑图片监听事件
		imageListener = ImageLoader.getImageListener(iv_music_songpic,
				R.drawable.moren, R.drawable.moren_big);

		// 人性话歌词提示
		goneLrc(getString(R.string.music_activity_renxinghua));

	}

	@Override
	protected void onNewIntent(Intent intent) {

	}

	private void initMusicActivity() {

		// 更新UI
		if (isplay) {
			music_play.setImageResource(R.drawable.play);
			// 倒计时实现
			if (countDown != null) {
				countDown.start();
			}
		} else {
			music_play.setImageResource(R.drawable.pause);
			if (countDown != null) {
				countDown.cancel();
			}
		}
		String songName = mm.getSongname() != null ? mm.getSongname()
				: getString(R.string.music_songname_sm);
		tv_music_songname.setText(songName);
		String singName = mm.getSingername() != null ? mm.getSingername()
				: "未知";
		tv_music_singer.setText(singName);
		int heart = (mm.getSongid() + mm.getSingerid()) / 40 + 23;
		tv_music_heart.setText(heart + "");
		String allTime = TimeUtil.getMinuteBySecond(mm.getSeconds());
		tv_time_all.setText(allTime);

	}

	@Override
	public void onClick(View v) {
		i++;
		// 点击事件
		switch (v.getId()) {
		case R.id.iv_music_back:
			// 返回
			finish();
			break;
		case R.id.iv_music_download:
			showToast("暂时不支持下载操作");
			break;
		case R.id.iv_music_songpic:
			// 歌曲专辑图片信息
			break;
		case R.id.music_play:
			// 播放与暂停
			serviceIntent.putExtra(MUSIC_ACTIVITY_SERVICE_KEY, 40002);
			sendBroadcast(serviceIntent);
			break;
		case R.id.music_next:
			// 下一曲
			music_show_gone.setVisibility(View.VISIBLE);
			serviceIntent.putExtra(MUSIC_ACTIVITY_SERVICE_KEY, 40003);
			sendBroadcast(serviceIntent);
			break;
		case R.id.music_prev:
			// 上一曲
			music_show_gone.setVisibility(View.VISIBLE);
			serviceIntent.putExtra(MUSIC_ACTIVITY_SERVICE_KEY, 40004);
			sendBroadcast(serviceIntent);
			break;
		}
	}

	private void CountDown(int allTime) {
		countDown = new CountDownTime(allTime, COUNT_DOWN_INTERVAL);
	}

	// toast工具类
	private void showToast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public void onStop() {
		super.onStop();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadMusicReceiver);
	}

	/**
	 * 接收MusicService 来的 信息
	 */
	private class BroadCastMusicReceiver extends BroadcastReceiver {

		int code = 0;

		@Override
		public void onReceive(Context context, Intent intent) {

			code = intent.getIntExtra(MUSIC_SERVICE_TOACTIVITY_CODE, 0);
			if (code > 0) {
				// MusicService 来的控制
				musicServiceKong(code, intent);
			}
			Log.d("MaskMusic", "MusicActivity - code : " + code);
			// 隐藏进度条
			music_show_gone.setVisibility(View.GONE);
		}

		/**
		 * MusicService 来的控制
		 * 
		 * @param intent
		 */
		private void musicServiceKong(int code, Intent intent) {
			isplay = intent.getBooleanExtra(MUSIC_SERVICE_TO_ACTIVITY_ISPLAY,
					false);
			// isEnd=intent.get
			mm = (MusicModel) intent
					.getSerializableExtra(MUSIC_SERVICE_TO_ACTIVITY_MODEL);
			allSecond = mm.getSeconds();
//			Log.d("MaskMusic","mm : "+mm.toString());
			if (code == 41001) {
				// 初始化 时间
				if (countDown != null) {
					countDown.cancel();
					countDown = null;
				}
				CountDown(mm.getSeconds() * 1000);
			} else {

				// 销毁上一个对象
				if (countDown != null) {
					countDown.cancel();
					countDown = null;
				}
				// 倒计时同步
				currentTime = intent.getIntExtra(
						MUSIC_SERVICE_TO_ACTIVITY_NOWTIME, 0);
				CountDown(currentTime);
			}

			// Log.d("MaskMusic",
			// "allSecond :"+allSecond+" currentTime :"+currentTime);

			/**
			 * 实现步骤 ： 请求专辑图片，1.解析JSON 2.对比歌手名称 3.返回专辑图片URL
			 */
			if (musicName == null || !musicName.equals(mm.getSongname())) {
				// 进行专辑图片获取
				if (mm != null) {
					Log.d("MaskMusic","mm : "+mm.toString());
					musicRequest.setMusicAsyncHandler(musicAsyncHandlerGetUrl);
					musicRequest.requestStringData(mm.getSingername() + "");
					musicName = mm.getSongname();
				}
			}

			/**
			 * 获得歌词
			 */
			// 获得歌词
			try {
				music_show_gone.setVisibility(View.VISIBLE);
				if (isplay) {
					showLrc();
				}
				lrc.loadLrcByUrl(mm.getSongid() + "");
			} catch (Exception e) {
				// Log.d("MaskMusic", e.getMessage());
				e.printStackTrace();
			}

			/**
			 * 暂停的时候初始化 界面
			 * 
			 */
			if (!isplay) {
				double second = currentTime / 1000;
				goneLrc(getString(R.string.maskmusic_welcome));
				lrcplaytoend
						.playToPause((long) (allSecond * 1000 - currentTime));
				tv_time_sheng.setText(TimeUtil.getMinuteBySecond((int) second));
				second = second / allSecond * 100;
				progressbar_music.setProgress((int) (100 - second));
			}

			initMusicActivity();
		}

	}

	/**
	 * 倒计时
	 */
	private class CountDownTime extends CountDownTimer {

		private double second = 0;

		public CountDownTime(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			second = millisUntilFinished / 1000;
			tv_time_sheng.setText(TimeUtil.getMinuteBySecond((int) second));
			// 进度条实现
			second = (allSecond - second) / allSecond * 100;
			// Log.d("MaskMusic", "tick : "+second);
			progressbar_music.setProgress((int) second);
			// 歌词
			second = allSecond * 1000 - millisUntilFinished;
			// Log.d("MaskMusic", "geci  : "+(long)second);
			lrc.updateTime((long) second);
			// lrcplaytoend.playToPause((long)
			// (allSecond*1000-millisUntilFinished));
		}

		@Override
		public void onFinish() {
			// showToast("MusicActivity 播放完毕");
			lrc.destroyDrawingCache();
			// 播放完毕显示歌词
			// showLrc();
			// 播放完毕需要进行 ，初始化界面 1.进度条初始值，2.歌词回归到第一行 3.时间恢复到总时间
			// 播放中 ，暂停恢复 ： 1.进度条进度保持 2.歌词保持位置 3.时间保持（可以从MusicService获取）
			progressbar_music.setProgress(0);
			tv_time_sheng.setText(TimeUtil.getMinuteBySecond((int) allSecond));
			allSecond = 0;
			lrcplaytoend.playToEnd();
		}
	}

	/**
	 * 加载图片
	 */
	@Override
	public void getSongImageURL(String imgUrl) {
		VolleyHttpRequest.Image_Loader(imgUrl, imageListener);
	}

	/**
	 * 没有歌词 ，显示提示信息
	 * 
	 * @param msg
	 */
	private void goneLrc(String msg) {
		lrc.setVisibility(View.GONE);
		tv_song_lrc.setVisibility(View.VISIBLE);
		tv_song_lrc.setText(msg);
	}

	/**
	 * 有歌词 ，显示歌词，隐藏提示信息
	 */
	private void showLrc() {
		lrc.setVisibility(View.VISIBLE);
		tv_song_lrc.setVisibility(View.GONE);
	}

	/**
	 * 判断是否有歌词
	 */
	@Override
	public void LrcViewIsLrc(boolean isLrc) {
		if (isLrc) {
			showLrc();
		} else {
			goneLrc(getString(R.string.msuic_acitivity_nolrc));
		}
	}

}
