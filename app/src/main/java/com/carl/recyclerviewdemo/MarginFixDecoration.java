package com.carl.recyclerviewdemo;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author carl
 */
public class MarginFixDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "MarginFixDecoration";

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int adapterPos = parent.getChildAdapterPosition(view);
        RecyclerView.Adapter adapter = parent.getAdapter();
        int itemCount = adapter.getItemCount();
        int parentW = parent.getWidth();

        if (adapterPos == 0) {
            outRect.left = (int) (parentW * 0.15f);
        }

        if (adapterPos == itemCount - 1) {
            outRect.right = (int) (parentW * 0.15f);
        }
    }
}
