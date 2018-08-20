package com.wf.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.Locale;

/**
 * Created by Junhua Lv on 2016-5-20.
 * 圆形百分比动态显示
 */
public class PercentCircleView extends View {

    int mDrawingProgress;
    float mPercentage;
    String mPercentageStr;
    Paint mPaint;
    RectF mCircleRect = new RectF();
    Rect mValueTextRect = new Rect();
    Rect mSymbolTextRect = new Rect();
    int mTextSize;
    int baseLine;

    /**
     * 设置百分比 0~100
     * @param percentage 百分比
     */
    public void setPercentage(float percentage){
        mPercentage = percentage;
        mPaint = null;
        invalidate();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        mDrawingProgress = 0;
    }

    void init(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        int strokeWidth = getWidth() / 32 + 1;
        mPaint.setStrokeWidth(strokeWidth);
        mPercentageStr = String.format(Locale.CHINA, "%d", (int)mPercentage);
        mTextSize = getHeight() / 3;
        mPaint.setTextSize(mTextSize / 2);
        mPaint.getTextBounds("%", 0, 1, mSymbolTextRect);
        mPaint.setTextSize(mTextSize);
        mPaint.getTextBounds(mPercentageStr, 0, mPercentageStr.length(), mValueTextRect);
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        baseLine = (getHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;

        mCircleRect.top = strokeWidth;
        mCircleRect.left = strokeWidth;
        mCircleRect.right = getWidth() - strokeWidth;
        mCircleRect.bottom = getHeight() - strokeWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == mPaint){
            init();
        }

        int width = getWidth();
        int height = getHeight();
        int color = mPercentage > 70 ? Color.GREEN : Color.RED;
        // 画圆圈
        int strokeWidth = getWidth() / 32 + 1;
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(0xFFd0d0d0);
        canvas.drawArc(mCircleRect, -90, 360, false, mPaint);
        mPaint.setColor(color);
        mDrawingProgress += 10;
        canvas.drawArc(mCircleRect, -90, mPercentage * mDrawingProgress / 100, false, mPaint);
        // 画百分比数字
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        int x = (width - mValueTextRect.width() - mSymbolTextRect.width()) / 2;
        //int y = (height + mValueTextRect.height()) / 2;
        int y = baseLine;
        mPaint.setTextSize(mTextSize);
        canvas.drawText(mPercentageStr, x, y, mPaint);
        // 画%
        x = x + mValueTextRect.width() + mSymbolTextRect.width() / 2;
//        y = y + mValueTextRect.height() - mSymbolTextRect.height();
        mPaint.setTextSize(mTextSize / 2);
        canvas.drawText("%", x, y, mPaint);

        if (mDrawingProgress < 360)
            super.invalidate();
        else
            mDrawingProgress = 0;
    }

    public PercentCircleView(Context context) {
        super(context);
    }

    public PercentCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PercentCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public PercentCircleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
