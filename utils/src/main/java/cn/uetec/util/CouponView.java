package cn.uetec.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by Administrator on 2016/12/1.
 */

public class CouponView extends View {

    private int mWidth;
    private int mHeight;
    private Paint mTrianglePaint;
    private Paint mCirclePaint;
    private Paint mDashPaint;
    private int mTriangleColor = Color.parseColor("#f97c00");
    private Path mTrianglePath;
    private Path mDashPath;
    private int mTriangleBottomLen;
    private int mTriangleHighLen;
    private int mRadius;
    private int mCircleY;
    private boolean mEnable = true;
    private int mDisableColor = Color.parseColor("#e0e0e0");

    public CouponView(Context context) {
        super(context);
        init();
    }

    public CouponView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CouponView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 优惠券是否可用
     * @return
     */
    public boolean isEnable() {
        return mEnable;
    }

    /**
     * 设置优惠券是否可用
     * @param enable
     */
    public void setEnable(boolean enable) {
        mEnable = enable;
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        if ( 0 == width || 0 == height ){
            super.onMeasure (widthMeasureSpec, heightMeasureSpec);
            return;
        }
        mWidth = width;
        mHeight = height;
        setMeasuredDimension(width, height);
        initView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        mTriangleBottomLen = 18;
        mTriangleHighLen = mTriangleBottomLen / 2;
        mRadius = mHeight / 20;
        mCircleY = (mHeight + mTriangleBottomLen) * 2 / 3;
        if(mEnable) {
            //画两边半圆
            canvas.drawCircle(0, mCircleY, mRadius, mCirclePaint);
            canvas.drawCircle(mWidth, mCircleY, mRadius, mCirclePaint);
            //画虚线
            mDashPath.moveTo(mRadius, mCircleY);
            mDashPath.lineTo(mWidth - mRadius, mCircleY);
            mDashPath.moveTo(mWidth / 4, mHeight / 12);
            mDashPath.lineTo(mWidth /4, mCircleY - mHeight / 15);
            canvas.drawPath(mDashPath, mDashPaint);

        } else {
            mTrianglePaint.setColor(mDisableColor);
            mDashPath.moveTo(mWidth / 4, mHeight / 12);
            mDashPath.lineTo(mWidth /4, mCircleY - mHeight / 15);
        }
        //画上方锯齿

        mTrianglePath.moveTo(0, 0);
        mTrianglePath.lineTo(mWidth, 0);
        mTrianglePath.moveTo(0, 0);
        for(int len = 0; len <= mWidth; len += mTriangleBottomLen / 2) {
            if(len % mTriangleBottomLen != 0) {
                mTrianglePath.lineTo(len, mTriangleHighLen);
            } else {
                mTrianglePath.lineTo(len, 0);
                mTrianglePath.moveTo(len, 0);
            }
        }
        mTrianglePath.lineTo(mWidth, 0);
        canvas.drawPath(mTrianglePath, mTrianglePaint);

    }

    private void init() {
        mTrianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTrianglePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTrianglePaint.setColor(mTriangleColor);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mCirclePaint.setColor(Color.parseColor("#dcdcdc"));

        mDashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDashPaint.setStyle(Paint.Style.STROKE);
        mDashPaint.setColor(Color.parseColor("#cccccc"));
        mDashPaint.setStrokeWidth(3);
        PathEffect effect = new DashPathEffect(new float[]{30, 6}, 0);
        mDashPaint.setPathEffect(effect);

        mTrianglePath = new Path();
        mDashPath = new Path();

    }

    private void initView() {


    }
}
