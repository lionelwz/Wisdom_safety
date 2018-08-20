package cn.uetec.util;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 206847 on 1/16/16.
 * OkHttp CacheInterceptor
 */
public class CacheInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder request = originalRequest.newBuilder();
        if (originalRequest.header("fresh") != null) {
            request.cacheControl(CacheControl.FORCE_NETWORK);
        }
        Response response = chain.proceed(request.build());
        return response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", cacheControl(3600))
                .build();
    }

    @NonNull
    private String cacheControl(int secs) {
//        String cacheHeaderValue;
//        if (networkStatus.isOnGoodConnection()) {
//            cacheHeaderValue = "public, max-age=2419200";
//        } else {
//            cacheHeaderValue = "public, only-if-cached, max-stale=2419200";
//        }
//        return cacheHeaderValue;
        return "public, max-age=" + secs;
    }
}
