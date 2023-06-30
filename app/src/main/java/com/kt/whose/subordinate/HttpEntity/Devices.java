package com.kt.whose.subordinate.HttpEntity;

import java.io.Serializable;

public class Devices implements Serializable {


    private int id;
    private String name;
    private String devicesId;
    private String devicesType;
    private String picUrl;


    private boolean state;
    private long lastTime;
    private long nowTime;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDevicesId(String devicesId) {
        this.devicesId = devicesId;
    }

    public String getDevicesId() {
        return devicesId;
    }

    public void setDevicesType(String devicesType) {
        this.devicesType = devicesType;
    }

    public String getDevicesType() {
        return devicesType;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }


    public long getNowTime() {
        return nowTime;
    }

    public void setNowTime(long nowTime) {
        this.nowTime = nowTime;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
