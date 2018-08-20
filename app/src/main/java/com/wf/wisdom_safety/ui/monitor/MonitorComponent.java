package com.wf.wisdom_safety.ui.monitor;

import com.wf.wisdom_safety.ui.WisdomSafetyModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Lionel on 2017/9/15.
 */

@Singleton
@Component(modules = WisdomSafetyModule.class)
public interface MonitorComponent {

    void inject(MonitorFragment monitorFragment);

}
