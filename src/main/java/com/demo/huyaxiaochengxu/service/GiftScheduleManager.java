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
    }

    public boolean cancelGiftSchedule(String groupId) {
        return giftScheduleMap.get(groupId).cancel();
    }
}
