package cn.labelnet.framework;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import cn.labelnet.maskmusic.MainActivity;
import cn.labelnet.model.MusicModel;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.Toast;

public class MusicService extends Service implements OnPreparedListener,
        OnCompletionListener, OnErrorListener {

    /**
     * MusicService 音乐播放控制 ： 随着应用的启动而启动 基本步揍 ： 1.应用启动 ： 就启动 2.状态栏显示 ：
     * notification 3.注册 : bordcastReceiver 4.请求第1首歌 ： 更新状态栏 5.实现上一曲，下一曲，播放，暂停控制
     */

    // 常量
    private final String MUSIC_INTENT_KEY = "musics";
    private final int MUSIC_INTENT_FLAG = 20001;
    private final int MAIN_MUSIC_INTENT_FLAG = 20017;
    // 音乐列表
    private List<MusicModel> musics = null;
    private int mmSize = 0;
    // 通知栏
    private MusicNotification musicNotifi = null;
    private MusicModel mm = null;

    // MediaPlay
    private MediaPlayer mp = null;
    private int currentTime = 0;

    // Music广播接收
    private MusicBroadCast musicBroadCast = null;
    // MainActivity 来的 Action
    private final String MAIN_ACTIVIY_ACTION = "mainActivity.To.MusicService";
    // 来自通知栏的Action
    private final String MUSIC_NOTIFICATION_ACTION_PLAY = "musicnotificaion.To.PLAY";
    private final String MUSIC_NOTIFICATION_ACTION_NEXT = "musicnotificaion.To.NEXT";
    private final String MUSIC_NOTIFICATION_ACTION_CLOSE = "musicnotificaion.To.CLOSE";
    private final String MUSIC_NOTIFICAION_INTENT_KEY = "notify.music";

    // MusicService 来的 Action
    private final String MUSIC_ACTIVITY_SERVICE_ACTION = "activity.to.musicservice";
    private final String MUSIC_ACTIVITY_SERVICE_KEY = "musictype";
    private final int MUSIC_ACTIVITY_SERVICE_REQUEST = 40001;

    // 给MusicActivity 的 Action
    private final String MUSIC_SERVICE_RECEIVER_ACTION = "service.to.musicactivity";
    private Intent mActivityIntent = null;
    private final String MUSIC_SERVICE_TO_ACTIVITY_MODEL = "model";
    private final String MUSIC_SERVICE_TO_ACTIVITY_ISPLAY = "isplay";
    private final String MUSIC_SERVICE_TO_ACTIVITY_NOWTIME = "nowtime";
    // 响应码 : 41001 没数据 , 41002 : 有数据
    private final String MUSIC_SERVICE_TOACTIVITY_CODE = "mpcode";

    // Intent keys
    private final String MAIN_MUSIC_INTENT_KEY = "mIntent";

    //停止服务的intent
    private Intent stopIntent;

    @Override
    public void onCreate() {
        // 初始化MusicActivity 的 Intent ,给 MusicActivity 发送广播 ,修改音乐播放界面
        mActivityIntent = new Intent();
        mActivityIntent.setAction(MUSIC_SERVICE_RECEIVER_ACTION);
        // 初始化通知栏
        musicNotifi = MusicNotification.getMusicNotification(getApplicationContext());
        musicNotifi.setContext(getBaseContext());
        musicNotifi
                .setManager((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
        musicNotifi.onCreateMusicNotifi();
        // 初始化MediaPlay : 设置监听事件
        mp = new MediaPlayer();
        mp.setOnPreparedListener(this);
        mp.setOnCompletionListener(this);
        mp.setOnErrorListener(this);
        // 注册广播
        musicBroadCast = new MusicBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MAIN_ACTIVIY_ACTION);
        filter.addAction(MUSIC_ACTIVITY_SERVICE_ACTION);
        filter.addAction(MUSIC_NOTIFICATION_ACTION_PLAY);
        filter.addAction(MUSIC_NOTIFICATION_ACTION_NEXT);
        filter.addAction(MUSIC_NOTIFICATION_ACTION_CLOSE);
        registerReceiver(musicBroadCast, filter);

        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.stopIntent = intent;
        try {
            musics = (List<MusicModel>) intent
                    .getSerializableExtra(MUSIC_INTENT_KEY);
        } catch (Exception e) {

        }
        if (musics != null) {
            mmSize = musics.size();
        }
        // showToast("1." + musics.get(1).getSongname());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        //释放音乐资源
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
            musics = null;
        }
        //停止当前的Service
        stopService(stopIntent);
        //取消通知
        musicNotifi.onCancelMusicNotifi();
        // 取消注册的广播
        unregisterReceiver(musicBroadCast);
        //关闭应用
        System.exit(0);
    }

    // //////////////////////////////Music Util//////////////////////

    // Toast
    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    // 音乐播放
    private void play(String musicUrl) {
        mp.reset();
        try {
            mp.setDataSource(getApplicationContext(), Uri.parse(musicUrl));
            mp.prepareAsync();

        } catch (IOException e) {
            showToast("网络错误,播放失败");
        }
        musicNotifi.onUpdataMusicNotifi(mm, true);
    }

    // 音乐暂停
    private void pause() {
        if (mp.isPlaying()) {
            currentTime = mp.getCurrentPosition();
            mp.pause();
        }
        musicNotifi.onUpdataMusicNotifi(mm, false);
    }

    // 音乐继续播放
    private void resume() {
        mp.start();
        if (currentTime > 0) {
            mp.seekTo(currentTime);
        }
        musicNotifi.onUpdataMusicNotifi(mm, true);
    }

    // 音乐停止
    private void stop() {
        mp.stop();
        try {
            mp.prepare();
        } catch (IOException e) {
            showToast("音乐停止异常");
        }
        musicNotifi.onUpdataMusicNotifi(mm, false);
    }

    // //////////////////////////////Music MediaPlayListener////////////

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // 出错的时候
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // 播放完毕的时候
//		showToast("播放完毕，准备播放下一首！");
        currentTime = 0;
        // 改变通知栏
        musicNotifi.onUpdataMusicNotifi(mm, false);
        //改变MusicActivity
        sendModelToMusicActivity();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // 准备加载的时候
        resume();
        sendModelToMusicActivity();
    }

    // ////////////////////////////其他工具方法//////////////////////////////////

    /**
     * 发送Model给MusicActivity
     */
    private void sendModelToMusicActivity() {
        if (mm != null) {
            // 正在播放的歌曲实体
            if (mm.getSeconds() == 0) {
                mm.setSeconds(mp.getDuration() / 1000);
            }
            mActivityIntent.putExtra(MUSIC_SERVICE_TOACTIVITY_CODE, 41002);
            mActivityIntent.putExtra(MUSIC_SERVICE_TO_ACTIVITY_ISPLAY,
                    mp.isPlaying());
            mActivityIntent.putExtra(MUSIC_SERVICE_TO_ACTIVITY_NOWTIME,
                    mp.getDuration() - mp.getCurrentPosition());
            mActivityIntent.putExtra(MUSIC_SERVICE_TO_ACTIVITY_MODEL,
                    (Serializable) mm);
        } else {
            // 默认发送第一首歌信息
            mm = musics.get(0);
            mActivityIntent.putExtra(MUSIC_SERVICE_TOACTIVITY_CODE, 41001);
            mActivityIntent.putExtra(MUSIC_SERVICE_TO_ACTIVITY_ISPLAY,
                    mp.isPlaying());
            mActivityIntent.putExtra(MUSIC_SERVICE_TO_ACTIVITY_MODEL,
                    (Serializable) mm);
        }
        sendBroadcast(mActivityIntent);
    }

    // //////////////////////////////Music BroadCastReceiver////////////

    // 接收广播
    private class MusicBroadCast extends BroadcastReceiver {

        private int flag = 0, position = -1, kzhi = 0, musictype = 0;

        @Override
        public void onReceive(Context context, Intent intent) {

            // 2.MainActivity 控制
            flag = intent.getFlags();
            mainToService(intent);

            // 3.MusicNotification控制
            kzhi = intent.getIntExtra("type", -1);
            if (kzhi > 0) {
                musicNotificationService(kzhi);
            }
            // 4.MusicActivity 来的控制
            musictype = intent.getIntExtra(MUSIC_ACTIVITY_SERVICE_KEY, 0);
            if (musictype > 0) {
                musicActivityService(musictype);
            }

        }

        /**
         * 来自 MusicActivity 的控制
         *
         * @param musictype2
         */
        private void musicActivityService(int musictype2) {

//			showToast("musicActivityService 执行了 musictype2 :" + musictype2);
            switch (musictype2) {
                case 40001:
                    sendModelToMusicActivity();
                    break;
                case 40002:
                    //播放与暂停
                    playSong();
                    break;
                case 40003:
                    //下一曲
                    nextSong();
                    break;
                case 40004:
                    //上一曲
                    preSong();
                    break;
            }

        }

        /**
         * musicNotification 来的控制
         *
         * @param intent
         */
        private void musicNotificationService(int k) {

            switch (k) {
                case 30001:
                    // 播放
                    playSong();
                    break;
                case 30002:
                    // 下一首
                    nextSong();
                    break;
                case 30003:
                    // 关闭通知栏
                    musicNotifi.onCancelMusicNotifi();
                    onDestroy();
                    break;
            }

        }

        /**
         * 播放
         */
        private void playSong() {
            if (mp.isPlaying()) {
                pause();
            } else {
                if (currentTime > 0) {
                    resume();
                } else {
                    if (mm != null) {
                        play(mm.getUrl());
                    }
                }
            }
            sendModelToMusicActivity();
        }

        /**
         * 下一曲
         */
        private void nextSong() {
            currentTime = 0;
            if (position < 0) {
                position = 0;
            }
            if (mmSize > 0) {
                position++;
                if (position < mmSize) {
                    // 不超过长度
                    mm = musics.get(position);
                    play(mm.getUrl());
                } else {
                    // 超过长度 播放第一首
                    mm = musics.get(0);
                    play(mm.getUrl());
                }
            }

        }

        /**
         * 上一曲
         */
        private void preSong() {
            currentTime = 0;
            if (position < 0) {
                position = 0;
            }
            if (mmSize > 0) {
                position--;
                if (position >= 0) {
                    //不小于0
                    mm = musics.get(position);
                    play(mm.getUrl());
                } else {
                    // 超过长度 播放第一首，小于0 ,播放第一首
                    mm = musics.get(0);
                    play(mm.getUrl());
                }
            }
        }

        /**
         * MainActivity来的数据
         *
         * @param intent
         */
        private void mainToService(Intent intent) {
            if (MAIN_MUSIC_INTENT_FLAG == flag) {
                // 来自MainActivity 的操作
                position = intent.getIntExtra(MAIN_MUSIC_INTENT_KEY, -1);
                // showToast("3.来自MainActivity 问候 : " + position);
                if (position > -1) {
                    // 播放

                    if (musics != null) {
                        mm = musics.get(position);
                    } else {
//						showToast("4.MUSICS IS NULL");
                    }

                    if (mm != null) {
                        /**
                         * 1.播放音乐 2.更新状态栏 3.如果进度条运行的话，通知改变
                         */
                        play(mm.getUrl());

                    } else {
//						showToast("5.musics 数据去哪里了！");
                    }
                } else {
//					showToast("6.这怎么可能发生呢？！");
                }
            } else {
//				showToast("不是MainActivity 来的数据");
            }
        }

    }

}
