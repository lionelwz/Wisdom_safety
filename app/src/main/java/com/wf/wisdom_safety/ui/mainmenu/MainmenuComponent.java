package com.wf.wisdom_safety.ui.mainmenu;

import com.wf.wisdom_safety.ui.SplashActivity;
import com.wf.wisdom_safety.ui.WisdomSafetyModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Administrator on 2016/7/14.
 */
@Singleton
@Component(modules = WisdomSafetyModule.class)
public interface MainmenuComponent {
    void inject(SplashActivity splashActivity);
    void inject(MainActivity mainActivity);
}