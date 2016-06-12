package com.carl.recyclerviewdemo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carl
 */
public class SwipeGestureHelper implements View.OnTouchListener {

    private static final String TAG = "SwipeGestureHelper";
    public static final float DEFAULT_SWIPE_THRESHOLD_RATIO = 0.4F;
    private static final float DEFAULT_SWIPE_THRESHOLD_SPEED_DP_PER_SECOND = 800F;

    private RecyclerView mRecyclerView;
    private boolean mLongPressInAction;
    private final GestureDetectorCompat mGestureDetector;
    private float mTouchStartX;
    private float mTouchStartY;
    private int mActivePointerIndex;
    private VelocityTracker mVelocityTracker;

    private View mSelectedView;
    private int mSelectedAdapterPos;
    private AnimatorHolder mSelectedAnimatorHolder;
    private AnimatorHolder mPrevAnimatorHolder;
    private AnimatorHolder mNextAnimatorHolder;
    private float mDy;
    private float mSwipeThresholdRatio = DEFAULT_SWIPE_THRESHOLD_RATIO;
    private float mSwipeThresholdSpeedDpPerSecond = DEFAULT_SWIPE_THRESHOLD_SPEED_DP_PER_SECOND;
    private OnSwipeListener mOnSwipeListener;

    private List<Animator> mRunningAnimators = new ArrayList<>();

    public SwipeGestureHelper(Context context) {
        mGestureDetector = new GestureDetectorCompat(context, new LongPressGestureListener());
        mGestureDetector.setIsLongpressEnabled(true);
        mLongPressInAction = false;
        mSelectedAnimatorHolder = new AnimatorHolder();
        mPrevAnimatorHolder = new AnimatorHolder();
        mNextAnimatorHolder = new AnimatorHolder();
    }

    public float getSwipeThresholdRatio() {
        return mSwipeThresholdRatio;
    }

    public void setSwipeThresholdRatio(float threshold) {
        this.mSwipeThresholdRatio = threshold;
    }

    public float getSwipeThresholdSpeedDpPerSecond() {
        return mSwipeThresholdSpeedDpPerSecond;
    }

    public void setSwipeThresholdSpeedDpPerSecond(float swipeThresholdSpeedDpPerSecond) {
        mSwipeThresholdSpeedDpPerSecond = swipeThresholdSpeedDpPerSecond;
    }

    public void setOnSwipeListener(OnSwipeListener listener) {
        this.mOnSwipeListener = listener;
    }

    public void attachToRecyclerView(RecyclerView rv, ViewOnTouchDelegate delegate) {
        if (delegate != null) {
            delegate.addOnTouchListener(this);
        } else {
            rv.setOnTouchListener(this);
        }
        rv.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                resetViewProperties(view);
                view.clearAnimation();
            }
        });
        this.mRecyclerView = rv;
    }

    private void recoverViews() {
        recoverAnimatorHolder(mSelectedAnimatorHolder);
        recoverAnimatorHolder(mPrevAnimatorHolder);
        recoverAnimatorHolder(mNextAnimatorHolder);
    }

    private void recoverAnimatorHolder(AnimatorHolder holder) {
        View v = holder.getView();
        if (v != null) {
            float currentScale = v.getScaleX();

            float translationY = v.getTranslationY();
            List<Animator> recoverAnimatorList = makeScaleAnimatorList(v, currentScale, 1.0f, 300);
            if (translationY != 0) {
                ObjectAnimator translationYAnimator =
                        ObjectAnimator.ofFloat(v, View.TRANSLATION_Y, translationY, 0);
                recoverAnimatorList.add(translationYAnimator);
            }

            AnimatorSet recoverAnimatorSet = new AnimatorSet();
            recoverAnimatorSet.playTogether(recoverAnimatorList);
            holder.playAnimator(recoverAnimatorSet);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        if (mLongPressInAction) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP: {
                    mLongPressInAction = false;

                    mVelocityTracker.computeCurrentVelocity(1000);
                    float pixelPerSecondY = VelocityTrackerCompat.getYVelocity(mVelocityTracker,
                            mActivePointerIndex);
                    float density = v.getContext().getResources().getDisplayMetrics().density;
                    float dpPerSecondY = pixelPerSecondY / density;

                    float ratioY = mDy / (float) mRecyclerView.getHeight();

                    Log.d(TAG, String.format("Up event: dpPerSecondY: %f, ratioY: %f",
                            dpPerSecondY, ratioY));
                    if (Math.abs(dpPerSecondY) >= mSwipeThresholdSpeedDpPerSecond) {
                        onSwipe(mRecyclerView, mSelectedAdapterPos, mDy);
                    } else if (Math.abs(ratioY) > mSwipeThresholdRatio) {
                        onSwipe(mRecyclerView, mSelectedAdapterPos, mDy);
                    } else {
                        recoverViews();
                    }
                    recycleVelocityTracker();
                    mDy = 0F;
                    // Intercept this event event if it's the last event of long press action
                    return true;
                }
                case MotionEvent.ACTION_MOVE: {
                    float x = e.getX();
                    float y = e.getY();
                    mDy = y - mTouchStartY;
                    mSelectedView.setTranslationY(mDy);
                    mVelocityTracker.addMovement(e);
                    break;
                }
            }
        }
