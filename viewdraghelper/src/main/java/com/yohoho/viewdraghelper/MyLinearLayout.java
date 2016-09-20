package com.yohoho.viewdraghelper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * @author Administrator
 * @time 2016/9/17 18:54
 * @des 为了使得控件leftMenu处于打开的状态的时候，拦截mainMenu事件,并且在手指松开的时候关闭lefMenu
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class MyLinearLayout extends LinearLayout {
    private DragLayout mDragLayout;

    public MyLinearLayout(Context context) {
        this(context, null);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDragLayout(DragLayout dragLayout) {
        this.mDragLayout = dragLayout;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mDragLayout.getStatus().equals(DragLayout.Status.Closed)) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return true;//打开就拦截掉mainMenu的事件
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        if (mDragLayout.getStatus().equals(DragLayout.Status.Closed)) {
            return super.onTouchEvent(event);
        } else {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mDragLayout.closeLeft();
            }
            return true;
        }

    }
}
