package com.carl.recyclerviewdemo;

import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carl
 */
public class ViewOnTouchDelegate implements View.OnTouchListener {

    private List<View.OnTouchListener> mListeners = new ArrayList<>();

    public void addOnTouchListener(View.OnTouchListener listener){
        if (listener == null) {
            return;
        }
        mListeners.add(listener);
    }

    public void removeOnTouchListener(View.OnTouchListener listener) {
        mListeners.remove(listener);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        for (View.OnTouchListener listener : mListeners) {
            if (listener.onTouch(v, event)) return true;
        }
        return false;
    }
}
