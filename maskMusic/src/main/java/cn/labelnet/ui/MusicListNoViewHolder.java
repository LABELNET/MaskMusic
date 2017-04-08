package cn.labelnet.ui;

import cn.labelnet.event.MusicListRecyclerOnItemClick;
import cn.labelnet.maskmusic.R;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * 无图 ViewHolder
 * 作者 ：原明卓
 * 时间 ：2015年12月11日 上午10:20:25
 * 描述 ：TODO
 */
public class MusicListNoViewHolder extends ViewHolder implements OnClickListener {

	private TextView list_item_play,tv_item_name,tv_item_singer,tv_item_heart;
	private MusicListRecyclerOnItemClick itemClick;
	
	public TextView getList_item_play() {
		return list_item_play;
	}

	public TextView getTv_item_name() {
		return tv_item_name;
	}

	public TextView getTv_item_singer() {
		return tv_item_singer;
	}

	public MusicListNoViewHolder(View itemView,MusicListRecyclerOnItemClick itemClick) {
		super(itemView);
		this.itemClick=itemClick;
		initView(itemView);
	}

	private void initView(View itemView) {
		list_item_play=(TextView) itemView.findViewById(R.id.list_item_play);
		tv_item_name=(TextView) itemView.findViewById(R.id.tv_item_name);
		tv_item_singer=(TextView) itemView.findViewById(R.id.tv_item_singer);
		tv_item_heart=(TextView) itemView.findViewById(R.id.tv_item_heart);
		itemView.setOnClickListener(this);
	}

	public TextView getTv_item_heart() {
		return tv_item_heart;
	}
	
	@Override
	public void onClick(View v) {
		itemClick.onRecyclerItemClick(getPosition());
	}

}
