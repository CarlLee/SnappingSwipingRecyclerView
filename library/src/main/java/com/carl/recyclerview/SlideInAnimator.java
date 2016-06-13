package com.carl.recyclerview;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * @author carl
 */
public class SlideInAnimator extends SimpleItemAnimator {

    private static final String TAG = "SlideInAnimator";
    public Map<RecyclerView.ViewHolder, ViewPropertyAnimatorCompat>
            mPendingAnimations = new HashMap<>();
    private Map<RecyclerView.ViewHolder, ViewPropertyAnimatorCompat>
            mRunningAnimations = new HashMap<>();

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        dispatchRemoveFinished(holder);
        Log.d(TAG, "animateRemove: " + holder.itemView);
        return false;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        dispatchAddFinished(holder);
        Log.d(TAG, "animateAdd: " + holder.itemView);
        return false;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        final View v = holder.itemView;
        v.setX(fromX);
        ViewPropertyAnimatorCompat vpa = ViewCompat.animate(v)
                .x(toX)
                .setDuration(3000)
                .setListener(new DefaultViewPropertyAnimatorListener(holder));
        mPendingAnimations.put(holder, vpa);
        Log.d(TAG, String.format("animateMove: %s, (%d, %d) -> (%d, %d)", holder.itemView,
                fromX, fromY, toX, toY));
        return true;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
        if (oldHolder != null && oldHolder == newHolder) {
            dispatchChangeFinished(oldHolder, true);
        } else {
            if (oldHolder != null) {
                dispatchChangeFinished(oldHolder, true);
            }
            if (newHolder != null) {
                dispatchChangeFinished(newHolder, false);
            }
        }
        return false;
    }

    @Override
    public void runPendingAnimations() {
        Log.d(TAG, "runPendingAnimations: " + mPendingAnimations.size());
        for (Map.Entry<RecyclerView.ViewHolder, ViewPropertyAnimatorCompat> entry
                : mPendingAnimations.entrySet()) {
            RecyclerView.ViewHolder holder = entry.getKey();
            ViewPropertyAnimatorCompat vpa = entry.getValue();
            vpa.start();
            mRunningAnimations.put(holder, vpa);
        }
        mPendingAnimations.clear();
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {
        Log.d(TAG, "endAnimation: " + item);
        ViewPropertyAnimatorCompat animator = mRunningAnimations.get(item);
        if (animator != null) {
            animator.cancel();
            mRunningAnimations.remove(item);
        }
    }

    @Override
    public void endAnimations() {
        Log.d(TAG, "endAnimations");
        for (Map.Entry<RecyclerView.ViewHolder, ViewPropertyAnimatorCompat> entry
                : mRunningAnimations.entrySet()) {
            ViewPropertyAnimatorCompat vpa = entry.getValue();
            vpa.cancel();
        }
        mRunningAnimations.clear();
    }

    @Override
    public boolean isRunning() {
        return mRunningAnimations != null && mRunningAnimations.size() != 0;
    }

    private class DefaultViewPropertyAnimatorListener implements ViewPropertyAnimatorListener {

        private final RecyclerView.ViewHolder mHolder;

        public DefaultViewPropertyAnimatorListener(RecyclerView.ViewHolder holder) {
            this.mHolder = holder;
        }

        @Override
        public void onAnimationStart(View view) {

        }

        @Override
        public void onAnimationEnd(View view) {
            Log.d(TAG, "onAnimationEnd: " + view);
            dispatchMoveFinished(mHolder);
            mRunningAnimations.remove(mHolder);
        }

        @Override
        public void onAnimationCancel(View view) {
            Log.d(TAG, "onAnimationCancel: " + view);
            dispatchMoveFinished(mHolder);
            mRunningAnimations.remove(mHolder);
        }
    }
}
