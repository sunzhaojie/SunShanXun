package com.example.sunshanxun.main;

import android.os.Bundle;
import android.view.View;

import com.example.sunshanxun.R;
import com.example.sunshanxun.view.SXTitleView;

/**
 * Created by SunZJ on 2017/6/4.
 */

public class AboutAty extends BaseAty {
    private SXTitleView mTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mTitle= (SXTitleView) findViewById(R.id.id_title_view);
        mTitle.setOnTitleClickListener(new SXTitleView.OnTitleClickListener() {
            @Override
            public void onLogoClick(View v) {
                AboutAty.this.finish();
            }

            @Override
            public void onTitleClick(View v) {

            }

            @Override
            public void onButtonClick(View v) {

            }
        });
    }
}
