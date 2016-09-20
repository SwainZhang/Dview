package com.yohoho.viewdraghelper;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private DragLayout mDragLayout;
    private ListView mList_left;
    private ListView mList_main;
    private String [] names=Cheeses.NAMES;
    private ImageView mIv_head;
    private MyLinearLayout mMyLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDragLayout = (DragLayout) findViewById(R.id.draglayout);
        mList_left = (ListView) findViewById(R.id.lv_left);
        mList_main = (ListView) findViewById(R.id.lv_main);
        mIv_head = (ImageView) findViewById(R.id.iv_head);
        mMyLinearLayout = (MyLinearLayout) findViewById(R.id.myLinearLayout);
        mMyLinearLayout.setDragLayout(mDragLayout);
        mList_left.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,names){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(Color.WHITE);
                return view;

            }
        });
        mList_main.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,names));
        mDragLayout.setOnDragStateChangedListener(new DragLayout.onDragStateChangedListener() {
            @Override
            public void onClosed() {
                Log.d("sliding_menu","onClosed");
                 //让头像shake

                ObjectAnimator animator = ObjectAnimator.ofFloat(mIv_head, "translationX", 15.0f);
                    animator.setInterpolator(new CycleInterpolator(5));//插值器动态的改变动画执行过程中速度的变化
                    animator.setDuration(500);
                    animator.start();
            }

            @Override
            public void onOpened() {
                Log.d("sliding_menu","onOpened");
                //left随机跳到一个条目
                Random random=new Random();
               // mList_left.setSelection( random.nextInt(50));
                mList_left.smoothScrollToPosition(random.nextInt(50));//平滑的移动到某个位置
            }

            @Override
            public void onDraging(float percent) {
                Log.d("sliding_menu","onDraging");
                //更新图标的透明度  1-->0.0
                ViewHelper.setAlpha(mIv_head,1-percent);
            }
        });
    }

}
