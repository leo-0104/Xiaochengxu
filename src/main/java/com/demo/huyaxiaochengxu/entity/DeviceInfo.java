package com.demo.huyaxiaochengxu.entity;

//特效事件实体类
public class DeviceInfo {
    private int effectId;
    private String deviceId;
    private String deviceName;
    private String deviceDesc;
    private String profileUid;
    private int expireTime;

    public int getEffectId() {
        return effectId;
    }

    public void setEffectId(int effectId) {
        this.effectId = effectId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceDesc() {
        return deviceDesc;
    }

    public void setDeviceDesc(String deviceDesc) {
        this.deviceDesc = deviceDesc;
    }

    public String getProfileUid() {
        return profileUid;
    }

    public void setProfileUid(String profileUid) {
        this.profileUid = profileUid;
    }

    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }
}
