package view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.yohoho.gooview.R;

import java.util.ArrayList;
import java.util.List;

import utils.GeometryUtil;
import utils.Utils;

/**
 * @author Administrator
 * @time 2016/9/20 9:45
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class GooView extends View {

    private Paint mPaint;

    private float mRawX;
    private float mRawY;
    private int mStatusBarHeight;

    public GooView(Context context) {
        this(context, null);
    }

    public GooView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GooView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);

    }

    //定义固定圆的的两个点和圆心
    private PointF mStickCenter = new PointF(540f, 300f);
    float mMStickRadius = 40f;
    PointF[] mStickPoints = new PointF[2];


    //定义拖动圆的两个点和圆心
    private PointF mDragCenter = new PointF(200f, 300f);
    float mDragRadius = 60f;
    PointF[] mDragPoints = new PointF[2];


    //拖动圆与固定圆心之间的最大距离
    private float maxDistance = 380f;
    //两个圆是否是断开
    private boolean isOutOfRange;
    private boolean isDismiss;

    @Override
    protected void onDraw(Canvas canvas) {
         /*Path path=new Path();
        path.moveTo(570f, 300f);//第一个点事调到
        path.lineTo(510f, 300f);

        path.quadTo(540f,400f,480f,500f);//前面的两个是控制点，后面是结束点

        path.lineTo(600f,500f);

        path.quadTo(540f,400f,570f, 300f);//回到第一个点

        path.close();
        canvas.drawPath(path,mPaint);
*/

        /*//固定圆的的两个点和圆心
        PointF mStickCenter=new PointF(540f,300f);
        float mStickRadius=30f;
        PointF[] mStickPoints = new PointF[]{
                new PointF(570f, 300f),//右边点
                new PointF(510f, 300f) //左边点
        };

        //拖动圆的两个点和圆心
        PointF mDragCenter =new PointF(540f,500f);
        float mDragRadius=60f;
        PointF[] mDragPoints = new PointF[]{
                new PointF(480f,500f),//左边点
                new PointF(600f,500f) //右边点
        };

        //曲线的控制点
        PointF mControlPoint = new PointF(540f,400f);//两个圆心连线的中心点,三阶函数可以有两个控制点
      */

        //根据临时半径来计算
        float mStickRadius = getTempStickRadius();
        //根据两个圆心计算斜率
        float dy = mStickCenter.y - mDragCenter.y;
        float dx = mStickCenter.x - mDragCenter.x;
        double lineK = 0.1;
        if (dx != 0) {
            lineK = dy / dx;
        }

        //得到两个圆与固定斜率直线相交的点
        //固定圆
        mStickPoints = GeometryUtil.getIntersectionPoints(mStickCenter,
                mStickRadius, lineK);

        //拖动圆
        mDragPoints = GeometryUtil.getIntersectionPoints(mDragCenter,
                mDragRadius, lineK);

        //得到曲线控制点
        PointF mControlPoint = GeometryUtil.getMiddlePoint(mStickCenter, mDragCenter);

        //因为我们拿到的视屏幕的坐标，总是跟手指点击的位置存在一个差值，就是状态栏的高度，所以移动向上画布
        //保存状态，绘制结束后恢复
        canvas.save();
        canvas.translate(0, -mStatusBarHeight);
        if (!isDismiss) {
            if (!isOutOfRange) {
                //四点连线
                Path path = new Path();
                path.moveTo(mStickPoints[0].x, mStickPoints[0].y);//第一个点事调到
                path.lineTo(mStickPoints[1].x, mStickPoints[1].y);

                path.quadTo(mControlPoint.x, mControlPoint.y, mDragPoints[1].x, mDragPoints[1].y)
                ;//前面的两个是控制点，后面是结束点


                path.lineTo(mDragPoints[0].x, mDragPoints[0].y);

                path.quadTo(mControlPoint.x, mControlPoint.y, mStickPoints[0].x, mStickPoints[0].y);
                //回到第一个点

                path.close();
                canvas.drawPath(path, mPaint);

                //固定圆
                canvas.drawCircle(mStickCenter.x, mStickCenter.y, mStickRadius, mPaint);

            }
            //拖动的圆
            canvas.drawCircle(mDragCenter.x, mDragCenter.y, mDragRadius, mPaint);
        }else{
            if (isDisappearAnimating) {
                drawDisappearFlagBitmap(canvas);
            }
        }

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2.0f);
        canvas.drawCircle(mStickCenter.x, mStickCenter.y, maxDistance, mPaint);

        mPaint.reset();
        mPaint.setColor(Color.RED);
        //恢复画布
        canvas.restore();

    }
    /**
     * 绘制标记消失的Bitmap
     */
    private void drawDisappearFlagBitmap(Canvas canvas) {

        if (disappearRes!=null) {
            Drawable drawable = getResources().getDrawable(disappearRes.get(which));
            if (drawable != null) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                canvas.drawBitmap(bitmap, (float) (mDragCenter.x - bitmap.getWidth() * 0.5),
                        (float) (mDragCenter.y - bitmap.getHeight() * 0.5), mPaint);
            }
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                //就算超出范围了，点击之后又会重新恢复（配合Action_up里的第一个else使用

                isOutOfRange = false;
                isDismiss = false;

                //这里拿到的是屏幕的坐标
                mRawX = event.getRawX();
                mRawY = event.getRawY();
                Log.d("sliding_menu", mRawX + "mRawX=p");

                //修改拖动圆的圆心(这里拿到的手机屏幕坐标，必须notTitlebar，并且状态栏的高度也要考虑在内，不然总是有个差值）
                updateDragCenter(mRawX, mRawY);

                break;
            case MotionEvent.ACTION_MOVE:

                mRawX = event.getRawX();
                mRawY = event.getRawY();

                updateDragCenter(mRawX, mRawY);

                //处理断开的事件
                float currentDistance = GeometryUtil.getDistanceBetween2Points(mDragCenter,
                        mStickCenter);
                if (currentDistance > maxDistance) {
                    isOutOfRange = true;
                    invalidate();
                }

                break;
            case MotionEvent.ACTION_UP:
                if (isOutOfRange) {
                    float upDistance = GeometryUtil.getDistanceBetween2Points(mDragCenter,
                            mStickCenter);

                    if (upDistance > maxDistance) {//超过就消失
                        isDismiss = true;
                        launchDisappearAnimation(500);
                        invalidate();
                    } else {
                        // b. 拖拽超出范围,断开,放回去了,恢复
                        updateDragCenter(mStickCenter.x, mStickCenter.y);
                    }
                } else {
                    //弹回去
                    ValueAnimator animator = ValueAnimator.ofFloat(1.0f);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {

                            final PointF temPoint = new PointF(mDragCenter.x, mDragCenter.y);

                            //0.0-->1.0f
                            float percent = animation.getAnimatedFraction();
                            //动态获的移动圆的坐标
                            PointF dynamicDragCenter = GeometryUtil.getPointByPercent(temPoint,
                                    mStickCenter, percent);

                            //拖动圆心的变化值
                            updateDragCenter(dynamicDragCenter.x, dynamicDragCenter.y);

                        }
                    });
                    animator.setDuration(500);
                    animator.setInterpolator(new OvershootInterpolator(4));

                    animator.start();
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void updateDragCenter(float rawX, float rawY) {
        mDragCenter.set(rawX, rawY);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //获得状态栏的高度
        mStatusBarHeight = Utils.getStatusBarHeight(this);
        super.onSizeChanged(w, h, oldw, oldh);
    }


    // 获取固定圆半径(根据两圆圆心距离)
    private float getTempStickRadius() {
        float distance = GeometryUtil.getDistanceBetween2Points(mDragCenter, mStickCenter);

        //控制圆心距离不可已超出
        //		if(distance> farestDistance){
        //			distance = farestDistance;
        //		}
        distance = Math.min(distance, maxDistance);

        // 0.0f -> 1.0f
        float percent = distance / maxDistance;//得到比例来计算固定圆的半径变化范围


        // percent , 100% -> 20%
        return evaluate(percent, mMStickRadius, mMStickRadius * 0.2f);//返回固定圆的半径变化范围
    }


    public Float evaluate(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }
    /**
     * 启动标记的消失动画
     */
    boolean  isDisappearAnimating;
    List<Integer> disappearRes =  new ArrayList<>();
    int   which;//当前绘制的是哪一张图片
    boolean isFlagDisappear;

    private void launchDisappearAnimation(long duration) {

        disappearRes.add(R.drawable.pop1);
        disappearRes.add(R.drawable.pop2);
        disappearRes.add(R.drawable.pop3);
        disappearRes.add(R.drawable.pop4);
        disappearRes.add(R.drawable.pop5);

        isDisappearAnimating =  true;
        ValueAnimator animator = ValueAnimator.ofInt(0, 4);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
               which = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isFlagDisappear = true;
                isDisappearAnimating = false;
            }
        });
        animator.start();
    }
}
