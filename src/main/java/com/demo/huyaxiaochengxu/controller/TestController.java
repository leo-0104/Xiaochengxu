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


//    @RequestMapping(path = {"/startTest"}, method = {RequestMethod.GET, RequestMethod.POST})
//    public String saveEffectEventTest(@RequestBody String data) {
//        {
//            try {
//                String profileId = "50077679";
//                String roomId = "520520";
//                JSONObject jsonObject = JSONObject.parseObject(data);
//                JSONArray jsonArray = jsonObject.getJSONArray("challenge");
//                List<EffectEvent> effectEventList = new ArrayList<>();
//                String groupId = "";
//                long time = 0L;
//
//                for (int j = 0; j < jsonArray.size(); j++) {
//
//                    if (j == 0) {
//                        time = jsonArray.getJSONObject(j).getLong("token");
//                        groupId = profileId + "_" + time;
//                    }
//
//                    EffectEvent effectEvent = new EffectEvent();
//                    effectEvent.setPrizeId((int) jsonArray.getJSONObject(j).get("gift"));
//                    effectEvent.setPrizeNum((int) jsonArray.getJSONObject(j).get("total"));
//                    effectEvent.setEffectId((int) jsonArray.getJSONObject(j).get("effect"));
//                    effectEvent.setEffectText((String) jsonArray.getJSONObject(j).get("desc"));
//                    effectEvent.setUid(profileId);
//                    effectEvent.setGroupId(groupId);
//                    effectEvent.setStatus(1);
//                    effectEvent.setAddTime((long) jsonArray.getJSONObject(j).get("token"));
//
//                    effectEventList.add(effectEvent);
//
//                }
//
//                effectEventService.batchInsertEvent(effectEventList);
//
//                List<EffectEvent> effectEventResult = effectEventService.getEventsByGroupId(groupId);
//                giftScheduleManager.createGiftSchedule(effectEventResult, roomId, groupId, time);
//
//                return returnJsonUtil.returnJson(200, "");
//            } catch (Exception e) {
//                logger.error("保存礼物和挑战数据失败" + e.getMessage());
//                return returnJsonUtil.returnJson(500, "参数错误");
//            }
//        }
//    }
}
