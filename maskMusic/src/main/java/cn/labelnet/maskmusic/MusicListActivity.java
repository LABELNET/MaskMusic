package cn.labelnet.maskmusic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.labelnet.adapter.MusicTypeListAdapter;
import cn.labelnet.event.MusicListRecyclerOnItemClick;
import cn.labelnet.framework.MusicService;
import cn.labelnet.maskmusic.MainActivity.SwifRefushListener;
import cn.labelnet.model.MusicModel;
import cn.labelnet.net.MusicAsync;
import cn.labelnet.net.MusicAsyncHandler;
import cn.labelnet.net.MusicRequest;
import cn.labelnet.ui.SpacesItemDecoration;

public class MusicListActivity extends Activity implements MusicAsync,
		MusicListRecyclerOnItemClick, OnClickListener {

	/**
	 * 控件
	 */
	private TextView tv_list_type,tv_list_item_show;
	private RecyclerView list_recycler_view;
	private MusicTypeListAdapter musicTypeAdapter;
	private ImageView tv_list_return;
	private SparseArray<String> maps = new SparseArray<String>();
	
	private final int REQUEST_CODE=123;

	/**
	 * 网络数据
	 */
	private MusicRequest musicRequest = null;
	private MusicAsyncHandler musicHandler = null;

	/**
	 * 数据保留
	 */
	private List<MusicModel> musics = new ArrayList<MusicModel>();
	private final String MUSIC_INTENT_KEY = "musics";
	private final int MUSIC_INTENT_FLAG = 20001;

	/**
	 * 和MusicService 通信
	 */
	private Intent musicIntent = null;
	private final String MAIN_ACTIVIY_ACTION = "mainActivity.To.MusicService";
	private final String MAIN_MUSIC_INTENT_KEY = "mIntent";
	
	// 滚动条
	private SwipeRefreshLayout swiperefresh_wei;
	private int typeid = 5;
	
	//
	private Handler handler=new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_list);
		initView();
		initData();
		initFrameWork();
		initTypes();
		initRefresh();
		typeid=getIntent().getIntExtra("musictype", 5);
		// 进行数据请求
		musicRequest.requestStringData(typeid);
		tv_list_type.setText(maps.get(typeid));
//		Log.d("MaskMusic", typeid + "typeid");
	}
	
	/**
	 * 初始化进度条
	 */
	private void initRefresh() {

		swiperefresh_wei = (SwipeRefreshLayout) findViewById(R.id.swiperefresh_wei);
		SwifRefushListener swifRefushListener = new SwifRefushListener();
		swiperefresh_wei.setColorSchemeResources(R.color.app_color_zhu,
				R.color.app_color_zi, R.color.text_color_black,
				R.color.text_color_main);
		swiperefresh_wei.setSize(SwipeRefreshLayout.LARGE);
		// 设置进度条的位置
		swiperefresh_wei.setProgressViewEndTarget(true, 800);
		// 设置监听
		swiperefresh_wei.setOnRefreshListener(swifRefushListener);
		// 首次加载开启刷新
		swiperefresh_wei.post(new Runnable() {
			@Override
			public void run() {
				swiperefresh_wei.setRefreshing(true);
			}
		});
		swifRefushListener.onRefresh();
	}


	/**
	 * 初始化分类信息
	 */
	private void initTypes() {
		maps.put(18, getString(R.string.music_fenlei_mingyao));
		maps.put(23, getString(R.string.msuic_fenlei_xiaoliang));
		maps.put(5,getString(R.string.music_fenlei_china));
		maps.put(3,getString(R.string.music_fenlei_oumei));
		maps.put(6,getString(R.string.music_fenlei_hangkang));
		maps.put(16,getString(R.string.music_fenlei_hanguo));
		maps.put(17,getString(R.string.music_fenlei_riben));
		maps.put(19,getString(R.string.music_fenlei_yaogun));
		maps.put(26,getString(R.string.music_list_resou));
	}

	/**
	 * 初始化组件
	 */
	private void initFrameWork() {
		// 初始化MusicService 广播组件Intent
		musicIntent = new Intent();
		// 设置识别Action
		musicIntent.setAction(MAIN_ACTIVIY_ACTION);
		// 设置来源
		musicIntent.addFlags(MUSIC_INTENT_FLAG);

	}

	/**
	 * 初始化网络请求数据
	 */
	private void initData() {
		// 请求基本数据
		musicHandler = new MusicAsyncHandler();
		musicHandler.setMAsync(this);
		musicRequest = new MusicRequest();
		musicRequest.setMusicAsyncHandler(musicHandler);
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		tv_list_item_show=(TextView) findViewById(R.id.tv_list_item_show);
		tv_list_return = (ImageView) findViewById(R.id.tv_list_return);
		tv_list_return.setOnClickListener(this);
		tv_list_type = (TextView) findViewById(R.id.tv_list_type);
		list_recycler_view = (RecyclerView) findViewById(R.id.list_recycler_view);
		// 设置是否固定长度
		list_recycler_view.setHasFixedSize(true);
		// 添加样式
		list_recycler_view.setLayoutManager(new LinearLayoutManager(this,
				LinearLayoutManager.VERTICAL, false));
		// 添加item动画
		list_recycler_view.setItemAnimator(new DefaultItemAnimator());
		// 添加item分割线
		list_recycler_view.addItemDecoration(new SpacesItemDecoration(2));
		// 添加适配器
		musicTypeAdapter = new MusicTypeListAdapter(musics, this, this);
		list_recycler_view.setAdapter(musicTypeAdapter);
	}

	@Override
	public void onSuccess(List<MusicModel> mms) {
		// Log.d("MaskMusic", "MMS : " + mms);
		// 请求成功
		synchronized (musics) {
			musics.clear();
			musics.addAll(mms);
			musicTypeAdapter.notifyDataSetChanged();
		}
		Intent intent = new Intent(MusicListActivity.this, MusicService.class);
		intent.putExtra(MUSIC_INTENT_KEY, (Serializable) musics);
		intent.addFlags(MUSIC_INTENT_FLAG);
		startService(intent);
		hideProgress();
	}

	/**
	 * 隐藏进度条 
	 * 隐藏提示
	 */
	private void hideProgress() {
		swiperefresh_wei.setRefreshing(false);
		tv_list_item_show.setVisibility(View.GONE);
	}

	@Override
	public void onFail(String msg) {
		// 请求失败
		showToast(msg);
		hideProgress();
	}

	/**
	 * Toast
	 * 
	 * @param msg
	 */
	private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 点击事件
	 */
	@Override
	public void onRecyclerItemClick(int position) {
		// Log.d("MaskMusic", "Position :"+position);
		musicIntent.putExtra(MAIN_MUSIC_INTENT_KEY, position);
		sendBroadcast(musicIntent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_list_return:
			startActivityForResult(new Intent(MusicListActivity.this,MainActivity.class),REQUEST_CODE);
			finish();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		startActivityForResult(new Intent(MusicListActivity.this,MainActivity.class),REQUEST_CODE);
		finish();
	}
	
	/**
	 * 1.下拉刷新监听 下拉刷新
	 */
	class SwifRefushListener implements SwipeRefreshLayout.OnRefreshListener {

		@Override
		public void onRefresh() {
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					//隐藏进度条
					hideProgress();
				}
			}, 3000);
		}
	}
	

}
