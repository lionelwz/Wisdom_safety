package com.wf.wisdom_safety.bean.api;

/**
 * Created by Junhua Lv on 2016-7-13.
 * Api通用响应
 */
public class ApiResponse<T> {

    int returnCode;
    T   result;
    String description;

    public int getReturnCode() {
        return returnCode;
    }

    public T getResult() {
        return result;
    }

    public String getDescription() {
        return description;
    }

}