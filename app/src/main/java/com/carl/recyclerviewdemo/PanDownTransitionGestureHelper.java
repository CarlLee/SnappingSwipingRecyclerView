package com.carl.recyclerviewdemo;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author carl
 */
public class PanDownTransitionGestureHelper implements View.OnTouchListener {
    private static final float DEFAULT_GESTURE_DETECTION_RANGE = 20F;
    private static final String TAG = "PanDownHelper";
    private static final float SQRT_OF_TWO = (float) Math.sqrt(2.0D);
    private float mInitX;
    private float mInitY;
    private float mDx;
    private float mDy;
    private boolean mGestureInAction;
    private RecyclerView mRecyclerView;
    private View mSelectedView;

    public PanDownTransitionGestureHelper() {
    }

    public void attachToRecyclerView(RecyclerView rv, ViewOnTouchDelegate delegate) {
        if (delegate != null) {
            delegate.addOnTouchListener(this);
        } else {
            rv.setOnTouchListener(this);
        }
        this.mRecyclerView = rv;
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        int pointerIndex = MotionEventCompat.getActionIndex(e);
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitX = MotionEventCompat.getX(e, pointerIndex);
                mInitY = MotionEventCompat.getY(e, pointerIndex);
                mSelectedView = mRecyclerView.findChildViewUnder(mInitX, mInitY);
                break;
            case MotionEvent.ACTION_MOVE:
                float x = MotionEventCompat.getX(e, pointerIndex);
                float y = MotionEventCompat.getY(e, pointerIndex);
                mDx = x - mInitX;
                mDy = y - mInitY;

                if (mDx * mDx + mDy * mDy <
                        DEFAULT_GESTURE_DETECTION_RANGE * DEFAULT_GESTURE_DETECTION_RANGE) {
                    break;
                }

                if (mSelectedView == null) {
                    break;
                }

                if (!mGestureInAction && mDy > 0 && Math.abs(mDy) > Math.abs(mDx) * SQRT_OF_TWO) {
                    Log.d(TAG, "Pan in action");
                    mGestureInAction = true;
                    // Elevate selected view a little bit so that it renders on top of other views
//                    mSelectedView.setZ(0.1f);
                }
                if (mGestureInAction) {
                    handlePanAction(mSelectedView, mDx, mDy);
                }
                break;
            case MotionEvent.ACTION_UP:
                mGestureInAction = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                mGestureInAction = false;
                break;
        }
        return mGestureInAction;
    }

    private void handlePanAction(View selectedView, float dx, float dy) {
        float progress = dy / (mRecyclerView.getBottom() - mInitY);
        applyViewTransformation(selectedView, progress);
    }

    private void applyViewTransformation(View selectedView, float progress) {
        if (selectedView == null || progress < 0 || progress > 1.0F) {
            return;
        }
        float targetBottom = mRecyclerView.getBottom();
        float targetRight = mRecyclerView.getRight();
        float targetScale = 0.6f;

        float curScale = MathUtils.lerp(1, targetScale, progress);

        float startRight = selectedView.getRight();
        float startBottom = selectedView.getBottom();
        float curRight = MathUtils.lerp(startRight, targetRight, progress);
        float curBottom = MathUtils.lerp(startBottom, targetBottom, progress);
        float curX = curRight - (float) selectedView.getWidth() * curScale;
        float curY = curBottom - (float) selectedView.getHeight() * curScale;

        Log.d(TAG, String.format("startRight: %f startBottom: %f curRight: %f curBottom %f " +
                "targetRight: %f targetBottom: %f progress: %f curScale: %f curX: %f " +
                "curY: %f view.getWidth(): %d view.getHeight(): %d",
                startRight, startBottom, curRight, curBottom, targetRight, targetBottom,
                progress, curScale, curX, curY, selectedView.getWidth(), selectedView.getHeight()));

        selectedView.setScaleX(curScale);
        selectedView.setScaleY(curScale);
        selectedView.setX(curX);
        selectedView.setY(curY);
    }
}
