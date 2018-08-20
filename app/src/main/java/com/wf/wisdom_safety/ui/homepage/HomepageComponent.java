package com.wf.wisdom_safety.ui.homepage;

import com.wf.wisdom_safety.ui.WisdomSafetyModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Lionel on 2017/9/13.
 */
@Singleton
@Component(modules = WisdomSafetyModule.class)
public interface HomepageComponent {

    void inject(HomepageFragment homepageFragment);

}
