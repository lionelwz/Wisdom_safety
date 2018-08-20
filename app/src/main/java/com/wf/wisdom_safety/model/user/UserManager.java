package com.wf.wisdom_safety.model.user;

import android.content.Context;

import com.wf.util.PersistentCookieStore;
import com.wf.wisdom_safety.R;
import com.wf.wisdom_safety.bean.api.ApiResponse;
import com.wf.wisdom_safety.bean.api.ApiResponseRxFunc1;
import com.wf.wisdom_safety.bean.user.AuthInfo;
import com.wf.wisdom_safety.bean.user.User;
import com.wf.wisdom_safety.data.network.FileSystemApi;
import com.wf.wisdom_safety.data.network.WisdomSafetyApi;
import com.wf.wisdom_safety.data.prf.ObjectPreference;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by Lionel on 2017/7/17.
 */

public class UserManager {

    WisdomSafetyApi mWisdomSafetyApi;
    FileSystemApi mFileSystemApi;
    private boolean mIsActive;
    private User mUser;
    Context mContext;
    private String mName;
    private String mPassword;

    public UserManager(Context context, WisdomSafetyApi api, FileSystemApi fileSystemApi) {
        this.mWisdomSafetyApi = api;
        mContext = context;
        mFileSystemApi = fileSystemApi;
    }

    /**
     * 登录
     * @param username  用户名
     * @param password  密码
     * @return 用户信息
     */
    public Observable<User> login(final String username, final String password){
        return mWisdomSafetyApi.login(username, password).map(new Func1<ApiResponse<User>, User>() {

            @Override
            public User call(ApiResponse<User> userApiResponse) {
                if (null == userApiResponse.getResult()) {
                    String description = null == userApiResponse.getDescription() ? mContext.getString(R.string.login_failed) : userApiResponse.getDescription();
                    throw new RuntimeException(description);
                }
                return userApiResponse.getResult();
            }
        }).map(new Func1<User, User>() {
            @Override
            public User call(User user) {
                mUser = user;
                mIsActive = true;
                saveNameAndPassword(mContext, username, password);
                return user;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 自动登录
     */
    public Observable<User> autoLogin(){
        mName = getDefaultName();
        mPassword = getDefaultPassword();

        return Observable.just(this).flatMap(new Func1<UserManager, Observable<User>>() {
            @Override
            public Observable<User> call(UserManager userManager) {
                if (mName.isEmpty() || mPassword.isEmpty()) {
                    throw new RuntimeException(mContext.getString(R.string.error_field_required));
                }
                return login(mName, mPassword);
            }
        });
    }

    /**
     * 是否已登录
     * @return
     */
    public boolean isActive(){
        return mIsActive;
    }

    /**
     * 保存的用户名
     * @return
     */
    public String getDefaultName(){
        AuthInfo info = ObjectPreference.getObject(mContext, AuthInfo.class);
        if (null != info)
            return info.getName();
        else
            return "";
    }

    /**
     * 保存的密码
     * @return
     */
    public String getDefaultPassword(){
        AuthInfo info = ObjectPreference.getObject(mContext, AuthInfo.class);
        if (null != info)
            return info.getPassword();
        else
            return "";
    }

    /**
     * 保存用户名和密码
     * @param context
     * @param name
     * @param password
     */
    private void saveNameAndPassword(Context context, String name, String password){
        ObjectPreference.saveObject(context, new AuthInfo(name, password));
    }

    /**
     * 清除登录信息
     * @param context
     */
    public void cleanAuthInfo(Context context){
        ObjectPreference.clearObject(context, AuthInfo.class);
    }

    /**
     * 退出登录
     */
    public Observable<Boolean> logout(){
        return mWisdomSafetyApi.logout()
                .map(new Func1<ApiResponse<String>, Boolean>() {
                    @Override
                    public Boolean call(ApiResponse<String> stringApiResponse) {
                        return 0 == stringApiResponse.getReturnCode();
                    }
                })
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        if(aBoolean) {
                            mIsActive = false;
                            cleanAuthInfo(mContext);
                            //清除Cookie
                            new PersistentCookieStore(mContext).removeAll();
                        }
                        return aBoolean;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取用户信息
     * @return
     */
    public User getUser() {
        return mUser;
    }

    /**
     * 修改密码
     * @param oldPassword    原密码
     * @param newPassword    新密码
     * @param rePassword     重复密码
     * @return true
     */
    public Observable<Boolean> changePassword(String oldPassword, String newPassword,String rePassword) {
        return mWisdomSafetyApi.changePassword(oldPassword, newPassword, rePassword)
                .map(new ApiResponseRxFunc1<ApiResponse<Integer>, Integer>("修改密码失败！"))
                .map(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer == 0;
                    }
                }).observeOn(AndroidSchedulers.mainThread());
    }

}
