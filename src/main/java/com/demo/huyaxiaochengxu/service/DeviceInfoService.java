package com.demo.huyaxiaochengxu.service;

import com.demo.huyaxiaochengxu.entity.DeviceInfo;
import com.demo.huyaxiaochengxu.entity.EffectEvent;

import java.util.List;


public interface DeviceInfoService {

    public List<DeviceInfo> getDeviceInfoByUid(String profileUid);
    public int batchInsertDeviceInfo( List<DeviceInfo> deviceInfoList);
    public int deleteDeviceById(int effectId);
}
