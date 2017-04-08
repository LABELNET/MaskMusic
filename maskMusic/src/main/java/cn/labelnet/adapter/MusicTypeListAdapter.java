package cn.labelnet.adapter;

import java.util.List;
import java.util.Random;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import cn.labelnet.event.MusicListRecyclerOnItemClick;
import cn.labelnet.maskmusic.R;
import cn.labelnet.model.MusicModel;
import cn.labelnet.net.MusicAsyncGetUrl;
import cn.labelnet.net.MusicAsyncHandlerGetUrl;
import cn.labelnet.net.MusicRequest;
import cn.labelnet.net.VolleyHttpRequest;
import cn.labelnet.ui.MusicListBigViewHolder;
import cn.labelnet.ui.MusicListNoViewHolder;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;

/**
 * MusicListActivity 适配器 作者 ：原明卓 时间 ：2015年12月11日 上午10:33:42 描述 ：TODO
 */
public class MusicTypeListAdapter extends RecyclerView.Adapter<ViewHolder>
		implements MusicAsyncGetUrl {

	private List<MusicModel> musics = null;
	private Random random = new Random();
	private int r = 7;
	private Context context;
	private MusicModel mm = null;
	// 布局控制
	private MusicListBigViewHolder bigViewHolder = null;
	private MusicListNoViewHolder listNoViewHolder = null;
	private MusicListRecyclerOnItemClick itemClick;
	// 图片请求
	private MusicAsyncHandlerGetUrl musicAsyncHandlerGetUrl;
	private ImageListener imageListener = null;
	private MusicRequest musicRequest = null;
	private ImageView iv_list;
	//
	private String urlStr = null;

	public MusicTypeListAdapter(List<MusicModel> musics, Context context,
			MusicListRecyclerOnItemClick itemClick) {
		this.musics = musics;
		this.context = context;
		this.itemClick = itemClick;

		// 请求图片资源
		musicAsyncHandlerGetUrl = new MusicAsyncHandlerGetUrl();
		musicAsyncHandlerGetUrl.setMusicasyncGetUrl(this);
		musicRequest = new MusicRequest();
		musicRequest.setMusicAsyncHandler(musicAsyncHandlerGetUrl);
	}

	@Override
	public int getItemViewType(int position) {
		return position == 0 ? 0 : 1;
	}

	@Override
	public int getItemCount() {
		return musics.size() > 0 ? musics.size() : 0;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup vg, int flag) {
		ViewHolder holder = null;
		if (flag == 0) {
			// 有图的布局
			View itemView = LayoutInflater.from(context).inflate(
					R.layout.list_item_music_layout, vg, false);
			holder = new MusicListBigViewHolder(itemView, itemClick);
		} else {
			// 没图的布局
			View itemView = LayoutInflater.from(context).inflate(
					R.layout.list_item_music_no_layout, vg, false);
			holder = new MusicListNoViewHolder(itemView, itemClick);
		}

		return holder;
	}

	@Override
	public void onBindViewHolder(ViewHolder vh, int position) {
		mm = musics.get(position);
		if (mm != null) {
			// 不建议，待优化
			if (vh instanceof MusicListNoViewHolder) {
				// 无图适配
				listNoViewHolder = (MusicListNoViewHolder) vh;
				// Log.d("MaskMusic", "无图适配");
				listNoViewHolder.getList_item_play().setText("" + position);
				listNoViewHolder.getTv_item_singer()
						.setText(mm.getSingername());
				listNoViewHolder.getTv_item_name().setText(mm.getSongname());
				int heartNum = (mm.getSongid() + mm.getSingerid()) / 134;

				listNoViewHolder.getTv_item_heart().setText("" + heartNum);

			} else {
				// 有图适配
				bigViewHolder = (MusicListBigViewHolder) vh;
				bigViewHolder.getMlist_tv_music_singer().setText(
						mm.getSingername());
				bigViewHolder.getMlist_tv_music_songname().setText(
						mm.getSongname());
			
				if (urlStr == null) {
					iv_list = bigViewHolder.getMlist_iv_music_songpic();
					imageListener = ImageLoader.getImageListener(iv_list,
							R.drawable.moren, R.drawable.moren_big);
					musicRequest.setMusicAsyncHandler(musicAsyncHandlerGetUrl);
					musicRequest.requestStringData(mm.getSingername() + "");
				}
			}
		}
	}

	@Override
	public void getSongImageURL(String imgUrl) {
		urlStr = imgUrl;
		VolleyHttpRequest.Image_Loader(imgUrl, imageListener);
	}

}
