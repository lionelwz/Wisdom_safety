package com.wf.wisdom_safety.bean.user;

/**
 * Created by Junhua Lv on 2016/4/15.
 * 登录验证信息
 */
public class AuthInfo {
    String name;
    String password;

    public AuthInfo() {
    }

    public AuthInfo(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public AuthInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public AuthInfo setPassword(String password) {
        this.password = password;
        return this;
    }
}