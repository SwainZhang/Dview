package com.yohoho.slidingdrawermenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * @author Administrator
 * @time 2016/9/15 22:04
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class SlidingMenu extends ViewGroup {

    private View mLeft;
    private View mContent;
    private int mLeftWidth;
    private int mLeftHeight;
    private float mDownX;
    private float mDownY;
    private int mScrollX;
    private Scroller mScroller;
    private boolean    isShowLeft=false;



    public SlidingMenu(Context context) {
        this(context,null);
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);


        mScroller=new Scroller(context);//初始化scroller

    }

    @Override
    protected void onFinishInflate() {
        //xml 加载完成时的回调
       mLeft=getChildAt(0);
        mContent=getChildAt(1);

        LayoutParams leftParams = mLeft.getLayoutParams();
        mLeftWidth = leftParams.width;
        mLeftHeight = leftParams.height;

    }

    //作为viewgroup 必须测量child的大小，否则child没有大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量child

        /*
        widthMeasureSpec，heightMeasureSpec是一个期望值（其实也就是父容器自己的大小32bit)，代表父容器对孩子的期望大小，32bit中 2bit=模式，30bit=实际大小。通过MeasureSpec.makeMeasureSpec（30bit,2bit）可以将大小和模式组装起来，

        通过MeasureSpec.getMode(32bit); 得到2bit模式，unspecified （不指定，随意大小），  exactly（精确的，如match parent,100dp), at_most(最大的，也就是<=某个数值）

        通过MeasureSpec.getSize(32bit)可以得到30bit的实际大小

        */


        //测量left
        int leftWidthMeasureSpec=MeasureSpec.makeMeasureSpec(mLeftWidth,MeasureSpec.EXACTLY);//根据我们自己定义的布局的宽高来组成（在布局完成的时候可以拿到布局参数）

        mLeft.measure(leftWidthMeasureSpec,heightMeasureSpec);//宽度自己定，高度跟随父容器的期望
        //测量content

        mContent.measure( widthMeasureSpec, heightMeasureSpec);//内容跟父容器是一样大小

        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);//如果不自己设定父容器就会根据自己来定义

        //设置父容器对自己的实际值要求
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

       //给child布局（相对于父容器，child在父容中的位置）

        int   leftWidth=mLeft.getMeasuredWidth();  //getWidth,getHeight()的数据来源于layout(l,t,r,b)，调用layout布局后才有，否则为0；
        int leftHeight=mLeft.getMeasuredHeight();  //getMeasureWidth,getMeasureHeight()的数据来源于setMeasureDemension(),也就是必须先测量measure 这个时候会在measure里面调用onMeasure接着在onMeasure会调用setMeasureDemension()来给自己设定mMeasureWidth和mMeasureHeight，最后通过getMeasureWidth()，getMeasureHeight()得到测量的宽高，否则没有值就为0;

        //给left布局
        int left=-leftWidth;
        int top =0;
        int right=0;
        int bottom=leftHeight;
        mLeft.layout(left,top,right,bottom);

        //给content布局

        //
        mContent.layout(0,0,mContent.getMeasuredWidth(),mContent.getMeasuredHeight());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        //当我们手指触发left来滑动的时候，发现不可以滑动，是因为left 的TextView被我们设置了点击事件，如果我们不拦截滑动事件就传递进到TextView,

        //拦截滑动事件
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();

               if(Math.abs(moveX-mDownX)>Math.abs(moveY-mDownY)){//拦截滑动事件
                   return  true;
               }

                break;
            case MotionEvent.ACTION_UP:

                break;
            default:
                break;
        }

        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //scrollTo(相对于原点，标准移动),
        // scrollBy(现对于上次停下的位置，是增量移动)
        // 都是手机屏幕相对于屏幕的左上角为坐标原点为坐标系在移动，而不是View在移动

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();

                int diffX=(int)(moveX-mDownX+0.5f);

                //手指滑动后屏幕的位置
                mScrollX = getScrollX()-diffX;//getScrollX()得到屏幕当前的位置+手指滑动的距离=屏幕滑动后的位置

               if(mScrollX >0){

                    //说明手指是在向右移动时那么应该是拉出left
                    scrollTo(0,0);

                }else if(mScrollX <0&& mScrollX <-mLeft.getMeasuredWidth()){
                    //说明手指是在向左移动那么应该是关闭left
                       scrollTo(-mLeft.getMeasuredWidth(),0);

                }else {
                    scrollBy(-diffX, 0);//屏幕移动的方向应该是和手指移动的方向相反

                }
                mDownX=moveX;
                mDownY=moveY;
                break;
            case MotionEvent.ACTION_UP:

                int mCurrentX=getScrollX();
                switchMenu(mCurrentX<=-mLeft.getMeasuredWidth()/2);//松开控制关闭或者打开

                break;
            default:
                break;
        }


        return true;
    }

    private void switchMenu(boolean isShowLeft) {
        int mCurrentX=getScrollX();

        this.isShowLeft=isShowLeft;//记录左侧菜单的状态

        if(isShowLeft){//该打开
           // scrollTo(-mLeft.getMeasuredWidth(),0);//屏幕移动的方向应该是和手指移动的方向相反*/

            //慢慢移动
            int startX=mCurrentX;
            int startY=0;

            int endX=-mLeft.getMeasuredWidth();
            int endY=0;

            int dx=endX-startX;
            int dy=endY-startY;

            int duration=Math.abs(dx)*10;
            if(duration>600){
                duration=600;
            }

            //模拟数据变化（scroller 必须重写 computeScroll()拿到模拟计算后的值
            mScroller.startScroll(startX,startY,dx,dy,duration);//不断的模拟变化的x,y的值
        }else{
            //关闭

           // scrollTo(0,0);
            int startX=mCurrentX;
            int startY=0;

            int endX=0;
            int endY=0;

            int dx=endX-startX;
            int dy=endY-startY;

            int duration=Math.abs(dx)*10;
            if(duration>600){
                duration=600;
            }

            mScroller.startScroll(startX,startY,dx,dy,duration);
        }
        invalidate();//刷新 Draw()-->dispatchDraw()-->drawChild()-->child.draw()-->computeScroll()
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()){//正在计算滚动
            scrollTo(mScroller.getCurrX(),0);
            invalidate();//不断的触发
        }
    }

    /*
    注意：click 事件应该在view 或者 activity中写，不可以在viewGroup中写
    public void clickTab(View v){
        Log.d("sliding_menu","点击了");
    }
    */
    public void toggle(){

    //每一次我们松开的时候都会走action_up，而我们给的条件满足的时候就会打开，或者关闭，并记录状态，这里手动的调用了把我们把状态改变了
        switchMenu(!isShowLeft);
    }

}
