package com.haohaohu.bannersample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.haohahu.banner.Banner;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Banner mBanner1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mBanner1 = (Banner) findViewById(R.id.banner1);

        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> urls = new ArrayList<>();
                urls.add("#ff00ff");
                urls.add("#ff0000");
                urls.add("#0000ff");

                mBanner1.setData(urls);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBanner1.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBanner1.onPause();
    }
}
