package com.carl.recyclerviewdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeGestureHelper.OnSwipeListener {
    static final String[] TEST_STRINGS = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX",
            "SEVEN", "EIGHT", "NINE", "TEN", "ELEVEN", "TWELVE"};
    private static final String TAG = "MainActivity";
    private SimpleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        if (recyclerView == null) {
            return;
        }
        SnappyLinearLayoutManager lm = new SnappyLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        lm.setFlingVelocityRatio(0.7f);
//        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(lm);

        ArrayList<String> strings = new ArrayList<>();

        for (int i = 0; i < TEST_STRINGS.length; i++) {
            strings.add(TEST_STRINGS[i]);
        }

        mAdapter = new SimpleAdapter(strings);
        recyclerView.setAdapter(mAdapter);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING);
//        recyclerView.setItemAnimator(new SlideInAnimator());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MarginFixDecoration());
        ViewOnTouchDelegate delegate = new ViewOnTouchDelegate();
        recyclerView.setOnTouchListener(delegate);
        SwipeGestureHelper swipeGestureHelper = new SwipeGestureHelper(getApplicationContext());
        swipeGestureHelper.attachToRecyclerView(recyclerView, delegate);
        swipeGestureHelper.setOnSwipeListener(this);
//        PanDownTransitionGestureHelper panDownHelper = new PanDownTransitionGestureHelper();
//        panDownHelper.attachToRecyclerView(recyclerView, delegate);
    }

    @Override
    public void onSwipe(RecyclerView rv, int adapterPosition, float dy) {
        mAdapter.removeItem(adapterPosition);
        rv.invalidateItemDecorations();
    }

    static class SimpleViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTextView;

        public SimpleViewHolder(View ll, TextView itemView) {
            super(ll);
            mTextView = itemView;
        }
    }

    static class SimpleAdapter extends RecyclerView.Adapter<SimpleViewHolder> {
        private final List<String> mDataSet;

        public SimpleAdapter(List<String> dataSet) {
            this.mDataSet = dataSet;
        }

        public void removeItem(int adapterPos) {
            mDataSet.remove(adapterPos);
            notifyItemRemoved(adapterPos);
        }

        @Override
        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder");
            LinearLayout ll = new LinearLayout(parent.getContext());
            TextView tv = new TextView(parent.getContext());
            tv.setTextColor(Color.WHITE);
            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ll.addView(tv);
            int w = parent.getWidth();
            int h = parent.getHeight();
            int childW = (int) (w * 0.7);
            ll.setPadding(40, 40, 40, 40);
            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(childW, h);
            ll.setLayoutParams(lp);
            return new SimpleViewHolder(ll, tv);
        }

        private int getColor(int position) {
            float factor = (float) position / (float) getItemCount();
            return 0xFF000000 | (int) (0x0000FF * Math.sin(factor))
                    | ((int) (0x0000FF * Math.sin(2 * Math.PI * (factor + 1F / 3F))) << 8)
                    | ((int) (0x0000FF * Math.sin(2 * Math.PI * (factor + 2F / 3F))) << 16);
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: " + holder.itemView);
            TextView tv = holder.mTextView;
            String content = mDataSet.get(position);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
            tv.setGravity(Gravity.CENTER);
            tv.setText(content);
            int color = getColor(position);
            Log.d(TAG, String.format("color: %8h", color));
            tv.setBackgroundColor(color);
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }
    }
}
