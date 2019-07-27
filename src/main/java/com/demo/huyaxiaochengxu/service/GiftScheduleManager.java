package com.demo.huyaxiaochengxu.service;



import com.demo.huyaxiaochengxu.entity.EffectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.ScheduledExecutorService;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class GiftScheduleManager {

    @Autowired
    RedisTemplate<String, String> redisTemplate;


    private static final Logger logger = LoggerFactory.getLogger(GiftScheduleManager.class);

    private Map<String, GiftSchedule> giftScheduleMap;
    private ScheduledExecutorService scheduledExecutorService;

    public GiftScheduleManager() {
        super();
        giftScheduleMap = new HashMap<>();
        scheduledExecutorService = new ScheduledThreadPoolExecutor(10);
    }

    public void createGiftSchedule(List<EffectEvent> effectEventResult, String roomId, String groupId, long time) {
        GiftSchedule giftSchedule = new GiftSchedule(effectEventResult, roomId, groupId, redisTemplate);
        scheduledExecutorService.schedule(giftSchedule,3,TimeUnit.SECONDS);
        giftScheduleMap.put(groupId, giftSchedule);
        logger.info("createGiftSchedule----->giftScheduleMap size: " + giftScheduleMap.size());
        logger.info("createGiftSchedule----->giftScheduleMap key: " + giftScheduleMap.keySet().toString());
    }

    public void cancelGiftSchedule(String groupId) {
        logger.info("cancelGiftSchedule before----->giftScheduleMap size: " + giftScheduleMap.size());
        logger.info("cancelGiftSchedule before----->giftScheduleMap key: " + giftScheduleMap.keySet().toString());
        if ( giftScheduleMap.get(groupId) == null){
            logger.info(groupId + " 不存在");
            return;
        }else{
            giftScheduleMap.get(groupId).setExecuteState(0);
            giftScheduleMap.remove(groupId);
        }
        logger.info("cancelGiftSchedule after----->giftScheduleMap size: " + giftScheduleMap.size());
        logger.info("cancelGiftSchedule after----->giftScheduleMap key: " +  giftScheduleMap.keySet().toString());
    }

    public Map<String, GiftSchedule> getGiftScheduleMap() {
        return giftScheduleMap;
    }

    public void setGiftScheduleMap(Map<String, GiftSchedule> giftScheduleMap) {
        this.giftScheduleMap = giftScheduleMap;
    }
}
