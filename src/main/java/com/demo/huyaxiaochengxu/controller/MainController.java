package com.demo.huyaxiaochengxu.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.huyaxiaochengxu.entity.*;
import com.demo.huyaxiaochengxu.service.CommonService;
import com.demo.huyaxiaochengxu.service.EffectEventService;
import com.demo.huyaxiaochengxu.util.JedisAdapter;
import com.demo.huyaxiaochengxu.util.JwtUtil;
import com.demo.huyaxiaochengxu.util.OpenApi;
import com.demo.huyaxiaochengxu.util.returnJsonUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
public class MainController {

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    CommonService commonService;

    @Autowired
    EffectEventService effectEventService;

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @RequestMapping(path = {"/giftAndChallenge"}, method = {RequestMethod.GET, RequestMethod.POST})
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
    public String saveEffectEvent(@RequestBody String data,@RequestHeader(value = "authorization")String token) {
        {
            try {
                Claims claims = JwtUtil.decryptByToken(token);
                if (claims == null){
                    return returnJsonUtil.returnJson(500,"解密失败");
                }
                String profileId = (String) claims.get("profileId");
                if(profileId == null){
                    return returnJsonUtil.returnJson(500,"获取uid失败");
                }
                JSONObject jsonObject = JSONObject.parseObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("challenge");
                List<EffectEvent> effectEventList = new ArrayList<>();
                for (int j = 0; j < jsonArray.size(); j++) {

                    EffectEvent effectEvent = new EffectEvent();
                    effectEvent.setPrizeId((int) jsonArray.getJSONObject(j).get("gift"));
                    effectEvent.setPrizeNum((int) jsonArray.getJSONObject(j).get("total"));
                    effectEvent.setEffectId((int) jsonArray.getJSONObject(j).get("effect"));
                    effectEvent.setEffectText((String) jsonArray.getJSONObject(j).get("desc"));
                    effectEvent.setUid(profileId);
                    effectEvent.setGroupId(profileId + "_" + jsonArray.getJSONObject(j).get("token"));
                    effectEvent.setStatus(1);
                    effectEvent.setAddTime((long)jsonArray.getJSONObject(j).get("token"));

                    effectEventList.add(effectEvent);
                }

                effectEventService.batchInsertEvent(effectEventList);

                return returnJsonUtil.returnJson(1, "");
            } catch (Exception e) {
                logger.error("保存礼物和挑战数据失败" + e.getMessage());
                return returnJsonUtil.returnJson(500, "参数错误");
            }
        }
    }

    @RequestMapping(path = {"/finish"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String profileForceQuit(@RequestBody String data,@RequestHeader(value = "authorization")String token) {
        {
            Claims claims = JwtUtil.decryptByToken(token);
            if (claims == null){
                return returnJsonUtil.returnJson(500,"解密失败");
            }
            String profileId = (String) claims.get("profileId");
            if(profileId == null){
                return returnJsonUtil.returnJson(500,"获取uid失败");
            }
            try {
                //TODO 关闭ws连接及删除redis相关礼物数据
                return returnJsonUtil.returnJson(1, "");
            } catch (Exception e) {
                logger.error("主播主动关闭挑战失败" + e.getMessage()+"profileId:"+profileId);
                return returnJsonUtil.returnJson(500, "主播主动关闭挑战失败");
            }
        }
    }


    @RequestMapping(value = "/getStatus",method = RequestMethod.GET)
//    public String getStatus(@RequestHeader(value = "authorization")String token){
       public String getStatus( ){
//            Claims claims = JwtUtil.decryptByToken(token);
//        if (claims == null){
//        return returnJsonUtil.returnJson(500,"解密失败");
//    }
//    String profileId = (String) claims.get("profileId");
//        if(profileId == null){
//        return returnJsonUtil.returnJson(500,"获取uid失败");
//    }
    String profileId = "111";
    Map<String,Object> resultMap = new HashMap<>();
    //查询当前主播开启中的挑战
    List<EffectEvent> effectEventList = null;
        try {
        effectEventList = effectEventService.getEventsByUid(profileId);
      }catch (Exception e){
        logger.error("查询当前主播开启中的挑战 error,e => " + e.getMessage());
        return returnJsonUtil.returnJson(500,"查询主播挑战事件失败");
      }
      //挑战尚未开始
        if (effectEventList == null || effectEventList.size() == 0){
        resultMap.put("status",1);
        return returnJsonUtil.returnJson(200,resultMap);
    }

        resultMap.put("id",effectEventList.get(0).getGroupId());   //任务id

        resultMap.put("timestamp",effectEventList.get(0).getAddTime());  //开始时间戳
        resultMap.put("total",effectEventList.size());    //挑战总数
         //倒计时阶段(10s倒计时)
        if (effectEventList.get(0).getAddTime() -  new Date().getTime() <= 10 * 1000){
            resultMap.put("status",2);
          List<Schedule> scheduleList = new ArrayList<>();
        for (EffectEvent effectEvent:effectEventList){
            Schedule schedule = new Schedule();
            schedule.setId(effectEvent.getId());
            schedule.setStatus(-1);
            schedule.setTotal(effectEvent.getPrizeNum());
            schedule.setCount(0);
            schedule.setScale();
            schedule.setFinished(false);
            Gift gift = CommonService.getGiftList().get(String.valueOf(effectEvent.getPrizeId()));
            schedule.setGift(gift);       //礼物信息
            Event event = CommonService.getEventList().get(effectEvent.getEffectId());
            schedule.setEffect(event);    //特效事件
            scheduleList.add(schedule);
        }
        resultMap.put("schedule",scheduleList);
        return returnJsonUtil.returnJson(200,resultMap);
    }
       //送礼阶段
        resultMap.put("status",3);
        List<Schedule> scheduleList = new ArrayList<>();
        for (EffectEvent effectEvent:effectEventList){
            Schedule schedule = new Schedule();
            schedule.setId(effectEvent.getId());
            schedule.setTotal(effectEvent.getPrizeNum());
            Gift gift = CommonService.getGiftList().get(String.valueOf(effectEvent.getPrizeId()));
            schedule.setGift(gift);       //礼物信息
            Event event = CommonService.getEventList().get(effectEvent.getEffectId());
            schedule.setEffect(event);    //特效事件
            schedule.setScale();
            //挑战完成
            if (effectEvent.getStatus() == 2){
                //获取的礼物数量
                schedule.setCount(effectEvent.getPrizeNum());
                schedule.setFinished(true);
                schedule.setStatus(2);
                //助攻者
                schedule.setAssistList(new ArrayList<Assist>());
            }else{
                //获取的礼物数量 >= 设置的礼物数量(挑战尚未完成)
                if (schedule.getTotal() >= effectEvent.getPrizeNum()){
                    //获取的礼物数量
                    schedule.setCount(effectEvent.getPrizeNum());
                    schedule.setFinished(false);
                    schedule.setStatus(1);
                    //助攻者
                    schedule.setAssistList(new ArrayList<Assist>());
                }else{    //送礼尚未完成
                    //获取的礼物数量
                    schedule.setCount(schedule.getCount());
                    schedule.setFinished(false);
                    schedule.setStatus(0);
                }
            }

            scheduleList.add(schedule);
        }
        resultMap.put("schedule",scheduleList);
        return returnJsonUtil.returnJson(200,resultMap);
       //return returnJsonUtil.returnJson(200,"你很棒棒哦国本，调用成功,uid =>" + profileId);
}
}
