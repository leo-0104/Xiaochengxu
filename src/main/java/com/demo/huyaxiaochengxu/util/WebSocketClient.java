package com.demo.huyaxiaochengxu.util;
import java.net.URI;

import com.alibaba.fastjson.JSONObject;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class WebSocketClient extends org.java_websocket.client.WebSocketClient {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketClient.class);

    private String groupId;
    private Map<Integer,List<Integer>> taskInfoMap; //key礼物id ,val任务id
    private RedisTemplate redisTemplate;

    public WebSocketClient(URI serverUri,String groupId,Map<Integer,List<Integer>> taskInfoMap, RedisTemplate redisTemplate) {
        super(serverUri);
        this.groupId = groupId;
        this.taskInfoMap = taskInfoMap;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onOpen(ServerHandshake arg0) {
        logger.info("------ MyWebSocket onOpen ------");
        logger.info("groupId:"+groupId+",taskIds"+taskInfoMap.entrySet().toString());
    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
        logger.info("------ MyWebSocket onClose ------");
    }

    @Override
    public void onError(Exception arg0) {
        logger.error("------ MyWebSocket onError ------");
    }

    @Override
    public void onMessage(String arg0) {
        logger.info("-------- 接收到服务端数据： " + arg0 + "--------");
        JSONObject data = JSONObject.parseObject(arg0).getJSONObject("data");
        Integer giftId = data.getInteger("itemId");
         if (giftId == null){
             return;
         }
        String[] showGiftIds = {"4", "20114", "20277", "20273", "20271", "20267"};
        boolean isContains = Arrays.asList(showGiftIds).contains(giftId.toString());

        if (isContains) {

            List<Integer> taskList = taskInfoMap.get(giftId);
            Double giftCount = data.getDouble("sendItemCount");
            String senderUid = data.getString("unionId");
            String senderNick = data.getString("sendNick");
            String senderAvatar = data.getString("senderAvatarurl");
            if (taskList!= null && taskList.size() > 0){
                for(Integer taskId:taskList){
                    String keyName =  taskId + "_";
                    String totalName = keyName + "total";
                    String formerCount =   redisTemplate.opsForValue().get(totalName) + "";
                    Integer newCount = 0;
                    if (!formerCount.equals("null")) {
                        newCount = Integer.valueOf(formerCount) + giftCount.intValue();
                    }
                    redisTemplate.opsForZSet().incrementScore(keyName, senderUid, giftCount);
                    redisTemplate.opsForValue().set(totalName, String.valueOf(newCount), 3600, TimeUnit.SECONDS);
                    redisTemplate.opsForValue().set(senderUid + "_nick", senderNick, 3600, TimeUnit.SECONDS);
                    redisTemplate.opsForValue().set(senderUid + "_avatar", senderAvatar, 3600, TimeUnit.SECONDS);

                    logger.info("-------- 统计数据成功： " + keyName + "--------");
                }
            }

        }
    }

}

