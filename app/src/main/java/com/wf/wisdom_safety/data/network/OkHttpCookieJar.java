package com.wf.wisdom_safety.data.network;

import android.content.Context;

import com.wf.util.PersistentCookieStore;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by Junhua Lv on 2016-7-14.
 */
public class OkHttpCookieJar implements CookieJar {

    private PersistentCookieStore cookieStore;

    public OkHttpCookieJar(Context context) {
        cookieStore = new PersistentCookieStore(context);
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (cookies != null && cookies.size() > 0) {
            for (Cookie item : cookies) {
                cookieStore.add(url, item);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        return cookieStore.get(url);
    }
}
