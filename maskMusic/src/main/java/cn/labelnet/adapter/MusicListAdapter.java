package cn.labelnet.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.labelnet.maskmusic.R;
import cn.labelnet.model.MusicModel;

public class MusicListAdapter extends BaseAdapter {

	/**
	 * 主页 listview 音乐列表
	 * 在MainListViewFragment中实现，实现初始化界面适配
	 */
	private List<MusicModel> list;
	private Context context;
	private ViewHolder holder = null;

	public MusicListAdapter(List<MusicModel> list, Context content) {
		this.list = list;
		this.context = content;
	}

	@Override
	public int getCount() {
		return list.size() > 0 ? list.size() : 0;
	}

	@Override
	public MusicModel getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.list_item_main_layout, parent, false);
			holder = new ViewHolder(convertView);
		}

		MusicModel musicModel = list.get(position);
		String songName = musicModel.getSongname() != null ? musicModel
				.getSongname() : "什么东东";
		holder.tv_item_name.setText(songName);
		String singerName = musicModel.getSingername() != null ? musicModel
				.getSingername() : "未知";
		position += 1;
		String num = position >= 10 ? (position + "") : ("0" + position);
		holder.list_item_play.setText(num);
		holder.tv_item_singer.setText(singerName);
		return convertView;
	}

	class ViewHolder {
		public TextView tv_item_name;
		public TextView tv_item_singer, list_item_play;

		public ViewHolder(View convertView) {
			tv_item_name = (TextView) convertView
					.findViewById(R.id.tv_item_name);
			tv_item_singer = (TextView) convertView
					.findViewById(R.id.tv_item_singer);
			list_item_play = (TextView) convertView
					.findViewById(R.id.list_item_play);
		}
	}

}
