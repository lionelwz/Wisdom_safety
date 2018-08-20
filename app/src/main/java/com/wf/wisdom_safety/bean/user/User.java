package com.wf.wisdom_safety.bean.user;

/**
 * Created by Junhua Lv on 2016-7-13.
 * 用户实体类
 */
public class User {
    String username;
    String name;
    String id;
    String avatar;
    int status;
    int userType;
    String relativeSchoolId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getRelativeSchoolId() {
        return relativeSchoolId;
    }

    public void setRelativeSchoolId(String relativeSchoolId) {
        this.relativeSchoolId = relativeSchoolId;
    }
}
