package com.demo.huyaxiaochengxu.entity;

import java.io.Serializable;

public class DeviceDetail implements Serializable {
    private int id;
    private int type;
    private String deviceID;
    private String src;
    private String name;
    private Event effect;

    public DeviceDetail(){
    }

    public DeviceDetail(int id, String deviceID, String name, String desc, String urge) {
        this.id = id;
        this.deviceID = deviceID;
        this.name = name;
        this.effect= new Event(id,2,desc,urge);
    }

    public DeviceDetail(String deviceID, String name, String desc, String urge) {
        this.deviceID = deviceID;
        this.name = name;
        this.effect= new Event(id,2,desc,urge);
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Event getEffect() {
        return effect;
    }

    public void setEffect(Event effect) {
        this.effect = effect;
    }
}
