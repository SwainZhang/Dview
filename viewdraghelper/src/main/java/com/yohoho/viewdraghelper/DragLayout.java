package com.yohoho.viewdraghelper;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * @author Administrator
 * @time 2016/9/16 19:43
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class DragLayout extends FrameLayout {

    private ViewDragHelper mViewDragHelper;
    private ViewGroup mLeftMenu;
    private ViewGroup mMainMenu;
    private int mMeasuredWidth;
    private int mMeasuredHeight;
    private Status mStatus=Status.Closed;//当前的状态

    public Status getStatus() {
        return mStatus;
    }

    //状态枚举
    public static enum Status{
        Closed,Draging,Opened;
    }
    public DragLayout(Context context) {
        this(context,null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //mTouchSlop 最小敏感范围，值越小越敏感，越容易滑动

        //a:初始化操作
        mViewDragHelper = ViewDragHelper.create(this, mCallBack);
    }

    //当尺寸有变化的时候被调用

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //也可以在onMeasure()里拿屏幕的宽高

        //拿到屏幕的宽度
        mMeasuredWidth = getMeasuredWidth();
        //拿到屏幕的高度
        mMeasuredHeight = getMeasuredHeight();
    }

    ViewDragHelper.Callback mCallBack=new ViewDragHelper.Callback() {
       //d:重写ViewDragHelper中的方法

        //1.根据返回结果决定当前的child是否可以被拖动

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //child 拖动的view  ， pointerId 区分多点触摸的Id

            //return child==mMainMenu;
           return true; //所有的child都可以被拖动
        }

        //当Child被捕获（拖动）的时候调用，能不能被拖动由tryCaptureView（）的返回值决定

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        //child 可以水平拖动的范围（不对child的拖动真正进行限制，仅仅决定动画执行的速度来计算动画的时长）

        @Override
        public int getViewHorizontalDragRange(View child) {
            return (int) (mMeasuredWidth*0.6f);//移动范围是屏幕宽度的60%
        }

        //2.根据建议值修正将要移动到的（横向）位置，此时没有发生真正的移动
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            // child 当前的child ,left 新的位置建议知 , dx 位置变化量

            //child.getLeft() + dx=left -----> 当前的left+手指拖动child移动的变化量=移动后的left 相当于slidingDrawerMenu中的getScrollX+moveX=ScrollX得到屏幕移动后的位置;

            if(child==mMainMenu){//只是限制主菜单
                left=fixLeft(left);
            }
            return left;//left 就是移动后的左边距
        }




        //当位置改变的时候实时调用，可以伴随动画，重新绘制，更新状态...(此时位置已经发生了改变）

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            int newLeft=left;
            if(changedView==mLeftMenu){//将手指滑动leftMenu的变化量dx,转移给mainMenu
                newLeft=mMainMenu.getLeft()+dx;
                newLeft=fixLeft(newLeft);
            }

            if(changedView==mLeftMenu){//不让leftMenu滑动
                mLeftMenu.layout(0,0,mMeasuredWidth,mMeasuredHeight);
                mMainMenu.layout(newLeft,0,mMeasuredWidth+newLeft,mMeasuredHeight);
            }

            playAnimaiton(newLeft);//伴随动画

            //为了兼容低版本，每次修改完值后重新绘制
            invalidate();

        }

        //当view被释放的时候调用可以执行动画

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {

            // 手指在 xvel=x的速度 , yvel=y的速度 向右为+，向下为+

            int maxRange= (int) (mMeasuredWidth*0.6f);
            int cLeft = mMainMenu.getLeft();
            if(cLeft>maxRange/2){
                //打开
                open(true);
            }else{
                close(true);

            }

        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
        }
    };

    //伴随动画
    private void playAnimaiton(int newLeft) {

        //根据 left 和 mainMenu 的最大偏移量来计算百分比来控制动画
        float percent =newLeft*1.0f/(mMeasuredWidth*0.6f);

        //根据percent跟新状态
        Status preState=mStatus;//前一个状态
        mStatus=upState(percent);//当前状态

        //拖动的过程中
        if(mOnDragStateChangedListener!=null) {
            mOnDragStateChangedListener.onDraging(percent);
        }

        if(!preState.equals(mStatus)){//状态发生了改变

            if(mStatus.equals(Status.Closed)){

                if(mOnDragStateChangedListener!=null){
                mOnDragStateChangedListener.onClosed();
                }

            }else if(mStatus.equals(Status.Opened)){
                if(mOnDragStateChangedListener!=null) {
                    mOnDragStateChangedListener.onOpened();
                }
            }

        }
        //伴随动画
        allAnimation(percent);


    }

    private Status upState(float percent) {
        if(percent==1){//说明是打开的
            return  Status.Opened;
        }else if(percent==0){
            return  Status.Closed;
        }else {
            return Status.Draging;
        }

    }

    private void allAnimation(float percent) {

        //为了解决低版本兼容，采用了NineOldAndroids开源的viewHelper

    /*--------------- leftMenu ----缩放，平移，渐变-----------*/

        ViewHelper.setScaleX(mLeftMenu,evaluate(percent,0.5f,1.0f));
        ViewHelper.setScaleY(mLeftMenu,0.5f+0.5f*percent);//0.5---1.0

        // range=-mMeasuredWidth/2-->0
        ViewHelper.setTranslationX(mLeftMenu, evaluate(percent,-mMeasuredWidth/2,0));
        ViewHelper.setAlpha(mLeftMenu,evaluate(percent,0.5f,1.0f));


        /*--------------- mainMenu ----缩放-----------*/
        ViewHelper.setScaleX(mMainMenu,evaluate(percent,1.0f,0.8f));
        ViewHelper.setScaleY(mMainMenu,evaluate(percent,1.0f,0.8f));

        /*--------------- 背景 ----亮度(颜色变化)-----------*/
       /* Float Argb = evaluate(percent, Color.BLACK, Color.TRANSPARENT);
        float f_argb = Float.parseFloat(Argb + "");
        getBackground().setColorFilter((int)(f_argb), PorterDuff.Mode.SRC_OVER);//把一个颜色添加到背景上面
*/
    }

    // ArgbEvaluate.class里面的 颜色混合模式
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24);
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endValue;
        int endA = (endInt >> 24);
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int)((startA + (int)(fraction * (endA - startA))) << 24) |
                (int)((startR + (int)(fraction * (endR - startR))) << 16) |
                (int)((startG + (int)(fraction * (endG - startG))) << 8) |
                (int)((startB + (int)(fraction * (endB - startB))));
    }
    // FloatEvaluate.class里面的 估算范围
    public Float evaluate(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }
    public void closeLeft(){
        close(true);
    }
    public void openLeft(){
        open(true);
    }
    private void open(boolean isSmooth){
        int maxRange= (int) (mMeasuredWidth*0.6f);
        if(isSmooth){
            //触发动画
            if(mViewDragHelper.smoothSlideViewTo(mMainMenu,maxRange,0)){//true 还没有一定到我们需要的位置

                ViewCompat.postInvalidateOnAnimation(this);//this 表示当前view所在的viewgroup
            }

        }else{
            mMainMenu.layout(maxRange,0,mMeasuredWidth+maxRange,mMeasuredHeight);

        }
    }

    private void close(boolean isSmooth){
        if(isSmooth){
            //触发动画
            if(mViewDragHelper.smoothSlideViewTo(mMainMenu,0,0)){

                ViewCompat.postInvalidateOnAnimation(this);//this 表示当前view所在的viewgroup
            }
        }else{
            mMainMenu.layout(0,0,mMeasuredWidth,mMeasuredHeight);
        }
    }

    @Override
    public void computeScroll() {

        //不断计算值持续动画
        if(mViewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    //修正left的范围
    private int fixLeft(int left) {
        if(left<0){
            return 0;
        }else if(left>=mMeasuredWidth*0.6f){
            return (int) (mMeasuredWidth*0.6f);//只可以拉动0.6
        }
        return left;
    }
     //b:传递触摸事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //传递给 mViewDragHelper
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    //c:xml布局完成后拿到child
    @Override
    protected void onFinishInflate() {  //布局完成时候调用，拿到child

        if(getChildCount()<2){
           throw new IllegalStateException("布局至少有2个child");
        }

        if(!(getChildAt(0)instanceof ViewGroup&&getChildAt(0)instanceof ViewGroup)){
            throw new IllegalArgumentException("子View必须是ViewGroup的子类");
        }

        mLeftMenu = (ViewGroup) getChildAt(0);
        mMainMenu = (ViewGroup) getChildAt(1);
    }

    /*--------------接口监听-----------*/

    public interface onDragStateChangedListener{
        void  onClosed();
        void  onOpened();
        void  onDraging(float percent);

    }
    private onDragStateChangedListener mOnDragStateChangedListener;

    public void setOnDragStateChangedListener(onDragStateChangedListener Listener){
        this.mOnDragStateChangedListener=Listener;
    }

}
