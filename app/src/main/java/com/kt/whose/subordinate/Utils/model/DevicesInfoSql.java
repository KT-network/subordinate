package com.kt.whose.subordinate.Utils.model;


import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class DevicesInfoSql extends LitePalSupport {

    private long id;

    // 设备名称
    private String name;
    // 设备id
    private String devicesId;

    // 设备类型
    private String devicesType;

    private String picUrl;

    private String devicesWifiSsid;

    private String devicesWifiPwd;

    private String devicesWifiIp;

    private String devicesWifiGateway;

    private String devicesMqttHost;

    private String devicesMqttPort;

    private String devicesMqttUser;

    private String devicesMqttPwd;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDevicesId() {
        return devicesId;
    }

    public void setDevicesId(String devicesId) {
        this.devicesId = devicesId;
    }

    public String getDevicesType() {
        return devicesType;
    }

    public void setDevicesType(String devicesType) {
        this.devicesType = devicesType;
    }

    public String getDevicesWifiSsid() {
        return devicesWifiSsid;
    }

    public void setDevicesWifiSsid(String devicesWifiSsid) {
        this.devicesWifiSsid = devicesWifiSsid;
    }

    public String getDevicesWifiPwd() {
        return devicesWifiPwd;
    }

    public void setDevicesWifiPwd(String devicesWifiPwd) {
        this.devicesWifiPwd = devicesWifiPwd;
    }

    public String getDevicesWifiIp() {
        return devicesWifiIp;
    }

    public void setDevicesWifiIp(String devicesWifiIp) {
        this.devicesWifiIp = devicesWifiIp;
    }

    public String getDevicesWifiGateway() {
        return devicesWifiGateway;
    }

    public void setDevicesWifiGateway(String devicesWifiGateway) {
        this.devicesWifiGateway = devicesWifiGateway;
    }

    public String getDevicesMqttHost() {
        return devicesMqttHost;
    }

    public void setDevicesMqttHost(String devicesMqttHost) {
        this.devicesMqttHost = devicesMqttHost;
    }

    public String getDevicesMqttPort() {
        return devicesMqttPort;
    }

    public void setDevicesMqttPort(String devicesMqttPort) {
        this.devicesMqttPort = devicesMqttPort;
    }

    public String getDevicesMqttUser() {
        return devicesMqttUser;
    }

    public void setDevicesMqttUser(String devicesMqttUser) {
        this.devicesMqttUser = devicesMqttUser;
    }

    public String getDevicesMqttPwd() {
        return devicesMqttPwd;
    }

    public void setDevicesMqttPwd(String devicesMqttPwd) {
        this.devicesMqttPwd = devicesMqttPwd;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
