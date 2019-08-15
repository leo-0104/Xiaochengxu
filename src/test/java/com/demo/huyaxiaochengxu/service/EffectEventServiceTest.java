package com.demo.huyaxiaochengxu.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.huyaxiaochengxu.entity.Assist;
import com.demo.huyaxiaochengxu.entity.EffectEvent;
import com.demo.huyaxiaochengxu.service.EffectEventService;
import com.demo.huyaxiaochengxu.util.DateUtil;
import com.demo.huyaxiaochengxu.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.kafka.common.protocol.types.Field;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EffectEventServiceTest {

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;
    @Autowired
    private EffectEventService effectEventService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Test
    public void batchInsertEventTest(){
        List<EffectEvent> effectEventList = new ArrayList<>();
        EffectEvent effectEvent = new EffectEvent();
        effectEvent.setUid("111");
        effectEvent.setPrizeId(1);
        effectEvent.setPrizeNum(10);
        effectEvent.setEffectId(1);
        effectEventList.add(effectEvent);
        EffectEvent effectEvent1 = new EffectEvent();
        effectEvent1.setUid("122");
        effectEvent1.setPrizeId(2);
        effectEvent1.setPrizeNum(100);
        effectEvent1.setEffectId(2);
        effectEventList.add(effectEvent1);
        int num = effectEventService.batchInsertEvent(effectEventList);
        System.out.println(num);
    }

    @Test
    public void batchupdateEventTest(){
        String profileUid = "50077679";
        int result = effectEventService.batchUpdateEvent(profileUid);
        System.out.println(result);
    }

    @Test
    public void getEventsByUid(){
        List<EffectEvent> effectEvents = effectEventService.getStartEvents();
        Map<String,List<EffectEvent>> resultMap = new HashMap<>();
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
        if (resultMap != null && resultMap.size() > 0 ){
            //将对应主播的特效事件写到缓存中
            for (Map.Entry<String, List<EffectEvent>> entry:resultMap.entrySet()){
                redisTemplate.opsForValue().set(entry.getKey() + "_effectList", JSONArray.toJSONString(entry.getValue()),300, TimeUnit.SECONDS);
            }
        }
    }

    @Test
    public void test(){
        List<EffectEvent> list = new ArrayList<>();
        EffectEvent effectEvent = new EffectEvent();
        effectEvent.setEffectId(1);
        list.add(effectEvent);
        redisTemplate.opsForValue().set("name",JSONArray.toJSONString(list),300,TimeUnit.SECONDS);
        List<EffectEvent> name = JSONArray.parseArray(redisTemplate.opsForValue().get("name"),EffectEvent.class);
        System.out.println(name == null);
        redisTemplate.delete("name");

    }





}
