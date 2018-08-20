package com.wf.wisdom_safety.data.network;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by Junhua Lv on 2016/6/25.
 * OkHttp 单例
 */
public class OkHttp {
    static private final String DEFAULT_CACHE_DIR = "OkHttpCache";
    static private final int DEFAULT_CACHE_SIZE = 10 * 1024 * 1024;
    private static OkHttpClient mInstance;
    private static Cache mCache;
    private static OkHttpCookieJar mOkHttpCookieJar;

    public static OkHttpClient client() {
        if (null == mInstance) {
            synchronized (OkHttp.class) {
                if (null == mInstance) {
                    mInstance = getDefaultBuilder().build();
                }
            }
        }
        return mInstance;
    }

    private OkHttp() {
    }

    static public void createCache(File appCacheDir){
        synchronized (OkHttp.class){
            if (null == mCache){
                File cacheDir = new File(appCacheDir, DEFAULT_CACHE_DIR);
                mCache = new Cache(cacheDir, DEFAULT_CACHE_SIZE);
            }
        }
    }

    static public void setCookieJar(OkHttpCookieJar cookieJar){
        if (null == mOkHttpCookieJar)
            mOkHttpCookieJar = cookieJar;
    }

    static public void create(OkHttpClient.Builder builder){
        synchronized (OkHttp.class){
            mInstance = builder.build();
        }
    }

    static public OkHttpClient.Builder getDefaultBuilder(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).cache(mCache);
        if (null != mOkHttpCookieJar)
            builder.cookieJar(mOkHttpCookieJar);

        return builder;
    }
}
