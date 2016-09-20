package com.yohoho.dview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout mRelativeLayout;
    private RelativeLayout mInnerLayout;
    private ImageView mInnerIconHome;
    private RelativeLayout mMiddleLayout;
    private ImageView mMiddleIconMenu;
    private ImageView mMiddleIconSearch;
    private ImageView mMiddleIconMyyouku;
    private RelativeLayout mOutsideLayout;
    private ImageView mOutsiedChannel1;
    private ImageView mOutsideChannel2;
    private ImageView mOutsiedChannel3;
    private ImageView mOutsiedChannel4;
    private ImageView mOutsiedChannel5;
    private ImageView mOutsideChannel6;
    private ImageView mOutsiedChannel7;
    private boolean isMiddleMenuDisplay=true;
    private boolean isOutsideMenuDisplay=true;
    private int animationCount=0;

    private void assignViews() {
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        mInnerLayout = (RelativeLayout) findViewById(R.id.inner_layout);
        mInnerIconHome = (ImageView) findViewById(R.id.inner_icon_home);
        mInnerIconHome.setOnClickListener(this);

        mMiddleLayout = (RelativeLayout) findViewById(R.id.middle_layout);
        mMiddleIconMenu = (ImageView) findViewById(R.id.middle_icon_menu);
        mMiddleIconMenu.setOnClickListener(this);
        mMiddleIconSearch = (ImageView) findViewById(R.id.middle_icon_search);
        mMiddleIconMyyouku = (ImageView) findViewById(R.id.middle_icon_myyouku);

        mOutsideLayout = (RelativeLayout) findViewById(R.id.outside_layout);
        mOutsiedChannel1 = (ImageView) findViewById(R.id.outsied_channel1);
        mOutsideChannel2 = (ImageView) findViewById(R.id.outside_channel2);
        mOutsiedChannel3 = (ImageView) findViewById(R.id.outsied_channel3);
        mOutsiedChannel4 = (ImageView) findViewById(R.id.outsied_channel4);
        mOutsiedChannel5 = (ImageView) findViewById(R.id.outsied_channel5);
        mOutsideChannel6 = (ImageView) findViewById(R.id.outside_channel6);
        mOutsiedChannel7 = (ImageView) findViewById(R.id.outsied_channel7);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignViews();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.inner_icon_home:
                 if(animationCount!=0){
                     return;//动画正在播放的时候不再响应
                 }
                if(isMiddleMenuDisplay&&isOutsideMenuDisplay){
                //隐藏所有的菜单
                hideMenu(mOutsideLayout);
                hideMenu(mMiddleLayout);

                    mMiddleIconMenu.setClickable(false);
                isOutsideMenuDisplay=false;
                isMiddleMenuDisplay=false;
                    return;//如果不return 的话那么就会刚好进入下一个判断，状态还是会被还原所以不起作用
                }

                if(!isMiddleMenuDisplay&&!isOutsideMenuDisplay){
                    //显示二级菜单
                    displayMenu(mMiddleLayout);

                    mMiddleIconMenu.setClickable(true);
                    isMiddleMenuDisplay=true;
                    return;
                }

                if(isMiddleMenuDisplay&&!isOutsideMenuDisplay){
                    hideMenu(mMiddleLayout);
                    isMiddleMenuDisplay=false;
                    return;
                }
                break;

            case R.id.middle_icon_menu:

                if(animationCount!=0){
                    return;//动画正在播放的时候不再响应
                }

                if(isOutsideMenuDisplay){
                  hideMenu(mOutsideLayout);
                  isOutsideMenuDisplay=false;
                  return;
              }
              if(!isOutsideMenuDisplay){
                  displayMenu(mOutsideLayout);
                  isOutsideMenuDisplay=true;
              }
            break;
            default:
                break;
        }


    }
    private void displayMenu(RelativeLayout container) {
       // container.setVisibility(View.VISIBLE);
        RotateAnimation animation=new RotateAnimation(-180,0,RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,1f);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animationCount++;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animationCount--;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        container.startAnimation(animation);
    }
    private void hideMenu(RelativeLayout container) {
        //container.setVisibility(View.GONE);

        RotateAnimation animation=new RotateAnimation(0,-180,RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,1f);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animationCount++;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animationCount--;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        container.startAnimation(animation);

    }
}
