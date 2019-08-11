package com.demo.huyaxiaochengxu.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.huyaxiaochengxu.common.Action;
import com.demo.huyaxiaochengxu.entity.*;
import com.demo.huyaxiaochengxu.service.CommonService;
import com.demo.huyaxiaochengxu.service.EffectEventService;
import com.demo.huyaxiaochengxu.service.GiftScheduleManager;
import com.demo.huyaxiaochengxu.util.JwtUtil;
import com.demo.huyaxiaochengxu.util.returnJsonUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@EnableCaching
@RestController
public class MainController {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    CommonService commonService;

    @Autowired
    EffectEventService effectEventService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    GiftScheduleManager giftScheduleManager;

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);


    @RequestMapping(path = {"/giftAndChallenge"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String getGiftAndChallenge() {
        try {


            Map<String, Object> map = new HashMap<>();
            map.put("gift", commonService.getGiftList().values());
            map.put("effect", commonService.getEventList().values());

            return returnJsonUtil.returnJson(200, map);
        } catch (Exception e) {
            logger.error("获取礼物和挑战数据失败" + e.getMessage());
            return returnJsonUtil.returnJson(500, "");
        }
    }

    @RequestMapping(path = {"/start"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String saveEffectEvent(@RequestBody String data, @RequestHeader(value = "authorization") String token) {
        {
            try {
                Claims claims = JwtUtil.decryptByToken(token);
                if (claims == null) {
                    return returnJsonUtil.returnJson(500, "解密失败");
                }
                String profileId = (String) claims.get("profileId");
                String roomId = (String) claims.get("roomId");
                if (profileId == null) {
                    return returnJsonUtil.returnJson(500, "获取uid失败");
                }
                logger.info("saveEffectEvent  -- " + data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("challenge");
                List<EffectEvent> effectEventList = new ArrayList<>();
                String groupId = "";
                long time = 0L;

                for (int j = 0; j < jsonArray.size(); j++) {

                    if (j == 0) {
//                        time = jsonArray.getJSONObject(j).getLong("token");
                        time = System.currentTimeMillis();
                        groupId = profileId + "_" + time;
                    }

                    EffectEvent effectEvent = new EffectEvent();
                    effectEvent.setPrizeId(Integer.valueOf(jsonArray.getJSONObject(j).getString("gift")));
                    effectEvent.setPrizeNum((int) jsonArray.getJSONObject(j).get("total"));
                    effectEvent.setEffectId((int) jsonArray.getJSONObject(j).get("effect"));
                    effectEvent.setEffectText((String) jsonArray.getJSONObject(j).get("desc"));
                    effectEvent.setUid(profileId);
                    effectEvent.setGroupId(groupId);
                    effectEvent.setStatus(1);
                    effectEvent.setAddTime(time);

                    effectEventList.add(effectEvent);
                }
                //插入事件
                effectEventService.batchInsertEvent(effectEventList);

                //获取对应事件的详细信息，建立监听
                List<EffectEvent> effectEventResult = effectEventService.getEventsByGroupId(groupId);

                giftScheduleManager.createGiftSchedule(effectEventResult, roomId, groupId, time);

                for (EffectEvent effectEvent : effectEventResult) {
                    if (effectEvent.getEffectId() > 0) {
                        Map<Integer, String> effectDeviceMap = commonService.getDeviceList(roomId);
                        //通知设备更新触发特效  +  更新挑战状态
                        Message message = new Message();
                        message.setAction(Action.START.getAction());
                        message.setDeviceName(effectDeviceMap.get(effectEvent.getEffectId()));  //设备名字
                        message.setDuration(2);    //特效触发持续的时间
                        message.setCount(1);       //特效触发的次数
                        //生产者发送消息，存至消息队列中
                        kafkaTemplate.send("device", JSON.toJSONString(message));
//                        Thread.sleep(500);
//                        message.setAction(Action.ON.getAction());
//                        //生产者发送消息，存至消息队列中
//                        kafkaTemplate.send("device", JSON.toJSONString(message));
                    }
                }



                return returnJsonUtil.returnJson(200, "");
            } catch (Exception e) {
                logger.error("保存礼物和挑战数据失败" + e.getMessage());
                return returnJsonUtil.returnJson(500, "参数错误");
            }
        }
    }

    @RequestMapping(path = {"/finish"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String profileForceQuit( @RequestHeader(value = "authorization") String token) {
        {
            Claims claims = JwtUtil.decryptByToken(token);
            if (claims == null) {
                return returnJsonUtil.returnJson(500, "解密失败");
            }
            String profileId = (String) claims.get("profileId");
            if (profileId == null) {
                return returnJsonUtil.returnJson(500, "获取uid失败");
            }
            try {
                List<EffectEvent> effectEventList = effectEventService.getEventsByUid(profileId);
                if (!effectEventList.isEmpty()){
                    String groupId = effectEventList.get(0).getGroupId();
                    giftScheduleManager.cancelGiftSchedule(groupId);
                    effectEventService.batchUpdateEvent(profileId);
                    return returnJsonUtil.returnJson(200, "");
                }
                return returnJsonUtil.returnJson(500, "该主播没有进行中的挑战");
            } catch (Exception e) {
                logger.error("主播主动关闭挑战失败" + e.getMessage() + "profileId:" + profileId);
                return returnJsonUtil.returnJson(500, "主播主动关闭挑战失败");
            }
        }
    }

    /**
     * 获取任务状态
     * (1)挑战尚未开始
     * (2)倒计时阶段
     * (3)送礼阶段
     * -----1.送礼尚未完成
     * -----2.送礼完成，挑战尚未完成
     * -----3.送礼完成，挑战完成
     * @param token
     * @return
     */
    @RequestMapping(value = "/getStatus", method = RequestMethod.GET)
    public String getStatus(@RequestHeader(value = "authorization") String token) {
        Claims claims = JwtUtil.decryptByToken(token);
        if (claims == null) {
            return returnJsonUtil.returnJson(500, "解密失败");
        }
        String profileId = (String) claims.get("profileId");
        String roomId = (String) claims.get("roomId");
        //用于区分主播/用户请求
        String role = (String) claims.get("role");
        if (profileId == null) {
            return returnJsonUtil.returnJson(500, "获取uid失败");
        }
        if (roomId == null){
            return returnJsonUtil.returnJson(500, "获取roomId失败");
        }
        if (role == null){
            return returnJsonUtil.returnJson(500, "获取role(用户身份)失败");
        }
        Map<String, Object> resultMap = new HashMap<>();
        //查询当前主播开启中的挑战
        List<EffectEvent> effectEventList = null;
        try {
            effectEventList = effectEventService.getEventsByUid(profileId);
        } catch (Exception e) {
            logger.error("查询当前主播开启中的挑战 error,e => " + e.getMessage());
            return returnJsonUtil.returnJson(500, "查询主播挑战事件失败");
        }
        //挑战尚未开始
        if (effectEventList == null || effectEventList.size() == 0) {
            resultMap.put("status", 1);
            return returnJsonUtil.returnJson(200, resultMap);
        }

        resultMap.put("id", effectEventList.get(0).getGroupId());   //任务id

        resultMap.put("timestamp", effectEventList.get(0).getAddTime());  //开始时间戳
        resultMap.put("total", effectEventList.size());    //挑战总数
        //从缓存中读取礼物信息
        Map<String, JSONObject> giftMap = commonService.getGiftList();
        //从缓存中读取特效事件信息
        Map<Integer, JSONObject> eventMap = commonService.getEventList();
        //特效设备绑定信息
        Map<Integer,String> effectDeviceMap = commonService.getDeviceList(roomId);
        //倒计时阶段(3s倒计时)
        int distanceTime = 3;
        if (new Date().getTime() - effectEventList.get(0).getAddTime()  <= distanceTime * 1000) {
            logger.info("getStatus timestamp:" + effectEventList.get(0).getAddTime() + ";distance: " + (new Date().getTime() - effectEventList.get(0).getAddTime()));
            resultMap.put("status", 2);
            resultMap.put("distanceTime",distanceTime);
            logger.info("getStatus distanceTime:" + distanceTime);
            List<Schedule> scheduleList = new ArrayList<>();
            for (EffectEvent effectEvent : effectEventList) {
                Schedule schedule = new Schedule();
                schedule.setId(effectEvent.getId());
                schedule.setStatus(-1);
                schedule.setTotal(effectEvent.getPrizeNum());
                schedule.setCount(0);
                schedule.setScale();
                schedule.setFinished(false);
                Gift gift = JSONObject.parseObject(giftMap.get(String.valueOf(effectEvent.getPrizeId())).toString(),Gift.class);
                schedule.setGift(gift);       //礼物信息
                Event event = JSONObject.parseObject(eventMap.get(effectEvent.getEffectId()).toString(),Event.class);
                schedule.setEffect(event);    //特效事件
                scheduleList.add(schedule);
            }
            resultMap.put("schedule", scheduleList);
            return returnJsonUtil.returnJson(200, resultMap);
        }
        //送礼阶段
        resultMap.put("status", 3);
        List<Schedule> scheduleList = new ArrayList<>();
        for (EffectEvent effectEvent : effectEventList) {
            Schedule schedule = new Schedule();
            schedule.setId(effectEvent.getId());
            schedule.setTotal(effectEvent.getPrizeNum());
            Gift gift = JSONObject.parseObject(giftMap.get(String.valueOf(effectEvent.getPrizeId())).toString(),Gift.class);
            schedule.setGift(gift);       //礼物信息
            Event event = JSONObject.parseObject(eventMap.get(effectEvent.getEffectId()).toString(),Event.class);
            schedule.setEffect(event);    //特效事件

            //返回集合内元素的排名，以及分数（从大到小）
            Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet().reverseRangeWithScores(String.valueOf(effectEvent.getId()), 0, -1);
            //挑战完成
            if (effectEvent.getStatus() == 2) {
                //获取的礼物数量
                schedule.setCount(effectEvent.getPrizeNum());
                schedule.setFinished(true);
                schedule.setStatus(2);
                schedule.setScale();
                //获取最佳助攻列表
                schedule.setAssistList(getAssistList(tuples));
            } else {
                //从缓存中读取 获取的礼物数量
                String totalNum = redisTemplate.opsForValue().get(effectEvent.getId() + "_total");
                int getGiftNum = 0;
                if (totalNum != null) {
                    getGiftNum = Integer.valueOf(totalNum);
                }
                //获取的礼物数量 >= 设置的礼物数量(挑战尚未完成)
                if (getGiftNum >= effectEvent.getPrizeNum()) {
                    //获取的礼物数量
                    schedule.setCount(effectEvent.getPrizeNum() );
                    schedule.setScale();
                    schedule.setFinished(false);
                    schedule.setStatus(1);
                    //获取最佳助攻列表
                    //schedule.setAssistList(getAssistList(tuples));

                    //通知设备更新触发特效  +  更新挑战状态(特效id > 0 且 主播请求)
                    if ("P".equals(role)  && effectEvent.getEffectId() > 0) {
                        Message message = new Message();
                        message.setGroupId(effectEvent.getGroupId());
                        message.setTaskId(effectEvent.getId());
                        message.setAction(Action.ON_OFF.getAction());
                        message.setDeviceName(effectDeviceMap.get(effectEvent.getEffectId()));  //设备名字
                        message.setDuration(2);    //特效触发持续的时间
                        message.setCount(1);       //特效触发的次数
                        message.setChange(true);   //是否修改挑战状态
                        //气球设备特殊处理
                        if (effectEvent.getEffectId() == 1) {
                            message.setDuration(80);    //特效触发持续的时间
//                            //气球已经触发的秒数
//                            String touchNum = redisTemplate.opsForValue().get(effectEvent.getId() + "_touchNum");
//                            int getTouchNum = 0;
//                            if (touchNum != null) {
//                                getTouchNum = Integer.valueOf(touchNum);
//                            }
//                            //保存已经获取到的礼物数量
//                            redisTemplate.opsForValue().set(effectEvent.getId() + "_touchNum", String.valueOf(getGiftNum), 3600, TimeUnit.SECONDS);
//                            //特效触发时间
//                            int duration = (getGiftNum - getTouchNum) * 60 / effectEvent.getPrizeNum();
//                            if (duration > 0) {
//                                message.setDuration(300);    //特效触发持续的时间
//                            }
                        }
                        //生产者发送消息，存至消息队列中
                        kafkaTemplate.send("device", JSON.toJSONString(message));
                    }
                } else {    //送礼尚未完成
                    //获取的礼物数量
                    schedule.setCount(getGiftNum);
                    schedule.setFinished(false);
                    schedule.setStatus(0);
                    schedule.setScale();
//                    //气球设备特殊处理
//                    if (effectEvent.getEffectId() ==1){
//                        //气球已经触发的秒数
//                        String touchNum = redisTemplate.opsForValue().get(effectEvent.getId() + "_touchNum");
//                        int getTouchNum = 0;
//                        if (touchNum != null) {
//                            getTouchNum = Integer.valueOf(touchNum);
//                        }
//                        //保存已经获取到的礼物数量
//                        redisTemplate.opsForValue().set(effectEvent.getId() + "_touchNum",String.valueOf(getGiftNum),3600, TimeUnit.SECONDS);
//                        //特效触发时间
//                        int duration = (getGiftNum - getTouchNum) * 60  /effectEvent.getPrizeNum();
//                        if (duration > 0){
//                            //通知设备触发特效
//                            Message message = new Message();
//                            message.setGroupId(effectEvent.getGroupId());
//                            message.setTaskId(effectEvent.getId());
//                            message.setAction(Action.ON_OFF.getAction());
//                            message.setDeviceName(effectDeviceMap.get(effectEvent.getEffectId()));  //设备名字
//                            message.setDuration(duration);    //特效触发持续的时间
//                            message.setCount(1);       //特效触发的次数
//                            message.setEffectId(1);
//                            message.setChange(false);
//                            //生产者发送消息，存至消息队列中
//                            kafkaTemplate.send("device",JSON.toJSONString(message));
//                        }
//                    }
                }
            }
            scheduleList.add(schedule);
        }
        resultMap.put("schedule", scheduleList);
        return returnJsonUtil.returnJson(200, resultMap);
    }

    @GetMapping("/sendMsg")
    public String sendMsg() {
        Message message = new Message();
        message.setGroupId("111");
        message.setTaskId(222);
        message.setAction(Action.ON_OFF.getAction());
        message.setDeviceName("test");
        message.setDuration(1);
        message.setCount(3);
        //生产者发送消息，存至消息队列中
        kafkaTemplate.send("device",JSON.toJSONString(message));
        return "发送成功";
    }

    /**
     * 获取最佳助攻列表
     * @param tuples
     * @return
     */
    private List<Assist> getAssistList(Set<ZSetOperations.TypedTuple<String>>tuples){
        //助攻者   ---------》从缓存中获取
        List<Assist> assistList = new ArrayList<>();
        if (tuples != null || tuples.size() != 0){
            for (ZSetOperations.TypedTuple<String> tuple : tuples) {
                String uid = tuple.getValue().trim();
                if (uid != null && uid != ""){
                    String uidNick =   redisTemplate.opsForValue().get(uid + "_nick");
                    String uidAvatar = redisTemplate.opsForValue().get(uid + "_avatar");
                    if (uidNick != null && uidAvatar != null){
                        assistList.add(new Assist(uid,uidNick,uidAvatar));
                    }
                }
                if (assistList.size() >=3){
                    break;
                }
            }
        }
        return assistList;
    }

    @RequestMapping(path = {"/startTest"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String saveEffectEventTest(@RequestBody String data) {
        {
            try {
                String profileId = "50077679";
                String roomId = "520520";
                JSONObject jsonObject = JSONObject.parseObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("challenge");
                List<EffectEvent> effectEventList = new ArrayList<>();
                String groupId = "";
                long time = 0L;

                for (int j = 0; j < jsonArray.size(); j++) {

                    if (j == 0) {
                        time = jsonArray.getJSONObject(j).getLong("token");
                        groupId = profileId + "_" + time;
                    }

                    EffectEvent effectEvent = new EffectEvent();
                    effectEvent.setPrizeId((int) jsonArray.getJSONObject(j).get("gift"));
                    effectEvent.setPrizeNum((int) jsonArray.getJSONObject(j).get("total"));
                    effectEvent.setEffectId((int) jsonArray.getJSONObject(j).get("effect"));
                    effectEvent.setEffectText((String) jsonArray.getJSONObject(j).get("desc"));
                    effectEvent.setUid(profileId);
                    effectEvent.setGroupId(groupId);
                    effectEvent.setStatus(1);
                    effectEvent.setAddTime((long) jsonArray.getJSONObject(j).get("token"));

                    effectEventList.add(effectEvent);

                }

                effectEventService.batchInsertEvent(effectEventList);

                List<EffectEvent> effectEventResult = effectEventService.getEventsByGroupId(groupId);
                giftScheduleManager.createGiftSchedule(effectEventResult, roomId, groupId, time);

                return returnJsonUtil.returnJson(200, "");
            } catch (Exception e) {
                logger.error("保存礼物和挑战数据失败" + e.getMessage());
                return returnJsonUtil.returnJson(500, "参数错误");
            }
        }
    }
}
