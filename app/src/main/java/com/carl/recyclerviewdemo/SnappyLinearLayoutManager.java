package com.carl.recyclerviewdemo;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.IntDef;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Layout manager that supports snapping into position
 * TODO: Add vertical support
 * @author carl
 */
public class SnappyLinearLayoutManager extends LinearLayoutManager implements SnappyScrollCalculator {
    @IntDef({
            SnappyLinearSmoothScroller.SNAP_CENTER,
            SnappyLinearSmoothScroller.SNAP_START,
            SnappyLinearSmoothScroller.SNAP_END,
            SnappyLinearSmoothScroller.SNAP_NONE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface SnapMethod {
    }

    private static final float DEFAULT_FLING_VELOCITY_DISTANCE_RATIO = 0.18F;
    private static final float DEFAULT_FLING_VELOCITY_RATIO = 0.7F;
    private static final float MAX_MILLIS_PER_INCH_ALLOWED = 600f;
    private static final String TAG = "SnappyManager";
    private int mDensityDpi;
    /*
     * Speed for scroller when scrolling to target position
     */
    private float mScrollerSpeed;
    private float mFlingVelocityRatio = DEFAULT_FLING_VELOCITY_RATIO;
    private int mSnapMethod = SnappyLinearSmoothScroller.SNAP_CENTER;

    public SnappyLinearLayoutManager(Context context) {
        super(context);
        this.mDensityDpi = context.getResources().getDisplayMetrics().densityDpi;
    }

    public SnappyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        this.mDensityDpi = context.getResources().getDisplayMetrics().densityDpi;
    }

    public SnappyLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mDensityDpi = context.getResources().getDisplayMetrics().densityDpi;
    }

    /**
     * Sets the ratio between scroll velocity and fling velocity
     * @param flingVelocityRatio the ratio used to calculate fling velocity
     */
    public void setFlingVelocityRatio(float flingVelocityRatio) {
        mFlingVelocityRatio = flingVelocityRatio;
    }

    /**
     * Set method used when calculating snapping positions
     *
     * @param snapMethod Constants that starts with SNAP_ in SnappyLinearSmoothScroller
     */
    public void setSnapMethod(@SnapMethod int snapMethod) {
        mSnapMethod = snapMethod;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        SnappyLinearSmoothScroller linearSmoothScroller = new SnappyLinearSmoothScroller(recyclerView.getContext());
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    @Override
    public int computeScrollToItemIndex(int velocityX, int velocityY) {
        if (getOrientation() == HORIZONTAL) {
            mScrollerSpeed = Math.abs(convertPixelPerSecondToMillisPerInch(velocityX)
                    * mFlingVelocityRatio);
            mScrollerSpeed = Math.min(mScrollerSpeed, MAX_MILLIS_PER_INCH_ALLOWED);
            Log.d(TAG, "mScrollerSpeed :" + mScrollerSpeed);
            return computeScrollX(velocityX);
        }
        return 0;
    }


    private int computeScrollX(int velocityX) {
        int itemCount = getItemCount();
        if (itemCount == 0) return 0;
        int distance = computeScrollDistance(velocityX);

        int first = findFirstVisibleItemPosition();
        View firstView = findViewByPosition(first);
        int firstViewLeft = firstView.getLeft();
        int totalDistance = distance - firstViewLeft;

        int childWidth = firstView.getWidth();
        int indexOffset = Math.round((float) totalDistance / (float) childWidth);
        int targetIndex = first + indexOffset;
        targetIndex = Math.max(0, targetIndex);
        targetIndex = Math.min(targetIndex, itemCount - 1);
        Log.d(TAG, String.format("distance: %d, totalDistance: %d, childWidth: %d, indexOffset: %d", distance,
                totalDistance, childWidth, indexOffset));
        return targetIndex;
    }

    private int computeScrollDistance(int velocityX) {
        return (int) (velocityX * DEFAULT_FLING_VELOCITY_DISTANCE_RATIO);
    }

    private float convertPixelPerSecondToMillisPerInch(int pixelPerSecond) {
        return (float) mDensityDpi / (float) pixelPerSecond * 1000;
    }

    public class SnappyLinearSmoothScroller extends LinearSmoothScroller {
        public static final int SNAP_START = 0;
        public static final int SNAP_END = 1;
        public static final int SNAP_CENTER = 2;
        public static final int SNAP_NONE = 3;

        public SnappyLinearSmoothScroller(Context context) {
            super(context);
        }

        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return SnappyLinearLayoutManager.this
                    .computeScrollVectorForPosition(targetPosition);
        }

        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            return mScrollerSpeed / displayMetrics.densityDpi;
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            switch (mSnapMethod) {
                case SNAP_START:
                    return boxStart - viewStart;
                case SNAP_END:
                    return boxEnd - viewEnd;
                case SNAP_CENTER:
                    int boxMid = boxStart + (boxEnd - boxStart) / 2;
                    int viewMid = viewStart + (viewEnd - viewStart) / 2;
                    return boxMid - viewMid;
                case SNAP_NONE:
                    final int dtStart = boxStart - viewStart;
                    if (dtStart > 0) {
                        return dtStart;
                    }
                    final int dtEnd = boxEnd - viewEnd;
                    if (dtEnd < 0) {
                        return dtEnd;
                    }
                    break;
                default:
                    throw new IllegalArgumentException("snap preference should be one" +
                            " of the"
                            + " constants defined in SnapperLinearLayoutManager, " +
                            "starting with SNAP_");
            }
            return 0;
        }

        @Override
        protected void onStop() {
            super.onStop();
            Log.d(TAG, "SnappyLinearSmoothScroller.onStop");
        }
    }
}
