package com.demo.huyaxiaochengxu.mapper;

import com.demo.huyaxiaochengxu.entity.EffectEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
public interface EffectEventMapper {
    public int batchInsertEvent(@Param("effectEventList")List<EffectEvent> effectEventList);
}
