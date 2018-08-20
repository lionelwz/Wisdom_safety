package cn.uetec.util;


import android.content.Context;
import android.content.Intent;

import com.google.gson.stream.MalformedJsonException;

import rx.Subscriber;

/**
 * CookiesInvalidSubscriber
 * Created by Micheal Wang on 2016/12/1.
 */

public abstract class CookiesInvalidSubscriber<T> extends Subscriber<T> {

    private Context mContext;

    public CookiesInvalidSubscriber(Context context) {
        mContext = context;
    }

    public CookiesInvalidSubscriber(Subscriber<?> subscriber, Context context) {
        super(subscriber);
        mContext = context;
    }

    public CookiesInvalidSubscriber(Subscriber<?> subscriber, boolean shareSubscriptions, Context context) {
        super(subscriber, shareSubscriptions);
        mContext = context;
    }

    @Override
    public void onError(Throwable e) {
        if(e instanceof MalformedJsonException) {
            Intent intent = new Intent();
            intent.setAction(Constant.ACTION_LOGIN_ACTIVITY);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mContext.startActivity(intent);
        } else {
            onExceptError(e);
        }
    }

    /**
     * 除了Cookies无效以外的所有异常
     * @param e
     */
    public abstract void onExceptError(Throwable e);
}
