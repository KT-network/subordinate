package com.kt.whose.subordinate.HttpEntity;

import java.util.List;

public class Login {

    private List<Devices> devices;
    private String token;

    public void setDevices(List<Devices> devices) {
        this.devices = devices;
    }

    public List<Devices> getDevices() {
        return devices;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }


}
