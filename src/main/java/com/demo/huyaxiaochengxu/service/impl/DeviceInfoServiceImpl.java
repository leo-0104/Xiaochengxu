package com.demo.huyaxiaochengxu.service.impl;

import com.demo.huyaxiaochengxu.entity.DeviceDetail;
import com.demo.huyaxiaochengxu.entity.DeviceInfo;
import com.demo.huyaxiaochengxu.entity.EffectEvent;
import com.demo.huyaxiaochengxu.mapper.DeviceInfoMapper;
import com.demo.huyaxiaochengxu.mapper.EffectEventMapper;
import com.demo.huyaxiaochengxu.service.DeviceInfoService;
import com.demo.huyaxiaochengxu.service.EffectEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
     //获取设备列表
    public Map<String,DeviceDetail> getDeviceList(){
        Map<String,DeviceDetail> deviceInfoMap = new HashMap<>();
        deviceInfoMap.put("qiqiu",new DeviceDetail("qiqiu","气球机","爆炸气球","气球即将爆炸啦!"));
        deviceInfoMap.put("paopao",new DeviceDetail("paopao","泡泡机","泡泡","泡泡雨即将来临!"));
        deviceInfoMap.put("penqi",new DeviceDetail("penqi","喷雾机","喷雾","喷雾即将到来!"));
        deviceInfoMap.put("shuiqiang",new DeviceDetail("shuiqiang","水枪","水枪","水枪快要激活了!"));
        return deviceInfoMap;
    }
}


