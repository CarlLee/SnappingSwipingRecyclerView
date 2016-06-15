package com.carl.recyclerview;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Help setup related classes to achieve the "snapping and swiping" behaviour of a RecyclerView
 *
 * @author carl
 */
public class SnappingSwipingViewBuilder {

    private final SnappyRecyclerView mRecyclerView;
    private final DisplayMetrics mDisplayMetrics;
    private final SwipeGestureHelper mSwipeGestureHelper;
    private final SnappyLinearLayoutManager mSnappyLinearLayoutManager;
    private final MarginDecoration mMarginDecoration;

    public SnappingSwipingViewBuilder(Context context) {
        this(context, null);
    }

    public SnappingSwipingViewBuilder(Context context, SnappyRecyclerView srv) {
        this.mDisplayMetrics = context.getResources().getDisplayMetrics();
        if(srv == null){
            this.mRecyclerView = new SnappyRecyclerView(context);
        } else {
            this.mRecyclerView = srv;
        }
        this.mSwipeGestureHelper = new SwipeGestureHelper(context);
        this.mSnappyLinearLayoutManager = new SnappyLinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false);
        this.mMarginDecoration = new MarginDecoration();
        ViewOnTouchDelegate touchDelegate = new ViewOnTouchDelegate();
        mRecyclerView.setOnTouchListener(touchDelegate);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(mSnappyLinearLayoutManager);
        mRecyclerView.addItemDecoration(mMarginDecoration);
        mSwipeGestureHelper.attachToRecyclerView(mRecyclerView, touchDelegate);
    }

    /**
     * Sets the ratio between scroll velocity and fling velocity
     *
     * @param flingVelocityRatio the ratio used to calculate fling velocity
     * @return this for chaining calls
     */
    public SnappingSwipingViewBuilder setFlingVelocityRatio(float flingVelocityRatio) {
        mSnappyLinearLayoutManager.setFlingVelocityRatio(flingVelocityRatio);
        return this;
    }


    /**
     * Set method used when calculating snapping positions
     *
     * @param snapMethod Constants that starts with SNAP_ in SnappyLinearSmoothScroller
     * @return this for chaining calls
     */
    public SnappingSwipingViewBuilder setSnapMethod(@SnappyLinearLayoutManager.SnapMethod
                                                    int snapMethod) {
        mSnappyLinearLayoutManager.setSnapMethod(snapMethod);
        return this;
    }

    /**
     * Sets the orientation of the layout. {@link android.support.v7.widget.LinearLayoutManager}
     * will do its best to keep scroll position.
     *
     * @param orientation {@link android.support.v7.widget.LinearLayoutManager#HORIZONTAL} or
     *                    {@link android.support.v7.widget.LinearLayoutManager#VERTICAL}
     * @return this for chaining calls
     */
    public SnappingSwipingViewBuilder setOrientation(int orientation) {
        mSnappyLinearLayoutManager.setOrientation(orientation);
        return this;
    }

    /**
     * Sets the ratio threshold which determines if a swipe is successful, default is 0.4F.
     * Speed takes precedence over dragging distance in determine if a swipe is successful.
     *
     * @param threshold the ratio between the distance user has dragged the item view and the
     *                  height/width of parent
     * @return this for chaining calls
     */
    public SnappingSwipingViewBuilder setSwipeThresholdRatio(float threshold) {
        mSwipeGestureHelper.setSwipeThresholdRatio(threshold);
        return this;
    }

    /**
     * Sets the speed threshold which determines if a swipe is successful, default is 800F.
     * Speed takes precedence over dragging distance in determine if a swipe is successful.
     *
     * @param swipeThresholdSpeedDpPerSecond the threshold speed of user's swipe in DP per second
     * @return this for chaining calls
     */
    public SnappingSwipingViewBuilder setSwipeThresholdSpeedDpPerSecond(float swipeThresholdSpeedDpPerSecond) {
        mSwipeGestureHelper.setSwipeThresholdSpeedDpPerSecond(swipeThresholdSpeedDpPerSecond);
        return this;
    }

    /**
     * Sets the magnitude of change in scale for the scale animation when user long presses an item
     * view. The selected item view will be scaled up to 1.0F + scaleAnimationOffset, other item
     * views will be scaled down to 1.0F - scaleAnimationOffset.
     *
     * @param scaleAnimationOffset the magnitude of change in scale for scale animations
     * @return this for chaining calls
     */
    public SnappingSwipingViewBuilder setScaleAnimationOffset(float scaleAnimationOffset) {
        mSwipeGestureHelper.setScaleAnimationOffset(scaleAnimationOffset);
        return this;
    }

    /**
     * Sets the duration of scale animation
     *
     * @param scaleAnimationDuration the animation duration
     * @return this for chaining calls
     */
    public SnappingSwipingViewBuilder setScaleAnimationDuration(long scaleAnimationDuration) {
        mSwipeGestureHelper.setScaleAnimationDuration(scaleAnimationDuration);
        return this;
    }

    /**
     * Sets the duration of out animation when a swipe is successful and the selected view is
     * transitioning out of sight
     *
     * @param outAnimationDuration the animation duration
     * @return this for chaining calls
     */
    public SnappingSwipingViewBuilder setOutAnimationDuration(long outAnimationDuration) {
        mSwipeGestureHelper.setOutAnimationDuration(outAnimationDuration);
        return this;
    }

    /**
     * Sets the duration of recover animation when views are recovering into their original scale
     * and position when user's finger lifts
     *
     * @param recoverAnimationDuration the animation duration
     * @return this for chaining calls
     */
    public SnappingSwipingViewBuilder setRecoverAnimationDuration(long recoverAnimationDuration) {
        mSwipeGestureHelper.setRecoverAnimationDuration(recoverAnimationDuration);
        return this;
    }

    /**
     * Sets an {@link SwipeGestureAdapter} to tell SwipeGestureHelper if item views at given
     * adapter position should be swiped at all, if it returns false, not haptic feedback nor
     * swipe animation will be played even when user long-presses this item view.
     * @param swipeGestureAdapter the {@link SwipeGestureAdapter} to set
     * @return this for chaining calls
     */
    public SnappingSwipingViewBuilder setSwipeGestureAdapter(SwipeGestureAdapter swipeGestureAdapter) {
        mSwipeGestureHelper.setSwipeGestureAdapter(swipeGestureAdapter);
        return this;
    }

    /**
     * Overrides the default implementation of ItemAnimator of RecyclerView
     *
     * @param animator the animator to override the default implementation
     * @return this for chaining calls
     */
    public SnappingSwipingViewBuilder setItemAnimator(RecyclerView.ItemAnimator animator) {
        mRecyclerView.setItemAnimator(animator);
        return this;
    }

    /**
     * Sets adapter for RecyclerView
     *
     * @param adapter adapter to set
     * @return this for chaining calls
     */
    public SnappingSwipingViewBuilder setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
        return this;
    }

    /**
     * Sets the size of blank spaces left. In order to center first and last item
     * view in RecyclerView
     *
     * @param marginInDp the size of blank spaces in dp
     * @return this for chaining calls
     */
    public SnappingSwipingViewBuilder setHeadTailExtraMarginDp(float marginInDp) {
        int marginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                marginInDp, mDisplayMetrics);
        setHeadTailExtraMarginPx(marginPx);
        return this;
    }

    /**
     * Sets the size of blank spaces left. In order to center first and last item
     * view in RecyclerView
     *
     * @param marginPx the size of blank spaces in px
     * @return this for chaining calls
     */
    public SnappingSwipingViewBuilder setHeadTailExtraMarginPx(int marginPx) {
        mMarginDecoration.setMarginHead(marginPx);
        mMarginDecoration.setMarginTail(marginPx);
        return this;
    }

    /**
     * Set margin values for every item view, in dp
     * @param left left margin
     * @param top top margin
     * @param right right margin
     * @param bottom bottom margin
     * @return this for chaining calls
     */
    public SnappingSwipingViewBuilder setItemMarginDp(float left, float top, float right,
                                                      float bottom) {
        setItemMarginPx(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        left, mDisplayMetrics),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        top, mDisplayMetrics),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        right, mDisplayMetrics),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        bottom, mDisplayMetrics)
        );
        return this;
    }

    /**
     * Set margin values for every item view, in px
     * @param left left margin
     * @param top top margin
     * @param right right margin
     * @param bottom bottom margin
     * @return this for chaining calls
     */
    public SnappingSwipingViewBuilder setItemMarginPx(int left, int top, int right, int bottom) {
        mMarginDecoration.setMarginLeft(left);
        mMarginDecoration.setMarginTop(top);
        mMarginDecoration.setMarginRight(right);
        mMarginDecoration.setMarginBottom(bottom);
        return this;
    }

    /**
     * Sets the listener to receive calls when the user swipes out an item view
     *
     * @param listener the listener to receive calls
     * @return this for chaining calls
     */
    public SnappingSwipingViewBuilder setOnSwipeListener(SwipeGestureHelper.OnSwipeListener listener) {
        mSwipeGestureHelper.setOnSwipeListener(listener);
        return this;
    }

    /**
     * Build the RecyclerView
     *
     * @return the RecyclerView with "snapping and swiping" behaviour setup
     */
    public RecyclerView build() {
        return mRecyclerView;
    }
}
