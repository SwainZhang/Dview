package com.yohoho.slidingmenu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author Administrator
 * @time 2016/9/14 15:28
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class SlidingToggle extends View {

    private static  final int ACTION_DOWN=0;
    private static  final int ACTION_MOVE=1;
    private static  final int ACTION_UP=2;
    private static  final int ACTION_NULL=-1;
    private  int mState=ACTION_NULL;

    private Bitmap mSlidingToggle;
    private Bitmap mSwitchBg;
    private Paint mPaint;
    private boolean isOpened=false;
    private float mDownX;

    public SlidingToggle(Context context) {
        this(context,null);
    }

    public SlidingToggle(Context context, AttributeSet attrs) {
        super(context, attrs);


    }

    //设置背景
    public void setBackgroundBitmapResource(int res){
        mSwitchBg = BitmapFactory.decodeResource(getResources(),res);
    }

    //设置滑动开关
    public void setSlidingToggleBitmapResource(int res){
        mSlidingToggle = BitmapFactory.decodeResource(getResources(),res);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //将背景图片的大小作为画布的大小
        if(mSwitchBg!=null){
            int width=mSwitchBg.getWidth();
            int height=mSwitchBg.getHeight();

            setMeasuredDimension(width,height);
        }else{

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制背景
        if(mSwitchBg!=null){
            canvas.drawBitmap(mSwitchBg,0,0,mPaint);
        }
        if(mSlidingToggle==null) {
            return;
        }
        int slidingWidth = mSlidingToggle.getWidth();//滑动块的宽度(这个也是根据画布的坐标系来的

        switch (mState) {
            case ACTION_DOWN:
            case ACTION_MOVE://move的时候mDownX一直是变化的，所有与donw的的行为是一样的
                if(!isOpened) {
                    if (mDownX < slidingWidth / 2) {
                        //按下的是滑块的左边不移动
                        canvas.drawBitmap(mSlidingToggle,0,0,mPaint);
                    }else {

                        float left=mDownX- slidingWidth / 2;//中线滑动的距离就应该是left滑动的距离
                        float maxLeft=mSwitchBg.getWidth()-slidingWidth;
                        if(left>maxLeft){
                            left=maxLeft;//控制滑块不可以滑动出背景
                        }
                        //按下的是滑块的右边滑块的中线移动到我们点击的位置
                        canvas.drawBitmap(mSlidingToggle,left,0,mPaint);
                    }
                }else {
                    //是关闭的状态
                    float middle=mSwitchBg.getWidth()-slidingWidth/2;
                    if(mDownX<middle){
                        //点击滑块的左侧
                       float left=mDownX-slidingWidth/2;
                        float minLeft=0;
                        if(left<minLeft){
                            left=minLeft;
                        }
                        canvas.drawBitmap(mSlidingToggle,left,0,mPaint);
                    }else{
                        //点击滑块的右侧那么就不要移动
                        canvas.drawBitmap(mSlidingToggle,mSwitchBg.getWidth()-mSlidingToggle.getWidth(),0,mPaint);
                    }

                }


                break;
            case ACTION_UP:
            case ACTION_NULL:
                if(!isOpened){
                    canvas.drawBitmap(mSlidingToggle,0,0,mPaint);
                }else {
                    canvas.drawBitmap(mSlidingToggle,mSwitchBg.getWidth()-mSlidingToggle.getWidth(),0,mPaint);
                }

                break;
            default:
                break;
        }
        //按下的时候是关闭的

    }

    @Override
        public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mState=ACTION_DOWN;
                //getX是以画布为参考系，getRawX 是以手机屏幕为参考系
                mDownX = event.getX();

                invalidate();//在主线程中刷新
                //postInvalidate();//在子线程中刷新

                break;
            case MotionEvent.ACTION_MOVE:
                mState=ACTION_MOVE;
                mDownX = event.getX();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
               mState=ACTION_UP;
                mDownX = event.getX();

                //松开判断滑块的停止滑动的位置与背景中线的位置
                if(mDownX >mSwitchBg.getWidth()/2f && !isOpened){
                    isOpened=true;
                    if(mOnToggleStateListener!=null){
                        mOnToggleStateListener.onToggleStateChanged(true);
                    }
                }else if(mDownX <=mSwitchBg.getWidth()/2f && isOpened) {
                    isOpened=false;
                    if(mOnToggleStateListener!=null){
                        mOnToggleStateListener.onToggleStateChanged(false);
                    }
                }
                invalidate();
                break;

            default:
                break;
        }

        return true;
    }
    public void setOnToggleStateListener(onToggleStateListener listener){
        this.mOnToggleStateListener=listener;
    }
    onToggleStateListener mOnToggleStateListener;
    interface onToggleStateListener{
        void onToggleStateChanged(boolean isOpened);
    }
}
