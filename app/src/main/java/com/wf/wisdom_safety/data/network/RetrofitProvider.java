package com.wf.wisdom_safety.data.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

/**
 * Created by Junhua Lv on 2016/6/26.
 * Retrofit2 + gson + okhttp3
 */
public class RetrofitProvider {
//    static private ConcurrentMap<String, Retrofit> retrofitMap = new ConcurrentHashMap<>();
//
//    static public Retrofit get(String baseUrl){
//        if (null == retrofitMap.get(baseUrl)) {
//            synchronized (RetrofitProvider.class) {
//                if (null == retrofitMap.get(baseUrl)) {
//                    Retrofit retrofit = builder(baseUrl, OkHttp.client()).build();
//                    retrofitMap.put(baseUrl, retrofit);
//                }
//            }
//        }
//        return retrofitMap.get(baseUrl);
//    }

    static public Retrofit.Builder builder(String baseUrl, OkHttpClient client){
        return new Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create()).client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()));
    }
}