//        Log.d(TAG, "onTouch: " + e);
        return mLongPressInAction || mRunningAnimators.size() > 0;
    }

    private void onSwipe(final RecyclerView recyclerView, final int adapterPos, final float dy) {
        Log.d(TAG, String.format("onSwipe: %s, %d, %f", recyclerView, adapterPos, dy));
        playOutAnimation(mSelectedAnimatorHolder, recyclerView, (int) dy,
                new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        recoverAnimatorHolder(mPrevAnimatorHolder);
                        recoverAnimatorHolder(mNextAnimatorHolder);

                        if (mOnSwipeListener != null) {
                            mOnSwipeListener.onSwipe(recyclerView, adapterPos, dy);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
    }

    private void resetAllChildrenProperties(RecyclerView recyclerView) {
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View child = recyclerView.getChildAt(i);
            resetViewProperties(child);
        }
    }

    private void resetViewProperties(View child) {
        child.setTranslationX(0);
        child.setTranslationY(0);
        child.setScaleX(1.F);
        child.setScaleY(1.F);
    }

    private void playOutAnimation(AnimatorHolder holder, View parent, int direction,
                                  Animator.AnimatorListener listener) {
        View selectedView = holder.getView();
        float fromY = selectedView.getTranslationY();
        float toY;
        if (direction > 0) {
            toY = fromY + (parent.getBottom() - selectedView.getTop());
        } else {
            toY = fromY - selectedView.getBottom();
        }
        ObjectAnimator outAnimation = ObjectAnimator.ofFloat(selectedView, View.TRANSLATION_Y,
                fromY, toY);
        if (listener != null) {
            outAnimation.addListener(listener);
        }
        holder.playAnimator(outAnimation);
    }

    private AnimatorSet makeScaleAnimatorSet(View view, float fromScale, float toScale, long duration) {
        AnimatorSet animatorSet = new AnimatorSet();
        List<Animator> animators = makeScaleAnimatorList(view, fromScale, toScale, duration);
        animatorSet.playTogether(animators);
        return animatorSet;
    }

    private List<Animator> makeScaleAnimatorList(View view, float fromScale, float toScale, long duration) {
        List<Animator> result = new ArrayList<>();
        ValueAnimator scaleXAnimation = ObjectAnimator.ofFloat(view, View.SCALE_X,
                fromScale, toScale);
        scaleXAnimation.setInterpolator(new OvershootInterpolator());
        scaleXAnimation.setDuration(duration);
        result.add(scaleXAnimation);

        ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(view, View.SCALE_Y,
                fromScale, toScale);
        scaleYAnimation.setInterpolator(new OvershootInterpolator());
        scaleYAnimation.setDuration(duration);
        result.add(scaleYAnimation);
        return result;
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public interface OnSwipeListener {
        void onSwipe(RecyclerView rv, int adapterPosition, float dy);
    }

    protected class AnimatorHolder implements Animator.AnimatorListener {
        private Animator mAnimator;
        private View mView;

        public View getView() {
            return mView;
        }

        public void setView(View view) {
            this.mView = view;
        }

        public void playAnimator(Animator animator) {
            animator.addListener(this);
            if (mAnimator != null && mAnimator.isRunning()) {
                mAnimator.cancel();
            }
            animator.start();
            mAnimator = animator;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            mRunningAnimators.add(animation);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mRunningAnimators.remove(animation);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            mRunningAnimators.remove(animation);
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    }

    private class LongPressGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            int pointerIndex = MotionEventCompat.getActionIndex(e);
            mActivePointerIndex = MotionEventCompat.getPointerId(e, pointerIndex);
            if (mRecyclerView != null) {
                RecyclerView rv = mRecyclerView;
                View v = rv.findChildViewUnder(e.getX(), e.getY());
                if (v != null) {
                    mLongPressInAction = true;
                    mTouchStartX = e.getX();
                    mTouchStartY = e.getY();

                    mSelectedView = v;
                    mSelectedAnimatorHolder.setView(mSelectedView);
                    mSelectedAnimatorHolder.playAnimator(makeScaleAnimatorSet(mSelectedView, 1.0f, 1.03f, 300));

                    int adapterPos = rv.getChildAdapterPosition(v);
                    mSelectedAdapterPos = adapterPos;
                    Log.d(TAG, "adapterPos: " + adapterPos);

                    RecyclerView.LayoutManager lm = rv.getLayoutManager();
                    View prevView = lm.findViewByPosition(adapterPos - 1);
                    if (prevView != null) {
                        mPrevAnimatorHolder.setView(prevView);
                        mPrevAnimatorHolder.playAnimator(makeScaleAnimatorSet(prevView, 1.0f, 0.97f, 300));
                    }

                    View nextView = lm.findViewByPosition(adapterPos + 1);
                    if (nextView != null) {
                        mNextAnimatorHolder.setView(nextView);
                        mNextAnimatorHolder.playAnimator(makeScaleAnimatorSet(nextView, 1.0f, 0.97f, 300));
                    }

                    Log.d(TAG, String.format("onLongPress: %s, %s, %s", prevView, mSelectedView, nextView));

                    recycleVelocityTracker();
                    mVelocityTracker = VelocityTracker.obtain();
                }
                mRecyclerView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            }
        }
    }
}
