package com.haohahu.banner;

import android.view.View;
import android.widget.ImageView;

/**
 * @author haohao(ronghao3508@gmail.com) on 2017/9/25 下午 05:10
 * @version v1.0
 */
public class ImageBannerView implements IBannerView<String> {
    private ImageView imageView;

    @Override
    public View getView() {
        return imageView;
    }

    @Override
    public void initView(int position, int sum, String s, IBannerViewClickListener listener) {

    }
}
