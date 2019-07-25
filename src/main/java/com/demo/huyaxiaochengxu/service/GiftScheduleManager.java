package com.demo.huyaxiaochengxu.service;

import com.demo.huyaxiaochengxu.entity.EffectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GiftScheduleManager {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(GiftScheduleManager.class);

    private Map<String, GiftSchedule> giftScheduleMap;
    private Timer giftTimer;

    public GiftScheduleManager() {
        super();
        giftScheduleMap = new HashMap<>();
        giftTimer = new Timer();
    }

    public void createGiftSchedule(List<EffectEvent> effectEventResult, String roomId, String groupId, long time) {
        GiftSchedule giftSchedule = new GiftSchedule(effectEventResult, roomId, groupId, redisTemplate);
        giftTimer.schedule(giftSchedule,new Date(time));
        giftScheduleMap.put(groupId, giftSchedule);
        logger.info("createGiftSchedule----->giftScheduleMap size: " + giftScheduleMap.size());
        logger.info("createGiftSchedule----->giftScheduleMap key: " + giftScheduleMap.keySet().toString());
    }

    public boolean cancelGiftSchedule(String groupId) {
        boolean flag =  giftScheduleMap.get(groupId).cancel();
        logger.info("cancelGiftSchedule----->giftScheduleMap size: " + giftScheduleMap.size());
        logger.info("cancelGiftSchedule----->giftScheduleMap key: " +  giftScheduleMap.keySet().toString());
        return flag;
    }
}
