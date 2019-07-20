package com.demo.huyaxiaochengxu.service;

import com.demo.huyaxiaochengxu.entity.EffectEvent;
import com.demo.huyaxiaochengxu.util.JwtUtil;
import com.demo.huyaxiaochengxu.util.ParamsUtil;
import com.demo.huyaxiaochengxu.util.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.springframework.data.redis.core.RedisTemplate;

import java.net.URI;
import java.util.*;
import java.util.TimerTask;

public class GiftSchedule extends TimerTask {

    private String roomId;
    private String groupId;
    private RedisTemplate redisTemplate;
    private WebSocketClient myClient;
    private int ExecuteState = 1;


    private Map<Integer, Integer> taskInfoMap = new HashMap<>();

    public GiftSchedule(List<EffectEvent> effectEventsList, String roomId, String groupId, RedisTemplate redisTemplate) {
        super();
        this.roomId = roomId;
        this.groupId = groupId;
        for (EffectEvent effectEvent : effectEventsList) {
            taskInfoMap.put(effectEvent.getPrizeId(), effectEvent.getId());
        }
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean cancel() {
        ExecuteState = 0;
        return super.cancel();
    }

    @Override
    public long scheduledExecutionTime() {
        return super.scheduledExecutionTime();
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
            myClient.send("{\"command\":\"subscribeNotice\",\"data\":[\"getSendItemNotice\"],\"reqId\":\"" + new Date().getTime() + "\"}");
            while (ExecuteState == 1) {
                Thread.sleep(3000);
                myClient.send("ping");
            }
            myClient.closeConnection(0,"bye");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getGroupId(){
        return this.groupId;
    }

}
