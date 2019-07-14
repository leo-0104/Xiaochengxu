package com.demo.huyaxiaochengxu.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.huyaxiaochengxu.entity.Event;
import com.demo.huyaxiaochengxu.entity.Gift;
import com.demo.huyaxiaochengxu.util.OpenApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@Controller
public class CommonService {


    private static final Logger logger = LoggerFactory.getLogger(CommonService.class);

    public static Map<String, Gift> getGiftList() {
        try {
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

            return giftMap;
        } catch (Exception e) {
            logger.error("获取礼物数据失败" + e.getMessage());
            return new HashMap<>();
        }
    }

    public static Map<Integer, Event> getEventList() {

        Map<Integer, Event> eventMap = new HashMap<>();
        eventMap.put(1,new Event().setId(1).setType(2).setDesc("爆炸气球").setUrge("气球充气中"));
        eventMap.put(2,new Event().setId(2).setType(2).setDesc("喷雾").setUrge("喷雾提示语"));
        eventMap.put(3,new Event().setId(3).setType(2).setDesc("水枪").setUrge("水枪提示语"));
        eventMap.put(4,new Event().setId(4).setType(2).setDesc("泡泡").setUrge("泡泡提示语"));

        return eventMap;
    }


}
