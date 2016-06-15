package com.carl.recyclerview;

/**
 * @author carl
 */
public interface SwipeGestureAdapter {
    /**
     * Tells SwipeGestureHelper if the given position in RecyclerView.Adapter could be swiped
     * @param adapterPosition the adapter position of the item in RecyclerView.Adapter
     * @return true if it could be swiped, false otherwise
     */
    boolean shouldSwipe(int adapterPosition);
}
