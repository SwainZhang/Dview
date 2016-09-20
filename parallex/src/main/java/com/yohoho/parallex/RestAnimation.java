package com.yohoho.parallex;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;

/**
 * @author Administrator
 * @time 2016/9/18 13:28
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class RestAnimation extends Animation {

    private ImageView mImageView;
    private int mCurrentHeight;
    private int mImageMeasuredHeight;


    public RestAnimation(ImageView imageView, int currentHeight, int imageMeasuredHeight) {

        mImageView = imageView;
        mCurrentHeight = currentHeight;
        mImageMeasuredHeight = imageMeasuredHeight;
    }

    // 类似addUpdateListener
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        // interpolatedTime 是个百分比

         mImageView.getLayoutParams().height= evaluate(interpolatedTime,mCurrentHeight,mImageMeasuredHeight);
         mImageView.requestLayout();

        super.applyTransformation(interpolatedTime, t);
    }

    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        return (int)(startInt + fraction * (endValue - startInt));
    }
}
