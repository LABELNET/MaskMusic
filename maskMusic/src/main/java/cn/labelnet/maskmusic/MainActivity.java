package cn.labelnet.maskmusic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import cn.labelnet.adapter.MusicGridAdapter;
import cn.labelnet.event.MainToFragmentRefrsh;
import cn.labelnet.fragment.MainListViewFragment;
import cn.labelnet.framework.MusicService;
import cn.labelnet.model.MusicModel;
import cn.labelnet.net.VolleyHttpPath;
import cn.labelnet.net.VolleyHttpRequest;
import cn.labelnet.util.ViewUtil;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;

public class MainActivity extends Activity implements MainToFragmentRefrsh,
		OnClickListener {

	// 滑动view
	private ScrollView main_scroll_view;
	private RelativeLayout main_listview_fragement;
	private LayoutParams main_listview_parames;

	// flipper
	private ViewFlipper main_view_flipper;
	private View flipperView = null;
	private TextView flipperTv = null;
	private ImageView flipperIv = null;
	private TextView tv_list_wen = null;

	// 手势判断
	private GestureDetector gestureDetector;

	// net
	private ImageListener imageListener = null;

	// Fragment
	private FragmentManager mainFragmentManger = null;
	private MainListViewFragment mainListViewFrag = null;
	private FragmentTransaction mainListViewTrans = null;

	// GridView
	private GridView main_gridview;
	private SparseArray<String> gridItems = new SparseArray<String>();
	private Map<String, Integer> maps = new HashMap<String, Integer>();

	// Notification
	private final String MUSIC_INTENT_KEY = "musics";
	private final int MUSIC_INTENT_FLAG = 20001;

	// 给MusicService的广播
	private final String MAIN_ACTIVIY_ACTION = "mainActivity.To.MusicService";
	private final String MAIN_MUSIC_INTENT_KEY = "mIntent";
	private Intent musicIntent = null;

	// 基本控件
	private ImageView iv_main_music, iv_main_search;
	private TextView tv_main_more;

	// 滚动条
	private SwipeRefreshLayout swiperefresh_wei;
	private boolean isRefresh = true;
	

	// 数据
	private String[] strs = { "听温暖的音乐，看感人的电影", "赏宁静的风景，交暖心的朋友",
			"音乐，开心时入耳、伤心时入心", "时而不靠谱，时而不着调" };
	private List<MusicModel> musicModels = new ArrayList<MusicModel>();
	
	private boolean isback=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initData();
		initView();
		initRefresh();
		initFrameWork();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.d("MaskMusic", "requestCode : " + requestCode + " : resultCode"
				+ resultCode);
		if (requestCode == 123) {
			if (musicModels.size() > 0 && musicModels != null) {
				startMusicService(musicModels);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
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
				isRefresh = true;
			}
		});
		swifRefushListener.onRefresh();

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
	 * 初始化 数据
	 */
	private void initData() {
		gridItems.put(0, getString(R.string.music_fenlei_mingyao));
		gridItems.put(1, getString(R.string.msuic_fenlei_xiaoliang));
		gridItems.put(2, getString(R.string.music_fenlei_china));
		gridItems.put(3, getString(R.string.music_fenlei_oumei));
		gridItems.put(4, getString(R.string.music_fenlei_hangkang));
		gridItems.put(5, getString(R.string.music_fenlei_hanguo));
		gridItems.put(6, getString(R.string.music_fenlei_riben));
		gridItems.put(7, getString(R.string.music_fenlei_yaogun));
		getFenlei();
	}

	public void getFenlei() {
		maps.put(getString(R.string.music_fenlei_mingyao), 18);
		maps.put(getString(R.string.msuic_fenlei_xiaoliang), 23);
		maps.put(getString(R.string.music_fenlei_china), 5);
		maps.put(getString(R.string.music_fenlei_oumei), 3);
		maps.put(getString(R.string.music_fenlei_hangkang), 6);
		maps.put(getString(R.string.music_fenlei_hanguo), 16);
		maps.put(getString(R.string.music_fenlei_riben), 17);
		maps.put(getString(R.string.music_fenlei_yaogun), 19);
		// 热歌 26
	}

	/**
	 * 初始化 View
	 */
	private void initView() {
		iv_main_search = (ImageView) findViewById(R.id.iv_main_search);
		iv_main_search.setOnClickListener(this);
		tv_main_more = (TextView) findViewById(R.id.tv_main_more);
		tv_main_more.setOnClickListener(this);
		//
		iv_main_music = (ImageView) findViewById(R.id.iv_main_music);
		iv_main_music.setOnClickListener(this);

		// 收拾判断
		gestureDetector = new GestureDetector(this,
				new gestureDetectorListener());

		// GridView
		main_gridview = (GridView) findViewById(R.id.main_gridview);

		// Fragement 所在的布局
		main_listview_fragement = (RelativeLayout) findViewById(R.id.main_listview_fragement);
		// 实例化
		main_listview_parames = main_listview_fragement.getLayoutParams();

		// ViewFlipper
		main_view_flipper = (ViewFlipper) findViewById(R.id.main_view_flipper);
		main_scroll_view = (ScrollView) findViewById(R.id.main_scroll_view);
		initViewFlipper();

		// MainListViewFragment
		mainFragmentManger = getFragmentManager();
		mainListViewFrag = new MainListViewFragment();
		// 添加事件
		mainListViewFrag.setMainToFragmentRefrsh(this);
		// 添加到布局
		mainListViewTrans = mainFragmentManger.beginTransaction();
		mainListViewTrans.add(R.id.main_listview_fragement, mainListViewFrag);
		mainListViewTrans.commit();

		// GridView初始化分类信息
		MusicGridAdapter musicGridAdapter = new MusicGridAdapter();
		musicGridAdapter.setContext(this);
		musicGridAdapter.setGridItems(gridItems);
		main_gridview.setAdapter(musicGridAdapter);
		main_gridview.setOnItemClickListener(new main_gridviewListener());
	}

	/**
	 * 01.实现顶部ViewFlipper
	 * 
	 */
	private void initViewFlipper() {
		main_view_flipper.setInAnimation(this, R.drawable.main_fliper_in);
		main_view_flipper.setOutAnimation(this, R.drawable.main_fliper_out);
		main_view_flipper.setOnTouchListener(new viewFlipperListener());

		for (int i = 0; i < 4; i++) {
			flipperView = LayoutInflater.from(this).inflate(
					R.layout.list_item_main_flipper, main_scroll_view, false);
			flipperTv = (TextView) flipperView
					.findViewById(R.id.tv_list_item_num);
			flipperIv = (ImageView) flipperView
					.findViewById(R.id.iv_list_item_flipper);
			tv_list_wen = (TextView) flipperView.findViewById(R.id.tv_list_wen);
			tv_list_wen.setText(strs[i] + "");
			flipperIv.setTag(VolleyHttpPath.RANDOM_IMAGE_URL);
			flipperTv.setText((i + 1) + "/4");
			imageListener = ImageLoader.getImageListener(flipperIv,
					R.drawable.moren, R.drawable.moren_big);
			main_view_flipper.addView(flipperView);
			VolleyHttpRequest.Image_Loader(VolleyHttpPath.RANDOM_IMAGE_URL
					+ "?" + i, imageListener);
		}
		main_view_flipper.setFlipInterval(7000);
		main_view_flipper.startFlipping();
	}

	/**
	 * Toast
	 * 
	 * @param msg
	 *            消息
	 */
	private void showToast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * GirdView点击事件
	 */
	private class main_gridviewListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent(MainActivity.this,
					MusicListActivity.class);
			intent.putExtra("musictype", maps.get(gridItems.get(position)));
			startActivity(intent);
			finish();
		}
	}

	/**
	 * ViewFlipper 手势控制
	 *
	 */
	private class viewFlipperListener implements OnTouchListener {

		private int start = 0;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			gestureDetector.onTouchEvent(event);
			return true;
		}
	}

	/**
	 * 手势判断
	 *
	 */
	private class gestureDetectorListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e1.getX() - e2.getX() < 1000) {
				main_view_flipper.showPrevious();
			}
			return true;
		}
	}

	/**
	 * 作用 : 从 Fragment拿过来 总长度,后 设置 Fragment 所在布局的总高度
	 */
	@Override
	public void changeFragmentHeight(int size) {
		// 给listView 设置 高度
		main_listview_parames.height = ViewUtil.px2dip(this,
				ViewUtil.dip2px(this, 70) * size);
		main_listview_fragement.setLayoutParams(main_listview_parames);
	}

	@Override
	public void getMusicModelList(List<MusicModel> models) {
		if (musicModels.size() == 0) {
			musicModels.addAll(models);
		}
		startMusicService(models);
		// 关闭进度条
		swiperefresh_wei.setRefreshing(false);
		isRefresh = false;
	}

	private void startMusicService(List<MusicModel> models) {
		// 初始化 Service : 开启MUSIC服务
		Intent intent = new Intent(MainActivity.this, MusicService.class);
		intent.putExtra(MUSIC_INTENT_KEY, (Serializable) models);
		intent.addFlags(MUSIC_INTENT_FLAG);
		startService(intent);
	}

	@Override
	public void onListviewOnItemClickListener(int postion) {
		// listView点击事件
		// 发送广播
		musicIntent.putExtra(MAIN_MUSIC_INTENT_KEY, postion);
		sendBroadcast(musicIntent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_main_music:
			startActivity(new Intent(MainActivity.this, MusicActivity.class));
			break;
		case R.id.tv_main_more:
			Intent intent = new Intent(MainActivity.this,
					MusicListActivity.class);
			intent.putExtra("musictype", 26);
			startActivity(intent);
			break;
		case R.id.iv_main_search:
			// finish();
//			showToast("分享功能还未实现哦!");
			showShare();
			break;
		}
	}

	private void showShare() {
		ShareSDK.initSDK(getApplicationContext());
		OnekeyShare oks = new OnekeyShare();
		//下载地址
		oks.setInstallUrl("http://www.pgyer.com/apiv1/app/install?aId=d017cb7c061ef9a56efb139c098685d9&_api_key=38975cd846bcdb263616e7d0f628241e");
		oks.setExecuteUrl("http://www.pgyer.com/yuanmusic");
		oks.setText("音心音乐,不一样的试听!");
		oks.setImageUrl("http://7kttjt.com1.z0.glb.clouddn.com/image/view/app_icons/009c9a96c3f2f2d6c8a438b35bbb39cd/120");
		// 启动分享GUI
		oks.setSite(getString(R.string.app_name));
		oks.show(getApplicationContext());
	}

	/**
	 * 1.下拉刷新监听 下拉刷新
	 */
	class SwifRefushListener implements SwipeRefreshLayout.OnRefreshListener {

		@Override
		public void onRefresh() {
			if (!isRefresh) {
				swiperefresh_wei.setRefreshing(isRefresh);
			}
		}
	}

	@Override
	public void onBackPressed() {
		if(isback){
			isback=false;
			finish();
		}else{
			showToast("再按一次退出应用");
			isback=true;
		}
	}
	
	@Override
	public void onFailListener() {
		// 消失进度条
		swiperefresh_wei.setRefreshing(false);
		isRefresh = false;
	}

}
