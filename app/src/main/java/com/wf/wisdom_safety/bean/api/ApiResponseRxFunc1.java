package com.wf.wisdom_safety.bean.api;

import com.wf.util.UserReadableException;

import rx.functions.Func1;

/**
 * Created by Junhua Lv on 2016-7-15.
 * 默认处理
 */
public class ApiResponseRxFunc1<T extends ApiResponse<R>, R> implements Func1<T, R> {

    String mMessage;

    /**
     * 当description为空时，设置的信息
     * @param message 字符串
     */
    public ApiResponseRxFunc1(String message) {
        mMessage = message;
    }

    @Override
    public R call(T t) {
        if (null == t.getResult()) {
            if (null == t.getDescription())
                throw new UserReadableException(mMessage);
            else
                throw new UserReadableException(t.getDescription());
        }

        return t.getResult();
    }
}