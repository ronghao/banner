package com.haohahu.banner;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class Banner extends BaseBanner<String> {

    protected int child_show_count = 3;

    public Banner(Context context, AttributeSet attrs) {
        super(context, attrs);
        child_show_count = 3;
    }

    public Banner(Context context) {
        super(context);
        child_show_count = 3;
    }

    @Override
    public View initItem(final int i, int sum) {
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(
                new ViewGroup.LayoutParams(child_width, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setTag(i + 1);
        String url = images.get(i % sum);
        imageView.setBackgroundColor(Color.parseColor(url));
        imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), i + "", Toast.LENGTH_SHORT).show();
                stopAutoScroll();
                isChecked(current_index, false);
                current_index = (Integer) v.getTag();
                scroll();
                isChecked(current_index, true);
                startAutoScroll();
            }
        });
        return imageView;
    }

    public void isChecked(int item, boolean isChecked) {
        ImageView imageView = (ImageView) linearLayout.getChildAt(item - 1);
        //if (isChecked) {
        //    imageView.setImageResource(images.get((item - 1) % 4)[1]);
        //} else {
        //    imageView.setImageResource(images.get((item - 1) % 4)[0]);
        //}
    }
}
