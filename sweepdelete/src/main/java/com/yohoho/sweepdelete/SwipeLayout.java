package com.yohoho.sweepdelete;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * @author Administrator
 * @time 2016/9/19 9:35
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class SwipeLayout extends FrameLayout {

    private ViewDragHelper mViewDragHelper;
    private ViewGroup mCallAndDelete;
    private ViewGroup mHeadANdName;


    public Status mStatus=Status.closed;
    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, mCallback);
    }

    public static enum  Status{
        closed,draging,opened;
    }

    @Override
    protected void onFinishInflate() {
        mCallAndDelete = (ViewGroup) getChildAt(0);
        mHeadANdName = (ViewGroup) getChildAt(1);



    }

    ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {

            return true;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {

            //如果拖动的是mHeadANdName的left

            if (child == mHeadANdName) {
                if (left >= 0) {
                    left = 0;//右边拉动不可以超过0
                } else if (left <= -mCallAndDelete.getMeasuredWidth()) {
                    left = -mCallAndDelete.getMeasuredWidth();//左拉不可以小于-mCallAndDelete
                }

            } else if (child == mCallAndDelete) {  //如果拖动的是mCallAndDelete的left

                if (left >= mHeadANdName.getMeasuredWidth()) {
                    left = mHeadANdName.getMeasuredWidth();//右拉left不可以超过屏幕
                } else if (left <= mHeadANdName.getMeasuredWidth() - mCallAndDelete
                        .getMeasuredWidth()) {
                    left = mHeadANdName.getMeasuredWidth() - mCallAndDelete.getMeasuredWidth();
                }

            }
            return left;
        }

        //当位置改变的时候实时调用，可以伴随动画，重新绘制，更新状态...(此时位置已经发生了改变）

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {

            disPatchSweepDelteEvent();//根据不同的状态完成接口监听


            //将left移动量传递给right(也可以使用left+dx 配合layout使用，见ViewDragHelper使用）

            if (changedView == mHeadANdName) {
                mCallAndDelete.offsetLeftAndRight(dx);
                return;
            }
            if (changedView == mCallAndDelete) {
                mHeadANdName.offsetLeftAndRight(dx);
            }



            //为了兼容低版本，每次修改完值后重新绘制
            invalidate();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            Log.d("sliding_menu","xvel="+xvel);


            if (mHeadANdName.getLeft() < -mCallAndDelete.getWidth() / 2.0f) {
                //打开
                Toast.makeText(getContext(), "open", Toast.LENGTH_SHORT).show();
                open(true);

            } else if(xvel>0){

                Toast.makeText(getContext(), "close", Toast.LENGTH_SHORT).show();
                close(true);

            }else{
                close(true);
            }
        }


    };

    private void disPatchSweepDelteEvent() {

        //记录上一次的状态
        Status preStatus=mStatus;

        mStatus=upDataStatus();
        mOnStateChangedListener.onDraging(this);//拖动中

        if(mStatus!=preStatus&&mOnStateChangedListener!=null){
            if(mStatus==Status.closed){
                mOnStateChangedListener.onClosed(this);//关闭
            }else if(mStatus==Status.opened){
                mOnStateChangedListener.onOpened(this);//打开
            }else if(mStatus==Status.draging){
                if(preStatus==Status.closed){
                    mOnStateChangedListener.onStartOpen(this);//开始拖动
                }else if(preStatus==Status.opened){
                    mOnStateChangedListener.onStartClose(this);//开始关闭
                }
            }
        }
    }

    private Status upDataStatus() {
        if(mHeadANdName.getLeft()==-mCallAndDelete.getMeasuredWidth()){
            return  Status.opened;
        }else if(mHeadANdName.getLeft()==0){
            return  Status.closed;
        }else{
            return  Status.draging;
        }

    }

    /**
     *
     * @param isSmooth  是否平滑的关闭
     */
    public void close(boolean isSmooth) {
        if (isSmooth) {
            slidingSmoothly(false);
        } else {
            layoutContent(false);
        }

    }

    /**
     *
     * @param isSmooth  是否平滑的关闭
     */
    public void open(boolean isSmooth) {
        if (isSmooth) {
            slidingSmoothly(true);
        } else {
            layoutContent(true);
        }
    }

    private void slidingSmoothly(boolean isOpend) {
        if (!isOpend) {
            if (mViewDragHelper.smoothSlideViewTo(mHeadANdName, 0, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            if (mViewDragHelper.smoothSlideViewTo(mHeadANdName, -mCallAndDelete.getMeasuredWidth
                    (), 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {//是否不断计算
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        super.onLayout(changed, left, top, right, bottom);

        layoutContent(false);//默认关闭右边

    }

    private void layoutContent(boolean isOpen) {

        if (isOpen) {
            mHeadANdName.layout(-mCallAndDelete.getMeasuredWidth(), 0, mHeadANdName
                    .getMeasuredWidth() - mCallAndDelete.getMeasuredWidth(), mHeadANdName
                    .getMeasuredHeight());
            mCallAndDelete.layout(mHeadANdName.getMeasuredWidth() - mCallAndDelete
                    .getMeasuredWidth(), 0, mHeadANdName.getMeasuredWidth(), mCallAndDelete
                    .getMeasuredHeight());

        } else {

            mHeadANdName.layout(0, 0, mHeadANdName.getMeasuredWidth(), mHeadANdName
                    .getMeasuredHeight());
            mCallAndDelete.layout(mHeadANdName.getMeasuredWidth(), 0, mHeadANdName
                    .getMeasuredWidth() + mCallAndDelete.getMeasuredWidth(), mCallAndDelete
                    .getMeasuredHeight());
            bringChildToFront(mCallAndDelete);
        }
    }

            /*----------------接口监听状态---------------------*/
    public interface  onStateChangedListener{
                void  onClosed(SwipeLayout mSwipeLayout);
                void  onOpened(SwipeLayout mSwipeLayout);
                void  onDraging(SwipeLayout mSwipeLayout);
                void  onStartOpen(SwipeLayout mSwipeLayout);
                void  onStartClose(SwipeLayout mSwipeLayout);
            }
    private onStateChangedListener mOnStateChangedListener;

    public void setOnStateChangedListener(onStateChangedListener Listener){
        this.mOnStateChangedListener=Listener;
    }
}
