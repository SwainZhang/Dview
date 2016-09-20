package com.yohoho.slidingdrawermenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SlidingMenu mSlidingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSlidingMenu = (SlidingMenu) findViewById(R.id.sliding_menu);
    }

    public void clickTab(View view){//click事件会自动回传点击了哪个view
      String str=  ((TextView)view).getText().toString();
        Log.d("sliding_menu","点击了"+str);
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
        mSlidingMenu.toggle();
    }
    public void   clickBack(View view){

        Toast.makeText(getApplicationContext(), "点击了返回", Toast.LENGTH_SHORT).show();
        mSlidingMenu.toggle();
    }

}
