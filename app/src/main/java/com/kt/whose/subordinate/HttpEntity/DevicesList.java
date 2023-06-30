package com.kt.whose.subordinate.HttpEntity;

import java.io.Serializable;
import java.util.List;

public class DevicesList implements Serializable {

    private List<Devices> devices;

    public List<Devices> getDevices() {
        return devices;
    }

    public void setDevices(List<Devices> devices) {
        this.devices = devices;
    }
}
