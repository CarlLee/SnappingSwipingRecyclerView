Preview
=======
![preview](https://raw.githubusercontent.com/CarlLee/SnappingSwipingRecyclerView/master/preview.gif)


Intro
=====
SnappingSwipingRecyclerView is an implementation of "viewpager-like" behavior of RecyclerView, 
but it also adds a "long press to swipe" pattern like used in [Wechat Reading](https://play.google.com/store/apps/details?id=com.tencent.weread), 
A.K.A, 微信读书, in Chinese.
 
 
Usage
=====

You can use classes like `SwipeGestureHelper`, `SnappyRecyclerView`, `SnappyLinearLayoutManager`, `MarginDecoration` directly, just read their Java doc.
 
Or, you can simply use this convenient `SnappingSwipingViewBuilder` class to save you some work.

```java
    RecyclerView recyclerView = new SnappingSwipingViewBuilder(this)
                    .setAdapter(mAdapter)
                    .setHeadTailExtraMarginDp(17F)
                    .setItemMarginDp(8F, 20F, 8F, 20F)
                    .setOnSwipeListener(this)
                    .setSnapMethod(SnappyLinearLayoutManager.SnappyLinearSmoothScroller.SNAP_CENTER)
                    .build();
```

TODOs
=====

- Add support for vertical layouts
- Add relative width support for item views
