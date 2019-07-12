package com.demo.huyaxiaochengxu.mapper;

import com.demo.huyaxiaochengxu.entity.EffectEvent;

import java.util.List;

public interface EffectEventMapper {
    public int batchInsertEvent(List<EffectEvent> effectEventList);
}
