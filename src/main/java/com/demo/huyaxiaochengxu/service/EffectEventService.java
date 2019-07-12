package com.demo.huyaxiaochengxu.service;

import com.demo.huyaxiaochengxu.entity.EffectEvent;
import org.springframework.stereotype.Service;

import java.util.List;


public interface EffectEventService {
    public int batchInsertEvent(List<EffectEvent> effectEventList);
}
