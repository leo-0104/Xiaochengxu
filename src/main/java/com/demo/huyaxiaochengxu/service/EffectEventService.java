package com.demo.huyaxiaochengxu.service;

import com.demo.huyaxiaochengxu.entity.EffectEvent;

import java.util.List;


public interface EffectEventService {
    public int batchInsertEvent(List<EffectEvent> effectEventList);
    public List<EffectEvent> getEventsByUid(String uid);
}
