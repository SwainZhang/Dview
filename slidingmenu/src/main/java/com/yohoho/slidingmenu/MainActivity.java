package com.yohoho.slidingmenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private SlidingToggle mSlidingToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSlidingToggle = (SlidingToggle) findViewById(R.id.sliding_toggle);

        mSlidingToggle.setBackgroundBitmapResource(R.drawable.switch_background);
        mSlidingToggle.setSlidingToggleBitmapResource(R.drawable.slide_button_background);

        mSlidingToggle.setOnToggleStateListener(new SlidingToggle.onToggleStateListener() {
            @Override
            public void onToggleStateChanged(boolean isOpened) {

               Log.e("sliding_menu","开关的状态"+isOpened);
               
            }
        });
    }
}
