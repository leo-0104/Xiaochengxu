package com.demo.huyaxiaochengxu.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.huyaxiaochengxu.entity.Event;
import com.demo.huyaxiaochengxu.entity.Gift;
import com.demo.huyaxiaochengxu.util.JedisAdapter;
import com.demo.huyaxiaochengxu.util.OpenApi;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@Controller
public class CommonService {

    @Autowired
    JedisAdapter jedisAdapter;

    private static final Logger logger = LoggerFactory.getLogger(CommonService.class);

    public Map<String, Gift> getGiftList() {
        try {
            Map<String, Gift> result = (Map) JSONObject.parse(jedisAdapter.get("giftList"));
            if (result == null || result.isEmpty()) {

                String totalGiftList = OpenApi.getLiveGiftInfoList();
                JSONObject jsonObject = JSONObject.parseObject(totalGiftList);

                if (jsonObject.getInteger("code") != 200) {
                    logger.error("获取礼物和挑战数据失败");
                    return new HashMap<>();
                }

                JSONArray jsonArray = JSONArray.parseArray(jsonObject.getString("data"));
                Map<String, Gift> giftMap = new HashMap<>();
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
                        giftMap.put(giftId, gift);

                    }
                }
                jedisAdapter.set("giftList", JSONObject.toJSONString(giftMap), 300);
                return giftMap;
            }

            return result;
        } catch (Exception e) {
            logger.error("获取礼物数据失败" + e.getMessage());
            return new HashMap<>();
        }
    }

    public Map<Integer, Event> getEventList() {
        try {
            Map<Integer, Event> result = (Map) JSONObject.parse(jedisAdapter.get("eventList"));
            if (result == null || result.isEmpty()) {
                Map<Integer, Event> eventMap = new HashMap<>();

                eventMap.put(1, new Event().setId(1).setType(2).setDesc("爆炸气球").setUrge("气球充气中"));
                eventMap.put(2, new Event().setId(2).setType(2).setDesc("喷雾").setUrge("喷雾提示语"));
                eventMap.put(3, new Event().setId(3).setType(2).setDesc("水枪").setUrge("水枪提示语"));
                eventMap.put(4, new Event().setId(4).setType(2).setDesc("泡泡").setUrge("泡泡提示语"));

                jedisAdapter.set("eventList", JSONObject.toJSONString(eventMap), 300);
                return eventMap;
            }
            return result;
        } catch (Exception e) {
            logger.error("获取事件数据失败" + e.getMessage());
            return new HashMap<>();
        }
    }

    public Map<Integer, String> getDeviceList(Long roomId) {
        try {
                Map<Integer, String> result = (Map) JSONObject.parse(jedisAdapter.get(roomId + "_deviceList"));
             if (result == null || result.isEmpty()) {
                Map<Integer,String>  effectDeviceMap = new HashMap<>();
                 effectDeviceMap.put(1,"qiqiu");
                 effectDeviceMap.put(2,"penqi");
                 effectDeviceMap.put(3,"shuiqiang");
                 effectDeviceMap.put(4,"penqu");
                jedisAdapter.set(roomId + "_deviceList", JSONObject.toJSONString(effectDeviceMap), 300);
                return effectDeviceMap;
            }
            return result;
        } catch (Exception e) {
            logger.error("获取事件数据失败" + e.getMessage());
            return new HashMap<>();
        }
    }


    }
