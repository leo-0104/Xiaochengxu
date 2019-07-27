package com.demo.huyaxiaochengxu.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.demo.huyaxiaochengxu.common.Action;
import com.demo.huyaxiaochengxu.entity.EffectEvent;
import com.demo.huyaxiaochengxu.entity.Message;
import com.demo.huyaxiaochengxu.service.EffectEventService;
import com.demo.huyaxiaochengxu.service.GiftScheduleManager;
import com.demo.huyaxiaochengxu.util.HttpUtil;
import com.demo.huyaxiaochengxu.util.ParamsUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.*;

//kafka消费者监听器
@Component
public class MsgConsumer {

    @Autowired
    private EffectEventService effectEventService;
    @Autowired
    private GiftScheduleManager giftScheduleManager;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static String URL = "http://starrydistance.xyz/";

    @KafkaListener(topics = {"device"})
    public void listen(ConsumerRecord<?, ?> record) {
        logger.info("kafka的value: " + record.value().toString());
        //开启异步延迟任务，通知设备监听
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                JSONObject jsonObject = JSON.parseObject(record.value().toString());
                String groupId = jsonObject.getString("groupId");
                int taskId = jsonObject.getInteger("taskId");
                Map<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("device", jsonObject.getString("deviceName"));
                paramsMap.put("action", jsonObject.getString("action"));
                paramsMap.put("duration", jsonObject.getIntValue("duration"));
                paramsMap.put("count", jsonObject.getIntValue("count"));

                //请求触发特效
                String result = HttpUtil.doGet(URL + ParamsUtil.MapToUrlString(paramsMap));
                JSONObject resultObject = JSON.parseObject(result);
                logger.info("设备请求结果: result: " + resultObject.toJSONString());
                //判断执行是否成功
                if (!resultObject.getBoolean("success")) {
                    logger.error("请求设备失败 ：" + resultObject.toJSONString());
                    return;
                }
                //触发特效请求
                if (jsonObject.getString("action").trim().equals(Action.ON_OFF.getAction()) && jsonObject.getBoolean("change")) {
                    //更新挑战状态
                    int num = effectEventService.updateEventById(taskId);
                    if (num <= 0) {
                        logger.error("更新挑战状态失败 id：" + taskId);
                        return;
                    }
                    //  判断所有挑战是否完成，完成则结束礼物监听
                    List<EffectEvent> effectEvents = effectEventService.getStartEventsByGroupId(groupId);
                    //所有挑战完成，结束礼物监听
                    if (effectEvents == null || effectEvents.size() == 0) {
                        if (giftScheduleManager != null && giftScheduleManager.getGiftScheduleMap().containsKey(groupId)) {
                            logger.info("结束礼物监听" + groupId);
                            //结束礼物监听事件
                            giftScheduleManager.cancelGiftSchedule(groupId);
                        }
                    }
                }
            }
        }, 0);


    }


}
