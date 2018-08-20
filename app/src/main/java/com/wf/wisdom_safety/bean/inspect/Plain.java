package com.wf.wisdom_safety.bean.inspect;

/**
 * 巡检计划
 * Created by Lionel on 2017/7/22.
 */

public class Plain {

    private String uuid;

    private String unitId;

    private String name;

    private Integer cycle;

    private Integer type;

    private String busStart;

    private String busEnd;

    private long startTime;

    private long endTime;

    private String exceptr;

    private String excuterId;

    private String excuter;

    private Integer status;

    private long createTime;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId == null ? null : unitId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getCycle() {
        return cycle;
    }

    public void setCycle(Integer cycle) {
        this.cycle = cycle;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getBusStart() {
        return busStart;
    }

    public void setBusStart(String busStart) {
        this.busStart = busStart == null ? null : busStart.trim();
    }

    public String getBusEnd() {
        return busEnd;
    }

    public void setBusEnd(String busEnd) {
        this.busEnd = busEnd == null ? null : busEnd.trim();
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getExceptr() {
        return exceptr;
    }

    public void setExceptr(String exceptr) {
        this.exceptr = exceptr == null ? null : exceptr.trim();
    }

    public String getExcuterId() {
        return excuterId;
    }

    public void setExcuterId(String excuterId) {
        this.excuterId = excuterId == null ? null : excuterId.trim();
    }

    public String getExcuter() {
        return excuter;
    }

    public void setExcuter(String excuter) {
        this.excuter = excuter;
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

}
