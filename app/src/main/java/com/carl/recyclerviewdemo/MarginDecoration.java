package com.carl.recyclerviewdemo;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Decoration that adds margin item views of RecyclerView. If you want to refresh margin settings,
 * try calling {@link RecyclerView#invalidateItemDecorations()}
 *
 * @author carl
 */
public class MarginDecoration extends RecyclerView.ItemDecoration {


    private int mMarginHead;
    private int mMarginTail;
    private int mMarginLeft;
    private int mMarginTop;
    private int mMarginRight;
    private int mMarginBottom;

    public MarginDecoration() {
    }

    public int getMarginHead() {
        return mMarginHead;
    }

    public void setMarginHead(int marginHead) {
        mMarginHead = marginHead;
    }

    public int getMarginTail() {
        return mMarginTail;
    }

    public void setMarginTail(int marginTail) {
        mMarginTail = marginTail;
    }

    public int getMarginLeft() {
        return mMarginLeft;
    }

    public void setMarginLeft(int marginLeft) {
        mMarginLeft = marginLeft;
    }

    public int getMarginTop() {
        return mMarginTop;
    }

    public void setMarginTop(int marginTop) {
        mMarginTop = marginTop;
    }

    public int getMarginRight() {
        return mMarginRight;
    }

    public void setMarginRight(int marginRight) {
        mMarginRight = marginRight;
    }

    public int getMarginBottom() {
        return mMarginBottom;
    }

    public void setMarginBottom(int marginBottom) {
        mMarginBottom = marginBottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int adapterPos = parent.getChildAdapterPosition(view);
        RecyclerView.Adapter adapter = parent.getAdapter();
        int itemCount = adapter.getItemCount();

        outRect.left = mMarginLeft;
        outRect.top = mMarginTop;
        outRect.right = mMarginRight;
        outRect.bottom = mMarginBottom;
        if (adapterPos == 0) {
            outRect.left += mMarginHead;
        }

        if (adapterPos == itemCount - 1) {
            outRect.right += mMarginTail;
        }
    }
}
