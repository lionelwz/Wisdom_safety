package cn.uetec.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2016-4-20.
 * 知识节点列表项前面的带线圆点
 */
public class ListHeadIcon extends View {

    public static final int TYPE_BOTH = 0;
    public static final int TYPE_TOP = 1;
    public static final int TYPE_BOTTOM = 2;

    Paint mPaint;
    int mDrawType = TYPE_BOTH;
    private Context mContext;

    public ListHeadIcon(Context context) {
        super(context);
        mContext = context;
    }

    public ListHeadIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public ListHeadIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @TargetApi(21)
    public ListHeadIcon(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }

    public void setDrawType(int drawType) {
        mDrawType = drawType;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == mPaint)
            init();

        int width = canvas.getWidth();
        int height = canvas.getHeight();
        mPaint.setStyle(Paint.Style.FILL);
        int radius = width / 8;
        canvas.drawCircle(width / 2, height / 2, radius, mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);
        switch (mDrawType) {
            case TYPE_BOTH:
                canvas.drawLine(width / 2, 0, width / 2, height / 2 - radius, mPaint);
                canvas.drawLine(width / 2, height / 2 + radius, width / 2, height, mPaint);
                break;
            case TYPE_TOP:
                canvas.drawLine(width / 2, 0, width / 2, height / 2 - radius, mPaint);
                break;
            case TYPE_BOTTOM:
                canvas.drawLine(width / 2, height / 2 + radius, width / 2, height, mPaint);
                break;
            default:
                break;
        }
    }

    void init(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.grey));
    }
}
