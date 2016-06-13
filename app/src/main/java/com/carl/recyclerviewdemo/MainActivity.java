package com.carl.recyclerviewdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
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
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.container);

        ArrayList<String> strings = new ArrayList<>();
        Collections.addAll(strings, TEST_STRINGS);
        mAdapter = new SimpleAdapter(strings);

        RecyclerView recyclerView = new SnappingSwipingViewBuilder(this)
                .setAdapter(mAdapter)
                .setHeadTailExtraMarginDp(17F)
                .setItemMarginDp(8F, 20F, 8F, 20F)
                .setOnSwipeListener(this)
                .setSnapMethod(SnappyLinearLayoutManager.SnappyLinearSmoothScroller.SNAP_CENTER)
                .build();

        if (rl != null) {
            recyclerView.setLayoutParams(new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            rl.addView(recyclerView);
        }
    }

    @Override
    public void onSwipe(RecyclerView rv, int adapterPosition, float dy) {
        mAdapter.removeItem(adapterPosition);
        rv.invalidateItemDecorations();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTextView;

        public SimpleViewHolder(View ll, TextView itemView) {
            super(ll);
            mTextView = itemView;
        }
    }

    class SimpleAdapter extends RecyclerView.Adapter<SimpleViewHolder> {
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
//            LinearLayout ll = new LinearLayout(parent.getContext());
            TextView tv = new TextView(parent.getContext());
            tv.setTextColor(Color.WHITE);
//            tv.setLayoutParams(new ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            ll.addView(tv);
            int w = parent.getWidth();
            int itemMargin = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9F,
                    getResources().getDisplayMetrics()) + 0.5F);
            int itemPadding = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8F,
                    getResources().getDisplayMetrics()) + 0.5F);
            int itemWidth = w - (itemMargin + itemPadding * 2) * 2;
            tv.setLayoutParams(new ViewGroup.LayoutParams(
                    itemWidth, ViewGroup.LayoutParams.MATCH_PARENT));
//            int h = parent.getHeight();
//            int childW = (int) (w * 0.7);
//            ll.setPadding(itemPadding, itemPadding, itemPadding, itemPadding);
//            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
//                    itemWidth, ViewGroup.LayoutParams.MATCH_PARENT);
//            ll.setLayoutParams(lp);
//            ll.setBackground(getDrawable(R.drawable.round_rect_border));
            return new SimpleViewHolder(tv, tv);
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
