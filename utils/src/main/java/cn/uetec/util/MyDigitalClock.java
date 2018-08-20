package cn.uetec.util;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;

public class MyDigitalClock extends TextView {

	Calendar mCalendar;
    private final static String m12 = "H:mm aa";//h:mm:ss aa
    private final static String m24 = "k:mm";//k:mm:ss
    private FormatChangeObserver mFormatChangeObserver;

    private Runnable mTicker;
    private Handler mHandler;

    private boolean mTickerStopped = false;

    String mFormat;
    private boolean isSetFormat = false;
    
    

    public void setmFormatChangeObserver(FormatChangeObserver mFormatChangeObserver) {
		this.mFormatChangeObserver = mFormatChangeObserver;
	}

	public MyDigitalClock(Context context) {
        super(context);
        initClock(context);
        
    }

    public MyDigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        initClock(context);
    }

    private void initClock(Context context) {
        //Resources r = context.getResources();

        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }

        mFormatChangeObserver = new FormatChangeObserver();
        context.getContentResolver().registerContentObserver(
                Settings.System.CONTENT_URI, true, mFormatChangeObserver);

        setFormat();
    }

	@Override
	protected void onAttachedToWindow() {
		Log.i("MyDigitalClock", "onAttachedToWindow==");
    	mTickerStopped = false;
        super.onAttachedToWindow();
        mHandler = new Handler();

        /**
         * requests a tick on the next hard-second boundary
         */
        mTicker = new Runnable() {
                public void run() {
                    if (mTickerStopped) 
                    	return;
                    mCalendar.setTimeInMillis(System.currentTimeMillis());
                    setText(DateFormat.format(mFormat, mCalendar));
                    invalidate();
                    long now = SystemClock.uptimeMillis();
                    long next = now + (1000 - now % 1000);
                    mHandler.postAtTime(mTicker, next);
                }
            };
        mTicker.run();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTickerStopped = true;
        Log.i("MyDigitalClock", "onDetachedFromWindow==");
        mHandler.removeCallbacks(mTicker);
    }

    /**
     * Pulls 12/24 mode from system settings
     */
    private boolean get24HourMode() {
        return DateFormat.is24HourFormat(getContext());
    }

    private void setFormat() {
        if (get24HourMode()) {
            mFormat = m24;
        } else {
            mFormat = m12;
        }
    }
    
    public void setFormat(String format) {
    	this.mFormat = format;
    	isSetFormat = true;
    }

    private class FormatChangeObserver extends ContentObserver {
        public FormatChangeObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
        	if(!isSetFormat){
        		setFormat();
        	}
        }
    }

}
