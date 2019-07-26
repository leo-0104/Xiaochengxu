package com.demo.huyaxiaochengxu.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.demo.huyaxiaochengxu.common.Action;
import com.demo.huyaxiaochengxu.entity.AppInfo;
import com.demo.huyaxiaochengxu.entity.Gift;
import com.demo.huyaxiaochengxu.util.*;
import com.sun.corba.se.impl.oa.toa.TOA;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.java_websocket.enums.ReadyState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.awt.*;
import java.net.URI;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HuyaxiaochengxuApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;
    //根据房间号获取主播签约公会信息
    @Test
    public void contextLoads() {
        String url = "https://open-apiext.huya.com/channel/index";

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("do", "getChannelInfoByRoom");
        paramsMap.put("roomId", 852674);
        Map<String, Object> authMap = JwtUtil.getJwtParamsMap(paramsMap);
        url = url + ParamsUtil.MapToUrlString(authMap);
        String result = HttpUtil.doGet(url);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.getInteger("code") == 200) {
            System.out.println(jsonObject.getString("message"));
            System.out.println(jsonObject.getString("data"));
        }

    }

    //开放API 获取礼物素材列表
    @Test
    public void getLiveGiftInfoListTest() {
        String url = "https://open-apiext.huya.com/proxy/index";

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("do", "getLiveGiftInfoList");
        Map<String, Object> authMap = JwtUtil.getJwtParamsMap(paramsMap);
        url = url + ParamsUtil.MapToUrlString(authMap);
        String result = HttpUtil.doGet(url);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.getInteger("code") == 200) {
            System.out.println(jsonObject.getString("message"));
            System.out.println(jsonObject.getString("data"));
        }
    }

    //	订阅消息/取消订阅消息(websocket协议)
//    @Test
//    public void subscribeTest() {
//        try {
//            String url = "ws://ws-apiext.huya.com/index.html";
//            Map<String, Object> map = new HashMap<String, Object>();
//            map.put("do", "comm");
//            map.put("roomId", 520520);
//            map = JwtUtil.getJwtParamsMap(map);
//            System.out.println(map);
//            url = url + ParamsUtil.MapToUrlString(map);
//            WebSocketClient myClient = new WebSocketClient(URI.create(url));
//            myClient.connect();
//
//            while (!myClient.getReadyState().equals(ReadyState.OPEN)) {
//            }
//            while (true) {
//                myClient.send("{\"command\":\"subscribeNotice\",\"data\":[\"getSendItemNotice\"],\"reqId\":\"123456789\"}");
//                Thread.sleep(100000);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Test
    public void test() throws InterruptedException {
        String url = "ws://ws-apiext.huya.com/index.html";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("do", "comm");
        map.put("roomId", 5205201);
        map = JwtUtil.getJwtParamsMap(map);
        url = url + ParamsUtil.MapToUrlString(map);
        Map<Integer, List<Integer>> taskInfoMap = new HashMap<>();
        WebSocketClient myClient = new WebSocketClient(URI.create(url), "111", taskInfoMap,redisTemplate);
        myClient.setConnectionLostTimeout(3600);
        myClient.connect();
        while (!myClient.getReadyState().equals(ReadyState.OPEN)) {
        }
        Long reqId = new Date().getTime();
        String sendMsg = "{\"command\":\"subscribeNotice\",\"data\":[\"getSendItemNotice\"],\"reqId\":\"" + reqId + "\"}";
        myClient.send(sendMsg);
        int ExecuteState = 1;
        while (ExecuteState == 1) {
            Thread.sleep(15000);
            System.out.println("ping---");
            myClient.send("ping");
        }
        myClient.closeConnection(0,"bye");
    }

    @Test
    public void test11(){
       List<Integer> list = null;
       for (int i = 0;i < 10;i++){
           list.add(i);
       }
        System.out.println(list);
    }
}
