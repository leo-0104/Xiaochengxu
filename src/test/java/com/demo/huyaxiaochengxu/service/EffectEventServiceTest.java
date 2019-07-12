package com.demo.huyaxiaochengxu.service;


import com.demo.huyaxiaochengxu.entity.EffectEvent;
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
        effectEvent.setUid(111);
        effectEvent.setPrizeId(1);
        effectEvent.setPrizeNum(10);
        effectEvent.setEffectId(1);
        effectEventList.add(effectEvent);
        EffectEvent effectEvent1 = new EffectEvent();
        effectEvent1.setUid(122);
        effectEvent1.setPrizeId(2);
        effectEvent1.setPrizeNum(100);
        effectEvent1.setEffectId(2);
        effectEventList.add(effectEvent1);
        int num = effectEventService.batchInsertEvent(effectEventList);
        System.out.println(num);
    }
}
