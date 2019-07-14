package com.demo.huyaxiaochengxu.service;

import com.demo.huyaxiaochengxu.entity.EffectEvent;
import com.demo.huyaxiaochengxu.service.EffectEventService;
import com.demo.huyaxiaochengxu.util.DateUtil;
import com.demo.huyaxiaochengxu.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EffectEventServiceTest {

    @Autowired
    private EffectEventService effectEventService;
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
    public void getEventsByUid(){
      String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjcmVhdG9yIjoiU1lTIiwicm9sZSI6IlAiLCJwcm9maWxlSWQiOiJ1blZqWnpxUEhENUR6QXJmS1l6Nkg2VUN2MTA4MUhKK3owIiwiYXBwSWQiOiJsNWNjMzZiYTQ4M2MyMDdjIiwiZXh0SWQiOiJ1dTl2M2tuMCIsImV4cCI6MTU2MzExNTQ3NCwidXNlcklkIjoidW5Walp6cVBIRDVEekFyZktZejZINlVDdjEwODFISit6MCIsImlhdCI6MTU2MzEwODI3NCwicm9vbUlkIjoiMTkwOTEyNzcifQ.SmbwXWyByuAvLczgTHqivG7kMPJ4rn6O295qnzg7SHs";
        //token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1NjMxMDkyNzcsImV4cCI6MTU2MzEwOTg3NywiYXBwSWQiOiJnYjgxYTUyMzM2MzBiMmU5In0.PRTre4vE7581AHVyIAc2Xt8xRVUh-Rcj2jtWQ2AeO7E";
        Claims claims = JwtUtil.decryptByToken(token);
        System.out.println(claims);

    }


}
