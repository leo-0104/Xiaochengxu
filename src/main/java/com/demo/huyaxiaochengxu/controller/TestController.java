package com.demo.huyaxiaochengxu.controller;


import com.demo.huyaxiaochengxu.service.EffectEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class TestController {

    @Autowired
    private EffectEventService effectEventService;

    @RequestMapping("/test")
    public String test(){
        return "测试 测试 spring boot";
    }

//    @RequestMapping("/addEffectEvent")
//    public int batchInsertEvent(@RequestParam(value = "eventInfos")String eventList){
//        JSONArray objar = new JSONArray(eventList);
//        List<Object> list = objar.toList();
//    }
}
