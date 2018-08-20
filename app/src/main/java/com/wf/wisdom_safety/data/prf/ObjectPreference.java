package com.wf.wisdom_safety.data.prf;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

/**
 * Created by Junhua Lv on 2016/3/29.
 * 存储全局对象
 */
public class ObjectPreference {

    static private final String PRF = ObjectPreference.class.getName();

    @Nullable
    static public <T> T getObject(Context context, Class<T> cls){
        SharedPreferences prf = context.getSharedPreferences(PRF, Context.MODE_PRIVATE);
        return new Gson().fromJson(prf.getString(cls.getName(), null), cls);
    }

    static public void saveObject(Context context, Object obj){
        SharedPreferences.Editor editor = context.getSharedPreferences(PRF, Context.MODE_PRIVATE).edit();
        editor.putString(obj.getClass().getName(), new Gson().toJson(obj));
        editor.apply();
    }

    static public <T> void clearObject(Context context, Class<T> cls) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PRF, Context.MODE_PRIVATE).edit();
        editor.remove(cls.getName());
        editor.apply();
    }
}