package com.wf.wisdom_safety.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jude.swipbackhelper.SwipeBackHelper;
import com.wf.util.PersistentCookieStore;
import com.wf.util.RxSubscriptionCollection;
import com.wf.wisdom_safety.LoginActivity;
import com.wf.wisdom_safety.R;
import com.wf.wisdom_safety.bean.user.User;
import com.wf.wisdom_safety.model.user.UserManager;
import com.wf.wisdom_safety.service.ListenNetStateService;
import com.wf.wisdom_safety.ui.mainmenu.DaggerMainmenuComponent;
import com.wf.wisdom_safety.ui.mainmenu.MainActivity;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Lionel on 2017/7/17.
 */

public class SplashActivity extends AppCompatActivity {

    @Inject
    RxSubscriptionCollection mRxSubscriptionCollection;
    @Inject
    UserManager mUserManager;
    @Inject
    Context mContext;

    PersistentCookieStore mCookieStore;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // enable status bar tint
        SwipeBackHelper.getCurrentPage(this)
                .setSwipeBackEnable(false);

        DaggerMainmenuComponent.create().inject(this);
        mCookieStore = new PersistentCookieStore(mContext);
        Subscription subscribe = Observable.just(0).delay(2000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                if(mUserManager.getDefaultName().trim().isEmpty() ||
                        mUserManager.getDefaultPassword().trim().isEmpty()
                        || mCookieStore.getCookies().isEmpty()) {
                    startHomeActivity();
                } else {
                    if(!mUserManager.isActive()) {
                        login();
                    } else {
                        startHomeActivity();
                    }
                }
            }
        });
        mRxSubscriptionCollection.add(subscribe);
        this.startService(new Intent(this, ListenNetStateService.class));

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRxSubscriptionCollection.cancelAll();
    }

    private void startHomeActivity() {
        if(mUserManager.isActive()) {
            //mPushUtil.init();
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            SplashActivity.this.startActivity(intent);
        } else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            SplashActivity.this.startActivity(intent);
        }
        SplashActivity.this.finish();
    }

    /**
     * 登录
     */
    public void login(){
        Subscription subscription = mUserManager.autoLogin().subscribe(new Observer<User>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                startHomeActivity();
            }

            @Override
            public void onNext(User user) {
                startHomeActivity();
            }
        });
        mRxSubscriptionCollection.add(subscription);
    }
}
