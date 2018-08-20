package com.wf.wisdom_safety.bean.inspect;

/**
 * 建筑物信息
 * Created by Lionel on 2017/7/24.
 */
public class Building {

    private String uuid;

    private String name;

    private String code;

    private String unitId;

    private Integer minFloor;

    private Integer maxFloor;

    private Integer status;

    private long createTime;

    private Integer offdeviceCount;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId == null ? null : unitId.trim();
    }

    public Integer getMinFloor() {
        return minFloor;
    }

    public void setMinFloor(Integer minFloor) {
        this.minFloor = minFloor;
    }

    public Integer getMaxFloor() {
        return maxFloor;
    }

    public void setMaxFloor(Integer maxFloor) {
        this.maxFloor = maxFloor;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Integer getOffdeviceCount() {
        return offdeviceCount;
    }

    public void setOffdeviceCount(Integer offdeviceCount) {
        this.offdeviceCount = offdeviceCount;
    }
}
