package com.demo.huyaxiaochengxu.service;

import com.demo.huyaxiaochengxu.entity.DeviceDetail;
import com.demo.huyaxiaochengxu.service.impl.DeviceInfoServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@EnableCaching
@Component
public class AssemService {

    @Autowired
    private DeviceInfoService deviceInfoService;
    private static final Logger logger = LoggerFactory.getLogger(AssemService.class);

//    //获取设备名称
//    public Map<String, String> getDeviceDetailById(String deviceId) {
//
//        Map<String, String> result = new HashMap<>();
//
//        String deviceName = "";
//        String deviceDesc = "";
//        String deviceUrge = "";
//        if (deviceId.equals("qiqiu")) {
//            deviceName = "气球机-01";
//            deviceDesc = "爆炸气球";
//            deviceUrge = "气球即将爆炸啦!";
//        } else if (deviceId.equals("paopao")) {
//            deviceName = "泡泡机-01";
//            deviceDesc = "泡泡";
//            deviceUrge = "泡泡雨即将来临!";
//        } else if (deviceId.equals("penqi")) {
//            deviceName = "喷雾机-01";
//            deviceDesc = "喷雾";
//            deviceUrge = "喷雾即将到来!";
//        } else if (deviceId.equals("shuiqiang")) {
//            deviceName = "水枪-01";
//            deviceDesc = "水枪";
//            deviceUrge = "水枪快要激活了!";
//        }
//        result.put("deviceName",deviceName);
//        result.put("deviceDesc",deviceDesc);
//        result.put("deviceUrge",deviceUrge);
//
//        return result;
//    }

    //获取设备名称
    public Map<String, String> getDeviceDetailById(String deviceId) {

        Map<String, DeviceDetail> deviceDetailMap = deviceInfoService.getDeviceList();
        Map<String,String> result =  null;
        if (deviceDetailMap.containsKey(deviceId)){
            result = new HashMap<>();
            DeviceDetail deviceDetail = deviceDetailMap.get(deviceId);
            result.put("deviceName",deviceDetail.getName());
            result.put("deviceDesc",deviceDetail.getEffect().getDesc());
            result.put("deviceUrge",deviceDetail.getEffect().getUrge());
        }else{
            return null;
        }
        return result;
    }


}
