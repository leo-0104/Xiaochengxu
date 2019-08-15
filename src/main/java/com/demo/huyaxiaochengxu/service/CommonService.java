package com.demo.huyaxiaochengxu.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.huyaxiaochengxu.entity.DeviceInfo;
import com.demo.huyaxiaochengxu.entity.Event;
import com.demo.huyaxiaochengxu.entity.Gift;
import com.demo.huyaxiaochengxu.util.OpenApi;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Component
public class CommonService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    GiftScheduleManager giftScheduleManager;

    @Autowired
    DeviceInfoService deviceInfoService;

    private static final Logger logger = LoggerFactory.getLogger(CommonService.class);

    public Map<String, JSONObject> getGiftList() {
        try {
            Map<String, JSONObject> result = (Map) JSONObject.parse(redisTemplate.opsForValue().get("giftList"));
            if (result == null || result.isEmpty()) {

                String totalGiftList = OpenApi.getLiveGiftInfoList();
                JSONObject jsonObject = JSONObject.parseObject(totalGiftList);

                if (jsonObject.getInteger("code") != 200) {
                    logger.error("获取礼物和挑战数据失败");
                    return new HashMap<>();
                }
                JSONArray jsonArray = JSONArray.parseArray(jsonObject.getString("data"));
                Map<String, JSONObject> giftMap = new HashMap<>();
                for (int j = 0; j < jsonArray.size(); j++) {
                    String[] showGiftIds = {"4", "20114", "20277", "20273", "20271", "20267"};
                    String giftId = jsonArray.getJSONObject(j).get("giftId").toString();
                    boolean isContains = Arrays.asList(showGiftIds).contains(giftId);
                    if (isContains) {
                        Gift gift = new Gift();
                        gift.setId(giftId);
                        gift.setName((String) jsonArray.getJSONObject(j).get("giftCnName"));
                        gift.setPrice(Double.parseDouble(jsonArray.getJSONObject(j).get("prizeYb").toString()) * 1000);
                        gift.setSrc((String) jsonArray.getJSONObject(j).get("iconUrl"));
                        giftMap.put(giftId, (JSONObject) JSONObject.toJSON(gift));
                    }
                }
                redisTemplate.opsForValue().set("giftList", JSONObject.toJSONString(giftMap), 300, TimeUnit.SECONDS);
                return giftMap;
            }
            return result;
        } catch (Exception e) {
            logger.error("获取礼物数据失败" + e.getMessage());
            return new HashMap<>();
        }
    }

    public Map<Integer, JSONObject> getEventList(String profileId) {
        try {
            String redisEventKey = "eventList_" + profileId;
//            Map<Integer, JSONObject> result = (Map) JSONObject.parse(redisTemplate.opsForValue().get(redisEventKey));
//            if (result == null || result.isEmpty()) {
                Map<Integer, JSONObject> eventMap = new HashMap<>();
                eventMap.put(-1, (JSONObject) JSONObject.toJSON(new Event().setId(-1).setType(1).setDesc("自定义").setUrge("请主播尽快完成挑战")));
                List<DeviceInfo> deviceInfoList = deviceInfoService.getDeviceInfoByUid(profileId);

                if (deviceInfoList == null || deviceInfoList.isEmpty()) {
                    logger.info("主播绑定设备为空 profileUid:" + profileId);
                    return eventMap;
                }

                for (DeviceInfo deviceInfo : deviceInfoList) {
                    eventMap.put(deviceInfo.getEffectId(), (JSONObject) JSONObject.toJSON(new Event().setId(deviceInfo.getEffectId()).setType(2).setDesc(deviceInfo.getDeviceDesc()).setUrge(deviceInfo.getDeviceDesc())));
                }

//                eventMap.put(1, (JSONObject) JSONObject.toJSON(new Event().setId(1).setType(2).setDesc("爆炸气球").setUrge("气球充气中")));
//                eventMap.put(2, (JSONObject) JSONObject.toJSON(new Event().setId(2).setType(2).setDesc("喷雾").setUrge("喷雾提示语")));
//                eventMap.put(3, (JSONObject) JSONObject.toJSON(new Event().setId(3).setType(2).setDesc("水枪").setUrge("水枪提示语")));
//                eventMap.put(4, (JSONObject) JSONObject.toJSON(new Event().setId(4).setType(2).setDesc("泡泡").setUrge("泡泡提示语")));

//                redisTemplate.opsForValue().set(redisEventKey, JSONObject.toJSONString(eventMap), 300, TimeUnit.SECONDS);
                return eventMap;
//            }
//            return result;
        } catch (Exception e) {
            logger.error("获取事件数据失败" + e.getMessage());
            return new HashMap<>();
        }
    }

    public Map<Integer, String> getDeviceList(String profileId) {
        try {
            String redisDeviceKey = profileId + "_deviceList";
//            Map<Integer, String> result = (Map) JSONObject.parse(redisTemplate.opsForValue().get(redisDeviceKey));
//            if (result == null || result.isEmpty()) {
                Map<Integer, String> effectDeviceMap = new HashMap<>();
                List<DeviceInfo> deviceInfoList = deviceInfoService.getDeviceInfoByUid(profileId);

                if (deviceInfoList == null || deviceInfoList.isEmpty()) {
                    logger.info("主播绑定设备为空 profileId:" + profileId);
                    return effectDeviceMap;
                }
                for (DeviceInfo deviceInfo : deviceInfoList) {
                    effectDeviceMap.put(deviceInfo.getEffectId(), deviceInfo.getDeviceName());
                }
                redisTemplate.opsForValue().set(redisDeviceKey, JSONObject.toJSONString(effectDeviceMap), 300);
                return effectDeviceMap;
//            }
//            return result;
        } catch (Exception e) {
            logger.error("获取设备数据失败" + e.getMessage());
            return new HashMap<>();
        }
    }

    public boolean cancelGiftSchedule(String groupId) {
        {
            try {
                giftScheduleManager.cancelGiftSchedule(groupId);
                return true;
            } catch (Exception e) {
                logger.error("关闭" + e.getMessage());
                return false;
            }
        }
    }


}
