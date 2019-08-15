package com.demo.huyaxiaochengxu.crontab;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.huyaxiaochengxu.entity.EffectEvent;
import com.demo.huyaxiaochengxu.entity.Event;
import com.demo.huyaxiaochengxu.service.EffectEventService;
import com.demo.huyaxiaochengxu.service.GiftScheduleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 获取 处于挑战中的 特效事件
 */
@Component
@Configuration
@EnableScheduling
public class GetOnlineTask {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    EffectEventService effectEventService;

    private static final Logger logger = LoggerFactory.getLogger(GetOnlineTask.class);

    @Scheduled(fixedRate=1000)
    private void run() {
       try {
//           logger.info("GetOnlineTask -- start microTime=" + new Date().getTime());
           //查询所有 处于挑战中的特效事件
           List<EffectEvent> effectEvents = effectEventService.getStartEvents();
//           logger.info("GetOnlineTask -- startTask num =" + effectEvents.size());
           Map<String,List<EffectEvent>> resultMap = new HashMap<>();
           if (effectEvents != null || effectEvents.size() > 0){
               for(EffectEvent effectEvent:effectEvents){
                   String uid = effectEvent.getUid();
                   if (resultMap.size() > 0 && resultMap.containsKey(uid)){
                       List<EffectEvent> list = new ArrayList<>(resultMap.get(uid));
                       list.add(effectEvent);
                       resultMap.put(uid,list);
                   }else{
                       resultMap.put(uid,Arrays.asList(effectEvent));
                   }
               }
           }
//           logger.info("GetOnlineTask -- uidSize = " + resultMap.size());
           if (resultMap != null && resultMap.size() > 0 ){
               //将对应主播的特效事件写到缓存中
               for (Map.Entry<String, List<EffectEvent>> entry:resultMap.entrySet()){
                   redisTemplate.opsForValue().set((entry.getKey() + "_effectList").trim(), JSONArray.toJSONString(entry.getValue()),5, TimeUnit.SECONDS);
               }
           }
//           logger.info("GetOnlineTask -- end microTime=" + new Date().getTime());
       }catch (Exception e){
           logger.error("GetOnlineTask error e=" + e.getMessage());
       }
    }

}
