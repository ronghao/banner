package com.haohahu.banner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import java.util.ArrayList;

public abstract class BaseBanner<T> extends HorizontalScrollView {

    private final int AUTO_SCROLL = 0;

    //内部LinearLayout
    protected LinearLayout linearLayout;
    // 滚动条的宽度
    protected int hsv_width;

    // 每一个view的宽度
    protected int child_width;
    // 预计显示在屏幕上的view的个数
    protected int child_show_count = 2;
    protected int child_count;
    // 一开始居中选中的view
    protected int current_index;
    //是否自动滚动
    protected boolean is_auto_scroll = true;
    protected int duration = 2000;
    protected ArrayList<T> images;

    protected Scroller mScroller;

    @SuppressLint("HandlerLeak") private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case AUTO_SCROLL:
                    if (is_auto_scroll) {
                        scrollToNext();
                        Message msg1 = new Message();
                        msg1.what = AUTO_SCROLL;
                        handler.sendEmptyMessageDelayed(AUTO_SCROLL, duration);
                    }
                    break;

                default:
                    break;
            }
        }
    };

    public BaseBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseBanner(Context context) {
        super(context);
        init();
    }

    private void init() {
        initLinearLayout();
        setVerticalScrollBarEnabled(false);
        mScroller = new Scroller(getContext());
        images = new ArrayList<>();
    }

    private void initLinearLayout() {
        linearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(params);
        addView(linearLayout);
    }

    /**
     * 给滚动控件添加view，只有重复两个列表才能实现循环滚动
     */
    private void initItems() {
        if (images == null || images.size() == 0 || child_width == 0) return;
        int size = images.size();
        linearLayout.removeAllViews();
        for (int i = 0; i < size * 2; i++) {
            View imageView = initItem(i, size);
            linearLayout.addView(imageView);
        }
    }

    /**
     * 创建子项
     */
    public abstract View initItem(final int i, int sum);

    /**
     * 设置指定位置的状态
     */
    public abstract void isChecked(int item, boolean isChecked);

    /**
     * 实现滚动的循环处理，及停止触摸时的处理
     */
    private void initHsvTouch() {
        setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean flag = false;
                // 获得scroll的位置
                int x = getScrollX();
                // 获得当前居中的item的tag
                int current_item = (x + hsv_width / 2) / child_width + 1;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        stopAutoScroll();
                        flag = false;
                        if (x <= child_width) {
                            scrollBy(child_width * child_count, 0);
                            current_item += child_count;
                        } else if (x >= (child_width * child_count * 2 - hsv_width - child_width)) {
                            scrollBy(-child_width * child_count, 0);
                            current_item -= child_count;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        startAutoScroll();
                        flag = true;
                        smoothScrollTo(child_width * current_item - child_width / 2 - hsv_width / 2,
                                getScrollY());
                        Log.e("MotionEvent.ACTION_UP", "" + current_item);
                        break;
                }
                if (current_index == 0) {
                    isChecked(current_item, true);
                } else if (current_index != current_item) {
                    isChecked(current_index, false);
                    isChecked(current_item, true);
                }
                current_index = current_item;
                return flag;
            }
        });
    }

    /**
     * 刚开始进入界面时的初始选中项的处理
     */
    private void initStart() {
        final ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                firstInit();
                return false;
            }
        });
    }

    private void firstInit() {
        if ((current_index * child_width - child_width / 2 - hsv_width / 2) <= child_width) {
            current_index += child_count;
        }
        scrollTo(child_width * current_index - child_width / 2 - hsv_width / 2, getScrollY());
        isChecked(current_index, true);
    }

    /**
     * 只有到了这个方法才能获取控件的尺寸
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hsv_width != 0) return;
        hsv_width = getWidth();
        int child_width_temp = hsv_width / child_show_count;
        if (child_width_temp % 2 != 0) {
            child_width_temp++;
        }
        child_width = child_width_temp;
        initItems();
        initHsvTouch();
        initStart();
        startAutoScroll();
    }

    public void scrollToNext() {
        isChecked(current_index, false);
        current_index += 1;
        scroll();
        isChecked(current_index, true);
    }

    protected void scroll() {
        if (current_index == 1) {
            isChecked(current_index, false);
            scrollBy(child_width * (child_count + 1), 0);
            current_index += child_count;
        }
        if (current_index == child_count * 2) {
            scrollBy(-child_width * (child_count + 1), 0);
            current_index -= child_count;
        }
        smoothScrollTo(child_width * current_index - child_width / 2 - hsv_width / 2, getScrollY());
    }

    public void onResume() {
        startAutoScroll();
    }

    public void onPause() {
        stopAutoScroll();
    }

    protected void startAutoScroll() {
        stopAutoScroll();
        //if (is_auto_scroll) {
        //    handler.sendEmptyMessageDelayed(AUTO_SCROLL, duration);
        //}
    }

    protected void stopAutoScroll() {
        if (handler != null) handler.removeMessages(AUTO_SCROLL);
    }

    //调用此方法滚动到目标位置  duration滚动时间
    public void smoothScrollToSlow(int fx, int fy) {
        smoothScrollToSlow(fx, fy, 3000);
    }

    //调用此方法滚动到目标位置  duration滚动时间
    public void smoothScrollToSlow(int fx, int fy, int duration) {
        int dx = fx - getScrollX();//mScroller.getFinalX();  普通view使用这种方法
        int dy = fy - getScrollY();  //mScroller.getFinalY();
        smoothScrollBySlow(dx, dy, duration);
    }

    //调用此方法设置滚动的相对偏移
    public void smoothScrollBySlow(int dx, int dy, int duration) {
        //设置mScroller的滚动偏移量
        mScroller.startScroll(getScrollX(), getScrollY(), dx, dy,
                duration);//scrollView使用的方法（因为可以触摸拖动）
        //        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, duration);  //普通view使用的方法
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    public void setData(ArrayList<T> images) {
        if (images == null || images.size() == 0) return;
        this.images = images;
        child_count = images.size();
        initItems();

        if (images == null || images.size() == 0 || child_width == 0) return;
        firstInit();
    }

    public void setIsAutoScroll(boolean is) {
        is_auto_scroll = is;
    }
}
