package cn.labelnet.adapter;

import cn.labelnet.maskmusic.R;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



public class MusicGridAdapter extends BaseAdapter {

	/**
	 * 主界面 分类信息，适配Adapter
	 */
	private SparseArray<String> gridItems;
	private Context context;
	private ViewHolder holder = null;
	private int[] ids = { R.drawable.mingyao, R.drawable.xiaoliang,
			R.drawable.china, R.drawable.oumei, R.drawable.hongkang,
			R.drawable.hanguo, R.drawable.riben, R.drawable.yaogun };

	public void setGridItems(SparseArray<String> gridItems) {
		this.gridItems = gridItems;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return gridItems.size() > 0 ? gridItems.size() : 0;
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return gridItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.list_item_main_gridview, parent, false);
			holder = new ViewHolder(convertView);
		}
		holder.tv_item_main_grid.setText(gridItems.get(position) + "");
		holder.iv_item_main_gird.setImageResource(ids[position]);
		return convertView;
	}

	private class ViewHolder {
		/**
		 * ViewHolder
		 */
		public ImageView iv_item_main_gird;
		public TextView tv_item_main_grid;

		public ViewHolder(View convertView) {
			iv_item_main_gird = (ImageView) convertView
					.findViewById(R.id.iv_item_main_gird);
			tv_item_main_grid = (TextView) convertView
					.findViewById(R.id.tv_item_main_grid);
		}
	}

}
