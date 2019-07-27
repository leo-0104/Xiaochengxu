package com.demo.huyaxiaochengxu.crontab;

import com.demo.huyaxiaochengxu.entity.EffectEvent;
import com.demo.huyaxiaochengxu.service.EffectEventService;
import com.demo.huyaxiaochengxu.service.GiftScheduleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Configuration
@EnableScheduling
public class CleanTimeoutTask {

    @Autowired
    GiftScheduleManager giftScheduleManager;

    @Autowired
    EffectEventService effectEventService;

    private static final Logger logger = LoggerFactory.getLogger(CleanTimeoutTask.class);

    @Scheduled(fixedRate=1000 * 3600)
    private void run() {
        long currentTime = new Date().getTime();
        logger.info("CleanTimeoutTask -- start microTime=" + currentTime);
        List<EffectEvent> eventList = effectEventService.getCloseEvents(currentTime);
        List<Integer> taskList = new ArrayList<>();
        for (EffectEvent effectEvent: eventList) {
            String groupId = effectEvent.getGroupId();
            giftScheduleManager.cancelGiftSchedule(groupId);
            logger.info("CleanTimeoutTask -- 关闭ws连接:" + groupId);
            taskList.add(effectEvent.getId());
        }
        if (!eventList.isEmpty()) {
            logger.info("CleanTimeoutTask -- taskIds:" + taskList.toString());
            int result = effectEventService.batchCloseEvent(eventList);
            logger.info("CleanTimeoutTask -- 关闭挑战任务条数:" + result);
        }
        logger.info("CleanTimeoutTask -- end");
    }


}