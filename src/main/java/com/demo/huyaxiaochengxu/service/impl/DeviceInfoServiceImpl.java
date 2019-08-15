package com.demo.huyaxiaochengxu.service.impl;

import com.demo.huyaxiaochengxu.entity.DeviceInfo;
import com.demo.huyaxiaochengxu.entity.EffectEvent;
import com.demo.huyaxiaochengxu.mapper.DeviceInfoMapper;
import com.demo.huyaxiaochengxu.mapper.EffectEventMapper;
import com.demo.huyaxiaochengxu.service.DeviceInfoService;
import com.demo.huyaxiaochengxu.service.EffectEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceInfoServiceImpl implements DeviceInfoService {

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    @Override
    public List<DeviceInfo> getDeviceInfoByUid(String profileUid){
        return deviceInfoMapper.getDeviceInfoByUid(profileUid);
    }

    @Override
    public int batchInsertDeviceInfo( List<DeviceInfo> deviceInfoList){
        return deviceInfoMapper.batchInsertDeviceInfo(deviceInfoList);
    }

    @Override
    public int deleteDeviceById(int effectId){
        return deviceInfoMapper.deleteDeviceById(effectId);
    }

}


