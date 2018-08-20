package com.wf.wisdom_safety.bean.inspect;

import java.io.Serializable;

/**
 * Created by Lionel on 2017/10/18.
 */

public class InspectVo implements Serializable {

    private static final long serialVersionUID = 8633299996744734593L;

    private String uuid;

    private String name;//名称

    private Double lat;

    private Double lon;

    private String unitName;

    private String addr;

    private String contactPerson;

    private String contactPhone;

    private String normalCount;

    private String faultCount;

    private String brokenCount;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getNormalCount() {
        return normalCount;
    }

    public void setNormalCount(String normalCount) {
        this.normalCount = normalCount;
    }

    public String getFaultCount() {
        return faultCount;
    }

    public void setFaultCount(String faultCount) {
        this.faultCount = faultCount;
    }

    public String getBrokenCount() {
        return brokenCount;
    }

    public void setBrokenCount(String brokenCount) {
        this.brokenCount = brokenCount;
    }
}
