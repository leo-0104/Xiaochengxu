package com.demo.huyaxiaochengxu.service;

import com.demo.huyaxiaochengxu.entity.EffectEvent;
import com.demo.huyaxiaochengxu.util.JwtUtil;
import com.demo.huyaxiaochengxu.util.ParamsUtil;
import com.demo.huyaxiaochengxu.util.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.net.URI;
import java.util.*;
import java.util.TimerTask;

public class GiftSchedule implements Runnable {

    private String roomId;
    private String groupId;
    private RedisTemplate redisTemplate;
    private WebSocketClient myClient;
    private volatile int ExecuteState = 1;
    private final static Logger log = LoggerFactory.getLogger(GiftSchedule.class);

    private Map<Integer, List<Integer>> taskInfoMap = new HashMap<>();

    public GiftSchedule(List<EffectEvent> effectEventsList, String roomId, String groupId, RedisTemplate redisTemplate) {
        super();
        this.roomId = roomId;
        this.groupId = groupId;
        for (EffectEvent effectEvent : effectEventsList) {
            List<Integer> effectList = new ArrayList<>();
            if (taskInfoMap.get(effectEvent.getPrizeId()) != null){
                effectList = taskInfoMap.get(effectEvent.getPrizeId());
            }
            effectList.add(effectEvent.getId());
            taskInfoMap.put(effectEvent.getPrizeId(),effectList);
        }
        this.redisTemplate = redisTemplate;
    }


    @Override
    public void run() {
        try {
            String url = "ws://ws-apiext.huya.com/index.html";
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("do", "comm");
            map.put("roomId", this.roomId);
            map = JwtUtil.getJwtParamsMap(map);
            url = url + ParamsUtil.MapToUrlString(map);
            myClient = new WebSocketClient(URI.create(url), groupId, taskInfoMap,redisTemplate);
            myClient.connect();
            while (!myClient.getReadyState().equals(ReadyState.OPEN)) {
            }
            Long reqId = new Date().getTime();
            String sendMsg = "{\"command\":\"subscribeNotice\",\"data\":[\"getSendItemNotice\"],\"reqId\":\"" + reqId + "\"}";
            myClient.send(sendMsg);
            log.info(sendMsg + "  " + new Date());
            while (ExecuteState == 1) {
                Thread.sleep(15000);
                log.info("ping");
                myClient.send("ping");
            }
            myClient.closeConnection(0,"bye");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getExecuteState(){
        return this.ExecuteState;
    }

    public void setExecuteState(int state){
        this.ExecuteState = state;
    }

    public String getGroupId(){
        return this.groupId;
    }

}
