package cn.labelnet.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import cn.labelnet.adapter.MusicListAdapter;
import cn.labelnet.event.MainToFragmentRefrsh;
import cn.labelnet.maskmusic.R;
import cn.labelnet.model.MusicModel;
import cn.labelnet.net.MusicAsync;
import cn.labelnet.net.MusicAsyncHandler;
import cn.labelnet.net.MusicRequest;

public class MainListViewFragment extends Fragment implements MusicAsync {

	/**
	 * 热歌榜单实现 MainActivity界面内容填充
	 */

	// 数据请求
	private MusicRequest musicRequest = null;
	private MusicAsyncHandler musicHandler = null;

	// listview
	private ListView main_list_view;
	// adapter
	private MusicListAdapter musicAdapter;
	private List<MusicModel> mmsList = new ArrayList<MusicModel>();

	// 接口 : 给 Main传递参数
	private MainToFragmentRefrsh mainToFragmentRefrsh;

	// 给Fragment 添加此事件
	public void setMainToFragmentRefrsh(
			MainToFragmentRefrsh mainToFragmentRefrsh) {
		this.mainToFragmentRefrsh = mainToFragmentRefrsh;
	}

	public android.view.View onCreateView(android.view.LayoutInflater inflater,
			android.view.ViewGroup container,
			android.os.Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_main_listview_layout,
				container, false);

		return view;
	};

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// 初始化数据
		initData();
		// 初始化View
		initView(view);
		// 数据请求
		musicRequest.requestStringData(5);

	}

	/**
	 * 初始化View
	 * 
	 * @param view
	 */
	private void initView(View view) {
		main_list_view = (ListView) view.findViewById(R.id.main_list_view);
		musicAdapter = new MusicListAdapter(mmsList, getActivity());
		main_list_view.setAdapter(musicAdapter);
		main_list_view.setOnItemClickListener(new Main_list_viewListener());
	}

	/**
	 * 初始化数据请求
	 */
	private void initData() {
		musicHandler = new MusicAsyncHandler();
		musicHandler.setMAsync(this);
		musicRequest = new MusicRequest();
		musicRequest.setMusicAsyncHandler(musicHandler);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			musicRequest.requestStringData(5);
		}
	}

	@Override
	public void onSuccess(List<MusicModel> mms) {
		try {
			// 给MainActivity返回size
			mainToFragmentRefrsh.changeFragmentHeight(mms.size());
			mainToFragmentRefrsh.getMusicModelList(mms);
			// 请求成功
			mmsList.addAll(mms);
			musicAdapter.notifyDataSetChanged();
		} catch (Exception e) {
		}

	}

	@Override
	public void onFail(String msg) {
		// 请求失败
		showToast(msg);
		mainToFragmentRefrsh.onFailListener();
	}

	/**
	 * Toast
	 * 
	 * @param msg
	 *            消息
	 */
	private void showToast(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	}

	// listview点击事件
	private class Main_list_viewListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			mainToFragmentRefrsh.onListviewOnItemClickListener(position);
		}
	}

}
