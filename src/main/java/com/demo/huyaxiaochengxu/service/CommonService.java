package com.demo.huyaxiaochengxu.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
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

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Component
public class CommonService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    GiftScheduleManager giftScheduleManager;

    private static final Logger logger = LoggerFactory.getLogger(CommonService.class);

    public Map<String, String> getGiftList() {
        try {
            Map<String, String> result = (Map) JSONObject.parse(redisTemplate.opsForValue().get("giftList"));
            if (result == null || result.isEmpty()) {

                String totalGiftList = OpenApi.getLiveGiftInfoList();
                JSONObject jsonObject = JSONObject.parseObject(totalGiftList);

                if (jsonObject.getInteger("code") != 200) {
                    logger.error("获取礼物和挑战数据失败");
                    return new HashMap<>();
                }

                JSONArray jsonArray = JSONArray.parseArray(jsonObject.getString("data"));
                Map<String, String> giftMap = new HashMap<>();
                for (int j = 0; j < jsonArray.size(); j++) {
                    String[] showGiftIds = {"4", "20114", "20277", "20273", "20271", "20267"};
                    String giftId = jsonArray.getJSONObject(j).get("giftId").toString();
                    boolean isContains = Arrays.asList(showGiftIds).contains(giftId);
                    if (isContains) {
                        Gift gift = new Gift();
                        gift.setId(giftId);
                        gift.setName((String) jsonArray.getJSONObject(j).get("giftCnName"));
                        gift.setPrize(Double.parseDouble(jsonArray.getJSONObject(j).get("prizeYb").toString()));
                        gift.setSrc((String) jsonArray.getJSONObject(j).get("iconUrl"));
                        giftMap.put(giftId, JSONObject.toJSONString(gift));

                    }
                }
                redisTemplate.opsForValue().set("giftList", JSONObject.toJSONString(giftMap, SerializerFeature.DisableCircularReferenceDetect), 300, TimeUnit.SECONDS);
                return giftMap;
            }
            return result;
        } catch (Exception e) {
            logger.error("获取礼物数据失败" + e.getMessage());
            return new HashMap<>();
        }
    }

    public Map<Integer, String> getEventList() {
        try {
            Map<Integer, String> result = (Map) JSONObject.parse(redisTemplate.opsForValue().get("eventList"));
            if (result == null || result.isEmpty()) {
                Map<Integer, String> eventMap = new HashMap<>();

                eventMap.put(1,JSONObject.toJSONString( new Event().setId(1).setType(2).setDesc("爆炸气球").setUrge("气球充气中")));
                eventMap.put(2, JSONObject.toJSONString(new Event().setId(2).setType(2).setDesc("喷雾").setUrge("喷雾提示语")));
                eventMap.put(3, JSONObject.toJSONString(new Event().setId(3).setType(2).setDesc("水枪").setUrge("水枪提示语")));
                eventMap.put(4, JSONObject.toJSONString(new Event().setId(4).setType(2).setDesc("泡泡").setUrge("泡泡提示语")));

                redisTemplate.opsForValue().set("eventList", JSONObject.toJSONString(eventMap,SerializerFeature.DisableCircularReferenceDetect), 300, TimeUnit.SECONDS);
                return eventMap;
            }
            return result;
        } catch (Exception e) {
            logger.error("获取事件数据失败" + e.getMessage());
            return new HashMap<>();
        }
    }

    public Map<Integer, String> getDeviceList(String roomId) {
        try {
                Map<Integer, String> result = (Map) JSONObject.parse(redisTemplate.opsForValue().get(roomId + "_deviceList"));
             if (result == null || result.isEmpty()) {
                Map<Integer,String>  effectDeviceMap = new HashMap<>();
                 effectDeviceMap.put(1,"qiqiu");
                 effectDeviceMap.put(2,"penqi");
                 effectDeviceMap.put(3,"shuiqiang");
                 effectDeviceMap.put(4,"penqu");
                redisTemplate.opsForValue().set(roomId + "_deviceList", JSONObject.toJSONString(effectDeviceMap), 300);
                return effectDeviceMap;
            }
            return result;
        } catch (Exception e) {
            logger.error("获取事件数据失败" + e.getMessage());
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
