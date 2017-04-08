package cn.labelnet.framework;

import com.android.volley.toolbox.ImageLoader.ImageListener;

import android.R.anim;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.RemoteViews;
import cn.labelnet.maskmusic.R;
import cn.labelnet.model.MusicModel;
import cn.labelnet.net.VolleyHttpRequest;

public class MusicNotification extends Notification {
	
	/**
	 * Music播放控制 的  Notification 
	 * 动态的显示后台的MusicService的前台展示
	 */

	/**
	 * 恶汉式实现单例模式加载
	 */
	private static MusicNotification notifyInstance = null;

	// 通知id
	private final int NOTIFICATION_ID = 10001;
	// 通知
	private Notification musicNotifi = null;
	// 管理通知
	private NotificationManager manager = null;
	// 界面实现
	private Builder builder = null;
	// 上下文
	private Context context;
	// 布局
	private RemoteViews remoteViews;
	private final int REQUEST_CODE = 30000;

	// 给Service 发送广播
	private final String MUSIC_NOTIFICATION_ACTION_PLAY = "musicnotificaion.To.PLAY";
	private final String MUSIC_NOTIFICATION_ACTION_NEXT = "musicnotificaion.To.NEXT";
	private final String MUSIC_NOTIFICATION_ACTION_CLOSE = "musicnotificaion.To.CLOSE";
	private final String MUSIC_NOTIFICAION_INTENT_KEY = "type";
	private final int MUSIC_NOTIFICATION_VALUE_PLAY = 30001;
	private final int MUSIC_NOTIFICATION_VALUE_NEXT = 30002;
	private final int MUSIC_NOTIFICATION_VALUE_CLOSE =30003;
	private Intent play=null,next=null,close = null;
	private PendingIntent musicPendIntent = null;

	// 给进度条页面广播
	// 待实现

	// 网络 ： 加载图片实现
	private ImageListener imageListener = null;

	public void setManager(NotificationManager manager) {
		this.manager = manager;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	private MusicNotification(Context context) {
		this.context=context;
		// 初始化操作
		remoteViews = new RemoteViews("cn.labelnet.maskmusic",
				R.layout.list_item_notification);
		builder = new Builder(context);

		// 初始化 进度条
		play = new Intent();
		play.setAction(MUSIC_NOTIFICATION_ACTION_PLAY);
		next = new Intent();
		next.setAction(MUSIC_NOTIFICATION_ACTION_NEXT);
	    close = new Intent();
		close.setAction(MUSIC_NOTIFICATION_ACTION_CLOSE);

	}

	/**
	 * 恶汉式实现 通知
	 * 
	 * @return
	 */
	public static MusicNotification getMusicNotification(Context context) {
		if (notifyInstance == null) {
			notifyInstance = new MusicNotification(context);
		}
		return notifyInstance;
	}

	/**
	 * 创建通知
	 * 初始化通知
	 */
	@SuppressLint("NewApi")
	public void onCreateMusicNotifi() {
		// 设置点击事件

		// 1.注册控制点击事件
		play.putExtra("type",
				MUSIC_NOTIFICATION_VALUE_PLAY);
		PendingIntent pplay = PendingIntent.getBroadcast(context, REQUEST_CODE,
				play, NOTIFICATION_ID);
		remoteViews.setOnClickPendingIntent(R.id.iv_nofitication_kzhi_play,
				pplay);

		// 2.注册下一首点击事件
		next.putExtra("type",
				MUSIC_NOTIFICATION_VALUE_NEXT);
		PendingIntent pnext = PendingIntent.getBroadcast(context, REQUEST_CODE,
				next, NOTIFICATION_ID);
		remoteViews.setOnClickPendingIntent(R.id.iv_nofitication_kzhi_next,
				pnext);

		// 3.注册关闭点击事件
		close.putExtra("type",
				MUSIC_NOTIFICATION_VALUE_CLOSE);
		PendingIntent pclose = PendingIntent.getBroadcast(context, REQUEST_CODE,
			  close, NOTIFICATION_ID);
		remoteViews.setOnClickPendingIntent(R.id.iv_nofitication_kzhi_colse,
				pclose);

		builder.setContent(remoteViews).setWhen(System.currentTimeMillis())
				// 通知产生的时间，会在通知信息里显示
//				.setPriority(Notification.PRIORITY_DEFAULT)
				// 设置该通知优先级
				.setOngoing(true).setTicker("播放新的一首歌")
				.setSmallIcon(R.drawable.logo);

		// 兼容性实现
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			musicNotifi = builder.getNotification();
		} else {
			musicNotifi = builder.build();
		}
		musicNotifi.flags = Notification.FLAG_ONGOING_EVENT;
		manager.notify(NOTIFICATION_ID, musicNotifi);
	}

	/**
	 * 更新通知
	 */
	public void onUpdataMusicNotifi(MusicModel mm, boolean isplay) {
		// 设置添加内容
		remoteViews.setTextViewText(R.id.tv_nofitication_singname,
				(mm.getSongname()!=null?mm.getSongname():"什么东东") + "");
		remoteViews.setTextViewText(R.id.tv_nofitication_singer,
				(mm.getSingername()!=null?mm.getSingername():"未知") + "");

		//判断是否播放
		if (isplay) {
			remoteViews.setImageViewResource(R.id.iv_nofitication_kzhi_play,
					android.R.drawable.ic_media_pause);
		} else {
			remoteViews.setImageViewResource(R.id.iv_nofitication_kzhi_play,
					android.R.drawable.ic_media_play);
		}
		onCreateMusicNotifi();
	}
	
	/**
	 * 取消通知栏
	 */
	public void onCancelMusicNotifi(){
		manager.cancel(NOTIFICATION_ID);
	}
	

}
