package com.haohahu.banner;

import android.view.View;

/**
 * @author haohao(ronghao3508@gmail.com) on 2017/9/25 下午 05:07
 * @version v1.0
 */
public interface IBannerView<T> {

    View getView();

    void initView(int position, int sum, T t, IBannerViewClickListener listener);
}
