package com.yohoho.parallex;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * @author Administrator
 * @time 2016/9/17 19:27
 * @des  overScrollBy
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class MyListView extends ListView {
    private  ImageView mImageView;
    private int mImageMeasuredHeight;
    private int mOriginHeight;

    public MyListView(Context context) {
        this(context,null);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int
            scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean
            isTouchEvent) {
        Log.d("sliding_menu","deltaX="+deltaX+"\n"+"deltaY="+ deltaY+"\n"+ "scrollX="+ scrollX+"\n"+"scrollY="+  scrollY+"\n"+"scrollRangeX="+
                scrollRangeX+"\n"+"scrollRangeY="+scrollRangeY+"\n"+"maxOverScrollX="+ maxOverScrollX+"\n"+"maxOverScrollY="+ maxOverScrollY
                +"\n"+"isTouchEvent="+isTouchEvent);

        /*

        deltaY :竖直方向的瞬时偏移量 下拉header为负数，上拉footer为正
        scrollY :竖直方向的偏移量
        scrollRangeY :竖直方向滑动的的范围
        maxOverScrollY :竖直方向最大滑动的的范围
        isTouchEvent :是否是手指触摸滑动，false 为惯性滑动

        */

        //手指拉动并且是下拉
        if(isTouchEvent&&deltaY<0){
            //拉动的瞬时变化量交给header
            int dY = mImageMeasuredHeight +Math.abs(deltaY)*30;//设置的高度+滑动的高度=拉动后图片的高度

     if(dY<mOriginHeight) {
         mImageView.getLayoutParams().height = dY;
         mImageView.requestLayout();
     }
           /* ViewGroup.LayoutParams layoutParams = mImageView.getLayoutParams();
            layoutParams.height=dY;
            mImageView.setLayoutParams(layoutParams);*/
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY,
                maxOverScrollX, maxOverScrollY, isTouchEvent);
    }
    public void setImageView(ImageView view){
        this.mImageView= view;
        //拿到图片的高度，必须先 image.getViewTreeObserver().addOnGlobalLayoutListener() 布局，测量完成才能拿到，否则值为0

        int height=view.getHeight();//布局完成后才可以拿到 我们设置的160dp
        mImageMeasuredHeight = view.getMeasuredHeight();//测量完成后出来的高度 我们设置的160dp

        //拿到图片的原始高度
        mOriginHeight = view.getDrawable().getIntrinsicHeight();


    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                //执行回弹动画   从 image.getMeasureHeighet() 到 mImageMeasuredHeight
               int currentHeight = mImageView.getMeasuredHeight();
                if(currentHeight>mImageMeasuredHeight) {

                  //  valueAnimator(currentHeight);//方式一利用valueAnimator

                    //方式2,重写animation.class
                    RestAnimation animation= new   RestAnimation (mImageView,currentHeight,mImageMeasuredHeight);
                    animation.setDuration(300);
                    animation.setInterpolator(new OvershootInterpolator());
                    startAnimation(animation);
                }
                //invalidate();
                break;

            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void valueAnimator(int currentHeight) {
        ValueAnimator animator = ValueAnimator.ofFloat(currentHeight, mImageMeasuredHeight);
        animator.setInterpolator(new OvershootInterpolator());//回弹插值器
        animator.setDuration(500);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float animatedValue = (Float) animation.getAnimatedValue();
                mImageView.getLayoutParams().height = (int) Float.parseFloat(animatedValue + "");

                mImageView.requestLayout();
            }
        });
    }
}
