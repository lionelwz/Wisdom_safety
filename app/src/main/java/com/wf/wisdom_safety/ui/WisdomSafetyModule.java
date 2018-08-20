package com.wf.wisdom_safety.ui;

import android.content.Context;

import com.squareup.picasso.Picasso;
import com.wf.wisdom_safety.data.network.FileSystemApi;
import com.wf.wisdom_safety.data.network.OkHttp;
import com.wf.wisdom_safety.data.network.PicassoProvider;
import com.wf.wisdom_safety.data.network.RetrofitProvider;
import com.wf.wisdom_safety.data.network.ServerConfig;
import com.wf.wisdom_safety.data.network.WisdomSafetyApi;
import com.wf.wisdom_safety.model.user.UserManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * Created by Lionel on 2017/7/17.
 */
@Module
public class WisdomSafetyModule {

    static private Context mAppContext;
    static private Picasso mPicasso;
    static private WisdomSafetyApi mWisdomSafetyApi;
    static FileSystemApi mFileSystemApi;
    static private UserManager mUserManager;

    static public void init(Context appContext){
        mAppContext = appContext;
    }

    @Provides
    static public Context getContext() {
        return mAppContext;
    }

    @Provides
    static public OkHttpClient getOkHttpClient(){
        return OkHttp.client();
    }

    @Singleton
    @Provides
    static synchronized public Picasso getPicasso(){
        if (null == mPicasso)
            mPicasso = PicassoProvider.builder(getContext(), getOkHttpClient()).build();

        return mPicasso;
    }

    @Singleton
    @Provides
    static synchronized public WisdomSafetyApi getWisdomSafetyApi(){
        if (null == mWisdomSafetyApi)
            mWisdomSafetyApi = RetrofitProvider.builder(ServerConfig.KLSS_HTTP + "/", getOkHttpClient()).build().create(WisdomSafetyApi.class);

        return mWisdomSafetyApi;
    }

    @Singleton
    @Provides
    static synchronized public UserManager getUserManager(){
        if (null == mUserManager)
            mUserManager = new UserManager(getContext(), getWisdomSafetyApi(), getFileSystemApi());
        return mUserManager;
    }

    @Singleton
    @Provides
    static synchronized public FileSystemApi getFileSystemApi(){
        if (null == mFileSystemApi)
            mFileSystemApi = RetrofitProvider.builder(ServerConfig.FILE_HTTP + "/", getOkHttpClient()).build().create(FileSystemApi.class);

        return mFileSystemApi;
    }

  /*  @Singleton
    @Provides
    static synchronized public PushUtil getPushUtil(){
        if (null == mPushUtil)
            mPushUtil = new PushUtil(getWisdomSafetyApi(), getContext());
        return mPushUtil;
    }*/

}
