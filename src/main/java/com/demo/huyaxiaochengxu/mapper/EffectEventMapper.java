package com.demo.huyaxiaochengxu.mapper;

import com.demo.huyaxiaochengxu.entity.EffectEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
public interface EffectEventMapper {
    public int batchInsertEvent(@Param("effectEventList")List<EffectEvent> effectEventList);
    public List<EffectEvent> getEventsByUid(@Param("uid")String uid);
    public List<EffectEvent> getEventsByGroupId(@Param("groupId")String groupId);
    public int batchUpdateEvent(@Param("uid")String uid);

    public int updateEventById(@Param("id")int id);
    //获取在挑战中的任务
    public List<EffectEvent> getStartEventsByGroupId(@Param("groupId")String groupId);
}
