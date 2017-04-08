package cn.labelnet.ui;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.labelnet.event.MusicListRecyclerOnItemClick;
import cn.labelnet.maskmusic.R;

/**
 * 有图 ViewHolder 作者 ：原明卓 时间 ：2015年12月11日 上午10:19:05 描述 ：TODO
 */
public class MusicListBigViewHolder extends ViewHolder implements
		OnClickListener {

	private ImageView mlist_iv_music_songpic;
	private TextView mlist_tv_music_songname, mlist_tv_music_singer;
	private MusicListRecyclerOnItemClick itemClick;

	

	public ImageView getMlist_iv_music_songpic() {
		return mlist_iv_music_songpic;
	}

	public TextView getMlist_tv_music_songname() {
		return mlist_tv_music_songname;
	}

	public TextView getMlist_tv_music_singer() {
		return mlist_tv_music_singer;
	}

	public MusicListBigViewHolder(View itemView,MusicListRecyclerOnItemClick itemClick) {
		super(itemView);
		this.itemClick=itemClick;
		initView(itemView);
	}

	private void initView(View itemView) {
		mlist_iv_music_songpic = (ImageView) itemView
				.findViewById(R.id.mlist_iv_music_songpic);
		mlist_tv_music_songname = (TextView) itemView
				.findViewById(R.id.mlist_tv_music_songname);
		mlist_tv_music_singer = (TextView) itemView
				.findViewById(R.id.mlist_tv_music_singer);
		itemView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		itemClick.onRecyclerItemClick(getPosition());
	}

}
