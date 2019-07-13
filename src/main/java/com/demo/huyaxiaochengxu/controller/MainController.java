package com.demo.huyaxiaochengxu.controller;

import com.alibaba.fastjson.JSONObject;
import com.demo.huyaxiaochengxu.entity.Event;
import com.demo.huyaxiaochengxu.entity.Gift;
import com.demo.huyaxiaochengxu.service.CommonService;
import com.demo.huyaxiaochengxu.util.JedisAdapter;
import com.demo.huyaxiaochengxu.util.OpenApi;
import com.demo.huyaxiaochengxu.util.returnJsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@Controller
public class MainController {

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    CommonService commonService;

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

//    @RequestMapping(path = {"/start"}, method = {RequestMethod.GET, RequestMethod.POST})
//    @ResponseBody
//    public String saveEffectEvent(@RequestParam("challenge") String[] image,
//                                  @RequestParam("title") String title,
//                                  @RequestParam("link") String link) {
//        {
//            try {
//                String totalGiftList = OpenApi.getLiveGiftInfoList();
//                JSONObject jsonObject = JSONObject.parseObject(totalGiftList);
//
//                if (jsonObject.getInteger("code") != 200) {
//                    logger.error("获取礼物和挑战数据失败");
//                    return returnJsonUtil.returnJson(500, "");
//                }
//
//                ArrayList<Gift> giftList = new ArrayList<>(commonService.getGiftList().values());
//                ArrayList<Event> eventList = new ArrayList<>(commonService.getEventList().values());
//
//                Map<String, Object> map = new HashMap<>();
//                map.put("gift", giftList);
//                map.put("effect", eventList);
//
//                return returnJsonUtil.returnJson(1, map);
//            } catch (Exception e) {
//                logger.error("获取礼物和挑战数据失败" + e.getMessage());
//                return returnJsonUtil.returnJson(500, "");
//            }
//        }

    }