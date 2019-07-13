package com.demo.huyaxiaochengxu.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.huyaxiaochengxu.entity.EffectEvent;
import com.demo.huyaxiaochengxu.entity.Event;
import com.demo.huyaxiaochengxu.entity.Gift;
import com.demo.huyaxiaochengxu.service.CommonService;
import com.demo.huyaxiaochengxu.service.EffectEventService;
import com.demo.huyaxiaochengxu.util.JedisAdapter;
import com.demo.huyaxiaochengxu.util.OpenApi;
import com.demo.huyaxiaochengxu.util.returnJsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller
public class MainController {

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    CommonService commonService;

    @Autowired
    EffectEventService effectEventService;

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @RequestMapping(path = {"/giftAndChallenge"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String getGiftAndChallenge() {
        try {
            String totalGiftList = OpenApi.getLiveGiftInfoList();
            JSONObject jsonObject = JSONObject.parseObject(totalGiftList);

            if (jsonObject.getInteger("code") != 200) {
                logger.error("获取礼物和挑战数据失败");
                return returnJsonUtil.returnJson(500, "");
            }

            ArrayList<Gift> giftList = new ArrayList<>(commonService.getGiftList().values());
            ArrayList<Event> eventList = new ArrayList<>(commonService.getEventList().values());

            Map<String, Object> map = new HashMap<>();
            map.put("gift", giftList);
            map.put("effect", eventList);

            return returnJsonUtil.returnJson(1, map);
        } catch (Exception e) {
            logger.error("获取礼物和挑战数据失败" + e.getMessage());
            return returnJsonUtil.returnJson(500, "");
        }
    }

    @RequestMapping(path = {"/start"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String saveEffectEvent(@RequestBody String data) {
        {
            String profileUid = "50077679";
            try {
                JSONObject jsonObject = JSONObject.parseObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("challenge");
                List<EffectEvent> effectEventList = new ArrayList<>();
                for (int j = 0; j < jsonArray.size(); j++) {

                    EffectEvent effectEvent = new EffectEvent();
                    effectEvent.setPrizeId((int) jsonArray.getJSONObject(j).get("gift"));
                    effectEvent.setPrizeNum((int) jsonArray.getJSONObject(j).get("total"));
                    effectEvent.setEffectId((int) jsonArray.getJSONObject(j).get("effect"));
                    effectEvent.setEffectText((String) jsonArray.getJSONObject(j).get("desc"));
                    effectEvent.setUid(profileUid);
                    effectEvent.setGroupId(profileUid + "_" + jsonArray.getJSONObject(j).get("token"));
                    effectEvent.setStatus(1);
                    effectEvent.setAddTime((long)jsonArray.getJSONObject(j).get("token"));

                    effectEventList.add(effectEvent);
                }

                effectEventService.batchInsertEvent(effectEventList);

                return returnJsonUtil.returnJson(1, "");
            } catch (Exception e) {
                logger.error("保存礼物和挑战数据失败" + e.getMessage()+"profileUid:"+profileUid);
                return returnJsonUtil.returnJson(500, "参数错误");
            }
        }
    }

    @RequestMapping(path = {"/finish"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String profileForceQuit(@RequestBody String data) {
        {
            String profileUid = "50077679";
            try {
                //TODO 关闭ws连接及删除redis相关礼物数据
                return returnJsonUtil.returnJson(1, "");
            } catch (Exception e) {
                logger.error("主播主动关闭挑战失败" + e.getMessage()+"profileUid:"+profileUid);
                return returnJsonUtil.returnJson(500, "主播主动关闭挑战失败");
            }
        }
    }
}
