package com.yohoho.parallex;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        final MyListView listView = (MyListView) findViewById(R.id.mylistview);
        View view=View.inflate(getApplicationContext(),R.layout.view_header,null);

        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);//不显示蓝色影印

        final ImageView iv= (ImageView) view.findViewById(R.id.iv);

        //当布局填充结束的时候会被调用
        iv.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {


            @Override
            public void onGlobalLayout() {
                //当布局填充结束的时候会被调用
                listView.setImageView(iv);
                iv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        listView.addHeaderView(view);
        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Cheeses.NAMES));

    }
}
