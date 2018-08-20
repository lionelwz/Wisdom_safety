package com.wf.wisdom_safety.ui.user;

import com.wf.wisdom_safety.LoginActivity;
import com.wf.wisdom_safety.ui.WisdomSafetyModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Lionel on 2017/7/18.
 */

@Singleton
@Component(modules = WisdomSafetyModule.class)
public interface UserComponent {

    void inject(UserFragment userFragment);

    void inject(LoginActivity loginActivity);

    void inject(ChangePasswordActivity changePasswordActivity);
}
