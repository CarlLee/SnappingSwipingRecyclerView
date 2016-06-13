package com.carl.recyclerview;

/**
 * @author carl
 */
public interface SnappyScrollCalculator {
    int computeScrollToItemIndex(int velocityX, int velocityY);
}
