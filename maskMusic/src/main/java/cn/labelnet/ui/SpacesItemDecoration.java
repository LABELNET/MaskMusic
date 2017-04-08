package cn.labelnet.ui;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 作者 ：原明卓
 * 时间 ：2015年12月11日 上午10:08:33
 * 描述 ：RecyclerView 绘制Item工具类
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
  private int space;

  public SpacesItemDecoration(int space) {
    this.space = space;
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, 
      RecyclerView parent, RecyclerView.State state) {
    outRect.left = space;
    outRect.right = space;
    outRect.bottom = space;

    // Add top margin only for the first item to avoid double space between items
    if(parent.getChildPosition(view) == 0)
        outRect.top = space;
  }
}