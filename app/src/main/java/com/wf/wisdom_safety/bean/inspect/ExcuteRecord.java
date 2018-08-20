package com.wf.wisdom_safety.bean.inspect;

/**
 * Created by Lionel on 2017/9/22.
 */

public class ExcuteRecord {

    private String uuid;

    private String unitId;

    private String userId;

    private String excutor;

    private String plainId;

    private String plainName;

    private Long createTime;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getExcutor() {
        return excutor;
    }

    public void setExcutor(String excutor) {
        this.excutor = excutor;
    }

    public String getPlainId() {
        return plainId;
    }

    public void setPlainId(String plainId) {
        this.plainId = plainId;
    }

    public String getPlainName() {
        return plainName;
    }

    public void setPlainName(String plainName) {
        this.plainName = plainName;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
